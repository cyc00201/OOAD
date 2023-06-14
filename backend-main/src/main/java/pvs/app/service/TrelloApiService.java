package pvs.app.service;

import com.google.gson.Gson;
import kong.unirest.CookieSpecs;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.extern.java.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import pvs.app.service.data.TrelloData;

import java.util.Iterator;
import java.util.Objects;

@Service
@SuppressWarnings("squid:S1192")
public class TrelloApiService {
    private final String trelloApiKey, trelloApiToken;
    private final String trelloApiBaseUrl;

    public TrelloApiService() {
        this.trelloApiKey = System.getenv("PVS_TRELLO_KEY");
        this.trelloApiToken = System.getenv("PVS_TRELLO_TOKEN");
        this.trelloApiBaseUrl = "https://api.trello.com/1/";
        Unirest.config().cookieSpec(CookieSpecs.IGNORE_COOKIES);
    }

    public String getBoardsFromTrello() {
        HttpResponse<String> response = Unirest.get(trelloApiBaseUrl+ "members/me/boards?fields=name,url&key=" + trelloApiKey + "&token=" + trelloApiToken)
                .header("Accept", "application/json")
                .asString();

        return response.getBody();
    }

    public String getMemberNamebyId(String memid){

        HttpResponse<JsonNode> response = Unirest.get("https://api.trello.com/1/members/" + memid)
                .header("Accept", "application/json")
                .queryString("key", trelloApiKey)
                .queryString("token", trelloApiToken)
                .asJson();
        return response.getBody().getObject().get("fullName").toString();
    }

    public String getBoard(String url) {
        JSONArray jsonArray = new JSONArray(getBoardsFromTrello());
        String id = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (Objects.equals(jsonObject.getString("url"), url)) {
                id = jsonObject.getString("id");
                break;
            }
        }
        return id;
    }

    public String getDataOfBoard(String ID, String dataCategory) {
        HttpResponse<String> response = Unirest.get(trelloApiBaseUrl+ "boards/" + ID + "/" + dataCategory + "?&key=" + trelloApiKey + "&token=" + trelloApiToken)
                .header("Accept", "application/json")
                .asString();

        return response.getBody();
    }

    public String getDataOfList(String ID, String dataCategory) {
        HttpResponse<String> response = Unirest.get(trelloApiBaseUrl + "lists/" + ID + "/" + dataCategory + "?&key=" + trelloApiKey + "&token=" + trelloApiToken)
                .header("Accept", "application/json")
                .asString();

        return response.getBody();
    }

    public String generate_data(String url) {
        String id = getBoard(url);
        JSONArray listsOfBoard = new JSONArray(getDataOfBoard(id, "lists"));
        TrelloData trelloData = new TrelloData();
        for (int i = 0; i < listsOfBoard.length(); i++) {
            JSONObject list = listsOfBoard.getJSONObject(i);
            JSONArray cardsInList = new JSONArray(getDataOfList(list.getString("id"), "cards"));
            String label = cardsInList.length() + "/" + cardsInList.length();
            TrelloData.Lane lane = trelloData.createLane(list.getString("id"), list.getString("name"), label, 280);
            for (int j = 0; j < cardsInList.length(); j++) {
                JSONObject cardJsonObject = cardsInList.getJSONObject(j);
                TrelloData.Card card;

                if (cardJsonObject.getJSONArray("idMembers").length() > 0){
                    JSONArray memberlist = cardJsonObject.getJSONArray("idMembers");
                    String members = "\nCardOwner: " + getMemberNamebyId(memberlist.getString(0));
                    for (int c = 1;c < memberlist.length(); c++){
                        members += "," + getMemberNamebyId(memberlist.getString(i));
                    }
                    card = new TrelloData.Card(cardJsonObject.getString("id"), cardJsonObject.getString("name"), "", cardJsonObject.getString("desc"),members);
                }
                else {
                     card = new TrelloData.Card(cardJsonObject.getString("id"), cardJsonObject.getString("name"), "", cardJsonObject.getString("desc"));
                }

                lane.addCard(card);
            }
            trelloData.addLane(lane);
        }
        Gson gson = new Gson();
        return gson.toJson(trelloData);
    }

    public String getAvatarURL() {
        HttpResponse<String> response = Unirest.get(trelloApiBaseUrl+ "members/me/?fields=avatarUrl&key=" + trelloApiKey + "&token=" + trelloApiToken)
                .header("Accept", "application/json")
                .asString();
        JSONObject jsonObject = new JSONObject(response.getBody());
        return jsonObject.getString("avatarUrl");
    }

    public  int updateCard(JSONObject data){
        String cardId = data.getString("id");
        String changeKey = "";
        Iterator it = data.keys();
        while(it.hasNext()){

            String  item = it.next().toString();
           if (! item.equals("id")){
              // System.out.println(item);
               changeKey = item;
           }
        }


        System.out.println(changeKey);
        HttpResponse<JsonNode> response;

        if (changeKey.equals("title")){
            //card name
            System.out.println(changeKey);
             response = Unirest.put(trelloApiBaseUrl + "cards/" + cardId)
                    .header("Accept", "application/json")
                    .queryString("name",data.getString(changeKey))
                    .queryString("key", trelloApiKey)
                    .queryString("token", trelloApiToken)
                    .asJson();
            System.out.println(response.getBody());
            return  response.getStatus();
        }
        else if (changeKey.equals("description")){
            //card description
            response =  Unirest.get(trelloApiBaseUrl + "cards/" + cardId)
                    .queryString("key", trelloApiKey)
                    .queryString("token", trelloApiToken)
                    .asJson();
           // System.out.println(response.getBody());

            response = Unirest.put(trelloApiBaseUrl + "cards/" + cardId)
                    .header("Accept", "application/json")
                    .queryString("desc",data.getString(changeKey))
                    .queryString("key", trelloApiKey)
                    .queryString("token", trelloApiToken)
                    .asJson();

            return  response.getStatus();
        }
        else  if (changeKey.equals("label"))
            return  200;
        else
            return  404;
    }

    public int createCard(JSONObject card,String laneId){
        if(card.has("description") == false){

            System.out.println("D empty");
            HttpResponse<JsonNode> response = Unirest.post(trelloApiBaseUrl + "cards")
                    .header("Accept", "application/json")
                    .queryString("idList", laneId)
                    .queryString("name",card.getString("title"))
                    .queryString("key", trelloApiKey)
                    .queryString("token", trelloApiToken)
                    .asJson();

            return  response.getStatus();
        }
        System.out.println("Data");
        System.out.println(card.toString());

        HttpResponse<JsonNode> response = Unirest.post(trelloApiBaseUrl + "cards")
                .header("Accept", "application/json")
                .queryString("idList", laneId)
                .queryString("name",card.getString("title"))
                .queryString("desc",card.getString("description"))
                .queryString("key", trelloApiKey)
                .queryString("token", trelloApiToken)
                .asJson();
       // System.out.println(response.getBody());
        return  response.getStatus();

    }
    public int deleteCard(String ID){

        HttpResponse<String> response = Unirest.delete(trelloApiBaseUrl + "cards/" + ID)
                .queryString("key", trelloApiKey)
                .queryString("token", trelloApiToken)
                .asString();
       // System.out.println(response.getStatus());
        return  response.getStatus();
    }

    public int addLane(String url,String title){
        String id = getBoard(url);

        HttpResponse<String> response = Unirest.post("https://api.trello.com/1/lists")
                .queryString("name", title)
                .queryString("idBoard", id)
                .queryString("pos","top")
                .queryString("key", trelloApiKey)
                .queryString("token", trelloApiToken)
                .asString();

        System.out.println(response.getBody());
        System.out.println(id);
        return  response.getStatus();
    }
}
