package htwb.ai.willi.repository;

import htwb.ai.willi.enity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, String>
{
     Optional<User> findByToken(String token);
}
