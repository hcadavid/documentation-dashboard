/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.models;

import java.util.List;

/**
 *
 * @author hcadavid
 */
public class PipelineOutputDescription {

        private String date;

        private String adocName;

        private List<String> errors;

        private List<String> fatalErrors;

        
        
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