/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.controllers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import rug.icdtools.icddashboard.models.BuildProcessOutputDescription;

/**
 *
 * @author hcadavid
 */
@RestController
public class DocBuildingOutputController {
  
  //                     pipelineid     docname,output
  private static final HashMap<String,HashMap<String,BuildProcessOutputDescription>> db=new HashMap<>();

  @GetMapping("/test")
  String test() {
    return "Working";
  }   
  
    @CrossOrigin
    @PostMapping("/outputs/{pipelineid}/{docname}")
    BuildProcessOutputDescription addOutput(@RequestBody BuildProcessOutputDescription desc, @PathVariable String pipelineid, @PathVariable String docname) {
        if (db.get(pipelineid) != null) {
            db.get(pipelineid).put(docname, desc);
        } else {
            HashMap<String, BuildProcessOutputDescription> docBuildOutputEntries = new HashMap<>();
            docBuildOutputEntries.put(docname, desc);
            db.put(pipelineid, docBuildOutputEntries);
        }

        System.out.println(">>>>done");

        return desc;
    }

  @CrossOrigin  
  @GetMapping("/outputs/{pipelineid}/{docname}")
    BuildProcessOutputDescription getOutput(@PathVariable String pipelineid, @PathVariable String docname){
      if (db.get(pipelineid)==null){          
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("pipeline %s not found",pipelineid));
      }
      else{
          BuildProcessOutputDescription desc = db.get(pipelineid).get(docname);
          if (desc == null){
              throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("doc %s information for pipeline %s: not found",docname,pipelineid));
          }
          else{
              return desc;
          }           
      }          
  }
  
  
    @CrossOrigin
    @GetMapping("/outputs/{pipelineid}")
    Collection<BuildProcessOutputDescription> getPipelineOutput(@PathVariable String pipelineid) {
        if (db.get(pipelineid) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("pipeline %s not found", pipelineid));
        } else {
            return db.get(pipelineid).values();
        }
    }


  
}
