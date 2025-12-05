package com.community.model;

public class Photo {
    private String text;
    private int correctAngle;
    private String base64Image;
    private String originalImage;
    private int initialAngle;

    public Photo(String text, int correctAngle, String base64Image) {
        this.text = text;
        this.correctAngle = correctAngle;
        this.base64Image = base64Image;
    }

    public Photo(String text, int correctAngle, String base64Image, String originalImage, int initialAngle) {
        this.text = text;
        this.correctAngle = correctAngle;
        this.base64Image = base64Image;
        this.originalImage = originalImage;
        this.initialAngle = initialAngle;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCorrectAngle() {
        return correctAngle;
    }

    public void setCorrectAngle(int correctAngle) {
        this.correctAngle = correctAngle;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public String getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(String originalImage) {
        this.originalImage = originalImage;
    }

    public int getInitialAngle() {
        return initialAngle;
    }

    public void setInitialAngle(int initialAngle) {
        this.initialAngle = initialAngle;
    }
}