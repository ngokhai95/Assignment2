package com.example.ngokhai.photogalleryapp;

public class Image
{
    private int id;
    private double latitude, longitude;
    private String imageName, capName;

    public Image(int id, String imageName, String capName, double latitude, double longitude)
    {
        this.id = id;
        this.imageName = imageName;
        this.capName = capName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId()
    {
        return id;
    }
    public void setId()
    {
        this.id = id;
    }

    public String getImageName()
    {
        return imageName;
    }
    public void setImageName()
    {
        this.imageName = imageName;
    }

    public String getCapName()
    {
        return capName;
    }
    public void setCapName()
    {
        this.capName = capName;
    }

    double getLatitude()
    {
        return latitude;
    }
    double getLongitude()
    {
        return longitude;
    }
}
