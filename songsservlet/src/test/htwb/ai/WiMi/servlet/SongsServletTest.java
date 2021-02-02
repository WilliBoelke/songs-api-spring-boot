package htwb.ai.WiMi.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SongsServletTest
{
    private DatabaseHelper mockDbHelper = Mockito.mock(DatabaseHelper.class);
    private SongsServlet testServlet;
    private HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
    private HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);
    private Song testSong1;
    private Song testSong2;
    private Song testSong3;

    @BeforeEach
    public void setup()
    {
        //test song
        testSong1 = new Song();
        testSong1.setId(1);
        testSong1.setTitle("testTitel");
        testSong1.setArtist("testartist");
        testSong1.setLabel("testLable");
        testSong1.setReleased(2020);
        //
        testSong2 = new Song();
        testSong2.setId(2);
        testSong2.setTitle("testTitel2");
        testSong2.setArtist("testartist2");
        testSong2.setLabel("testLable2");
        testSong2.setReleased(2022);
        //
        testSong3 = new Song();
        testSong3.setId(3);
        testSong3.setTitle("testTitel3");
        testSong3.setArtist("testartist3");
        testSong3.setLabel("testLable3");
        testSong3.setReleased(2023);
        //
        testServlet  = new SongsServlet();
        testServlet.insertMockDbHelper(mockDbHelper);
    }

    @Test
    public void doPutTest()
    {
        //Verify that the doPut response will be the 405 status
        testServlet.doPut(mockRequest, mockResponse);
        verify(mockResponse).setStatus(405);
    }

    @Test
    public void doDeleteTest()
    {
        //Verify that the doDelete response will be the 405 status
        testServlet.doDelete(mockRequest, mockResponse);
        verify(mockResponse).setStatus(405);
    }

    @Test
    public void doGetSongId() throws IOException
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Hashtable<String, String> h = new Hashtable<>();
        h.put("songId", "1");
        Enumeration<String> params = h.keys();

        when(mockRequest.getParameterNames()).thenReturn(params);
        when(mockRequest.getParameter("songId")).thenReturn("1");
        when(mockDbHelper.getSong(1)).thenReturn(testSong1);
        when(mockResponse.getWriter()).thenReturn(pw);

        testServlet.doGet(mockRequest, mockResponse);

        String resultJSON = sw.toString();
        System.out.println(resultJSON);
        ObjectMapper objectMapper = new ObjectMapper();
        Song resultSong = objectMapper.readValue(resultJSON, Song.class);
        Assertions.assertTrue(testSong1.compareTo(resultSong) > 0);
    }

    @Test
    public void doGetAll() throws IOException
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Hashtable<String, String> h = new Hashtable<>();
        h.put("all" ,"");
        Enumeration<String> params = h.keys();
        ArrayList<Song> songs = new ArrayList<>();
        songs.add(testSong1);
        songs.add(testSong2);
        songs.add(testSong3);

        Mockito.when(mockRequest.getParameterNames()).thenReturn(params);
        Mockito.when(mockResponse.getWriter()).thenReturn(pw);
        Mockito.when(mockDbHelper.getAllSongs()).thenReturn(songs);

        testServlet.doGet(mockRequest, mockResponse);

        String resultJSON = sw.toString();
        System.out.println(resultJSON);

        Assertions.assertTrue(resultJSON.contains("\"id\" : 1"));
        Assertions.assertTrue(resultJSON.contains("\"title\" : \"testTitel\""));
        Assertions.assertTrue(resultJSON.contains("\"artist\" : \"testartist\""));
        Assertions.assertTrue(resultJSON.contains("\"label\" : \"testLable\""));
        Assertions.assertTrue(resultJSON.contains("\"released\" : 2020"));

        Assertions.assertTrue(resultJSON.contains("\"id\" : 2"));
        Assertions.assertTrue(resultJSON.contains("\"title\" : \"testTitel2\""));
        Assertions.assertTrue(resultJSON.contains("\"artist\" : \"testartist2\""));
        Assertions.assertTrue(resultJSON.contains("\"label\" : \"testLable2\""));
        Assertions.assertTrue(resultJSON.contains("\"released\" : 2022"));

        Assertions.assertTrue(resultJSON.contains("\"id\" : 3"));
        Assertions.assertTrue(resultJSON.contains("\"title\" : \"testTitel3\""));
        Assertions.assertTrue(resultJSON.contains("\"artist\" : \"testartist3\""));
        Assertions.assertTrue(resultJSON.contains("\"label\" : \"testLable3\""));
        Assertions.assertTrue(resultJSON.contains("\"released\" : 2023"));
    }


    @Test
    public void doGetWrongParamName() throws IOException
    {
        // Needed Objects
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Hashtable<String, String> h = new Hashtable<>();
        h.put("not a valid param", "");
        Enumeration<String> params = h.keys();

        // Mock returns
        when(mockRequest.getParameterNames()).thenReturn(params);
        when(mockResponse.getWriter()).thenReturn(pw);

        //Executing
        testServlet.doGet(mockRequest, mockResponse);
        String resultJSON = sw.toString();
        System.out.println(resultJSON);

        //Verifying results
        verify(mockResponse).setStatus(400);
        assertEquals("Invalid request : use `all` or 'songId=' to get a response", resultJSON);
    }


    @Test
    public void doGetWrongParam() throws IOException
    {
        // Needed Objects
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Hashtable<String, String> h = new Hashtable<>();
        h.put("songId", "not a number");
        Enumeration<String> params = h.keys();

        // Mock returns
        when(mockRequest.getParameterNames()).thenReturn(params);
        when(mockResponse.getWriter()).thenReturn(pw);
        when(mockRequest.getParameter("songId")).thenReturn(h.get("songId"));

        //Executing
        testServlet.doGet(mockRequest, mockResponse);
        String resultString = sw.toString();
        System.out.println(resultString);

        //Verifying results
        verify(mockResponse).setStatus(400);
        assertEquals("Invalid request : No index specified, try something like 'songId=1'", resultString);
    }

    @Test
    public void doGetSongNotFound() throws IOException
    {
        // Needed Objects
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Hashtable<String, String> h = new Hashtable<>();
        h.put("songId", "1");
        Enumeration<String> params = h.keys();

        // Mock returns
        when(mockRequest.getParameterNames()).thenReturn(params);
        when(mockRequest.getParameter("songId")).thenReturn("2");
        when(mockDbHelper.getSong(2)).thenReturn(null);
        when(mockResponse.getWriter()).thenReturn(pw);

        //Executing
        testServlet.doGet(mockRequest, mockResponse);
        String resultString = sw.toString();
        System.out.println(resultString);

        //Verifying results
        verify(mockResponse).setStatus(404);
        assertEquals("Entry not found", resultString);
    }

    @Test
    public void doPostCorrectBody() throws IOException
    {
        //Needed Objects
        StringReader sr = new StringReader("{\"title\":\"WreckingBall\",\"artist\":\"MILEYCYRUS\",\"label\":\"RCA\"," +
                "\"released\":2013} ");
        BufferedReader br = new BufferedReader(sr);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        // Mock  Returns
        when(mockRequest.getReader()).thenReturn(br);
        when(mockResponse.getWriter()).thenReturn(pw);

        // Execute
        testServlet.doPost(mockRequest, mockResponse);

        // verify Result
        verify(mockResponse).setStatus(201);
    }



    @Test
    public void doPostMissingArtist() throws IOException
    {
        //Needed Objects
        StringReader sr = new StringReader("{\"title\":\"WreckingBall\",\"label\":\"RCA\",\"released\":2013} {\"title" +
                "\":\"WreckingBall\",\"label\":\"RCA\",\"released\":2013} ");
        BufferedReader br = new BufferedReader(sr);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);


        // Mock  Returns
        when(mockRequest.getReader()).thenReturn(br);
        when(mockResponse.getWriter()).thenReturn(pw);

        // Execute
        testServlet.doPost(mockRequest, mockResponse);

        // Verify Result
        verify(mockResponse).setStatus(400);
        assertEquals("Invalid request : Not all necessary information found : declare at least title and artist", sw.toString());
    }

    @Test
    public void doPostMissingTitle() throws IOException
    {
        //Needed Objects
        StringReader sr = new StringReader("{\"artist\":\"MILEYCYRUS\",\"label\":\"RCA\",\"released\":2013} ");
        BufferedReader br = new BufferedReader(sr);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        // Mock  Returns
        when(mockRequest.getReader()).thenReturn(br);
        when(mockResponse.getWriter()).thenReturn(pw);

        // Execute
        testServlet.doPost(mockRequest, mockResponse);

        // Verify Result
        verify(mockResponse).setStatus(400);
        assertEquals("Invalid request : Not all necessary information found : declare at least title and artist", sw.toString());
    }

    @Test
    public void doPostMissingLabel() throws IOException
    {
        //Needed Objects
        StringReader sr = new StringReader("{\"title\":\"WreckingBall\",\"artist\":\"MILEYCYRUS\",\"released\":2013} ");
        BufferedReader br = new BufferedReader(sr);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        // Mock  Returns
        when(mockRequest.getReader()).thenReturn(br);
        when(mockResponse.getWriter()).thenReturn(pw);

        // Execute
        testServlet.doPost(mockRequest, mockResponse);

        // Verify Result
        verify(mockResponse).setStatus(201);
    }


    @Test
    public void doPostMissingRelease() throws IOException
    {
        //Needed Objects
        StringReader sr = new StringReader("{\"title\":\"WreckingBall\",\"artist\":\"MILEYCYRUS\",\"label\":\"RCA\"}  ");
        BufferedReader br = new BufferedReader(sr);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        // Mock  Returns
        when(mockRequest.getReader()).thenReturn(br);
        when(mockResponse.getWriter()).thenReturn(pw);

        // Execute
        testServlet.doPost(mockRequest, mockResponse);

        // Verify Result
        verify(mockResponse).setStatus(201);
    }



}