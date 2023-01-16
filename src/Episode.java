import javax.swing.*;
import java.awt.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Episode {

    private final String title;
    private final String description;
    private final String id;
    private final String epStart;

    private final String epEnd;
    private final String imageURl;

    private ImageIcon image;


    Episode(String id, String title, String description , ZonedDateTime epStart, ZonedDateTime epEnd, String imageURL){
        this.id = id;
        this.title = title;
        this.description = description;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm");
        this.epStart = dateTimeFormatter.format(epStart);
        this.epEnd = dateTimeFormatter.format(epEnd);
        this.imageURl = imageURL;
        this.image = null;
    }


    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public String getStartTime(){
        return epStart;
    }

    public String getEndTime(){
        return epEnd;
    }

    public ImageIcon getImage(){
        return image;
    }

    public void setImage(ImageIcon image){
        this.image = image;
    }
}
