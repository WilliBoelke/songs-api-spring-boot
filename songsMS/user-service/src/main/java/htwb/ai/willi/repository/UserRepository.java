package htwb.ai.willi.repository;

import htwb.ai.willi.enity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * The UserRepository is communicates with the Database configured in the
 * resources/ application.properties file.
 * <p>
 * Which is currently a PostgreSQL database on Heroku
 */
@Repository
public interface UserRepository extends JpaRepository<User, String>
{
     /**
      * Returns the user with the given auth token
      *
      * @param token
      *         the provided auth token
      *
      * @return an Optional user with that given token, will be empty if no user has
      *         the given token
      */
     Optional<User> findByToken(String token);
}
