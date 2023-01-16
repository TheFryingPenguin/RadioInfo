import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ChannelCollector {
    ArrayList<Channel> channels;
    ChannelCollector(){
        this.channels = new ArrayList<>();
    }

    public void getChannels(){
        //connect to api
        try {
            for (int pageNr = 1; pageNr<=6;pageNr++) {
                URL url = new URL("http://api.sr.se/v2/channels/?page="+pageNr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                //Check for response code
                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    throw new RuntimeException("HTTP responsecode: " + responseCode);
                } else {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = factory.newDocumentBuilder();
                    Document document = docBuilder.parse(url.openStream());
                    document.getDocumentElement().normalize();


                    NodeList listCh = document.getElementsByTagName("channel");
                    for (int i = 0; i < listCh.getLength(); i++) {
                        Node chNode = listCh.item(i);
                        if (chNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element chElement = (Element) chNode;
                            String chId = chElement.getAttribute("id");
                            String chName = chElement.getAttribute("name");
                            this.channels.add(new Channel(chId,chName, pageNr));
                        }
                    }
                }
           }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }



    public ArrayList<Channel> getChannelList(){
        return channels;
    }
}
