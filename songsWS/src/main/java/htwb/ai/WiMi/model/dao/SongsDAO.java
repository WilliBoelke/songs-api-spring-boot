package htwb.ai.WiMi.model.dao;

import htwb.ai.WiMi.logger.Log;
import htwb.ai.WiMi.model.database.Song;

import javax.persistence.*;
import java.util.List;



/**
 * Handles the connection to a PostgreSQl Database hosted on Heroku
 * Adds Songs to the Database and gets songs
 */
public class SongsDAO implements SongsDAOInterface
{
    private final String TAG = getClass().getSimpleName();

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("WiMi-DB-SONG");

    @Override
    public List<Song> getAllSongs()
    {
        Log.deb(TAG, "getAllSongs", "Called");

        EntityManager entityManager = getNewEntityManger();
        // Database query for all Songs
        String query = "SELECT c FROM Song c WHERE c.id IS NOT NULL";

        Log.deb(TAG, "getAllSongs", "Trying to get songs from database ... ");

        TypedQuery<Song> typedQuery = entityManager.createQuery(query, Song.class);
        List<Song> allSongs = null;
        try
        {
            allSongs = typedQuery.getResultList();
        }
        catch (NoResultException exception)
        {
            Log.err(TAG, "getAllSongs", "NoResultException :  " + exception.getStackTrace());
        }
        finally
        {
            entityManager.close();
            Log.deb(TAG, "getAllSongs", "Got Songs from database : found " + allSongs.size() + " entries");
        }
        Log.deb(TAG, "getAllSongs", "Return");
        return allSongs;
    }

    @Override
    public Song getSongById(int id)
    {
        Log.deb(TAG, "getSongById", "Called with id " + id);

        // Database query for the Song with ID
        String query = "SELECT c FROM Song c WHERE c.id = (:id)";
        EntityManager entityManager = getNewEntityManger();
        TypedQuery<Song> typedQuery = entityManager.createQuery(query, Song.class);
        typedQuery.setParameter("id", id);
        Song song = null;

        try
        {
            song = typedQuery.getSingleResult();
        }
        catch (NoResultException exception)
        {
            exception.printStackTrace();
        }
        finally
        {
            entityManager.close();
        }
        return song;
    }

    @Override
    public Integer addSong(Song song)
    {
        Log.deb(TAG, "addSong", "Called with Song = "  + song.getTitle() + " "  +  song.getId()+ " " + song.getArtist()) ;

        EntityManager entityManager = getNewEntityManger();
        entityManager.getTransaction().begin();
        entityManager.persist(song);
        entityManager.getTransaction().commit();
        entityManager.close();
        return song.getId();
    }

    @Override
    public void updateSong(Song song)
    {
        Log.deb(TAG, "updateSong", "Called for Song  " + song.getTitle());

        EntityManager entityManager = getNewEntityManger();

        entityManager.getTransaction().begin();
        entityManager.merge(song);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void deleteSong(Song song)
    {
        Log.deb(TAG, "deleteSong", "Called for Song  " + song.getTitle());

        EntityManager entityManager = getNewEntityManger();

        Song s = entityManager.find(Song.class, song.getId());
        entityManager.getTransaction().begin();
        entityManager.remove(s);
        entityManager.getTransaction().commit();
        entityManager.close();
    }




    private EntityManager getNewEntityManger()
    {
        if(mockEm == null)
        {
            return ENTITY_MANAGER_FACTORY.createEntityManager();
        }
        return mockEm;
    }

    private EntityManager mockEm = null;

    public void insertMockEntityManager(EntityManager mock)
    {
        mockEm = mock;
    }
}
