package edu.lawrence.daycare.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ProviderRowMapper implements RowMapper<Provider> {
    @Override
    public Provider mapRow(ResultSet row, int rowNum) throws SQLException {
        Provider p = new Provider();
        p.setId(row.getInt("id"));
        p.setName(row.getString("name"));
        p.setAddress(row.getString("address"));
        p.setCity(row.getString("city"));
        p.setCapacity(row.getInt("capacity"));
        p.setMinAge(row.getString("minAge"));
        p.setMaxAge(row.getString("maxAge"));
        return p;
    }
}
