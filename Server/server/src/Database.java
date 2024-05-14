import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private final String url = "jdbc:postgresql://localhost/FoodFeed";
    private final String user = "admin";
    private final String password = "admin";


    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // Getting the food consumed by a user.
    public ArrayList<String[]> getFoodConsumed(Connection conn, String UserID) throws SQLException {
        // Initializing variables
        ArrayList<String[]> list = new ArrayList<String[]>();
        String meal_id = null;

        // First query to get meal_id
        String query = "SELECT * FROM meals WHERE user_id=" + UserID;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            // Second query to get the food consumed
            meal_id = rs.getString("meal_id");
            String meal_date = rs.getString("meal_date");
            query = "SELECT * FROM food WHERE meal_id=" + meal_id;
            stmt = conn.createStatement();
            ResultSet rs2 = stmt.executeQuery(query);
            while (rs2.next()) {
                String food_id = rs2.getString("food_id");
                meal_id = rs2.getString("meal_id");
                String name = rs2.getString("name");
                String energy = rs2.getString("energy");
                String protein = rs2.getString("protein");
                String fat = rs2.getString("fat");
                String carbohydrates = rs2.getString("carbohydrates");
                String[] food = {food_id, meal_id, name, energy, protein, fat, carbohydrates, meal_date};
                list.add(food);
            }
        }

        return list;
    }

    // Getting all the information from a user
    public ArrayList<String> getUserID(Connection conn, String UserID) throws SQLException {
        // Initializing variables
        ArrayList<String> list = new ArrayList<String>();

        // Query to get users
        String query = "SELECT * FROM users WHERE user_id=" + UserID;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            String nom = rs.getString("user_name");
            String mail = rs.getString("user_email");
            String height = rs.getString("user_height");
            String weight = rs.getString("user_weight");
            list.add(nom);
            list.add(mail);
            list.add(height);
            list.add(weight);
        }
        return list;
    }

    // Getting the food searched by a user
    public ArrayList<String[]> getSearchedFood(Connection conn, String UserID) throws SQLException {
        // Initializing variables
        ArrayList<String[]> list = new ArrayList<String[]>();

        // Query to get user search_bar
        String query = "SELECT * FROM search_bar WHERE user_id=" + UserID;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            String search_bar_id = rs.getString("search_bar_id");
            String searched = rs.getString("searched");
            String user_id = rs.getString("user_id");
            String [] result = {search_bar_id, searched, user_id};
            list.add(result);
        }
        return list;
    }

    // Getting the food scanned by a user
    public ArrayList<String[]> getScannedFood(Connection conn, String UserID) throws SQLException {
        // Initializing variables
        ArrayList<String[]> list = new ArrayList<String[]>();

        // Query to get users images
        String query = "SELECT * FROM images WHERE user_id=" + UserID;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            String imageID = rs.getString("image_id");
            String imageName = rs.getString("image_path");
            String userID = rs.getString("user_id");
            String [] result = {imageID, imageName, userID};
            list.add(result);
        }
        return list;
    }

    // Deleting the food consumed by a user
    public void deleteFoodConsumed(Connection conn, String foodID) throws SQLException {
        String query = "DELETE FROM food WHERE food_id=" + foodID;
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(query);
    }

    // Adding a searched food by a user
    public void addSearchedFood(Connection conn, String userID, String searched) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("INSERT INTO search_bar (searched, user_id) VALUES (?,?)");
        prepStmt.setString(1, searched);
        prepStmt.setInt(2, Integer.parseInt(userID));
        prepStmt.executeUpdate();
    }

    //Adding food scanned by a user
    public void addScannedFood(Connection conn, String imagePath, String userID) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("INSERT INTO images ( image_path, user_id) VALUES (?,?)");
        prepStmt.setString(1, imagePath);
        prepStmt.setInt(2, Integer.parseInt(userID));
        prepStmt.executeUpdate();
    }

    // Updating user information
    public void updateUser(Connection conn, String userID, String userName, String userEmail, String userPassword,
                           String userHeight, String userWeight) throws SQLException {

        PreparedStatement prepStmt = conn.prepareStatement("UPDATE users SET user_name=?, user_email=?, " +
                "user_password=?, user_height=?, user_weight=? WHERE user_id=?");
        prepStmt.setString(1, userName);
        prepStmt.setString(2, userEmail);
        prepStmt.setString(3, userPassword);
        prepStmt.setInt(4, Integer.parseInt(userHeight));
        prepStmt.setInt(5, Integer.parseInt(userWeight));
        prepStmt.setInt(6, Integer.parseInt(userID));

        prepStmt.executeUpdate();

    }

    // Getting the necessary information to verify a login
    public ArrayList<String> login(Connection conn, String userEmail) throws SQLException{
        // Initializing variables
        ArrayList<String> list = new ArrayList<String>();

        String query = "SELECT * FROM users WHERE user_email= '" + userEmail + "'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            String userID = rs.getString("user_id");
            String userPassword = rs.getString("user_password");
            list.add(userID);
            list.add(userPassword);
        }
        return list;

    }

    // Register a new user
    public void register(Connection conn, String userName, String userEmail, String userPassword,
                           String userHeight, String userWeight) throws SQLException {

        PreparedStatement prepStmt = conn.prepareStatement("INSERT INTO users (user_name, user_email, user_password, user_height, user_weight) VALUES (?, ?, ?, ?, ?)");
        prepStmt.setString(1, userName);
        prepStmt.setString(2, userEmail);
        prepStmt.setString(3, userPassword);
        prepStmt.setInt(4, Integer.parseInt(userHeight));
        prepStmt.setInt(5, Integer.parseInt(userWeight));
        prepStmt.executeUpdate();
    }

    // Adding food consumed by a user
    public void addFoodConsumed(Connection conn, String userID, String date, String name, String energy, String protein, String fat, String chydrates) throws SQLException, ParseException {
        // Initializing variables
        String meal_id = null;

        // Searching if the user have a meal created for that day
        String query = "SELECT * FROM meals WHERE user_id= ? AND meal_date = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, Integer.parseInt(userID));
        stmt.setDate(2, java.sql.Date.valueOf(date));
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            meal_id = rs.getString("meal_id");
        }

        // Adding the food information into the searched meal
        if(meal_id != null){
            PreparedStatement prepStmt  = conn.prepareStatement("INSERT INTO food (meal_id, name, energy, protein, fat, carbohydrates) VALUES (?, ?, ?, ?, ?, ?)");
            prepStmt.setInt(1, Integer.parseInt(meal_id));
            prepStmt.setString(2, name);
            prepStmt.setFloat(3, Float.parseFloat(energy));
            prepStmt.setFloat(4, Float.parseFloat(protein));
            prepStmt.setFloat(5, Float.parseFloat(fat));
            prepStmt.setFloat(6, Float.parseFloat(chydrates));
            prepStmt.executeUpdate();

        }else{
            // Creating a meal in order to add the food there
            PreparedStatement prepStmt  = conn.prepareStatement("INSERT INTO meals (user_id, meal_date) VALUES (?,?)");
            prepStmt.setInt(1, Integer.parseInt(userID));
            prepStmt.setDate(2, java.sql.Date.valueOf(date));
            prepStmt.executeUpdate();

            String meal_id2 = null;
            String query2 = "SELECT * FROM meals WHERE user_id= ? AND meal_date = ?";
            PreparedStatement stmt2 = conn.prepareStatement(query2);
            stmt2.setInt(1, Integer.parseInt(userID));
            stmt2.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs2 = stmt2.executeQuery();
            while (rs2.next()) {
                meal_id2 = rs2.getString("meal_id");
            }

            // Adding the food information into the created meal
            prepStmt  = conn.prepareStatement("INSERT INTO food (meal_id, name, energy, protein, fat, carbohydrates) VALUES (?, ?, ?, ?, ?, ?)");
            prepStmt.setInt(1, Integer.parseInt(meal_id2));
            prepStmt.setString(2, name);
            prepStmt.setFloat(3, Float.parseFloat(energy));
            prepStmt.setFloat(4, Float.parseFloat(protein));
            prepStmt.setFloat(5, Float.parseFloat(fat));
            prepStmt.setFloat(6, Float.parseFloat(chydrates));
            prepStmt.executeUpdate();
        }
    }
}
