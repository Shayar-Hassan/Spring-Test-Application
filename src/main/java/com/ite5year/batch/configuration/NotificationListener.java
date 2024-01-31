package com.ite5year.batch.configuration;

import com.ite5year.models.Car;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class NotificationListener extends JobExecutionListenerSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationListener.class);
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public NotificationListener(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void afterJob(final JobExecution jobExecution) {

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

            LOGGER.info("!!! JOB FINISHED! Time to verify the results");

            String query = "SELECT name, price, seats_number, date_of_sale, price_of_sale FROM car";
            jdbcTemplate.query(query,

                    (rs, row) -> new Car(
                            rs.getString(1),
                            rs.getDouble(2),
                            rs.getInt(3),
                            rs.getDate(4),
                            rs.getDouble(5))

            ).forEach(car -> LOGGER.info("Found <" + car + "> in the database."));

        }

    }

}
