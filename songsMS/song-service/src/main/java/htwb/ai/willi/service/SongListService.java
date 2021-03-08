package htwb.ai.willi.service;


import htwb.ai.willi.enitity.SongList;
import htwb.ai.willi.repository.SongListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


/**
 * The SongList service lets the controller access the {@link SongListRepository}
 */
@Service
public class SongListService
{

     //-----------INSTANCE VARIABLES-----------//


     /**
      * The SongsList Repository
      */
     @Autowired
     SongListRepository songListRepository;


     //-----------PUBLIC METHODS-----------//


     /**
      * Returns the SongList with the given ID as an Optional.
      * If there is no List with the ID, then the Optional will be empty
      *
      * @param userId
      *
      * @return
      */
     public Optional<SongList> findById(int userId)
     {
          return songListRepository.findById(userId);
     }

     /**
      * Returns a list of all the songslist which have the owner with the given ID
      *
      * @param userId
      *         The Id of the owner of the songLists
      *
      * @return A List of all song owned by the user with userId
      */
     public List<SongList> getSongListsFromUser(String userId)
     {
          return songListRepository.findAllByOwnerId(userId);
     }

     /**
      * Adds a new Song List to the model
      *
      * @param songList
      *         The SongList to be added
      */
     public void addSongList(SongList songList)
     {
          songListRepository.save(songList);
     }

     /**
      * Deletes  a SongList from the model
      *
      * @param songList
      *         The SongList to be deleted
      */
     public void deleteList(SongList songList)
     {
          songListRepository.delete(songList);
     }
}
