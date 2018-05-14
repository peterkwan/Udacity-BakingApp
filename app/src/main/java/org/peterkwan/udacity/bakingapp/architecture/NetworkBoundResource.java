package org.peterkwan.udacity.bakingapp.architecture;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

public abstract class NetworkBoundResource<ResultType, RequestType> {

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDatabase();

    @MainThread
    protected abstract boolean shouldFetchFromNetwork(@Nullable ResultType data);

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    @NonNull
    @MainThread
    protected abstract LiveData<IApiResponse<RequestType>> createNetworkCall();

    @MainThread
    protected abstract void onFetchFailed();

    @MainThread
    public NetworkBoundResource() {
        result.postValue(Resource.loading((ResultType) null, null));
        final LiveData<ResultType> dbSource = loadFromDatabase();

        result.addSource(dbSource, new Observer<ResultType>() {
            @Override
            public void onChanged(@Nullable ResultType data) {
                result.removeSource(dbSource);

                if (shouldFetchFromNetwork(data)) {
                    fetchDataFromNetwork(dbSource);
                }
                else {
                    result.addSource(dbSource, new Observer<ResultType>() {
                        @Override
                        public void onChanged(@Nullable ResultType newData) {
                            if (newData != null)
                                result.postValue(Resource.success(newData));
                        }
                    });
                }
            }
        });
    }

    private void fetchDataFromNetwork(final LiveData<ResultType> dbSource) {
        final LiveData<IApiResponse<RequestType>> apiResponse = createNetworkCall();

        result.addSource(dbSource, new Observer<ResultType>() {
            @Override
            public void onChanged(@Nullable ResultType newData) {
                result.postValue(Resource.loading(newData, null));
            }
        });

        result.addSource(apiResponse, new Observer<IApiResponse<RequestType>>() {
            @Override
            public void onChanged(@Nullable final IApiResponse<RequestType> response) {
                result.removeSource(apiResponse);
                result.removeSource(dbSource);

                if (response != null && response.isSuccessful()) {
                    saveResultAndReInit(response);
                }
                else {
                    onFetchFailed();
                    result.addSource(dbSource, new Observer<ResultType>() {
                        @Override
                        public void onChanged(@Nullable ResultType newData) {
                            result.postValue(Resource.error(newData, response == null ? "" : response.getErrorMessage()));
                        }
                    });
                }
            }
        });
    }

    @MainThread
    private void saveResultAndReInit(final IApiResponse<RequestType> response) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                saveCallResult(response.getBody());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                result.addSource(loadFromDatabase(), new Observer<ResultType>() {
                    @Override
                    public void onChanged(@Nullable ResultType data) {
                        if (data != null)
                            result.postValue(Resource.success(data));
                    }
                });
            }

        }.execute();
    }

    public final MediatorLiveData<Resource<ResultType>> getAsLiveData() {
        return result;
    }

}
