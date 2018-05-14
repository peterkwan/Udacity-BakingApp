package org.peterkwan.udacity.bakingapp;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.peterkwan.udacity.bakingapp.ui.MainActivity;
import org.peterkwan.udacity.bakingapp.ui.RecipeDetailActivity;
import org.peterkwan.udacity.bakingapp.utils.RecyclerViewMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private static final int RECYCLER_VIEW_ID = R.id.recipeListView;
    private static final String RECIPE_ID = "recipeId";

    @Rule
    public final IntentsTestRule<MainActivity> activityTestRule = new IntentsTestRule<>(MainActivity.class);

    private MainActivity activity;
    private RecyclerViewMatcher matcher;
    private RecyclerView recyclerView;

    @Before
    public void setUp() {
        activity = activityTestRule.getActivity();
        matcher = new RecyclerViewMatcher(RECYCLER_VIEW_ID);
        recyclerView = activity.findViewById(RECYCLER_VIEW_ID);
    }

    @Test
    public void testRecyclerViewData() {
        assertThat(recyclerView.getAdapter().getItemCount(), Matchers.is(4));

        onView(matcher.atPositionOnView(0, -1)).check(matches(isDisplayed()));
        onView(matcher.atPositionOnView(0, R.id.recipeItemFooterTextView)).check(matches(isDisplayed()));
        onView(withText("Nutella Pie")).check(matches(matcher.atPositionOnView(0, R.id.recipeItemFooterTextView)));

        onView(matcher.atPositionOnView(1, -1)).check(matches(isDisplayed()));
        onView(matcher.atPositionOnView(1, R.id.recipeItemFooterTextView)).check(matches(isDisplayed()));
        onView(withText("Brownies")).check(matches(matcher.atPositionOnView(1, R.id.recipeItemFooterTextView)));

        onView(matcher.atPositionOnView(2, -1)).check(matches(isDisplayed()));
        onView(matcher.atPositionOnView(2, R.id.recipeItemFooterTextView)).check(matches(isDisplayed()));
        onView(withText("Yellow Cake")).check(matches(matcher.atPositionOnView(2, R.id.recipeItemFooterTextView)));

        onView(matcher.atPositionOnView(3, -1)).check(matches(isDisplayed()));
        onView(matcher.atPositionOnView(3, R.id.recipeItemFooterTextView)).check(matches(isDisplayed()));
        onView(withText("Cheesecake")).check(matches(matcher.atPositionOnView(3, R.id.recipeItemFooterTextView)));
    }

    @Test
    public void testItem0Click() {
        onView(withId(RECYCLER_VIEW_ID)).perform(actionOnItemAtPosition(0, click()));
        intending(allOf(hasComponent(RecipeDetailActivity.class.getName()),
                hasExtras(hasEntry(equalTo(RECIPE_ID), equalTo(0)))
        ));
    }

    @Test
    public void testItem1Click() {
        onView(withId(RECYCLER_VIEW_ID)).perform(actionOnItemAtPosition(1, click()));
        intending(allOf(hasComponent(RecipeDetailActivity.class.getName()),
                hasExtras(hasEntry(equalTo(RECIPE_ID), equalTo(1)))
        ));
    }

    @Test
    public void testItem2Click() {
        onView(withId(RECYCLER_VIEW_ID)).perform(actionOnItemAtPosition(2, click()));
        intending(allOf(hasComponent(RecipeDetailActivity.class.getName()),
                hasExtras(hasEntry(equalTo(RECIPE_ID), equalTo(2)))
        ));
    }

    @Test
    public void testItem3Click() {
        onView(withId(RECYCLER_VIEW_ID)).perform(actionOnItemAtPosition(3, click()));
        intending(allOf(hasComponent(RecipeDetailActivity.class.getName()),
                hasExtras(hasEntry(equalTo(RECIPE_ID), equalTo(3)))
        ));
    }

    @Test
    public void testNetworkFailure() throws Throwable {
        activityTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.onNetworkFailure();
            }
        });

        onView(withText(R.string.load_data_error_title)).check(matches(isDisplayed()));
    }

}
