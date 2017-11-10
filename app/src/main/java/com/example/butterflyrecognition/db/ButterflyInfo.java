package com.example.butterflyrecognition.db;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by Dr.P on 2017/11/6.
 */

public class ButterflyInfo extends DataSupport{
    Bitmap bitmap;

    @SerializedName("id")
    private int id;

    @SerializedName("image")
    private String imageUrl;

    @Column(unique = true)
    @SerializedName("name")
    private String name;

    @SerializedName("latinName")
    private String latinName;

    @SerializedName("type")
    private String type;

    @SerializedName("feature")
    private String feature;

    @SerializedName("area")
    private String area;

    @SerializedName("protect")
    private String protect;

    @SerializedName("rare")
    private String rare;

    @SerializedName("uniqueToChina")
    private String uniqueToChina;

    private String imagePath;

    public ButterflyInfo() {
    }

    public int getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getLatinName() {
        return latinName;
    }

    public String getType() {
        return type;
    }

    public String getFeature() {
        return feature;
    }

    public String getArea() {
        return area;
    }

    public String getProtect() {
        return protect;
    }

    public String getRare() {
        return rare;
    }

    public String getUniqueToChina() {
        return uniqueToChina;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatinName(String latinName) {
        this.latinName = latinName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setProtect(String protect) {
        this.protect = protect;
    }

    public void setRare(String rare) {
        this.rare = rare;
    }

    public void setUniqueToChina(String uniqueToChina) {
        this.uniqueToChina = uniqueToChina;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
