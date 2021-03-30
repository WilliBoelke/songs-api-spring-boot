package htwb.ai.willi.enitity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SongTest
{

     private Song testSong1;
     private Song testSong2;

     @BeforeEach
     void setUp()
     {
          testSong1 = new Song(1, "testSong1", "testArtist1", "testLabel1", 1901);
          testSong2 = new Song(2, "testSong2", "testArtist2", "testLabel2", 1902);
     }

     @Test
     void getTitle()
     {
          assertEquals("testSong1", testSong1.getTitle());
          assertEquals("testSong2", testSong2.getTitle());
     }

     @Test
     void setTitle()
     {
          testSong1.setTitle("newTitle");
          assertEquals("newTitle", testSong1.getTitle());
          assertEquals("testSong2", testSong2.getTitle());
     }

     @Test
     void getArtist()
     {
          assertEquals("testArtist1", testSong1.getArtist());
          assertEquals("testArtist2", testSong2.getArtist());
     }

     @Test
     void setArtist()
     {
          testSong1.setArtist("newArtist");
          assertEquals("newArtist", testSong1.getArtist());
          assertEquals("testArtist2", testSong2.getArtist());
     }

     @Test
     void getLabel()
     {
          assertEquals("testLabel1", testSong1.getLabel());
          assertEquals("testLabel2", testSong2.getLabel());
     }

     @Test
     void setLabel()
     {
          testSong1.setLabel("newLabel");
          assertEquals("newLabel", testSong1.getLabel());
          assertEquals("testLabel2", testSong2.getLabel());
     }

     @Test
     void getReleased()
     {
          assertEquals(1901, testSong1.getReleased());
          assertEquals(1902, testSong2.getReleased());
     }

     @Test
     void setReleased()
     {
          testSong1.setReleased(1903);
          assertEquals(1903, testSong1.getReleased());
          assertEquals(1902, testSong2.getReleased());
     }

     @Test
     void getId()
     {
          assertEquals(1, testSong1.getId());
          assertEquals(2, testSong2.getId());
     }

     @Test
     void setId()
     {
          testSong1.setId(3);
          assertEquals(3, testSong1.getId());
          assertEquals(2, testSong2.getId());
     }

     @Test
     void builder()
     {
          Song song =
                  new Song(Song.builder().withId(3).withArtist("testArtist3").withLabel("testLabel3").withReleased(1903).withTitle("testTitle3"));
          assertEquals(1903, song.getReleased());
          assertEquals("testArtist3", song.getArtist());
          assertEquals("testLabel3", song.getLabel());
          assertEquals("testTitle3", song.getTitle());
          assertEquals(3, song.getId());

     }


}