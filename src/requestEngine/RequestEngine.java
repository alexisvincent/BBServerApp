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

        responceElement.setAttribute("ResponceCode", "400");
        responceElement.setAttribute("ResponceDiscription", "Failed");

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

        } else if (request.getRequestType().equals("ExampleRequestType")) {
            
        }

        responce = new Responce(new Document(responceElement), request.getSocket());
        request.getSocket().postResponce(responce);
    }
}
