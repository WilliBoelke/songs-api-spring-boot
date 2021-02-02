package htwb.ai.WiMi.model.dao;

import htwb.ai.WiMi.logger.Log;
import htwb.ai.WiMi.model.database.User;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class SessionDAO implements SessionDAOInterface
{
    private final String TAG = getClass().getSimpleName();
    private HashMap<User, String> tokens = new HashMap<User, String>();


    @Override
    public boolean authenticate(String token)
    {
        Log.deb(TAG, "authenticate", "Called");
        return tokens.containsValue(token);
    }

    @Override
    public String createToken(User user)
    {
        Log.deb(TAG, "createToken", "Called with user : " + user.getUserId());
        Log.deb(TAG, "createToken", "Generating token");
        Supplier<String> tokenSupplier = () -> {

            StringBuilder token = new StringBuilder();
            long currentTimeInMilisecond = Instant.now().toEpochMilli();
            return token.append(currentTimeInMilisecond).append("-")
                    .append(UUID.randomUUID().toString()).toString();
        };

        String token = tokenSupplier.get();
        Log.deb(TAG, "createToken", "Token generated  = " + token);
        token = token.replaceAll("-", "");

        //shorten the token
        if (token.length() > 21)
        {
            token = token.substring(0, 20);
        }

        if (!tokens.containsKey(user))
        {
            this.addSession(user, token);
        }
        else
        {
            tokens.replace(user, token);
        }

        return token;
    }

    @Override
    public Map<User, String> getMap()
    {
        return tokens;
    }


    private void addSession(User user, String token)
    {
        this.tokens.put(user, token);
    }

}
