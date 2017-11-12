package com.example.butterflyrecognition.recycleView.indexBar;

import com.google.gson.annotations.SerializedName;
import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;

import org.litepal.annotation.Column;

/**
 * Created by Dr.P on 2017/11/12.
 * runas /user:Dr.P "cmd /k"
 */

public class ButterflyInfo_copy extends BaseIndexPinyinBean {
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

    private boolean isTop;//是否是最上面的 不需要被转化成拼音的

    public ButterflyInfo_copy() {
    }

    public ButterflyInfo_copy(String name) {
        this.name = name;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ButterflyInfo_copy setName(String name) {
        this.name = name;
        return this;
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

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isTop() {
        return isTop;
    }

    public ButterflyInfo_copy setTop(boolean top) {
        isTop = top;
        return this;
    }

    @Override
    public String getTarget() {
        return name;
    }

    @Override
    public boolean isNeedToPinyin() {
        return !isTop;
    }


    @Override
    public boolean isShowSuspension() {
        return !isTop;
    }
}
