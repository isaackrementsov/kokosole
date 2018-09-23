package app.models;
import java.util.Date;
public class Duration {
    public Date start;
    public Date end;
    public Duration(Date start, Date end){
        this.start = start;
        this.end = end;
    }
}