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
public class ICDDescription implements Serializable {

    public ICDDescription(String ICDname, String status) {
        this.ICDname = ICDname;
        this.status = status;
    }

    public ICDDescription() {
    }

    
    
    public String getICDname() {
        return ICDname;
    }

    public void setICDname(String ICDname) {
        this.ICDname = ICDname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    String ICDname;
    
    String status;
    
}
