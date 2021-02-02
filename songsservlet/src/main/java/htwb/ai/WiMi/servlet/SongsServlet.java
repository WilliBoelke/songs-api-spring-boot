package htwb.ai.WiMi.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Enumeration;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * The Songs Servlet Handles user post and get requests
 *
 * It uses the {@link DatabaseHelper} to connect to a PostgreSql Database
 * The user can add Songs to the Database using Http POST requests
 * and use the Http GET requets "all" to get all songs or "songId=x" to get the song with the id x
 */
public class SongsServlet extends HttpServlet
{
    private DatabaseHelper databaseHelper = new DatabaseHelper();

    /**
     * Handles http GET request
     * @param request The Request to the database;</br> Accepted: all; songId=x --> x stands for an integer value
     * @param response
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String responseString = "";
        String parameter = "";
        ObjectMapper objectMapper = new ObjectMapper();
        Enumeration<String> parameterNames = request.getParameterNames();
        PrintWriter responseWriter = response.getWriter();

        // Looping over request params
        while (parameterNames.hasMoreElements())
        {
            // Adding them to the response string
            parameter = parameterNames.nextElement();
            responseString = responseString + parameter + "=" + request.getParameter(parameter) + "\n";
        }

        try (responseWriter)
        {
            response.setContentType("application/json");

            if (parameter.equals("songId"))
            {
                // Control if request parameter is a number/songID
                if (request.getParameter(parameter).matches("-?\\d+"))
                {
                    if (databaseHelper.getSong(Integer.parseInt(request.getParameter(parameter))) != null) // Check if index exists in database
                    {
                        // If song with index exists : Print
                        int index = Integer.parseInt(request.getParameter(parameter));
                        responseWriter.print(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(databaseHelper.getSong(index)));
                        response.setStatus(200);
                    }
                    else
                    {
                        responseWriter.print("Entry not found");
                        response.setStatus(404);
                    }
                }
                else
                {
                    responseWriter.print("Invalid request : No index specified, try something like 'songId=1'");
                    response.setStatus(400);
                }
            }
            else if (parameter.equals("all")) // If request is "all" : Print all songs
            {
                List<Song> songList = databaseHelper.getAllSongs();
                responseWriter.print(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(songList));
                response.setStatus(200);
            }
            else
            {
                // If its not an accepted Requests (all or songId=)
                responseWriter.print("Invalid request : use `all` or 'songId=' to get a response");
                response.setStatus(400);
            }
        }
    }

    /**
     *  Handles http post requests
     *  in JSON format.
     *
     *  Example Body:
     *  {"title":"WreckingBall","artist":"MILEYCYRUS","label":"RCA","released":2013}
     *
     * @param request the request body (JSON) String
     * @param response
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        // Getting the Json String from the Body
        String requestBodyJson = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        ObjectMapper objectMapper = new ObjectMapper();
        PrintWriter responseWriter = response.getWriter();

        //Checking the Json String
        try
        {
            if (requestBodyJson != null)
            {
                // Song title and Artist need to be in the Request (Database NOT NULL)
                if (requestBodyJson.contains("title") && requestBodyJson.contains("artist"))
                {
                    // Building a POJO from the jsonString
                    Song song = objectMapper.readValue(requestBodyJson, Song.class);
                    // Check if both artist and title are there
                    if (song.getTitle().isEmpty() || song.getArtist().isEmpty())
                    {
                        responseWriter.print("Invalid request : Both, song title and artist needs to be declared");
                        response.setStatus(400);
                    }
                    else
                    {
                        // if everything is correct: inserting the song into the Database
                        databaseHelper.addSong(song);
                        response.setStatus(201);
                        response.setHeader("Location", "/songsservlet-WIMI/songs?songId=" + song.getId());
                    }
                }
                else
                    {
                    responseWriter.print("Invalid request : Not all necessary information found : declare at least title and artist");
                    response.setStatus(400);
                }
            }
            else
                {
                responseWriter.print("Invalid request : No information given");
                response.setStatus(400);
                }
        }
        catch (Exception exception)
        {
            responseWriter.print("Invalid request : An unknown exception occurred");
            response.setStatus(400);
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response)
    {
        // Method not allowed / implemented
        response.setStatus(405);
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response)
    {
        // Method not allowed / implemented
        response.setStatus(405);
    }


    /**
     *  just for testing
     * @param mock
     */
    public void insertMockDbHelper(DatabaseHelper mock)
    {
        this.databaseHelper = mock;
    }
}
