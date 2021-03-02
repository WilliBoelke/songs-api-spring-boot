package htwb.ai.willi.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


public class Lyric implements Serializable, Comparable<Lyric>
{
     private int songId;

     private String songTitle;

     private String lyric;


     /**
      * Required empty constructor
      */
     public Lyric()
     {
          // Empty
     }

     public Lyric(int songId, String songTitle, String lyric)
     {
          this.songId = songId;
          this.songTitle = songTitle;
          this.lyric = lyric;
     }

     public int getSongId()
     {
          return songId;
     }

     public void setSongId(int songId)
     {
          this.songId = songId;
     }

     public String getSongTitle()
     {
          return songTitle;
     }

     public void setSongTitle(String songTitle)
     {
          this.songTitle = songTitle;
     }

     public String getLyric()
     {
          return lyric;
     }

     public void setLyric(String lyric)
     {
          this.lyric = lyric;
     }


     @Override
     public int compareTo(Lyric o)
     {

          if (this.getSongTitle().equals(o.getSongTitle()) && this.getLyric().equals(o.getLyric()))
          {
               return 1;
          }
          return -1;

     }
}
