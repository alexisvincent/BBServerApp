package sqlEngine;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import objects.Address;
import objects.Candidate;
import objects.Voter;

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

    public String vote(String voterID, String candidateID) {
        String status = "";

        String sql = "SELECT votersID, candidateID FROM tblVotes WHERE votersID='" + voterID.substring(0, 13) + "'";

        ResultSet resultSet = executeQuery(sql);
        try {
            if (resultSet.next()) {
                if (resultSet.getString("candidateID") == null) {
                    sql = "UPDATE tblVotes SET candidateID='" + candidateID + "' WHERE votersID='" + voterID.substring(0, 13) + "'";
                    executeUpdate(sql);
                    status = "Successfull";
                } else {
                    status = "AlreadyVoted";
                }

            } else {
                status = "IncorrectKey";
            }
        } catch (SQLException ex) {
            Logger.getLogger(SQLEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

        return status;
    }

    public ArrayList<Candidate> getCandidates() {
        ArrayList<Candidate> candidates = new ArrayList<>();

        String sql = "SELECT id, name, info, image FROM tblCandidates";
        ResultSet resultSet = executeQuery(sql);
        try {
            while (resultSet.next()) {
                candidates.add(new Candidate(resultSet.getString("id"), resultSet.getString("name"), resultSet.getString("info"), null));
            }
        } catch (SQLException ex) {
            Logger.getLogger(SQLEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

        return candidates;
    }

    public ArrayList<Candidate> getCandidateStats() {
        ArrayList<Candidate> candidates = getCandidates();

        double tallyTotal = 0;
        for (Candidate candidate : candidates) {
            String sql = "SELECT * FROM tblVotes WHERE candidateID='" + candidate.getId() + "'";
            ResultSet rs = executeQuery(sql);

            int tally = 0;
            try {
                while (rs.next()) {
                    tally++;
                }
                tallyTotal += tally;
                candidate.setTally(tally);
            } catch (SQLException ex) {
                Logger.getLogger(SQLEngine.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        System.out.println(tallyTotal);

        for (Candidate candidate : candidates) {
            double percentage = 0;
            if (tallyTotal != 0) {
                percentage = candidate.getTally() / tallyTotal * 100;
            }
            candidate.setPercentage(percentage);
        }

        return candidates;
    }

    public String getVotersKey(String idNumber, String firstName) {
        String votersKey = "";

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
                + " votersID VARCHAR(13), "
                + " encryptionKey VARCHAR(32), "
                + " PRIMARY KEY ( idNumber ))";

        String tblVotes = "CREATE TABLE tblVotes "
                + "(votersID VARCHAR(13), "
                + " encryptionKey VARCHAR(32), "
                + " candidateID VARCHAR(15), "
                + " timestamp VARCHAR(15), "
                + " status VARCHAR(15), "
                + " PRIMARY KEY ( votersID ))";

        String tblCandidates = "CREATE TABLE tblCandidates "
                + "(id VARCHAR(5), "
                + " name VARCHAR(30), "
                + " info VARCHAR(120), "
                + " image VARCHAR(255), "
                + " PRIMARY KEY ( id ))";

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
        voter.setIdNumber("1111111111111");
        voter.setAddress(new Address("51 Kirkia Street", "", "Heldervue", "Somerset West", "Western Cape"));
        addVoter(voter);

        voter.setFirstName("Ben");
        voter.setIdNumber("2222222222222");
        addVoter(voter);
        
        voter.setFirstName("John");
        voter.setIdNumber("3333333333333");
        addVoter(voter);
        
        voter.setFirstName("Peter");
        voter.setIdNumber("4444444444444");
        addVoter(voter);
        
        voter.setFirstName("Lucy");
        voter.setIdNumber("5555555555555");
        addVoter(voter);
        
        voter.setFirstName("Julien");
        voter.setIdNumber("6666666666666");
        addVoter(voter);

        Candidate candidate = new Candidate();
        candidate.setName("African National Congress");
        candidate.setInfo("Haha these guys suck");
        addCandidate(candidate);

        candidate.setName("Democratic Alliance");
        candidate.setInfo("These are slightly better");
        addCandidate(candidate);

        candidate.setName("Alexis is the Best");
        candidate.setInfo("The name says it all");
        addCandidate(candidate);

        candidate.setName("Blah Blie Blue Bal");
        candidate.setInfo("The name says it all");
        addCandidate(candidate);

        candidate.setName("I want to go to the moon");
        candidate.setInfo("The name says it all");
        addCandidate(candidate);

        candidate.setName("Im bored");
        candidate.setInfo("The name says it all");
        addCandidate(candidate);

        candidate.setName("O.o");
        candidate.setInfo("The name says it all");
        addCandidate(candidate);

        candidate.setName("sdfgsdfgsdfg");
        candidate.setInfo("sdfgsdfg");
        addCandidate(candidate);

        candidate.setName("sfdgsdfg");
        candidate.setInfo("sdfgsdfgsdfg");
        addCandidate(candidate);
    }

    public void addCandidate(Candidate candidate) {
        
        boolean unique = true;
        while (unique) {
            candidate.setId(generateKey(5));
            
            String sql = "SELECT id FROM tblCandidates WHERE id='"+candidate.getId()+"'";
            ResultSet rs = executeQuery(sql);
            try {
                if(!rs.first()) {
                    unique = false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(SQLEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        String sql = "INSERT INTO tblCandidates VALUES('"
                + candidate.getId() + "','"
                + candidate.getName() + "','"
                + candidate.getInfo() + "','"
                + "IMAGECODE')";

        executeUpdate(sql);
    }

    public void addVoter(Voter voter) {
        
        boolean unique = true;
        while (unique) {
            voter.setVotersID(generateKey(13));
            
            String sql = "SELECT votersID FROM tblVoterRegistry WHERE votersID='"+voter.getVotersID()+"'";
            ResultSet rs = executeQuery(sql);
            try {
                if(!rs.first()) {
                    unique = false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(SQLEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        voter.setEncryptionKey(generateKey(13));

        String sql = "INSERT INTO tblVoterRegistry VALUES('"
                + voter.getIdNumber() + "','"
                + voter.getFirstName() + "','"
                + voter.getMiddleNames() + "','"
                + voter.getSurname() + "','"
                + voter.getAddress().getAddressLine1() + "','"
                + voter.getAddress().getAddressLine2() + "','"
                + voter.getAddress().getSuburb() + "','"
                + voter.getAddress().getCity() + "','"
                + voter.getAddress().getProvince() + "','"
                + voter.getVotersID() + "','"
                + voter.getEncryptionKey() + "')";

        executeUpdate(sql);

        sql = "INSERT INTO tblVotes(votersID) VALUES('"
                + voter.getVotersID() + "')";

        executeUpdate(sql);

    }

    private String generateKey(int size) {
        String key = "";

        for (int i = 0; i < size; i++) {
            char character = '*';
            while (!Character.isLetterOrDigit(character)) {
                character = (char) ((Math.random() * 97) + 42);
            }
            key += character;
        }

        System.out.println(key);
        return key;
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
