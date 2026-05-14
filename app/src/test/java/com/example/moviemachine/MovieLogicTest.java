package com.example.moviemachine;

import com.example.moviemachine.Model.Movie;
import org.junit.Test;
import static org.junit.Assert.*;

public class MovieLogicTest {

    @Test
    public void testMovieDataProcessing() {
        // 1. Sahte bir film verisi oluşturuyoruz (API'den gelmiş gibi)
        Movie testMovie = new Movie(
                27205,
                "Inception",
                "A thief who steals corporate secrets through the use of dream-sharing technology.",
                "/poster_path.jpg",
                8.8
        );

        // 2. Özel ayarladığımız Dizi/Film ayırım mantığını (mediaType) test ediyoruz
        testMovie.setMediaType("movie");
        testMovie.setImdbId("tt1375666");

        // 3. Beklenen sonuçlarla elimizdeki verileri karşılaştırıyoruz (Doğrulama)
        assertEquals("Beklenen ID eşleşmedi!", 27205, testMovie.getId());
        assertEquals("Film adı yanlış işlendi!", "Inception", testMovie.getTitle());
        assertEquals("Tür ayrımı (mediaType) hatalı!", "movie", testMovie.getMediaType());
        assertNotNull("IMDb ID boş olmamalı!", testMovie.getImdbId());
    }
}