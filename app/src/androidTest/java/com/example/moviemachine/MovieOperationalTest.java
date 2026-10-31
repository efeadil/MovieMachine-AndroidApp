package com.example.moviemachine;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertFalse;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.moviemachine.view.MovieDetailActivity;
import com.example.moviemachine.view.MovieListActivity;
import com.example.moviemachine.view.MainActivity;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * EN: Operational UI tests that execute real application screens and real TMDB requests.
 * TR: Gerçek uygulama ekranlarını ve gerçek TMDB isteklerini çalıştıran operasyonel UI testleri.
 *
 * These tests intentionally fail when the API key, network, API response, or UI binding is broken.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MovieOperationalTest {

    @Test
    public void userFlowLoadsRealMoviesOnMovieListScreen() {
        requireApiKey();

        try (ActivityScenario<MainActivity> scenario =
                     ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.btnSkip)).perform(androidx.test.espresso.action.ViewActions.click());

            RecyclerViewDataIdlingResource dataResource = registerRecyclerViewResource();
            try {
                onView(withId(R.id.rvPopular))
                        .check(matches(isDisplayed()))
                        .check(matches(hasMinimumChildCount(1)));
            } finally {
                IdlingRegistry.getInstance().unregister(dataResource);
            }
        }
    }

    @Test
    public void movieListLoadsRealMoviesFromTmdb() {
        requireApiKey();

        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                MovieListActivity.class);
        intent.putExtra("SELECTED_GENRES", "28");

        try (ActivityScenario<MovieListActivity> scenario = ActivityScenario.launch(intent)) {
            RecyclerViewDataIdlingResource dataResource = getRecyclerViewResource(scenario);
            IdlingRegistry.getInstance().register(dataResource);
            try {
                onView(withId(R.id.rvPopular))
                        .check(matches(isDisplayed()))
                        .check(matches(hasMinimumChildCount(1)));
            } finally {
                IdlingRegistry.getInstance().unregister(dataResource);
            }
        }
    }

    @Test
    public void movieDetailsLoadsRealReviewIntoScreen() {
        requireApiKey();

        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                MovieDetailActivity.class);
        intent.putExtra("MOVIE_ID", 550);
        intent.putExtra("MOVIE_TITLE", "Fight Club");
        intent.putExtra("MOVIE_OVERVIEW", "A test uses a real TMDB movie.");
        intent.putExtra("MOVIE_RATING", 8.4);
        intent.putExtra("MEDIA_TYPE", "movie");

        try (ActivityScenario<MovieDetailActivity> scenario = ActivityScenario.launch(intent)) {
            TextViewContentIdlingResource reviewResource = getReviewResource(scenario);
            IdlingRegistry.getInstance().register(reviewResource);
            try {
                onView(withId(R.id.tvDetailTitle))
                        .check(matches(withText("Fight Club")));
                onView(withId(R.id.tvDetailReview))
                        .check(matches(not(withText("Loading review..."))))
                        .check(matches(not(withText("Error loading reviews."))))
                        .check(matches(withText(containsString("•"))));
            } finally {
                IdlingRegistry.getInstance().unregister(reviewResource);
            }
        }
    }

    private static void requireApiKey() {
        assertFalse(
                "tmdb_api_key must be configured for operational tests.",
                BuildConfig.TMDB_API_KEY.trim().isEmpty());
    }

    private static RecyclerViewDataIdlingResource getRecyclerViewResource(
            ActivityScenario<MovieListActivity> scenario) {
        RecyclerViewDataIdlingResource[] resource = new RecyclerViewDataIdlingResource[1];
        scenario.onActivity(activity -> resource[0] =
                new RecyclerViewDataIdlingResource(activity.findViewById(R.id.rvPopular)));
        return resource[0];
    }

    private static RecyclerViewDataIdlingResource registerRecyclerViewResource() {
        AtomicReference<RecyclerViewDataIdlingResource> resource = new AtomicReference<>();
        onView(withId(R.id.rvPopular)).perform(new ViewAction() {
            @Override
            public org.hamcrest.Matcher<View> getConstraints() {
                return org.hamcrest.Matchers.isA(RecyclerView.class);
            }

            @Override
            public String getDescription() {
                return "register movie list data idling resource";
            }

            @Override
            public void perform(UiController uiController, View view) {
                RecyclerViewDataIdlingResource idlingResource =
                        new RecyclerViewDataIdlingResource((RecyclerView) view);
                resource.set(idlingResource);
                IdlingRegistry.getInstance().register(idlingResource);
            }
        });
        return resource.get();
    }

    private static TextViewContentIdlingResource getReviewResource(
            ActivityScenario<MovieDetailActivity> scenario) {
        TextViewContentIdlingResource[] resource = new TextViewContentIdlingResource[1];
        scenario.onActivity(activity -> resource[0] =
                new TextViewContentIdlingResource(activity.findViewById(R.id.tvDetailReview)));
        return resource[0];
    }

    private static final class RecyclerViewDataIdlingResource implements IdlingResource {
        private final RecyclerView recyclerView;
        private ResourceCallback callback;
        private boolean idleNotified;

        private RecyclerViewDataIdlingResource(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        public String getName() {
            return "RecyclerViewDataIdlingResource";
        }

        @Override
        public boolean isIdleNow() {
            boolean idle = recyclerView.getAdapter() != null
                    && recyclerView.getAdapter().getItemCount() > 0;
            if (idle && !idleNotified && callback != null) {
                idleNotified = true;
                callback.onTransitionToIdle();
            }
            return idle;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            this.callback = callback;
        }
    }

    private static final class TextViewContentIdlingResource implements IdlingResource {
        private final TextView textView;
        private ResourceCallback callback;
        private boolean idleNotified;

        private TextViewContentIdlingResource(TextView textView) {
            this.textView = textView;
        }

        @Override
        public String getName() {
            return "TextViewContentIdlingResource";
        }

        @Override
        public boolean isIdleNow() {
            CharSequence text = textView.getText();
            boolean idle = text != null
                    && text.length() > 0
                    && !"Loading review...".contentEquals(text);
            if (idle && !idleNotified && callback != null) {
                idleNotified = true;
                callback.onTransitionToIdle();
            }
            return idle;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            this.callback = callback;
        }
    }
}
