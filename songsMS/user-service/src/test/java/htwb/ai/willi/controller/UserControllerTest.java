package htwb.ai.willi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import htwb.ai.willi.UserServiceApplication;
import htwb.ai.willi.enity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Integration tests for the user Service using a HSQL2 in-memory-database
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest
{

     @Autowired
     MockMvc mockMvc;

     private static String token;


     //-----------User Authentication -----------//


     @Test
     public void successfulAuthentication() throws Exception
     {
          User user = new User("mmuster", "pass1234", "Maxime", "Muster");
          MvcResult result =
                  mockMvc.perform(post("/auth").contentType(MediaType.APPLICATION_JSON).content(asJsonString(user))).andReturn();

          String body = result.getResponse().getContentAsString();
          System.out.println(body);
          token = body;
          Assert.assertFalse(token.length() < 20);
          Assert.assertFalse(token.length() > 20);
          Assert.assertEquals(20, token.length());

     }

     @Test
     public void wrongPassword() throws Exception
     {
          User user = new User("mmuster", "pass134", "Maxime", "Muster");
          mockMvc.perform(post("/auth").contentType(MediaType.APPLICATION_JSON).content(asJsonString(user))).andExpect(status().is(401));
     }


     @Test
     public void wrongUserId() throws Exception
     {
          User user = new User("muster", "pass1234", "Maxime", "Muster");
          mockMvc.perform(post("/auth/").contentType(MediaType.APPLICATION_JSON).content(asJsonString(user))).andExpect(status().is(401));
     }


     //-----------Get user by Token -----------//


     /**
      * Generate a new Token for UserTwo (in the Database)
      * use that Token to get his userId.
      */
     @Test
     public void getUserIdByNewToken() throws Exception
     {
          //Authenticate
          User user = new User("userTwo", "pass1234", "User", "Two");
          MvcResult result1 =
                  mockMvc.perform(post("/auth").contentType(MediaType.APPLICATION_JSON).content(asJsonString(user))).andReturn();

          String body = result1.getResponse().getContentAsString();
          System.out.println(body);
          String userTwoToken = body;


          //Getting User Id
          MvcResult result2 =
                  mockMvc.perform(get("/auth/" + userTwoToken).contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(200)).andReturn();

          String username = result2.getResponse().getContentAsString();

          //Checking User Id
          Assert.assertEquals("userTwo", username);

     }

     @Test
     public void getUserNameByToken() throws Exception
     {
          MvcResult result =
                  mockMvc.perform(get("/auth/" + token).contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(200)).andReturn();

          String username = result.getResponse().getContentAsString();
          Assert.assertEquals("mmuster", username);
     }


     /**
      * Should return HTTp Unauthorized / 401
      *
      * @throws Exception
      */
     @Test
     public void getUserIDWithWrongToken() throws Exception
     {
          mockMvc.perform(get("/auth/" + "wrongToken123").contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(401)).andReturn();
     }


     private String asJsonString(final Object obj)
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


}
