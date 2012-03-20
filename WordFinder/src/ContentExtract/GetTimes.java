package ContentExtract;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class GetTimes {

	/**
	 * @param args
	 */
	LinkedList<Date> dates;
	TimeExtract te;
	SimpleDateFormat format;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public GetTimes(){
		dates = new LinkedList<Date>();
		te = new TimeExtract();
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	public LinkedList<Date> getTimes(LinkedList<String> urls){
		
		int length = urls.size();
		String time;
		Date date = null;
		for(int i=0;i<length;i++){
			time = te.getTime(urls.get(i));
			if(time=="no time"||time=="error"){
				try{
				dates.add(format.parse("0000-00-00  00:00:00"));
				continue;
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			try{
				System.out.println(time);
				date = format.parse(time);
			}catch(Exception e){
				System.err.println("转换时间出错！");
			}
			
			dates.add(date);
		}
		
		
		return dates;
	}

}
