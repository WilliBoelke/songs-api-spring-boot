package htwb.ai.willi.Service;

import htwb.ai.willi.Enity.User;
import htwb.ai.willi.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService
{
     @Autowired
     private UserRepository userRepository;

     public User getUser(User user)
     {
          return userRepository.getOne(user.getUserId());
     }

     public Optional<User> findByToken(String token)
     {
          return userRepository.findByToken(token);
     }


     /**
      * Checks the Input Parameters
      *
      * @param user
      *
      * @return
      */
     public boolean correctValues(User user)
     {
          if (user.getUserId() == null)
          {
               return false;
          }
          if (user.getUserId().equals(""))
          {
               return false;
          }
          if (user.getPassword() == null)
          {
               return false;
          }
          return !user.getPassword().equals("");
     }
}
