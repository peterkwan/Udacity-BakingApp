package org.peterkwan.udacity.bakingapp.architecture;

public interface IApiResponse<ResultType> {

    ResultType getBody();
    String getErrorMessage();
    boolean isSuccessful();

}
