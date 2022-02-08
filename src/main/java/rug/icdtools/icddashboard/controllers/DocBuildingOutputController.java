/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.controllers;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import rug.icdtools.icddashboard.models.ICDDescription;
import rug.icdtools.icddashboard.models.PipelineFailure;
import rug.icdtools.icddashboard.models.PipelineFailureDetails;
import rug.icdtools.icddashboard.models.PublishedICDMetadata;

/**
 * TODO: add transactions: https://github.com/spring-projects/spring-data-redis/blob/main/src/main/asciidoc/reference/redis-transactions.adoc
 * @author hcadavid
 */
@RestController
public class DocBuildingOutputController {

    @Autowired
    private RedisTemplate<String, PipelineFailureDetails> failureDetailsRedisTemplate;

    @Autowired
    private RedisTemplate<String,String> stringKeyValueRedisTeamplate;
    
    @Autowired
    private RedisTemplate<String,PipelineFailure> buildFailuresRedisTeamplate;
        
    @Autowired
    private RedisTemplate<String,PublishedICDMetadata> publishedICDMetadataTemplate;
    
    @Autowired
    private RedisTemplate<String,ICDDescription> icdDescriptionTemplate;
    
    @GetMapping("/test")
    String test() {
        return "Working";
    }

    
    @CrossOrigin
    @PutMapping("/v1/icds/{icdid}/current")
    @PreAuthorize("hasRole('CI')")
    public PublishedICDMetadata addSuccesfulBuildMetadata(@PathVariable String icdid, @RequestBody PublishedICDMetadata metadata) {
        
        stringKeyValueRedisTeamplate.opsForSet().add("icdids", icdid);
        publishedICDMetadataTemplate.opsForValue().set("published:"+icdid, metadata);        

        return metadata;
    }    
    
    @CrossOrigin
    @PostMapping("/v1/icds/{icdid}/{pipelineid}/errors")
    @PreAuthorize("hasRole('CI')")
    public PipelineFailureDetails addOutput(@PathVariable String icdid, @RequestBody PipelineFailureDetails desc, @PathVariable String pipelineid) {

        //add to the list of ICD pipelines only once (a list is used to keep the pipelines order)
        if (!stringKeyValueRedisTeamplate.opsForSet().isMember("failures:" + icdid + ":pipelines", pipelineid)) {
            buildFailuresRedisTeamplate.opsForList().leftPush("failures:" + icdid, new PipelineFailure(desc.getDate(), pipelineid));
        }

        stringKeyValueRedisTeamplate.opsForSet().add("failures:" + icdid + ":pipelines", pipelineid);
        failureDetailsRedisTemplate.opsForList().leftPush("failures:" + icdid + ":" + pipelineid, desc);
        icdDescriptionTemplate.opsForSet().add("icdids", new ICDDescription(icdid, "Undefined status"));

        return desc;

    }

    @CrossOrigin
    @GetMapping("/v1/icds/{icdid}/{pipelineid}/errors")
    List<PipelineFailureDetails> getDocFailureOutput(@PathVariable String icdid, @PathVariable String pipelineid) {

        //PipelineFailureDescription fdesc = 
        List<PipelineFailureDetails> docerrors = failureDetailsRedisTemplate.opsForList().range("failures:" + icdid + ":" + pipelineid, 0, -1);

        if (docerrors == null){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("internal server error while accessing pipeline %s resource.", pipelineid));
        }
        else if (docerrors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("error information for pipeline %s: not found", pipelineid));
        } else {
            return docerrors;
        }
    }

    @CrossOrigin
    @GetMapping("/v1/icds")
    @PreAuthorize("hasRole('CI')")
    Set<ICDDescription> getICDs() {
        Set<ICDDescription> members = icdDescriptionTemplate.opsForSet().members("icdids");
        return members;
    }

    
    @CrossOrigin
    @GetMapping("/v1/icds/{icdid}/failedpipelines")
    List<PipelineFailure> getICDFailedPipelines(@PathVariable String icdid) {
        List<PipelineFailure> pipelineIds = buildFailuresRedisTeamplate.opsForList().range("failures:"+icdid,0,-1);
        return pipelineIds;
    }
    
 
    private class HATEOASDocWrapper {

        int count;

        String next;

        String previous;

        List<String> errors;

        public HATEOASDocWrapper(List<String> errors) {
            this.errors = errors;
            count = errors.size();
            next = null;
            previous = null;
        }

        public int getCount() {
            return count;
        }

        public String getNext() {
            return next;
        }

        public String getPrevious() {
            return previous;
        }

        public Collection<String> getErrors() {
            return errors;
        }

    }

    private class HATEOASPipelinesWrapper {

        Collection<PipelineFailureDetails> results;

        int count;

        String next;

        String previous;

        public HATEOASPipelinesWrapper(Collection<PipelineFailureDetails> results) {
            this.results = results;
            count = results.size();
            next = null;
            previous = null;
        }

        public Collection<PipelineFailureDetails> getResults() {
            return results;
        }

        public int getCount() {
            return count;
        }

        public String getNext() {
            return next;
        }

        public String getPrevious() {
            return previous;
        }

    }

}
