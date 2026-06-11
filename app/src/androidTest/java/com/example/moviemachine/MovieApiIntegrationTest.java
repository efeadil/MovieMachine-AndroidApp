package com.example.moviemachine;

import com.example.moviemachine.Controller.MovieController;
import com.example.moviemachine.Model.Movie;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

/**
 * EN: Integration Test that verifies API data flow using real/sample data.
 * TR: Gerçek veya örnek veriler kullanarak API veri akışını doğrulayan Entegrasyon Testi.
 */
public class MovieApiIntegrationTest {

    @Test
    public void testRealApiFetch_ReturnsData() {
        MovieController controller = new MovieController();
        
        // EN: Try to parse data from a sample API response.
        // TR: Örnek bir API cevabından verileri ayrıştırmayı dener.
        List<Movie> movies = controller.parseMoviesFromJson(fetchSampleJson()); 

        assertNotNull("Data from API should not be null!", movies);
        assertTrue("Movie list should not be empty!", movies.size() > 0);
        
        assertFalse(movies.get(0).getTitle().isEmpty());
    }

    private String fetchSampleJson() {
        return "{\"results\": [{\"id\": 1, \"title\": \"Integration Test Movie\", \"overview\": \"Success\", \"poster_path\": \"/test.jpg\", \"vote_average\": 10.0}]}";
    }
}
