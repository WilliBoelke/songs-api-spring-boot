package htwb.ai.willi;

import com.fasterxml.jackson.databind.ObjectMapper;
import htwb.ai.willi.repository.LyricsRepository;
import htwb.ai.willi.service.AuthRestTemplateWrapper;
import htwb.ai.willi.controller.LyricController;
import htwb.ai.willi.entity.Lyric;
import htwb.ai.willi.service.LyricService;
import htwb.ai.willi.service.SongRestTemplateWrapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LyricsServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LyricsServiceApplicationTests {


	//-----------INSTANCE VARIABLES  -----------//


	@Autowired
	private MockMvc mockMvc;

	@InjectMocks
	@Autowired
	LyricController lyricController;

	@InjectMocks
	@Autowired
	LyricService lyricService;


	@Mock
	private AuthRestTemplateWrapper restTemplateWrapper;

	@Mock
	private SongRestTemplateWrapper songRestTemplateWrapper;

	@Autowired
	LyricsRepository lyricsRepository;

	private String token;

	private String invalidToken;

	private String userID;


	//-----------SETUP AND TEARDOWN-----------//


	@Before
	public void setup() throws IOException
	{

		setupTestFiles();

		// user token

		lyricsRepository.setDirectory(LyricsRepository.TEST_DIRECTORY);
		token = "token123";
		invalidToken = "invalidToken";
		userID = "testuser";


		// Mocked RestTemplate Wrapper (The user and song-service arent part of this test)

		MockitoAnnotations.initMocks(this);

		Mockito.when(restTemplateWrapper.request(token)).thenReturn(userID);
		Mockito.when(restTemplateWrapper.request(invalidToken)).thenThrow(HttpServerErrorException.class);

		Mockito.when(songRestTemplateWrapper.verifySongId(1)).thenReturn("Here Comes The Sun");
		Mockito.when(songRestTemplateWrapper.verifySongId(2)).thenReturn("A Different Song Title");
		Mockito.when(songRestTemplateWrapper.verifySongId(3)).thenReturn("Does not Exist");


		mockMvc = MockMvcBuilders.standaloneSetup(lyricController, lyricsRepository).build();
	}





	//-----------TESTS-----------//


	//-----------HTTP GET-----------//


	@Test
	public void getLyricsByNameAvailableLyrics() throws Exception
	{
		//Request
		MvcResult result =
			mockMvc.perform(get("/lyrics/Dancing With Tears In My Eyes")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is(200)).andExpect(content()
				.contentType(MediaType.APPLICATION_JSON)).andReturn();


		//Verify Result
		Assert.assertEquals("{\"songId\":3,\"songTitle\":\"Dancing With Tears In My Eyes\",\"lyric\":\"Dancing with " +
			"tears in my eyes\\nWeeping for the memory of a life gone by\\nDancing with tears in my eyes\\nLiving " +
			"out a memory of a love that died\\n\\nIt's five and I'm driving home again\\nIt's hard to believe that" +
			" it's my last time\\nThe man on the wireless cries again:\\n\\\"It's over, it's over\\\"\\n\\nDancing " +
			"with tears in my eyes\\nWeeping for the memory of a life gone by\\nDancing with tears in my " +
			"eyes\\nLiving out a memory of a love that died\\n\\nIt's late and I'm with my love alone\\nWe drink to" +
			" forget the coming storm\\nWe love to the sound of our favourite song\\nOver and over\\n\\nDancing " +
			"with tears in my eyes\\nLiving out a memory of a love that died\\n\\nIt's time and we're in each " +
			"others arms\\nIt's time but I don't think we really care\\n\\nDancing with tears in my eyes\\nWeeping " +
			"for the memory of a life gone by\\nDancing with tears in my eyes\\nWeeping for the memory of a life " +
			"gone by\\n\\nDancing with tears in my eyes...\"}", result.getResponse().getContentAsString());
	}

     /**
      * Make a request with an invalid auth token
      * expect Http 401 / Unauthorized
      * and a message
      * @throws Exception
      */
	@Test
	public void getAllLyricsWrongAuth() throws Exception
	{
		//Request
		MvcResult result = mockMvc.perform(get("/lyrics")
			.accept(MediaType.APPLICATION_JSON)
			.header(HttpHeaders.AUTHORIZATION, invalidToken))
				.andExpect(status()
                            .is(401))
			     .andExpect(content()
                            .contentType(MediaType.APPLICATION_JSON))
                  .andReturn();

		Assert.assertEquals("Not a valid authorization token :  invalidToken", result.getResponse().getContentAsString());
	}

     /**
      * Make a request with an invalid auth token
      * expect Http 401 / Unauthorized
      * and a message
      * @throws Exception
      */
     @Test
     public void getLyricsByNameWrongAuth() throws Exception
     {
          //Request
          MvcResult result = mockMvc.perform(get("/lyrics/Dancing With Tears In My Eyes")
                  .accept(MediaType.APPLICATION_JSON)
                  .header(HttpHeaders.AUTHORIZATION, invalidToken))
                  .andExpect(status()
                          .is(401))
                  .andExpect(content()
                          .contentType(MediaType.APPLICATION_JSON))
                  .andReturn();

          Assert.assertEquals("Not a valid authorization token :  invalidToken", result.getResponse().getContentAsString());
     }



     /**
      * Posting a new and valid song
      * The song should be added to the Databse
      * and we can expect a HTTP 201
      * @throws Exception
      */
     @Test
     public void getAllLyrics() throws Exception
     {
          // Request
          MvcResult result =
                  mockMvc.perform(get("/lyrics")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token)
                          .contentType(MediaType.APPLICATION_JSON))
                          .andExpect(status().is(200)).andReturn();


          // I can onl verify two song, which will be always in the directory

          Assert.assertEquals("[{\"songId\":3,\"songTitle\":\"Dancing With Tears In My Eyes\",\"lyric\":\"Dancing " +
			"with tears in my eyes\\nWeeping for the memory of a life gone by\\nDancing with tears in my " +
			"eyes\\nLiving out a memory of a love that died\\n\\nIt's five and I'm driving home again\\nIt's hard " +
			"to believe that it's my last time\\nThe man on the wireless cries again:\\n\\\"It's over, it's " +
			"over\\\"\\n\\nDancing with tears in my eyes\\nWeeping for the memory of a life gone by\\nDancing with " +
			"tears in my eyes\\nLiving out a memory of a love that died\\n\\nIt's late and I'm with my love " +
			"alone\\nWe drink to forget the coming storm\\nWe love to the sound of our favourite song\\nOver and " +
			"over\\n\\nDancing with tears in my eyes\\nLiving out a memory of a love that died\\n\\nIt's time and " +
			"we're in each others arms\\nIt's time but I don't think we really care\\n\\nDancing with tears in my " +
			"eyes\\nWeeping for the memory of a life gone by\\nDancing with tears in my eyes\\nWeeping for the " +
			"memory of a life gone by\\n\\nDancing with tears in my eyes...\"},null]", result.getResponse().getContentAsString());

     }


     @Test
	public void getLyricsByWrongName() throws Exception
	{
		//Request
		MvcResult result =
			mockMvc.perform(get("/lyrics/thatSongDoesNotExistAtAll123")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is(404)).andExpect(content()
				.contentType(MediaType.APPLICATION_JSON)).andReturn();


		//Verify Result
		Assert.assertEquals("No lyrics to the songthatSongDoesNotExistAtAll123", result.getResponse().getContentAsString());
	}


	//-----------HTTP POST-----------//




     /**
      * Posting new and valid lyrics, but trhe auth token is
      * invalid
      * Expecting Http 401 / Unathorized
      * and a message
      * @throws Exception
      */
     @Test
     public void postNewLyricsWrongAuth() throws Exception
     {
          //The Song to post
          Lyric testLyric = new Lyric(1, "Here Comes The Sun", "Here comes the sun (doo doo doo doo)\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, it's been a long cold lonely winter\n" + "Little darling, it feels like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, the smiles returning to the faces\n" + "Little darling, it seems like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "\n" + "Little darling, I feel that ice is slowly melting\n" + "Little darling, it seems like years since it's been clear\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "It's all right");

          // Request
          MvcResult response =
                  mockMvc.perform(post("/lyrics")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, invalidToken)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(asJsonString(testLyric))).andExpect(status()
                          .is(401)).andReturn();

          Assert.assertEquals("Not a valid authorization token :  invalidToken", response.getResponse().getContentAsString());
     }


     /**
      *Post new Lyrics without title
      * Expecting Http 400 / BadRequest
      * and a message
      * I mocked the RestTemplateWrapper so that the song with id 3
      * will not exist
      * @throws Exception
      */
     @Test
     public void postNewLyricsWithoutTitle() throws Exception
     {
          //The Lyrics to post
          Lyric testLyric = new Lyric(3, "", "Here comes the sun (doo doo doo doo)\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, it's been a long cold lonely winter\n" + "Little darling, it feels like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, the smiles returning to the faces\n" + "Little darling, it seems like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "\n" + "Little darling, I feel that ice is slowly melting\n" + "Little darling, it seems like years since it's been clear\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "It's all right");

          // Request
          MvcResult response =
                  mockMvc.perform(post("/lyrics")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(asJsonString(testLyric))).andExpect(status()
                          .is(400)).andReturn();

          Assert.assertEquals("Wrong body: no title", response.getResponse().getContentAsString());
     }


     /**
      * Lyrics can only be posted for songs which are in the song databse
      * if that not the case we expect
      * Http 400 / BadRequest
      * and a message
      * @throws Exception
      */
     @Test
     public void postNewLyricsSongDoesntExist() throws Exception
     {
          //The Song to post
          Lyric testLyric = new Lyric(3, "Here Comes The Sun", "Here comes the sun (doo doo doo doo)\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, it's been a long cold lonely winter\n" + "Little darling, it feels like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, the smiles returning to the faces\n" + "Little darling, it seems like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "\n" + "Little darling, I feel that ice is slowly melting\n" + "Little darling, it seems like years since it's been clear\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "It's all right");

          // Request
          MvcResult response =
                  mockMvc.perform(post("/lyrics")
                          .accept(MediaType.APPLICATION_JSON)
                          .header(HttpHeaders.AUTHORIZATION, token)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(asJsonString(testLyric))).andExpect(status()
                          .is(400)).andReturn();

          Assert.assertEquals("The Song doesnt exist in the database : not existing ID", response.getResponse().getContentAsString());
     }

	/**
	 * Lyrics can only be posted for songs which are in the song databse
	 * if that not the case we expect
	 * Http 400 / BadRequest
	 * and a message
	 * @throws Exception
	 */
	@Test
	public void postNewLyricsIdAndTitleMismatch() throws Exception
	{
		//The Song to post
		Lyric testLyric = new Lyric(2, "Here Comes The Sun", "Here comes the sun (doo doo doo doo)\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, it's been a long cold lonely winter\n" + "Little darling, it feels like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, the smiles returning to the faces\n" + "Little darling, it seems like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "\n" + "Little darling, I feel that ice is slowly melting\n" + "Little darling, it seems like years since it's been clear\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "It's all right");

		// Request
		MvcResult response =
			mockMvc.perform(post("/lyrics")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(testLyric))).andExpect(status()
				.is(400)).andReturn();

		Assert.assertEquals("The Song doesnt exist in the database : id - title mismatch", response.getResponse().getContentAsString());
	}


	//-----------HTTP PUT-----------//


	/**
	 *PUT  Lyrics without title
	 * Expecting Http 400 / BadRequest
	 * and a message
	 * I mocked the RestTemplateWrapper so that the song with id 3
	 * will not exist
	 * @throws Exception
	 */
	@Test
	public void putSongWithoutTitle() throws Exception
	{
		//The Lyrics to post
		Lyric testLyric = new Lyric(3, "", "Here comes the sun (doo doo doo doo)\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, it's been a long cold lonely winter\n" + "Little darling, it feels like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, the smiles returning to the faces\n" + "Little darling, it seems like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "\n" + "Little darling, I feel that ice is slowly melting\n" + "Little darling, it seems like years since it's been clear\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "It's all right");

		// Request
		MvcResult response =
			mockMvc.perform(put("/lyrics")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(testLyric))).andExpect(status()
				.is(400)).andReturn();

		Assert.assertEquals("Wrong body: no title", response.getResponse().getContentAsString());
	}



	/**
	 * Putting   valid lyrics, but the auth token is
	 * invalid
	 * Expecting Http 401 / Unathorized
	 * and a message
	 * @throws Exception
	 */
	@Test
	public void putNewLyricsWrongAuth() throws Exception
	{
		//The Song to post
		Lyric testLyric = new Lyric(1, "Here Comes The Sun", "Here comes the sun (doo doo doo doo)\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, it's been a long cold lonely winter\n" + "Little darling, it feels like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, the smiles returning to the faces\n" + "Little darling, it seems like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "\n" + "Little darling, I feel that ice is slowly melting\n" + "Little darling, it seems like years since it's been clear\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "It's all right");

		// Request
		MvcResult response =
			mockMvc.perform(put("/lyrics")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, invalidToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(testLyric))).andExpect(status()
				.is(401)).andReturn();

		Assert.assertEquals("Not a valid authorization token :  invalidToken", response.getResponse().getContentAsString());
	}


	/**
	 * Putting lrics which doesnt exist (title)
	 * Expecting Http 400 / Bad Requesr
	 * and a message
	 * @throws Exception
	 */
	@Test
	public void putLyricsWrongTitle() throws Exception
	{
		//The Song to post
		Lyric testLyric = new Lyric(1, "Here Comes The Moon", "Here comes the sun (doo doo doo doo)\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, it's been a long cold lonely winter\n" + "Little darling, it feels like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, the smiles returning to the faces\n" + "Little darling, it seems like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "\n" + "Little darling, I feel that ice is slowly melting\n" + "Little darling, it seems like years since it's been clear\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "It's all right");

		// Request
		MvcResult response =
			mockMvc.perform(put("/lyrics")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(testLyric))).andExpect(status()
				.is(400)).andReturn();

		Assert.assertEquals("The Lyrics with the name Here Comes The Moon dont exist", response.getResponse().getContentAsString());
	}



	/**
	 * Putting valid lyrics
	 * Expecting Http 401 / Unathorized
	 * and a message
	 * @throws Exception
	 */
	@Test
	public void putLyrics() throws Exception
	{
		Lyric testLyric = new Lyric(1, "Here Comes The Sun", "Here comes the sun (doo doo doo doo)\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, it's been a long cold lonely winter\n" + "Little darling, it feels like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, the smiles returning to the faces\n" + "Little darling, it seems like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "\n" + "Little darling, I feel that ice is slowly melting\n" + "Little darling, it seems like years since it's been clear\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "It's all right");

		// Request

		MvcResult postRespnse =
			mockMvc.perform(post("/lyrics")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(testLyric))).andExpect(status()
				.is(201)).andReturn();

		//Getting the newly added song

		MvcResult getterResponse1 =
			mockMvc.perform(get("/lyrics/Here Comes The Sun")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is(200)).andExpect(content()
				.contentType(MediaType.APPLICATION_JSON)).andReturn();

		//verify

		Assert.assertEquals("{\"songId\":1,\"songTitle\":\"Here Comes The Sun\",\"lyric\":\"Here comes the sun (doo " +
			"doo doo doo)\\nHere comes the sun, and I say\\nIt's all right\\n\\nLittle darling, it's been a long " +
			"cold lonely winter\\nLittle darling, it feels like years since it's been here\\nHere comes the " +
			"sun\\nHere comes the sun, and I say\\nIt's all right\\n\\nLittle darling, the smiles returning to the " +
			"faces\\nLittle darling, it seems like years since it's been here\\nHere comes the sun\\nHere comes the" +
			" sun, and I say\\nIt's all right\\n\\nSun, sun, sun, here it comes\\nSun, sun, sun, here it " +
			"comes\\nSun, sun, sun, here it comes\\nSun, sun, sun, here it comes\\nSun, sun, sun, here it " +
			"comes\\n\\nLittle darling, I feel that ice is slowly melting\\nLittle darling, it seems like years " +
			"since it's been clear\\nHere comes the sun\\nHere comes the sun, and I say\\nIt's all right\\n\\nHere " +
			"comes the sun\\nHere comes the sun, and I say\\nIt's all right\\nIt's all right\"}", getterResponse1.getResponse().getContentAsString());

		//The Song update
		testLyric = new Lyric(1, "Here Comes The Sun", "Here comes the sun (doo doo doo doo...and the rest is changed");

		// Request
		MvcResult puResponse =
			mockMvc.perform(put("/lyrics")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(testLyric))).andExpect(status()
				.is(201)).andReturn();

		Assert.assertEquals("The Lyrics are saved, yo can access them under \\lyrics\\Here Comes The Sun", puResponse.getResponse().getContentAsString());


		//Getting the newly added song

		MvcResult getterResponse2 =
			mockMvc.perform(get("/lyrics/Here Comes The Sun")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is(200)).andExpect(content()
				.contentType(MediaType.APPLICATION_JSON)).andReturn();

		//verify

		Assert.assertEquals("{\"songId\":1,\"songTitle\":\"Here Comes The Sun\",\"lyric\":\"Here comes the sun (doo " +
			"doo doo doo...and the rest is changed\"}", getterResponse2.getResponse().getContentAsString());
	}




	//-----------DELETE-----------//

	/**
	 * i put post and delete in one test, so i wont have any
	 * random song in the test directory
	 *
	 * Posting new Lyrics : we can expect a HTTP 201
	 *
	 * For the deletion we expect the status Http 500 / Deleted
	 * and a message
	 *
	 *
	 *  @throws Exception
	 */
	@Test
	public void postNewLyricsAndDeleteThemAgain() throws Exception
	{

		// POST

		//The Song to post

		Lyric testLyric = new Lyric(1, "Here Comes The Sun", "Here comes the sun (doo doo doo doo)\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, it's been a long cold lonely winter\n" + "Little darling, it feels like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, the smiles returning to the faces\n" + "Little darling, it seems like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "\n" + "Little darling, I feel that ice is slowly melting\n" + "Little darling, it seems like years since it's been clear\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "It's all right");

		// Request

		MvcResult response =
			mockMvc.perform(post("/lyrics")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(testLyric))).andExpect(status()
				.is(201)).andReturn();

		//Getting the newly added song

		MvcResult getterResponse =
			mockMvc.perform(get("/lyrics/Here Comes The Sun")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is(200)).andExpect(content()
				.contentType(MediaType.APPLICATION_JSON)).andReturn();

		//verify

		Assert.assertEquals("{\"songId\":1,\"songTitle\":\"Here Comes The Sun\",\"lyric\":\"Here comes the sun (doo " +
			"doo doo doo)\\nHere comes the sun, and I say\\nIt's all right\\n\\nLittle darling, it's been a long " +
			"cold lonely winter\\nLittle darling, it feels like years since it's been here\\nHere comes the " +
			"sun\\nHere comes the sun, and I say\\nIt's all right\\n\\nLittle darling, the smiles returning to the " +
			"faces\\nLittle darling, it seems like years since it's been here\\nHere comes the sun\\nHere comes the" +
			" sun, and I say\\nIt's all right\\n\\nSun, sun, sun, here it comes\\nSun, sun, sun, here it " +
			"comes\\nSun, sun, sun, here it comes\\nSun, sun, sun, here it comes\\nSun, sun, sun, here it " +
			"comes\\n\\nLittle darling, I feel that ice is slowly melting\\nLittle darling, it seems like years " +
			"since it's been clear\\nHere comes the sun\\nHere comes the sun, and I say\\nIt's all right\\n\\nHere " +
			"comes the sun\\nHere comes the sun, and I say\\nIt's all right\\nIt's all right\"}", getterResponse.getResponse().getContentAsString());

		//DELETE

		MvcResult deleteResponse =
			mockMvc.perform(delete("/lyrics/Here Comes The Sun")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(testLyric))).andExpect(status()
				.is(500)).andReturn();

		// verify

		Assert.assertEquals("", deleteResponse.getResponse().getContentAsString());


	}


	//-----------OTHER-----------//

	private void setupTestFiles() throws IOException
	{
		File testDir = new File(LyricsRepository.TEST_DIRECTORY +"songLyricsTest");
		File testSongOne = new File(LyricsRepository.TEST_DIRECTORY +"Dancing With Tears In My Eyes");
		File testSongTwo = new File(LyricsRepository.TEST_DIRECTORY +"Muskrat Love");

		FileWriter fileWriter = new FileWriter(testSongOne);
		fileWriter.write("{\"songId\":3,\"songTitle\":\"Dancing With Tears In My Eyes\",\"lyric\":\"Dancing with tears " +
			"in my eyes\\nWeeping for the memory of a life gone by\\nDancing with tears in my eyes\\nLiving out a " +
			"memory of a love that died\\n\\nIt's five and I'm driving home again\\nIt's hard to believe that it's " +
			"my last time\\nThe man on the wireless cries again:\\n\\\"It's over, it's over\\\"\\n\\nDancing with " +
			"tears in my eyes\\nWeeping for the memory of a life gone by\\nDancing with tears in my eyes\\nLiving " +
			"out a memory of a love that died\\n\\nIt's late and I'm with my love alone\\nWe drink to forget the " +
			"coming storm\\nWe love to the sound of our favourite song\\nOver and over\\n\\nDancing with tears in " +
			"my eyes\\nLiving out a memory of a love that died\\n\\nIt's time and we're in each others arms\\nIt's " +
			"time but I don't think we really care\\n\\nDancing with tears in my eyes\\nWeeping for the memory of a" +
			" life gone by\\nDancing with tears in my eyes\\nWeeping for the memory of a life gone by\\n\\nDancing " +
			"with tears in my eyes...\"}");
		fileWriter.flush();
		fileWriter.close();

		FileWriter fileWriter2 = new FileWriter(testSongTwo);
		fileWriter2.write("{\"songId\":1,\"songTitle\":\"Muskrat Love\",\"lyric\":\"Dancing with tears " +
			"in my eyes\\nWeeping for the memory of a life gone by\\nDancing with tears in my eyes\\nLiving out a " +
			"memory of a love that died\\n\\nIt's five and I'm driving home again\\nIt's hard to believe that it's " +
			"my last time\\nThe man on the wireless cries again:\\n\\\"It's over, it's over\\\"\\n\\nDancing with " +
			"tears in my eyes\\nWeeping for the memory of a life gone by\\nDancing with tears in my eyes\\nLiving " +
			"out a memory of a love that died\\n\\nIt's late and I'm with my love alone\\nWe drink to forget the " +
			"coming storm\\nWe love to the sound of our favourite song\\nOver and over\\n\\nDancing with tears in " +
			"my eyes\\nLiving out a memory of a love that died\\n\\nIt's time and we're in each others arms\\nIt's " +
			"time but I don't think we really care\\n\\nDancing with tears in my eyes\\nWeeping for the memory of a" +
			" life gone by\\nDancing with tears in my eyes\\nWeeping for the memory of a life gone by\\n\\nDancing " +
			"with tears in my eyes...\"}");
		fileWriter2.flush();
		fileWriter2.close();
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
