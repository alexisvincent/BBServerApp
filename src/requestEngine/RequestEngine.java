package requestEngine;

import networking.Request;
import networking.Responce;
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
            
            String votersKey = sqlEngine.getVotersKey(request);
            
            responceElement = new Element("Responce");
            responceElement.setAttribute("VotersKey", votersKey);
            
            if (votersKey.equals("")) {
                responceElement.setAttribute("ResponceCode", "400");
                responceElement.setAttribute("ResponceDiscription", "No such details");
            } else {
                responceElement.setAttribute("ResponceCode", "200");
                responceElement.setAttribute("ResponceDiscription", "OK");
            }
            
        }

        responce = new Responce(new Document(responceElement), request.getSocket());
        request.getSocket().postResponce(responce);
    }
}
