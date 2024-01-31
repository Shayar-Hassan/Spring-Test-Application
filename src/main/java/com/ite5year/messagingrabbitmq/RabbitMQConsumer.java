package com.ite5year.messagingrabbitmq;

import com.ite5year.models.Car;
import com.ite5year.models.RabbitMessage;
import com.ite5year.services.CarServiceImpl;
import com.ite5year.services.GoogleGmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RabbitMQConsumer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);
    private CarServiceImpl carService;


    @Autowired
    public void setCarService(CarServiceImpl carService) {
        this.carService = carService;
    }

    public boolean sendToEmail(RabbitMessage rabbitMessage) {
        String sellingDate = rabbitMessage.getDate();
        System.out.println("sellingDate: " + sellingDate);
        if (!sellingDate.equals("")) {

            try {
                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                final LocalDateTime dt = LocalDateTime.parse(sellingDate, formatter);
                System.out.println(dt);
                System.out.println("Getting cars........");
                List<Car> cars = carService.findAllSoldCardByDate(dt);
                System.out.println("Cars: " + cars);
                FileWriter writer = new FileWriter("sto1.csv");
                StringBuilder stringBuilder = new StringBuilder();
                String[] headers = {"carId", "carName", "dateOfSale", "ownerName"};
                stringBuilder.append(String.join(",", headers));
                stringBuilder.append('\n');
                cars.forEach(car -> {
                    String line = car.getId() + ","
                            + car.getName() + ","
                            + car.getDateOfSale().toString() + ","
                            + car.getPayerName();
                    stringBuilder.append(line).append('\n');
                });
                writer.write(stringBuilder.toString());
                writer.close();


                File file = new File(rabbitMessage.getFileName());
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                Multipart multipart = new MimeMultipart();
                FileDataSource source = new FileDataSource(file);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(rabbitMessage.getFileName());
                multipart.addBodyPart(messageBodyPart);
                GoogleGmailService.Send(
                        "abd.fl.19999@gmail.com",
                        "A3#33$$F",
                        rabbitMessage.getEmail(),
                        "Report",
                        rabbitMessage.getContent(), multipart);

                System.out.println("Successfully an email is sent");
                return true;
            } catch (Exception e) {
                System.out.println("Error: " + e);
                return false;
            }
        }
        return false;
    }

    @RabbitListener(queues = "ite5year.queue")
    public void receivedMessage(RabbitMessage rabbitMessage) {
        logger.info("Received Message From RabbitMQ: " + rabbitMessage);
        this.sendToEmail(rabbitMessage);
    }
}