package htwb.ai.WiMi.servlet;

import javax.persistence.*;
import java.util.List;

/**
 * Handles the connection to a PostgreSQl Database hosted on Heroku
 * Adds Songs to the Database and gets songs
 */
public class DatabaseHelper
{
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("WiMi-DB");

    /**
     * Adds a single {@link Song} to the database
     * @param song the song to be added
     */
    public void addSong(Song song)
    {
        EntityManager entityManager = getNewEntityManger();
        EntityTransaction entityTransaction = null;

        try
        {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            entityManager.persist(song);
            entityTransaction.commit();
        }
        catch (Exception exception)
        {
            if (entityTransaction != null)
            {
                entityTransaction.rollback();
            }
            exception.printStackTrace();
        }
        finally
        {
            entityManager.close();
        }
    }

    /**
     * Retrieves a single {@link Song} from the Database and returns it
     * @param id of the Song 
     * @return
     */
    public Song getSong(int id)
    {
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

    /**
     * @return all Songs that are stored in the Database
     */
    public List<Song> getAllSongs()
    {
        // Database query for all Songs
        String query = "SELECT c FROM Song c WHERE c.id IS NOT NULL";

        EntityManager entityManager = getNewEntityManger();

        TypedQuery<Song> typedQuery = entityManager.createQuery(query, Song.class);
        List<Song> allSongs = null;
        try
        {
            allSongs = typedQuery.getResultList();
        }
        catch (NoResultException exception)
        {
            exception.printStackTrace();
        }
        finally
        {
            entityManager.close();
        }
        return allSongs;
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
