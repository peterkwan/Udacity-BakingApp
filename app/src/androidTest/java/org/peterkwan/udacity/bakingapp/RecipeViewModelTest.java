package org.peterkwan.udacity.bakingapp;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.peterkwan.udacity.bakingapp.data.RecipeTestData;
import org.peterkwan.udacity.bakingapp.data.pojo.RecipeInfo;
import org.peterkwan.udacity.bakingapp.repository.RecipeRepository;
import org.peterkwan.udacity.bakingapp.utils.BakingAppTestRunner;
import org.peterkwan.udacity.bakingapp.viewmodel.RecipeViewModel;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.peterkwan.udacity.bakingapp.utils.LiveDataTestUtils.constructLiveData;

@RunWith(AndroidJUnit4.class)
public class RecipeViewModelTest {

    private static final int RECIPE_ID = 1;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private RecipeRepository repository;
    private RecipeViewModel viewModel;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        BakingApp app = (BakingApp) BakingAppTestRunner.newApplication(BakingApp.class, InstrumentationRegistry.getInstrumentation().getTargetContext());

        repository = mock(RecipeRepository.class);
        viewModel = new RecipeViewModel(app, repository);
    }

    @Test
    public void testVerifyRepositoryCall() {
        viewModel.retrieveRecipe(RECIPE_ID);
        verify(repository).retrieveRecipe(RECIPE_ID);
    }

    @Test
    public void testVerifyData() {
        LiveData<RecipeInfo> recipeInfoLiveData = constructLiveData(RecipeTestData.recipeInfo1);
        when(repository.retrieveRecipe(RECIPE_ID)).thenReturn(recipeInfoLiveData);
        LiveData<RecipeInfo> resultLiveData = viewModel.retrieveRecipe(RECIPE_ID);

        assertNotNull(resultLiveData);

        RecipeInfo result = resultLiveData.getValue();

        assertNotNull(result);
        assertThat(result.getRecipe(), Matchers.is(RecipeTestData.recipeEntity1));
        assertThat(result.getIngredientList().size(), Matchers.is(3));
        assertThat(result.getIngredientList().get(0), Matchers.is(RecipeTestData.ingredientEntity11));
        assertThat(result.getIngredientList().get(1), Matchers.is(RecipeTestData.ingredientEntity12));
        assertThat(result.getIngredientList().get(2), Matchers.is(RecipeTestData.ingredientEntity13));
        assertThat(result.getStepList().size(), Matchers.is(3));
        assertThat(result.getStepList().get(0), Matchers.is(RecipeTestData.stepEntity11));
        assertThat(result.getStepList().get(1), Matchers.is(RecipeTestData.stepEntity12));
        assertThat(result.getStepList().get(2), Matchers.is(RecipeTestData.stepEntity13));
    }
}
