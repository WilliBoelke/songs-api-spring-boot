package htwb.ai.willi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import htwb.ai.willi.SongServiceApplication;
import htwb.ai.willi.enitity.Song;
import htwb.ai.willi.service.AuthRestTemplateWrapper;
import htwb.ai.willi.service.SongService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SongServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SongControllerTest
{
     @Autowired
     private MockMvc mockMvc;

     @InjectMocks
     @Autowired
     SongController songController;

     @Mock
     private AuthRestTemplateWrapper restTemplateWrapper;

     @Autowired
     SongService songService;

     private String token;

     private String invalidToken;

     private String userID;


     @Before
     public void setup()
     {
          token = "token123";
          invalidToken = "invalidToken";
          userID = "testuser";
          MockitoAnnotations.initMocks(this);
          Mockito.when(restTemplateWrapper.authenticateUser(token)).thenReturn(userID);
          Mockito.when(restTemplateWrapper.authenticateUser(invalidToken)).thenThrow(HttpServerErrorException.class);
          mockMvc = MockMvcBuilders.standaloneSetup(songController).build();
     }





     //-----------GET ALL SONGS-----------//



     /**
      * Get all songs with a valid user authorization token
      * Should Return 2 songs from the H2 Database
      * and Http ok / 200
      *
      * @throws Exception
      */
     @Test
     public void getAllSongs() throws Exception
     {
          //Request
          MvcResult result =
                  mockMvc.perform(get("/").header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(200)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();

          //Verify Result
          String content = (result.getResponse().getContentAsString());
          List<Song> songs = Arrays.asList(new ObjectMapper().readValue(content, Song[].class));
          Assert.assertTrue(songs.size() > 0);
          Assert.assertEquals(songs.size(), 4);
     }



     /**
      * Get all songs with a valid user authorization token
      * So SOngs, but the message that the token was invalid
      * and Http Unauthorized / 401
      *
      * @throws Exception
      */
     @Test
     public void getAllSongsInvalidAuthorization() throws Exception
     {
          //Request
          MvcResult result =
                  mockMvc.perform(get("/").header(HttpHeaders.AUTHORIZATION, invalidToken))
                          .andExpect(status().is(401)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();

          //Verify Result
          Assert.assertEquals("Not a valid authorization token :  invalidToken", result.getResponse().getContentAsString());
     }





     //-----------GET SONG BY ID-----------//



     /**
      * Get Song by id with a valid id and authorization token
      * Should return song with id two and Http 200 / OK
      * @throws Exception
      */
     @Test
     public void getSongBId() throws Exception
     {
          //Request
          MvcResult result =
                  mockMvc.perform(get("/2")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(200)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();


          //Verify Result
          Assert.assertEquals("{\"id\":2,\"title\":\"test\",\"artist\":\"test\",\"label\":\"test\",\"released\":1985}", result.getResponse().getContentAsString());
     }



     /**
      * Get Song by id with a valid id but invalid authorization
      * Should return message and Http 401 / Unauthorized
      * @throws Exception
      */
     @Test
     public void getSogByIdInvalidAuth() throws Exception
     {
          //Request
          MvcResult result =
                  mockMvc.perform(get("/2")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, invalidToken))
                          .andExpect(status().is(401)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();


          //Verify Result
          Assert.assertEquals("Not a valid authorization token :  invalidToken", result.getResponse().getContentAsString());
     }


     /**
      * Getting a song by an id which doesnt exist
      * we have the ids 1 and 2 in the database
      * so lets request id= 3
      *
      * This should return an message and Http 404
      * @throws Exception
      */
     @Test
     public void getSongByIdNotExistingId() throws Exception
     {
          //Request
          MvcResult result =
               mockMvc.perform(get("/7")
                       .accept(MediaType.APPLICATION_JSON)
                       .header(HttpHeaders.AUTHORIZATION, token))
                       .andExpect(status().is(404)).andExpect(content()
                       .contentType(MediaType.APPLICATION_JSON)).andReturn();


          //Verify Result
          Assert.assertEquals("No Song with ID 7", result.getResponse().getContentAsString());
     }


     /**
      * Getting a song by an id < 0
      * @throws Exception
      */
     @Test
     public void getSongByIdWithNegativeId() throws Exception
     {
          //Request
          MvcResult result =
                  mockMvc.perform(get("/-3")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(400)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();


          //Verify Result
          Assert.assertEquals("The ID needs to be positive", result.getResponse().getContentAsString());
     }






     //-----------POST SONG-----------//



     /**
      * Posting a new and valid song
      * The song should be added to the Databse
      * and we can expect a HTTP 201
      * @throws Exception
      */
     @Test
     public void postANewSong() throws Exception
     {
          //The Song to post
          Song song = new Song(6, "Travelling Light", "Leonard Cohen", "Columbia", 2016);

          // Request
          MvcResult response =
                  mockMvc.perform(post("/")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(asJsonString(song))).andExpect(status()
                          .is(201)).andReturn();

          //Verify Result
          String header = response.getResponse().getHeader("location");

          //Request the new song
          MvcResult getterResponse =
                  mockMvc.perform(get("/6")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(200)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();


          //Verify the new song
          Song songInDB = asSong(getterResponse.getResponse().getContentAsString());
          Assert.assertEquals(song.getReleased(), songInDB.getReleased());
          Assert.assertEquals(song.getLabel(), songInDB.getLabel());
          Assert.assertEquals(song.getTitle(), songInDB.getTitle());
          Assert.assertEquals(song.getArtist(), songInDB.getArtist());
     }



     /**
      * The Authorization Token needs to be  valid to post a song
      * if it isnt we expect a Http 401 Unauthorized and a corresponding
      * message
      * @throws Exception
      */
     @Test
     public void postSongWithInvalidAuthorization() throws Exception
     {
          //The Song to post
          Song song = new Song(6, "Travelling Light", "Leonard Cohen", "Columbia", 2016);

          // Request
          MvcResult result =
                  mockMvc.perform(post("/")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, invalidToken)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(asJsonString(song))).andExpect(status()
                          .is(401)).andReturn();

          //Verify Result
          Assert.assertEquals("Not a valid authorization token :  invalidToken", result.getResponse().getContentAsString());
     }



     /**
      * A song needs to have a Title - Always
      * soo lets test the behavior if no title is given
      * we can expect a Http 400 and a corresponding message in the return body
      * @throws Exception
      */
     @Test
     public void postSongWithoutTitle() throws Exception
     {
          //The Song to post
          Song song = new Song(6, "", "Leonard Cohen", "Columbia", 2016);


          MvcResult result =
          mockMvc.perform(post("/")
                  .header(HttpHeaders.AUTHORIZATION, token)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJsonString(song)))
                  .andExpect(status().is(400)).andReturn();

          //Verify Result
          Assert.assertEquals("Wrong body: no title", result.getResponse().getContentAsString());
     }



     /**
      * A Song can be posted without a label
      * @throws Exception
      */
     @Test
     public void postSongWithoutLabel() throws Exception
     {
          //The Song to post
          Song song = new Song(6, "Travelling Light", "Leonard Cohen", "", 2016);

          MvcResult response =
                  mockMvc.perform(post("/")
                          .header(HttpHeaders.AUTHORIZATION, token)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(asJsonString(song))).andExpect(status().is(201)).
                          andReturn();

          //Request the new song
          MvcResult getterResponse =
                  mockMvc.perform(get("/6")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(200)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();


          //Verify the new song
          Song songInDB = asSong(getterResponse.getResponse().getContentAsString());
          Assert.assertEquals(song.getReleased(), songInDB.getReleased());
          Assert.assertEquals(song.getLabel(), songInDB.getLabel());
          Assert.assertEquals(song.getTitle(), songInDB.getTitle());
          Assert.assertEquals(song.getArtist(), songInDB.getArtist());

     }



     /**
      * A Song can be saved without Artist
      * @throws Exception
      */
     @Test
     public void postSongWithoutArtist() throws Exception
     {
          //The Song to post
          Song song = new Song(6, "Travelling Light", "", "Columbia", 2016);

          MvcResult response =
                  mockMvc.perform(post("/")
                          .header(HttpHeaders.AUTHORIZATION, token)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(asJsonString(song))).andExpect(status().is(201)).
                          andReturn();

          //Request the new song
          MvcResult getterResponse =
                  mockMvc.perform(get("/6")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(200)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();


          //Verify the new song
          Song songInDB = asSong(getterResponse.getResponse().getContentAsString());
          Assert.assertEquals(song.getReleased(), songInDB.getReleased());
          Assert.assertEquals(song.getLabel(), songInDB.getLabel());
          Assert.assertEquals(song.getTitle(), songInDB.getTitle());
          Assert.assertEquals(song.getArtist(), songInDB.getArtist());
     }



     //-----------DELETE SONG-----------//


     /**
      * deleting a existing song should work, with the id and a valid
      * Authorization token.
      *
      * We Expect a Http 204 and a corresponding message.
      *
      * We can verify that the song is deleted by using the get method for the same id
      * @throws Exception
      */
     @Test
     public void deleteValidEntry() throws Exception
     {

          //delete entry
          MvcResult result =
                  mockMvc.perform(delete("/3")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(204)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();

          //Verify Result
          Assert.assertEquals("Song with ID '3' was deleted.", result.getResponse().getContentAsString());

          //Request
          MvcResult resultGet =
                  mockMvc.perform(get("/3")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(404)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();


          //Verify Result
          Assert.assertEquals("No Song with ID 3", resultGet.getResponse().getContentAsString());

     }



     /**
      * Deleting a song with a id which doesnt
      * exist should return hTTP 404 and a message
      * @throws Exception
      */
     @Test
     public void deleteInvalidEntry() throws Exception
     {
          //delete entry
          MvcResult result =
                  mockMvc.perform(delete("/20")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(404)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();

          //Verify Result
          Assert.assertEquals("No song with ID '20' exists.", result.getResponse().getContentAsString());
     }


     /**
      *Deleting with negative id
      */
     @Test
     public void deleteWithNegativeID() throws Exception
     {
          //delete entry
          MvcResult result =
                  mockMvc.perform(delete("/-20")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(400)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();

          //Verify Result
          Assert.assertEquals("ID cant be less than 0. Your ID was : -20", result.getResponse().getContentAsString());
     }



     /**
      * Deleting a song With invalid Authorization shouldn't work
      * it should return Http 401 and a message
      * The song should still be there
      */
     @Test
     public void deleteWithInvalidAuthorization() throws Exception
     {
          //delete entry
          MvcResult result =
                  mockMvc.perform(delete("/1")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, invalidToken))
                          .andExpect(status().is(401)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();

          //Verify Result
          Assert.assertEquals("Not a valid authorization token :  invalidToken", result.getResponse().getContentAsString());

          //Request
          MvcResult resultGet =
                  mockMvc.perform(get("/1")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(200)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();


          //Verify Result - song is still there
          Assert.assertTrue(! "No Song with ID 1".equals( resultGet.getResponse().getContentAsString()));
     }



     //-----------PUT SONG-----------//


     /**
      * Changing a song with valid id and auth token should return
      * Http 204 and a message
      * @throws Exception
      */
     @Test
     public void putValidSong() throws Exception
     {
          //Changing Title of  song with id 2
          Song changedSong = Song.builder().withId(2).withTitle("Changed").withArtist("alsoChanged").build();

          MvcResult result =
          mockMvc.perform(put("/2")
                  .accept(MediaType.APPLICATION_JSON)
                  .header(HttpHeaders.AUTHORIZATION, token)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJsonString(changedSong))).andExpect(status().is(204)).andReturn();


          //Verify
          Assert.assertTrue(! "No Song with ID 1".equals( result.getResponse().getContentAsString()));
     }


     /**
      * Mismatching id between url and body should
      * return a HTTP 400 and a message
      * @throws Exception
      */
     @Test
     public void putMismatchingID() throws Exception
     {
          //Changing Title of  song with id 2
          Song changedSong = Song.builder().withId(2).withTitle("Changed").withArtist("alsoChanged").build();

          MvcResult result =
                  mockMvc.perform(put("/31")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(asJsonString(changedSong))).andExpect(status().is(400)).andReturn();

          //Verify Result
          Assert.assertEquals("URL ID doesnt match payload ID.", result.getResponse().getContentAsString());

     }

     /**
      * Mismatching id between url and body should
      * return a HTTP 400 and a message
      * @throws Exception
      */
     @Test
     public void putEmptyName() throws Exception
     {
          //Changing Title of  song with id 2
          Song changedSong = Song.builder().withId(2).withTitle("").withArtist("alsoChanged").build();

          MvcResult result =
                  mockMvc.perform(put("/2")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(asJsonString(changedSong))).andExpect(status().is(400)).andReturn();

          //Verify Result
          Assert.assertEquals("Wrong body: title is null or has no declaration.", result.getResponse().getContentAsString());

     }


     private static String asJsonString(final Object obj)
     {
          try
          {
               return new ObjectMapper().writeValueAsString(obj);
          }
          catch (Exception e)
          {
               throw new RuntimeException(e);
          }
     }



     private Song asSong(String returnedObject)
     {
          try
          {
               return new ObjectMapper().readValue(returnedObject, Song.class);
          }
          catch (Exception e)
          {
               throw new RuntimeException(e);
          }


     }

}


