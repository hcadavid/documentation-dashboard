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
public class ICDStatus implements Serializable {
    String ICDname;
    String version;
    String status;


    public ICDStatus(String ICDname, String version, String status) {
        this.ICDname = ICDname;
        this.version = version;
        this.status = status;
    }



    public ICDStatus() {
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
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
    
    
}
