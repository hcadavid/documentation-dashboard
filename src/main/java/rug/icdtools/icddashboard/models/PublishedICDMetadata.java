/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author hcadavid
 */
public class PublishedICDMetadata implements Serializable {
    
    private List<String> otherICDRefs;
        
    private String lastUpdate;

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
