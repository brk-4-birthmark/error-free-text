package com.example.errorfreetext;

import java.util.ArrayList;
import java.util.List;

public class TextSplitter {
    private static final int MAX_CHUNK_SIZE = 9000;

    public static List<String> splitText(String text) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isEmpty()) return chunks;

        int length = text.length();
        for (int i = 0; i < length; i += MAX_CHUNK_SIZE) {
            int end = Math.min(length, i + MAX_CHUNK_SIZE);
            chunks.add(text.substring(i, end));
        }
        return chunks;
    }
}