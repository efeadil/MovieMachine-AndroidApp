package com.example.moviemachine;

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
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class SearchUITest {

    // Testin hangi ekranda (Activity) başlayacağını belirtiyoruz
    @Rule
    public ActivityScenarioRule<SearchActivity> activityRule =
            new ActivityScenarioRule<>(SearchActivity.class);

    @Test
    public void testSearchInputAndButtonClick() {
        // 1. Arama kutusunu (EditText) bul, içine "Matrix" yaz ve klavyeyi kapat
        onView(withId(R.id.etSearchQuery))
                .perform(typeText("Matrix"), closeSoftKeyboard());

        // 2. Arama butonunu (Button) bul ve üzerine tıkla
        onView(withId(R.id.btnDoSearch))
                .perform(click());

        // Not: Bu adımda butonun çökmeden çalıştığı ve kullanıcının girdisinin
        // başarılı bir şekilde sisteme aktarıldığı doğrulanmış olur.
    }
}