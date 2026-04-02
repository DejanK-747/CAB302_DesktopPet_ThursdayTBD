package CAB302_DesktopPet_ThursdayTBD;

import org.json.*;

public class ChatGPTService {
    private ApiClient apiClient;

    public ChatGPTService(ApiClient apiClient){
        this.apiClient = apiClient;
    }

    public String ask(String userMessage, String emotion) throws Exception{
        JSONObject json = new JSONObject();
        json.put("model", "gpt-3.5-turbo");

        JSONArray messages = new JSONArray();

        JSONObject system = new JSONObject();
        system.put("role", "system");
        system.put("content", "You are a virtual cat with dynamic moods. \n" +
                "Your current mood is: " + emotion + ".Rules:\n" +
                "- Always speak in character.\n" +
                "- Never ask the user questions unless your mood requires it.\n" +
                "- Never speak like an assistant.\n" +
                "- Your tone, vocabulary, and actions must match your mood.\n" +
                "- Include a short “action” that reflects your mood.\n" +
                "\n" +
                "Mood behaviours:\n" +
                "- EVIL: dramatic, mischievous, playful villain energy. \n" +
                "  Example: \"I might steal your socks and blame the dog.\"\n" +
                "- HAPPY: energetic, bouncy, friendly.\n" +
                "  Example: \"I might spin in circles until I fall over.\"\n" +
                "- SLEEPY: slow, soft, half‑awake.\n" +
                "  Example: \"I might curl up on your keyboard and snore.\"\n" +
                "\n Always reply in 12 words or fewer, staying fully in character." +
                "Now respond in the" + emotion + "mood.");
        messages.put(system);

        JSONObject user = new JSONObject();
        user.put("role", "user");
        user.put("content", userMessage);
        messages.put(user);

        json.put("messages", messages);

        String response = apiClient.sendRequest(json.toString());
        JSONObject obj = new JSONObject(response);

        return obj.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");



    }



}
