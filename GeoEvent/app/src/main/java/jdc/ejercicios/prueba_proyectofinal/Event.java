package jdc.ejercicios.prueba_proyectofinal;

import java.io.Serializable;

public class Event implements Serializable {
    private int id;
    private String title;
    private String description;
    private String date;
    private String imageUri;
    private double latitude;   // Nueva propiedad
    private double longitude;  // Nueva propiedad

    public Event(int id, String title, String description, String date, String imageUri, double latitude, double longitude) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.imageUri = imageUri;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getImageUri() { return imageUri; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(String date) { this.date = date; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}
