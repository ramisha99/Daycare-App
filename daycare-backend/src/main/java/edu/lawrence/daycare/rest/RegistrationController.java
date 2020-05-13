/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.lawrence.daycare.rest;

import edu.lawrence.daycare.data.*;
import java.sql.Date;
import java.util.List;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Kate
 */
@RestController
@RequestMapping("/registration")
@CrossOrigin(origins="*")
public class RegistrationController {
    private RegistrationDAO registrationDAO;
    
    public RegistrationController(RegistrationDAO dao) {
        this.registrationDAO = dao;
    }

    @PostMapping
    public int create(@RequestBody Registration r) {
        return registrationDAO.createRegistration(r);
    }
    
}
