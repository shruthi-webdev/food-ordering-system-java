import java.sql.Connection;
import java.sql.SQLException;

import com.example.foodorder.util.DatabaseUtil;

public class TestDB {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseUtil.getConnection();
            if (conn != null) {
                System.out.println("Successfully connected to the database!");
                conn.close();
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
