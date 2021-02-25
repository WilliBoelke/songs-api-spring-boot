package htwb.ai.willi.lyricsservice.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import htwb.ai.willi.lyricsservice.entity.Lyric;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Watchable;

@Repository
public class LyricsRepository
{
     public void getLyrics(String name)
     {

     }


     public void addLyrics(Lyric lyric) throws IOException
     {
          FileWriter fileWriter = new FileWriter(getFile(lyric.getSongTitle()));
          fileWriter.write(asJsonString(lyric));
     }


     public void deleteLyrics(String name)
     {

     }

     public void updateLyrics(String name)
     {

     }

     public void  getBySongID(int id)
     {

     }


     private File getFile(String name)
     {
          URL url = this.getClass().getClassLoader().getResource("/lyrics/" + name +".json");
          File file = null;
          try
          {
               file = new File(url.toURI());
          }
          catch (URISyntaxException e)
          {
               file = new File(url.getPath());
          }
          finally
          {
               return file;
          }
     }

     public static String asJsonString(final Object obj)
     {
          try
          {
               return new ObjectMapper().writeValueAsString(obj);
          }
          catch (Exception e)
          {
               throw new RuntimeException(e);
          }
     }

}
