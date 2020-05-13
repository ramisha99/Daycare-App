/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.lawrence.daycare.rest;

import edu.lawrence.daycare.data.*;
import java.util.List;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Ramisha Mahiyat
 */
@RestController
@CrossOrigin(origins="*")
public class ChildController { 
    private ChildDAO childDAO;

    public ChildController(ChildDAO dao) {
        this.childDAO = dao;
    }

    @GetMapping("/children")
    public List<Child> getChildren(@RequestParam(value="parent") int id) {
        return childDAO.getChildrenByParent(id);
    }
    @GetMapping("/child")
    public Child getChild(@RequestParam(value="id") int id) {
        return childDAO.getChildById(id);
    }
    
    @PostMapping("/child")
    public int create(@RequestBody Child child) {
        return childDAO.insertChild(child);
    }
    
    @PutMapping("/child")
    public void update(@RequestBody Child child) {
        childDAO.updateChild(child);
    }
}
