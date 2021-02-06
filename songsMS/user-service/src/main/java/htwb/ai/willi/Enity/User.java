package htwb.ai.willi.Enity;

import com.sun.istack.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;

/**
 *  Represents a song, with title, artist etc.
 */

@Entity
@Table(name = "users",schema = "public")
public class User implements Serializable
{
    @Transient
    private Logger log = LoggerFactory.getLogger(User.class);

    private static final long serialVersionUID = 1L;


    //-----------Instance Variables -----------//

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "userId", nullable = false)
    private String userId;

    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Column(name = "firstName", nullable = false)
    private String firstName;

    @NotNull
    @Column(name = "lastName", nullable = false)
    private String lastName;

    /**
     * Authorization token for the user
     */
    @Column(name = "token")
    private String token;

    //----------- Constructor -----------//

    public User() {
    }

    //----------- Getter And Setter -----------//

    public User(String userId, String password, String firstName, String lastName) {
        this.userId = userId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }



    //----------- Auth Token -----------//
    
    public void setToken() {
        this.token = createToken();
    }

    public String getToken() {
        return this.token;
    }

    private String createToken() {

        log.debug("createToken: Called" );
        log.debug("createToken: Generating token");
        Supplier<String> tokenSupplier = () -> {

            StringBuilder token = new StringBuilder();
            long currentTimeInMilisecond = Instant.now().toEpochMilli();
            return token.append(currentTimeInMilisecond).append("-")
                    .append(UUID.randomUUID().toString()).toString();
        };

        String token = tokenSupplier.get();
               log.debug("createToken: Token generated  = " + token);
        token = token.replaceAll("-", "");

        //shorten the token
        if (token.length() > 21)
        {
            token = token.substring(0, 20);
        }
        
        return token;
    }

}
