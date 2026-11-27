package com.example.moviemachine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.moviemachine.Controller.MovieController;
import com.example.moviemachine.Model.Movie;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class MovieControllerHttpTest {

    private MockWebServer server;
    private MovieController controller;

    @Before
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        controller = new MovieController(server.url("/3/").toString());
    }

    @After
    public void tearDown() throws IOException {
        controller.close();
        server.shutdown();
    }

    @Test
    public void fetchMovies_sendsExpectedRequestAndReturnsParsedApiResponse() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"results\":[{" +
                        "\"id\":550," +
                        "\"title\":\"Fight Club\"," +
                        "\"overview\":\"An insomniac office worker.\"," +
                        "\"poster_path\":\"/fight-club.jpg\"," +
                        "\"vote_average\":8.4" +
                        "}]}"));

        CountDownLatch callbackLatch = new CountDownLatch(1);
        AtomicReference<List<Movie>> result = new AtomicReference<>();
        AtomicReference<String> error = new AtomicReference<>();
        MovieController.MovieCallback callback = mock(MovieController.MovieCallback.class);
        doAnswer(invocation -> {
            result.set(invocation.getArgument(0));
            callbackLatch.countDown();
            return null;
        }).when(callback).onSuccess(anyList());
        doAnswer(invocation -> {
            error.set(invocation.getArgument(0));
            callbackLatch.countDown();
            return null;
        }).when(callback).onError(anyString());

        controller.fetchMovies("test-api-key", "28,12", callback);

        assertTrue("The fetch callback was not invoked.",
                callbackLatch.await(5, TimeUnit.SECONDS));
        verify(callback).onSuccess(anyList());
        verify(callback, never()).onError(anyString());
        assertNull("Expected success callback, got: " + error.get(), error.get());
        assertNotNull(result.get());
        assertEquals(1, result.get().size());

        Movie movie = result.get().get(0);
        assertEquals(550, movie.getId());
        assertEquals("Fight Club", movie.getTitle());
        assertEquals("An insomniac office worker.", movie.getOverview());
        assertEquals("/fight-club.jpg", movie.getPosterPath());
        assertEquals(8.4, movie.getVoteAverage(), 0.001);

        RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);
        assertNotNull("Controller did not send an HTTP request.", request);
        assertEquals("GET", request.getMethod());
        assertEquals("/3/discover/movie", request.getRequestUrl().encodedPath());
        assertEquals("test-api-key", request.getRequestUrl().queryParameter("api_key"));
        assertEquals("en-US", request.getRequestUrl().queryParameter("language"));
        assertEquals("popularity.desc", request.getRequestUrl().queryParameter("sort_by"));
        assertEquals("28,12", request.getRequestUrl().queryParameter("with_genres"));
    }

    @Test
    public void fetchMovies_unauthorizedResponseInvokesErrorCallback() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401));

        CountDownLatch callbackLatch = new CountDownLatch(1);
        AtomicReference<List<Movie>> result = new AtomicReference<>();
        AtomicReference<String> error = new AtomicReference<>();
        MovieController.MovieCallback callback = mock(MovieController.MovieCallback.class);
        doAnswer(invocation -> {
            error.set(invocation.getArgument(0));
            callbackLatch.countDown();
            return null;
        }).when(callback).onError(anyString());
        doAnswer(invocation -> {
            result.set(invocation.getArgument(0));
            callbackLatch.countDown();
            return null;
        }).when(callback).onSuccess(anyList());

        controller.fetchMovies("invalid-key", "28", callback);

        assertTrue("The error callback was not invoked.",
                callbackLatch.await(5, TimeUnit.SECONDS));
        verify(callback).onError("Invalid or missing API key.");
        verify(callback, never()).onSuccess(anyList());
        assertNull(result.get());
        assertEquals("Invalid or missing API key.", error.get());
        assertNotNull(server.takeRequest(1, TimeUnit.SECONDS));
    }

    @Test
    public void fetchMovies_missingApiKeyReturnsErrorWithoutSendingRequest() {
        MovieController.MovieCallback callback = mock(MovieController.MovieCallback.class);

        controller.fetchMovies(" ", "28", callback);

        verify(callback).onError("API key is missing.");
        verify(callback, never()).onSuccess(anyList());
        assertEquals(0, server.getRequestCount());
    }
    @Test
    public void fetchMovies_malformedResponseInvokesErrorOnly() throws Exception {
        verifyListResponse("{\"results\":[{\"id\":1},42]}", false, true);
    }

    @Test
    public void searchMovies_missingResultsInvokesErrorOnly() throws Exception {
        verifyListResponse("{}", true, true);
    }

    @Test
    public void searchMovies_validEmptyResponseInvokesSuccessOnly() throws Exception {
        verifyListResponse("{\"results\":[]}", true, false);
    }

    private void verifyListResponse(String body, boolean search, boolean expectError) throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200)
                .addHeader("Content-Type", "application/json").setBody(body));
        CountDownLatch latch = new CountDownLatch(1);
        MovieController.MovieCallback callback = mock(MovieController.MovieCallback.class);
        AtomicReference<List<Movie>> movies = new AtomicReference<>();
        doAnswer(invocation -> {
            movies.set(invocation.getArgument(0));
            latch.countDown();
            return null;
        }).when(callback).onSuccess(anyList());
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(callback).onError(anyString());
        if (search) controller.searchMovies("test-api-key", "Batman", callback);
        else controller.fetchMovies("test-api-key", "28", callback);
        assertTrue("No callback received.", latch.await(5, TimeUnit.SECONDS));
        if (expectError) {
            verify(callback).onError("Invalid API response.");
            verify(callback, never()).onSuccess(anyList());
        } else {
            verify(callback).onSuccess(anyList());
            verify(callback, never()).onError(anyString());
            assertNotNull(movies.get());
            assertTrue(movies.get().isEmpty());
        }
        assertNotNull(server.takeRequest(1, TimeUnit.SECONDS));
    }

    @Test
    public void fetchMovieDetails_malformedReviewsInvokesErrorOnly() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody("{\"id\":550,\"title\":\"Fight Club\",\"reviews\":{\"results\":null}}"));
        CountDownLatch latch = new CountDownLatch(1);
        MovieController.MovieDetailCallback callback = mock(MovieController.MovieDetailCallback.class);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(callback).onError(anyString());
        controller.fetchMovieDetailsAndCast(550, "movie", "test-api-key", callback);
        assertTrue("No error callback received.", latch.await(5, TimeUnit.SECONDS));
        verify(callback).onError("Invalid API response.");
        verify(callback, never()).onSuccess(anyString(), anyString(), anyList(), anyString());
    }

}
