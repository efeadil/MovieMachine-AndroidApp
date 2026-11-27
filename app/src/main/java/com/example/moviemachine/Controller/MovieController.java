package com.example.moviemachine.Controller;

import com.example.moviemachine.Model.Movie;
import com.example.moviemachine.Model.CastMember;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller Class (MVC - C)
 * EN: Manages the coordination between Model and View.
 * TR: Model ve View arasındaki koordinasyonu sağlar.
 * SOLID: Adheres to Single Responsibility Principle.
 */
public class MovieController implements AutoCloseable {

    private static final String INVALID_RESPONSE = "Invalid API response.";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final String apiBaseUrl;

    public MovieController() {
        this("https://api.themoviedb.org/3/");
    }

    public MovieController(String apiBaseUrl) {
        if (apiBaseUrl == null || apiBaseUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("API base URL must not be empty.");
        }
        this.apiBaseUrl = apiBaseUrl.endsWith("/") ? apiBaseUrl : apiBaseUrl + "/";
    }

    public interface MovieCallback {
        void onSuccess(List<Movie> movies);
        void onError(String errorMessage);
    }

    public interface MovieDetailCallback {
        void onSuccess(String videoUrl, String primeUrl, List<CastMember> cast, String reviews);
        void onError(String errorMessage);
    }

    // --- CORE API METHODS (Satisfies Requirement #31: Movie Recommendations) ---

    /**
     * EN: Fetches movies from TMDB API based on genre filters.
     * TR: TMDB API'den film türlerine göre verileri çeker.
     */
    public void fetchMovies(String apiKey, String genreIds, MovieCallback callback) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            callback.onError("API key is missing.");
            return;
        }
        String url = apiBaseUrl + "discover/movie?api_key=" + apiKey +
                "&language=en-US&sort_by=popularity.desc&with_genres=" + genreIds;
        executeRequest(url, response -> deliverMovieList(response, callback), callback::onError);
    }

    /**
     * EN: Searches for movies using a keyword.
     * TR: Anahtar kelime kullanarak film araması yapar.
     */
    public void searchMovies(String apiKey, String query, MovieCallback callback) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            callback.onError("API key is missing.");
            return;
        }
        if (query == null || query.trim().isEmpty()) {
            callback.onError("Search query is missing.");
            return;
        }
        final String encodedQuery;
        try {
            encodedQuery = java.net.URLEncoder.encode(query.trim(), "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            callback.onError("Encoding error");
            return;
        }
        String url = apiBaseUrl + "search/multi?api_key=" + apiKey + "&query=" + encodedQuery;
        executeRequest(url, response -> deliverMovieList(response, callback), callback::onError);
    }

    /**
     * EN: Fetches details, trailers, and cast information for a specific movie/TV show.
     * TR: Belirli bir film veya dizi için detayları, fragmanları ve oyuncu kadrosunu çeker.
     */
    public void fetchMovieDetailsAndCast(int movieId, String mediaType, String apiKey, MovieDetailCallback callback) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            callback.onError("API key is missing.");
            return;
        }
        String type = ("tv".equals(mediaType)) ? "tv/" : "movie/";
        // EN: Added 'reviews' to append_to_response to satisfy Req #31.
        // TR: 31. maddeyi karşılamak için 'reviews' (yorumlar) eklendi.
        String url = apiBaseUrl + type + movieId + "?api_key=" + apiKey + "&append_to_response=credits,videos,watch/providers,external_ids,reviews";

        executeRequest(url, response -> {
            final String videoUrl, primeUrl, reviews;
            final List<CastMember> cast;
            try {
                JSONObject json = parseResponseObject(response);
                if (json.optInt("id", -1) != movieId) {
                    throw new JSONException("Unexpected movie ID.");
                }
                videoUrl = extractTrailerUrl(json);
                primeUrl = generatePrimeSearchUrl(json);
                cast = extractCast(json);
                reviews = extractReviews(json);
            } catch (Exception e) {
                callback.onError(INVALID_RESPONSE);
                return;
            }
            callback.onSuccess(videoUrl, primeUrl, cast, reviews);
        }, callback::onError);
    }

    // --- HELPER METHODS ---

    /**
     * EN: Executes the network request on a background thread.
     * TR: Ağ isteğini arka planda (background thread) çalıştırır.
     */
    private void executeRequest(String urlString, ResponseListener listener, ErrorListener errorListener) {
        executorService.execute(() -> {
            HttpURLConnection conn = null;
            String response = null;
            String error = null;
            try {
                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(20000);
                conn.setReadTimeout(30000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader r = new BufferedReader(new InputStreamReader(
                            conn.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder res = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) res.append(line);
                        response = res.toString();
                    }
                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    error = "Invalid or missing API key.";
                } else {
                    error = "Server error: " + responseCode;
                }
            } catch (IOException | SecurityException e) {
                // Do not expose request URLs (which contain the API key) in error messages.
                error = "Network error. Please check your connection and try again.";
            } finally {
                if (conn != null) conn.disconnect();
            }
            // Keep callbacks outside the network try/catch to avoid double delivery.
            if (error != null) errorListener.onError(error);
            else listener.onResponse(response);
        });
    }

    private void deliverMovieList(String response, MovieCallback callback) {
        final List<Movie> movies;
        try {
            movies = parseMoviesFromJson(response);
        } catch (IllegalArgumentException e) {
            callback.onError(INVALID_RESPONSE);
            return;
        }
        callback.onSuccess(movies);
    }

    private JSONObject parseResponseObject(String jsonString) throws JSONException {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            throw new JSONException("Empty response.");
        }
        JSONTokener tokener = new JSONTokener(jsonString);
        Object value = tokener.nextValue();
        if (!(value instanceof JSONObject) || tokener.nextClean() != 0) {
            throw new JSONException("Expected one JSON object.");
        }
        JSONObject response = (JSONObject) value;
        if (response.has("success") && !response.optBoolean("success", true)) {
            throw new JSONException("API reported an error.");
        }
        return response;
    }

    /**
     * Returns an empty list only for a valid response with no supported results.
     * Invalid payloads fail as a whole; a partial list is never reported as success.
     * Optional movie fields retain safe defaults.
     */
    public List<Movie> parseMoviesFromJson(String jsonString) {
        List<Movie> movies = new ArrayList<>();
        try {
            JSONObject response = parseResponseObject(jsonString);
            JSONArray results = response.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject obj = results.getJSONObject(i);
                String mediaType = obj.optString("media_type", "movie");
                // TMDB multi-search also returns people, which are not playable titles.
                if (!"movie".equals(mediaType) && !"tv".equals(mediaType)) continue;
                int id = obj.optInt("id", -1);
                if (id <= 0) throw new JSONException("Invalid movie ID.");
                String posterPath = obj.isNull("poster_path") ? null : obj.optString("poster_path", null);
                Movie movie = new Movie(id,
                        obj.optString("title", obj.optString("name", "Unknown")),
                        obj.optString("overview", ""), posterPath,
                        obj.optDouble("vote_average", 0.0));
                movie.setMediaType(mediaType);
                movies.add(movie);
            }
        } catch (JSONException e) {
            throw new IllegalArgumentException(INVALID_RESPONSE, e);
        }
        return movies;
    }

    private String extractTrailerUrl(JSONObject json) throws Exception {
        if (!json.has("videos")) return "";
        JSONArray videos = json.getJSONObject("videos").getJSONArray("results");
        for (int i = 0; i < videos.length(); i++) {
            JSONObject v = videos.getJSONObject(i);
            if ("YouTube".equals(v.getString("site")) && "Trailer".equals(v.getString("type"))) {
                return "https://www.youtube.com/watch?v=" + v.getString("key");
            }
        }
        return "";
    }

    private String generatePrimeSearchUrl(JSONObject json) throws Exception {
        String title = json.optString("title", json.optString("name", ""));
        return "https://www.primevideo.com/search/?phrase=" + java.net.URLEncoder.encode(title, "UTF-8");
    }

    public List<CastMember> extractCast(JSONObject json) throws Exception {
        List<CastMember> cast = new ArrayList<>();
        if (!json.has("credits")) return cast;
        JSONArray array = json.getJSONObject("credits").getJSONArray("cast");
        for (int i = 0; i < Math.min(array.length(), 10); i++) {
            JSONObject obj = array.getJSONObject(i);
            cast.add(new CastMember(obj.getString("name"), obj.optString("profile_path", null)));
        }
        return cast;
    }

    public String extractReviews(JSONObject json) throws JSONException {
        if (json.has("reviews")) {
            JSONArray reviewsArray = json.getJSONObject("reviews").getJSONArray("results");
            StringBuilder sb = new StringBuilder();
            int count = Math.min(reviewsArray.length(), 3);
            for (int i = 0; i < count; i++) {
                JSONObject reviewObj = reviewsArray.getJSONObject(i);
                String author = reviewObj.optString("author", "Anonymous");
                String content = reviewObj.optString("content", "");

                if (content.length() > 120) {
                    content = content.substring(0, 117) + "...";
                }
                sb.append("• ").append(author).append(": \"").append(content.trim()).append("\"\n\n");
            }
            return sb.length() > 0 ? sb.toString().trim() : "No reviews available.";
        }
        return "No reviews available.";
    }

    interface ResponseListener { void onResponse(String response); }
    interface ErrorListener { void onError(String error); }

    @Override
    public void close() {
        executorService.shutdownNow();
    }
}
