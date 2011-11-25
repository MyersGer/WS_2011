import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class Clock {
	long seconds;
	boolean runs;
	
	public Clock(){
		seconds = 0;
		runs = false;
	}
	
	public void start(){
		runs = true;
	}
	
	public void stop(){
		runs = false;
	}
	
	public void reset(){
		this.seconds = 0;
	}
	
	public void update(){
		this.seconds ++;
	}
	
	public long getSeconds(){
		return this.seconds;
	}
	
	public String getTimeString(){
		int minutes = (int) (seconds / 60);
		long secs = seconds % 60;
		
		return minutes+":"+secs;
	}
}
