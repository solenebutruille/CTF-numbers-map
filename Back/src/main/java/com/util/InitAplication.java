package com.util;

import com.features.Invoices;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.*;
import java.sql.*;

public class InitAplication {

    //Create and interact with the database
    public static void main(String[] args) {
        initApplication();
    }

    public static void initApplication(){
        try {

            Invoices invoices = new Invoices();
            FileWriter writer;

            String xxeFlag = System.getenv("XXE_FLAG");
            if (xxeFlag == null){
                xxeFlag = "FLAG{There_is_this_XXE_2023}";
            }

            writer = new FileWriter("flag.flg");
            writer.write(xxeFlag);
            writer.close();

            String osCommandInjectionFlag = System.getenv("OSCI_FLAG");
            if (osCommandInjectionFlag == null){
                osCommandInjectionFlag = "FLAG{OS_COMMAND_INJECTION_DANGER}";
            }

            File file = new File("src/main/resources/flag/flag.flg");
            file.getParentFile().mkdirs();

            writer = new FileWriter("./src/main/resources/flag/flag.flg");
            writer.write(osCommandInjectionFlag);
            writer.close();

            // Load the H2 JDBC driver
            Class.forName("org.h2.Driver");

            // Create a connection to the H2 database
            Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");

            // Create a statement for executing SQL statements
            Statement statement = connection.createStatement();

            statement.executeUpdate("DROP USER IF EXISTS READ_ONLY_USER");
            statement.executeUpdate("DROP USER IF EXISTS INSERT_MARKER_USER");
            statement.executeUpdate("DROP USER IF EXISTS DELETE_SESSION_COOKIES");
            statement.executeUpdate("DROP USER IF EXISTS INSERT_SESSION_COOKIES");

            statement.executeUpdate("DROP TABLE IF EXISTS MARKERS");
            statement.executeUpdate("DROP TABLE IF EXISTS USERS");
            statement.executeUpdate("DROP TABLE IF EXISTS SESSION_COOKIES");

            String sql = "CREATE TABLE SESSION_COOKIES (id INT AUTO_INCREMENT PRIMARY KEY, session_id VARCHAR(255) NOT NULL, user_id INT NOT NULL, expiration TIMESTAMP NOT NULL )";
            statement.executeUpdate(sql);

            String loginFlag = System.getenv("LOGIN_FLAG");
            if (loginFlag == null){
                loginFlag = "FLAG{LOG_IN_IS_BROKEN}";
            }
            // Execute a SQL statement to create a table
            statement.execute("CREATE TABLE USERS (ID INT PRIMARY KEY, USERNAME VARCHAR(255), PASSWORD VARCHAR(255))");
            statement.execute("INSERT INTO USERS VALUES (1, '" + loginFlag  + "', 'hebhrwh7257gr')");
            statement.execute("INSERT INTO USERS VALUES (2, 'JaneDoe', 'tgretjk725fd')");
            statement.execute("INSERT INTO USERS VALUES (3, 'admin', 'dfsdsdjhj2582hb')");
            statement.execute("INSERT INTO USERS VALUES (4, 'userAccount', '123456')");

            // Execute a SQL statement to create a table
            statement.execute("CREATE TABLE MARKERS (ID INT PRIMARY KEY, LATITUDE FLOAT, LONGITUDE FLOAT, MESSAGE VARCHAR(255), BUYER INT DEFAULT 1 NOT NULL)");
            statement.execute("INSERT INTO MARKERS VALUES (1, 5.73831, 45.19215, 'This is Ethiopia', 1)");
            statement.execute("INSERT INTO MARKERS VALUES (2, 74.0060, 40.7128, 'This is a random place', 3)");
            statement.execute("INSERT INTO MARKERS VALUES (3, 28.521880369291182, 67.69006067663784, 'My friend leaves here !', 2)");
            statement.execute("INSERT INTO MARKERS VALUES (4, 40.70784011495947, -74.03097246235315, 'This is New York', 3)");
            statement.execute("INSERT INTO MARKERS VALUES (5, 51.51646030932025, -0.08764523518303236, 'The London office is great !', 3)");
            statement.execute("INSERT INTO MARKERS VALUES (6, -22.845463843132507, -43.18617181056439, 'The carnival here is amazing', 3)");
            statement.execute("INSERT INTO MARKERS VALUES (7, 5.117745980421077, -154.4864223601217, 'Who put me here ?', 3)");
            statement.execute("INSERT INTO MARKERS VALUES (8, -15.019274825906322, -64.52375568786758, 'What is the favorite food here ?', 3)");
            statement.execute("INSERT INTO MARKERS VALUES (9, -74.03097246235315, 40.70784011495947, 'Did you find the flag.flg ?', 3)");
            statement.execute("INSERT INTO MARKERS VALUES (10, 78, -10, 'I love this landscape', 3)");

            String indirectObjectReferenceFlag = System.getenv("IOR_FLAG");
            if (indirectObjectReferenceFlag == null){
                indirectObjectReferenceFlag = "FLAG{U_found_br0k3n_authentication}";
            }

            invoices.createInvoice(1, "1");
            invoices.createInvoice(2, "3");
            invoices.createInvoice(3, "2");
            invoices.createInvoice(4, "3");
            invoices.createInvoice(5, "3");
            invoices.createInvoice(6, "3");
            invoices.createInvoice(7, indirectObjectReferenceFlag);
            invoices.createInvoice(8, "3");
            invoices.createInvoice(9, "3");
            invoices.createInvoice(10, "3");

            statement.executeUpdate("CREATE USER  IF NOT EXISTS READ_ONLY_USER PASSWORD '123ghfjdfhj7382tvFTYF66'");
            statement.executeUpdate("GRANT SELECT ON SCHEMA PUBLIC TO READ_ONLY_USER");

            statement.executeUpdate("CREATE USER IF NOT EXISTS INSERT_MARKER_USER PASSWORD 'fhjd837bcvGG3'");
            statement.executeUpdate("GRANT INSERT ON MARKERS TO INSERT_MARKER_USER");

            statement.executeUpdate("CREATE USER IF NOT EXISTS DELETE_SESSION_COOKIES PASSWORD '12jkbGSFTS9043y7'");
            statement.executeUpdate("GRANT DELETE ON SESSION_COOKIES TO DELETE_SESSION_COOKIES");

            statement.executeUpdate("CREATE USER IF NOT EXISTS INSERT_SESSION_COOKIES PASSWORD 'dlcpscbdshj89230328tevdghe'");
            statement.executeUpdate("GRANT INSERT ON SESSION_COOKIES TO INSERT_SESSION_COOKIES");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}