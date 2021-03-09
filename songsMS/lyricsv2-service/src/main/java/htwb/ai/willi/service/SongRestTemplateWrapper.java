package htwb.ai.willi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class SongRestTemplateWrapper
{
     private RestTemplate restTemplate;

     public SongRestTemplateWrapper()
     {
          restTemplate = new RestTemplate();
     }

     public String verifySongId(int id)
     {
          try
          {
               return restTemplate.getForObject("http://localhost:9002 /songs/verify/" + id, String.class);
          }
          catch (HttpClientErrorException e)
          {
               return "Does not Exist";
          }
     }
}
