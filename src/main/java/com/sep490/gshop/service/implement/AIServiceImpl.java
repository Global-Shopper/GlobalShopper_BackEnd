package com.sep490.gshop.service.implement;

import com.sep490.gshop.service.AIService;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AIServiceImpl implements AIService {
    private final ChatClient chatClient;

    @Autowired
    public AIServiceImpl(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @Override
    public String chat(String message) {
        try {
            log.info("chat() AIServiceImpl start | message: {}", message);
            String response = chatClient.prompt().user(message).call().content();
            log.info("chat() AIServiceImpl end | response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error during chat: {}", e.getMessage());
            throw e;
        }
    }
}
