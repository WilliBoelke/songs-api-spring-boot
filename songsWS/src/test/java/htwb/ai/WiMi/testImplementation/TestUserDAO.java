package htwb.ai.WiMi.testImplementation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import htwb.ai.WiMi.model.dao.UserDAOInterface;
import htwb.ai.WiMi.model.database.User;

/**
 * Implementation of the UserDAOInterface for test purposes
 */
public class TestUserDAO implements UserDAOInterface {

    /**
     * Map UserId-User
     *
     */
    private final Map<String, User> myUsers;

    public TestUserDAO()
    {
        // Initializing the list and adding a test user
        myUsers = new ConcurrentHashMap<String, User>();

        User testusser = new User("42","geheim","TestUser","nachname");
        myUsers.put(testusser.getUserId(),testusser);

    }

    @Override
    public User getUser(User user)
    {
        return myUsers.get(user.getUserId());
    }
}
