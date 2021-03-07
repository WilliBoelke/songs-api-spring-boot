package htwb.ai.willi.enity;

import com.sun.istack.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * The User PJO /  Entity
 * <p>
 * A user can Sign in via the user Controller,
 * the user the will get a Token, which is also saved in the Database.
 * <p>
 * A user can use this Token to Authenticate in Other services.
 * <p>
 * For a Sign in the user needs to provide a correct pair of {@link #userId}
 * and {@link #password}.
 */
@Entity
@Table(name = "users", schema = "public")
@Slf4j
public class User implements Serializable
{


     private static final long serialVersionUID = 1L;


     //-----------Instance Variables -----------//


     /**
      * Slf4j logger
      */
     @Transient
     private Logger log = LoggerFactory.getLogger(User.class);

     /**
      * The users unique ID
      */
     @Id
     @Basic(optional = false)
     @NotNull
     @Column(name = "userid", nullable = false)
     private String userId;

     /**
      * The users passowrd
      */
     @NotNull
     @Column(name = "password", nullable = false)
     private String password;

     /**
      * First name of the user
      */
     @NotNull
     @Column(name = "firstname", nullable = false)
     private String firstName;

     /**
      * last name of the user
      */
     @NotNull
     @Column(name = "lastname", nullable = false)
     private String lastName;

     /**
      * Authorization token for the user
      * Can be null if the user haven't used the {@link htwb.ai.willi.controller.UserController}
      * to sign in, will be overwritten at each sign in
      */
     @Column(name = "token")
     private String token;


     //----------- Constructor -----------//


     /**
      * Empty Constructor
      * TODO I am not sure if this can be deleted...look into that later
      */
     public User()
     {
     }


     /**
      * Standard Constructor to set all fields
      *
      * @param userId
      * @param password
      * @param firstName
      * @param lastName
      */
     public User(String userId, String password, String firstName, String lastName)
     {
          this.userId = userId;
          this.password = password;
          this.firstName = firstName;
          this.lastName = lastName;
     }


     //----------- Auth Token -----------//


     /**
      * Generates a random 20 digits String which will be used as Token.
      * The token will be generated and asigned to (this) user Object by calling
      * its setToken() method.
      *
      * @return
      */
     private String createToken()
     {

          log.info("createToken: Called");
          log.info("createToken: Generating token");
          Supplier<String> tokenSupplier = () ->
          {

               StringBuilder token = new StringBuilder();
               long currentTimeInMilisecond = Instant.now().toEpochMilli();
               return token.append(currentTimeInMilisecond).append("-").append(UUID.randomUUID().toString()).toString();
          };

          String token = tokenSupplier.get();
          log.info("createToken: Token generated  = " + token);
          token = token.replaceAll("-", "");

          //shorten the token
          if (token.length() > 21)
          {
               log.info("createToken: shorten token ");
               token = token.substring(0, 20);
          }
          log.info("createToken: final token = " + token);
          return token;
     }


     //----------- Getter And Setter -----------//


     public String getUserId()
     {
          return userId;
     }


     public void setUserId(String userId)
     {
          this.userId = userId;
     }


     public String getPassword()
     {
          return password;
     }


     public void setPassword(String password)
     {
          this.password = password;
     }


     public String getFirstName()
     {
          return firstName;
     }


     public void setFirstName(String firstName)
     {
          this.firstName = firstName;
     }


     public String getLastName()
     {
          return lastName;
     }


     public void setLastName(String lastName)
     {
          this.lastName = lastName;
     }


     public void setToken()
     {
          this.token = createToken();
     }


     public String getToken()
     {
          return this.token;
     }

}
