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
public class PipelineFailureDetails implements Serializable {

    String date;

    String adocName;

    List<String> errors;

    List<String> fatalErrors;

    List<String> failedQualityGates;

    public List<String> getFailedQualityGates() {
        return failedQualityGates;
    }

    public void setFailedQualityGates(List<String> failedQualityGates) {
        this.failedQualityGates = failedQualityGates;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getFatalErrors() {
        return fatalErrors;
    }

    public void setFatalErrors(List<String> fatalErrors) {
        this.fatalErrors = fatalErrors;
    }

    public String getdocName() {
        return adocName;
    }

    public void setdocName(String adocName) {
        this.adocName = adocName;
    }

 }