/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.models;

import java.io.Serializable;
import rug.icdtools.core.models.PublishedICDMetadata;

/**
 *
 * @author hcadavid
 */
public class ICDStatusDescription implements Serializable {
    String ICDname;
    String version;
    ICDStatusType status;
    PublishedICDMetadata publishedDocDetails;

    public ICDStatusDescription(String ICDname, String version, ICDStatusType status) {
        this.ICDname = ICDname;
        this.version = version;
        this.status = status;
    }

    public ICDStatusDescription() {
    }

    
    
    public String getICDname() {
        return ICDname;
    }

    public void setICDname(String ICDname) {
        this.ICDname = ICDname;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ICDStatusType getStatus() {
        return status;
    }

    public void setStatus(ICDStatusType status) {
        this.status = status;
    }

    public PublishedICDMetadata getPublishedDocDetails() {
        return publishedDocDetails;
    }

    public void setPublishedDocDetails(PublishedICDMetadata publishedDocDetails) {
        this.publishedDocDetails = publishedDocDetails;
    }


    
    
}
