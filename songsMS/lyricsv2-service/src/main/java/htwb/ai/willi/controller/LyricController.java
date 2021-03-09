package htwb.ai.willi.controller;


import htwb.ai.willi.entity.Lyric;
import htwb.ai.willi.service.AuthRestTemplateWrapper;
import htwb.ai.willi.service.LyricService;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;


@RestController
@RequestMapping(value = "/lyrics")
@Slf4j
public class LyricController
{


     //-----------INSTANCE VARIABLES  -----------//


     /**
      * The Lyrics Service
      */
     @Autowired
     private LyricService lyricService;

     /**
      * Auth RestTemplate wrapper to request the user-service
      */
     @Autowired
     private AuthRestTemplateWrapper authRestTemplateWrapper;

     /**
      * Song Rest Template Wrapper, to request the songs-service
      */
     @Autowired
     private SongRestTemplateWrapper songRestTemplateWrapper;

     /**
      * The logger
      */
     private Logger log = LoggerFactory.getLogger(LyricController.class);


     //-----------HTTP GET-----------//


     /**
      * Returns all lyrics as a Json String
      *
      * @param acceptHeader
      *         The Accept Header (should be JSON)
      * @param authorization
      *         The users ath token
      *
      * @return A ResponseEntity containing6 the HTTP status and a message,
      *         in case of success the message will be a JSON string with the
      *         requested lyrics
      */
     @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
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


     /**
      * GET for a specific song lyric, here defined by the title of the song
      *
      * @param songName
      *         The name of the song/lyrics
      * @param acceptHeader
      *         The accept header (should be JSON)
      * @param authorization
      *         The users auth token
      *
      * @return A ResponseEntity containing6 the HTTP status and a message,
      *         in case of success the message will be a JSON string with the
      *         requested lyrics
      */
     @GetMapping(value = "/{title}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
     public ResponseEntity<String> getSong(@PathVariable(value = "title") String songName,
                                           @RequestHeader("Accept") String acceptHeader, @RequestHeader(
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


     /**
      * GET for a specific song lyric, here defined by the id of the song
      *
      * @param id
      *         The id of the song/lyrics
      * @param acceptHeader
      *         The accept header (should be JSON)
      * @param authorization
      *         The users auth token
      *
      * @return A ResponseEntity containing6 the HTTP status and a message,
      *         in case of success the message will be a JSON string with the
      *         requested lyrics
      */
     @GetMapping(value = "/id/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
     public ResponseEntity<String> getSong(@PathVariable(value = "id") int id,
                                           @RequestHeader("Accept") String acceptHeader, @RequestHeader(
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


          return new ResponseEntity<String>("Not Supported Ýet4", HttpStatus.NOT_FOUND);

     }


     //-----------HTTP POST-----------//


     /**
      * Posting new Lyrics, new Lyrics will be saved to the local file system using
      * the {@link LyricService} and the LyricsRepository
      *
      * @param lyric
      *         the new lyrics
      * @param request
      *
      * @return A ResponseEntity containing the HTTP Status and a message
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
          return new ResponseEntity<>("The Lyrics are saved, yo can access them under \\lyrics\\" + lyric.getSongTitle(), HttpStatus.CREATED);

     }


     //-----------HTTP DELETE -----------//


     /**
      * Deletes lyrics by the title of the song
      *
      * @param songName
      *         the name/ titel of the song
      * @param acceptHeader
      *         accept media type
      * @param authorization
      *         the users auth token
      *
      * @return ResponseEntity
      *         with a String message and the HTTP Status
      */
     @DeleteMapping(value = "/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
     public ResponseEntity<String> deleteByName(@PathVariable(value = "title") String songName, @RequestHeader(
             "Accept") String acceptHeader, @RequestHeader("Authorization") String authorization)
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
                    return new ResponseEntity("The Lyrics for the song " + songName + " was deleted", HttpStatus.OK);
               case 1:
                    return new ResponseEntity("The lyrics for the song " + songName + "doesnt exist",
                            HttpStatus.BAD_REQUEST);
               case 2:
                    return new ResponseEntity("Couldt delete The Lyrics for the Song" + songName,
                            HttpStatus.INTERNAL_SERVER_ERROR);
               default:
                    return new ResponseEntity("Couldt delete The Lyrics for the Song" + songName,
                            HttpStatus.INTERNAL_SERVER_ERROR);
          }
     }


     /**
      * Deletes Lyrics by their id
      *
      * @param id
      *         The song/lyrics id
      * @param acceptHeader
      *         Accept (should be JSON)
      * @param authorization
      *         The users auth token
      *
      * @return A ResponseEntity containing the HTTP status and a message
      */
     @DeleteMapping(value = "id//{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
     public ResponseEntity<String> deleteByID(@PathVariable(value = "id") int id,
                                              @RequestHeader("Accept") String acceptHeader, @RequestHeader(
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

          String songName = songRestTemplateWrapper.requestSongNameById(id);

          int result = lyricService.deleteLyrics(songName);

          switch (result)
          {
               case 0:
                    return new ResponseEntity("The Lyrics for the song " + songName + " was deleted", HttpStatus.OK);
               case 1:
                    return new ResponseEntity("The lyrics for the song " + songName + "doesnt exist",
                            HttpStatus.BAD_REQUEST);
               case 2:
                    return new ResponseEntity("Couldt delete The Lyrics for the Song" + songName,
                            HttpStatus.INTERNAL_SERVER_ERROR);
               default:
                    return new ResponseEntity("Couldt delete The Lyrics for the Song" + songName,
                            HttpStatus.INTERNAL_SERVER_ERROR);
          }
     }
}
