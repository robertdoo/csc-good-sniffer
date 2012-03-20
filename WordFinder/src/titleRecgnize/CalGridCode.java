package titleRecgnize;
/*
 * 计算网格特征值的类
 */
public class CalGridCode {
	static public int[] get64Partions(int[] pix,int width,int height){
		int[] code=new int[64];
		
		for(int i=0;i<height;i++)
			for(int j=0;j<width;j++)
				if(pix[i*width+j]==0xffffffff){
					if((i>=0 && i<10) && (j>=0 && j<10))
						code[0]++;
					if((i>=0 && i<10) && (j>=10 && j<20))
						code[1]++;
					if((i>=0 && i<10) && (j>=20 && j<30))
						code[2]++;
					if((i>=0 && i<10) && (j>=30 && j<40))
						code[3]++;
					if((i>=0 && i<10) && (j>=40 && j<50))
						code[4]++;
					if((i>=0 && i<10) && (j>=50 && j<60))
						code[5]++;
					if((i>=0 && i<10) && (j>=60 && j<70))
						code[6]++;
					if((i>=0 && i<10) && (j>=70 && j<80))
						code[7]++;
					if((i>=10 && i<20) && (j>=0 && j<10))
						code[8]++;
					if((i>=10 && i<20) && (j>=10 && j<20))
						code[9]++;
					if((i>=10 && i<20) && (j>=20 && j<30))
						code[10]++;
					if((i>=10 && i<20) && (j>=30 && j<40))
						code[11]++;
					if((i>=10 && i<20) && (j>=40 && j<50))
						code[12]++;
					if((i>=10 && i<20) && (j>=50 && j<60))
						code[13]++;
					if((i>=10 && i<20) && (j>=60 && j<70))
						code[14]++;
					if((i>=10 && i<20) && (j>=70 && j<80))
						code[15]++;
					if((i>=20 && i<30) && (j>=0 && j<10))
						code[16]++;
					if((i>=20 && i<30) && (j>=10 && j<20))
						code[17]++;
					if((i>=20 && i<30) && (j>=20 && j<30))
						code[18]++;
					if((i>=20 && i<30) && (j>=30 && j<40))
						code[19]++;
					if((i>=20 && i<30) && (j>=40 && j<50))
						code[20]++;
					if((i>=20 && i<30) && (j>=50 && j<60))
						code[21]++;
					if((i>=20 && i<30) && (j>=60 && j<70))
						code[22]++;
					if((i>=20 && i<30) && (j>=70 && j<80))
						code[23]++;
					if((i>=30 && i<40) && (j>=0 && j<10))
						code[24]++;
					if((i>=30 && i<40) && (j>=10 && j<20))
						code[25]++;
					if((i>=30 && i<40) && (j>=20 && j<30))
						code[26]++;
					if((i>=30 && i<40) && (j>=30 && j<40))
						code[27]++;
					if((i>=30 && i<40) && (j>=40 && j<50))
						code[28]++;
					if((i>=30 && i<40) && (j>=50 && j<60))
						code[29]++;
					if((i>=30 && i<40) && (j>=60 && j<70))
						code[30]++;
					if((i>=30 && i<40) && (j>=70 && j<80))
						code[31]++;
					if((i>=40 && i<50) && (j>=0 && j<10))
						code[32]++;
					if((i>=40 && i<50) && (j>=10 && j<20))
						code[33]++;
					if((i>=40 && i<50) && (j>=20 && j<30))
						code[34]++;
					if((i>=40 && i<50) && (j>=30 && j<40))
						code[35]++;
					if((i>=40 && i<50) && (j>=40 && j<50))
						code[36]++;
					if((i>=40 && i<50) && (j>=50 && j<60))
						code[37]++;
					if((i>=40 && i<50) && (j>=60 && j<70))
						code[38]++;
					if((i>=40 && i<50) && (j>=70 && j<80))
						code[39]++;
					if((i>=50 && i<60) && (j>=0 && j<10))
						code[40]++;
					if((i>=50 && i<60) && (j>=10 && j<20))
						code[41]++;
					if((i>=50 && i<60) && (j>=20 && j<30))
						code[42]++;
					if((i>=50 && i<60) && (j>=30 && j<40))
						code[43]++;
					if((i>=50 && i<60) && (j>=40 && j<50))
						code[44]++;
					if((i>=50 && i<60) && (j>=50 && j<60))
						code[45]++;
					if((i>=50 && i<60) && (j>=60 && j<70))
						code[46]++;
					if((i>=50 && i<60) && (j>=70 && j<80))
						code[47]++;
					if((i>=60 && i<70) && (j>=0 && j<10))
						code[48]++;
					if((i>=60 && i<70) && (j>=10 && j<20))
						code[49]++;
					if((i>=60 && i<70) && (j>=20 && j<30))
						code[50]++;
					if((i>=60 && i<70) && (j>=30 && j<40))
						code[51]++;
					if((i>=60 && i<70) && (j>=40 && j<50))
						code[52]++;
					if((i>=60 && i<70) && (j>=50 && j<60))
						code[53]++;
					if((i>=60 && i<70) && (j>=60 && j<70))
						code[54]++;
					if((i>=60 && i<70) && (j>=70 && j<80))
						code[55]++;
					if((i>=70 && i<80) && (j>=0 && j<10))
						code[56]++;
					if((i>=70 && i<80) && (j>=10 && j<20))
						code[57]++;
					if((i>=70 && i<80) && (j>=20 && j<30))
						code[58]++;
					if((i>=70 && i<80) && (j>=30 && j<40))
						code[59]++;
					if((i>=70 && i<80) && (j>=40 && j<50))
						code[60]++;
					if((i>=70 && i<80) && (j>=50 && j<60))
						code[61]++;
					if((i>=70 && i<80) && (j>=60 && j<70))
						code[62]++;
					if((i>=70 && i<80) && (j>=70 && j<80))
						code[63]++;
				}
			
		return code;
	}
	
	static public int[] get25Partions(int[] pix,int width,int height){
		int[] code=new int[25];
		
		for(int i=0;i<height;i++)
			for(int j=0;j<width;j++)
				if(pix[i*width+j]==0xffffffff){
					if((i>=0 && i<16) && (j>=0 && j<16))
						code[0]++;
					if((i>=0 && i<16) && (j>=16 && j<32))
						code[1]++;
					if((i>=0 && i<16) && (j>=32 && j<48))
						code[2]++;
					if((i>=0 && i<16) && (j>=48 && j<64))
						code[3]++;
					if((i>=0 && i<16) && (j>=64 && j<80))
						code[4]++;
					if((i>=16 && i<32) && (j>=0 && j<16))
						code[5]++;
					if((i>=16 && i<32) && (j>=16 && j<32))
						code[6]++;
					if((i>=16 && i<32) && (j>=32 && j<48))
						code[7]++;
					if((i>=16 && i<32) && (j>=48 && j<64))
						code[8]++;
					if((i>=16 && i<32) && (j>=64 && j<80))
						code[9]++;
					if((i>=32 && i<48) && (j>=0 && j<16))
						code[10]++;
					if((i>=32 && i<48) && (j>=16 && j<32))
						code[11]++;
					if((i>=32 && i<48) && (j>=32 && j<48))
						code[12]++;
					if((i>=32 && i<48) && (j>=48 && j<64))
						code[13]++;
					if((i>=32 && i<48) && (j>=64 && j<80))
						code[14]++;
					if((i>=48 && i<64) && (j>=0 && j<16))
						code[15]++;
					if((i>=48 && i<64) && (j>=16 && j<32))
						code[16]++;
					if((i>=48 && i<64) && (j>=32 && j<48))
						code[17]++;
					if((i>=48 && i<64) && (j>=48 && j<64))
						code[18]++;
					if((i>=48 && i<64) && (j>=64 && j<80))
						code[19]++;
					if((i>=64 && i<80) && (j>=0 && j<16))
						code[20]++;
					if((i>=64 && i<80) && (j>=16 && j<32))
						code[21]++;
					if((i>=64 && i<80) && (j>=32 && j<48))
						code[22]++;
					if((i>=64 && i<80) && (j>=48 && j<64))
						code[23]++;
					if((i>=64 && i<80) && (j>=64 && j<80))
						code[24]++;
				}
			
		return code;
	}
	
	static public int[][] get64FloatGridCode(int[] pix,int width,int height){
		int[][] floatGridCode=new int[9][];
		
		floatGridCode[0]=get64Partions(Movement.moveUpLeft(pix, width, height, 1),width,height);
		floatGridCode[1]=get64Partions(Movement.moveUp(pix, width, height, 1),width,height);
		floatGridCode[2]=get64Partions(Movement.moveUpRight(pix, width, height, 1),width,height);
		floatGridCode[3]=get64Partions(Movement.moveLeft(pix, width, height, 1),width,height);
		floatGridCode[4]=get64Partions(pix,width,height);
		floatGridCode[5]=get64Partions(Movement.moveRight(pix, width, height, 1),width,height);
		floatGridCode[6]=get64Partions(Movement.moveDownLeft(pix, width, height, 1),width,height);
		floatGridCode[7]=get64Partions(Movement.moveDown(pix, width, height, 1),width,height);
		floatGridCode[8]=get64Partions(Movement.moveDownRight(pix, width, height, 1),width,height);
		
		return floatGridCode;
	}
	
	static public int[][] get25FloatGridCode(int[] pix,int width,int height){
		int[][] floatGridCode=new int[9][];
		
		floatGridCode[0]=get25Partions(Movement.moveUpLeft(pix, width, height, 1),width,height);
		floatGridCode[1]=get25Partions(Movement.moveUp(pix, width, height, 1),width,height);
		floatGridCode[2]=get25Partions(Movement.moveUpRight(pix, width, height, 1),width,height);
		floatGridCode[3]=get25Partions(Movement.moveLeft(pix, width, height, 1),width,height);
		floatGridCode[4]=get25Partions(pix,width,height);
		floatGridCode[5]=get25Partions(Movement.moveRight(pix, width, height, 1),width,height);
		floatGridCode[6]=get25Partions(Movement.moveDownLeft(pix, width, height, 1),width,height);
		floatGridCode[7]=get25Partions(Movement.moveDown(pix, width, height, 1),width,height);
		floatGridCode[8]=get25Partions(Movement.moveDownRight(pix, width, height, 1),width,height);
		
		return floatGridCode;
	}
	
	static public int[][] getBigMi64FloatGridCode(int[] pix,int width,int height){
		int[][] floatGridCode=new int[25][];
		
		floatGridCode[0]=get64Partions(Movement.moveUpLeft(pix, width, height, 3),width,height);
		floatGridCode[1]=get64Partions(Movement.moveUp(pix, width, height, 3),width,height);
		floatGridCode[2]=get64Partions(Movement.moveUpRight(pix, width, height, 3),width,height);
		
		floatGridCode[3]=get64Partions(Movement.moveUpLeft(pix, width, height, 2),width,height);
		floatGridCode[4]=get64Partions(Movement.moveUp(pix, width, height, 2),width,height);
		floatGridCode[5]=get64Partions(Movement.moveUpRight(pix, width, height, 2),width,height);
		
		floatGridCode[6]=get64Partions(Movement.moveUpLeft(pix, width, height, 1),width,height);	
		floatGridCode[7]=get64Partions(Movement.moveUp(pix, width, height, 1),width,height);
		floatGridCode[8]=get64Partions(Movement.moveUpRight(pix, width, height, 1),width,height);
		
		floatGridCode[9]=get64Partions(Movement.moveLeft(pix, width, height, 3),width,height);
		floatGridCode[10]=get64Partions(Movement.moveLeft(pix, width, height, 2),width,height);
		floatGridCode[11]=get64Partions(Movement.moveLeft(pix, width, height, 1),width,height);
		floatGridCode[12]=get64Partions(pix,width,height);
		floatGridCode[13]=get64Partions(Movement.moveRight(pix, width, height, 1),width,height);
		floatGridCode[14]=get64Partions(Movement.moveRight(pix, width, height, 2),width,height);
		floatGridCode[15]=get64Partions(Movement.moveRight(pix, width, height, 3),width,height);
		
		floatGridCode[16]=get64Partions(Movement.moveDownLeft(pix, width, height, 1),width,height);
		floatGridCode[17]=get64Partions(Movement.moveDown(pix, width, height, 1),width,height);
		floatGridCode[18]=get64Partions(Movement.moveDownRight(pix, width, height, 1),width,height);
		
		floatGridCode[19]=get64Partions(Movement.moveDownLeft(pix, width, height, 2),width,height);
		floatGridCode[20]=get64Partions(Movement.moveDown(pix, width, height, 2),width,height);
		floatGridCode[21]=get64Partions(Movement.moveDownRight(pix, width, height, 2),width,height);
		
		floatGridCode[22]=get64Partions(Movement.moveDownLeft(pix, width, height, 3),width,height);
		floatGridCode[23]=get64Partions(Movement.moveDown(pix, width, height, 3),width,height);
		floatGridCode[24]=get64Partions(Movement.moveDownRight(pix, width, height, 3),width,height);
		
		return floatGridCode;
	}
}
