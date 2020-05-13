package edu.lawrence.daycare.data;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;
   
    public User login(String userName,String password) {
        String sql = "SELECT iduser, name, password FROM users WHERE name=? and password=?";
        RowMapper<User> rowMapper = new UserRowMapper();
        User user = null;
        try {
            user = jdbcTemplate.queryForObject(sql, rowMapper, userName, password);
        } catch (EmptyResultDataAccessException ex) {

        }
        return user;
    }

    public User findByName(String name) {
        String sql = "SELECT iduser, name, password FROM users WHERE name=?";
        RowMapper<User> rowMapper = new UserRowMapper();
        User user = null;
        try {
            user = jdbcTemplate.queryForObject(sql, rowMapper, name);
        } catch (EmptyResultDataAccessException ex) {

        }
        return user;
    }
    
    public int save(User user) {
        String insertSQL = "INSERT INTO users (name, password) values (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, user.getName());
                    ps.setString(2, user.getPassword());
                    return ps;
                }, keyHolder);

        return keyHolder.getKey().intValue();
    }
}
