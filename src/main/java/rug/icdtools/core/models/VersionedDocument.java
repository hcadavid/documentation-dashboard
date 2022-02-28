/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.core.models;

import java.util.Objects;

public class VersionedDocument{
    
    private final String docName;
    
    private final String versionTag;
    
    public VersionedDocument(String docName, String versionTag) {
        this.docName = docName;
        this.versionTag = versionTag;
    }

    public String getDocName() {
        return docName;
    }


    public String getVersionTag() {
        return versionTag;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VersionedDocument other = (VersionedDocument) obj;
        if (!Objects.equals(this.docName, other.docName)) {
            return false;
        }
        return Objects.equals(this.versionTag, other.versionTag);
    }   

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.docName);
        hash = 79 * hash + Objects.hashCode(this.versionTag);
        return hash;
    }

    @Override
    public String toString() {
        return docName+":"+versionTag;
    }
    
    
}