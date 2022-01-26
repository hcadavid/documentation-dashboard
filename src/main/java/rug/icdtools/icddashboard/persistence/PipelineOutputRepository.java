/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.persistence;

import org.springframework.data.repository.CrudRepository;
import rug.icdtools.icddashboard.models.PipelineOutputDescription;

/**
 *
 * @author hcadavid
 */
public interface PipelineOutputRepository extends CrudRepository<PipelineOutputDescription, Object>{
    
}
