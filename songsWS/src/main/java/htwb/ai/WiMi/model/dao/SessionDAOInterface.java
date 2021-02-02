package htwb.ai.WiMi.model.dao;

import htwb.ai.WiMi.model.database.User;

import java.util.Map;

public interface SessionDAOInterface
{
    boolean authenticate(String token);

    String createToken(User user);

    Map<User,String> getMap();
}
