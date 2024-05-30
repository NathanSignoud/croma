package com.tsan.chromaynk.datatypes;

public class Cursor {

    private double x = 0, y = 0;
    private int red = 0, green = 0, blue = 0;
    private double rotation = 0;

    public Cursor(){}

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public double getRotation(){
        return rotation;
    }

    public void setX(double x){
        this.x = x;
    }
    
    public void setY(double y){
        this.y = y;
    }
    
    public void setRotation(double r){
        this.rotation = r;
    }

}
