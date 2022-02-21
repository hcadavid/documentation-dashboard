/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.services;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
                operations.opsForSet().add("icdids", icdid);
                operations.opsForValue().set("published:" + icdid, metadata);
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

        boolean firstPipelineFailure = !redisTemplate.opsForSet().isMember("failed_pipelines:" + icdid, pipelineid);
        

        List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                
                //register a pipeline date/id in a list (to keep its order) and a set (for isMember evaluation in O(1))
                //when the first error from such pipeline is reported                
                if (firstPipelineFailure) {
                    operations.opsForList().leftPush("failed_pipelines:" + icdid, new PipelineFailure(desc.getDate(), pipelineid));
                    operations.opsForSet().add("failed_pipelines:" + icdid, pipelineid);
                }
                
                operations.opsForSet().add("icdstatus:"+icdid,new ICDDescription(icdid, ""));
                
                      
                //add the details of the failure
                operations.opsForList().leftPush("failures:" + icdid + ":" + pipelineid, desc);
                

                return operations.exec();
            }
        });

        return desc;

    }

    public List<PipelineFailureDetails> getFailedPipelineDetails(String icdid, String pipelineid) throws DocumentationServicesException, NonExistingResourceException {
        List<PipelineFailureDetails> docerrors = failureDetailsRedisTemplate.opsForList().range("failures:" + icdid + ":" + pipelineid, 0, -1);

        if (docerrors == null) {
            throw new DocumentationServicesException(String.format("error while accessing pipeline %s resource.", pipelineid));
        } else if (docerrors.isEmpty()) {
            throw new NonExistingResourceException(String.format("pipeline %s: not found", pipelineid));
        } else {
            return docerrors;
        }
    }

    public Set<ICDDescription> getRegisteredICDs() {
        Set<ICDDescription> members = icdDescriptionRedisTemplate.opsForSet().members("icdids");
        return members;
    }

    public List<PipelineFailure> getFailedPipelines(String icdid) {
        List<PipelineFailure> pipelineIds = failuresRedisTemplate.opsForList().range("failed_pipelines:" + icdid, 0, -1);
        return pipelineIds;
    }

}
