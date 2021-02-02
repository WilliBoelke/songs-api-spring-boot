package htwb.ai.WiMi.model.dao;

import htwb.ai.WiMi.model.database.User;

import javax.persistence.*;

public class UserDAO implements UserDAOInterface
{
    private EntityManagerFactory entityManagerFactory;
    private final String TAG = getClass().getSimpleName();

    public UserDAO(String PU)
    {
        entityManagerFactory = Persistence.createEntityManagerFactory(PU);
    }

    public UserDAO()
    {
        entityManagerFactory = Persistence.createEntityManagerFactory("WiMi-DB-USER");
    }

    @Override
    public User getUser(User user)
    {
        String id = user.getUserId();

        // Database query for the user with ID
        String query = "SELECT c FROM User c WHERE c.userId = (:id)";

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        TypedQuery<User> typedQuery = entityManager.createQuery(query, User.class);
        typedQuery.setParameter("id", id);


        User databaseUser = null;

        try
        {
            databaseUser = typedQuery.getSingleResult();
        }
        catch (NoResultException exception)
        {
            exception.printStackTrace();
        }
        finally
        {
            entityManager.close();
        }
        return databaseUser;
    }

}
