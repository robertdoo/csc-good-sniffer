package ContentExtract;


public class NewThread extends Thread{

	/**
	 * @param args
	 */
	private  SQLTest st;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
	public NewThread(SQLTest st){
		this.st = st;
	}
	
	public void add(SQLTest SqlTest){
		try{
		SqlTest.add();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void run(){
		add(st);
	}

}
