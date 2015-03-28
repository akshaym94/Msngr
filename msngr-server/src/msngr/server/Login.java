/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package msngr.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Akshay Mahajan
 */
public class Login {
    protected static boolean login(String username, String password){
        String pass;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/msngr", "root", "password");
            Statement s = con.createStatement();
            String loginQuery = "SELECT pass from login WHERE user='"+username+"';";
            ResultSet rs = s.executeQuery(loginQuery);
            rs.next();
            pass = rs.getString("pass");
            rs.close();
            s.close();
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return password.equals(pass);
    }
}
