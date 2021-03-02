package htwb.ai.willi.controller;


import htwb.ai.willi.service.AuthRestTemplateWrapper;
import htwb.ai.willi.service.LyricService;
import htwb.ai.willi.entity.Lyric;
import htwb.ai.willi.service.SongRestTemplateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;


@RestController
@RequestMapping(value = "/lyrics")
@Slf4j
public class LyricController
{


     //-----------INSTANCE VARIABLES  -----------//

     @Autowired
     private LyricService lyricService;

     @Autowired
     private AuthRestTemplateWrapper authRestTemplateWrapper;

     @Autowired
     private SongRestTemplateWrapper songRestTemplateWrapper;

     private Logger log = LoggerFactory.getLogger(LyricController.class);


     @GetMapping ( produces = MediaType.APPLICATION_JSON_VALUE)
     public ResponseEntity<String> getAllLyrics(@RequestHeader("Accept") String acceptHeader, @RequestHeader(
             "Authorization") String authorization)
     {
          String userIDForGivenAuthorizationToken = "";
          try
          {
               userIDForGivenAuthorizationToken = authRestTemplateWrapper.request(authorization);
          }
          catch (Exception e)
          {
               return new ResponseEntity("Not a valid authorization token :  " + authorization,
                       HttpStatus.UNAUTHORIZED);
          }

          ArrayList<Lyric> allLyrics = null;
          try
          {
               allLyrics = lyricService.getAll();
               log.info("getAllLyrics: got  " + allLyrics.size() + " lyrics from files");
          }
          catch (IOException e)
          {
               return new ResponseEntity("A serverside problem occurred", HttpStatus.INTERNAL_SERVER_ERROR);
          }

          if (allLyrics.size() > 0)
          {
               log.info("getAllLyrics: Request finished send  " + allLyrics.size() + " lyrics as response");
               return new ResponseEntity(allLyrics, HttpStatus.OK);
          }
          return new ResponseEntity<String>("No lyrics available", HttpStatus.NOT_FOUND);
     }


     @GetMapping (value = "/{title}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
     public ResponseEntity<String> getSong(@PathVariable(value = "title") String songName, @RequestHeader("Accept") String acceptHeader, @RequestHeader(
             "Authorization") String authorization)
     {

          String userIDForGivenAuthorizationToken = "";
          try
          {
               userIDForGivenAuthorizationToken = authRestTemplateWrapper.request(authorization);
          }
          catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc)
          {
               return new ResponseEntity("Not a valid authorization token :  " + authorization,
                       HttpStatus.UNAUTHORIZED);
          }


          Optional<Lyric> optLyric = null;
          try
          {
               optLyric = lyricService.getByName(songName);
          }
          catch (IOException e)
          {
               e.printStackTrace();
          }


          if (optLyric.isPresent())
          {
               return new ResponseEntity(optLyric.get(), HttpStatus.OK);
          }
          return new ResponseEntity<String>("No lyrics to the song" + songName, HttpStatus.NOT_FOUND);

     }

     @GetMapping (value = "/id/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
     public ResponseEntity<String> getSong(@PathVariable(value = "id") int id, @RequestHeader("Accept") String acceptHeader, @RequestHeader(
             "Authorization") String authorization)
     {

          String userIDForGivenAuthorizationToken = "";
          try
          {
               userIDForGivenAuthorizationToken = authRestTemplateWrapper.request(authorization);
          }
          catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc)
          {
               return new ResponseEntity("Not a valid authorization token :  " + authorization,
                       HttpStatus.UNAUTHORIZED);
          }


          Optional<Lyric> optLyric = null;
          try
          {
               optLyric = lyricService.getByName("songName");
          }
          catch (IOException e)
          {
               e.printStackTrace();
          }


          return new ResponseEntity<String>("Not Supported Ýet4" , HttpStatus.NOT_FOUND);

     }



     //-----------POST -----------//


     /**
      * HTTP POST
      * <p>
      * Adding new Lyrics to the files
      *
      * @param lyric
      * @param request
      *
      * @return
      */
     @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
     public ResponseEntity<String> postSong(@RequestBody Lyric lyric, HttpServletRequest request, @RequestHeader(
             "Authorization") String authorization)
     {

          log.info("postSong: Called with new Lyrics for the Song : " + lyric.getSongTitle());


          String userIDForGivenAuthorizationToken = "";
          try
          {
               userIDForGivenAuthorizationToken = authRestTemplateWrapper.request(authorization);
          }
          catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc)
          {
               return new ResponseEntity("Not a valid authorization token :  " + authorization,
                       HttpStatus.UNAUTHORIZED);
          }

          if (lyric.getSongTitle() == null || lyric.getSongTitle().equals(""))
          {
               log.error("postSong: Lyrics without title");
               return new ResponseEntity<>("Wrong body: no title", HttpStatus.BAD_REQUEST);
          }


          try
          {
               lyricService.addLyrics(lyric);
          }
          catch (IOException e)
          {
               e.printStackTrace();
          }
         // URI location = URI.create(request.getRequestURI() + "/" + lyric.getSongTitle());
          return new ResponseEntity<>("The Lyrics are saved, yo can access them under \\lyrics\\"+ lyric.getSongTitle(), HttpStatus.CREATED);

     }


     //-----------Delete -----------//

     @DeleteMapping (value = "/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
     public ResponseEntity<String> deleteByName(@PathVariable(value = "title") String songName, @RequestHeader("Accept") String acceptHeader, @RequestHeader("Authorization") String authorization)
     {

          String userIDForGivenAuthorizationToken = "";
          try
          {
               userIDForGivenAuthorizationToken = authRestTemplateWrapper.request(authorization);
          }
          catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc)
          {
               return new ResponseEntity("Not a valid authorization token :  " + authorization,
                       HttpStatus.UNAUTHORIZED);
          }

          int result = lyricService.deleteLyrics(songName);

          switch (result)
          {
               case 0:
                    return new ResponseEntity("The Lyrics for the song " +songName+ " was deleted", HttpStatus.OK);
               case 1 :
                    return new ResponseEntity("The lyrics for the song " +songName+ "doesnt exist", HttpStatus.BAD_REQUEST);
               case 2:
                    return new ResponseEntity( "Couldt delete The Lyrics for the Song" + songName , HttpStatus.INTERNAL_SERVER_ERROR);
               default:
                    return new ResponseEntity( "Couldt delete The Lyrics for the Song" + songName , HttpStatus.INTERNAL_SERVER_ERROR);
          }
     }


     @DeleteMapping (value = "id//{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
     public ResponseEntity<String> deleteByID(@PathVariable(value = "id") int id, @RequestHeader("Accept") String acceptHeader, @RequestHeader("Authorization") String authorization)
     {

          String userIDForGivenAuthorizationToken = "";
          try
          {
               userIDForGivenAuthorizationToken = authRestTemplateWrapper.request(authorization);
          }
          catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc)
          {
               return new ResponseEntity("Not a valid authorization token :  " + authorization,
                       HttpStatus.UNAUTHORIZED);
          }

          String songName = songRestTemplateWrapper.requestSongNameById(id);

          int result = lyricService.deleteLyrics(songName);

          switch (result)
          {
               case 0:
                    return new ResponseEntity("The Lyrics for the song " +songName+ " was deleted", HttpStatus.OK);
               case 1 :
                    return new ResponseEntity("The lyrics for the song " +songName+ "doesnt exist", HttpStatus.BAD_REQUEST);
               case 2:
                    return new ResponseEntity( "Couldt delete The Lyrics for the Song" + songName , HttpStatus.INTERNAL_SERVER_ERROR);
               default:
                    return new ResponseEntity( "Couldt delete The Lyrics for the Song" + songName , HttpStatus.INTERNAL_SERVER_ERROR);
          }
     }
}
