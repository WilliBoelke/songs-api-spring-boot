package htwb.ai.willi.service;


import htwb.ai.willi.enitity.Song;
import htwb.ai.willi.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class SongService
{

     @Autowired
     private SongRepository songRepository ;

     public ArrayList<Song> getAllSongs()
     {
          Iterable<Song> songs = songRepository.findAll();
          ArrayList songsArrayList = new ArrayList();
          songs.forEach(songsArrayList::add);

          return songsArrayList;
     }

     public Optional<Song> getSongById(Integer id)
     {
          Optional<Song> song = songRepository.findById(id);
          return song;
     }

     public void deleteSong(Integer id)
     {
          songRepository.deleteById(id);
     }

     public void addSong(Song song)
     {
          songRepository.save(song);
     }

     public void updateSong(Song song)
     {
          songRepository.save(song);
     }
}
