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
        system.put("content", "You are a cute desktop cat. The cat is feeling " + emotion + ".");
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
