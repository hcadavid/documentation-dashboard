/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.controllers;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import rug.icdtools.core.models.PublishedICDMetadata;
import rug.icdtools.icddashboard.models.ICDStatusDescription;
import rug.icdtools.icddashboard.models.PipelineFailure;
import rug.icdtools.icddashboard.models.PipelineFailureDetails;
import rug.icdtools.icddashboard.services.DocumentationServices;
import rug.icdtools.icddashboard.services.DocumentationServicesException;
import rug.icdtools.icddashboard.services.NonExistingResourceException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
/**
 * 
 * @author hcadavid
 */
@RestController
public class DocBuildingOutputController {

    @Autowired
    private DocumentationServices docServices;
    
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @GetMapping("/test")
    String test() {        
        return "Working";
    }

    private void notifyWebClients(){
        simpMessagingTemplate.convertAndSend("/ws/topic/update", new Random(System.currentTimeMillis()).nextLong());
    }
    
    @CrossOrigin
    @PutMapping("/v1/icds/{icdid}/current")
    public PublishedICDMetadata addSuccesfulBuildMetadata(@PathVariable String icdid, @RequestBody PublishedICDMetadata metadata) {
        
        try {
            docServices.updateCurrentlyPublishedICD(icdid, metadata);
            notifyWebClients();
        } catch (DocumentationServicesException ex) {
            Logger.getLogger(DocBuildingOutputController.class.getName()).log(Level.SEVERE, "Invalid request:"+ex.getLocalizedMessage(), ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request:"+ex.getLocalizedMessage(),ex);
        }

        return metadata;
    }    
    
 
    @CrossOrigin
    @GetMapping("/v1/icds/{icdid}/current")
    public PublishedICDMetadata getPublishedDocumentMetadata(@PathVariable String icdid) {
        try {
            PublishedICDMetadata metadata = docServices.getPublishedDocumentMetadata(icdid);
            return metadata;
        } catch (NonExistingResourceException ex) {
            Logger.getLogger(DocBuildingOutputController.class.getName()).log(Level.INFO, "Resource not found:"+ex.getLocalizedMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found:"+ex.getLocalizedMessage());
        }
        
    }    
    
    
    @CrossOrigin
    @GetMapping("/v1/icds/{icdid}/{versionTag}")
    public PublishedICDMetadata addSuccesfulBuildMetadata(@PathVariable String icdid, @PathVariable String versionTag) {
        
        try {
            PublishedICDMetadata metadata = docServices.getPublishedDocumentMetadata(icdid,versionTag);
            return metadata;
        } catch (NonExistingResourceException ex) {
            Logger.getLogger(DocBuildingOutputController.class.getName()).log(Level.INFO, "Resource not found:"+ex.getLocalizedMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found:"+ex.getLocalizedMessage(),ex);
        }
    }  

    
    @CrossOrigin
    @PostMapping("/v1/icds/{icdid}/{version}/{pipelineid}/errors")
    public PipelineFailureDetails addFailedPipelineOutput(@PathVariable String icdid,@PathVariable String version, @RequestBody PipelineFailureDetails desc, @PathVariable String pipelineid) {

        docServices.registerFailedPipeline(icdid, version, desc, pipelineid);
        notifyWebClients();
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
    Collection<ICDStatusDescription> getICDs() {
        return docServices.getRegisteredICDs();
    }

    
    @CrossOrigin
    @GetMapping("/v1/icds/{icdid}/failedpipelines")
    List<PipelineFailure> getICDFailedPipelines(@PathVariable String icdid) {
        return docServices.getFailedPipelines(icdid);
    }
    
 

}
