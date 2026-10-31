package com.example.moviemachine.view;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.moviemachine.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * EN: Robust Navigation Tests using Espresso.
 * TR: Espresso kullanarak dayanıklı navigasyon testleri.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class NavigationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testLoginValidation() {
        onView(withId(R.id.btnLogin)).perform(click());
        onView(withId(R.id.etUsername)).check(matches(hasErrorText("Name is required!")));
    }

    @Test
    public void testFullUserFlow() {
        // 1. Login Ekranı
        onView(withId(R.id.etUsername)).perform(typeText("TestUser"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        // 2. Onboarding Ekranı (Geldiğini doğrula)
        onView(withId(R.id.btnSavePreferences)).check(matches(isDisplayed()));
        onView(withId(R.id.cbAction)).perform(click());
        onView(withId(R.id.btnSavePreferences)).perform(click());

        // 3. Confirm that the real movie-list screen was opened.
        onView(withId(R.id.rvPopular)).check(matches(isDisplayed()));
    }
}
