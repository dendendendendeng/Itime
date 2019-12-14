package com.example.itime;

public class Add_item {
    private int add_image;
    private String maintext;
    private String subtext;
    private String lefttext;

    public Add_item(int add_image, String maintext, String subtext, String lefttext) {
        this.add_image = add_image;
        this.maintext = maintext;
        this.subtext = subtext;
        this.lefttext = lefttext;
    }

    public int getAdd_image() {
        return add_image;
    }

    public void setAdd_image(int add_image) {
        this.add_image = add_image;
    }

    public String getMaintext() {
        return maintext;
    }

    public void setMaintext(String maintext) {
        this.maintext = maintext;
    }

    public String getSubtext() {
        return subtext;
    }

    public void setSubtext(String subtext) {
        this.subtext = subtext;
    }

    public String getLefttext() {
        return lefttext;
    }

    public void setLefttext(String lefttext) {
        this.lefttext = lefttext;
    }
}
