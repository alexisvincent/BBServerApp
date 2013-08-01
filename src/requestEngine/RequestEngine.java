package requestEngine;

import java.util.ArrayList;
import networking.Request;
import networking.Responce;
import objects.Candidate;
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

            if (rootElement.getAttributeValue("UNPassword").equals("test") && rootElement.getAttributeValue("GOVPassword").equals("test")) {
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
                candidateElement.setAttribute("id", candidate.getId());
                candidateElement.setAttribute("name", candidate.getName());
                candidateElement.setAttribute("info", candidate.getInfo());
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

        }

        responce = new Responce(new Document(responceElement), request.getSocket());
        request.getSocket().postResponce(responce);
    }
}
