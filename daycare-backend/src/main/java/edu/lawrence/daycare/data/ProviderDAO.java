package edu.lawrence.daycare.data;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ProviderDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Provider> findAll() {
        String sql = "SELECT * FROM providers";
        RowMapper<Provider> rowMapper = new ProviderRowMapper();
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<Provider> findByLocation(double lat, double lgt) {
        String sql = "SELECT id, name, address, city, capacity, minAge, maxAge, gc_dist(?,?,lat,lgt) AS dist FROM providers ORDER BY dist";
        RowMapper<Provider> rowMapper = new ProviderRowMapper();
        return jdbcTemplate.query(sql, rowMapper, lat, lgt);
    }

    public Provider findById(int id) {
        String sql = "SELECT id, name, address, city, capacity, minAge, maxAge FROM providers WHERE id=?";
        RowMapper<Provider> rowMapper = new ProviderRowMapper();
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    public void save(Provider p) {
        String sql = "INSERT INTO providers (name, address, city, capacity, minAge, maxAge) values (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, p.getName(), p.getAddress(), p.getCity(), p.getCapacity(), p.getMinAge(), p.getMaxAge());
    }

    public void remove(int id) {
        String sql = "DELETE FROM providers WHERE id=?";
        jdbcTemplate.update(sql, id);
    }
    
    public List<Provider> getProvidersForAgeAndStartDate(int age, Date startDate, Date endDate) {
        String sql = "select id, name, address, city, capacity, minAge, maxAge from "
                + "providers left join (select provider, count(provider) c from registration where start <= ? and end >= ? group by provider)tmp on providers.id = tmp.provider "
                + "where ifnull(tmp.c,0) < capacity and minAge < ? and maxAge > ?";
        RowMapper<Provider> rowMapper = new ProviderRowMapper();
        
        int endAge = (int)(((endDate.getTime() - new java.util.Date().getTime()) / (24d * 30 * 60 * 60 * 1000))
                + age);
        
        // note from Kate:
        //
        // I think the SQL here is flawed, but I'm copying it off the example.
        // to my intuition we should be testing for any kind of intersection 
        // between the lines (startDate, endDate) and (registration.start, registration.end)
        // if either of registration.start or registration.end fall within the interval
        // (startDate, endDate), the registration is in conflict with the one to be created
        // 
        // however, I'll use the SQL as-is, with the "where start <= @start and end >= @start"
        // clause
        
        return jdbcTemplate.query(sql, rowMapper, startDate, startDate, age, endAge);
    }
}
