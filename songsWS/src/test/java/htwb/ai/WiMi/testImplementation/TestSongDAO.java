package htwb.ai.WiMi.testImplementation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import htwb.ai.WiMi.model.dao.SongsDAOInterface;
import htwb.ai.WiMi.model.database.Song;

/**
 * Test Implementation of the {@link SongsDAOInterface}
 * used fpr unit testing
 */
public class TestSongDAO implements SongsDAOInterface {

    private final Map<Integer, Song> mySongs;

    public TestSongDAO() {

        mySongs = new ConcurrentHashMap<>();

        Song song = new Song(1, "Wind Of Change", "Scorpions", "Single", 1991);
        mySongs.put(song.getId(), song);

    }

    @Override
    public List<Song> getAllSongs()
    {

        return new ArrayList<>(mySongs.values());
    }

    @Override
    public Song getSongById(int id)
    {
        Collection<Song> allSongs = mySongs.values();


        for(Song u : allSongs)
        {
            if (u.getId() == id) {return u;}
        }
        return null;
    }

    @Override
    public Integer addSong(Song song)
    {
        mySongs.put(song.getId(), song);
        return song.getId();
    }

    @Override
    public void updateSong(Song song)
    {
        mySongs.put(song.getId(), song);
    }

    @Override
    public void deleteSong(Song song)
    {
        mySongs.remove(song.getId());
    }
}
