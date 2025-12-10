package com.example.dadambackend.common.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AiClient {

    @Value("${ai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String AI_API_URL = "https://api.openai.com/v1/chat/completions";

    public String request(String systemPrompt, String userPrompt) {
        try {
            // 1. API 키 체크
            if (apiKey == null || apiKey.isBlank()) {
                System.out.println("[AiClient] ai.api.key 설정이 비어 있습니다. application.yml 또는 환경 변수를 확인하세요.");
                return buildFallbackJson();
            }

            // 2. 헤더
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // 3. 메시지 구성
            OpenAiMessage systemMsg = new OpenAiMessage();
            systemMsg.setRole("system");
            systemMsg.setContent(systemPrompt);

            OpenAiMessage userMsg = new OpenAiMessage();
            userMsg.setRole("user");
            userMsg.setContent(userPrompt);

            OpenAiRequest body = new OpenAiRequest();
            body.setModel("gpt-4o-mini");
            body.setMessages(new OpenAiMessage[]{systemMsg, userMsg});

            // 4. 응답 포맷: JSON 오브젝트 강제
            OpenAiRequest.ResponseFormat responseFormat = new OpenAiRequest.ResponseFormat();
            responseFormat.setType("json_object");
            body.setResponse_format(responseFormat);

            HttpEntity<OpenAiRequest> entity = new HttpEntity<>(body, headers);

            // 5. OpenAI 호출
            ResponseEntity<String> response =
                    restTemplate.exchange(AI_API_URL, HttpMethod.POST, entity, String.class);

            System.out.println("[AiClient] status = " + response.getStatusCodeValue());
            System.out.println("[AiClient] body   = " + response.getBody());

            if (!response.getStatusCode().is2xxSuccessful()) {
                System.out.println("[AiClient] OpenAI HTTP 에러 → " + response.getStatusCode());
                return buildFallbackJson();
            }

            // 6. JSON 파싱 (알 수 없는 필드는 모두 무시)
            OpenAiResponse aiResponse =
                    objectMapper.readValue(response.getBody(), OpenAiResponse.class);

            String content = aiResponse.getContentText();
            if (content == null || content.isBlank()) {
                System.out.println("[AiClient] GPT content 비어 있음 → fallback 사용");
                return buildFallbackJson();
            }

            return content;

        } catch (HttpStatusCodeException e) {
            System.out.println("[AiClient] HTTP 예외 발생 → status: " + e.getStatusCode());
            System.out.println("[AiClient] response body: " + e.getResponseBodyAsString());
            return buildFallbackJson();
        } catch (Exception e) {
            System.out.println("[AiClient] GPT 호출 실패 → fallback 사용");
            e.printStackTrace();
            return buildFallbackJson();
        }
    }

    public String request(String prompt) {
        String systemPrompt = "너는 사용자의 요청에 맞는 JSON을 생성하는 어시스턴트야. " +
                "사용자가 요구한 형식 그대로 JSON만 출력해라.";
        return request(systemPrompt, prompt);
    }

    @Data
    @NoArgsConstructor
    private static class OpenAiRequest {
        private String model;
        private OpenAiMessage[] messages;
        private ResponseFormat response_format;

        @Data
        @NoArgsConstructor
        public static class ResponseFormat {
            private String type;
        }
    }

    /**
     * OpenAI의 message 형식
     * - role, content만 사용하고
     * - refusal, annotations 등 나머지는 전부 무시
     */
    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class OpenAiMessage {
        private String role;
        private String content;
    }

    /**
     * OpenAI chat/completions 응답 DTO
     * - choices 배열만 사용하고 나머지 필드는 전부 무시
     */
    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class OpenAiResponse {
        private Choice[] choices;

        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Choice {
            private OpenAiMessage message;
        }

        public String getContentText() {
            try {
                if (choices == null || choices.length == 0) return null;
                OpenAiMessage msg = choices[0].getMessage();
                if (msg == null) return null;
                return msg.getContent();
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Question 용 기본 fallback JSON
     */
    private String buildFallbackJson() {
        return """
            {
              "content": "요즘 가장 감사했던 순간은 뭐야?",
              "category": "MEMORY"
            }
            """;
    }
}
