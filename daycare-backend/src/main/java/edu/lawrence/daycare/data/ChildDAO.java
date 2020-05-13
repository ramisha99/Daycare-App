/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.lawrence.daycare.data;

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
public class ChildDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public List<Child> getChildrenByParent(int id) {
        String sql = "SELECT * FROM children WHERE parent = ?";
        RowMapper<Child> rowMapper = new ChildRowMapper();
        return jdbcTemplate.query(sql, rowMapper, id);
    }
    
    public Child getChildById(int id) {
        String sql = "SELECT * FROM children WHERE id = ?";
        RowMapper<Child> rowMapper = new ChildRowMapper();
        return jdbcTemplate.query(sql, rowMapper, id).get(0);
    }
    
    public int insertChild(Child child) {
        String insertSQL = "INSERT INTO children values (NULL, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, child.parentId);
                    ps.setString(2, child.name);
                    ps.setDate(3, child.birthDate);
                    return ps;
                }, keyHolder);

        return keyHolder.getKey().intValue();
    }
    
    public void updateChild(Child child) {
        String updateSQL = "UPDATE children SET name = ?, birthday = ? where id = ?";
        jdbcTemplate.update(updateSQL, child.name, child.birthDate, child.childId);
    }
}
