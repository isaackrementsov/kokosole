package app.models;
import java.time.LocalDate;
public class Duration {
    public LocalDate start;
    public LocalDate end;
    public Duration(LocalDate start, LocalDate end){
        this.start = start;
        this.end = end;
    }
    public Duration(java.sql.Date start, java.sql.Date end){
        this.start = start.toLocalDate();
        this.end = end.toLocalDate();
    }
}