import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class EpisodeCollector {

    private ArrayList<Episode> episodes;
    private ZonedDateTime EarliestEpTime;
    private ZonedDateTime LatestEpTime;
    private ZonedDateTime timeUpdated;
    private ZoneId zoneId;
    private boolean endSearch;

    EpisodeCollector(ZonedDateTime timeUpdated){
        this.timeUpdated = timeUpdated;
        this.EarliestEpTime = timeUpdated.minusHours(6);
        this.LatestEpTime = timeUpdated.plusHours(12);
        episodes = new ArrayList<>();
        this.endSearch = false;
        this.zoneId = timeUpdated.getZone();
    }

    public void scheduledEpisodes(String channalId){
        try {
            for (int pageNr = 1;pageNr<=7;pageNr++){
                if(endSearch == true){
                    break;
                }else{
                    URL url = new URL("http://api.sr.se/v2/scheduledepisodes?channelid="+channalId+"&date="+timeUpdated.toLocalDate()+"&page="+pageNr);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if(responseCode != 200){
                        throw new RuntimeException("HTTP responsecode: " + responseCode);
                    }else{
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder docBuilder = factory.newDocumentBuilder();
                        Document document = docBuilder.parse(url.openStream());
                        document.getDocumentElement().normalize();
                        ImageIcon image = null;
                        NodeList epList = document.getElementsByTagName("scheduledepisode");
                        for (int i = 0; i < epList.getLength(); i++) {
                            Node epNode = epList.item(i);
                            if (epNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element epElement = (Element) epNode;
                                ZonedDateTime epStartTime = ZonedDateTime.parse(epElement
                                        .getElementsByTagName("starttimeutc").item(0).getTextContent());
                                epStartTime.withZoneSameInstant(zoneId);
                                if(epStartTime.isAfter(EarliestEpTime) && epStartTime.isBefore(LatestEpTime)){
                                    String epTitle = epElement.getElementsByTagName("title").item(0).getTextContent();
                                    String epDesc = epElement.getElementsByTagName("description").item(0).getTextContent();
                                    ZonedDateTime epEndTime = ZonedDateTime.parse(epElement
                                            .getElementsByTagName("endtimeutc").item(0).getTextContent());
                                    Node epNodeId = epElement.getElementsByTagName("program").item(0);
                                    Element epIdElement = (Element) epNodeId;
                                    String epId = epIdElement.getAttribute("id");
                                    String imageURL = null;
                                    if(epElement.getElementsByTagName("imageurl").item(0) != null){
                                        imageURL = epElement.getElementsByTagName("imageurl").item(0).getTextContent();
                                        image = new ImageIcon(ImageIO.read(new URL(imageURL))
                                                    .getScaledInstance(200, 200, Image.SCALE_SMOOTH));
                                    }
                                    Episode episode = new Episode(epId,epTitle,epDesc, epStartTime, epEndTime, imageURL);
                                    episode.setImage(image);
                                    episodes.add(episode);
                                }else if(epStartTime.isAfter(LatestEpTime)){
                                    endSearch = true;
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }


    }

    public ArrayList<Episode> getEpisodes(){
        return episodes;
    }
}
