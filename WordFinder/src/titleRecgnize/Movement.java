package titleRecgnize;

/*
 * �����װ�˽���λ�Ʋ����ĸ�������
 */

public class Movement {
	static public int[] moveUp(int[] pix,int w,int h,int offset){      //����offset������
		int[] temp=new int[w*h];
		for(int i=0;i<w*h;i++)
			temp[i]=0xff000000;
		
		for(int i=offset;i<h;i++)
			for(int j=0;j<w;j++)
				if(pix[i*w+j]==0xffffffff)
					temp[(i-offset)*w+j]=0xffffffff;
		
		return temp;
	}
	
	static public int[] moveDown(int[] pix,int w,int h,int offset){     //����offset������
		int[] temp=new int[w*h];
		for(int i=0;i<w*h;i++)
			temp[i]=0xff000000;
		
		for(int i=0;i<h-offset;i++)
			for(int j=2;j<w-2;j++)
				if(pix[i*w+j]==0xffffffff)
					temp[(i+offset)*w+j]=0xffffffff;
		
		return temp;
	}
	
	static public int[] moveLeft(int[] pix,int w,int h,int offset){      //����offset������
		int[] temp=new int[w*h];
		for(int i=0;i<w*h;i++)
			temp[i]=0xff000000;
		
		for(int i=0;i<h;i++)
			for(int j=offset;j<w;j++)
				if(pix[i*w+j]==0xffffffff)
					temp[i*w+j-offset]=0xffffffff;
		
		return temp;
	}
	
	static public int[] moveRight(int[] pix,int w,int h,int offset){      //����offset������
		int[] temp=new int[w*h];
		for(int i=0;i<w*h;i++)
			temp[i]=0xff000000;
		
		for(int i=0;i<h;i++)
			for(int j=0;j<w-offset;j++)
				if(pix[i*w+j]==0xffffffff)
					temp[i*w+j+offset]=0xffffffff;
		
		return temp;
	}
	
	static public int[] moveUpLeft(int[] pix,int w,int h,int offset){      //������offset������
		int[] temp=new int[w*h];
		for(int i=0;i<w*h;i++)
			temp[i]=0xff000000;
		
		for(int i=offset;i<h;i++)
			for(int j=offset;j<w;j++)
				if(pix[i*w+j]==0xffffffff)
					temp[(i-offset)*w+j-offset]=0xffffffff;
		
		return temp;
	}
	
	static public int[] moveUpRight(int[] pix,int w,int h,int offset){      //������offset������
		int[] temp=new int[w*h];
		for(int i=0;i<w*h;i++)
			temp[i]=0xff000000;
		
		for(int i=offset;i<h;i++)
			for(int j=0;j<w-offset;j++)
				if(pix[i*w+j]==0xffffffff)
					temp[(i-offset)*w+j+offset]=0xffffffff;
		
		return temp;
	}
	
	static public int[] moveDownLeft(int[] pix,int w,int h,int offset){       //������offset������
		int[] temp=new int[w*h];
		for(int i=0;i<w*h;i++)
			temp[i]=0xff000000;
		
		for(int i=0;i<h-offset;i++)
			for(int j=offset;j<w;j++)
				if(pix[i*w+j]==0xffffffff)
					temp[(i+offset)*w+j-offset]=0xffffffff;
		
		return temp;
	}
	
	static public int[] moveDownRight(int[] pix,int w,int h,int offset){      //������offset������
		int[] temp=new int[w*h];
		for(int i=0;i<w*h;i++)
			temp[i]=0xff000000;
		
		for(int i=0;i<h-offset;i++)
			for(int j=0;j<w-offset;j++)
				if(pix[i*w+j]==0xffffffff)
					temp[(i+offset)*w+j+offset]=0xffffffff;
		
		return temp;
	}
}
