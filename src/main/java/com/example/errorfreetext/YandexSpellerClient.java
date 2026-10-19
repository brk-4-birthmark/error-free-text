package com.example.errorfreetext;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class YandexSpellerClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String API_URL = "https://speller.yandex.net/services/spellservice.json/checkTexts";

    // 1. Добавили параметр lang в метод
    public String correctText(String originalText, String lang) {
        int options = 0;
        if (originalText.matches(".*\\d.*")) {
            options += 2;
        }
        if (originalText.matches(".*(https?://|www\\.).*")) {
            options += 4;
        }

        List<String> chunks = TextSplitter.splitText(originalText);
        StringBuilder finalResult = new StringBuilder();

        for (String chunk : chunks) {
            // 2. Передаем lang в метод sendToYandex
            String correctedChunk = sendToYandex(chunk, options, lang);
            finalResult.append(correctedChunk);
        }

        return finalResult.toString();
    }

    @SuppressWarnings("unchecked")
    // 3. Добавили параметр lang в метод
    private String sendToYandex(String text, int options, String lang) {
        try {
            org.springframework.util.MultiValueMap<String, String> params = new org.springframework.util.LinkedMultiValueMap<>();
            params.add("text", text);
            // 4. Передаем язык, который пришел от пользователя (в нижнем регистре, т.к. Яндекс ждет 'ru' или 'en')
            params.add("lang", lang.toLowerCase());
            params.add("options", String.valueOf(options));

            List<List<Map<String, Object>>> response = restTemplate.postForObject(API_URL, params, List.class);

            if (response == null || response.isEmpty()) {
                return text;
            }

            List<Map<String, Object>> errors = response.get(0);
            if (errors == null || errors.isEmpty()) {
                return text;
            }

            StringBuilder result = new StringBuilder(text);

            errors.sort((e1, e2) -> {
                int pos1 = (Integer) e1.get("pos");
                int pos2 = (Integer) e2.get("pos");
                return Integer.compare(pos2, pos1);
            });

            for (Map<String, Object> error : errors) {
                int pos = (Integer) error.get("pos");
                int len = (Integer) error.get("len");
                List<String> suggestions = (List<String>) error.get("s");

                if (suggestions != null && !suggestions.isEmpty()) {
                    String correctWord = suggestions.get(0);
                    result.replace(pos, pos + len, correctWord);
                }
            }

            return result.toString();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка API Яндекса: " + e.getMessage());
        }
    }
}