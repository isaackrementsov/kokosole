package app.models;
public class Trip {
    public String name;
    public Location[] locations;
    public Trip(String name, Location[] locations){
        this.name = name;
        this.locations = locations;
    }
}