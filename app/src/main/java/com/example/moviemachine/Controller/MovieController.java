package com.example.moviemachine.Controller;

import android.util.Log;
import com.example.moviemachine.Model.Movie;
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
 * Controller class managing API communication and data processing.
 * Handles network operations asynchronously to prevent UI blocking.
 */
public class MovieController {

    // Executor for asynchronous operations (Arka plan islemleri icin)
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Callback interface for UI communication (Arayuz ile haberlesme arayuzu)
    public interface MovieCallback {
        void onSuccess(List<Movie> movies);
        void onError(String errorMessage);
    }

    /**
     * Fetches movies from TMDB API based on user preferences.
     * Includes exception handling for network loss.
     */
    public void fetchMovies(String apiKey, String genreIds, MovieCallback callback) {
        executorService.execute(() -> {
            HttpURLConnection connection = null;
            try {
                // Constructing the URL with genres and API key
                String urlString = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey +
                        "&language=en-US&sort_by=popularity.desc&with_genres=" + genreIds;
                URL url = new URL(urlString);

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000); // 5 seconds timeout

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);
                    reader.close();

                    // JSON Parsing (JSON Verisi ayristirma)
                    List<Movie> movies = new ArrayList<>();
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray results = jsonResponse.getJSONArray("results");

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject obj = results.getJSONObject(i);
                        movies.add(new Movie(
                                obj.getInt("id"),
                                obj.getString("title"),
                                obj.getString("overview"),
                                obj.getString("poster_path"),
                                obj.getDouble("vote_average")
                        ));
                    }
                    callback.onSuccess(movies);
                } else {
                    callback.onError("Invalid API response: " + connection.getResponseCode());
                }

            } catch (Exception e) {
                // Proper Exception Handling for network loss (Baglanti hatasi yönetimi)
                Log.e("MovieController", "Network Error", e);
                callback.onError("Network connection lost. Please check your internet!");
            } finally {
                if (connection != null) connection.disconnect();
            }
        });
    }

    // Detay sayfası için iletişim arayüzü
    public interface MovieDetailCallback {
        void onSuccess(String imdbId, List<com.example.moviemachine.Model.CastMember> cast);
        void onError(String errorMessage);
    }

    // YENİ: Filme veya Diziye özel detayları ve oyuncuları çekme metodu (mediaType eklendi)
    public void fetchMovieDetailsAndCast(int movieId, String mediaType, String apiKey, MovieDetailCallback callback) {
        executorService.execute(() -> {
            HttpURLConnection connection = null;
            try {
                String urlString;
                // EĞER DİZİYSE (tv) external_ids çekmemiz lazım ki IMDb koduna ulaşalım!
                if ("tv".equals(mediaType)) {
                    urlString = "https://api.themoviedb.org/3/tv/" + movieId + "?api_key=" + apiKey + "&append_to_response=credits,external_ids";
                } else {
                    // FİLM İSE
                    urlString = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + apiKey + "&append_to_response=credits";
                }

                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());

                    // GERÇEK IMDb ID'sini ALMA MANTIĞI:
                    String imdbId = "";
                    if ("tv".equals(mediaType)) {
                        // Dizilerde IMDb ID'si external_ids içindedir
                        if (jsonResponse.has("external_ids")) {
                            imdbId = jsonResponse.getJSONObject("external_ids").optString("imdb_id", "");
                        }
                    } else {
                        // Filmlerde direkt ana menüdedir
                        imdbId = jsonResponse.optString("imdb_id", "");
                    }

                    // Oyuncuları Al
                    List<com.example.moviemachine.Model.CastMember> castList = new ArrayList<>();
                    if (jsonResponse.has("credits")) {
                        JSONArray castArray = jsonResponse.getJSONObject("credits").getJSONArray("cast");
                        // Sadece ilk 10 oyuncuyu alalım
                        int maxCast = Math.min(castArray.length(), 10);
                        for (int i = 0; i < maxCast; i++) {
                            JSONObject actorObj = castArray.getJSONObject(i);
                            castList.add(new com.example.moviemachine.Model.CastMember(
                                    actorObj.getString("name"),
                                    actorObj.optString("profile_path", null)
                            ));
                        }
                    }
                    callback.onSuccess(imdbId, castList);
                } else {
                    callback.onError("Detaylar alınamadı.");
                }
            } catch (Exception e) {
                callback.onError("Bağlantı hatası!");
            } finally {
                if (connection != null) connection.disconnect();
            }
        });
    }

    // FİLM VE DİZİLERİ BİRLİKTE ARAMA METODU (MULTI SEARCH)
    public void searchMovies(String apiKey, String query, MovieCallback callback) {
        executorService.execute(() -> {
            HttpURLConnection connection = null;
            try {
                String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");

                // 'multi' kullanarak hem film hem dizi arıyoruz
                String urlString = "https://api.themoviedb.org/3/search/multi?api_key=" + apiKey + "&query=" + encodedQuery;
                URL url = new URL(urlString);

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray resultsArray = jsonResponse.getJSONArray("results");
                    List<Movie> movies = new ArrayList<>();

                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject itemObject = resultsArray.getJSONObject(i);

                        // Arama sonuçlarında 'oyuncular' (person) da çıkabilir, onları eliyoruz.
                        String mediaType = itemObject.optString("media_type", "");
                        if (mediaType.equals("movie") || mediaType.equals("tv")) {

                            // Filmlerin başlığı 'title', dizilerin başlığı 'name' ile gelir. Akıllı seçim yapıyoruz:
                            String title = itemObject.optString("title", itemObject.optString("name", "Bilinmeyen Başlık"));

                            // YENİ: Tıklanan şey dizi mi film mi anlamak için mediaType bilgisini objeye ekliyoruz
                            Movie m = new Movie(
                                    itemObject.getInt("id"),
                                    title,
                                    itemObject.optString("overview", "Özet bulunamadı."),
                                    itemObject.optString("poster_path", null),
                                    itemObject.optDouble("vote_average", 0.0)
                            );
                            m.setMediaType(mediaType);
                            movies.add(m);
                        }
                    }
                    callback.onSuccess(movies);
                } else {
                    callback.onError("Arama sonuçları alınamadı.");
                }
            } catch (Exception e) {
                callback.onError("Bağlantı hatası: " + e.getMessage());
            } finally {
                if (connection != null) connection.disconnect();
            }
        });
    }
}