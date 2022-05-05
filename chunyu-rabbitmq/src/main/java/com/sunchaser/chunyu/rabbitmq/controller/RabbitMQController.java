package com.sunchaser.chunyu.rabbitmq.controller;

import com.sunchaser.chunyu.rabbitmq.model.MsgDTO;
import com.sunchaser.chunyu.rabbitmq.mq.producer.RabbitMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息发送入口
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@RestController
public class RabbitMQController {

    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    @GetMapping("/send")
    public void send(String msg, String routingKey) {
        rabbitMQProducer.send(msg, routingKey);
    }

    @PostMapping("/send")
    public void send(@RequestBody MsgDTO msgDTO, String routingKey) {
        rabbitMQProducer.send(msgDTO, routingKey);
    }

    @PostMapping("/send-ttl")
    public void send(@RequestBody MsgDTO msgDTO, String routingKey, String expire) {
        rabbitMQProducer.send(msgDTO, routingKey, expire);
    }

    @PostMapping("/send-normal")
    public void sendNormalExchange(@RequestBody MsgDTO msgDTO, String routingKey) {
        rabbitMQProducer.sendNormalExchange(msgDTO, routingKey);
    }

    @PostMapping("/send-delayed")
    public void send(@RequestBody MsgDTO msgDTO, String routingKey, Integer delay) {
        rabbitMQProducer.send(msgDTO, routingKey, delay);
    }
}
