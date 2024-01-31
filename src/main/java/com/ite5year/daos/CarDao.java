package com.ite5year.daos;

import com.ite5year.models.Car;
import com.ite5year.optimisticlock.OptimisticallyLocked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@Repository
public class CarDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Car findOneUsingJDBC(Long id) {
        return this.jdbcTemplate.queryForObject("select * from car where id = ?", new Object[]{id},
                new RowMapper<Car>() {
                    public Car mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Car car = new Car();
                        car.setId(rs.getLong("id"));
                        car.setVersion(rs.getLong("version"));
                        car.setName(rs.getString("name"));
                        car.setPrice(rs.getDouble("price"));
                        car.setSeatsNumber(rs.getInt("seats_number"));
                        car.setPriceOfSale(rs.getDouble("price_of_sale"));
                        car.setDateOfSale(rs.getDate("date_of_sale"));
                        car.setPayerName(rs.getString("payer_name"));
                        return car;
                    }
                });
    }

    @OptimisticallyLocked
    public Car saveCarByJDBC(Car car) {
        String query = "update car set version = ? , name = ?, price = ?, seats_number = ?, date_of_sale = ?, price_of_sale = ?, payer_name = ? where id = ?";
        this.jdbcTemplate.update(query,
                car.getVersion(), car.getName(), car.getPrice(), car.getSeatsNumber(), car.getDateOfSale(), car.getPriceOfSale(), car.getPayerName(), car.getId());
        return this.findOneUsingJDBC(car.getId());
    }

    @OptimisticallyLocked
    public Car updateCarByJDBC(Car car, long pr) {
        System.out.println(car);
        try {
            TimeUnit.SECONDS.sleep(pr);

        } catch (Exception e) {
            System.out.println("ERROR WHILE WAITING..." + e);
        }
        System.out.println("Passing time.........................................");
        String query = "update car set version = ? , name = ?, price = ?, seats_number = ?, date_of_sale = ?, price_of_sale = ?, payer_name = ? where id = ?";
        this.jdbcTemplate.update(query,
                car.getVersion(), car.getName(), car.getPrice(), car.getSeatsNumber(), car.getDateOfSale(), car.getPriceOfSale(), car.getPayerName(), car.getId());
        return this.findOneUsingJDBC(car.getId());
    }

}
