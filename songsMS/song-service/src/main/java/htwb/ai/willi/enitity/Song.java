package htwb.ai.willi.enitity;

import com.sun.istack.NotNull;

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
     @Basic(optional = false)
     @NotNull
     @Column(name = "id", nullable = false)
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


     //----------- Constructor -----------//

     /**
      * Required empty constructor
      */
     public Song()
     {
          // Empty
     }

     public Song(Builder builder){
          this.id = builder.id;
          this.title = builder.title;
          this.artist = builder.artist;
          this.label = builder.label;
          this.released = builder.released;
     }

     public Song(int id, String title, String artist, String label, int released)
     {
          this.id = id;
          this.title = title;
          this.artist = artist;
          this.label = label;
          this.released = released;
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

     public static long getSerialVersionUID()
     {
          return serialVersionUID;
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


     //----------- Builder -----------//

     public static Builder builder(){
          return new Builder();
     }

     public static final class Builder
     {
          private int id;
          private String title;
          private String artist;
          private String label;
          private int released;

          private Builder()
          {

          }

          public Builder withId(int id)
          {
               this.id = id;
               return this;
          }

          public Builder withTitle(String title)
          {
               this.title = title;
               return this;
          }

          public Builder withArtist(String artist)
          {
               this.artist = artist;
               return this;
          }

          public Builder withLabel(String label)
          {
               this.label = label;
               return this;
          }


          public Builder withReleased(int released)
          {
               this.released = released;
               return this;
          }

          public Song build()
          {
               return new Song(this);
          }
     }
}
