package htwb.ai.willi.controller;

import htwb.ai.willi.enitity.Song;
import htwb.ai.willi.service.RestTemplateWrapper;
import htwb.ai.willi.service.SongService;
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
import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;


/**
 * Controller for /songs
 * <p>
 * Lets the user add, get delete and update songs from the database
 */
@RestController
@RequestMapping(value = "")
@Slf4j
public class SongController
{


     //-----------INSTANCE VARIABLES -----------//

     /**
      * The SongsService
      */
     @Autowired
     private SongService songService;

     /**
      * A RestTemplate wrapper
      * to send requests to the user-service
      */
     @Autowired
     private RestTemplateWrapper restTemplateWrapper;

     /**
      * the logger
      */
     private Logger log = LoggerFactory.getLogger(SongController.class);



     //-----------CONSTRUCTORS -----------//


     @Autowired
     public SongController(RestTemplateWrapper restTemplateWrapper)
     {
          this.restTemplateWrapper = restTemplateWrapper;
     }


     //-----------HTTP MAPPINGS -----------//


     //-----------HTTP GET -----------//

     /**
      * HTTP GET
      * Returns all songs
      * <p>
      * (GET http://localhost:8080/songsWS-WiMi/rest/songs )
      *
      * @return
      */
     @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
     public ResponseEntity<String> getAll(@RequestHeader("Authorization") String authorization)
     {
          log.info("getAll: Called");


          // Check user token

          String userIDForGivenAuthorizationToken = "";
          try
          {
               userIDForGivenAuthorizationToken = restTemplateWrapper.authenticateUser(authorization);
          }
          catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc)
          {
               return new ResponseEntity("Not a valid authorization token :  " + authorization,
                       HttpStatus.UNAUTHORIZED);
          }


          //Getting the Songs from the Repository


          ArrayList<Song> songs = songService.getAllSongs();


          //Response


          if (songs != null && songs.size() > 0)
          {
               log.info("get All: Retrieved songs from database : " + songs.size() + " entries");
               return new ResponseEntity(songs, HttpStatus.OK);
          }
          else if (songs != null && songs.size() == 0)
          {
               log.info("get All: No songs in database");
               return new ResponseEntity(songs, HttpStatus.OK);
          }

          log.error("getAll No songs found");
          return new ResponseEntity<>("No Songs in the Database", HttpStatus.BAD_REQUEST);

     }


     /**
      * HTTP GET
      * Returns all songs
      * <p>
      * (GET http://localhost:8080/songsWS-WiMi/rest/songs )
      *
      * @return
      */
     @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
     public ResponseEntity<String> getSong(@PathVariable(value = "id") Integer id, @RequestHeader("Accept") String acceptHeader, @RequestHeader("Authorization") String authorization)
     {
          log.info("getSong: Called with id " + id);


          // Check user token


          String userIDForGivenAuthorizationToken = "";
          try
          {
               userIDForGivenAuthorizationToken = restTemplateWrapper.authenticateUser(authorization);
          }
          catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc)
          {
               return new ResponseEntity("Not a valid authorization token :  " + authorization,
                       HttpStatus.UNAUTHORIZED);
          }


          // Response


          if (id < 0)
          {
               return new ResponseEntity<>("The ID needs to be positive", HttpStatus.BAD_REQUEST);
          }

          Optional<Song> optSong = songService.getSongById(id);


          if (optSong.isPresent())
          {
               return new ResponseEntity(optSong.get(), HttpStatus.OK);
          }
          return new ResponseEntity<String>("No Song with ID " + id, HttpStatus.NOT_FOUND);
     }


     /**
      * HTTP GET
      * The Title of the song with the id.
      * If the song does exist returns HTTP NOT_FOUND
      *
      * This is used by the Lyrics server, to verify that a song exist
      *
      * @return
      */
     @GetMapping(value = "verify/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
     public ResponseEntity<String> getIdByTitle(@PathVariable(value = "id") Integer id, @RequestHeader("Accept") String acceptHeader)
     {
          log.info("getIdByTitle: Called with id " + id);


          // Response

          Optional<Song> optSong = songService.getSongById(id);


          if (optSong.isPresent())
          {
               return new ResponseEntity(optSong.get().getTitle(), HttpStatus.OK);
          }
          return new ResponseEntity<String>("No Song with ID " + id, HttpStatus.NOT_FOUND);
     }



     //-----------HTTP DELETE-----------//


     @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
     public ResponseEntity<String> deleteSong(@PathVariable(value = "id") Integer id, @RequestHeader("Authorization") String authorization)
     {
          log.info("deleteSong: Called with ID : " + id);


          // Check user token


          String userIDForGivenAuthorizationToken = "";
          try
          {
               userIDForGivenAuthorizationToken = restTemplateWrapper.authenticateUser(authorization);
          }
          catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc)
          {
               return new ResponseEntity("Not a valid authorization token :  " + authorization,
                       HttpStatus.UNAUTHORIZED);

          }



          if (id < 0)
          {
               log.error("deleteSong: ID was negative");
               return new ResponseEntity<>("ID cant be less than 0. Your ID was : " + id, HttpStatus.BAD_REQUEST);
          }


          Optional<Song> optSong = songService.getSongById(id);

          if (optSong.isEmpty())
          {
               log.error("deleteSong: Song with id " + id + " doesnt exist");
               return new ResponseEntity<>("No song with ID '" + id + "' exists.", HttpStatus.NOT_FOUND);
          }


          log.info("deleteSong: Found Song to delete : " + optSong.get().getTitle());


          songService.deleteSong(id);
          return new ResponseEntity<>("Song with ID '" + id + "' was deleted.", HttpStatus.NO_CONTENT);
     }


     //-----------HTTP POST-----------//


     /**
      * HTTP POST
      * <p>
      * Adding new Song to the database
      *
      * @param song
      *         the Song POJO
      * @param request
      *
      * @return
      */
     @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
     public ResponseEntity<String> postSong(@RequestBody Song song, HttpServletRequest request, @RequestHeader("Authorization") String authorization)
     {
          log.info("postSong: Called with new song : " + song.getTitle());


          // Check user token


          String userIDForGivenAuthorizationToken = "";
          try
          {
               userIDForGivenAuthorizationToken = restTemplateWrapper.authenticateUser(authorization);
          }
          catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc)
          {
               return new ResponseEntity("Not a valid authorization token :  " + authorization,
                       HttpStatus.UNAUTHORIZED);
          }


          // Response


          if (song.getTitle() == null || song.getTitle().equals(""))
          {
               log.error("postSong: Song without title");
               return new ResponseEntity<>("Wrong body: no title", HttpStatus.BAD_REQUEST);
          }


          songService.addSong(song);
          URI location = URI.create(request.getRequestURI() + "/" + song.getId());
          return ResponseEntity.created(location).body(null);

     }


     //-----------HTTP PUT-----------//


     @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
     public ResponseEntity<String> putSong(@PathVariable(value = "id") Integer id, @RequestBody Song song, @RequestHeader("Authorization") String authorization)
     {


          // Check user token


          String userIDForGivenAuthorizationToken = "";
          try
          {
               userIDForGivenAuthorizationToken = restTemplateWrapper.authenticateUser(authorization);
          }
          catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc)
          {
               return new ResponseEntity("Not a valid authorization token :  " + authorization, HttpStatus.UNAUTHORIZED);
          }


          // Response


          if (id != song.getId())
          {
               return new ResponseEntity<>("URL ID doesnt match payload ID.", HttpStatus.BAD_REQUEST);
          }

          if (song.getTitle().equals("") || song.getTitle() == null)
          {
               return new ResponseEntity<>("Wrong body: title is null or has no declaration.", HttpStatus.BAD_REQUEST);
          }


          songService.updateSong(song);

          return new ResponseEntity<String>("Song with ID '" + song.getId() + "' was updated.", HttpStatus.NO_CONTENT);

     }


     //-----------OTHERS -----------//


}


