import com.google.gson.*;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FoodDataCentralAPI {
    private static final String key = "TmJjcoWwSpvq1cIpC8JhAs0k4V4UmcUcBcFgx4lC";
    private static final String url = "https://api.nal.usda.gov/fdc/v1/";
    private static final String fTags1 = "&dataType=Foundation&pageSize=1&pageNumber=1";
    private static final String fTags2 = "&dataType=Survey&pageSize=1&pageNumber=1";
    private static final String fTags3 = "&dataType=Branded&pageSize=1&pageNumber=1";
    // Tags for searching only the nutrients we want
    private static final String iDTags = "&format=abridged&nutrients=203&nutrients=204&nutrients=205&nutrients=208&nutrients=957";

   public String searchFood(String foodName) throws IOException {
       // Complete path for searching the food id
       String path = url + "foods/search?api_key=" + key + "&query=" + foodName + fTags1;
       JsonObject x = GET(path);

       // Compare number of hits to know if the food exists in the API for every type of food
       int totalHits = x.get("totalHits").getAsInt();
       if(totalHits == 0) {
           path = url + "foods/search?api_key=" + key + "&query=" + foodName + fTags2;
           x = GET(path);
           totalHits = x.get("totalHits").getAsInt();
           if(totalHits == 0){
               path = url + "foods/search?api_key=" + key + "&query=" + foodName + fTags3;
               x = GET(path);
               totalHits = x.get("totalHits").getAsInt();
               if(totalHits == 0){
                   return "Not Found";
               }
           }
       }

       // Get food information array
       JsonArray food = x.get("foods").getAsJsonArray();
       JsonElement id = food.get(0);

       // Recieve the id from the food
       int id2= id.getAsJsonObject().get("fdcId").getAsInt();

       return searchById(id2);
   }

   public String searchById(int foodId) throws IOException {
       //Complete path for searching the exact food information
       String path = url + "food/" + foodId + "?api_key=" + key + "&query=" + foodId + iDTags;
       JsonObject json = GET(path);
       JsonArray nutrients = json.get("foodNutrients").getAsJsonArray();
       ArrayList<String> result = new ArrayList<>(4);
       for (int i=0; i<4; i++){
           result.add("");
       }
       // Iterate for all the nutrients we receive
       for (int i=0; i < nutrients.size(); i++)
       {
           JsonElement score = nutrients.get(i);
           String x = score.getAsJsonObject().get("number").getAsString();
           String y = score.getAsJsonObject().get("amount").getAsString();
           //System.out.println( " "+ x + " amount " + y);
           switch (x) {
               case "203" -> result.set(0, y);
               case "204" -> result.set(1, y);
               case "205" -> result.set(2, y);
               case "208" -> result.set(3, y);
               case "957" -> result.set(3, y);
           }
       }
       Gson gson = new Gson();
       String ret = gson.toJson(result);
       return ret;
   }
   // Function to open a path, receiving the information in a JsonObject
   public JsonObject GET(String path) throws IOException {
       URL url = new URL(path);
       HttpURLConnection con = (HttpURLConnection) url.openConnection();
       con.setRequestMethod("GET");

       BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
       String inputLine;
       StringBuilder response = new StringBuilder();
       while ((inputLine = in.readLine()) != null) {
           response.append(inputLine);
       }
       in.close();

       // Analize Json response
       JsonObject responseJson = JsonParser.parseString(response.toString()).getAsJsonObject();

       in.close();
       return responseJson;
   }
}
