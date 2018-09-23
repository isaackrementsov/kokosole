package app.models;
import java.util.Date;
public class Location {
    public String town;
    public String subdivision;
    public String country;
    public int zip;
    public Duration duration;
    public Activity[] activities;
    public Location(String town, String subdivision, String country, int zip, Date start, Date end, Activity[] activities){
        this.town = town;
        this.subdivision = subdivision;
        this.country = country;
        this.zip = zip;
        this.duration = new Duration(start, end);
        this.activities = activities;
    }
}