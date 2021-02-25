package htwb.ai.willi.controller;

import htwb.ai.willi.enity.User;
import htwb.ai.willi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Optional;


/**
 * This REST Controller provides REST methods
 * for a user to sign in, and get his session Token
 * and for other Services to verify that a token exists or belongs to a registered user.
 */
@RestController
@RequestMapping(value = "auth")
@Slf4j
public class UserController
{
     private final String TAG = getClass().getSimpleName();
     private final Logger log = LoggerFactory.getLogger(UserController.class);

     @Autowired
     private UserService userService;


     /**
      * Lets a user start a "Session"
      * A Token will be generated for the user, and added to the user Model.
      * The user can use that token, to authenticate himself throughout other services
      *
      * @param user
      *
      * @return
      */
     @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
     public ResponseEntity<String> userSignIn(@RequestBody User user)
     {
          log.info("userSignIn: Called with parameters = " + user.getUserId());


          // Checking the input Parameters

          if (!userService.correctValues(user))
          {
               log.error("userSignIn: username and or password is missing ");
               return new ResponseEntity<>("username and or password is missing.", HttpStatus.UNAUTHORIZED);
          }
          log.info("userSignIn: Parameters accepted =  " + user.getUserId() + " - " + user.getPassword());


          // Comparing with database


          log.info("userSignIn: Comparing with Database...");
          Optional<User> optionalUser = userService.findById(user);
          User databaseUser;

          if (optionalUser.isPresent()) // No Match found
          {
            databaseUser = optionalUser.get();
          }
          else
          {
               log.error("userSignIn: no Matching User in Database");
               return new ResponseEntity<>("User does not exist", HttpStatus.UNAUTHORIZED);
          }
          log.error("userSignIn: One matching user in database 1/3");


          if (!databaseUser.getPassword().equals(user.getPassword()))  // No the same password
          {
               log.error("userSignIn: Wrong Password");
               return new ResponseEntity<>("Wrong Password", HttpStatus.UNAUTHORIZED);
          }
          log.info("userSignIn: One matching user in database 2/3");


          if (!databaseUser.getUserId().equals(user.getUserId()))    // Not the same ID
          {
               log.error("userSignIn: User Id not matching = " + user.getUserId() + "  -  " + databaseUser.getUserId());
               return new ResponseEntity<>("Wrong User Name", HttpStatus.UNAUTHORIZED);
          }
          log.info("userSignIn: One matching user in database 3/3");

          databaseUser.setToken();
          userService.saveUser(databaseUser);
          return new ResponseEntity<>(databaseUser.getToken(), HttpStatus.OK);
     }


     /**
      * Other Services may use this Method to verify if a Token exists/belongs to a user
      *
      * @param token
      *         authentication token
      *
      * @return userId which belongs to token
      */
     @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
     public ResponseEntity<String> getUserByToken(@PathVariable(value = "id") String token)
     {
          try
          {
               Optional<User> user = userService.findByToken(token);
               return user.map(value -> new ResponseEntity<>(value.getUserId(), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED));
          }
          catch (HttpStatusCodeException ex)
          {
               return new ResponseEntity<>(ex.getResponseBodyAsString(), ex.getStatusCode());
          }
     }




}
