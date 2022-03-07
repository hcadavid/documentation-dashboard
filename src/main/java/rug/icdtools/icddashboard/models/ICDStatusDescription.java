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
    String lastPublishedVersion;
    String lastFailedToPublishVersion;
    ICDStatusType status;
    PublishedICDMetadata publishedDocDetails;


    public ICDStatusDescription() {
    }

    public String getICDname() {
        return ICDname;
    }

    public void setICDname(String ICDname) {
        this.ICDname = ICDname;
    }

    public String getLastPublishedVersion() {
        return lastPublishedVersion;
    }

    public void setLastPublishedVersion(String lastPublishedVersion) {
        this.lastPublishedVersion = lastPublishedVersion;
    }

    public String getLastFailedToPublishVersion() {
        return lastFailedToPublishVersion;
    }

    public void setLastFailedToPublishVersion(String lastFailedToPublishVersion) {
        this.lastFailedToPublishVersion = lastFailedToPublishVersion;
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
