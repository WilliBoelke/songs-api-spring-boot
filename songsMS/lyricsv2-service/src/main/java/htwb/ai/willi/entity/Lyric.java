package htwb.ai.willi.entity;

import java.io.Serializable;

/**
 * This Service manages Lyrics and saves the to the local file system.
 * This Klass describes the Lyrics as managed here
 * Lyrics have a name and an Id, which are equal to the name and the id
 * of the song (in the songs-service)
 *
 * Finally they have a string which - of course - contains the Lyrics itself
 */
public class Lyric implements Serializable
{


     //-----------INSTANCE VARIABLES  -----------//

     /**
      * The id of the son/lyrics
      */
     private int songId;

     /**
      * The title of the song/lyrics
      */
     private String songTitle;

     /**
      * The lyrics
      */
     private String lyric;


     //-----------CONSTRUCTORS-----------//


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


     //-----------GETTER AND SETTER -----------//


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
