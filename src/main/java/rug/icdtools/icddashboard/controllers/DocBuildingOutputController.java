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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import rug.icdtools.icddashboard.models.PipelineOutputDescription;

/**
 *
 * @author hcadavid
 */
@RestController
public class DocBuildingOutputController {

    //                     pipelineid     docname,output
    private static final HashMap<String, HashMap<String, PipelineOutputDescription>> db = new HashMap<>();

    private RedisTemplate<String, PipelineOutputDescription> template;

    @GetMapping("/test")
    String test() {
        return "Working";
    }

    @CrossOrigin
    @PostMapping("/pipelines/{pipelineid}/{docname}")
    PipelineOutputDescription addOutput(@RequestBody PipelineOutputDescription desc, @PathVariable String pipelineid, @PathVariable String docname) {
        if (db.get(pipelineid) != null) {
            db.get(pipelineid).put(docname, desc);
        } else {
            HashMap<String, PipelineOutputDescription> docBuildOutputEntries = new HashMap<>();
            docBuildOutputEntries.put(docname, desc);
            db.put(pipelineid, docBuildOutputEntries);
        }

        System.out.println(">>>>done");

        return desc;
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


    @CrossOrigin
    @GetMapping("/pipelines/{pipelineid}/{docname}")
    HATEOASDocWrapper getDocProcessingOutput(@PathVariable String pipelineid,@PathVariable String docname ) {
        if (db.get(pipelineid) == null || db.get(pipelineid).get(docname) == null) {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("doc %s information for pipeline %s: not found", docname, pipelineid));
        } else {
            return new HATEOASDocWrapper(db.get(pipelineid).get(docname).getErrors());
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

        Collection<PipelineOutputDescription> results;

        int count;

        String next;

        String previous;

        public HATEOASPipelinesWrapper(Collection<PipelineOutputDescription> results) {
            this.results = results;
            count = results.size();
            next = null;
            previous = null;
        }

        public Collection<PipelineOutputDescription> getResults() {
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
