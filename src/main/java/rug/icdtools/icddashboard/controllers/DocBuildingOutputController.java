/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.controllers;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
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
import rug.icdtools.icddashboard.services.DocumentationServices;
import rug.icdtools.icddashboard.services.DocumentationServicesException;
import rug.icdtools.icddashboard.services.NonExistingResourceException;

/**
 * TODO: add transactions: https://github.com/spring-projects/spring-data-redis/blob/main/src/main/asciidoc/reference/redis-transactions.adoc
 * @author hcadavid
 */
@RestController
public class DocBuildingOutputController {

    @Autowired
    private DocumentationServices docServices;
    
    @GetMapping("/test")
    String test() {
        return "Working";
    }

    
    @CrossOrigin
    @PutMapping("/v1/icds/{icdid}/current")
    public PublishedICDMetadata addSuccesfulBuildMetadata(@PathVariable String icdid, @RequestBody PublishedICDMetadata metadata) {
        
        docServices.updateCurrentlyPublishedICD(icdid, metadata);

        return metadata;
    }    
    
    @CrossOrigin
    @PostMapping("/v1/icds/{icdid}/{pipelineid}/errors")
    public PipelineFailureDetails addOutput(@PathVariable String icdid, @RequestBody PipelineFailureDetails desc, @PathVariable String pipelineid) {

        docServices.registerFailedPipeline(icdid, desc, pipelineid);
        return desc;

    }

    @CrossOrigin
    @GetMapping("/v1/icds/{icdid}/{pipelineid}/errors")
    List<PipelineFailureDetails> getDocFailureOutput(@PathVariable String icdid, @PathVariable String pipelineid) {
        try {
            return docServices.getFailedPipelineDetails(icdid, pipelineid);            
        } catch (DocumentationServicesException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "internal server error:"+ex.getLocalizedMessage(),ex);            
        } catch (NonExistingResourceException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found:"+ex.getLocalizedMessage(),ex);
        }
        
    }

    @CrossOrigin
    @GetMapping("/v1/icds")
    Set<ICDDescription> getICDs() {
        return docServices.getRegisteredICDs();
    }

    
    @CrossOrigin
    @GetMapping("/v1/icds/{icdid}/failedpipelines")
    List<PipelineFailure> getICDFailedPipelines(@PathVariable String icdid) {
        return docServices.getFailedPipelines(icdid);
    }
    
 

}
