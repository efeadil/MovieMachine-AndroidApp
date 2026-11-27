package com.example.moviemachine;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.anyOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import android.content.Intent;
import android.view.View;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewTreeObserver;
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

import com.example.moviemachine.view.MainActivity;
import com.example.moviemachine.view.MovieListActivity;

import java.util.concurrent.atomic.AtomicReference;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.hamcrest.TypeSafeMatcher;

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
            onView(withId(R.id.cbAction)).perform(androidx.test.espresso.action.ViewActions.click());
            onView(withId(R.id.btnSavePreferences))
                    .perform(androidx.test.espresso.action.ViewActions.click());

            RecyclerViewDataIdlingResource popularResource =
                    registerRecyclerViewResource(R.id.rvPopular);
            RecyclerViewDataIdlingResource recommendedResource =
                    registerRecyclerViewResource(R.id.rvRecommended);
            try {
                onView(withId(R.id.rvPopular))
                        .check(matches(isDisplayed()))
                        .check(matches(hasAdapterItems(1)));
                onView(withId(R.id.rvRecommended))
                        .check(matches(isDisplayed()))
                        .check(matches(hasAdapterItems(1)));
            } finally {
                IdlingRegistry.getInstance().unregister(popularResource);
                IdlingRegistry.getInstance().unregister(recommendedResource);
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
            RecyclerViewDataIdlingResource popularResource =
                    getRecyclerViewResource(scenario, R.id.rvPopular);
            RecyclerViewDataIdlingResource recommendedResource =
                    getRecyclerViewResource(scenario, R.id.rvRecommended);
            IdlingRegistry.getInstance().register(popularResource);
            IdlingRegistry.getInstance().register(recommendedResource);
            try {
                onView(withId(R.id.rvPopular))
                        .check(matches(isDisplayed()))
                        .check(matches(hasAdapterItems(1)));
                onView(withId(R.id.rvRecommended))
                        .check(matches(isDisplayed()))
                        .check(matches(hasAdapterItems(1)));
            } finally {
                IdlingRegistry.getInstance().unregister(popularResource);
                IdlingRegistry.getInstance().unregister(recommendedResource);
            }
        }
    }

    @Test
    public void selectingRealMovieOpensDetailsAndLoadsReview() {
        requireApiKey();

        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                MovieListActivity.class);
        intent.putExtra("SELECTED_GENRES", "28");

        try (ActivityScenario<MovieListActivity> scenario = ActivityScenario.launch(intent)) {
            RecyclerViewDataIdlingResource listResource =
                    getRecyclerViewResource(scenario, R.id.rvPopular);
            IdlingRegistry.getInstance().register(listResource);
            try {
                onView(withId(R.id.rvPopular))
                        .check(matches(hasAdapterItems(1)));
            } finally {
                IdlingRegistry.getInstance().unregister(listResource);
            }

            AtomicReference<String> selectedTitle = new AtomicReference<>();
            onView(withId(R.id.rvPopular)).perform(clickFirstMovie(selectedTitle));
            assertNotNull("The selected movie must have a title.", selectedTitle.get());
            assertFalse("The selected movie title must not be blank.",
                    selectedTitle.get().trim().isEmpty());

            TextViewContentIdlingResource reviewResource = registerReviewResource();
            try {
                onView(withId(R.id.tvDetailTitle))
                        .check(matches(withText(selectedTitle.get())));
                onView(withId(R.id.tvDetailReview))
                        .check(matches(not(withText("Loading review..."))))
                        .check(matches(not(withText("Error loading reviews."))))
                        .check(matches(anyOf(
                                withText(containsString("•")),
                                withText("No reviews available."))));
            } finally {
                IdlingRegistry.getInstance().unregister(reviewResource);
            }
            pressBack();
        }
    }

    private static void requireApiKey() {
        assertFalse(
                "tmdb_api_key must be configured for operational tests.",
                BuildConfig.TMDB_API_KEY.trim().isEmpty());
    }

    private static RecyclerViewDataIdlingResource getRecyclerViewResource(
            ActivityScenario<MovieListActivity> scenario, int viewId) {
        RecyclerViewDataIdlingResource[] resource = new RecyclerViewDataIdlingResource[1];
        scenario.onActivity(activity -> resource[0] =
                new RecyclerViewDataIdlingResource(activity.findViewById(viewId)));
        return resource[0];
    }

    private static RecyclerViewDataIdlingResource registerRecyclerViewResource(int viewId) {
        AtomicReference<RecyclerViewDataIdlingResource> resource = new AtomicReference<>();
        onView(withId(viewId)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
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

    private static Matcher<View> hasAdapterItems(int minimumCount) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView adapter with at least ")
                        .appendValue(minimumCount)
                        .appendText(" items");
            }

            @Override
            protected boolean matchesSafely(View view) {
                if (!(view instanceof RecyclerView)) {
                    return false;
                }
                RecyclerView.Adapter<?> adapter = ((RecyclerView) view).getAdapter();
                return adapter != null && adapter.getItemCount() >= minimumCount;
            }
        };
    }

    private static ViewAction clickFirstMovie(AtomicReference<String> selectedTitle) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return org.hamcrest.Matchers.isA(RecyclerView.class);
            }

            @Override
            public String getDescription() {
                return "click the first loaded movie";
            }

            @Override
            public void perform(UiController uiController, View view) {
                RecyclerView recyclerView = (RecyclerView) view;
                RecyclerView.ViewHolder holder =
                        recyclerView.findViewHolderForAdapterPosition(0);
                assertNotNull("The first movie card must be attached.", holder);
                TextView title = holder.itemView.findViewById(R.id.textTitle);
                selectedTitle.set(title.getText().toString());
                holder.itemView.performClick();
            }
        };
    }

    private static TextViewContentIdlingResource registerReviewResource() {
        AtomicReference<TextViewContentIdlingResource> resource = new AtomicReference<>();
        onView(withId(R.id.tvDetailReview)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return org.hamcrest.Matchers.isA(TextView.class);
            }

            @Override
            public String getDescription() {
                return "register movie review content idling resource";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextViewContentIdlingResource idlingResource =
                        new TextViewContentIdlingResource((TextView) view);
                resource.set(idlingResource);
                IdlingRegistry.getInstance().register(idlingResource);
            }
        });
        return resource.get();
    }

    private static final class RecyclerViewDataIdlingResource implements IdlingResource {
        private final RecyclerView recyclerView;
        private final ViewTreeObserver.OnGlobalLayoutListener layoutListener;
        private ResourceCallback callback;
        private boolean idleNotified;

        private RecyclerViewDataIdlingResource(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
            layoutListener = this::notifyIfIdle;
            recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }

        @Override
        public String getName() {
            return "RecyclerViewDataIdlingResource";
        }

        @Override
        public boolean isIdleNow() {
            return recyclerView.getAdapter() != null
                    && recyclerView.getAdapter().getItemCount() > 0;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            this.callback = callback;
            notifyIfIdle();
        }

        private void notifyIfIdle() {
            if (!idleNotified && callback != null && isIdleNow()) {
                idleNotified = true;
                ViewTreeObserver observer = recyclerView.getViewTreeObserver();
                if (observer.isAlive()) {
                    observer.removeOnGlobalLayoutListener(layoutListener);
                }
                callback.onTransitionToIdle();
            }
        }
    }

    private static final class TextViewContentIdlingResource implements IdlingResource {
        private final TextView textView;
        private final TextWatcher textWatcher;
        private ResourceCallback callback;
        private boolean idleNotified;

        private TextViewContentIdlingResource(TextView textView) {
            this.textView = textView;
            textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence text, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence text, int start, int before, int count) {
                    notifyIfIdle();
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            };
            textView.addTextChangedListener(textWatcher);
        }

        @Override
        public String getName() {
            return "TextViewContentIdlingResource";
        }

        @Override
        public boolean isIdleNow() {
            CharSequence text = textView.getText();
            return text != null
                    && text.length() > 0
                    && !"Loading review...".contentEquals(text);
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            this.callback = callback;
            notifyIfIdle();
        }

        private void notifyIfIdle() {
            if (!idleNotified && callback != null && isIdleNow()) {
                idleNotified = true;
                textView.removeTextChangedListener(textWatcher);
                callback.onTransitionToIdle();
            }
        }
    }
}
