package com.example.moviemachine;

import com.example.moviemachine.Model.Movie;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * EN: Unit Test to verify if Model classes hold data correctly.
 * TR: Model sınıflarının veriyi doğru tutup tutmadığını kontrol eden Unit Test.
 */
public class MovieLogicTest {

    @Test
    public void testMovieDataAssignment() {
        // EN: Scenario - Correct assignment of movie data.
        // TR: Film verilerinin doğru atanması senaryosu.
        Movie movie = new Movie(550, "Fight Club", "An insomniac office worker...", "/path.jpg", 8.4);
        
        assertEquals("Fight Club", movie.getTitle());
        assertEquals(550, movie.getId());
        assertEquals(8.4, movie.getVoteAverage(), 0.1);
    }

    @Test
    public void testMovieModelIntegrity() {
        // EN: Verifies default values in the Movie model.
        // TR: Movie modelindeki varsayılan değerleri kontrol eder.
        Movie movie = new Movie(123, "The Dark Knight", "A gritty movie.", "/path.jpg", 9.0);
        assertEquals("movie", movie.getMediaType());
    }

    @Test
    public void testMovieObjectNullSafety() {
        // EN: Checks object behavior with null inputs.
        // TR: Null girdilerle nesne davranışını kontrol eder.
        Movie emptyMovie = new Movie(0, null, "", null, 0.0);
        assertNull(emptyMovie.getTitle());
        assertEquals(0.0, emptyMovie.getVoteAverage(), 0.0);
    }
}
