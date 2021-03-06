package requestEngine;

import gui.HomeScreen;
import gui.MainFrame;
import java.util.ArrayList;
import networking.Request;
import networking.Responce;
import objects.Candidate;
import objects.Voter;
import org.jdom2.Document;
import org.jdom2.Element;
import sqlEngine.SQLEngine;

/**
 *
 * @author alexisvincent
 */
public class RequestEngine {

    SQLEngine sqlEngine;

    public RequestEngine(SQLEngine sqlEngine) {
        this.sqlEngine = sqlEngine;
    }

    public void postRequest(Request request) {

        System.out.println("Request Recieved");
        Document document = request.getXmlDocument();
        Element rootElement = document.getRootElement();

        Responce responce;
        Element responceElement = new Element("Responce");
        responceElement.setAttribute("ResponceCode", "400");
        responceElement.setAttribute("ResponceDiscription", "Invalid Request");

        if (request.getRequestType().equals("Login") && request.getFrom().equals("RegistrationApp")) {

            responceElement = new Element("Responce");
            responceElement.setAttribute("ResponceCode", "200");

            if (rootElement.getAttributeValue("Password").equals(MainFrame.getHomeScreen().getPassword())) {
                responceElement.setAttribute("ResponceCode", "200");
                responceElement.setAttribute("ResponceDiscription", "OK");
            } else {
                responceElement.setAttribute("ResponceCode", "400");
                responceElement.setAttribute("ResponceDiscription", "Bad Password");
            }

        } else if (request.getRequestType().equals("VotersKey") && request.getFrom().equals("RegistrationApp")) {

            String idNumber = rootElement.getAttributeValue("idNumber");
            String firstName = rootElement.getAttributeValue("firstName");
            String votersKey = sqlEngine.getVotersKey(idNumber, firstName);

            responceElement = new Element("Responce");
            responceElement.setAttribute("VotersKey", votersKey);

            if (votersKey.equals("")) {
                responceElement.setAttribute("ResponceCode", "400");
                responceElement.setAttribute("ResponceDiscription", "No such details");
            } else {
                responceElement.setAttribute("ResponceCode", "200");
                responceElement.setAttribute("ResponceDiscription", "OK");
            }

        } else if (request.getRequestType().equals("Candidates")) {

            ArrayList<Candidate> candidates = sqlEngine.getCandidates();

            responceElement = new Element("Responce");

            int counter = 1;
            for (Candidate candidate : candidates) {
                Element candidateElement = new Element("Candidate" + counter++);
                candidateElement.setAttribute("ID", candidate.getId());
                candidateElement.setAttribute("Name", candidate.getName());
                candidateElement.setAttribute("Info", candidate.getInfo());
                candidateElement.setAttribute("Image", candidate.getEncodedImage());
                responceElement.addContent(candidateElement);
            }

            responceElement.setAttribute("ResponceCode", "200");
            responceElement.setAttribute("ResponceDiscription", "OK");
            responceElement.setAttribute("CandidatesCount", candidates.size() + "");

        } else if (request.getRequestType().equals("Vote") && request.getFrom().equals("VotingApp")) {

            String voterkey = rootElement.getAttributeValue("VoterKey");
            String candidateID = rootElement.getAttributeValue("CandidateID");
            String status = sqlEngine.vote(voterkey, candidateID);

            if (status.equals("Successfull")) {
                responceElement.setAttribute("ResponceCode", "200");
                responceElement.setAttribute("ResponceDiscription", "OK");
            } else if (status.equals("AlreadyVoted")) {
                responceElement.setAttribute("ResponceCode", "300");
                responceElement.setAttribute("ResponceDiscription", "Already Voted");
            } else if (status.equals("IncorrectKey")) {
                responceElement.setAttribute("ResponceCode", "310");
                responceElement.setAttribute("ResponceDiscription", "Incorrect Key");
            } else {
                responceElement.setAttribute("ResponceCode", "400");
                responceElement.setAttribute("ResponceDiscription", "Failed");
            }

        } else if (request.getRequestType().equals("Stats") && request.getFrom().equals("AdminApp")) {

            ArrayList<Candidate> candidates = sqlEngine.getCandidateStats();

            responceElement = new Element("Responce");

            int counter = 1;
            for (Candidate candidate : candidates) {
                Element candidateElement = new Element("Candidate" + counter++);
                candidateElement.setAttribute("ID", candidate.getId());
                candidateElement.setAttribute("Name", candidate.getName());
                candidateElement.setAttribute("Info", candidate.getInfo());
                candidateElement.setAttribute("Image", candidate.getEncodedImage());
                candidateElement.setAttribute("Tally", "" + candidate.getTally());
                candidateElement.setAttribute("Percentage", "" + candidate.getPercentage());
                responceElement.addContent(candidateElement);
            }

            responceElement.setAttribute("ResponceCode", "200");
            responceElement.setAttribute("ResponceDiscription", "OK");
            responceElement.setAttribute("CandidatesCount", candidates.size() + "");

        } else if (request.getRequestType().equals("RemoveCandidate") && request.getFrom().equals("AdminApp")) {

            boolean success = sqlEngine.removeCandidate(request.getRootElement().getAttributeValue("CandidateID"));

            responceElement = new Element("Responce");

            if (success) {
                responceElement.setAttribute("ResponceCode", "200");
                responceElement.setAttribute("ResponceDiscription", "OK");
            } else {
                responceElement.setAttribute("ResponceCode", "400");
                responceElement.setAttribute("ResponceDiscription", "Failed");
            }

        } else if (request.getRequestType().equals("AddCandidate") && request.getFrom().equals("AdminApp")) {

            Candidate candidate = new Candidate();
            candidate.setName(request.getRootElement().getAttributeValue("CandidateName"));
            candidate.setInfo(request.getRootElement().getAttributeValue("CandidateInfo"));
            candidate.setEncodedImage(request.getRootElement().getAttributeValue("CandidateImage"));

            responceElement = new Element("Responce");

            if (!candidate.getName().equals("") && !candidate.getInfo().equals("")) {
                sqlEngine.addCandidate(candidate);
                responceElement.setAttribute("ResponceCode", "200");
                responceElement.setAttribute("ResponceDiscription", "OK");
            }

            responceElement.setAttribute("ResponceCode", "400");
            responceElement.setAttribute("ResponceDiscription", "Failed");

        } else if (request.getRequestType().equals("UpdateCandidate") && request.getFrom().equals("AdminApp")) {

            Candidate candidate = new Candidate();
            candidate.setName(request.getRootElement().getAttributeValue("CandidateName"));
            candidate.setInfo(request.getRootElement().getAttributeValue("CandidateInfo"));
            candidate.setId(request.getRootElement().getAttributeValue("CandidateID"));
            candidate.setEncodedImage(request.getRootElement().getAttributeValue("CandidateImage"));

            responceElement = new Element("Responce");

            if (!candidate.getName().equals("") && !candidate.getInfo().equals("")) {
                sqlEngine.updateCandidate(candidate);
                responceElement.setAttribute("ResponceCode", "200");
                responceElement.setAttribute("ResponceDiscription", "OK");
            }

            responceElement.setAttribute("ResponceCode", "400");
            responceElement.setAttribute("ResponceDiscription", "Failed");

        } else if (request.getRequestType().equals("RemoveVoter") && request.getFrom().equals("AdminApp")) {

            System.out.println("removing voter");
            
            boolean success = sqlEngine.removeVoter(request.getRootElement().getAttributeValue("VoterID"));

            responceElement = new Element("Responce");

            if (success) {
                responceElement.setAttribute("ResponceCode", "200");
                responceElement.setAttribute("ResponceDiscription", "OK");
            }

        } else if (request.getRequestType().equals("AddVoter") && request.getFrom().equals("AdminApp")) {

            Voter voter = new Voter();
            voter.setFirstName(request.getRootElement().getAttributeValue("FirstName"));
            voter.setMiddleNames(request.getRootElement().getAttributeValue("MiddleNames"));
            voter.setIdNumber(request.getRootElement().getAttributeValue("IDNumber"));
            voter.setSurname(request.getRootElement().getAttributeValue("LastName"));

            responceElement = new Element("Responce");

            sqlEngine.addVoter(voter);
            responceElement.setAttribute("ResponceCode", "200");
            responceElement.setAttribute("ResponceDiscription", "OK");

        } else if (request.getRequestType().equals("UpdateVoter") && request.getFrom().equals("AdminApp")) {

            Voter voter = new Voter();
            voter.setFirstName(request.getRootElement().getAttributeValue("FirstName"));
            voter.setMiddleNames(request.getRootElement().getAttributeValue("MiddleNames"));
            voter.setIdNumber(request.getRootElement().getAttributeValue("IDNumber"));
            voter.setSurname(request.getRootElement().getAttributeValue("LastName"));
            voter.setVotersID(request.getRootElement().getAttributeValue("VotersID"));

            responceElement = new Element("Responce");

            sqlEngine.updateVoter(voter);
            responceElement.setAttribute("ResponceCode", "200");
            responceElement.setAttribute("ResponceDiscription", "OK");

        } else if (request.getRequestType().equals("Voters")) {

            ArrayList<Voter> voters = sqlEngine.getVoters();

            responceElement = new Element("Responce");

            int counter = 1;
            for (Voter voter : voters) {
                Element voterElement = new Element("Voter" + counter++);
                voterElement.setAttribute("FirstName", voter.getFirstName());
                voterElement.setAttribute("MiddleNames", voter.getMiddleNames());
                voterElement.setAttribute("LastName", voter.getSurname());
                voterElement.setAttribute("IDNumber", voter.getIdNumber());
                voterElement.setAttribute("VoterID", voter.getVotersID());
                voterElement.setAttribute("EncryptionKey", voter.getEncryptionKey());
                voterElement.setAttribute("AddressLine1", voter.getEncryptionKey());
                voterElement.setAttribute("AddressLine2", voter.getEncryptionKey());
                voterElement.setAttribute("City", voter.getEncryptionKey());
                voterElement.setAttribute("Suburb", voter.getEncryptionKey());
                voterElement.setAttribute("Province", voter.getEncryptionKey());
                

                responceElement.addContent(voterElement);
            }
            
            

            responceElement.setAttribute("ResponceCode", "200");
            responceElement.setAttribute("ResponceDiscription", "OK");
            responceElement.setAttribute("VotersCount", voters.size() + "");

        } else if (request.getRequestType().equals("ResetElection") && request.getFrom().equals("AdminApp")) {

            sqlEngine.resetElection();

            responceElement = new Element("Responce");
            responceElement.setAttribute("ResponceCode", "200");
            responceElement.setAttribute("ResponceDiscription", "OK");

        }

        responce = new Responce(new Document(responceElement), request.getSocket());
        request.getSocket().postResponce(responce);
    }
}
