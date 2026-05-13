package com.cab302thursdaytbd.Service;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class PetConversationService {
    private final String apiKey;
    private static final String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent";
    public PetConversationService() {
        this.apiKey = System.getenv("DesktopPetAPIKey");
    }
    public String askQuestion(String question) throws IOException, InterruptedException {
        String jsonBody = "{" +
                "\"contents\": [{" +
                "\"parts\":[{" +
                "\"text\": \"" + question + "\"" +
                "}]" +
                "}]" +
                "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PetConversationService.url))
                .header("Content-Type", "application/json")
                // Gemini uses 'x-goog-api-key' instead of 'Authorization: Bearer'
                .header("x-goog-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        String respText = null;
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String strJsonResBody = response.body();
            System.out.println("Status Code: " + statusCode);
            if (statusCode == 200) {
                System.out.println("Response: " + strJsonResBody);
                JSONObject jsonResp = new JSONObject(strJsonResBody);
                respText = jsonResp.getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
            }
        } catch (Exception e) {
            System.err.println("Error calling Gemini: " + e.getMessage());
            throw e;
        }
        return respText;
    }
    public static void main(String[] args) {
        // Replace with your actual Gemini API Key

        String prompt = "Hello, how are you?";
        String AIprompt = "You should reply as if you are a pet do not reply with actions, that current has a mood of sad with a curiousity level below average" + prompt;
        PetConversationService petService = new PetConversationService();
        String response;
        try {
            response = petService.askQuestion(AIprompt);
        } catch (Exception e) {
            response = e.getMessage();
        }
        System.out.println(response);

    }
}
