/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rug.icdtools.icddashboard.models.PipelineFailureDetails;

/**
 *
 * @author hcadavid
 */
@Repository
public interface FailedPipelinesRepository extends CrudRepository<PipelineFailureDetails, String>{
    
}
