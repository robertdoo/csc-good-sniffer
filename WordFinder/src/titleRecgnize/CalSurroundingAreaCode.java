package titleRecgnize;
/*
 * ���������������
 * ���µĿ��Ϊ10�����ҵĿ��Ϊ15
 */

public class CalSurroundingAreaCode {

	static public int[] getThinedAreaValue(int[] pix,int w,int h){//ȡϸ���ֿ�ı�Ե����ֵ
		int[] code=new int[4];
		int white;
		//��
		white=0;
		for(int i=0;i<10;i++)
			for(int j=0;j<w;j++)
				if(pix[i*w+j]==0xffffffff)
					white++;
		code[0]=white;
		//��
		white=0;
		for(int i=0;i<h;i++)
			for(int j=0;j<15;j++)
				if(pix[i*w+j]==0xffffffff)
					white++;
		code[1]=white;
		//��
		white=0;
		for(int i=70;i<h;i++)
			for(int j=0;j<w;j++)
				if(pix[i*w+j]==0xffffffff)
					white++;
		code[2]=white;		
		//��
		white=0;
		for(int i=0;i<h;i++)
			for(int j=65;j<w;j++)
				if(pix[i*w+j]==0xffffffff)
					white++;
		code[3]=white;
		return code;
	}
	
	/*
	 * �������ܣ�ȡ��һ���ַ���3x3��Χ�ڵĸ�������ֵ
	 * ������pix�����ַ�����������
	 *      w�����ַ���
	 *      h�����ַ���
	 * ���أ���������ֵ����
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
	 * �������ܣ�ȡ��һ���ַ��������ͷ�Χ�ڵĸ�������ֵ
	 * ������pix�����ַ�����������
	 *      w�����ַ���
	 *      h�����ַ���
	 * ���أ���������ֵ����
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
	 * �������ܣ�ȡ��һ���ַ���ʮ���η�Χ�ڵĸ�������ֵ
	 * ������pix�����ַ�����������
	 *      w�����ַ���
	 *      h�����ַ���
	 * ���أ���������ֵ����
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
