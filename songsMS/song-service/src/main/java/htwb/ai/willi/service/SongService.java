package htwb.ai.willi.service;


import htwb.ai.willi.enitity.Song;
import htwb.ai.willi.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

/**
 * The SongsService allows the SongsController to access the Model
 */
@Service
public class SongService
{


     //-----------INSTANCE VARIABLES-----------//


     /**
      * The Songs Repository
      */
     @Autowired
     private SongRepository songRepository;


     //-----------INSTANCE VARIABLES-----------//


     /**
      * Returns a lis of all songs which are in the Database
      *
      * @return An ArraList of all songs in the Database
      */
     public ArrayList<Song> getAllSongs()
     {
          Iterable<Song> songs = songRepository.findAll();
          ArrayList songsArrayList = new ArrayList();
          songs.forEach(songsArrayList::add);

          return songsArrayList;
     }


     /**
      * Returns the Song with the gicen id
      *
      * @param id
      *         the id of the song
      *
      * @return An Optional with a song, the optional will be empty if there is no
      *         song with the id
      */
     public Optional<Song> getSongById(Integer id)
     {
          Optional<Song> song = songRepository.findById(id);
          return song;
     }


     /**
      * Deletes the song with the given id
      * if the song exists
      *
      * @param id
      *         The id of the song to delete
      */
     public void deleteSong(Integer id)
     {
          songRepository.deleteById(id);
     }


     /**
      * Adds a Song to the database
      *
      * @param song
      *         The song to be added
      */
     public void addSong(Song song)
     {
          songRepository.save(song);
     }


     /**
      * Updates an already existing song in the Database
      *
      * @param song
      *         the new version of the Song
      */
     public void updateSong(Song song)
     {
          songRepository.save(song);
     }
}
