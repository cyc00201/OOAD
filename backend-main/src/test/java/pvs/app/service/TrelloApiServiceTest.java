package pvs.app.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import  pvs.app.service.TrelloApiService;
import pvs.app.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class,webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TrelloApiServiceTest {

    @Autowired
    private TrelloApiService trelloApiService;

    @Test
    public void updateCard() throws JSONException {
        String datastr = "{'title':'dd0000','id':'6487ad7a39ea2e6c5770e496'}";
        JSONObject data = new JSONObject(datastr);
        int status = trelloApiService.updateCard(data);
        Assert.assertEquals(status,404);

    }
    @Test
    public void createCard() throws JSONException {
        String laneID = "640ec44dbe6bd2b7acc21798";
        String datastr = "{\"id\":\"8b738720-0997-11ee-8a68-2fcc5b85c53d\",\"title\":\"fdfsdfsdf\"}";

        JSONObject data = new JSONObject(datastr);
        int status = trelloApiService.createCard(data,laneID);

        Assert.assertEquals(status ,200);
    }

    @Test

    public void deleteCard(){
        String ID = "645117cec1d6a61a35c7ea94";
       int status = trelloApiService.deleteCard(ID);
       Assert.assertEquals(status,404);
    }
    @Test
    public  void getMemberNamebyId(){
        String memID = "638892a833756400f3d15abb";
        String name = trelloApiService.getMemberNamebyId(memID);
       // System.out.println("N = " + name);
        Assert.assertEquals(name,"李映廷");
    }

    @Test
    public  void AddLane(){
        String url = "https://trello.com/b/MJUrWbBD/test";
        int status = trelloApiService.addLane(url,"Newtest");
        Assert.assertEquals(status,200);
    }
}
