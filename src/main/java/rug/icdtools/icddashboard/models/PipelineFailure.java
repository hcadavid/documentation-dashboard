/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.models;

import java.io.Serializable;

/**
 *
 * @author hcadavid
 */
public class PipelineFailure implements Serializable {
    
    private String buildDate;

    private String pipelineid;
    
    private String icdid;
    
    private String versionTag;

    public PipelineFailure() {
    }

    public PipelineFailure(String icdid, String versionTag, String pipelineid, String buildDate) {
        this.buildDate = buildDate;
        this.pipelineid = pipelineid;
        this.icdid = icdid;
        this.versionTag = versionTag;
    }


    public String getIcdid() {
        return icdid;
    }
    public void setIcdid(String icdid) {
        this.icdid = icdid;
    }
    public String getVersionTag() {
        return versionTag;
    }
    public void setVersionTag(String versionTag) {
        this.versionTag = versionTag;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public String getPipelineid() {
        return pipelineid;
    }

}
