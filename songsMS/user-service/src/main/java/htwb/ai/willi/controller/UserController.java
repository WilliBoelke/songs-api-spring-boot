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
 * for a user to sign in, and get his auth Token
 * and for other Services to verify that a token
 * exists or belongs to a registered user.
 */
@RestController
@RequestMapping(value = "auth")
@Slf4j
public class UserController
{


     //-----------Instance Variables -----------//


     /**
      * Logger
      */
     private final Logger log = LoggerFactory.getLogger(UserController.class);

     /**
      * The user Service
      */
     @Autowired
     private UserService userService;


     //-----------Http Mapping -----------//


     /**
      * Lets a user start a "Session"
      * A Token will be generated for the user, and added to the user Model.
      * The user can use that token, to authenticate himself throughout other services
      * which will user the {@link this#getUserByToken(String)} )} method
      *
      * @param user
      *         the user, can be given as JSON
      *
      * @return A {@link ResponseEntity} which contains a message and the HTTP status
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


          // No Match found


          if (optionalUser.isPresent())
          {
               databaseUser = optionalUser.get();
          }
          else
          {
               log.error("userSignIn: no Matching User in Database");
               return new ResponseEntity<>("User does not exist", HttpStatus.UNAUTHORIZED);
          }
          log.info("userSignIn: One matching user in database 1/3");


          // No the same password


          if (!databaseUser.getPassword().equals(user.getPassword()))
          {
               log.error("userSignIn: Wrong Password");
               return new ResponseEntity<>("Wrong Password", HttpStatus.UNAUTHORIZED);
          }
          log.info("userSignIn: One matching user in database 2/3");


          // Not the same ID


          if (!databaseUser.getUserId().equals(user.getUserId()))
          {
               log.error("userSignIn: User Id not matching = " + user.getUserId() + "  -  " + databaseUser.getUserId());
               return new ResponseEntity<>("Wrong User Name", HttpStatus.UNAUTHORIZED);
          }
          log.info("userSignIn: One matching user in database 3/3");


          //Updating the model


          databaseUser.setToken();
          userService.saveUser(databaseUser);


          // response (Success)


          return new ResponseEntity<>(databaseUser.getToken(), HttpStatus.OK);
     }


     /**
      * Other Services may use this Method to verify if a Token exists/belongs to a user
      *
      * @param token
      *         authentication token
      *
      * @return userId
      *         A ResponseEntity with the HTTP Status and a message,
      *         if the user exists the message will be the userId
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


     //-----------Others -----------//
}
