/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.lawrence.daycare.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Date;

/**
 *
 * @author Ramisha Mahiyat
 */
public class Child {
    public int childId;
    public int parentId;
    public String name;
    
    public Date birthDate;
}
