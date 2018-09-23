package app.models;
import java.util.Date;
public class Activity {
    public String name;
    public Duration duration;
    public User[] participants;
    public void Activity(String name, Date start, Date end, User[] participants){
        this.name = name;
        this.duration = new Duration(start, end);
        this.participants = participants;
    }
}