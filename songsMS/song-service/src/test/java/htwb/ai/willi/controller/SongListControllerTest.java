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
     public void getAll() throws Exception
     {


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
                  mockMvc.perform(get("/playlist/1").header(HttpHeaders.AUTHORIZATION, invalidToken))
                          .andExpect(status().is(401)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();

          //Verify Result
          Assert.assertEquals("Not a valid authorization token :  invalidToken", result.getResponse().getContentAsString());
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
          Assert.assertEquals( "mmuster", songList.getOwnerId());
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

}
