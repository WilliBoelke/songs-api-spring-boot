package htwb.ai.willi.service;

import htwb.ai.willi.entity.Lyric;
import htwb.ai.willi.repository.LyricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;



@Service
public class LyricService
{


     @Autowired
     private LyricsRepository lyricsRepository ;

     @Autowired SongRestTemplateWrapper songRestTemplateWrapper;

     public Optional<Lyric> getByName(String songName) throws IOException
     {
          try
          {
               return Optional.of(lyricsRepository.getLyrics(songName));
          }
          catch (NullPointerException e)
          {
               return Optional.empty();
          }
     }


     public void addLyrics(Lyric lyric) throws IOException
     {
          lyricsRepository.addLyrics(lyric);
     }

     public ArrayList<Lyric> getAll() throws IOException
     {
          return lyricsRepository.getAll();
     }

     public int deleteLyrics(String songName)
     {
          return lyricsRepository.deleteLyrics(songName);
     }

     public int verifySong(String name, int id)
     {
          String result = songRestTemplateWrapper.verifySongId(id);
          if(result.equals("Does not Exist"))
          {
               return  3;
          }
          else if (!result.equals(name))
          {
               return 2;
          }
          return 1;
     }

}
