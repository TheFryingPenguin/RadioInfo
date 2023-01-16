import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class RadioInfoGUI{

    private final JFrame frame;
    private JMenuBar menubar;

    private RadioController controller;
    public JPanel panel;
    public JScrollPane scrollpane;
    public JPanel tablePanel;
    private ChannelCollector channelCollector;
    private EpisodeCollector episodeCollector;
    private DefaultTableModel model;
    private ArrayList<Episode> episodeList;
    public JTable table;
    public HashMap<String, ArrayList<Episode>> hashMap;

    public RadioInfoGUI(RadioController controller){
        hashMap = new HashMap<>();
        model = new DefaultTableModel();
        model.addColumn("Titel");
        model.addColumn("Avsnitt start");
        model.addColumn("Avsnitt slut");
        model.addColumn("Description");
        model.addColumn("image");
        ZonedDateTime currentTime = ZonedDateTime.now();
        this.controller = controller;
        channelCollector = new ChannelCollector();
        episodeCollector = new EpisodeCollector(currentTime);
        panel = new JPanel();
        frame = new JFrame("Radio Info");
        frame.setLayout(new BorderLayout());
        guiConfig();
        buildMenu();
    }

    private void guiConfig(){
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000,700);
        frame.setVisible(true);
    }

    private void buildMenu(){
        menubar = new JMenuBar();
        panel = new JPanel();
        JLabel instruct = new JLabel("Klicka på kanaler uppe i vänstra hörnet för att hitta kanaler, välj en sida" +
                " och navigera till en kanal");
        panel.add(instruct);
        frame.add(panel);
        JMenu menu = new JMenu("Kanaler");
        int currentPage = 1;
        JMenu subMenu = new JMenu("Sida "+currentPage);
        ArrayList<Channel> chList = controller.getChannelList();
        for (int i = 0; i< chList.size();i++){
            if(currentPage != chList.get(i).pageNr){
                currentPage = chList.get(i).pageNr;
                subMenu = new JMenu("Sida "+currentPage);
            }
            JMenuItem item = new JMenuItem(chList.get(i).name);
            String currentChannel = chList.get(i).id;
            item.addActionListener(e -> {
                if(!hashMap.containsKey(currentChannel)){
                    controller.update(currentChannel,this);
                }else{
                    channelChange(currentChannel);
                }
            });
            subMenu.add(item);
            menu.add(subMenu);
        }
        menubar.add(menu);
        frame.setJMenuBar(menubar);
    }

    public void updateTable(){
        table = new JTable(model);
        table.getColumnModel().getColumn(3).setMinWidth(0);
        table.getColumnModel().getColumn(3).setMaxWidth(0);
        table.getColumnModel().getColumn(4).setMinWidth(0);
        table.getColumnModel().getColumn(4).setMaxWidth(0);
        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                JPanel dialogPanel = new JPanel(new BorderLayout());
                Object title = table.getValueAt(row,0);
                Object desc = table.getValueAt(row,3);
                JDialog dialog = new JDialog(frame, title.toString(),true );
                JTextArea descLabel = new JTextArea(desc.toString());
                descLabel.setLineWrap(true);
                descLabel.setEditable(false);
                ImageIcon image = null;
                System.out.println(table.getValueAt(row, 4));
                if(table.getValueAt(row, 4) == null){
                    JTextArea isEmptyLable = new JTextArea("Finns ingen bild för detta avsnitt!");
                    dialogPanel.add(isEmptyLable, BorderLayout.NORTH);
                }else{
                    image = (ImageIcon) table.getValueAt(row, 4);
                }
                JLabel imageLabel = new JLabel(image);
                dialogPanel.add(descLabel, BorderLayout.SOUTH);
                dialogPanel.add(imageLabel, BorderLayout.CENTER);
                dialog.setContentPane(dialogPanel);
                dialog.setSize(500,400);
                dialog.setVisible(true);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        scrollpane = new JScrollPane(table);
        tablePanel = new JPanel();
        tablePanel.add(scrollpane);
        panel.add(tablePanel, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    public void channelChange(String chId){
        updateTableData(chId);
        if(panel.getComponentCount() != 0){
            for(Component c : panel.getComponents()){
                panel.remove(c);
            }
        }
        updateTable();
        JButton updateButton = new JButton("Uppdatera");
        updateButton.addActionListener(e -> {
            controller.update(chId, this);
        });
        panel.add(updateButton, BorderLayout.SOUTH);
        panel.revalidate();
        panel.repaint();
        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    public void updateTableData(String currentChannel){
        model.setRowCount(0);
        episodeList = hashMap.get(currentChannel);
        if(episodeList != null && !episodeList.isEmpty()){
            for(Episode ep : episodeList){
                model.addRow(new Object[]{ep.getTitle(),ep.getStartTime(),ep.getEndTime(),ep.getDescription(),ep.getImage()});
            }
        }else{
            model.addRow(new Object[]{"Inga program tillgängliga", "Ingen start tid", "Ingen slut tid","Inga detaljer"});
        }
        model.fireTableDataChanged();
    }


    public void loadingScreen(){
        if(panel.getComponentCount() != 0){
            for(Component c : panel.getComponents()){
                panel.remove(c);
            }
        }
        JLabel label = new JLabel("Hämtar avsnitt, var vänlig vänta...");
        panel.add(label, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
        frame.add(panel);
        frame.revalidate();
        frame.repaint();

    }
}
