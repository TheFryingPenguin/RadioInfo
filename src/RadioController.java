import javax.swing.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class RadioController {

    private ArrayList<Channel> channelList;
    private HashMap<String,ArrayList<Episode>> hashMap;
    RadioController(){
        channelList = new ArrayList<>();
        setupChannels();
        SwingUtilities.invokeLater(() -> {
            RadioInfoGUI gui = new RadioInfoGUI(this);
        });
    }

    public void setupChannels(){
        ChannelCollector channelCollector = new ChannelCollector();
        channelCollector.getChannels();
        channelList = channelCollector.getChannelList();
    }

    public void update(String chId, RadioInfoGUI gui){
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                gui.loadingScreen();
                ZonedDateTime timeUpdated = ZonedDateTime.now();
                EpisodeCollector episodeCollector = new EpisodeCollector(timeUpdated);
                episodeCollector.scheduledEpisodes(chId);
                ArrayList<Episode> eplist = episodeCollector.getEpisodes();
                gui.hashMap.put(chId,eplist);
                return null;
            }
            @Override
            protected void done() {
                gui.channelChange(chId);
            }
        };

        worker.execute();
    }

    public ArrayList<Channel> getChannelList(){
        return channelList;
    }

    public HashMap<String, ArrayList<Episode>> getHashMap(){
        return hashMap;
    }
}
