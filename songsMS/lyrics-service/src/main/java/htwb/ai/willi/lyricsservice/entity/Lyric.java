package htwb.ai.willi.lyricsservice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Lyric
{
     @Id
     private int songId;

     @Column
     private String songTitle;

     private String lyric;


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


}
