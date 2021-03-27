package htwb.ai.willi.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import htwb.ai.willi.entity.Lyric;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;

/**
 * The LyricsRepository persists the lyrics to the local file system.
 * Lyrics will be persisted as JSON string in .txt files.
 * this class can add , deleted and find those files.
 */
@Repository
@Slf4j
public class LyricsRepository
{

     public static final String RUNTIME_DIRECTORY = "C:\\Users\\willi\\songLyrics\\";

     public static final String TEST_DIRECTORY = "C:\\Users\\willi\\songLyricsTest\\";


     //-----------INSTANCE VARIABLES -----------//

     /**
      * The logger
      */
     private Logger log = LoggerFactory.getLogger(LyricsRepository.class);

     /**
      * The path of the directory in use
      * can be either <code>RUNTIME_DIRECTORY</code>
      * or <code>TEST_DIRECTORY</code>
      */
     private String currentDirectoryPath;


     //-----------CONSTRUCTORS -----------//


     public LyricsRepository()
     {
          currentDirectoryPath = RUNTIME_DIRECTORY;
     }


     //-----------ADD UPDATE DELETE AND GET LYRICS -----------//


     /**
      * Returns a Lyrics Object created from the file, if the file exists and
      * its content is a matching json string
      * Else returns null.
      *
      * @param name
      *
      * @return
      *
      * @throws IOException
      */
     public Lyric getLyrics(String name) throws IOException
     {
          log.info("getLyrics: called with name " + name);
          if (doesFileExist(name))
          {
               log.info("getLyrics: File exists, reading from file " + name);
               FileReader fileReader = new FileReader(getFile(name));
               BufferedReader bf = new BufferedReader(fileReader, 10000);
               String Json = bf.readLine();
               log.info("getLyrics: Read the following JSON string from File: " + Json);
               return asLyricObject(Json);
          }
          else
          {
               return null;
          }

     }


     public ArrayList<Lyric> getAll() throws IOException
     {
          File directory = new File("C:\\Users\\willi\\songLyrics");
          String[] files = directory.list();
          log.info("getAll: found files in diorectory : " + files.toString());
          ArrayList<Lyric> lyricsList = new ArrayList<>();

          for (String name : files)
          {
               lyricsList.add(getLyrics(name));
          }

          return lyricsList;
     }

     public void addLyrics(Lyric lyric) throws IOException
     {
          if (doesFileExist(lyric.getSongTitle()))
          {
               createFile(lyric.getSongTitle());
          }
          FileWriter fileWriter = new FileWriter(getFile(lyric.getSongTitle()));
          fileWriter.write(asJsonString(lyric));
          fileWriter.flush();
          fileWriter.close();
     }


     public int deleteLyrics(String songName)
     {
          if (doesFileExist(songName))
          {
               File songToDelete = getFile(songName);
               if (songToDelete.delete())
               {
                    return 0; // Song successfully Deleted
               }
               else
               {
                    return 2; // Could delete
               }
          }
          return 1; // song doesnt exist
     }

     public void updateLyrics(String name)
     {

     }

     public void getBySongID(int id)
     {

     }


     //-----------FILE MANAGEMENT-----------//


     private Boolean doesFileExist(String name)
     {
          log.info("doesFileExist: checking if file " + name + " exist");
          File file = new File(getPathByName(name));
          if (file.exists())
          {
               log.info("doesFileExist: file with name " + name + " exists");
               return true;
          }
          log.info("doesFileExist: file with name " + name + " doesnt exists");
          return false;
     }

     private File getFile(String name)
     {
          log.info("getFile: called with name " + name);
          File file = new File(getPathByName(name));

          log.info("getFile: lyrics with name " + name + " found in files");
          return file;
     }

     private File createFile(String name) throws IOException
     {
          log.info("createFile: called with name " + name);

          File file = new File(getPathByName(name));
          if (file.exists())
          {
               log.info("createFile: lyrics with name " + name + " already in files");
          }
          else
          {
               log.info("createFile: created new file with name " + name);
               file.createNewFile();
          }
          return file;
     }

     private String getPathByName(String name)
     {
          return currentDirectoryPath + name;
     }


     //-----------JSON TO POJO-----------//


     /**
      * Converts a POJO to a JSON string
      *
      * @param obj
      *         the object
      *
      * @return the objects as JSON string
      */
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

     /**
      * Converts a JSON string into a {@link Lyric} POJO
      *
      * @param string
      *         The JSON string
      *
      * @return A lyrics Object generated from the JSON string
      */
     private Lyric asLyricObject(String string)
     {
          try
          {
               return new ObjectMapper().readValue(string, Lyric.class);
          }
          catch (Exception e)
          {
               throw new RuntimeException(e);
          }
     }


     //-----------OTHERS-----------//


     /**
      * Used to change the working directory
      * for example fore testing.
      * @param path
      */
     public void setDirectory(String path)
     {
          this.currentDirectoryPath = path;
     }

}