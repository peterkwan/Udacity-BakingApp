package org.peterkwan.udacity.bakingapp;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.peterkwan.udacity.bakingapp.data.RecipeDatabase;
import org.peterkwan.udacity.bakingapp.data.RecipeTestData;
import org.peterkwan.udacity.bakingapp.data.dao.RecipeDao;
import org.peterkwan.udacity.bakingapp.data.entity.Recipe;
import org.peterkwan.udacity.bakingapp.data.entity.RecipeStep;
import org.peterkwan.udacity.bakingapp.data.pojo.RecipeInfo;
import org.peterkwan.udacity.bakingapp.utils.LiveDataTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RecipeDaoTest {

    private static final int RECIPE_ID = 1;
    private static final int RECIPE_STEP_ID = 2;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private RecipeDatabase db;
    private RecipeDao recipeDao;

    @Before
    public void initDatabase() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), RecipeDatabase.class)
                .allowMainThreadQueries()
                .build();

        recipeDao = db.recipeDao();
    }

    @After
    public void closeDatabase() {
        if (db != null)
            db.close();
    }

    @Test
    public void testEmptyRecipeList() throws InterruptedException {
        List<Recipe> recipeList = LiveDataTestUtils.getValue(recipeDao.findAllRecipes());

        assertTrue(recipeList.isEmpty());
    }

    @Test
    public void testRefreshRecipes() throws InterruptedException {
        recipeDao.refreshAllRecipes(Arrays.asList(RecipeTestData.recipeInfo1, RecipeTestData.recipeInfo2));

        List<Recipe> recipeList = LiveDataTestUtils.getValue(recipeDao.findAllRecipes());

        assertThat(recipeList.size(), Matchers.is(2));

        Recipe recipe1 = recipeList.get(0);
        assertThat(recipe1.getId(), Matchers.is(1));
        assertThat(recipe1.getName(), Matchers.is("Yellow Cake"));
        assertThat(recipe1.getServings(), Matchers.is(8));
        assertThat(recipe1.getImagePath(), Matchers.is(""));

        Recipe recipe2 = recipeList.get(1);
        assertThat(recipe2.getId(), Matchers.is(2));
        assertThat(recipe2.getName(), Matchers.is("Brownies"));
        assertThat(recipe2.getServings(), Matchers.is(8));
        assertThat(recipe2.getImagePath(), Matchers.is(""));
    }

    @Test
    public void testRetrieveRecipeById()  throws InterruptedException {
        recipeDao.refreshAllRecipes(Arrays.asList(RecipeTestData.recipeInfo1, RecipeTestData.recipeInfo2));

        RecipeInfo recipeInfo = LiveDataTestUtils.getValue(recipeDao.findRecipeById(RECIPE_ID));

        assertNotNull(recipeInfo.getRecipe());
        assertThat(recipeInfo.getRecipe().getId(), Matchers.is(RECIPE_ID));
        assertThat(recipeInfo.getRecipe().getName(), Matchers.is("Yellow Cake"));
        assertThat(recipeInfo.getIngredientList().size(), Matchers.is(3));
        assertThat(recipeInfo.getIngredientList().get(0).getId(), Matchers.is(1));
        assertThat(recipeInfo.getIngredientList().get(0).getName(), Matchers.is("flour"));
        assertThat(recipeInfo.getIngredientList().get(0).getMeasure(), Matchers.is("g"));
        assertThat(recipeInfo.getIngredientList().get(0).getQuantity(), Matchers.is("10"));
        assertThat(recipeInfo.getStepList().size(), Matchers.is(3));
        assertThat(recipeInfo.getStepList().get(0).getId(), Matchers.is(1));
        assertThat(recipeInfo.getStepList().get(0).getShortDescription(), Matchers.is("Pour flour into a bowl"));
        assertThat(recipeInfo.getStepList().get(0).getDescription(), Matchers.is("Measure 10 gram of flour and pour it into a bowl"));
        assertThat(recipeInfo.getStepList().get(0).getVideoUrl(), Matchers.is(""));
        assertThat(recipeInfo.getStepList().get(0).getThumbnailUrl(), Matchers.is(""));
    }

    @Test
    public void testRetrieveRecipeStepById()  throws InterruptedException {
        recipeDao.refreshAllRecipes(Arrays.asList(RecipeTestData.recipeInfo1, RecipeTestData.recipeInfo2));

        List<RecipeStep> stepList = LiveDataTestUtils.getValue(recipeDao.findRecipeStepByRecipeId(RECIPE_ID));

        assertNotNull(stepList);
        assertThat(stepList.size(), Matchers.is(3));

        RecipeStep step = retrieveStepByStepId(stepList);

        assertNotNull(step);
        assertThat(step.getId(), Matchers.is(RECIPE_STEP_ID));
        assertThat(step.getShortDescription(), Matchers.is("Pour sugar into a bowl"));
        assertThat(step.getDescription(), Matchers.is("Pour 2 cups of sugar into the bowl"));
        assertThat(step.getVideoUrl(), Matchers.is(""));
        assertThat(step.getThumbnailUrl(), Matchers.is("https://test.org/step2.mp4"));
    }

    private RecipeStep retrieveStepByStepId(List<RecipeStep> recipeStepList) {
        for (RecipeStep step : recipeStepList) {
            if (step.getId() == RECIPE_STEP_ID)
                return step;
        }

        return null;
    }
}
