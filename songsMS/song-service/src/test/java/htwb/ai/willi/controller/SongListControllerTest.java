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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SongServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SongListControllerTest
{
     /*8
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
     /**@Test
     public void getAllSongs() throws Exception
     {
          //Request
          MvcResult result =
                  mockMvc.perform(get("/songLists")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token)
                          .header("userId", "mmuster"))
                          .andExpect(status()
                                  .is(400))
                          .andExpect(content()
                                .contentType(MediaType.APPLICATION_JSON)).andReturn();

          //Verify Result
          String content = (result.getResponse().getContentAsString());
          System.out.println(content + "-----------------------------");
          List<SongList> songs = Arrays.asList(new ObjectMapper().readValue(content, SongList[].class));
          Assert.assertTrue(songs.size() > 0);
          Assert.assertEquals(songs.size(), 2);
     }



     /**
      * Get all songs with a valid user authorization token
      * So SOngs, but the message that the token was invalid
      * and Http Unauthorized / 401
      *
      * @throws Exception
      */
     /**
     @Test
     public void getAllSongsInvalidAuthorization() throws Exception
     {
          //Request
          MvcResult result =
                  mockMvc.perform(get("/songLists/1").header(HttpHeaders.AUTHORIZATION, invalidToken))
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
     /**
     @Test
     public void getSongBId() throws Exception
     {
          //Request
          MvcResult result =
                  mockMvc.perform(get("/songs/2")
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
     /**
     @Test
     public void getSogByIdInvalidAuth() throws Exception
     {
          //Request
          MvcResult result =
                  mockMvc.perform(get("/songs/2")
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
     /**
     @Test
     public void getSongByIdNotExistingId() throws Exception
     {
          //Request
          MvcResult result =
                  mockMvc.perform(get("/songs/3")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token))
                          .andExpect(status().is(404)).andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON)).andReturn();


          //Verify Result
          Assert.assertEquals("No Song with ID 3", result.getResponse().getContentAsString());
     }
*/



}
