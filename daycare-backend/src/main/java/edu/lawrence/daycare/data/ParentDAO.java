/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.lawrence.daycare.data;

import java.sql.PreparedStatement;
import java.sql.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kate
 */
@Repository
public class ParentDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Parent findByUserId(int user_id) {
        String sql = "SELECT * FROM parents WHERE iduser = ?";
        RowMapper<Parent> rowMapper = new ParentRowMapper();
        return jdbcTemplate.query(sql, rowMapper, user_id).get(0);
    }
    
    public int add(Parent parent) {
        String sql = "INSERT INTO parents values (NULL, ?, ?, ?, ?, ?, ?)";        
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, parent.getName());
                    ps.setString(2, parent.getCity());
                    ps.setString(3, parent.getAddress());
                    ps.setString(4, parent.getPhone());
                    ps.setString(5, parent.getEmail());
                    ps.setInt(6, parent.getUser());
                    return ps;
                }, keyHolder);
        
        return keyHolder.getKey().intValue();
    }
    
    public void update(Parent parent) {
        String sql = "UPDATE parents SET name=?, phone=?, address=?, city=?, email=? where idparent=?";
        jdbcTemplate.update(sql, parent.getName(), parent.getPhone(), parent.getAddress(), parent.getCity(), parent.getEmail(), parent.getId());
    }
}
