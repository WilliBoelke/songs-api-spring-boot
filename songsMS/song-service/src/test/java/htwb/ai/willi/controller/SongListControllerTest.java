package htwb.ai.willi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import htwb.ai.willi.SongServiceApplication;
import htwb.ai.willi.enitity.Song;
import htwb.ai.willi.enitity.SongList;
import htwb.ai.willi.service.AuthRestTemplateWrapper;
import htwb.ai.willi.service.SongListService;
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
public class SongListControllerTest
{

     @Autowired
     private MockMvc mockMvc;

     @InjectMocks
     @Autowired
     SongListController songListController;

     @Mock
     private AuthRestTemplateWrapper restTemplateWrapper;

     @Autowired
     SongListService songListService;

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
          mockMvc = MockMvcBuilders.standaloneSetup(songListController).build();
     }



     //-----------GET ALL SONGS-----------//



     /**
      *Get All SongLists
      * Expected 2 Lists from the Database
      *
      * @throws Exception
      */

     /**
      * Get all songs with a valid user authorization token
      * Should Return 2 songs from the H2 Database
      * and Http ok / 200
      *
      * @throws Exception
      */
     @Test
     public void getAllSongsForOwner() throws Exception
     {
          //Request
          MvcResult result =
                  mockMvc.perform(get("/playlist?userId=owner").header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(202)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();

          //Verify Result
          Assert.assertEquals("[{\"id\":2,\"ownerId\":\"owner\",\"name\":\"getAllTest1\",\"isPrivate\":false," +
                  "\"songList\":[]},{\"id\":3,\"ownerId\":\"owner\",\"name\":\"getAllTest2\",\"isPrivate\":false," +
                  "\"songList\":[]},{\"id\":5,\"ownerId\":\"owner\",\"name\":\"getAllTest3\",\"isPrivate\":false," +
                  "\"songList\":[]}]", result.getResponse().getContentAsString());
     }



     /**
      * Get all songs with a valid user authorization token
      * So SOngs, but the message that the token was invalid
      * and Http Unauthorized / 401
      *
      * @throws Exception
      */
     @Test
     public void getAllForOwnerInvalidAuth() throws Exception
     {
          //Request
          MvcResult result =
                  mockMvc.perform(get("/playlist?userId=owner").header(HttpHeaders.AUTHORIZATION, invalidToken))
                          .andExpect(status().is(401)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();

          //Verify Result
          Assert.assertEquals("Not a valid authorization token :  invalidToken", result.getResponse().getContentAsString());
     }


     /**
      * Get all songs with a valid user authorization token
      * So SOngs, but the message that the token was invalid
      * and Http Unauthorized / 401
      *
      * @throws Exception
      */
     @Test
     public void getAllForUnknownIdOrNoPlaylistsForUser() throws Exception
     {
          //Request
          MvcResult result =
                  mockMvc.perform(get("/playlist?userId=unknown").header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(404)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();

          //Verify Result
          Assert.assertEquals("There are no Playlists for user unknown", result.getResponse().getContentAsString());
     }






     //-----------GET SONGLIST BY ID-----------//



     /**
      * Get SonList by id with a valid id and authorization
      * Should return message and Http 202 / Unauthorized
      * And a JSON string which can be mapped into a SongList Pojo
      * @throws Exception
      */
     @Test
     public void getSongBId() throws Exception
     {
          //Request
          MvcResult result =
                  mockMvc.perform(get("/playlist/1").header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(202)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();


          //Verify Result
          String content = (result.getResponse().getContentAsString());
          SongList songList = asSongList(content);
          Assert.assertEquals("testplalist", songList.getName());
          Assert.assertEquals( "testuser", songList.getOwnerId());
          Assert.assertFalse( songList.getIsPrivate());
          Assert.assertEquals(2, songList.getSongList().size());
     }



     /**
      * Get SonList by id with an invalid auth token
      * Should return HTTP 401/ Unauthorized and a message
      *
      * @throws Exception
      */
     @Test
     public void getSongListByIdInvalidAuth() throws Exception
     {
          //Request
          MvcResult result =
                  mockMvc.perform(get("/playlist/1").header(HttpHeaders.AUTHORIZATION, invalidToken))
                          .andExpect(status().is(401)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();


          //Verify Result
          String content = (result.getResponse().getContentAsString());
          Assert.assertEquals("Not a valid authorization token :  invalidToken", content);
     }


     /**
      * Getting a private list from another user should not work
      * and return Http 403 / forbidden and a message
      * here is use the song with the id 6  which is private and owned by "otherUser"
      * not "testUser" who will be returned by the mocked RestTemplateWrapper
      *
      * @throws Exception
      */
     @Test
     public void getSongBIdForNotOwnedPlayList() throws Exception
     {
          //Request
          MvcResult result =
                  mockMvc.perform(get("/playlist/6").header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(403)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();


          //Verify Result
          String content = (result.getResponse().getContentAsString());
          Assert.assertEquals("you arent the owner of the requested list, and the requested list is not public", content);
     }


     //-----------POST NEW SONGLIST-----------//


     private String songListJSON = "{\n" + "    \"ownerId\": \"testuser\",\n" + "\t\"isPrivate\": false,\n" + "\t\"name\": \"TesPlayList\",\n" + "\t\"songList\": [\n" + "\t\t{\n" + "\t\t\t\"id\": 5,\n" + "\t\t\t\"title\": \"We Built This City\",\n" + "\t\t\t\"artist\": \"Starship\",\n" + "\t\t\t\"label\": \"Grunt/RCA\",\n" + "\t\t\t\"released\": 1985\n" + "\t\t},\n" + "\t\t{\n" + "\t\t\t\"id\": 4,\n" + "\t\t\t\"title\": \"Sussudio\",\n" + "\t\t\t\"artist\": \"Phil Collins\",\n" + "\t\t\t\"label\": \"Virgin\",\n" + "\t\t\t\"released\": 1985\n" + "\t\t}\n" + "\t]\n" + "}\n" + " ";

     /**
      * Posting a new SonList should return HTTP
      * and a message.
      * The new SongsList should be saved in the database and
      * can be retrieved through the GET method
      *
      * @throws Exception
      */
     @Test
     public void postAValidNewSongLIst() throws Exception
     {
          //Request
          MvcResult resultPost =
                  mockMvc.perform(post("/playlist")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(songListJSON)).andExpect(status()
                          .is(201)).andReturn();


          //Verify Result
          String content = (resultPost.getResponse().getContentAsString());
          Assert.assertEquals("", content);


         // Getting the List
          MvcResult resultGet =
                  mockMvc.perform(get("/playlist/9").header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(202)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();


          //Verify Result
          String contentGet = (resultGet.getResponse().getContentAsString());
          SongList songList = asSongList(contentGet);
          Assert.assertEquals("TesPlayList", songList.getName());
          Assert.assertEquals( "testuser", songList.getOwnerId());
          Assert.assertFalse( songList.getIsPrivate());
          Assert.assertEquals(2, songList.getSongList().size());
     }


     /**
      * Get SonList by id with an invalid auth token
      * Should return HTTP 401/ Unauthorized and a message
      *
      * @throws Exception
      */
     @Test
     public void postSongListInvalidAuth() throws Exception
     {
          //Request
          MvcResult result =
                  mockMvc.perform(post("/playlist")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, invalidToken)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(songListJSON)).andExpect(status()
                          .is(401)).andReturn();


          //Verify Result
          String content = (result.getResponse().getContentAsString());
          Assert.assertEquals("Not a valid authorization token :  invalidToken", content);
     }

     /**
      * Get SonList by id with an invalid auth token
      * Should return HTTP 401/ Unauthorized and a message
      *
      * @throws Exception
      */
     @Test
     public void postSongListEmptyPlayList() throws Exception
     {


          //Request
          MvcResult result =
                  mockMvc.perform(post("/playlist")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content("       {\n" + "               \"ownerId\": \"user\",\n" + "                  \"isPrivate\": false,\n" + "                  \"name\": \"TesPlayList\",\n" + "                  \"songList\": []\n" + "          }")).andExpect(status()
                          .is(400)).andReturn();


          //Verify Result
          String content = (result.getResponse().getContentAsString());
          Assert.assertEquals("The songlist in the JSON was empty, it needs to contain at least one Song ", content);
     }



     //-----------DELETE SONGLIST-----------//


     /**
      * deleting a existing SongList should work, with the id and a valid
      * Authorization token.
      *
      * We Expect a Http 204 and a corresponding message.
      *
      * We can verify that the SongList is deleted by using the get method for the same id
      * @throws Exception
      */
     @Test
     public void deleteValidEntry() throws Exception
     {

          //delete entry
          MvcResult result =
                  mockMvc.perform(delete("/playlist/4").header(HttpHeaders.AUTHORIZATION, token).accept(MediaType.APPLICATION_JSON))
                          .andExpect(status().is(202)).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

          //Verify Result
          Assert.assertEquals("Playlist with ID '4' was deleted.", result.getResponse().getContentAsString());

          //Request
          MvcResult resultGet =
                  mockMvc.perform(get("/playlist/4")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(404)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();


          //Verify Result
          Assert.assertEquals("The list with the ID 4 was empty", resultGet.getResponse().getContentAsString());

     }

     /**
      * Get SonList by id with an invalid auth token
      * Should return HTTP 401/ Unauthorized and a message
      *
      * @throws Exception
      */
     @Test
     public void deleteSongListInvalidAuth() throws Exception
     {
          //delete entry
          MvcResult result =
                  mockMvc.perform(delete("/playlist/4").header(HttpHeaders.AUTHORIZATION, invalidToken).accept(MediaType.APPLICATION_JSON))
                          .andExpect(status().is(401)).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

          //Verify Result
          String content = (result.getResponse().getContentAsString());
          Assert.assertEquals("Not a valid authorization token :  invalidToken", content);
     }

     /**
      * Getting a SongList by an id which doesnt exist
      * we have the ids 1 and 2 in the database
      * so lets request id= 3
      *
      * This should return an message and Http 404
      * @throws Exception
      */
     @Test
     public void getSongListByIdNotExistingId() throws Exception
     {
          //Request
          MvcResult result =
                  mockMvc.perform(get("/playlist/200")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(404)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();


          //Verify Result
          Assert.assertEquals("The list with the ID 200 was empty", result.getResponse().getContentAsString());
     }

     private SongList asSongList(String returnedObject)
     {
          try
          {
               return new ObjectMapper().readValue(returnedObject, SongList.class);
          }
          catch (Exception e)
          {
               throw new RuntimeException(e);
          }


     }


     //-----------PUT SONGLIST-----------//

     private String updateJSON = "{\n" + "    \"ownerId\": \"testuser\",\n" + "\t\"isPrivate\": false,\n" + "\t\"name\": \"updateTestUpdated\",\n" + "\t\"songList\": [\n" + "\t\t{\n" + "\t\t\t\"id\": 5,\n" + "\t\t\t\"title\": \"We Built This City\",\n" + "\t\t\t\"artist\": \"Starship\",\n" + "\t\t\t\"label\": \"Grunt/RCA\",\n" + "\t\t\t\"released\": 1985\n" + "\t\t},\n" + "\t\t{\n" + "\t\t\t\"id\": 4,\n" + "\t\t\t\"title\": \"Sussudio\",\n" + "\t\t\t\"artist\": \"Phil Collins\",\n" + "\t\t\t\"label\": \"Virgin\",\n" + "\t\t\t\"released\": 1985\n" + "\t\t}\n" + "\t]\n" + "}\n" + " ";

     @Test
     public void updatePlayList() throws Exception
     {

          //delete entry
          MvcResult result1 =
                  mockMvc.perform(put("/playlist/7").header(HttpHeaders.AUTHORIZATION, token).accept(MediaType.APPLICATION_JSON)
                          .content(updateJSON)
                          .contentType(MediaType.APPLICATION_JSON))
                          .andExpect(status().is(201))
                           .andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();

          //Verify Result
          Assert.assertEquals("Song List was updated", result1.getResponse().getContentAsString());

          //Request
          MvcResult resultGet =
                  mockMvc.perform(get("/playlist/8")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(202)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();


          //Verify Result
          Assert.assertTrue(resultGet.getResponse().getContentAsString().contains("updateTestUpdated"));

     }

     @Test
     public void updatePlayListNotOwner() throws Exception
     {
          // Not Owner

          MvcResult result =
                  mockMvc.perform(put("/playlist/2").header(HttpHeaders.AUTHORIZATION, token).accept(MediaType.APPLICATION_JSON)
                          .content(updateJSON)
                          .contentType(MediaType.APPLICATION_JSON))
                          .andExpect(status().is(401))
                          .andExpect(content()
                                  .contentType(MediaType.APPLICATION_JSON)).andReturn();

          //Verify Result
          Assert.assertEquals("You arent the owner of the Playlist, you arent allow to make any changes", result.getResponse().getContentAsString());
     }


     @Test
     public void updatePlayListWrongToken() throws Exception
     {

          MvcResult result =
                  mockMvc.perform(put("/playlist/8").header(HttpHeaders.AUTHORIZATION, invalidToken).accept(MediaType.APPLICATION_JSON)
                          .content(updateJSON)
                          .contentType(MediaType.APPLICATION_JSON))
                          .andExpect(status().is(401))
                          .andExpect(content()
                                  .contentType(MediaType.APPLICATION_JSON)).andReturn();
     }

     @Test
     public void updatePlayListNotExisting() throws Exception
     {

          MvcResult result =
                  mockMvc.perform(put("/playlist/200").header(HttpHeaders.AUTHORIZATION, token).accept(MediaType.APPLICATION_JSON)
                          .content(updateJSON)
                          .contentType(MediaType.APPLICATION_JSON))
                          .andExpect(status().is(400))
                          .andExpect(content()
                                  .contentType(MediaType.APPLICATION_JSON)).andReturn();

          //Verify Result
          Assert.assertEquals("The songlist with the id doesnt exist ", result.getResponse().getContentAsString());
     }



}
