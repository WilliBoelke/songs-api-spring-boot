package htwb.ai.WiMi.controller;

import htwb.ai.WiMi.Marshalling.Marshaller;
import htwb.ai.WiMi.logger.Log;
import htwb.ai.WiMi.model.dao.SongsDAOInterface;
import htwb.ai.WiMi.model.database.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;


/**
 * Controller for /songs
 *
 * Lets the user add, get delete and update songs from the database
 */
@RestController
@RequestMapping(value = "songs")
public class SongsController
{

    //-----------INSTANCE VARIABLES  -----------//


    /**
     * TAG for the logger
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * Implementation of the songsDAOInterface
     * in production : SongDAO
     * In tests : TestSongDAO
     */
    @Autowired
    private final SongsDAOInterface songsDAO;

    @Autowired
    private final Marshaller<Song> jsonMarshaller;

    @Autowired
    private final Marshaller<Song> xmlMarshaller;


    //-----------CONSTRUCTORS  -----------//


    public SongsController (SongsDAOInterface songsDAOImplementation, Marshaller<Song> jsonMarshaller, Marshaller<Song> xmlMarshaller)
    {
        Log.deb(TAG, "Constructor", "Called");
        this.songsDAO = songsDAOImplementation;
        this.jsonMarshaller =jsonMarshaller;
        this.xmlMarshaller = xmlMarshaller;
    }




    //-----------GET  -----------//

    //-----------GET  -----------//


    /**
     * HTTP GET
     * Returns all songs
     *
     *(GET http://localhost:8080/songsWS-WiMi/rest/songs )
     * @return
     */
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> getAll(@RequestHeader("Accept") String acceptHeader)
    {
        Log.deb(TAG, "getAll", "Called");

        //Getting the Songs from the DAO
        ArrayList<Song> songs = (ArrayList<Song>) songsDAO.getAllSongs();


        // "Building" the response string based on the accept header
        String ans = "";
        if(acceptHeader.contains("application/xml"))
        {
           ans = xmlMarshaller.marshal(songs);
        }
        else if (acceptHeader.contains("application/json"))
        {
            ans  = jsonMarshaller.marshal(songs);
            Log.deb(TAG, "getAll", "json = " + ans);
        }
        else
        {
            Log.deb(TAG, "getAll", "Wrong Header");
        }


        if (songs != null && songs.size() > 0)
        {
            Log.deb(TAG, "getAll", "Retrieved songs from database : " + songs.size() + " entries");
            return new ResponseEntity<String>(ans, HttpStatus.OK);
        }
        else if (songs != null && songs.size() == 0)
        {
            Log.deb(TAG, "getAll", "No songs in database");
            return new ResponseEntity<String>(ans, HttpStatus.NOT_FOUND);
        }


        Log.err(TAG, "getAll", "No songs found");
        return new ResponseEntity<String>(ans, HttpStatus.BAD_REQUEST);
    }



    /**
     * HTTP GET
     * Returns all songs
     *
     *(GET http://localhost:8080/songsWS-WiMi/rest/songs )
     * @return
     */
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> getSong(@PathVariable(value = "id") Integer id, @RequestHeader("Accept") String acceptHeader)
    {
        Log.deb(TAG, "getSong", "Called with id " + id);


        if (id < 0)
        {
            return new ResponseEntity<>("The ID needs to be positive", HttpStatus.BAD_REQUEST);
        }


        Song song = songsDAO.getSongById(id);

        // "Building" the response string based on the accept header
        String ans = "";
        if(acceptHeader.contains("application/xml"))
        {
            ans = xmlMarshaller.marshal(song);
        }
        else if (acceptHeader.contains("application/json"))
        {
            ans  = jsonMarshaller.marshal(song);
            Log.deb(TAG, "getAll", "json = " + ans);
        }
        else
        {
            Log.deb(TAG, "getAll", "Wrong Header");
        }


        if (song != null)
        {
            return new ResponseEntity<String>(ans, HttpStatus.OK);
        }
        return new ResponseEntity<String>(ans, HttpStatus.NOT_FOUND);
    }



    //-----------DELETE -----------//


    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteSong(@PathVariable(value = "id") Integer id)
    {
        Log.deb(TAG, "deleteSong", "Called with ID : " + id);



        if (id < 0)
        {
            Log.err(TAG, "deleteSong", "ID was negative");
            return new ResponseEntity<>("ID cant be less than 0. Your ID: " + id, HttpStatus.BAD_REQUEST);
        }


        Song song = songsDAO.getSongById(id);

        if (song == null)
        {
            Log.err(TAG, "deleteSong", "Song with id " + id + " doesnt exist");
            return new ResponseEntity<>("No song with ID '" + id + "' exists.", HttpStatus.NOT_FOUND);
        }


        Log.deb(TAG, "deleteSong", "Found Song to delete : " + song.getTitle());


        songsDAO.deleteSong(song);
        return new ResponseEntity<>("Song with ID '" + id + "' was deleted.", HttpStatus.NO_CONTENT);
    }



    //-----------POST -----------//


    /**
     * HTTP POST
     *
     * Adding new Song to the database
     *
     * @param songString the Song as JSOn String
     *
     * @param request
     * @return
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postSong(@RequestBody String songString, HttpServletRequest request)
    {
        Log.deb(TAG, "postSong", "Called with new song : " + songString);


        Song song = jsonMarshaller.unmarshal(songString, Song.class);


        if (song.getTitle() == null || song.getTitle().equals(""))
        {
            Log.err(TAG, "postSong", "Song without title");
            return new ResponseEntity<>("Wrong body: no title", HttpStatus.BAD_REQUEST);
        }


        songsDAO.addSong(song);
        URI location = URI.create(request.getRequestURI() + "/" + song.getId());
        return ResponseEntity.created(location).body(null);
    }



    //-----------PUT -----------//


    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> putSong(@PathVariable(value = "id") Integer id, @RequestBody Song song)
    {


        if (id != song.getId())
        {
            return new ResponseEntity<>("URL ID doesnt match payload ID.", HttpStatus.BAD_REQUEST);
        }

        if (song.getTitle().equals("") || song.getTitle() == null)
        {
            return new ResponseEntity<>("Wrong body: title is null or has no declaration.", HttpStatus.BAD_REQUEST);
        }


        songsDAO.updateSong(song);

        return new ResponseEntity<String>("Song with ID '" + song.getId() + "' was updated.", HttpStatus.NO_CONTENT);

    }
}




