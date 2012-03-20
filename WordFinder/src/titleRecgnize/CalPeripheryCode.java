package titleRecgnize;
/*
 * ������Χ����ֵ
 * 4���ߣ�ÿ�߷�8�У�ÿ��2��ֵ������4 x 8 x 2 = 64������ֵ
 */
public class CalPeripheryCode {
	static public int[] getPeripheryValue(int[] pix,int w,int h){//ȡϸ���ַ�����Χ����ֵ
		int[] code=new int[64];
		int partion,row,column,first,second;
		
		//�ϱ߽�
		partion=w/8;//��8��
		for(int i=0;i<8;i++){
			column=partion/2+i*partion;      //Ҫɨ��������꣬�ڼ���
			first=0;
			second=0;
			for(int a=0;a<h;a++){
				if(pix[a*w+column]==0xffffffff)
					if(first==0 && second==0)
						first=a;
					else if(first!=0 && second==0)
						second=a;
			}
			code[i*2]=first;
			code[i*2+1]=second;
		}
		
		//��߽�
		partion=h/8;//��8��
		for(int i=0;i<8;i++){
			row=partion/2+i*partion;      //Ҫɨ��������꣬�ڼ���
			first=0;
			second=0;
			for(int a=0;a<w;a++){
				if(pix[row*w+a]==0xffffffff)
					if(first==0 && second==0)
						first=a;
					else if(first!=0 && second==0)
						second=a;
			}
			code[16+i*2]=first;
			code[16+i*2+1]=second;
		}
		
		//�±߽�
		partion=w/8;//��8��
		for(int i=0;i<8;i++){
			column=partion/2+i*partion;      //Ҫɨ��������꣬�ڼ���
			first=0;
			second=0;
			for(int a=0;a<h;a++){
				if(pix[(h-1-a)*w+column]==0xffffffff)
					if(first==0 && second==0)
						first=a;
					else if(first!=0 && second==0)
						second=a;
			}
			code[32+i*2]=first;
			code[32+i*2+1]=second;
		}
		
		//�ұ߽�
		partion=h/8;//��8��
		for(int i=0;i<8;i++){
			row=partion/2+i*partion;      //Ҫɨ��������꣬�ڼ���
			first=0;
			second=0;
			for(int a=0;a<w;a++){
				if(pix[row*w+(w-1-a)]==0xffffffff)
					if(first==0 && second==0)
						first=a;
					else if(first!=0 && second==0)
						second=a;
			}
			code[48+i*2]=first;
			code[48+i*2+1]=second;
		}
		
		return code;
	}
	
	/*
	 * �������ܣ�ȡ��һ���ַ���3x3��Χ�ڵĸ�����Χ����ֵ
	 * ������pix�����ַ�����������
	 *      w�����ַ���
	 *      h�����ַ���
	 * ���أ���������ֵ����
	 */
	static public int[][] getFloatPeripheryCode9(int[] pix,int w,int h){
		int[][] floatVector=new int[9][];
		
		floatVector[0]=getPeripheryValue(Movement.moveUpLeft(pix,w,h,1),w,h);
		floatVector[1]=getPeripheryValue(Movement.moveUp(pix,w,h,1),w,h);
		floatVector[2]=getPeripheryValue(Movement.moveUpRight(pix,w,h,1),w,h);
		floatVector[3]=getPeripheryValue(Movement.moveLeft(pix,w,h,1),w,h);
		
		floatVector[4]=getPeripheryValue(pix,w,h);
		
		floatVector[5]=getPeripheryValue(Movement.moveRight(pix,w,h,1),w,h);
		floatVector[6]=getPeripheryValue(Movement.moveDownLeft(pix,w,h,1),w,h);
		floatVector[7]=getPeripheryValue(Movement.moveDown(pix,w,h,1),w,h);
		floatVector[8]=getPeripheryValue(Movement.moveDownRight(pix,w,h,1),w,h);
		
		return floatVector;
	}

	/*
	 * �������ܣ�ȡ��һ���ַ��������ͷ�Χ�ڵĸ�������ֵ
	 * ������pix�����ַ�����������
	 *      w�����ַ���
	 *      h�����ַ���
	 * ���أ���������ֵ����
	 */
	static public int[][] getMiFloatPeripheryCode(int[] pix,int w,int h){
		int[][] floatVector=new int[17][];
		
		floatVector[0]=getPeripheryValue(Movement.moveUpLeft(pix,w,h,2),w,h);
		floatVector[1]=getPeripheryValue(Movement.moveUp(pix,w,h,2),w,h);
		floatVector[2]=getPeripheryValue(Movement.moveUpRight(pix,w,h,2),w,h);
		
		floatVector[3]=getPeripheryValue(Movement.moveUpLeft(pix,w,h,1),w,h);
		floatVector[4]=getPeripheryValue(Movement.moveUp(pix,w,h,1),w,h);
		floatVector[5]=getPeripheryValue(Movement.moveUpRight(pix,w,h,1),w,h);
		
		floatVector[6]=getPeripheryValue(Movement.moveLeft(pix,w,h,2),w,h);
		floatVector[7]=getPeripheryValue(Movement.moveLeft(pix,w,h,1),w,h);
		floatVector[8]=getPeripheryValue(pix,w,h);	
		floatVector[9]=getPeripheryValue(Movement.moveRight(pix,w,h,1),w,h);
		floatVector[10]=getPeripheryValue(Movement.moveRight(pix,w,h,2),w,h);
		
		floatVector[11]=getPeripheryValue(Movement.moveDownLeft(pix,w,h,1),w,h);
		floatVector[12]=getPeripheryValue(Movement.moveDown(pix,w,h,1),w,h);
		floatVector[13]=getPeripheryValue(Movement.moveDownRight(pix,w,h,1),w,h);
		
		floatVector[14]=getPeripheryValue(Movement.moveDownLeft(pix,w,h,1),w,h);
		floatVector[15]=getPeripheryValue(Movement.moveDown(pix,w,h,1),w,h);
		floatVector[16]=getPeripheryValue(Movement.moveDownRight(pix,w,h,1),w,h);
		
		return floatVector;
	}
	
	/*
	 * �������ܣ�ȡ��һ���ַ���ʮ���η�Χ�ڵĸ�������ֵ
	 * ������pix�����ַ�����������
	 *      w�����ַ���
	 *      h�����ַ���
	 * ���أ���������ֵ����
	 */
	static public int[][] getCrossFloatPeripheryCode(int[] pix,int w,int h){
		int[][] floatVector=new int[9][];
		
		floatVector[0]=getPeripheryValue(Movement.moveUp(pix,w,h,2),w,h);
		floatVector[1]=getPeripheryValue(Movement.moveUp(pix,w,h,1),w,h);
		
		floatVector[2]=getPeripheryValue(Movement.moveLeft(pix,w,h,2),w,h);
		floatVector[3]=getPeripheryValue(Movement.moveLeft(pix,w,h,1),w,h);
		floatVector[4]=getPeripheryValue(pix,w,h);
		floatVector[5]=getPeripheryValue(Movement.moveRight(pix,w,h,1),w,h);
		floatVector[6]=getPeripheryValue(Movement.moveRight(pix,w,h,2),w,h);
		
		floatVector[7]=getPeripheryValue(Movement.moveDown(pix,w,h,1),w,h);
		floatVector[8]=getPeripheryValue(Movement.moveDown(pix,w,h,2),w,h);
		
		return floatVector;
	}
	
	static public int[][] getBigMiFloatPeripheryCode(int[] pix,int w,int h){
		int[][] floatVector=new int[25][];
		
		floatVector[0]=getPeripheryValue(Movement.moveUpLeft(pix,w,h,3),w,h);
		floatVector[1]=getPeripheryValue(Movement.moveUp(pix,w,h,3),w,h);
		floatVector[2]=getPeripheryValue(Movement.moveUpRight(pix,w,h,3),w,h);
		
		floatVector[3]=getPeripheryValue(Movement.moveUpLeft(pix,w,h,2),w,h);
		floatVector[4]=getPeripheryValue(Movement.moveUp(pix,w,h,2),w,h);
		floatVector[5]=getPeripheryValue(Movement.moveUpRight(pix,w,h,2),w,h);
		
		floatVector[6]=getPeripheryValue(Movement.moveUpLeft(pix,w,h,1),w,h);
		floatVector[7]=getPeripheryValue(Movement.moveUp(pix,w,h,1),w,h);
		floatVector[8]=getPeripheryValue(Movement.moveUpRight(pix,w,h,1),w,h);
		
		floatVector[9]=getPeripheryValue(Movement.moveLeft(pix,w,h,3),w,h);
		floatVector[10]=getPeripheryValue(Movement.moveLeft(pix,w,h,2),w,h);
		floatVector[11]=getPeripheryValue(Movement.moveLeft(pix,w,h,1),w,h);
		floatVector[12]=getPeripheryValue(pix,w,h);	
		floatVector[13]=getPeripheryValue(Movement.moveRight(pix,w,h,1),w,h);
		floatVector[14]=getPeripheryValue(Movement.moveRight(pix,w,h,2),w,h);
		floatVector[15]=getPeripheryValue(Movement.moveRight(pix,w,h,3),w,h);
		
		floatVector[16]=getPeripheryValue(Movement.moveDownLeft(pix,w,h,1),w,h);
		floatVector[17]=getPeripheryValue(Movement.moveDown(pix,w,h,1),w,h);
		floatVector[18]=getPeripheryValue(Movement.moveDownRight(pix,w,h,1),w,h);
		
		floatVector[19]=getPeripheryValue(Movement.moveDownLeft(pix,w,h,2),w,h);
		floatVector[20]=getPeripheryValue(Movement.moveDown(pix,w,h,2),w,h);
		floatVector[21]=getPeripheryValue(Movement.moveDownRight(pix,w,h,2),w,h);
		
		floatVector[22]=getPeripheryValue(Movement.moveDownLeft(pix,w,h,3),w,h);
		floatVector[23]=getPeripheryValue(Movement.moveDown(pix,w,h,3),w,h);
		floatVector[24]=getPeripheryValue(Movement.moveDownRight(pix,w,h,3),w,h);
		
		return floatVector;
	}
	
	static public int[][] getBigCrossFloatPeripheryCode(int[] pix,int w,int h){
		int[][] floatVector=new int[17][];
		
		floatVector[0]=getPeripheryValue(Movement.moveUp(pix,w,h,4),w,h);
		floatVector[1]=getPeripheryValue(Movement.moveUp(pix,w,h,3),w,h);
		floatVector[2]=getPeripheryValue(Movement.moveUp(pix,w,h,2),w,h);
		floatVector[3]=getPeripheryValue(Movement.moveUp(pix,w,h,1),w,h);
		
		floatVector[4]=getPeripheryValue(Movement.moveLeft(pix,w,h,4),w,h);
		floatVector[5]=getPeripheryValue(Movement.moveLeft(pix,w,h,3),w,h);
		floatVector[6]=getPeripheryValue(Movement.moveLeft(pix,w,h,2),w,h);
		floatVector[7]=getPeripheryValue(Movement.moveLeft(pix,w,h,1),w,h);
		floatVector[8]=getPeripheryValue(pix,w,h);
		floatVector[9]=getPeripheryValue(Movement.moveRight(pix,w,h,1),w,h);
		floatVector[10]=getPeripheryValue(Movement.moveRight(pix,w,h,2),w,h);
		floatVector[11]=getPeripheryValue(Movement.moveRight(pix,w,h,3),w,h);
		floatVector[12]=getPeripheryValue(Movement.moveRight(pix,w,h,4),w,h);
		
		floatVector[13]=getPeripheryValue(Movement.moveDown(pix,w,h,1),w,h);
		floatVector[14]=getPeripheryValue(Movement.moveDown(pix,w,h,2),w,h);
		floatVector[15]=getPeripheryValue(Movement.moveDown(pix,w,h,3),w,h);
		floatVector[16]=getPeripheryValue(Movement.moveDown(pix,w,h,4),w,h);
		
		return floatVector;
	}
}
