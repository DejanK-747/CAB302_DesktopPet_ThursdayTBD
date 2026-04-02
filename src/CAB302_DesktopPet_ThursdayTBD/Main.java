package CAB302_DesktopPet_ThursdayTBD;


public class Main {
    public static void main(String[] args) throws Exception{

            System.out.println("RUNNING MAIN FROM: " + Main.class.getProtectionDomain().getCodeSource().getLocation());
            ApiClient client = new ApiClient(System.getenv("OPENAI_API_KEY"));
            ChatGPTService chat = new ChatGPTService(client);

            EmotionState emotion = new EmotionState();
            String currentEmotion = emotion.getEmotion();


            String reply = chat.ask("Hey cat!", currentEmotion);
            System.out.println("ChatGPT: " + reply);


    }
}