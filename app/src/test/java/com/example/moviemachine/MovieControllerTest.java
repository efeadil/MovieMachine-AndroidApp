package com.example.moviemachine;

import com.example.moviemachine.Controller.MovieController;
import com.example.moviemachine.Model.Movie;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

/**
 * EN: Tests the data processing and error handling logic of MovieController without an internet connection.
 * TR: MovieController'ın veri işleme ve hata yönetimi mantığını test eder.
 */
public class MovieControllerTest {

    private MovieController movieController;

    @Before
    public void setUp() {
        movieController = new MovieController();
    }

    @Test
    public void testParseMoviesFromJson_WithValidData() {
        // EN: Tests whether valid JSON data is parsed correctly.
        // TR: Geçerli JSON verisinin doğru şekilde parse edilip edilmediğini test eder.
        String jsonInput = "{\"results\": [" +
                "{\"id\": 1, \"title\": \"Inception\", \"overview\": \"A dream within a dream\", \"poster_path\": \"/p1.jpg\", \"vote_average\": 8.8}," +
                "{\"id\": 2, \"title\": \"Interstellar\", \"overview\": \"Space travel\", \"poster_path\": \"/p2.jpg\", \"vote_average\": 8.6}" +
                "]}";

        List<Movie> movies = movieController.parseMoviesFromJson(jsonInput);

        assertNotNull(movies);
        assertEquals(2, movies.size());
        assertEquals("Inception", movies.get(0).getTitle());
        assertEquals(1, movies.get(0).getId());
        assertEquals(8.6, movies.get(1).getVoteAverage(), 0.1);
    }

    @Test
    public void testParseMoviesFromJson_WithMissingFields() {
        // EN: Tests that the app doesn't crash and uses default values when some fields are missing.
        // TR: Eksik alanlarda uygulamanın çökmediğini ve varsayılan değerleri kullandığını test eder.
        String jsonInput = "{\"results\": [" +
                "{\"id\": 101, \"title\": \"Broken Movie\"}," + 
                "{\"name\": \"TV Show\", \"id\": 202}" + 
                "]}";

        List<Movie> movies = movieController.parseMoviesFromJson(jsonInput);

        assertNotNull(movies);
        assertEquals(2, movies.size());
        assertEquals("Broken Movie", movies.get(0).getTitle());
        assertEquals("TV Show", movies.get(1).getTitle());
    }

    @Test
    public void testParseMoviesFromJson_WithEmptyResults() {
        // EN: Tests that an empty list is returned when the API returns empty results.
        // TR: API'den boş bir sonuç listesi geldiğinde listenin boş döndüğünü test eder.
        String jsonInput = "{\"results\": []}";

        List<Movie> movies = movieController.parseMoviesFromJson(jsonInput);

        assertNotNull(movies);
        assertTrue(movies.isEmpty());
    }

    @Test
    public void testParseMoviesFromJson_WithInvalidJson() {
        // EN: Tests that errors are handled correctly when the input is not a valid JSON.
        // TR: Geçersiz JSON formatında hatanın yakalandığını test eder.
        String jsonInput = "Not a valid JSON string!";

        List<Movie> movies = movieController.parseMoviesFromJson(jsonInput);

        assertNotNull(movies);
        assertTrue(movies.isEmpty());
    }

    @Test
    public void testParseMoviesFromJson_WithNullInput() {
        // EN: Tests that the app safely returns an empty list if the input data is null.
        // TR: Giriş verisinin null olması durumunda güvenli bir şekilde boş liste döndüğünü test eder.
        List<Movie> movies = movieController.parseMoviesFromJson(null);

        assertNotNull(movies);
        assertTrue(movies.isEmpty());
    }
}
