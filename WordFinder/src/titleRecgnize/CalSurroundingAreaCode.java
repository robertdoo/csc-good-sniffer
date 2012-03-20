package titleRecgnize;
/*
 * 计算四周面积编码
 * 上下的宽度为10，左右的宽度为15
 */

public class CalSurroundingAreaCode {

	static public int[] getThinedAreaValue(int[] pix,int w,int h){//取细化字库的边缘特征值
		int[] code=new int[4];
		int white;
		//上
		white=0;
		for(int i=0;i<10;i++)
			for(int j=0;j<w;j++)
				if(pix[i*w+j]==0xffffffff)
					white++;
		code[0]=white;
		//左
		white=0;
		for(int i=0;i<h;i++)
			for(int j=0;j<15;j++)
				if(pix[i*w+j]==0xffffffff)
					white++;
		code[1]=white;
		//下
		white=0;
		for(int i=70;i<h;i++)
			for(int j=0;j<w;j++)
				if(pix[i*w+j]==0xffffffff)
					white++;
		code[2]=white;		
		//右
		white=0;
		for(int i=0;i<h;i++)
			for(int j=65;j<w;j++)
				if(pix[i*w+j]==0xffffffff)
					white++;
		code[3]=white;
		return code;
	}
	
	/*
	 * 函数功能：取得一个字符在3x3范围内的浮动特征值
	 * 参数：pix——字符的像素数组
	 *      w——字符宽
	 *      h——字符高
	 * 返回：浮动特征值数组
	 */
	static public int[][] getFloatAreaCode9(int[] pix,int w,int h){
		int[][] floatVector=new int[9][];
		
		floatVector[0]=getThinedAreaValue(Movement.moveUpLeft(pix,w,h,1),w,h);
		floatVector[1]=getThinedAreaValue(Movement.moveUp(pix,w,h,1),w,h);
		floatVector[2]=getThinedAreaValue(Movement.moveUpRight(pix,w,h,1),w,h);
		floatVector[3]=getThinedAreaValue(Movement.moveLeft(pix,w,h,1),w,h);
		
		floatVector[4]=getThinedAreaValue(pix,w,h);
		
		floatVector[5]=getThinedAreaValue(Movement.moveRight(pix,w,h,1),w,h);
		floatVector[6]=getThinedAreaValue(Movement.moveDownLeft(pix,w,h,1),w,h);
		floatVector[7]=getThinedAreaValue(Movement.moveDown(pix,w,h,1),w,h);
		floatVector[8]=getThinedAreaValue(Movement.moveDownRight(pix,w,h,1),w,h);
		
		return floatVector;
	}

	/*
	 * 函数功能：取得一个字符在米字型范围内的浮动特征值
	 * 参数：pix——字符的像素数组
	 *      w——字符宽
	 *      h——字符高
	 * 返回：浮动特征值数组
	 */
	static public int[][] getMiFloatAreaCode(int[] pix,int w,int h){
		int[][] floatVector=new int[17][];
		
		floatVector[0]=getThinedAreaValue(Movement.moveUpLeft(pix,w,h,2),w,h);
		floatVector[1]=getThinedAreaValue(Movement.moveUp(pix,w,h,2),w,h);
		floatVector[2]=getThinedAreaValue(Movement.moveUpRight(pix,w,h,2),w,h);
		
		floatVector[3]=getThinedAreaValue(Movement.moveUpLeft(pix,w,h,1),w,h);
		floatVector[4]=getThinedAreaValue(Movement.moveUp(pix,w,h,1),w,h);
		floatVector[5]=getThinedAreaValue(Movement.moveUpRight(pix,w,h,1),w,h);
		
		floatVector[6]=getThinedAreaValue(Movement.moveLeft(pix,w,h,2),w,h);
		floatVector[7]=getThinedAreaValue(Movement.moveLeft(pix,w,h,1),w,h);
		floatVector[8]=getThinedAreaValue(pix,w,h);	
		floatVector[9]=getThinedAreaValue(Movement.moveRight(pix,w,h,1),w,h);
		floatVector[10]=getThinedAreaValue(Movement.moveRight(pix,w,h,2),w,h);
		
		floatVector[11]=getThinedAreaValue(Movement.moveDownLeft(pix,w,h,1),w,h);
		floatVector[12]=getThinedAreaValue(Movement.moveDown(pix,w,h,1),w,h);
		floatVector[13]=getThinedAreaValue(Movement.moveDownRight(pix,w,h,1),w,h);
		
		floatVector[14]=getThinedAreaValue(Movement.moveDownLeft(pix,w,h,1),w,h);
		floatVector[15]=getThinedAreaValue(Movement.moveDown(pix,w,h,1),w,h);
		floatVector[16]=getThinedAreaValue(Movement.moveDownRight(pix,w,h,1),w,h);
		
		return floatVector;
	}
	
	/*
	 * 函数功能：取得一个字符在十字形范围内的浮动特征值
	 * 参数：pix——字符的像素数组
	 *      w——字符宽
	 *      h——字符高
	 * 返回：浮动特征值数组
	 */
	static public int[][] getCrossFloatAreaCode(int[] pix,int w,int h){
		int[][] floatVector=new int[9][];
		
		floatVector[0]=getThinedAreaValue(Movement.moveUp(pix,w,h,2),w,h);
		floatVector[1]=getThinedAreaValue(Movement.moveUp(pix,w,h,1),w,h);
		
		floatVector[2]=getThinedAreaValue(Movement.moveLeft(pix,w,h,2),w,h);
		floatVector[3]=getThinedAreaValue(Movement.moveLeft(pix,w,h,1),w,h);
		floatVector[4]=getThinedAreaValue(pix,w,h);
		floatVector[5]=getThinedAreaValue(Movement.moveRight(pix,w,h,1),w,h);
		floatVector[6]=getThinedAreaValue(Movement.moveRight(pix,w,h,2),w,h);
		
		floatVector[7]=getThinedAreaValue(Movement.moveDown(pix,w,h,1),w,h);
		floatVector[8]=getThinedAreaValue(Movement.moveDown(pix,w,h,2),w,h);
		
		return floatVector;
	}
	
	static public int[][] getBigMiFloatAreaCode(int[] pix,int w,int h){
		int[][] floatVector=new int[25][];
		
		floatVector[0]=getThinedAreaValue(Movement.moveUpLeft(pix,w,h,3),w,h);
		floatVector[1]=getThinedAreaValue(Movement.moveUp(pix,w,h,3),w,h);
		floatVector[2]=getThinedAreaValue(Movement.moveUpRight(pix,w,h,3),w,h);
		
		floatVector[3]=getThinedAreaValue(Movement.moveUpLeft(pix,w,h,2),w,h);
		floatVector[4]=getThinedAreaValue(Movement.moveUp(pix,w,h,2),w,h);
		floatVector[5]=getThinedAreaValue(Movement.moveUpRight(pix,w,h,2),w,h);
		
		floatVector[6]=getThinedAreaValue(Movement.moveUpLeft(pix,w,h,1),w,h);
		floatVector[7]=getThinedAreaValue(Movement.moveUp(pix,w,h,1),w,h);
		floatVector[8]=getThinedAreaValue(Movement.moveUpRight(pix,w,h,1),w,h);
		
		floatVector[9]=getThinedAreaValue(Movement.moveLeft(pix,w,h,3),w,h);
		floatVector[10]=getThinedAreaValue(Movement.moveLeft(pix,w,h,2),w,h);
		floatVector[11]=getThinedAreaValue(Movement.moveLeft(pix,w,h,1),w,h);
		floatVector[12]=getThinedAreaValue(pix,w,h);	
		floatVector[13]=getThinedAreaValue(Movement.moveRight(pix,w,h,1),w,h);
		floatVector[14]=getThinedAreaValue(Movement.moveRight(pix,w,h,2),w,h);
		floatVector[15]=getThinedAreaValue(Movement.moveRight(pix,w,h,3),w,h);
		
		floatVector[16]=getThinedAreaValue(Movement.moveDownLeft(pix,w,h,1),w,h);
		floatVector[17]=getThinedAreaValue(Movement.moveDown(pix,w,h,1),w,h);
		floatVector[18]=getThinedAreaValue(Movement.moveDownRight(pix,w,h,1),w,h);
		
		floatVector[19]=getThinedAreaValue(Movement.moveDownLeft(pix,w,h,2),w,h);
		floatVector[20]=getThinedAreaValue(Movement.moveDown(pix,w,h,2),w,h);
		floatVector[21]=getThinedAreaValue(Movement.moveDownRight(pix,w,h,2),w,h);
		
		floatVector[22]=getThinedAreaValue(Movement.moveDownLeft(pix,w,h,3),w,h);
		floatVector[23]=getThinedAreaValue(Movement.moveDown(pix,w,h,3),w,h);
		floatVector[24]=getThinedAreaValue(Movement.moveDownRight(pix,w,h,3),w,h);
		
		return floatVector;
	}
	
	static public int[][] getBigCrossFloatAreaCode(int[] pix,int w,int h){
		int[][] floatVector=new int[17][];
		
		floatVector[0]=getThinedAreaValue(Movement.moveUp(pix,w,h,4),w,h);
		floatVector[1]=getThinedAreaValue(Movement.moveUp(pix,w,h,3),w,h);
		floatVector[2]=getThinedAreaValue(Movement.moveUp(pix,w,h,2),w,h);
		floatVector[3]=getThinedAreaValue(Movement.moveUp(pix,w,h,1),w,h);
		
		floatVector[4]=getThinedAreaValue(Movement.moveLeft(pix,w,h,4),w,h);
		floatVector[5]=getThinedAreaValue(Movement.moveLeft(pix,w,h,3),w,h);
		floatVector[6]=getThinedAreaValue(Movement.moveLeft(pix,w,h,2),w,h);
		floatVector[7]=getThinedAreaValue(Movement.moveLeft(pix,w,h,1),w,h);
		floatVector[8]=getThinedAreaValue(pix,w,h);
		floatVector[9]=getThinedAreaValue(Movement.moveRight(pix,w,h,1),w,h);
		floatVector[10]=getThinedAreaValue(Movement.moveRight(pix,w,h,2),w,h);
		floatVector[11]=getThinedAreaValue(Movement.moveRight(pix,w,h,3),w,h);
		floatVector[12]=getThinedAreaValue(Movement.moveRight(pix,w,h,4),w,h);
		
		floatVector[13]=getThinedAreaValue(Movement.moveDown(pix,w,h,1),w,h);
		floatVector[14]=getThinedAreaValue(Movement.moveDown(pix,w,h,2),w,h);
		floatVector[15]=getThinedAreaValue(Movement.moveDown(pix,w,h,3),w,h);
		floatVector[16]=getThinedAreaValue(Movement.moveDown(pix,w,h,4),w,h);
		
		return floatVector;
	}
}
