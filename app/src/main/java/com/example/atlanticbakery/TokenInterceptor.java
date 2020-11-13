package com.example.atlanticbakery;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        //rewrite the request to add bearer token
        Request newRequest=chain.request().newBuilder()
                .header("Authorization","Bearer "+ "eyJhbGciOiJIUzUxMiIsImlhdCI6MTYwMjgzODc1MCwiZXhwIjoxNjAyODYwMzUwfQ.eyJ1c2VyX2lkIjoyfQ.258HElC8GalgQF8xkvsNyimDpzVGTfkeZ8x2AbrCgjln7OH8Bk7ikX5TinqJKrNX6q_K_YR1k-L8teVWwpMumA")
                .build();

        return chain.proceed(newRequest);
    }
}
