import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.sql.*;
import com.google.gson.Gson;

public class WebServer {
    private static final int PORT = 8080;

    //Initializing database connection
    Database db = new Database();
    Connection conn = db.connect();

    public WebServer() {
        try {
            ServerSocket serverConnect = new ServerSocket(PORT);
            System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
            // We listen until user halts server execution
            while (true) {
                // Each client connection will be managed in a dedicated Thread
                new SocketThread(serverConnect.accept());
                // Create dedicated thread to manage the client connection
            }
        } catch (IOException e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }

    private class SocketThread extends Thread {
        private final Socket insocked;

        //Initializing each thread
        SocketThread(Socket insocket) {
            this.insocked = insocket;
            this.start();
        }

        @Override
        public void run() {
            // We manage our particular client connection
            BufferedReader in;
            PrintWriter out;
            String resource;

            try {
                // We read characters from the client via input stream on the socket
                in = new BufferedReader(new InputStreamReader(insocked.getInputStream()));
                // We get character output stream to client
                out = new PrintWriter(insocked.getOutputStream());
                // Get first line of the request from the client
                String input = in.readLine();

                System.out.println("sockedthread : " + input);

                StringTokenizer parse = new StringTokenizer(input);
                String method = parse.nextToken().toUpperCase();
                // We get the HTTP method of the client
                if (!method.equals("GET") && !method.equals("POST")) {
                    System.out.println("501 Not Implemented : " + method + " method.");
                } else if (method.equals("GET")) {
                    // What comes after "localhost:8080"
                    resource = parse.nextToken();
                    System.out.println("input " + input);
                    System.out.println("method " + method);
                    System.out.println("resource " + resource);

                    // Divide the message by '/' and '&'
                    parse = new StringTokenizer(resource, "/[?]=&");
                    int i = 0;
                    String[] tokens = new String[20];
                    while (parse.hasMoreTokens()) {
                        tokens[i] = parse.nextToken();
                        System.out.println("token " + i + "=" + tokens[i]);
                        i++;
                    }

                    // Answer that will be sent to the client
                    String answer = makeHeaderAnswer() + makeBodyAnswerGET(tokens);
                    System.out.println("answer\n" + answer);
                    out.println(answer);
                    out.flush();
                }
                else { // Is POST
                    resource = parse.nextToken();
                    System.out.println("input " + input);
                    System.out.println("method " + method);
                    System.out.println("resource " + resource);

                    parse = new StringTokenizer(resource, "/[?]=&");
                    int i = 0;
                    String[] tokens = new String[20];
                    while (parse.hasMoreTokens()) {
                        tokens[i] = parse.nextToken();
                        System.out.println("token " + i + "=" + tokens[i]);
                        i++;
                    }

                    // Reading until we found the empty line between the header and the message
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        if (inputLine.isEmpty()) {
                            break;
                        }
                        System.out.println(inputLine);
                    }

                    // Reading the message itself
                    StringBuilder requestBody = new StringBuilder();
                    while (in.ready()) {
                        char c = (char) in.read();
                        requestBody.append(c);
                    }

                    String requestBodyString = requestBody.toString();


                    // Answer to be sent to the client
                    String answer = makeHeaderAnswer() + makeBodyAnswerPOST(tokens, requestBodyString);
                    System.out.println("answer\n" + answer);
                    out.println(answer);
                    out.flush();
                }

                in.close();
                out.close();
                insocked.close();
            } catch (Exception e) {
                System.err.println("Exception : " + e);
            }
        }

        // Every single GET case the client might ask us for
        private String makeBodyAnswerGET(String[] tokens) throws SQLException, IOException {
            String body = "";
            // tokens[0] has the case
            switch (tokens[0]) {
                case "getFoodConsumed": {
                    String userID = tokens[2];
                    ArrayList<String[]> foodConsumed = db.getFoodConsumed(conn, userID);
                    Gson gson = new Gson();
                    body = gson.toJson(foodConsumed);
                    break;
                }

                case "getSearchedFood": {
                    String userID = tokens[2];
                    ArrayList<String[]> searchedFood = db.getSearchedFood(conn, userID);
                    Gson gson = new Gson();
                    body = gson.toJson(searchedFood);
                    break;
                }

                case "getScannedFood": {
                    String userID = tokens[2];
                    ArrayList<String[]> scannedFood = db.getScannedFood(conn, userID);
                    Gson gson = new Gson();
                    body = gson.toJson(scannedFood);
                    break;
                }

                case "deleteFoodConsumed": {
                    String foodID = tokens[2];
                    db.deleteFoodConsumed(conn, foodID);
                    body = "{}";
                    break;
                }

                case "searchFoodInformation": {
                    String foodName = tokens[2];
                    FoodDataCentralAPI API = new FoodDataCentralAPI();
                    body = API.searchFood(foodName);
                    break;
                }
                case "getUser":{
                    String userID = tokens[2];
                    ArrayList<String> list = db.getUserID(conn, userID);
                    Gson gson = new Gson();
                    body = gson.toJson(list);
                    break;
                }

                default:
                    assert false;
            }
            System.out.println(body);
            return body;
        }

        // Every single POST case the client might ask us for
        private String makeBodyAnswerPOST(String[] tokens, String requestBodyString) throws SQLException, NoSuchAlgorithmException, ParseException {
            String body = "";

            // Dividing the message to get all the parameters
            String[] params = requestBodyString.split("&");
            String param1 = "";
            String param2 = "";
            String param3 = "";
            String param4 = "";
            String param5 = "";
            String param6 = "";
            String param7 = "";

            // Assigning the params that exist to variables. If they don't exist, the variables will not be
            // initialized.
            for (String param : params) {
                String[] keyValue = param.split("=");
                switch (keyValue[0]) {
                    case "param1" -> param1 = keyValue[1];
                    case "param2" -> param2 = keyValue[1];
                    case "param3" -> param3 = keyValue[1];
                    case "param4" -> param4 = keyValue[1];
                    case "param5" -> param5 = keyValue[1];
                    case "param6" -> param6 = keyValue[1];
                    case "param7" -> param7 = keyValue[1];

                }
            }

            // All the possible cases
            switch (tokens[0]) {
                case "register": {
                    //param1=user_name, param2=user_email, param3=user_password,param4=user_height
                    //param5=user_weight
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(param3.getBytes(StandardCharsets.UTF_8));
                    String hashedPassword = new String(hash, StandardCharsets.UTF_8);
                    db.register(conn, param1, param2, hashedPassword, param4, param5);
                    body = "{}";
                    break;
                }

                case "login": {
                    //param1=user_email, param2=user_password
                    ArrayList<String> list = db.login(conn, param1);
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(param2.getBytes(StandardCharsets.UTF_8));
                    String hashedPassword = new String(hash, StandardCharsets.UTF_8);
                    if (!list.isEmpty() && hashedPassword.equals(list.get(1))) {
                        body = list.get(0);
                    } else {
                        body = "It does not match";
                    }
                    break;
                }

                case "updateUser": {
                    //param1=user_id, param2=user_name, param3=user_email, param4=user_password,param5=user_height
                    //param6=user_weight
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(param4.getBytes(StandardCharsets.UTF_8));
                    String hashedPassword = new String(hash, StandardCharsets.UTF_8);
                    db.updateUser(conn, param1, param2, param3, hashedPassword, param5, param6);
                    body = "{}";
                    break;
                }
                case "addFoodConsumed": {
                    ////param1=userID, param2=date, param3=name, param4=energy, param5=protein, param6=fat, param7=carbohydrates
                    db.addFoodConsumed(conn, param1, param2, param3, param4, param5, param6, param7);
                    break;
                }

                case "addSearchedFood": {
                    //param1=searched, param2=userID
                    db.addSearchedFood(conn, param2, param1);
                    break;
                }
                case "addScannedFood": {
                    //param1=image_path, param2=user_id
                    db.addScannedFood(conn, param1, param2);
                    body = "{}";
                    break;
                }

                default:
                    assert false;
            }
            System.out.println(body);
            return body;
        }

        private String makeHeaderAnswer() {
            String answer = "";
            answer += "HTTP/1.0 200 OK\r\n";
            answer += "Content-type: application/json\r\n";
            answer += "Access-Control-Allow-Origin: *\r\n";
            answer += "\r\n";
            return answer;
        }
    } // SocketThread

} // WebServer