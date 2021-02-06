package htwb.ai.willi.Repository;

import htwb.ai.willi.Enity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, String>
{
     Optional<User> findByToken(String token);
}
