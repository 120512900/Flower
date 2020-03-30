package com.example.myapplication;

public class Info {
    private String name;
    private String url;
    private String Baike_url;
    public String getBaike_url() {
        return Baike_url;
    }

    public void setBaike_url(String baike_url) {
        Baike_url = baike_url;
    }



    public Info() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



}
