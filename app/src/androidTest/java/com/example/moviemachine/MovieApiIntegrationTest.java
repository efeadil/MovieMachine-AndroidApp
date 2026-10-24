package com.example.moviemachine;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.moviemachine.Controller.MovieController;
import com.example.moviemachine.Model.Movie;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

/**
 * EN: Verifies the complete controller-to-TMDB API flow on a connected device.
 * TR: Bağlı cihazda controller ile TMDB API arasındaki gerçek akışı doğrular.
 */
@RunWith(AndroidJUnit4.class)
public class MovieApiIntegrationTest {

    @Test
    public void testRealApiFetch_ReturnsMovies() throws InterruptedException {
        assertFalse(
                "tmdb_api_key must be set in local.properties for this integration test.",
                BuildConfig.TMDB_API_KEY.trim().isEmpty());

        MovieController controller = new MovieController();
        CountDownLatch callbackLatch = new CountDownLatch(1);
        AtomicReference<List<Movie>> result = new AtomicReference<>();
        AtomicReference<String> error = new AtomicReference<>();

        controller.fetchMovies(BuildConfig.TMDB_API_KEY, "28", new MovieController.MovieCallback() {
            @Override
            public void onSuccess(List<Movie> movies) {
                result.set(movies);
                callbackLatch.countDown();
            }

            @Override
            public void onError(String errorMessage) {
                error.set(errorMessage);
                callbackLatch.countDown();
            }
        });

        assertTrue("TMDB request did not finish within 45 seconds.",
                callbackLatch.await(45, TimeUnit.SECONDS));
        assertNull("TMDB request failed: " + error.get(), error.get());
        assertNotNull("TMDB success callback returned no movie list.", result.get());
        assertFalse("TMDB returned no movies for the action genre.", result.get().isEmpty());
        assertTrue("Returned movie ID must be positive.", result.get().get(0).getId() > 0);
        assertFalse("Returned movie title must not be empty.",
                result.get().get(0).getTitle().trim().isEmpty());
    }
}
