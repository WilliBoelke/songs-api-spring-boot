package htwb.ai.willi.repository;


import htwb.ai.willi.enitity.SongList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SongListRepository extends JpaRepository<SongList, Integer>
{

     List<SongList> findAllByOwnerId(String userId);
}
