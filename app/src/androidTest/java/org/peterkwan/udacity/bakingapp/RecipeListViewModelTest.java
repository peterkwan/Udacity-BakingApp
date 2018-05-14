package org.peterkwan.udacity.bakingapp;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.peterkwan.udacity.bakingapp.architecture.INetworkFailureCallback;
import org.peterkwan.udacity.bakingapp.architecture.Resource;
import org.peterkwan.udacity.bakingapp.data.RecipeTestData;
import org.peterkwan.udacity.bakingapp.data.entity.Recipe;
import org.peterkwan.udacity.bakingapp.repository.RecipeRepository;
import org.peterkwan.udacity.bakingapp.utils.BakingAppTestRunner;
import org.peterkwan.udacity.bakingapp.viewmodel.RecipeListViewModel;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.peterkwan.udacity.bakingapp.architecture.Resource.Status.ERROR;
import static org.peterkwan.udacity.bakingapp.architecture.Resource.Status.SUCCESS;
import static org.peterkwan.udacity.bakingapp.utils.LiveDataTestUtils.constructLiveData;

@RunWith(AndroidJUnit4.class)
public class RecipeListViewModelTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private RecipeRepository repository;
    private INetworkFailureCallback networkFailureCallback;
    private RecipeListViewModel viewModel;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        BakingApp app = (BakingApp) BakingAppTestRunner.newApplication(BakingApp.class, InstrumentationRegistry.getInstrumentation().getTargetContext());

        repository = mock(RecipeRepository.class);
        networkFailureCallback = mock(INetworkFailureCallback.class);
        viewModel = new RecipeListViewModel(app, repository);
    }

    @Test
    public void testVerifyRepositoryCall() {
        viewModel.retrieveRecipeData(networkFailureCallback);
        verify(repository).retrieveRecipeList(networkFailureCallback);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSuccessResponseData() {
        Resource<List<Recipe>> testResource = Resource.success(Arrays.asList(RecipeTestData.recipeEntity1, RecipeTestData.recipeEntity2));
        MediatorLiveData<Resource<List<Recipe>>> testLiveData = (MediatorLiveData)constructLiveData(testResource);

        when(repository.retrieveRecipeList(networkFailureCallback)).thenReturn(testLiveData);
        LiveData<Resource<List<Recipe>>> resultLiveData = viewModel.retrieveRecipeData(networkFailureCallback);

        assertNotNull(resultLiveData);

        Resource<List<Recipe>> resultResource = resultLiveData.getValue();

        assertNotNull(resultResource);
        assertThat(resultResource.getStatus(), Matchers.is(SUCCESS));
        assertNull(resultResource.getMessage());
        assertNotNull(resultResource.getData());
        assertThat(resultResource.getData().size(), Matchers.is(2));
        assertThat(resultResource.getData().get(0), Matchers.is(RecipeTestData.recipeEntity1));
        assertThat(resultResource.getData().get(1), Matchers.is(RecipeTestData.recipeEntity2));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testErrorResponseData() {
        String errorMessage = "Error loading resource";
        MediatorLiveData<Resource<List<Recipe>>> testLiveData = (MediatorLiveData)constructLiveData(
                Resource.error((List<Recipe>) null, errorMessage)
        );

        when(repository.retrieveRecipeList(networkFailureCallback)).thenReturn(testLiveData);
        LiveData<Resource<List<Recipe>>> resultLiveData = viewModel.retrieveRecipeData(networkFailureCallback);

        assertNotNull(resultLiveData);

        Resource<List<Recipe>> resultResource = resultLiveData.getValue();

        assertNotNull(resultResource);
        assertThat(resultResource.getStatus(), Matchers.is(ERROR));
        assertThat(resultResource.getMessage(), Matchers.is(errorMessage));
        assertNull(resultResource.getData());
    }

}
