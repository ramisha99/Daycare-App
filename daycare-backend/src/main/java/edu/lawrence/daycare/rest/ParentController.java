/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.lawrence.daycare.rest;

import edu.lawrence.daycare.data.*;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Ramisha Mahiyat
 */
@RestController
@RequestMapping("/parent")
@CrossOrigin(origins="*")
public class ParentController {
    private ParentDAO parentDAO;

    public ParentController(ParentDAO dao) {
        this.parentDAO = dao;
    }

    @GetMapping
    public Parent get(@RequestParam(value="user") int id) {
        return parentDAO.findByUserId(id);
    }
    
    @PostMapping
    public int post(@RequestBody Parent parent) {
        return parentDAO.add(parent);
    }
    
    @PutMapping
    public void put(@RequestBody Parent parent) {
        parentDAO.update(parent);
        
    }
}
