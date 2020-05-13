package edu.lawrence.daycare.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ParentRowMapper implements RowMapper<Parent> {
    @Override
    public Parent mapRow(ResultSet row, int rowNum) throws SQLException {
        Parent p = new Parent();
        p.setId(row.getInt("idparent"));
        p.setName(row.getString("name"));
        p.setPhone(row.getString("phone"));
        p.setAddress(row.getString("address"));
        p.setCity(row.getString("city"));
        p.setEmail(row.getString("email"));
        p.setUser(row.getInt("iduser"));
        return p;
    }
}