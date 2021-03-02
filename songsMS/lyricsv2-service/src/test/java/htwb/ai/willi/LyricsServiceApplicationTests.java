package htwb.ai.willi;

import com.fasterxml.jackson.databind.ObjectMapper;
import htwb.ai.willi.service.AuthRestTemplateWrapper;
import htwb.ai.willi.controller.LyricController;
import htwb.ai.willi.entity.Lyric;
import htwb.ai.willi.service.LyricService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LyricsServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LyricsServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@InjectMocks
	@Autowired
	LyricController lyricController;

	@Mock
	private AuthRestTemplateWrapper restTemplateWrapper;

	@Autowired
	LyricService lyricService;

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
		Mockito.when(restTemplateWrapper.request(token)).thenReturn(userID);
		Mockito.when(restTemplateWrapper.request(invalidToken)).thenThrow(HttpServerErrorException.class);
		mockMvc = MockMvcBuilders.standaloneSetup(lyricController).build();
	}


	@Test
	public void getJson()
	{
		Lyric testLyric = new Lyric(1, "Dancing With Tears In My Eyes", "Dancing with tears in my eyes\n" + "Weeping for the memory of a life gone by\n" + "Dancing with tears in my eyes\n" + "Living out a memory of a love that died\n" + "\n" + "It's five and I'm driving home again\n" + "It's hard to believe that it's my last time\n" + "The man on the wireless cries again:\n" + "\"It's over, it's over\"\n" + "\n" + "Dancing with tears in my eyes\n" + "Weeping for the memory of a life gone by\n" + "Dancing with tears in my eyes\n" + "Living out a memory of a love that died\n" + "\n" + "It's late and I'm with my love alone\n" + "We drink to forget the coming storm\n" + "We love to the sound of our favourite song\n" + "Over and over\n" + "\n" + "Dancing with tears in my eyes\n" + "Living out a memory of a love that died\n" + "\n" + "It's time and we're in each others arms\n" + "It's time but I don't think we really care\n" + "\n" + "Dancing with tears in my eyes\n" + "Weeping for the memory of a life gone by\n" + "Dancing with tears in my eyes\n" + "Weeping for the memory of a life gone by\n" + "\n" + "Dancing with tears in my eyes...");


		String json = asJsonString(testLyric);
		System.out.println(json);
	}
	@Test
	public void getLyricsByNameAvailableLyrics() throws Exception
	{
		//Request
		MvcResult result =
			mockMvc.perform(get("/lyrics/Dancing With Tears in m Eyes")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is(200)).andExpect(content()
				.contentType(MediaType.APPLICATION_JSON)).andReturn();


		//Verify Result
		Assert.assertEquals("{\"songId\":1,\"songTitle\":\"Dancing With Tears In My Eyes\",\"lyric\":\"Dancing with " +
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



	@Test
	public void getLyricsByName() throws Exception
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
		Lyric testLyric = new Lyric(1, "Heredsfa sd Comes The Sun", "Here comes the sun (doo doo doo doo)\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, it's been a long cold lonely winter\n" + "Little darling, it feels like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Little darling, the smiles returning to the faces\n" + "Little darling, it seems like years since it's been here\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "Sun, sun, sun, here it comes\n" + "\n" + "Little darling, I feel that ice is slowly melting\n" + "Little darling, it seems like years since it's been clear\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "\n" + "Here comes the sun\n" + "Here comes the sun, and I say\n" + "It's all right\n" + "It's all right");

		// Request
		MvcResult response =
			mockMvc.perform(post("/lyrics")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(testLyric))).andExpect(status()
				.is(201)).andReturn();

		//Verify Result
		String header = response.getResponse().getHeader("location");

		//Request the new song
		MvcResult getterResponse =
			mockMvc.perform(get("/lyrics/Here Comes The Sun")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().is(200)).andExpect(content()
				.contentType(MediaType.APPLICATION_JSON)).andReturn();


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

		//Verify Result
		Assert.assertEquals("[{\"songId\":1,\"songTitle\":\"Dancing With Tears In My Eyes\",\"lyric\":\"Dancing with" +
			" tears in my eyes\\nWeeping for the memory of a life gone by\\nDancing with tears in my eyes\\nLiving " +
			"out a memory of a love that died\\n\\nIt's five and I'm driving home again\\nIt's hard to believe that" +
			" it's my last time\\nThe man on the wireless cries again:\\n\\\"It's over, it's over\\\"\\n\\nDancing " +
			"with tears in my eyes\\nWeeping for the memory of a life gone by\\nDancing with tears in my " +
			"eyes\\nLiving out a memory of a love that died\\n\\nIt's late and I'm with my love alone\\nWe drink to" +
			" forget the coming storm\\nWe love to the sound of our favourite song\\nOver and over\\n\\nDancing " +
			"with tears in my eyes\\nLiving out a memory of a love that died\\n\\nIt's time and we're in each " +
			"others arms\\nIt's time but I don't think we really care\\n\\nDancing with tears in my eyes\\nWeeping " +
			"for the memory of a life gone by\\nDancing with tears in my eyes\\nWeeping for the memory of a life " +
			"gone by\\n\\nDancing with tears in my eyes...\"},{\"songId\":1,\"songTitle\":\"Here Comes The Sun\"," +
			"\"lyric\":\"Here comes the sun (doo doo doo doo)\\nHere comes the sun, and I say\\nIt's all " +
			"right\\n\\nLittle darling, it's been a long cold lonely winter\\nLittle darling, it feels like years " +
			"since it's been here\\nHere comes the sun\\nHere comes the sun, and I say\\nIt's all right\\n\\nLittle" +
			" darling, the smiles returning to the faces\\nLittle darling, it seems like years since it's been " +
			"here\\nHere comes the sun\\nHere comes the sun, and I say\\nIt's all right\\n\\nSun, sun, sun, here it" +
			" comes\\nSun, sun, sun, here it comes\\nSun, sun, sun, here it comes\\nSun, sun, sun, here it " +
			"comes\\nSun, sun, sun, here it comes\\n\\nLittle darling, I feel that ice is slowly melting\\nLittle " +
			"darling, it seems like years since it's been clear\\nHere comes the sun\\nHere comes the sun, and I " +
			"say\\nIt's all right\\n\\nHere comes the sun\\nHere comes the sun, and I say\\nIt's all right\\nIt's " +
			"all right\"}]", result.getResponse().getContentAsString());

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
