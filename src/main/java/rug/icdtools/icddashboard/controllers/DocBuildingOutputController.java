/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import rug.icdtools.icddashboard.models.PipelineFailureDescription;

/**
 *
 * @author hcadavid
 */
@RestController
public class DocBuildingOutputController {

    //                     pipelineid     docname,output
    private static final HashMap<String, HashMap<String, PipelineFailureDescription>> db = new HashMap<>();

    
    @Autowired
    private RedisTemplate<String, PipelineFailureDescription> failuresRedisTemplate;

    @GetMapping("/test")
    String test() {
        return "Working";
    }

    @CrossOrigin
    @PostMapping("/icds/{icdid}/{pipelineid}/docerrors")
    PipelineFailureDescription addOutput(@PathVariable String icdid,@RequestBody PipelineFailureDescription desc, @PathVariable String pipelineid) {


        ListOperations<String,PipelineFailureDescription> listOp = failuresRedisTemplate.opsForList();
        
        listOp.rightPush("failures:"+icdid+":"+pipelineid,desc);
        
                //String.format("failures:%s:%s",icdid,pipelineid), desc);
        
        System.out.println(">>>>done:"+String.format("failures:%s:%s",icdid,pipelineid));

        return desc;
    }


    @CrossOrigin
    @GetMapping("/icds/{icdid}/{pipelineid}/docerrors")
    List<PipelineFailureDescription> getDocFailureOutput(@PathVariable String icdid, @PathVariable String pipelineid) {
        
        //PipelineFailureDescription fdesc = 
        List<PipelineFailureDescription> docerrors= failuresRedisTemplate.opsForList().range("failures:"+icdid+":"+pipelineid, 0, -1);
                              
        if (docerrors.isEmpty()) {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("error information for pipeline %s: not found",  pipelineid));
        } else {
            System.out.println(docerrors);
            return docerrors;
        }
    } 
    
    
    @CrossOrigin
    @GetMapping("/pipelines")
    List<String> getPipelines() {
        Set<String> pipelines = db.keySet();
        List<String> sortedPipelines = new ArrayList<>(pipelines);
        Collections.sort(sortedPipelines,Collections.reverseOrder());
        return sortedPipelines;        
    }

    @CrossOrigin
    @GetMapping("/pipelines/{pipelineid}")
    HATEOASPipelinesWrapper getPipelineOutput(@PathVariable String pipelineid) {
        if (db.get(pipelineid) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("pipeline %s not found", pipelineid));
        } else {
            return new HATEOASPipelinesWrapper(db.get(pipelineid).values());
        }
    }


   
    
    private class HATEOASDocWrapper{


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

        Collection<PipelineFailureDescription> results;

        int count;

        String next;

        String previous;

        public HATEOASPipelinesWrapper(Collection<PipelineFailureDescription> results) {
            this.results = results;
            count = results.size();
            next = null;
            previous = null;
        }

        public Collection<PipelineFailureDescription> getResults() {
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
