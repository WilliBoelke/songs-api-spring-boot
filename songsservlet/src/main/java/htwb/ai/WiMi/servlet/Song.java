package htwb.ai.WiMi.servlet;

import javax.persistence.*;
import java.io.Serializable;

/**
 *  Represents a song, with title, artist etc.
 */
@Entity
@Table(name="songs")
public class Song implements Serializable, Comparable<Song>
{

    private static final long serialVersionUID = 1L;

    //-----------Instance Variables -----------//

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    /**
     * The name/title of the song
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * The creator of the song
     */
    @Column(name = "artist", nullable = false)
    private String artist;

    /**
     * Label which published the song
     */
    @Column(name = "label")
    private String label;

    /**
     * The the release year of the song (yyyy)
     */
    @Column(name = "released")
    private int released;


    //----------- Getter And Setter -----------//

    /**
     * Required empty constructor
     */
    public Song()
    {
         // Empty
    }


    //----------- Getter And Setter -----------//

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getArtist()
    {
        return artist;
    }

    public void setArtist(String artist)
    {
        this.artist = artist;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public int getReleased()
    {
        return released;
    }

    public void setReleased(int released)
    {
        this.released = released;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }


    @Override
    public int compareTo(Song o)
    {
        if(this.getTitle().equals(o.getTitle()) &&
            this.getArtist().equals(o.getArtist()) &&
            this.getLabel().equals(o.getLabel()) &&
           this.getReleased() == o.getReleased())
        {
            return 1;
        }
        return -1;
    }
}
