package htwb.ai.willi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SongRestTemplateWrapper
{
     private RestTemplate restTemplate;

     public SongRestTemplateWrapper()
     {
          restTemplate = new RestTemplate();
     }

     public String requestSongNameById(int id)
     {
          return    restTemplate.getForObject("http://localhost:9002 /songs/" + id, String.class);
     }
}
