package com.example.gifserverv3.global.kafka;

import java.util.HashMap;
import java.util.Map;

import com.example.gifserverv3.domain.chatmsg.entity.ChatMsgEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Consumers {
    private final Logger logger = LoggerFactory.getLogger(Consumers.class);
    @KafkaListener(topics = "${spring.kafka.template.default-topic}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(@Payload ChatMsgEntity chatMsg) throws Exception {
        logger.info("Consume msg : roomId : '{}', username :'{}', sender : '{}' ",
                chatMsg.getId(), chatMsg.getUser().getUsername(), chatMsg.getMessage());
        Map<String, String> msg = new HashMap<>();
        msg.put("roomNum", String.valueOf(chatMsg.getChatRoom().getId()));
        msg.put("message", chatMsg.getMessage());
        msg.put("sender", chatMsg.getUser().getUsername());
    }
}
