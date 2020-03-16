package com.example.projectem8uf2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Raid {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("nivel")
    @Expose
    private Integer nivel;
    @SerializedName("lat")
    @Expose
    private Float lat;
    @SerializedName("lng")
    @Expose
    private Float lng;
    @SerializedName("pokeId")
    @Expose
    private Integer pokeId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public Integer getPokeId() {
        return pokeId;
    }

    public void setPokeId(Integer pokeId) {
        this.pokeId = pokeId;
    }

}