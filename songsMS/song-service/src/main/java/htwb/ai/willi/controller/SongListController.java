package htwb.ai.willi.controller;

import htwb.ai.willi.enitity.Song;
import htwb.ai.willi.enitity.SongList;
import htwb.ai.willi.service.RestTemplateWrapper;
import htwb.ai.willi.service.SongListService;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Rest controller for Song Lists
 */
@RestController
@RequestMapping(value = "/playlist")
@Slf4j
public class SongListController
{


     //-----------INSTANCE VARIABLES-----------//


     /**
      * The Logger
      */
     private Logger log = LoggerFactory.getLogger(SongListController.class);

     /**
      * The SongListService
      */
     @Autowired
     private SongListService songListService;

     /**
      * The SongService
      */
     @Autowired
     private SongService songService;

     /**
      * A RestTemplateTemplateWrapper
      * to send requests to the user service
      */
     @Autowired

     private RestTemplateWrapper restTemplateWrapper;


     //-----------CONSTRUCTORS  -----------//


     public SongListController()
     {
     }


     //-----------HTTP MAPPINGS -----------//


     //-----------HTTP GET -----------//


     /**
      * Returns the {@link SongList} as JSON with id, when the user is authorized or the playlist is public
      *
      * @param id
      *         the ID of the SongList
      * @param authorization
      *         The users Auth Token
      *
      * @return
      */
     @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
     public ResponseEntity<SongList> getById(@PathVariable(value = "id") int id, @RequestHeader("Authorization") String authorization)
     {
          log.info("getById. Called from user " + authorization + ", with id: " + id);


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


          //Checking the Id of the songList


          if (id < 0)
          {
               log.error("getById: The ID for the songlist was null");
               return new ResponseEntity("You need to set an ID ", HttpStatus.BAD_REQUEST);
          }


          // Getting the SongList from the database, checking if the user is authorized and the returning it ...or not


          if (songListService.findById(id).isPresent())
          {
               SongList songs = songListService.findById(id).get();
               if (songs != null)
               {

                    log.info("getById. Found a matching songList in the DB");
                    if (songs.getOwnerId().equals(userIDForGivenAuthorizationToken))
                    {
                         log.info("getById. The SongList owner matches the authenticated user ");
                         log.info("getById. Request successfully finished");
                         return new ResponseEntity<>(songs, HttpStatus.ACCEPTED);
                    }
                    else
                    {
                         log.info("getById. The SongList owner doesnt match  the authenticated user ");
                         if (songs.getIsPrivate())
                         {
                              log.error("getById: The SongList is not public");
                              return new ResponseEntity("you arent the owner of the requested list, and the " +
                                      "requested" + " list is not public", HttpStatus.FORBIDDEN);
                         }
                         else
                         {
                              log.info("getById. The SongList is  public");
                              log.info("getById. Request successfully finished");
                              return new ResponseEntity<>(songs, HttpStatus.ACCEPTED);
                         }
                    }
               }
          }
          return new ResponseEntity("The list with the ID " + id + " was empty", HttpStatus.NOT_FOUND);
     }


     @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
     public ResponseEntity<List<SongList>> getAll(@RequestParam(value = "userId") String userId, @RequestHeader("Authorization") String authorization)
     {
          log.info("getAll: Called from user " + authorization);

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


          log.info("getAll: User exists, getting songLists from user");
          List<SongList> songs = songListService.getSongListsFromUser(userId);


          if (songs.size() == 0)
          {
               return new ResponseEntity("There are no Playlists for user " + userId, HttpStatus.NOT_FOUND);
          }

          if (userIDForGivenAuthorizationToken.equals(userId))
          {
               return new ResponseEntity<>(songs, HttpStatus.ACCEPTED);
          }
          else
          {
               List<SongList> returnList = new LinkedList<>();
               for (SongList songList : songs)
               {
                    if (!songList.getIsPrivate())
                    {
                         returnList.add(songList);
                    }
               }
               return new ResponseEntity<>(returnList, HttpStatus.ACCEPTED);
          }
     }


     //-----------HTTP POST-----------//


     /**
      * POST /rest/songList
      * <p>
      * Adding a new songList do the Database
      *
      * @param songList
      * @param request
      * @param authorization
      *
      * @return
      */
     @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
     public ResponseEntity<String> postSongList(@RequestBody SongList songList, HttpServletRequest request,
                                                @RequestHeader("Authorization") String authorization)
     {
          log.info("postSongList: Called with user " + authorization);


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


          if (songList.getSongList() == null || songList.getSongList().isEmpty())
          {
               log.error("postSongList: The JSON contained an empty songList");
               return new ResponseEntity<>("The songlist in the JSON was empty, it needs to contain at least one " +
                       "Song" + " ", HttpStatus.BAD_REQUEST);
          }


          log.info("postSongList: Proceeding to add the new songList...");
          Set<Song> songsFromPayload = songList.getSongList();
          for (Song song : songsFromPayload)
          {
               log.info("postSongList: Checking Song:  " + song.getTitle() + ", id: " + song.getId());
               Optional<Song> optionalSong = songService.getSongById(song.getId());
               if (optionalSong.isEmpty())
               {
                    log.error("postSongList: Song ID doesnt Exist in Database, ID : " + song.getId());
                    return new ResponseEntity<>("Song not in DB", HttpStatus.BAD_REQUEST);
               }
               Song temp = optionalSong.get();
               if (!song.getTitle().equals(temp.getTitle()) || !song.getArtist().equals(temp.getArtist()) || !song.getLabel().equals(temp.getLabel()) || song.getId() != temp.getId() || song.getReleased() != temp.getReleased())
               {
                    log.error("postSongList: Song with ID " + song.getId() + "doesnt match with the same song " +
                            "(ID) in DB");
                    return new ResponseEntity<>("one of the songs in the list doesnt exit in our Database",
                            HttpStatus.BAD_REQUEST);
               }
               log.info("postSongList: Song Exists :  " + song.getTitle() + ", id: " + song.getId());
          }

          log.info("postSongList: All Songs Checked , Adding to DB");

          songList.setUser(userIDForGivenAuthorizationToken);
          songListService.addSongList(songList);
          log.info("postSongList: Request successfully finished");
          URI location = URI.create(request.getRequestURI() + "/" + songList.getId());
          return ResponseEntity.created(location).body(null);
     }


     //-----------HTTP DELETE -----------//


     @DeleteMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
     public ResponseEntity<String> deleteSongList(@PathVariable(value = "id") Integer id, @RequestHeader(
             "Authorization") String authorization)
     {


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
               return new ResponseEntity<>("ID cant be less than 0. Your ID: " + id, HttpStatus.BAD_REQUEST);
          }


          if (songListService.findById(id).isPresent())
          {

               SongList songList = songListService.findById(id).get();
               if (songList != null)
               {
                    if (userIDForGivenAuthorizationToken.equals(songList.getOwnerId()))
                    {
                         songListService.deleteList(songList);
                         return new ResponseEntity<>("Playlist with ID '" + id + "' was deleted.", HttpStatus.ACCEPTED);
                    }
               }
               return new ResponseEntity<>("You arent the owner of the SongList", HttpStatus.FORBIDDEN);
          }
          return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

     }

     //-----------HTTP PUT -----------//


     /**
      * PUT /rest/songList
      * <p>
      * Updating a songList
      *
      * @param songList
      * @param request
      * @param authorization
      *
      * @return
      */
     @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
     public ResponseEntity<String> updateSongList(@PathVariable(value = "id") Integer id, @RequestBody SongList songList, HttpServletRequest request, @RequestHeader("Authorization") String authorization)
     {
          log.info("updateSongList: Called with user " + authorization);


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


          if (songList.getSongList() == null || songList.getSongList().isEmpty())
          {
               log.error("updateSongList: The JSON contained an empty songList");
               return new ResponseEntity<>("The songlist in the JSON was empty, it needs to contain at least one " + "Song" + " ", HttpStatus.BAD_REQUEST);
          }

          Optional<SongList> songListFromDatabase = songListService.findById(id); // Search for name


          if (!songListFromDatabase.isPresent())
          {
               return new ResponseEntity<>("The songlist with the id doesnt exist ", HttpStatus.BAD_REQUEST);
          }
          if (false == songListFromDatabase.get().getOwnerId().trim().equals(userIDForGivenAuthorizationToken.trim()))
          {
               log.error("updateSongList:  owner missmatch actual : " + songList.getOwnerId() + " given : "  + userIDForGivenAuthorizationToken );
               return new ResponseEntity<>("You arent the owner of the Playlist, you arent allow to make any changes", HttpStatus.UNAUTHORIZED);
          }


          log.info("updateSongList: Proceeding to add the new songList...");
          Set<Song> songsFromPayload = songList.getSongList();
          for (Song song : songsFromPayload)
          {
               log.info("updateSongList: Checking Song:  " + song.getTitle() + ", id: " + song.getId());
               Optional<Song> optionalSong = songService.getSongById(song.getId());
               if (optionalSong.isEmpty())
               {
                    log.error("updateSongList: Song ID doesnt Exist in Database, ID : " + song.getId());
                    return new ResponseEntity<>("Song not in DB", HttpStatus.BAD_REQUEST);
               }
               Song temp = optionalSong.get();
               if (!song.getTitle().equals(temp.getTitle()) || !song.getArtist().equals(temp.getArtist()) || !song.getLabel().equals(temp.getLabel()) || song.getId() != temp.getId() || song.getReleased() != temp.getReleased())
               {
                    log.error("updateSongList: Song with ID " + song.getId() + "doesnt match with the same song " +
                            "(ID) in DB");
                    return new ResponseEntity<>("one of the songs in the list doesnt exit in our Database",
                            HttpStatus.BAD_REQUEST);
               }
               log.info("updateSongList: Song Exists :  " + song.getTitle() + ", id: " + song.getId());
          }


          log.info("updateSongList: All Songs Checked , Adding to DB");

          songList.setUser(userIDForGivenAuthorizationToken);
          songListService.updateSongList(songList, songListFromDatabase.get());
          log.info("updateSongList: Request successfully finished");
          URI location = URI.create(request.getRequestURI() + "/" + songList.getId());
          return ResponseEntity.created(location).body("Song List was updated");
     }
}