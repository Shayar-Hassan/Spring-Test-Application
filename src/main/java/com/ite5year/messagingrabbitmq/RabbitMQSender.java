package com.ite5year.messagingrabbitmq;

import com.ite5year.models.RabbitMessage;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class RabbitMQSender {

    private AmqpTemplate rabbitTemplate;

    @Value("${ite5year.rabbitmq.exchange}")
    private String exchange;

    @Value("${ite5year.rabbitmq.routingkey}")
    private String routingkey;

    @Autowired
    public void setRabbitTemplate(AmqpTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public RabbitMessage send(RabbitMessage rabbitMessage) {
        rabbitTemplate.convertAndSend(exchange, routingkey, rabbitMessage);
        System.out.println("Send msg = " + rabbitMessage);
        return rabbitMessage;
    }
}