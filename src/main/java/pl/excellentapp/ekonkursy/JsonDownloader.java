package pl.excellentapp.ekonkursy;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class JsonDownloader {

    private final ObjectMapper objectMapper;

    JsonDownloader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T getJson(String urlString, Class<T> clazz) {
        return getJson(urlString, "GET", "application/json", clazz);
    }

    <T> T getJson(String urlString, String method, String accept, Class<T> clazz) {
        String jsonResponse = fetchJson(urlString, method, accept);
        return parseJson(jsonResponse, clazz);
    }

    private String fetchJson(String urlString, String method, String accept) {
        try {
            URI uri = URI.create(urlString);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Accept", accept);

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("HTTP request failed with status: " + responseCode);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            } finally {
                conn.disconnect();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch JSON from " + urlString, ex);
        }
    }

    private <T> T parseJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to parse JSON", ex);
        }
    }
}

