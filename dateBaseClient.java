package hr.vsite.hr;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;

public class dateBaseClient {

    static String url="jdbc:mysql://localhost:3306/ChatClient?autoReconnect=true&useSSL=false";
    static String user="root";
    static String pass="mysql";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    public static void insertMessage(String user, String polje) {

        String SQL = "INSERT INTO chatporuke(userName,poruka) VALUES(?,?)";

        try {

            PreparedStatement pstmt = connect().prepareStatement(SQL);
            pstmt.setString(1, user);
            pstmt.setString(2, polje);
            pstmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static String showPoruke() {
        Statement stmt=null;

        String queryCommand = "SELECT userName,poruka FROM chatporuke";
        String rezultat="";
        try {
            stmt = connect().createStatement();
            ResultSet rs = stmt.executeQuery(queryCommand);

            while (rs.next()) {
                String coffeeName = rs.getString(1);
                int supplierID = rs.getInt(2);
                float price = rs.getFloat(3);
                int sales = rs.getInt(4);
                int total = rs.getInt("TOTAL");
                System.out.println(coffeeName + "\t" + supplierID + "\t" + price + "\t" + sales + "\t" + total);

                rezultat+=rs.getString(1)+": "+rs.getString(2)+"\n";
            }

        } catch (SQLException ex) {
            ex.printStackTrace();

            ex.printStackTrace();

        }

        return rezultat;
    }
}

