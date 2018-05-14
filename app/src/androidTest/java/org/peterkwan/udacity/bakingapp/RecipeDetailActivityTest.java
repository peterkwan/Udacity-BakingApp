package org.peterkwan.udacity.bakingapp;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.peterkwan.udacity.bakingapp.ui.RecipeDetailActivity;
import org.peterkwan.udacity.bakingapp.ui.RecipeStepActivity;
import org.peterkwan.udacity.bakingapp.ui.RecipeStepFragment;
import org.peterkwan.udacity.bakingapp.utils.RecyclerViewMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.GONE;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RecipeDetailActivityTest {

    private static final String RECIPE_ID_EXTRA = "recipeId";
    private static final int RECIPE_ID = 2;
    private static final int STEP_ID = 2;
    private static final String RECIPE_NAME = "Brownies";
    private static final String RECIPE_NAME_EXTRA = "recipeName";
    private static final String RECIPE_STEP_EXTRA = "stepId";

    private static final int RECYCLER_VIEW_ID = R.id.stepListView;

    @Rule
    public final ActivityTestRule<RecipeDetailActivity> activityTestRule = new ActivityTestRule<>(RecipeDetailActivity.class, false);

    private RecipeDetailActivity activity;
    private boolean isTwoPaneLayout;
    private RecyclerViewMatcher matcher;
    private RecyclerView recyclerView;

    @Before
    public void setUp() {
        Intent intent = new Intent(InstrumentationRegistry.getTargetContext(), RecipeDetailActivity.class);
        intent.putExtra(RECIPE_ID_EXTRA, RECIPE_ID);
        activity = activityTestRule.launchActivity(intent);

        isTwoPaneLayout = activity.getResources().getBoolean(R.bool.two_pane_layout);
        matcher = new RecyclerViewMatcher(RECYCLER_VIEW_ID);
        recyclerView = activity.findViewById(RECYCLER_VIEW_ID);
    }

    @Test
    public void testSinglePaneStepFragmentVisibility() {
        if (!isTwoPaneLayout)
            onView(withId(R.id.stepFragmentContainer)).check(matches(withEffectiveVisibility(GONE)));
    }

    @Test
    public void testTwoPaneStepFragmentVisibility() {
        if (isTwoPaneLayout)
            onView(withId(R.id.stepFragmentContainer)).check(matches(withEffectiveVisibility(VISIBLE)));
    }

    @Test
    public void testActivityTitle() {
        assertThat(activity.getTitle().toString(), Matchers.is(RECIPE_NAME));
    }

    @Test
    public void testDetailFragment() {
        onView(withId(R.id.ingredientTextView)).check(matches(withText(containsString("350 g Bittersweet chocolate"))));

        // Check step list view and content
        ViewMatchers.assertThat(recyclerView.getAdapter().getItemCount(), Matchers.is(10));

        onView(withText("0. Recipe Introduction")).check(matches(matcher.atPositionOnView(0, R.id.stepItemTextView)));
        onView(withText("1. Starting prep")).check(matches(matcher.atPositionOnView(1, R.id.stepItemTextView)));
        onView(withText("2. Melt butter and bittersweet chocolate.")).check(matches(matcher.atPositionOnView(2, R.id.stepItemTextView)));
    }

    @Test
    public void testSinglePaneStepItemClick() {
        if (!isTwoPaneLayout) {
            onView(withId(RECYCLER_VIEW_ID)).perform(actionOnItemAtPosition(2, click()));

            Intents.init();
            intending(allOf(hasComponent(RecipeStepActivity.class.getName()),
                    hasExtras(allOf(hasEntry(equalTo(RECIPE_ID_EXTRA), equalTo(RECIPE_ID)),
                            hasEntry(equalTo(RECIPE_STEP_EXTRA), equalTo(STEP_ID)),
                            hasEntry(equalTo(RECIPE_NAME_EXTRA), equalTo(RECIPE_NAME))))));
            Intents.release();
        }
    }

    @Test
    public void testTwoPaneStepItemClick() {
        if (isTwoPaneLayout) {
            onView(withId(RECYCLER_VIEW_ID)).perform(actionOnItemAtPosition(2, click()));

            Fragment fragment = activity.getSupportFragmentManager().findFragmentById(R.id.stepFragmentContainer);
            assertThat(fragment, Matchers.<Fragment>instanceOf(RecipeStepFragment.class));
            assertNotNull(fragment.getArguments());
            assertTrue(fragment.getArguments().containsKey(RECIPE_ID_EXTRA));
            assertThat(fragment.getArguments().getInt(RECIPE_ID_EXTRA), Matchers.is(RECIPE_ID));
            assertTrue(fragment.getArguments().containsKey(RECIPE_STEP_EXTRA));
            assertThat(fragment.getArguments().getInt(RECIPE_STEP_EXTRA), Matchers.is(STEP_ID));
            assertTrue(fragment.getArguments().containsKey(RECIPE_NAME_EXTRA));
            assertThat(fragment.getArguments().getString(RECIPE_NAME_EXTRA), Matchers.is(RECIPE_NAME));
        }
    }
}
