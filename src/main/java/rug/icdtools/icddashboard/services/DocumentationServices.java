/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.services;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import rug.icdtools.core.models.PublishedICDMetadata;
import rug.icdtools.core.models.VersionedDocument;
import rug.icdtools.icddashboard.models.ICDStatusDescription;
import rug.icdtools.icddashboard.models.ICDStatusType;
import rug.icdtools.icddashboard.models.PipelineFailure;
import rug.icdtools.icddashboard.models.PipelineFailureDetails;

/**
 *
 * @author hcadavid
 */
@Service
public class DocumentationServices {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisTemplate<String, PipelineFailureDetails> failureDetailsRedisTemplate;

    @Autowired
    private RedisTemplate<String, PipelineFailure> failuresRedisTemplate;
    
    @Autowired
    private RedisTemplate<String, PublishedICDMetadata> publishedICDsTeamplate;
    
    @Autowired
    private RedisTemplate<String, VersionedDocument> versionedDocumentTemplate; 
    
    @Autowired
    private RedisTemplate<String, ICDStatusDescription> icdStatusDescriptionTemplate;
    


    
    /**
     * icds:{icdname}:status    (ICD status)
     * icds:{icdname}:current_version     (published ICD metadata)
     * icds:{icdname}:failed_builds_list   (LIST of failed pipelines - ordered, with general info)
     * icds:{icdname}:failed_builds_set   (SET of failed pipelines ids - for quick validation)
     * icds:{icdname}:failed_builds:{pipelines}  (SET of errors)
     * icdstatuses   redis hash name where the (unique) current statuses of the ICDs are stored
     */    
    //icds:{docname}:{version} 
    private static final String ICD_STATUS = "icds:%s:status";
    private static final String ICD_CURRENT_VERSION_METADATA = "icds:%s:current_version_metadata";
    private static final String ICD_CURRENT_VERSION = "icds:%s:current_version";    
    private static final String ICD_VERSIONS = "icds:%s:version_numbers";    
    private static final String ICD_OTHER_VERSIONS_METADATA = "icds:%s:%s";
    private static final String ICD_FAILED_BUILDS_LIST = " icds:%s:failed_builds_list";
    private static final String ICD_FAILED_BUILDS_SET = " icds:%s:failed_builds_set";
    private static final String ICD_FAILED_BUILD_ERRORS = " icds:%s:failed_builds:%s";
    private static final String ICD_REFERENCED_BY = "icds:%s:referenced_by";
    private static final String ICD_STATUSES_HASH_KEY = "icdstatuses";

    /**
     * 
     * @param icdid
     * @param metadata
     * @return
     * @throws DocumentationServicesException 
     */
    public PublishedICDMetadata updateCurrentlyPublishedICD(String icdid, PublishedICDMetadata metadata) throws DocumentationServicesException {        
        checkMetadataForCompleteness(metadata);

        String creationTimeStamp=metadata.getMetadata().get("CREATION_DATE");
        String docVersion = metadata.getMetadata().get("COMMIT_TAG");
        
        Set<VersionedDocument> dependentDocuments = versionedDocumentTemplate.opsForSet().members(String.format(ICD_REFERENCED_BY, icdid));
        Set<ICDStatusDescription> currentDependendDocumentStatuses = new LinkedHashSet<>();
        
        HashOperations<String,String,ICDStatusDescription> hashOps= icdStatusDescriptionTemplate.opsForHash();
        
        for (VersionedDocument dependentDoc : dependentDocuments) {
            currentDependendDocumentStatuses.add(hashOps.get(ICD_STATUSES_HASH_KEY, String.format(ICD_STATUS, dependentDoc.getDocName())));
        }

        List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();

                //create/update document's 'current' version metadata
                operations.opsForValue().set(String.format(ICD_CURRENT_VERSION_METADATA, icdid), metadata);
                operations.opsForValue().set(String.format(ICD_CURRENT_VERSION, icdid), docVersion);

                //update document status
                ICDStatusDescription docLastVersionStatus = new ICDStatusDescription();
                docLastVersionStatus.setICDname(icdid);
                docLastVersionStatus.setLastPublishedVersion(docVersion);
                docLastVersionStatus.setStatus(ICDStatusType.PUBLISHED);
                docLastVersionStatus.setPublishedDocDetails(metadata);

                operations.opsForHash().put(ICD_STATUSES_HASH_KEY, String.format(ICD_STATUS, icdid), docLastVersionStatus);

                //update status of documents that depend on this one
                for (ICDStatusDescription dependentDocCurrentStatusDesc : currentDependendDocumentStatuses) {
                    //Potential race condition: the dependent document status could get updated right after a document it depends on fires its update.                    
                    //The 'current_version' key of the dependent documents whose status will be updated is watched to make the transaction conditional
                    //on this value not changig during the transaction.
                    operations.watch(String.format(ICD_CURRENT_VERSION, dependentDocCurrentStatusDesc.getICDname()));

                    //get the metadata and update it with the new status
                    dependentDocCurrentStatusDesc.setStatus(ICDStatusType.DEPENDENCY_UPDATED);
                    operations.opsForHash().put(ICD_STATUSES_HASH_KEY, String.format(ICD_STATUS, dependentDocCurrentStatusDesc.getICDname()), dependentDocCurrentStatusDesc);
                }

                //Update document dependencies' indexes
                Set<VersionedDocument> referencedDocs = metadata.getReferencedDocs();
                for (VersionedDocument referencedDoc : referencedDocs) {
                    operations.opsForSet().add(String.format(ICD_REFERENCED_BY, referencedDoc.getDocName()), new VersionedDocument(icdid, docVersion));
                }

                //store metadata of this particular version (so a document version history can be traced)
                operations.opsForValue().set(String.format(ICD_OTHER_VERSIONS_METADATA, icdid, docVersion), metadata);
                operations.opsForSet().add(String.format(ICD_VERSIONS,icdid), docVersion);
                return operations.exec();
            }
        });

        return metadata;
    }

    /**
     * 
     * @param metadata
     * @throws DocumentationServicesException when one of the predefined properties is missing in metadata
     */
    private void checkMetadataForCompleteness(PublishedICDMetadata metadata) throws DocumentationServicesException{
        
        String[] metadataProps = new String[]{
            "PIPELINE_ID",
            "PROJECT_NAME",
            "PIPELINE_ID",
            "DEPLOYMENT_URL",
            "SOURCE_URL",
            "COMMIT_AUTHOR",
            "CREATION_DATE",
            "COMMIT_TAG"            
        };
        for (String property:metadataProps){
            if (!metadata.getMetadata().containsKey(property)){
                throw new DocumentationServicesException("Missing property on metadata:"+property);
            }
        }
        
    }
    
    /**
     * 
     * @param icdid
     * @param version
     * @param failureDesc
     * @param pipelineid
     * @return 
     */
    public PipelineFailureDetails registerFailedPipeline(String icdid, String version,PipelineFailureDetails failureDesc, String pipelineid) {

        boolean firstPipelineFailure = !redisTemplate.opsForSet().isMember(String.format(ICD_FAILED_BUILDS_SET, icdid), pipelineid);
        
        HashOperations<String,String,ICDStatusDescription> hashOps= icdStatusDescriptionTemplate.opsForHash();        
        ICDStatusDescription previousStatus = hashOps.get(ICD_STATUSES_HASH_KEY,String.format(ICD_STATUS,icdid));
                                        
        List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                
                //This is included for an scenario where the errors of multiple documents within the same pipeline are reported:
                //register the pipelines date/id in a list, to keep their order, and in a set for 'isMember' evaluation in O(1))
                //As multiple failures can be reported for the same pipeline, this operation is performed only once.               
                if (firstPipelineFailure) {                    
                    operations.opsForList().leftPush(String.format(ICD_FAILED_BUILDS_LIST, icdid), new PipelineFailure(icdid,version,pipelineid,failureDesc.getDate()));
                    operations.opsForSet().add(String.format(ICD_FAILED_BUILDS_SET, icdid), pipelineid);                    
                }
                
                //Update the status of the document.
                ICDStatusDescription updatedStatus = null;
                
                if (previousStatus!=null){
                    updatedStatus = new ICDStatusDescription();
                    updatedStatus.setICDname(icdid);
                    updatedStatus.setLastFailedToPublishVersion(version);                    
                    updatedStatus.setStatus(ICDStatusType.UNPUBLISHED);
                }                
                else{
                    updatedStatus = previousStatus;
                    updatedStatus.setLastFailedToPublishVersion(version);
                    updatedStatus.setStatus(ICDStatusType.PUBLISHED_FAILED_UPDATE);
                }
                operations.opsForHash().put(ICD_STATUSES_HASH_KEY,String.format(ICD_STATUS,icdid), updatedStatus); 
                
                //add the details of the failure
                operations.opsForList().leftPush(String.format(ICD_FAILED_BUILD_ERRORS,icdid,pipelineid), failureDesc);
                
                return operations.exec();
            }
        });

        return failureDesc;

    }

    public List<PipelineFailureDetails> getFailedPipelineDetails(String icdid, String pipelineid) throws DocumentationServicesException, NonExistingResourceException {
        List<PipelineFailureDetails> docerrors = failureDetailsRedisTemplate.opsForList().range(String.format(ICD_FAILED_BUILD_ERRORS, icdid,pipelineid), 0, -1);
        if (docerrors == null) {
            throw new DocumentationServicesException(String.format("error while accessing pipeline %s resource.", pipelineid));
        } else if (docerrors.isEmpty()) {
            throw new NonExistingResourceException(String.format("pipeline %s: not found", pipelineid));
        } else {
            return docerrors;
        }
    }

    public Collection<ICDStatusDescription> getRegisteredICDs() {
        HashOperations<String,String,ICDStatusDescription> hashops = redisTemplate.opsForHash();
        Map<String,ICDStatusDescription> entries = hashops.entries(ICD_STATUSES_HASH_KEY);
        return entries.values();        
    }

    public List<PipelineFailure> getFailedPipelines(String icdid) {
        List<PipelineFailure> pipelineIds = failuresRedisTemplate.opsForList().range(String.format(ICD_FAILED_BUILDS_LIST, icdid), 0, -1);
        return pipelineIds;
    }

    /**
     * 
     * @param icdid
     * @return  
     */
    public PublishedICDMetadata getPublishedDocumentMetadata(String icdid) throws NonExistingResourceException{
        PublishedICDMetadata metadata = publishedICDsTeamplate.opsForValue().get(String.format(ICD_CURRENT_VERSION_METADATA, icdid));
        if (metadata==null){
            throw new NonExistingResourceException("No available metadata for the last version of document "+icdid);
        }
        else{
            return metadata;
        }
    }
    

    public PublishedICDMetadata getPublishedDocumentMetadata(String icdid, String versionTag) throws NonExistingResourceException{
        PublishedICDMetadata metadata = publishedICDsTeamplate.opsForValue().get(String.format(ICD_OTHER_VERSIONS_METADATA, icdid, versionTag));
        if (metadata==null){
            throw new NonExistingResourceException(String.format("No available metadata for version %s of document %s",versionTag,icdid));
        }
        else{
            return metadata;
        }
    }    



}
