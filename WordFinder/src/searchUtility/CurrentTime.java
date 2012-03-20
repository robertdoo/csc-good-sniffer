package searchUtility;

import java.util.Calendar;

public class CurrentTime 
{
	public static String getInstance()
	{
		String currentTime;
		Calendar cal = Calendar.getInstance();
		String year = format(cal.get(Calendar.YEAR));
		String month = format(cal.get(Calendar.MONTH)+1);
		String day = format(cal.get(Calendar.DAY_OF_MONTH));
		String hour = format(cal.get(Calendar.HOUR_OF_DAY));
		String minute = format(cal.get(Calendar.MINUTE));
		String second = format(cal.get(Calendar.SECOND));
		currentTime = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;
		return currentTime;
	}
	static String format(int t)
	{
		String s = new String("" + t);
		if (s.length() == 1)
		{
			s = "0"+s;
		}
		return s;
	}
	
	public static int getMonthOfYear(){
		Calendar cal = Calendar.getInstance();
		int m = cal.get(Calendar.YEAR)*100 + cal.get(Calendar.MONTH)+1;
		return m;
	}
	
	public static void main(String[] args) 
	{
		String t = CurrentTime.getInstance();
		System.out.println(t);
		System.out.println(getMonthOfYear());
	}
}

