package htwb.ai.willi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthRestTemplateWrapper implements RestTemplateWrapper
{

     private RestTemplate restTemplate;

     public AuthRestTemplateWrapper()
     {
          restTemplate = new RestTemplate();
     }

     @Override
     public String authenticateUser(String id) throws HttpClientErrorException, HttpServerErrorException
     {
          return    restTemplate.getForObject("http://localhost:9001 /auth/" + id, String.class);
     }
}
