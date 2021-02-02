package htwb.ai.WiMi.model.dao;

import htwb.ai.WiMi.model.database.Song;

import java.util.List;

/**
 * Interface for Songs Data Access Objects
 *
 */
public interface SongsDAOInterface {

    /**
     * Returns all Songs
     * @return
     */
    List<Song> getAllSongs();

    /**
     * Returns the song with "ID"
     * @param id
     * @return
     */
    Song getSongById(int id);

    /**
     * Persisting a new Song
     * @param song
     * @return
     */
    Integer addSong(Song song);

    /**
     * Change properties of a existing Song
     * @param song
     */
    void updateSong(Song song);

    /**
     * Delete the Song
     * @param song
     */
    void deleteSong(Song song);

}
