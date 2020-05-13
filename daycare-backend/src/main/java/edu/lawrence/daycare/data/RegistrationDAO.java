/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.lawrence.daycare.data;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Ramisha Mahiyat
 */
@Repository
public class RegistrationDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int createRegistration(Registration r) {
        String insertSQL = "INSERT INTO registration values (NULL, ?, ?, ?, ?, 1)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, r.childId);
                    ps.setInt(2, r.providerId);
                    ps.setDate(3, r.start);
                    ps.setDate(4, r.end);
                    return ps;
                }, keyHolder);

        return keyHolder.getKey().intValue();
    }
}
