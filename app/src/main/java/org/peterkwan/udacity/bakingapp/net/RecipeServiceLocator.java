package org.peterkwan.udacity.bakingapp.net;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.peterkwan.udacity.bakingapp.net.NetConstants.RECIPE_BASE_URL;

public final class RecipeServiceLocator {

    private static final String CLOSE = "close";
    private static final String CONNECTION = "connection";

    public static IRecipeService getService(final Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RECIPE_BASE_URL)
                .client(createHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(IRecipeService.class);
    }

    private static OkHttpClient createHttpClient(Context context) {
        return new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader(CONNECTION, CLOSE)
                                .build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(new ConnectivityInterceptor(context))
                .build();
    }

}
