package pvs.app.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import  org.json.JSONObject;
import pvs.app.service.RepositoryService;
import pvs.app.service.TrelloApiService;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TrelloApiController {
    private final TrelloApiService trelloApiService;
    private final RepositoryService repositoryService;
    @Value("${message.exception}")
    private String exceptionMessage;
    @Value("${message.fail}")
    private String failMessage;

    public TrelloApiController(TrelloApiService trelloApiService, RepositoryService repositoryService) {
        this.trelloApiService = trelloApiService;
        this.repositoryService = repositoryService;
    }

    @GetMapping("/trello/board")
    public ResponseEntity<String> getDataFromTrello(@RequestParam("url") String url) {
        if (repositoryService.checkTrelloURL(url)) {
            return ResponseEntity.status(HttpStatus.OK).body(this.trelloApiService.generate_data(url));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/trello/deletecard")
    public ResponseEntity<String> deleteCard(@RequestParam("cardId") String ID){
       // System.out.println("ID " + ID);
        if (this.trelloApiService.deleteCard(ID) == 200)
        return ResponseEntity.ok().build();
        else return ResponseEntity.badRequest().build();

    }
    @PostMapping("/trello/updatecard")
    public ResponseEntity<String> updateCard(@RequestParam("changedata") JSONObject data){
        System.out.println("D " + data.toString());
        if (this.trelloApiService.updateCard(data) == 200)
            return ResponseEntity.ok().build();
        else return ResponseEntity.badRequest().build();
    }

    @PostMapping("/trello/addcard")
    public ResponseEntity<String> createCard(@RequestParam("card") JSONObject data,@RequestParam("laneId") String laneId){

        System.out.println("data " +data);
        if (this.trelloApiService.createCard(data,laneId) == 200)
            return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/trello/addlane")
    public ResponseEntity<String> addLane(@RequestParam("url") String url,@RequestParam("params") JSONObject data){
        if (this.trelloApiService.addLane(url,data.getString("title")) == 200)
            return ResponseEntity.ok().build();
        return  ResponseEntity.badRequest().build();

    }
}
