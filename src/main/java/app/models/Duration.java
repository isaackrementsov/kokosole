package app.models;
import java.util.Date;
public class Duration {
    public Date start;
    public Date end;
    public Duration(Date start, Date end){
        this.start = start;
        this.end = end;
    }
    public Duration(java.sql.Date start, java.sql.Date end){
        this.start = (Date) start;
        this.end = (Date) end;
    }
}