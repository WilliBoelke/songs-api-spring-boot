package htwb.ai.WiMi.model.dao;

import htwb.ai.WiMi.model.database.User;

/**
 * interface for the UserDAO
 * Only need a getter for a single user
 */
public interface UserDAOInterface
{
    User getUser(User user);
}
