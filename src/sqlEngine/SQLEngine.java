package sqlEngine;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alexisvincent
 */
public class SQLEngine {

    private Connection connection;
    private String databaseName = "ServerDatabase";

    public SQLEngine() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

            if (new File(databaseName).isDirectory()) {
                connect(databaseName, "username", "password");
                System.out.println("Connected to " + databaseName);
            } else {
                createDatebase(databaseName, "username", "password");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SQLEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void createDatebase(String databaseName, String username, String password) {
        
        connect(databaseName, username, password);

        String config1 = "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.propertiesOnly', 'TRUE')";
        String config2 = "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.user." + username + "', '" + password + "')";
        String config3 = "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.connection.requireAuthentication', 'TRUE')";

        String tblNationalRegistry = "CREATE TABLE tblNationalRegistry "
                + "(nrID INTEGER, "
                + " idNumber VARCHAR(13), "
                + " firstName VARCHAR(30), "
                + " lastName VARCHAR(30), "
                + " address VARCHAR(50), "
                + " PRIMARY KEY ( nrID ))";
        
        String tblVotersLink = "CREATE TABLE tblVotersLink "
                + "(nrID INTEGER, "
                + " votersID VARCHAR(32), "
                + " PRIMARY KEY ( nrID ))";
        
        String tblBallotDatabase1 = "CREATE TABLE tblBallotDatabase1 "
                + "(votersID VARCHAR(32), "
                + " encryptionKey1 VARCHAR(32), "
                + " encryptionKey2 VARCHAR(32), "
                + " decryptedVote VARCHAR(15), "
                + " status BOOLEAN, "
                + " PRIMARY KEY ( votersID ))";
        
        String tblBallotDatabase2 = "CREATE TABLE tblBallotDatabase2 "
                + "(votersID VARCHAR(32), "
                + " encryptionKey1 VARCHAR(32), "
                + " encryptionKey2 VARCHAR(32), "
                + " decryptedVote VARCHAR(15), "
                + " status BOOLEAN, "
                + " PRIMARY KEY ( votersID ))";
        
        String tblCandidates = "CREATE TABLE tblCandidates "
                + "(candidateID VARCHAR(15), "
                + " name VARCHAR(30), "
                + " bio VARCHAR(120), "
                + " picture VARCHAR(255), "
                + " PRIMARY KEY ( candidateID ))";
        
        try {
            
            Statement stmt = connection.createStatement();
            
            stmt.execute(config1);
            stmt.execute(config2);
            stmt.execute(config3);
            
            stmt.executeUpdate(tblNationalRegistry);
            stmt.executeUpdate(tblVotersLink);
            stmt.executeUpdate(tblBallotDatabase1);
            stmt.executeUpdate(tblBallotDatabase2);
            stmt.executeUpdate(tblCandidates);
            
            stmt.close();
            
            System.out.println("Successfully created new database: " + databaseName);

        } catch (SQLException ex) {
            System.out.println("Database creation was unsuccessfull :(");
            Logger.getLogger(SQLEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void connect(String databaseName, String username, String password) {
        Properties databaseProperties = new Properties();
        databaseProperties.setProperty("databaseName", databaseName);
        databaseProperties.setProperty("user", username);
        databaseProperties.setProperty("password", password);
        databaseProperties.setProperty("create", "true");

        connect(databaseProperties);
    }

    public void connect(Properties databaseProperties) {
        try {
            connection = DriverManager.getConnection("jdbc:derby:", databaseProperties);
        } catch (SQLException ex) {
            System.out.println("Connection Failed :(");
            Logger.getLogger(SQLEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args[]) {
        new SQLEngine();
    }
}
