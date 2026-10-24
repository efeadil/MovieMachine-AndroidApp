package com.example.moviemachine;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.moviemachine.view.SearchActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * EN: UI Test for SearchActivity using Espresso framework.
 * TR: SearchActivity için Espresso framework'ü kullanan arayüz (UI) testi.
 */
@RunWith(AndroidJUnit4.class)
public class SearchUITest {

    @Rule
    public ActivityScenarioRule<SearchActivity> activityRule =
            new ActivityScenarioRule<>(SearchActivity.class);

    @Test
    public void testEmptySearchHandling() {
        // EN: Test behavior when search button is clicked without a query.
        // TR: Sorgu girmeden arama butonuna tıklandığındaki davranışı test eder.
        onView(withId(R.id.btnDoSearch))
                .perform(click());

        // EN: Verify that the error message is shown on the EditText.
        // TR: EditText üzerinde hata mesajının gösterildiğini doğrular.
        onView(withId(R.id.etSearchQuery))
                .check(matches(hasErrorText("Please enter a movie name")));
    }
}
