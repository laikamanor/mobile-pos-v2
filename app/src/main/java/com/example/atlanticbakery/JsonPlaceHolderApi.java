package com.example.atlanticbakery;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolderApi {

    @GET("api/item/getall")
    Call<List<token_class>> getToken();
}
