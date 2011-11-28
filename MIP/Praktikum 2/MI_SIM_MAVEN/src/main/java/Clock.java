import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class Clock {
	long seconds;
	boolean runs;
	Date startTime;
	
	public Clock(Date startTime){
		this.startTime = startTime;
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
	
	public Date getAbsoluteTime(){
		Calendar c = Calendar.getInstance();
		c.setTime(startTime);
		c.add(Calendar.SECOND, (int) seconds);
		return c.getTime();
	}
}
