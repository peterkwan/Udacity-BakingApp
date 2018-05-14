package org.peterkwan.udacity.bakingapp.architecture;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.peterkwan.udacity.bakingapp.architecture.Resource.Status.ERROR;
import static org.peterkwan.udacity.bakingapp.architecture.Resource.Status.LOADING;
import static org.peterkwan.udacity.bakingapp.architecture.Resource.Status.SUCCESS;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Resource<T> {

    public enum Status {
        LOADING, SUCCESS, ERROR
    }

    @NonNull
    private final Status status;

    @Nullable
    private final T data;

    @Nullable
    private final String message;

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(SUCCESS, data, null);
    }

    public static <T> Resource<T> error(@Nullable T data, @NonNull String message) {
        return new Resource<>(ERROR, data, message);
    }

    public static <T> Resource<T> loading(@Nullable T data, @Nullable String message) {
        return new Resource<>(LOADING, data, message);
    }

}
