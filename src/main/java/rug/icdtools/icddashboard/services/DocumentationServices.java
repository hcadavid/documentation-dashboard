/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import rug.icdtools.icddashboard.models.ICDDescription;
import rug.icdtools.icddashboard.models.PipelineFailure;
import rug.icdtools.icddashboard.models.PipelineFailureDetails;
import rug.icdtools.icddashboard.models.PublishedICDMetadata;

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
    private RedisTemplate<String, ICDDescription> icdDescriptionRedisTemplate;

    
    /**
     * icds:{icdname}:status    (ICD status)
     * icds:{icdname}:current_version     (published ICD metadata)
     * icds:{icdname}:failed_builds_list   (LIST of failed pipelines - ordered, with general info)
     * icds:{icdname}:failed_builds_set   (SET of failed pipelines ids - for quick validation)
     * icds:{icdname}:failed_builds:{pipelines}  (SET of errors)
     * icdstatuses   redis hash name where the (unique) current statuses of the ICDs are stored
     */    
    private static final String ICD_STATUS = "icds:%s:status";
    private static final String ICD_CURRENT_VERSION = "icds:%s:current_version";
    private static final String ICD_FAILED_BUILDS_LIST = " icds:%s:failed_builds_list";
    private static final String ICD_FAILED_BUILDS_SET = " icds:%s:failed_builds_set";
    private static final String ICD_FAILED_BUILD_ERRORS = " icds:%s:failed_builds:%s";
    private static final String ICD_STATUSES_HASH_KEY = "icdstatuses";
    

    /**
     *
     * @param icdid
     * @param metadata
     * @return
     */
    public PublishedICDMetadata updateCurrentlyPublishedICD(String icdid, PublishedICDMetadata metadata) {

        List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForValue().set(String.format(ICD_CURRENT_VERSION, icdid), metadata);
                //operations.opsForValue().set(String.format(ICD_STATUS,icdid), new ICDDescription(icdid,"Published on "+metadata.getLastUpdate()));
                operations.opsForHash().put(ICD_STATUSES_HASH_KEY,String.format(ICD_STATUS,icdid), new ICDDescription(icdid,"Published on "+metadata.getLastUpdate()));
                return operations.exec();
            }
        });

        return metadata;
    }

    /**
     *
     * @param icdid
     * @param desc
     * @param pipelineid
     * @return
     */
    public PipelineFailureDetails registerFailedPipeline(String icdid, PipelineFailureDetails desc, String pipelineid) {

        boolean firstPipelineFailure = !redisTemplate.opsForSet().isMember(String.format(ICD_FAILED_BUILDS_SET, icdid), pipelineid);
        
        List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                
                //register a pipeline date/id in a list (to keep its order) and a set (for isMember evaluation in O(1))
                //when the first error from such pipeline is reported                
                if (firstPipelineFailure) {
                    operations.opsForList().leftPush(String.format(ICD_FAILED_BUILDS_LIST, icdid), new PipelineFailure(desc.getDate(), pipelineid));
                    operations.opsForSet().add(String.format(ICD_FAILED_BUILDS_SET, icdid), pipelineid);                    
                }
                
                //operations.opsForValue().set(String.format(ICD_STATUS, icdid),new ICDDescription(icdid, "Document building failed."));
                operations.opsForHash().put(ICD_STATUSES_HASH_KEY,String.format(ICD_STATUS,icdid), new ICDDescription(icdid,"Document building failed."));                                      
                //add the details of the failure
                operations.opsForList().leftPush(String.format(ICD_FAILED_BUILD_ERRORS,icdid,pipelineid), desc);
                
                return operations.exec();
            }
        });

        return desc;

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

    public Collection<ICDDescription> getRegisteredICDs() {
        HashOperations<String,String,ICDDescription> hashops = redisTemplate.opsForHash();
        Map<String,ICDDescription> entries = hashops.entries(ICD_STATUSES_HASH_KEY);
        return entries.values();        
    }

    public List<PipelineFailure> getFailedPipelines(String icdid) {
        List<PipelineFailure> pipelineIds = failuresRedisTemplate.opsForList().range(String.format(ICD_FAILED_BUILDS_LIST, icdid), 0, -1);
        return pipelineIds;
    }

}
