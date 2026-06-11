package com.example.moviemachine;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.moviemachine.view.SearchActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
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
    public void testSearchFlowAndResultVisibility() throws InterruptedException {
        // EN: Type "Matrix" in the search box and click search button.
        // TR: Arama kutusuna "Matrix" yazıp arama butonuna tıklar.
        onView(withId(R.id.etSearchQuery))
                .perform(typeText("Matrix"), closeSoftKeyboard());

        onView(withId(R.id.btnDoSearch))
                .perform(click());

        // EN: Wait for API response.
        // TR: API cevabı için kısa bir süre bekler.
        Thread.sleep(3000);

        // EN: Verify that the results list is visible.
        // TR: Sonuç listesinin görünür olduğunu doğrular.
        onView(withId(R.id.rvSearchResults))
                .check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testEmptySearchHandling() {
        // EN: Test behavior when search button is clicked without a query.
        // TR: Sorgu girmeden arama butonuna tıklandığındaki davranışı test eder.
        onView(withId(R.id.btnDoSearch))
                .perform(click());
    }
}
