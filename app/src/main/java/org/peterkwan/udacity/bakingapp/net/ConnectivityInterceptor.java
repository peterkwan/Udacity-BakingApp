package org.peterkwan.udacity.bakingapp.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import java.io.IOException;

import lombok.AllArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@AllArgsConstructor
class ConnectivityInterceptor implements Interceptor {

    private Context context;

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException, RuntimeException {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnectedOrConnecting())
                throw new IOException("No connectivity exception");

            Request.Builder builder = chain.request().newBuilder();
            return chain.proceed(builder.build());
        }

        throw new RuntimeException("No service found");
    }
}
