/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.services;

/**
 *
 * @author hcadavid
 */
public class NonExistingResourceException extends Exception {

    public NonExistingResourceException(String message) {
        super(message);
    }

    public NonExistingResourceException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
