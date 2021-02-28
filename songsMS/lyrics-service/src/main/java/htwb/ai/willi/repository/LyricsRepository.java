package htwb.ai.willi.repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import htwb.ai.willi.controller.LyricController;
import htwb.ai.willi.entity.Lyric;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

@Repository
@Slf4j
public class LyricsRepository
{

     private Logger log = LoggerFactory.getLogger(LyricsRepository.class);

     /**
      * Returns a Lyrics Object created from the file, if the file exists and
      * its content is a matching json string
      * Else returns null.
      *
      * @param name
      * @return
      * @throws IOException
      */
     public Lyric getLyrics(String name) throws IOException
     {
          log.info("getLyrics: called with name " + name);
          if(doesFileExist(name))
          {
               log.info("getLyrics: File exists, reading from file " + name);
               FileReader fileReader = new FileReader(getFile(name));
               BufferedReader bf = new BufferedReader(fileReader, 10000);
               String Json = bf.readLine();
               log.info("getLyrics: Read teh following JSON string from File: " + Json);
               return asLyricObject(Json);
          }
          else
          {
               return null;
          }

     }


     public void addLyrics(Lyric lyric) throws IOException
     {
          FileWriter fileWriter = new FileWriter(getFile(lyric.getSongTitle()));
          fileWriter.write(asJsonString(lyric));
          fileWriter.flush();
          fileWriter.close();
     }


     public void deleteLyrics(String name)
     {

     }

     public void updateLyrics(String name)
     {

     }

     public void  getBySongID(int id)
     {

     }

     private Boolean doesFileExist(String name)
     {
          log.info("doesFileExist: checking if file "+ name + " exist");
          File file =  new File( getPathByName(name) );
          if(file.exists())
          {
               log.info("doesFileExist: file with name " + name + " exists");
               return true;
          }
          log.info("doesFileExist: file with name " + name + " doesnt exists");
          return  false;
     }

     private File getFile(String name)
     {
          log.info("getFile: called with name " + name);
          File file =  new File( getPathByName(name) );

          log.info("getFile: lyrics with name " + name  + " found in files");
          return file;
     }

     private File createFile(String name) throws IOException
     {
          log.info("createFile: called with name " + name);

          File file =  new File( getPathByName(name) );
          if(file.exists())
          {
               log.info("createFile: lyrics with name " + name  + " already in files");
          }
          else
          {
               log.info("createFile: craeted new file with name " + name);
               file.createNewFile();
          }
          return file;
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

     private String getPathByName(String name)
     {
          return "lyricsRes/" + name;
     }

     public ArrayList<Lyric> getAll() throws IOException
     {
         File directory = new File("lyricsRes");
         File[] files = directory.listFiles();
         ArrayList<Lyric> lyricsList = new ArrayList<>();

          for (File f: files)
          {
               FileReader fileReader = new FileReader(f);
               BufferedReader bf = new BufferedReader(fileReader, 10000);
               String Json = bf.readLine();
               log.info("getLyrics: Read teh following JSON string from File: " + Json);
               lyricsList.add(asLyricObject(Json));
          }

          return  lyricsList;
     }
}
