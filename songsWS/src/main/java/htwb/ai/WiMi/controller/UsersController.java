package htwb.ai.WiMi.controller;


import htwb.ai.WiMi.Marshalling.Marshaller;
import htwb.ai.WiMi.logger.Log;
import htwb.ai.WiMi.model.dao.SessionDAOInterface;
import htwb.ai.WiMi.model.dao.UserDAOInterface;
import htwb.ai.WiMi.model.database.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="auth")
public class UsersController
{

    private final String TAG = getClass().getSimpleName();
    @Autowired
    private final SessionDAOInterface sessionDAO;
    @Autowired
    private final UserDAOInterface usersDAO;
    @Autowired
    private final Marshaller<User> jsonMarshaller;


    public UsersController(UserDAOInterface usersDAO, SessionDAOInterface sessionDAO, Marshaller<User> jsonMarshaller)
    {
        this.jsonMarshaller = jsonMarshaller;
        this.sessionDAO = sessionDAO;
        this.usersDAO = usersDAO;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> userSignIn(@RequestBody String string)
    {
        Log.deb(TAG, "userSignIn", "Called with parameters = " + string);


        // Checking the input Parameters

        User generatedUser = jsonMarshaller.unmarshal(string, User.class);

        if(!correctValues(generatedUser))
        {
            Log.err(TAG, "userSignIn", "username and or password is missing ");
            return new ResponseEntity<>("username and or password is missing.", HttpStatus.UNAUTHORIZED);
        }
        Log.deb(TAG, "userSignIn", "Parameters accepted =  " + generatedUser.getUserId() +" - "+ generatedUser.getPassword());


        // Comparing with database



        Log.deb(TAG , "userSignin", "Comparing with Database...");
        User databaseUser = usersDAO.getUser(generatedUser);




        if(databaseUser == null) // No Match found
        {
            Log.err(TAG, "userSignIn", "no Matching User in Database");
            return new ResponseEntity<>("User does not exist", HttpStatus.UNAUTHORIZED);
        }
        Log.deb(TAG, "userSignIn", "One matching user in database 1/3");




        if(!databaseUser.getUserId().equals(generatedUser.getUserId()))    // Not the same ID
        {
            Log.err(TAG, "userSignIn", "User Id not matching = " + generatedUser.getUserId() + "  -  " + databaseUser.getUserId());
            return new ResponseEntity<>("Wrong User Name", HttpStatus.UNAUTHORIZED);
        }
        Log.deb(TAG, "userSignIn", "One matching user in database 2/3");



        if(!databaseUser.getPassword().equals(generatedUser.getPassword()))  // No the same password
        {
            Log.err(TAG, "userSignIn", "Wrong Password");
            return new ResponseEntity<>("Wrong Password", HttpStatus.UNAUTHORIZED);
        }
        Log.deb(TAG, "userSignIn", "One matching user in database 3/3");


        return new ResponseEntity<>(sessionDAO.createToken(databaseUser), HttpStatus.OK);
    }



    /**
     * Checks the Input Parameters
     * @param user
     * @return
     */
    private boolean correctValues(User user)
    {
        if(user.getUserId() == null )
        {
            return  false;
         }
        if(user.getUserId().equals(""))
        {
            return false;
        }
        if(user.getPassword() == null )
        {
            return false;
        }
        if( user.getPassword().equals(""))
        {
            return false;
        }
        return true;
    }

}
