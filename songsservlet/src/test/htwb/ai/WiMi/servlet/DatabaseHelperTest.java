package htwb.ai.WiMi.servlet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;


class DatabaseHelperTest
{

    EntityManager mockEntityManger;
    TypedQuery mockTQ;
    DatabaseHelper testDatabaseHelper;
    Song testSong;

    @BeforeEach
    public void setup()
    {
        testSong = new Song();
        testSong.setId(1);
        testSong.setTitle("testTitel");
        testSong.setArtist("testartist");
        testSong.setLabel("testLable");
        testSong.setReleased(2020);
        testDatabaseHelper = new DatabaseHelper();
        mockEntityManger = Mockito.mock(EntityManager.class);
        mockTQ = Mockito.mock(TypedQuery.class);
        testDatabaseHelper.insertMockEntityManager(mockEntityManger);
    }


    @Test
    public void getSongValidID()
    {
        int id = 1;
        String query = "SELECT c FROM Song c WHERE c.id = (:id)";
        Mockito.when(mockEntityManger.createQuery(query, Song.class)).thenReturn(mockTQ);
        Mockito.when(mockTQ.getSingleResult()).thenReturn(testSong);
        Song result =testDatabaseHelper.getSong(id);

        Assertions.assertTrue(testSong.compareTo(result)> 0);
        Mockito.verify(mockEntityManger.createQuery(query, Song.class));
    }


}