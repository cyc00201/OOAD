package pvs.app.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import pvs.app.config.ApplicationConfig;
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
}
