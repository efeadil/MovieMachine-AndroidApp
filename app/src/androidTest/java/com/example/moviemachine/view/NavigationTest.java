package com.example.moviemachine.view;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.moviemachine.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * EN: Tests basic app navigation and user interactions using Espresso.
 * TR: Uygulamanın temel navigasyon ve kullanıcı etkileşimlerini Espresso ile test eder.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class NavigationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    /**
     * EN: Verifies input validation on the login screen.
     * TR: Giriş ekranındaki veri doğrulama (input validation) mekanizmasını test eder.
     */
    @Test
    public void testLoginValidation() {
        onView(withId(R.id.btnLogin)).perform(click());
        onView(withId(R.id.etUsername)).check(matches(hasErrorText("Name is required!")));
    }

    /**
     * EN: Tests navigation from Login screen to Onboarding screen.
     * TR: Giriş ekranından Onboarding ekranına geçişi test eder.
     */
    @Test
    public void testNavigationToOnboarding() {
        onView(withId(R.id.etUsername)).perform(typeText("TestUser"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
        onView(withId(R.id.btnSavePreferences)).check(matches(isDisplayed()));
    }

    /**
     * EN: Tests navigation from Onboarding screen to Movie List screen.
     * TR: Onboarding ekranından film listesi ekranına geçişi test eder.
     */
    @Test
    public void testNavigationFromOnboardingToMovieList() {
        onView(withId(R.id.etUsername)).perform(typeText("TestUser"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        // EN: Select a genre and proceed.
        // TR: Bir tür seçip devam eder.
        onView(withId(R.id.cbAction)).perform(click());
        onView(withId(R.id.btnSavePreferences)).perform(click());

        // EN: Check if the movie list (RecyclerView) is displayed.
        // TR: Film listesinin (RecyclerView) görüntülendiğini kontrol eder.
        onView(withId(R.id.rvPopular)).check(matches(isDisplayed()));
    }
}
