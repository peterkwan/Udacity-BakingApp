package org.peterkwan.udacity.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.peterkwan.udacity.bakingapp.ui.RecipeStepActivity;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.GONE;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.INVISIBLE;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;

@RunWith(AndroidJUnit4.class)
public class RecipeStepActivityTest {

    private static final String RECIPE_ID_EXTRA = "recipeId";
    private static final int RECIPE_ID = 1;
    private static final int STEP_ID = 2;
    private static final int STEP_ID2 = 1;
    private static final int STEP_ID3 = 5;
    private static final int STEP_ID4 = 0;
    private static final String RECIPE_NAME = "Nutella Pie";
    private static final String RECIPE_NAME_EXTRA = "recipeName";
    private static final String RECIPE_STEP_EXTRA = "stepId";

    private static final int NAV_NEXT_BUTTON_ID = R.id.navNextImageView;
    private static final int NAV_PREVIOUS_BUTTON_ID = R.id.navBeforeImageView;
    private static final int TITLE_VIEW_ID = R.id.stepDetailTitleView;
    private static final int SHORT_DESCRIPTION_VIEW_ID = R.id.stepDetailShortDescriptionView;
    private static final int FULL_DESCRIPTION_VIEW_ID = R.id.stepDetailFullDescriptionView;
    private static final int THUMBNAIL_VIEW_ID = R.id.thumbnailImageView;
    private static final int VIDEO_PLAYER_VIEW_ID = R.id.playerView;

    @Rule
    public final ActivityTestRule<RecipeStepActivity> activityTestRule = new ActivityTestRule<>(RecipeStepActivity.class, false);

    private boolean isTwoPaneLayout;
    private boolean isLandscapeOrientation;

    @Test
    public void testFragmentContent() {
        launchActivity(STEP_ID);

        if (!isTwoPaneLayout && !isLandscapeOrientation) {
            onView(withId(TITLE_VIEW_ID)).check(matches(withText("Step " + STEP_ID)));
            onView(withId(SHORT_DESCRIPTION_VIEW_ID)).check(matches(withText("Prep the cookie crust.")));
            onView(withId(FULL_DESCRIPTION_VIEW_ID)).check(matches(withText(containsString("Whisk the graham cracker crumbs"))));
        }
    }

    @Test
    public void testFragmentComponentVisibility() {
        launchActivity(STEP_ID);

        if (!isTwoPaneLayout && !isLandscapeOrientation) {
            RecipeStepActivity activity = activityTestRule.getActivity();
            isTwoPaneLayout = activity.getResources().getBoolean(R.bool.two_pane_layout);
            isLandscapeOrientation = activity.getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;

            onView(withId(TITLE_VIEW_ID)).check(matches(withEffectiveVisibility(VISIBLE)));
            onView(withId(SHORT_DESCRIPTION_VIEW_ID)).check(matches(withEffectiveVisibility(VISIBLE)));
            onView(withId(FULL_DESCRIPTION_VIEW_ID)).check(matches(withEffectiveVisibility(VISIBLE)));
            onView(withId(NAV_PREVIOUS_BUTTON_ID)).check(matches(withEffectiveVisibility(VISIBLE)));
            onView(withId(NAV_NEXT_BUTTON_ID)).check(matches(withEffectiveVisibility(VISIBLE)));
        }
    }

    @Test
    public void testClickNextStep() {
        launchActivity(STEP_ID);

        if (!isTwoPaneLayout && !isLandscapeOrientation) {
            onView(withId(NAV_NEXT_BUTTON_ID)).perform(click());

            onView(withId(TITLE_VIEW_ID)).check(matches(withText("Step " + (STEP_ID + 1))));
            onView(withId(SHORT_DESCRIPTION_VIEW_ID)).check(matches(withText("Press the crust into baking form.")));
            onView(withId(FULL_DESCRIPTION_VIEW_ID)).check(matches(withText(containsString("Press the cookie crumb mixture"))));
            onView(withId(NAV_PREVIOUS_BUTTON_ID)).check(matches(withEffectiveVisibility(VISIBLE)));
            onView(withId(NAV_NEXT_BUTTON_ID)).check(matches(withEffectiveVisibility(VISIBLE)));
        }
    }

    @Test
    public void testClickPreviousStep() {
        launchActivity(STEP_ID);

        if (!isTwoPaneLayout && !isLandscapeOrientation) {
            onView(withId(NAV_PREVIOUS_BUTTON_ID)).perform(click());

            onView(withId(TITLE_VIEW_ID)).check(matches(withText("Step " + (STEP_ID - 1))));
            onView(withId(SHORT_DESCRIPTION_VIEW_ID)).check(matches(withText("Starting prep")));
            onView(withId(FULL_DESCRIPTION_VIEW_ID)).check(matches(withText(containsString("Preheat the oven to 350"))));
            onView(withId(NAV_PREVIOUS_BUTTON_ID)).check(matches(withEffectiveVisibility(VISIBLE)));
            onView(withId(NAV_NEXT_BUTTON_ID)).check(matches(withEffectiveVisibility(VISIBLE)));
        }
    }

    @Test
    public void testClickGoToFirstStep() {
        launchActivity(STEP_ID2);

        if (!isTwoPaneLayout && !isLandscapeOrientation) {
            onView(withId(NAV_PREVIOUS_BUTTON_ID)).perform(click());

            onView(withId(NAV_PREVIOUS_BUTTON_ID)).check(matches(withEffectiveVisibility(INVISIBLE)));
            onView(withId(NAV_NEXT_BUTTON_ID)).check(matches(withEffectiveVisibility(VISIBLE)));
        }
    }

    @Test
    public void testClickGoToLastStep() {
        launchActivity(STEP_ID3);

        if (!isTwoPaneLayout && !isLandscapeOrientation) {
            onView(withId(NAV_NEXT_BUTTON_ID)).perform(click());

            onView(withId(NAV_PREVIOUS_BUTTON_ID)).check(matches(withEffectiveVisibility(VISIBLE)));
            onView(withId(NAV_NEXT_BUTTON_ID)).check(matches(withEffectiveVisibility(INVISIBLE)));
        }
    }

    @Test
    public void testThumbnailVisibility() {
        launchActivity(STEP_ID3);

        if (!isTwoPaneLayout) {
            onView(withId(THUMBNAIL_VIEW_ID)).check(matches(withEffectiveVisibility(VISIBLE)));
            onView(withId(VIDEO_PLAYER_VIEW_ID)).check(matches(withEffectiveVisibility(GONE)));
        }
    }

    @Test
    public void testVideoPlayerVisibility() {
        launchActivity(STEP_ID4);

        if (!isTwoPaneLayout) {
            onView(withId(THUMBNAIL_VIEW_ID)).check(matches(withEffectiveVisibility(GONE)));
            onView(withId(VIDEO_PLAYER_VIEW_ID)).check(matches(withEffectiveVisibility(VISIBLE)));
        }
    }

    private void launchActivity(int stepId) {
        Bundle args = new Bundle();
        args.putInt(RECIPE_ID_EXTRA, RECIPE_ID);
        args.putInt(RECIPE_STEP_EXTRA, stepId);
        args.putString(RECIPE_NAME_EXTRA, RECIPE_NAME);

        Intent intent = new Intent(InstrumentationRegistry.getTargetContext(), RecipeStepActivity.class);
        intent.putExtras(args);

        RecipeStepActivity activity = activityTestRule.launchActivity(intent);

        isTwoPaneLayout = activity.getResources().getBoolean(R.bool.two_pane_layout);
        isLandscapeOrientation = activity.getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;
    }
}
