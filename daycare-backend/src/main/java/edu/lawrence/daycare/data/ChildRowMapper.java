/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.lawrence.daycare.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author Ramisha Mahiyat
 */
public class ChildRowMapper implements RowMapper<Child> {
    @Override
    public Child mapRow(ResultSet row, int rowNum) throws SQLException {
        Child child = new Child();
        child.childId = row.getInt("id");
        child.parentId = row.getInt("parent");
        child.name = row.getString("name");
        child.birthDate = row.getDate("birthday");
        return child;
    }
}