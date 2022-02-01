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

    public PipelineFailure() {
    }

    public PipelineFailure(String buildDate, String pipelineid) {
        this.buildDate = buildDate;
        this.pipelineid = pipelineid;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public String getPipelineid() {
        return pipelineid;
    }

}
