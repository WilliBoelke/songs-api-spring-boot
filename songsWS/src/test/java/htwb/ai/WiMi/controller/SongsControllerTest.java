package htwb.ai.WiMi.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.xml.XmlMapper;
import htwb.ai.WiMi.Marshalling.GsonWrapper;
import htwb.ai.WiMi.Marshalling.XmlMapperWrapper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import htwb.ai.WiMi.model.database.Song;
import htwb.ai.WiMi.testImplementation.TestSongDAO;

class SongsControllerTest
{
    private MockMvc mockMvc;

    @BeforeEach
    public void setup()
    {
        mockMvc = MockMvcBuilders.standaloneSetup(new SongsController(new TestSongDAO(), new GsonWrapper<Song>(), new XmlMapperWrapper<Song>())).build();
    }


    public static String getJsonFromObject(final Object obj)
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



    //-----------GET  ALL-----------//


    /**
     * GET /songs
     *
     * With accept header set to JSON
     * Songs should be returned successfully with code 200
     * @throws Exception
     */
    @Test
    void getSongsJSON() throws Exception
    {
        MvcResult result = mockMvc.perform(get("/songs").accept("application/json"))
                .andExpect(status().is(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = (result.getResponse().getContentAsString());
        System.out.println(content);
        Assert.assertTrue(content.contains("title") && content.contains("id"));
        Assert.assertEquals("[{\"id\":1,\"title\":\"Wind Of Change\",\"artist\":\"Scorpions\",\"label\":\"Single\"," +
                "\"released\":1991}]", content);
    }



    /**
     * GET /songs
     *
     * With accept header set to an unknown accept header
     * @throws Exception
     */
    @Test
    void getSongsWithUnknownAcceptHeader() throws Exception
    {
        MvcResult result = mockMvc.perform(get("/songs").accept("anUnknownAcceptHeader/header"))
                .andExpect(status().is(406))
                .andReturn();
    }


    /**
     * GET /songs
     *
     *
     * @throws Exception
     */
    @Test
    void getSongsXML() throws Exception
    {
        MvcResult result = mockMvc.perform(get("/songs").accept("application/xml"))
                .andExpect(status().is(200))
                .andReturn();

        String content = (result.getResponse().getContentAsString());
        Assert.assertTrue(content.contains("title") && content.contains("id"));
        Assert.assertEquals("<Song><id>1</id><title>Wind Of Change</title><artist>Scorpions</artist><label>Single</label><released>1991</released></Song>", content);
    }


    /**
     * Not a valid URL
     * @throws Exception
     */
    @Test
    void getFalseURIShouldReturn404() throws Exception
    {
        mockMvc.perform(get("/notExistingURL"))
                .andExpect(status().is(404));
    }


    /**
     * GET /songs
     *
     * Testing getAllSongs with no songs in the "Database"
     * @throws Exception
     */
    @Test
    void getAllSongsEmptyDatabase() throws Exception
    {
        //Deleting the Song from the testImplementation so it should be empty
        mockMvc.perform(delete("/songs/1"))
                .andExpect(status().is(204));

        //GET Request for all songs
        MvcResult result = mockMvc.perform(get("/songs").accept("application/json"))
                .andExpect(status().is(404))
                .andReturn();

        String content = (result.getResponse().getContentAsString());

        //[] is empty JSON
        Assert.assertEquals("[]", content);
    }




    //-----------GET BY ID -----------//


    /**
     * GET request for the song with id "1",
     * This Song and id exist in the {@link TestSongDAO}
     * @throws Exception
     */
    @Test
    void getSongWithID() throws Exception
    {
        mockMvc.perform(get("/songs/1").accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Wind Of Change"))
                .andExpect(jsonPath("$.artist").value("Scorpions"))
                .andExpect(jsonPath("$.label").value("Single"))
                .andExpect(jsonPath("$.released").value(1991))
                .andReturn();
    }


    /**
     *
     * getSongById with unknown I.
     * In the {@link TestSongDAO} only one song exists (with ID 1)
     *
     * So id=2 should not return anything (...or 404)
     * @throws Exception
     */
    @Test
    void getSongByUnknownID() throws Exception
    {
        mockMvc.perform(get("/songs/2").accept("application/json"))
                .andExpect(status().is(404));
    }


    //-----------POST -----------//



    /**
     * Posting song with valid name but already existing id
     * Changes/Updates  the existing song
     * @throws Exception
     */
    @Test
    void postASongWithAnExistingID() throws Exception
    {
        Song newSong = Song.builder().withId(1).withTitle("NewSong").build();
        mockMvc.perform(post("/songs").contentType(MediaType.APPLICATION_JSON).content(getJsonFromObject(newSong)
        ))
                .andExpect(status().is(201))
                .andExpect(header().string("Location", "/songs/1"));
    }



    /**
     * Post a new, valid song (name and valid id) should return
     * HttpStatus.created (201)
     *
     * @throws Exception
     */
    @Test
    void postASongShouldReturn201() throws Exception
    {

        //Creating a song to add
        Song newSong = Song.builder().withId(2).withTitle("Where is my mind").withArtist("Pixies").build();

        //Adding the song
        mockMvc.perform(post("/songs").contentType(MediaType.APPLICATION_JSON).content(getJsonFromObject(newSong)
        ))
                .andExpect(status().is(201))
                .andExpect(header().string("Location", "/songs/2"));
    }



    /**
     * Posting an empty Song is not allowed
     * HttpResponse.BAD_REQUEST (400)
     * @throws Exception
     */
    @Test
    void postEmptySong() throws Exception
    {
        Song newSong = Song.builder().build();
        mockMvc.perform(post("/songs").contentType(MediaType.APPLICATION_JSON).content(getJsonFromObject(newSong)
        ))
                .andExpect(status().is(400));
    }



    /**
     * Posting an empty Song is not allowed
     * HttpResponse.BAD_REQUEST (400)
     * @throws Exception
     */
    @Test
    void postSongWithoutTitle() throws Exception
    {
        //Building a song without title
        Song newSong = Song.builder().withArtist("Pixies").withReleased(1995).build();
        mockMvc.perform(post("/songs").contentType(MediaType.APPLICATION_JSON).content(getJsonFromObject(newSong)
        ))
                .andExpect(status().is(400));
    }


    /**
     * Post with wrong URL should return HttpRequest.NOT_FOUND (404)
     * @throws Exception
     */
    @Test
    void postWrongURL() throws Exception
    {
        Song changedSong = Song.builder().withTitle("Test").withId(1).build();
        mockMvc.perform(post("/wrongURL").contentType(MediaType.APPLICATION_JSON).content(getJsonFromObject(changedSong)
        ))
                .andExpect(status().is(404));
    }



    //-----------PUT -----------//


    /**
     * Updating an existing song (title)
     *
     * Song should be changed
     * 204 should be returned
     * @throws Exception
     */
    @Test
    void putValidSong() throws Exception
    {

        Song changed = Song.builder().withId(1).withTitle("UpdatedSong").build();

        mockMvc.perform(put("/songs/1").contentType(MediaType.APPLICATION_JSON).content(getJsonFromObject(changed)
        ))
                .andExpect(status().is(204));

        MvcResult result = mockMvc.perform(get("/songs/1").accept("application/json"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("UpdatedSong"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        String content = result.getResponse().getContentAsString();
        Assert.assertEquals(content, getJsonFromObject(changed));
    }


    /**
     * When the ID in Payload and URl is different
     * HttpResponse.BAD_REQUEST should be returned
     * @throws Exception
     */
    @Test
    void putDifferentSongID() throws Exception
    {
        Song changed = Song.builder().withId(1).withTitle("UpdatedSong").build();
        mockMvc.perform(put("/songs/2").contentType(MediaType.APPLICATION_JSON).content(getJsonFromObject(changed)
        ))
                .andExpect(status().is(400));
    }

    /**
     * Put with wrong url
     * should -as always - return 404
     * @throws Exception
     */
    @Test
    void putWrongURLShouldReturn400() throws Exception
    {
        Song changed = Song.builder().withId(1).withTitle("UpdatedSong").build();
        mockMvc.perform(put("/song/1").contentType(MediaType.APPLICATION_JSON).content(getJsonFromObject(changed)
        ))
                .andExpect(status().is(404));
    }

    /**
     * Delete with a not existing id should return
     * HttpResponse.NOT_FOUND (404)
     * @throws Exception
     */
    @Test
    void deleteSongWithNotExistingID() throws Exception
    {
        mockMvc.perform(delete("/songs/7"))
                .andExpect(status().is(404));
    }


    /**
     * Delete Should remove the song and return
     * HttpResponse.NO_CONTENT (204)
     * @throws Exception
     */
    @Test
    void deleteSong() throws Exception
    {
        mockMvc.perform(delete("/songs/1"))
                .andExpect(status().is(204));

        //Song should be removed -404
        mockMvc.perform(get("/songs/1").accept("application/json"))
                .andExpect(status().is(404));
    }


    /**
     * Delete with wrong URL should -again return 404
     * @throws Exception
     */
    @Test
    void deleteWrongURL() throws Exception
    {
        mockMvc.perform(delete("/song/1"))
                .andExpect(status().is(404));
    }

}

