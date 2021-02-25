package htwb.ai.willi.service;

import htwb.ai.willi.enity.User;
import htwb.ai.willi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService
{
     @Autowired
     private UserRepository userRepository;

     public Optional<User> findById(User user)
     {
          return userRepository.findById(user.getUserId());
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


     public void saveUser(User user)
     {
          userRepository.save(user);
     }
}
