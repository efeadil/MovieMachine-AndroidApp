package com.example.moviemachine.Controller;

import com.example.moviemachine.Model.Movie;
import com.example.moviemachine.Model.CastMember;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
public class MovieController {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public interface MovieCallback {
        void onSuccess(List<Movie> movies);
        void onError(String errorMessage);
    }

    public interface MovieDetailCallback {
        void onSuccess(String videoUrl, String primeUrl, List<CastMember> cast);
        void onError(String errorMessage);
    }

    // --- CORE API METHODS ---

    /**
     * EN: Fetches movies from TMDB API based on genre filters.
     * TR: TMDB API'den film türlerine göre verileri çeker.
     */
    public void fetchMovies(String apiKey, String genreIds, MovieCallback callback) {
        String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey +
                "&language=en-US&sort_by=popularity.desc&with_genres=" + genreIds;
        executeRequest(url, response -> callback.onSuccess(parseMoviesFromJson(response)), callback::onError);
    }

    /**
     * EN: Searches for movies using a keyword.
     * TR: Anahtar kelime kullanarak film araması yapar.
     */
    public void searchMovies(String apiKey, String query, MovieCallback callback) {
        try {
            String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");
            String url = "https://api.themoviedb.org/3/search/multi?api_key=" + apiKey + "&query=" + encodedQuery;
            executeRequest(url, response -> callback.onSuccess(parseMoviesFromJson(response)), callback::onError);
        } catch (Exception e) {
            callback.onError("Encoding error");
        }
    }

    /**
     * EN: Fetches details, trailers, and cast information for a specific movie/TV show.
     * TR: Belirli bir film veya dizi için detayları, fragmanları ve oyuncu kadrosunu çeker.
     */
    public void fetchMovieDetailsAndCast(int movieId, String mediaType, String apiKey, MovieDetailCallback callback) {
        String type = ("tv".equals(mediaType)) ? "tv/" : "movie/";
        String url = "https://api.themoviedb.org/3/" + type + movieId + "?api_key=" + apiKey + "&append_to_response=credits,videos,watch/providers,external_ids";

        executeRequest(url, response -> {
            try {
                JSONObject json = new JSONObject(response);
                String videoUrl = extractTrailerUrl(json);
                String primeUrl = generatePrimeSearchUrl(json);
                List<CastMember> cast = extractCast(json);
                callback.onSuccess(videoUrl, primeUrl, cast);
            } catch (Exception e) {
                callback.onError("Parsing error");
            }
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
            try {
                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder res = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) res.append(line);
                    listener.onResponse(res.toString());
                } else {
                    errorListener.onError("Server error: " + conn.getResponseCode());
                }
            } catch (Exception e) {
                errorListener.onError("Network error: " + e.getMessage());
            } finally {
                if (conn != null) conn.disconnect();
            }
        });
    }

    /**
     * EN: Parses movie list from JSON response safely.
     * TR: JSON cevabından film listesini güvenli bir şekilde ayrıştırır.
     */
    public List<Movie> parseMoviesFromJson(String jsonString) {
        List<Movie> movies = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(jsonString);
            JSONArray results = response.optJSONArray("results");
            if (results == null) return movies;

            for (int i = 0; i < results.length(); i++) {
                JSONObject obj = results.getJSONObject(i);
                int id = obj.optInt("id", -1);
                if (id != -1) {
                    Movie m = new Movie(id, 
                        obj.optString("title", obj.optString("name", "Unknown")),
                        obj.optString("overview", ""),
                        obj.optString("poster_path", null),
                        obj.optDouble("vote_average", 0.0));
                    m.setMediaType(obj.optString("media_type", "movie"));
                    movies.add(m);
                }
            }
        } catch (Exception ignored) {}
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

    private List<CastMember> extractCast(JSONObject json) throws Exception {
        List<CastMember> cast = new ArrayList<>();
        if (!json.has("credits")) return cast;
        JSONArray array = json.getJSONObject("credits").getJSONArray("cast");
        for (int i = 0; i < Math.min(array.length(), 10); i++) {
            JSONObject obj = array.getJSONObject(i);
            cast.add(new CastMember(obj.getString("name"), obj.optString("profile_path", null)));
        }
        return cast;
    }

    interface ResponseListener { void onResponse(String response); }
    interface ErrorListener { void onError(String error); }
}
