package com.example.projectem8uf2;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface Controller {

    @GET("{raids}")
    Call<List<Raid>> getAllRaids(@Path("raids") String raids);

    @GET("{spawns}")
    Call<List<Spawn>> getAllSpawns(@Path("spawns") String spawns);

    @GET("poke/{id}")
    Call<String> getPokeByID(@Path("id") int id);

    @GET("raid/{id}")
    Call<Raid> getRaidByID(@Path("id") int id);

    @GET("spawn/{id}")
    Call<Spawn> getSpawnByID(@Path("id") int id);

    @PUT("raid/")
    Call<Raid> updateRaid(@Body Raid raid);

    @PUT("spawn/")
    Call<Spawn> updateSpawn(@Body Spawn spawn);

    @DELETE("raid/{id}")
    Call<Void> deleteRaid(@Path("id") int id);

    @DELETE("spawn/{id}")
    Call<Void> deleteSpawn(@Path("id") int id);

    @POST("spawn")
    Call<Spawn> insertSpawn(@Body Spawn spawn);
}
