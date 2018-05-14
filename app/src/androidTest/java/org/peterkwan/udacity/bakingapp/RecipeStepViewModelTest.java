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
import org.peterkwan.udacity.bakingapp.data.entity.RecipeStep;
import org.peterkwan.udacity.bakingapp.repository.RecipeRepository;
import org.peterkwan.udacity.bakingapp.utils.BakingAppTestRunner;
import org.peterkwan.udacity.bakingapp.viewmodel.RecipeStepViewModel;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.peterkwan.udacity.bakingapp.utils.LiveDataTestUtils.constructLiveData;

@RunWith(AndroidJUnit4.class)
public class RecipeStepViewModelTest {

    private static final int RECIPE_ID = 1;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private RecipeRepository repository;
    private RecipeStepViewModel viewModel;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        BakingApp app = (BakingApp) BakingAppTestRunner.newApplication(BakingApp.class, InstrumentationRegistry.getInstrumentation().getTargetContext());

        repository = mock(RecipeRepository.class);
        viewModel = new RecipeStepViewModel(app, repository);
    }

    @Test
    public void testVerifyRepositoryCall() {
        viewModel.retrieveRecipeSteps(RECIPE_ID);
        verify(repository).retrieveRecipeSteps(RECIPE_ID);
    }

    @Test
    public void testVerifyData() {
        LiveData<List<RecipeStep>> recipeStepLiveData = constructLiveData(Arrays.asList(RecipeTestData.stepEntity11, RecipeTestData.stepEntity12, RecipeTestData.stepEntity13));
        when(repository.retrieveRecipeSteps(RECIPE_ID)).thenReturn(recipeStepLiveData);
        LiveData<List<RecipeStep>> resultLiveData = viewModel.retrieveRecipeSteps(RECIPE_ID);

        assertNotNull(resultLiveData);

        List<RecipeStep> result = resultLiveData.getValue();

        assertNotNull(result);
        assertThat(result.size(), Matchers.is(3));
        assertThat(result.get(0), Matchers.is(RecipeTestData.stepEntity11));
        assertThat(result.get(1), Matchers.is(RecipeTestData.stepEntity12));
        assertThat(result.get(2), Matchers.is(RecipeTestData.stepEntity13));
    }
}
