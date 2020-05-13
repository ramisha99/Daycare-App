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
public class RegistrationRowMapper implements RowMapper<Registration> {
    @Override
    public Registration mapRow(ResultSet row, int rowNum) throws SQLException {
        Registration r = new Registration();
        r.id = row.getInt("id");
        r.childId = row.getInt("child");
        r.providerId = row.getInt("provider");
        r.start = row.getDate("start");
        r.end = row.getDate("end");
        r.status = row.getInt("status");
        
        return r;
    }
}
