/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.models;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author hcadavid
 */
public class PublishedICDMetadata implements Serializable {

    private List<String> otherICDRefs;
    
    private String url;
    
    private String sourceURL;

    private String lastUpdate;

    private List<String> warnings;
    
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getSourceURL() {
        return sourceURL;
    }
    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public List<String> getOtherICDRefs() {
        return otherICDRefs;
    }

    public void setOtherICDRefs(List<String> otherICDRefs) {
        this.otherICDRefs = otherICDRefs;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}
