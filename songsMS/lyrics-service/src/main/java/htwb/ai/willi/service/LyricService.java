package htwb.ai.willi.service;

import htwb.ai.willi.entity.Lyric;
import htwb.ai.willi.repository.LyricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;



@Service
public class LyricService
{


     @Autowired
     private LyricsRepository lyricsRepository ;

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
}
