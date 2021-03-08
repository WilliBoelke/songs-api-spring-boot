package htwb.ai.willi.service;


import htwb.ai.willi.enitity.SongList;
import htwb.ai.willi.repository.SongListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SongListService
{
     @Autowired
     SongListRepository songListRepository;

     public Optional<SongList> findById(int id)
     {
          return  songListRepository.findById(id);
     }

     public List<SongList> getSongListsFromUser(String userId)
     {
          return  songListRepository.findAllByOwnerId(userId);
     }

     public void addSongList(SongList songList)
     {
          songListRepository.save(songList);
     }


     public void deleteList(SongList songList)
     {
          songListRepository.delete(songList);
     }
}
