package htwb.ai.willi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;


/**
 * Class to Wrap the {@link RestTemplate}
 * Makes a request to the user-service.
 * To verify the Auth token of a user from within the song-service
 */
@Service
public class AuthRestTemplateWrapper implements RestTemplateWrapper
{

     //-----------INSTANCE VARIABLES-----------//


     /**
      * The Rets template
      */
     private RestTemplate restTemplate;


     //-----------CONSTRUCTORS-----------//


     public AuthRestTemplateWrapper()
     {
          restTemplate = new RestTemplate();
     }


     //-----------PUBLIC METHODS-----------//


     @Override
     public String authenticateUser(String id) throws HttpClientErrorException, HttpServerErrorException
     {
          return    restTemplate.getForObject("http://localhost:9001 /auth/" + id, String.class);
     }
}
