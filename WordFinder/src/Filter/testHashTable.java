package Filter;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;

public class testHashTable {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		readData();
	}
	
	public static void readData(){
		try{
			DataInputStream in = new DataInputStream(new BufferedInputStream(
					new FileInputStream("HashTable.dat")));
			DataInputStream in1 = new DataInputStream(new BufferedInputStream(
					new FileInputStream("HashTable1.dat")));
			
			int num = in.readInt();
			int num1 = in1.readInt();
			System.out.println(num);
			System.out.println(num1);
			
			/*for(int i=0;i<num1;i++){
				System.out.print(in.readFloat()+"  ");
				System.out.println(in1.readFloat());
				
			}*/
			
			//in.close();
			in1.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
