package com.sep490.gshop.service.implement;

import com.sep490.gshop.common.constants.PromptConstant;
import com.sep490.gshop.payload.response.RawDataResponse;
import com.sep490.gshop.service.AIService;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public RawDataResponse getRawData(String link) {
        try {
            log.info("getRawData() AIServiceImpl start");
            String prompt = PromptConstant.PROMPT_RAW_DATA.formatted(link);
            ChatOptions chatOptions = ChatOptions.builder()
                    .temperature(0D)
                    .build();
            String output = chatClient.prompt().options(chatOptions).user(prompt).call().content();

            if (output == null || output.isEmpty()) {
                log.warn("getRawData() AIServiceImpl | No data returned from chat client");
                return RawDataResponse.builder().build();
            }
            RawDataResponse response = parseRawData(output);
            log.info("getRawData() AIServiceImpl end | product name: {}", response.getName());
            return response;
        } catch (Exception e) {
            log.error("getRawData() AIServiceImpl error | error: {}", e.getMessage());
            throw e;
        }
    }

    private RawDataResponse parseRawData(String rawData) {
        String[] lines = rawData.split("\\r?\\n");
        String name = null;
        String description = null;
        List<String> variants = new ArrayList<>();

        for (String line : lines) {
            if (line.startsWith("Name:")) {
                name = line.substring(5).trim();
            } else if (line.startsWith("Description:")) {
                description = line.substring(12).trim();
            } else if (line.startsWith("Variant:")) {
                String variantStr = line.substring(8).trim();
                if (!variantStr.isEmpty()) {
                    variants = Arrays.stream(variantStr.split(";"))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .toList();
                }
            }
        }

        return RawDataResponse.builder()
                .name(name)
                .description(description)
                .variants(variants.isEmpty() ? null : variants)
                .build();
    }
}
