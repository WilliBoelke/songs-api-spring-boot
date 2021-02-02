package htwb.ai.WiMi.model.database;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.io.Serializable;

/**
 *  Represents a song, with title, artist etc.
 */

@Entity
@Table(name = "users",schema = "public")
public class User implements Serializable
{
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
    @Column(name = "firstname", nullable = false)
    private String firstName;

    @NotNull
    @Column(name = "lastname", nullable = false)
    private String lastName;


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
}
