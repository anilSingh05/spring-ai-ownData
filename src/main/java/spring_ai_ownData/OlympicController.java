package spring_ai_ownData;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/olympics")
public class OlympicController {

    private final ChatClient chatClient;

    @Value("classpath:/prompts/olympic-sports.st")
    private Resource olympicSportResource;
    @Value("classpath:/data/olympic-sports.txt")
    private Resource docsToStuffResource;

    public OlympicController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping("/2024")
    public String get2024OlympicSports(
            @RequestParam(value = "message", defaultValue = "What sports aare being included in  the  2024 Summer Olympics?") String message,
            @RequestParam(value = "stuffIt", defaultValue = "false") String stuffIt){

        PromptTemplate promptTemplate = new PromptTemplate(olympicSportResource);
        Map<String, Object> map =  new HashMap<>();
        map.put("question", message);

        if(stuffIt.equalsIgnoreCase("TRUE")){
            map.put("context",  docsToStuffResource);
        }else{
            map.put("context", "");
        }

        Prompt prompt = promptTemplate.create(map);
        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();

        return chatResponse.getResult().getOutput().getContent();
    }
}
