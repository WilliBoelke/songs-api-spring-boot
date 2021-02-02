package htwb.ai.WiMi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import htwb.ai.WiMi.Marshalling.GsonWrapper;
import htwb.ai.WiMi.model.dao.SessionDAO;
import htwb.ai.WiMi.model.database.User;
import htwb.ai.WiMi.testImplementation.TestUserDAO;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Units tests for the {@link UsersController}
 *
 */
class UsersControllerTest
{
    /**
     * The Spring MVC mock
     */
    private MockMvc mockMvc;
    /**
     * The test user equals the user in {@link TestUserDAO}
     * it will be changed for some tests
     */
    private User testUser;

    @BeforeEach
    public void setup()
    {
        testUser = new User("42","geheim","TestUser","nachname");

        mockMvc = MockMvcBuilders.standaloneSetup(
                new UsersController(new TestUserDAO(),new SessionDAO(), new GsonWrapper<User>())).build();
    }



    //-----------Post -----------//



    /**
     * POST
     *
     * "logIn" with an existing user returns Http.OK (200) and the generated token
     * The token length should be <= 17
     *
     * @throws Exception
     */
    @Test
    void postUser() throws Exception
    {

        MvcResult result = mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testUser)))
                .andExpect(status().isOk())
                .andReturn();

        //Token <= 17
        String token = result.getResponse().getContentAsString();
        Assert.assertTrue(token.length() <= 20);
    }

    @Test
    void verifyTokenChangeAndLength() throws Exception
    {
        String oldToken = ""; // Token of the last iteration

        for(int i = 0; i < 20;  i++)
        {
            MvcResult result = mockMvc.perform(post("/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(testUser)))
                    .andExpect(status().isOk())
                    .andReturn();

            //Token <= 20
            String token = result.getResponse().getContentAsString();
            System.out.println(token);

            //Token not "to long"
            Assert.assertTrue(token.length() <= 20);

            //Token changes with new POST
            Assert.assertNotEquals(oldToken, token);

        }
    }


    /**
     * posting a not Existing user should return 404
     *
     * @throws Exception
     */
    @Test
    void postNotExistingUser() throws Exception 
    {
        //Changing the testUser
        testUser = new User("33", "unbekannt", "unknown", "user");

        MvcResult result = mockMvc.perform(post("/auth").contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testUser)
        ))
                .andExpect(status().is(401))
                .andReturn();
    }



    /**
     * Trying to "Log In" / Post a User Without Password
     *
     * Should return Http Status Unauthorized (401)
     * @throws Exception
     */
    @Test
    void postNoPassword() throws Exception
    {
        MvcResult result = mockMvc.perform(post("/auth").contentType(MediaType.APPLICATION_JSON).content(asJsonString(new User("12", "", "Alphonso", "kek"))
        ))
                .andExpect(status().is(401))
                .andReturn();
    }


    /**
     * Tring to post a user to a Wrong URL should return Http Not Found (404)
     * @throws Exception
     */
    @Test
    void postUserWrongURL() throws Exception
    {
        MvcResult result = mockMvc.perform(post("/wrong").contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testUser)))
                .andExpect(status().is(404))
                .andReturn();
    }


    /**
     * There is one user in the Mocked user dao {@link TestUserDAO}, with the id "42"
     *
     * Trying to Post a user with a not existing or wrong ID should return Http Unauthorized (401)
     * and the message "username and or password is missing."
    * @throws Exception
     */
    @Test
    void postWithoutUserId() throws Exception
    {
        //User id empty
        testUser.setUserId("");

        MvcResult result = mockMvc.perform(post("/auth").contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testUser)))
                .andExpect(status().is(401))
                .andReturn();

        //Verifying the message
        String ans = result.getResponse().getContentAsString();
        Assert.assertEquals("username and or password is missing.", ans);
    }



    /**
     * There is one user in the Mocked user dao {@link TestUserDAO}, with the  password "geheim"
     *
     * Trying to Post a user with a not existing or wrong Password should return Http Unauthorized (401)
     * and the message "Wrong Password"
     * @throws Exception
     */
    @Test
    void postWithWrongPassword() throws Exception
    {
        testUser.setPassword("wrongPsswd");
        MvcResult result = mockMvc.perform(post("/auth").contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testUser)))

                .andExpect(status().is(401))
                .andReturn();

        //Verifying the message
        String ans = result.getResponse().getContentAsString();
        Assert.assertEquals("Wrong Password", ans);
    }



    /**
     * Posting a user without a surname
     * should work and thus return Http Ok (200)
     * @throws Exception
     */
    @Test
    void PostNoLastName() throws Exception
    {
        //no last name
        testUser.setLastName("");

        MvcResult result = mockMvc.perform(post("/auth").contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testUser)))
                .andExpect(status().is(200))
                .andReturn();
    }



    /**
     * Posting a user without a name (First Name)
     * should work and thus return Http Ok (200)
     * @throws Exception
     */
    @Test
    void PostNoName() throws Exception
    {
        //No first name
        testUser.setFirstName("");

        MvcResult result = mockMvc.perform(post("/auth").contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testUser)))
                .andExpect(status().is(200))
                .andReturn();
    }


    /**
     * Posing a user with wrong first name is possible, as long as suerId and password match
     * @throws Exception
     */
    @Test
    void postWithWrongFirstName() throws Exception
    {
        //Change first name
        testUser.setFirstName("AnotherFirstName");

        MvcResult result = mockMvc.perform(post("/auth").contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testUser)))
                .andExpect(status().is(200))
                .andReturn();
    }

    /**
     *
     * @throws Exception
     */
    @Test
    void postWithWrongLastName() throws Exception
    {
        //Change last name
        testUser.setLastName("AnotherLastName");

        MvcResult result = mockMvc.perform(post("/auth").contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testUser)))
                .andExpect(status().is(200))
                .andReturn();
    }



    /**
     *
     * @throws Exception
     */
    @Test
    void postWrongContentType() throws Exception
    {

        MvcResult result = mockMvc.perform(post("/auth").contentType(MediaType.APPLICATION_XML)
                .content(asJsonString(testUser)))
                .andExpect(status().is(415))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        //No body given
        Assert.assertEquals(0, content.length());
    }


    public static String asJsonString(final Object obj)
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

