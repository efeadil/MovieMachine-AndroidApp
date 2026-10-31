package com.example.moviemachine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.moviemachine.Controller.MovieController;
import com.example.moviemachine.Model.Movie;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Local unit tests for deterministic MovieController parsing and formatting rules.
 */
public class MovieControllerTest {

    private MovieController movieController;

    @Before
    public void setUp() {
        movieController = new MovieController();
    }

    @Test
    public void parseMovies_validData_mapsAllMovieFields() {
        String json = "{\"results\":[" +
                "{\"id\":1,\"title\":\"Inception\",\"overview\":\"A dream within a dream\"," +
                "\"poster_path\":\"/p1.jpg\",\"vote_average\":8.8,\"media_type\":\"movie\"}," +
                "{\"id\":2,\"title\":\"Interstellar\",\"overview\":\"Space travel\"," +
                "\"poster_path\":\"/p2.jpg\",\"vote_average\":8.6}" +
                "]}";

        List<Movie> movies = movieController.parseMoviesFromJson(json);

        assertEquals(2, movies.size());
        Movie first = movies.get(0);
        assertEquals(1, first.getId());
        assertEquals("Inception", first.getTitle());
        assertEquals("A dream within a dream", first.getOverview());
        assertEquals("/p1.jpg", first.getPosterPath());
        assertEquals(8.8, first.getVoteAverage(), 0.001);
        assertEquals("movie", first.getMediaType());

        Movie second = movies.get(1);
        assertEquals(2, second.getId());
        assertEquals(8.6, second.getVoteAverage(), 0.001);
    }

    @Test
    public void parseMovies_missingFields_usesControllerDefaults() {
        String json = "{\"results\":[" +
                "{\"id\":101,\"title\":\"Broken Movie\"}," +
                "{\"id\":202,\"name\":\"TV Show\"}" +
                "]}";

        List<Movie> movies = movieController.parseMoviesFromJson(json);

        assertEquals(2, movies.size());
        Movie movie = movies.get(0);
        assertEquals(101, movie.getId());
        assertEquals("Broken Movie", movie.getTitle());
        assertEquals("", movie.getOverview());
        assertNull(movie.getPosterPath());
        assertEquals(0.0, movie.getVoteAverage(), 0.0001);
        assertEquals("movie", movie.getMediaType());

        assertEquals("TV Show", movies.get(1).getTitle());
    }

    @Test
    public void parseMovies_nullPosterAndIntegerRating_areParsedSafely() {
        String json = "{\"results\":[{" +
                "\"id\":303,\"title\":\"Integer Rating\",\"overview\":\"Overview\"," +
                "\"poster_path\":null,\"vote_average\":8}]}";

        List<Movie> movies = movieController.parseMoviesFromJson(json);

        assertEquals(1, movies.size());
        assertNull(movies.get(0).getPosterPath());
        assertEquals(8.0, movies.get(0).getVoteAverage(), 0.0001);
    }

    @Test
    public void parseMovies_missingResultsKey_returnsEmptyList() {
        assertTrue(movieController.parseMoviesFromJson("{}").isEmpty());
    }

    @Test
    public void parseMovies_nullResults_returnsEmptyList() {
        assertTrue(movieController.parseMoviesFromJson("{\"results\":null}").isEmpty());
    }

    @Test
    public void parseMovies_emptyResults_returnsEmptyList() {
        assertTrue(movieController.parseMoviesFromJson("{\"results\":[]}").isEmpty());
    }

    @Test
    public void parseMovies_invalidJson_returnsEmptyList() {
        assertNotNull(movieController.parseMoviesFromJson("Not a valid JSON string!"));
        assertTrue(movieController.parseMoviesFromJson("Not a valid JSON string!").isEmpty());
    }

    @Test
    public void parseMovies_nullInput_returnsEmptyList() {
        assertNotNull(movieController.parseMoviesFromJson(null));
        assertTrue(movieController.parseMoviesFromJson(null).isEmpty());
    }

    @Test
    public void extractReviews_formatsAuthorsAndContentExactly() throws Exception {
        JSONObject json = new JSONObject(
                "{\"reviews\":{\"results\":[" +
                        "{\"author\":\"Adil\",\"content\":\"Amazing movie!\"}," +
                        "{\"author\":\"Efe\",\"content\":\"Short review\"}" +
                        "]}}");

        String result = movieController.extractReviews(json);

        assertEquals("• Adil: \"Amazing movie!\"\n\n• Efe: \"Short review\"", result);
    }

    @Test
    public void extractReviews_exactly120Characters_isNotTruncated() throws Exception {
        String exact = repeat('a', 120);
        JSONObject json = reviewsJson("Adil", exact);

        String result = movieController.extractReviews(json);

        assertEquals("• Adil: \"" + exact + "\"", result);
        assertFalse(result.contains("..."));
    }

    @Test
    public void extractReviews_121Characters_isTruncatedTo120Characters() throws Exception {
        String overLimit = repeat('a', 121);
        JSONObject json = reviewsJson("Adil", overLimit);

        String result = movieController.extractReviews(json);

        assertEquals("• Adil: \"" + repeat('a', 117) + "...\"", result);
        assertEquals(120, result.substring(result.indexOf('"') + 1, result.lastIndexOf('"')).length());
    }

    @Test
    public void extractReviews_missingAuthor_usesAnonymous() throws Exception {
        JSONObject json = new JSONObject(
                "{\"reviews\":{\"results\":[{\"content\":\"A review without an author\"}]}}");

        assertEquals("• Anonymous: \"A review without an author\"",
                movieController.extractReviews(json));
    }

    @Test
    public void extractReviews_emptyResults_returnsNoReviewsMessage() throws Exception {
        JSONObject json = new JSONObject("{\"reviews\":{\"results\":[]}}");

        assertEquals("No reviews available.", movieController.extractReviews(json));
    }

    @Test
    public void extractReviews_missingReviews_returnsNoReviewsMessage() throws Exception {
        assertEquals("No reviews available.",
                movieController.extractReviews(new JSONObject("{}")));
    }

    private static JSONObject reviewsJson(String author, String content) throws Exception {
        return new JSONObject("{\"reviews\":{\"results\":[{" +
                "\"author\":\"" + author + "\",\"content\":\"" + content + "\"}]}}");
    }

    private static String repeat(char character, int count) {
        StringBuilder value = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            value.append(character);
        }
        return value.toString();
    }
}
