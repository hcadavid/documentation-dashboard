/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author hcadavid
 */
public class PublishedICDMetadata implements Serializable {

    private Map<String,String> metadata;
    
    private List<String> referencedDocs;
    
    private List<String> warnings;   

    public List<String> getReferencedDocs() {
        return referencedDocs;
    }

    public void setReferencedDocs(List<String> referencedDocs) {
        this.referencedDocs = referencedDocs;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "PublishedICDMetadata{" + "metadata=" + metadata + ", referencedDocs=" + referencedDocs + ", warnings=" + warnings + '}';
    }

 


}
