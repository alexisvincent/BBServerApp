package sqlEngine;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import networking.Request;
import objects.Address;
import objects.Voter;
import org.jdom2.Element;

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
                populateDatabase();
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SQLEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getVotersKey(Request request) {
        String votersKey = "";
        String idNumber = "";
        String firstName = "";

        Element rootElement = request.getRootElement();
        idNumber = rootElement.getAttributeValue("idNumber");
        firstName = rootElement.getAttributeValue("firstName");

        if (!idNumber.equals("") && !firstName.equals("")) {
            String sql = "SELECT * FROM tblVoterRegistry WHERE firstName='" + firstName + "' AND idNumber='" + idNumber + "'";

            ResultSet resultSet = executeQuery(sql);
            try {
                if (resultSet.first()) {
                    votersKey = resultSet.getString("votersID") + resultSet.getString("encryptionKey");
                }
            } catch (SQLException ex) {
                Logger.getLogger(SQLEngine.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return votersKey;
    }

    private ResultSet executeQuery(String sql) {
        ResultSet resultSet = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, 
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(SQLEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultSet;
    }
    
    private boolean executeUpdate(String sql) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(SQLEngine.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
    }

    private void createDatebase(String databaseName, String username, String password) {

        connect(databaseName, username, password);

        String config1 = "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.propertiesOnly', 'TRUE')";
        String config2 = "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.user." + username + "', '" + password + "')";
        String config3 = "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.connection.requireAuthentication', 'TRUE')";

        String tblVotersRegistry = "CREATE TABLE tblVoterRegistry "
                + " (idNumber VARCHAR(13), "
                + " firstName VARCHAR(30), "
                + " middleNames VARCHAR(30), "
                + " lastName VARCHAR(30), "
                + " addressLine1 VARCHAR(50), "
                + " addressLine2 VARCHAR(50), "
                + " suburb VARCHAR(30), "
                + " city VARCHAR(30), "
                + " province VARCHAR(50), "
                + " votersID VARCHAR(32), "
                + " encryptionKey VARCHAR(32), "
                + " PRIMARY KEY ( idNumber ))";

        String tblVotes = "CREATE TABLE tblVotes "
                + "(votersID VARCHAR(32), "
                + " encryptionKey VARCHAR(32), "
                + " partyID VARCHAR(15), "
                + " timestamp VARCHAR(15), "
                + " status VARCHAR(15), "
                + " PRIMARY KEY ( votersID ))";

        String tblCandidates = "CREATE TABLE tblCandidates "
                + "(candidateID VARCHAR(15), "
                + " name VARCHAR(30), "
                + " bio VARCHAR(120), "
                + " emblem VARCHAR(255), "
                + " PRIMARY KEY ( candidateID ))";

        try {

            Statement stmt = connection.createStatement();

            stmt.execute(config1);
            stmt.execute(config2);
            stmt.execute(config3);

            stmt.executeUpdate(tblVotersRegistry);
            stmt.executeUpdate(tblVotes);
            stmt.executeUpdate(tblCandidates);

            stmt.close();

            System.out.println("Successfully created new database: " + databaseName);

        } catch (SQLException ex) {
            System.out.println("Database creation was unsuccessfull :(");
            Logger.getLogger(SQLEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void populateDatabase() {
        Voter voter = new Voter();
        
        voter.setFirstName("Alexis");
        voter.setMiddleNames("John");
        voter.setSurname("Vincent");
        voter.setIdNumber("1234567891234");
        voter.setAddress(new Address("51 Kirkia Street", "", "Heldervue", "Somerset West", "Western Cape"));
        addVoter(voter);
    }
    
    public void addVoter(Voter voter) {
        if (voter.getVotersID().equals("")) {
            voter.setVotersID("123456789");
        } else if (voter.getEncryptionKey().equals("123456789")) {
            
        }
        String sql = "INSERT INTO tblVoterRegistry VALUES('"
                + voter.getIdNumber()+"','"
                + voter.getFirstName()+"','"
                + voter.getMiddleNames()+"','"
                + voter.getSurname()+"','"
                + voter.getAddress().getAddressLine1()+"','"
                + voter.getAddress().getAddressLine2()+"','"
                + voter.getAddress().getSuburb()+"','"
                + voter.getAddress().getCity()+"','"
                + voter.getAddress().getProvince()+"','"
                + voter.getVotersID()+"','"
                + voter.getEncryptionKey()+"')";
        
        executeUpdate(sql);
        
    }

    private void connect(String databaseName, String username, String password) {
        Properties databaseProperties = new Properties();
        databaseProperties.setProperty("databaseName", databaseName);
        databaseProperties.setProperty("user", username);
        databaseProperties.setProperty("password", password);
        databaseProperties.setProperty("create", "true");

        connect(databaseProperties);
    }

    private void connect(Properties databaseProperties) {
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
