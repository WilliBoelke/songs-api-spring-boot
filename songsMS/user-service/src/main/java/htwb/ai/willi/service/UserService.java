package htwb.ai.willi.service;

import htwb.ai.willi.enity.User;
import htwb.ai.willi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * The UserService is user by the {@link htwb.ai.willi.controller.UserController} to access the
 * {@link UserService}
 */
@Service
public class UserService
{


     //-----------Instance Variables -----------//

     /**
      * The UserRepository
      */
     @Autowired
     private UserRepository userRepository;


     //-----------Access Model -----------//


     /**
      * @param user
      *
      * @return
      */
     public Optional<User> findById(User user)
     {
          return userRepository.findById(user.getUserId());
     }


     /**
      * Returns the User which has this token in an Optional.
      * The optional will be empty if no user has this token
      *
      * @param token
      *
      * @return
      */
     public Optional<User> findByToken(String token)
     {
          return userRepository.findByToken(token);
     }


     /**
      * Saves or updates a user in to the model
      *
      * @param user
      *         the new/updated user
      */
     public void saveUser(User user)
     {
          userRepository.save(user);
     }


     //-----------Others -----------//

     /**
      * Checks the Input Parameters
      *
      * @param user
      *
      * @return true ifd the values are valid , else false
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
