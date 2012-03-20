package titleRecgnize;
/*
 关于Image的操作
 */
import java.awt.*;
import java.awt.image.*;

import javax.imageio.*;

import java.util.*;
import java.io.*;

public class OperateImage {

	private int CharH,CharW,TitleH,TitleW,Width,Height;
	private final int CharScaleW = 80, CharScaleH = 80;
	private int[] originalImgPixels,grayImgPixels,binImgPixels,grayTitleImgPixels,binTitleImgPixels;
	private Image originalImg,grayImg,binImg,grayTitleImg,binTitleImg;
	private LinkedList<Image> CharImageList,CharScaledImageList,CharThinImageList;
	private HashMap<Integer, Double> tan;
	
	private ColorModel cm;
	private ImageBinaryzation ib;
	private ImagePageAnalysis ipa;
	
	public OperateImage() {
		cm=ColorModel.getRGBdefault();
		ib = new ImageBinaryzation(cm);
		ipa = new ImagePageAnalysis();
		
		CharImageList = new LinkedList<Image>();
		CharScaledImageList = new LinkedList<Image>();
		CharThinImageList = new LinkedList<Image>();
		
		// LookUpTable
		//用于Hough变换的查找表
		tan = new HashMap<Integer, Double>();
		for (int i = -20; i <= 20; i++)
			tan.put(i, Math.tan((Math.PI/180.0)*i));
	}
	
	public int getCharH() {
		return CharH;
	}

	public int getCharW() {
		return CharW;
	}

	public int getWidth() {
		return Width;
	}

	public int getHeight() {
		return Height;
	}

	public int[] getOriginalImgPixels() {
		return originalImgPixels;
	}

	public int[] getGrayImgPixels() {
		return grayImgPixels;
	}

	public void setGrayImgPixels(int[] grayImgPixels) {
		this.grayImgPixels = grayImgPixels;
	}

	public int[] getBinImgPixels() {
		return binImgPixels;
	}

	public int[] getBinTitleImgPixels() {
		return binTitleImgPixels;
	}

	public Image getOriginalImg() {
		return originalImg;
	}

	public Image getGrayImg() {
		return grayImg;
	}
	
	public Image getBinImg() {
		return binImg;
	}

	public Image getGrayTitleImg() {
		return grayTitleImg;
	}

	public Image getBinTitleImg() {
		return binTitleImg;
	}

	public LinkedList<Image> getCharImageList() {
		return CharImageList;
	}
	
	public LinkedList<Image> getCharScaledImageList() {
		return CharScaledImageList;
	}

	public LinkedList<Image> getCharThinImageList() {
		return CharThinImageList;
	}
	
	public Image getImg(String path){//取图片
		File file=new File(path);
		
		if(!file.exists()){
			System.out.println(path + "不存在~！");
			return null;
		}
		
		FileInputStream is = null;
		try{
			is=new FileInputStream(file);
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		BufferedImage bi = null;
		try{
			bi=ImageIO.read(is);
		}catch(IOException e){
			e.printStackTrace();
		}
		if(bi.getWidth()!=-1 && bi.getHeight()!=-1)
			return bi;
		else
			return null;
	}
	
	public int[] takeImg(Image image,int w,int h)//从图片转为数组
	{
		int[] pix;
		pix=new int[w*h];
		//抓取像素放到数组中
		try{
			PixelGrabber pg=new PixelGrabber(image,0,0,w,h,pix,0,w);
			pg.grabPixels();
		}
		catch(InterruptedException e)
		{
			System.out.println(e.toString());
		}
		return pix;
	}
	
	public int[] filter(int[] pix){//为了解决读入的二值图片含有噪声点的问题，特设此过滤函数
		int r,g,b,length;
		int[] temp;
		length=pix.length;
		temp=new int[length];
		ColorModel cm=ColorModel.getRGBdefault();
		for(int i=0;i<length;i++){
			r=cm.getRed(pix[i]);
			g=cm.getGreen(pix[i]);
			b=cm.getBlue(pix[i]);
			if(r>=200 && g>=200 && b>=200)
				temp[i]=0xffffffff;
			else
				temp[i]=0xff000000;
		}
		return temp;
	}
	
	public void print(int[] pix,int w,int h){//输出像素信息检查
		for(int i=0;i<h;i++){
			for(int j=0;j<w;j++)
				if(pix[i*w+j]==0xffffffff)
					System.out.print("1"+" ");
				else if(pix[i*w+j]==0xff000000)
					System.out.print("0"+" ");
				else
					System.out.print("?"+" ");
			System.out.println();
		}
	}
	
	public Image madeImg(int[] pix,int w,int h)//从数组转为图片
	{
		Image pic;
		ImageProducer imgpd=new MemoryImageSource(w,h,pix,0,w);
		pic=Toolkit.getDefaultToolkit().createImage(imgpd);
		/*
		MediaTracker tracker=new MediaTracker(null);
		tracker.addImage(pic, 1);
		try{
			tracker.waitForID(1);
		}
		catch(InterruptedException e)
		{
			System.out.println(e.toString());
		}
		*/
		return pic;
	}
	
	//Hough变换找直线
	public boolean lineDetect(int[] pixels, int width, int height){
		int i, j = 0;
		int[][] count = new int[41][height];
		int k, x, y;
		double b = 0;
		
		for (k = 0; k <= 40; k++) {
			for (i = 0; i < width * height; i++) {
				if (pixels[i] == 0xffffffff) {//是白点
					x = i % width;
					y = i / width;
					y = height - y;//以左下角为原点 
					b = y - tan.get(k - 20) * x;
					b = b % 1 > 0.5 ? b / 1 + 1 : b / 1;//四舍五入
					if (b < height && b >= 0)
						count[k][(int) b]++;
				}
			}
		}
		
		int max = 0;
		for (i = 0; i <= 40; i++)
			for (j = 0; j < height; j++)
				if (count[i][j] >= max) {
					max = count[i][j];//找到直线y = k*x+b
					k = i;
					b = j;
				}
		
		//System.out.println("在" + (k-20) + "," + b + "上有" + max + "个白点！");

		if (k > 18 && k < 22 && max > width/3)//斜率在-3°到3°之间，且长度至少为图像宽的1/3
			return true;
		else
			return false;
	}
	
	//分割标题中的各个字符
	private void getChar() {
		int[] row,charTemp;
		int a,b,i,j,leftBound,rightBound,ch,charMaxWidth=0;
		Image charImg;
		CharImageList.clear();
		row=new int[TitleW];
		for(i=0;i<TitleH;i++)//行投影
			for(j=0;j<TitleW;j++)
				if(binTitleImgPixels[i*TitleW+j]==0xffffffff)
					row[j]++;
		rightBound=leftBound=-1;
		for(i=0;i<TitleW;i++)//先统计字符的最大宽度
		{
			if(leftBound==-1 && row[i]!=0)
				leftBound=i;
			else if((leftBound!=-1 && i==TitleW-1)  //到达标题右边界
					||                              //或者
					(leftBound!=-1 && row[i]<3))    //到达一个字符的右边界
			{
				rightBound=i-1;
				ch=rightBound-leftBound+1;//计算字符宽
				if(ch>charMaxWidth)
					charMaxWidth = ch;
				leftBound=-1;
			}
		}
		
		if(rightBound == -1){//整个横投影没有空隙
			CharImageList.add(binTitleImg);
		}
		
		CharH = TitleH;
		rightBound=leftBound=-1;
		int upper = 0,bottom = 0,left=0,right=0,CharHTemp,CharWTemp;
		int[] rowTemp,volumnTemp,charPix;
		for(i=0;i<TitleW;i++)
		{	
			if(leftBound==-1 && row[i]!=0)
				leftBound=i;
			else if(leftBound!=-1 && 
					((row[i]==0 && i-leftBound+1 >= charMaxWidth) ||
							i==TitleW-1))
			{
				if(i-leftBound > charMaxWidth*1.75) {
					ch = Integer.MAX_VALUE;
					for(j=leftBound;j<i;j++)
						if(row[j] < ch)
							ch = row[j];
					for(j=i-5;j>leftBound;j--)
						if(row[j] == ch) {
							rightBound = j;
							break;
						}
					i = rightBound-2;
				}
				else 
					rightBound = i-1;
					
				CharW=rightBound-leftBound+1;//计算字符宽
				charTemp=new int[CharW*CharH];
				for(a=0;a<CharH;a++)
					for(b=0;b<CharW;b++)
						charTemp[a*CharW+b]=binTitleImgPixels[a*TitleW+b+leftBound];
				//同一标题中的字符高度和宽度可能不一样，所以重新投影
				rowTemp=new int[CharH];
				for(a=0;a<CharH;a++)
					for(b=0;b<CharW;b++)
						if(charTemp[a*CharW+b]==0xffffffff)
							rowTemp[a]++;
				for(a=0;a<CharH;a++)
					if(rowTemp[a]!=0){
						upper=a;
						break;
					}
				for(a=CharH-1;a>-1;a--)
					if(rowTemp[a]!=0){
						bottom=a;
						break;
					}
				CharHTemp=bottom-upper+1;//新的字符高度
				volumnTemp=new int[CharW];
				for(a=0;a<CharH;a++)
					for(b=0;b<CharW;b++)
						if(charTemp[a*CharW+b]==0xffffffff)
							volumnTemp[b]++;
				for(a=0;a<CharW;a++)
					if(volumnTemp[a]!=0){
						left=a;
						break;
					}
				for(a=CharW-1;a>-1;a--)
					if(volumnTemp[a]!=0){
						right=a;
						break;
					}
				CharWTemp=right-left+1;//新的字符宽度
				charPix=new int[CharWTemp*CharHTemp];
				for(a=0;a<CharHTemp;a++)
					for(b=0;b<CharWTemp;b++)
						charPix[a*CharWTemp+b]=charTemp[(a+upper)*CharW+b+left];
				
				charImg=madeImg(charPix,CharWTemp,CharHTemp);
				CharImageList.add(charImg);
				leftBound=-1;
			}	
		}	
		
	}
	
	private void charScaling() {
		Image scaledCharImage,filtedImage;
		int[] pixTemp;
		CharScaledImageList.clear();
		for(int i=0;i<CharImageList.size();i++){
			scaledCharImage=ImageScale.doubleLinearScale(CharImageList.get(i),CharScaleW,CharScaleH);//缩放为60x60
			pixTemp=filter(takeImg(scaledCharImage,CharScaleW,CharScaleH));

			//最外层的一圈置为黑像素
			for(int j=0;j<CharScaleW;j++)
				pixTemp[0*CharScaleW+j]=0xff000000;
			for(int j=0;j<CharScaleH;j++)
				pixTemp[j*CharScaleW+0]=0xff000000;
			for(int j=0;j<CharScaleW;j++)
				pixTemp[j*CharScaleW+(CharScaleW-1)]=0xff000000;
			for(int j=0;j<CharScaleH;j++)
				pixTemp[(CharScaleW-1)*CharScaleW+j]=0xff000000;

			//重定位
			pixTemp=positionToCenter(pixTemp,CharScaleW,CharScaleH);
			
			//横画加粗
			for(int a=1;a<CharScaleH-1;a++)
				for(int b=1;b<CharScaleW-1;b++){
					if(pixTemp[a*CharScaleW+b]==0xffffffff 
							&& pixTemp[(a-1)*CharScaleW+b]==0xff000000 
							&& pixTemp[(a+1)*CharScaleW+b]==0xff000000 
							&& a+2==CharScaleH)//是最下面一行的话，上两行变白
					{
						pixTemp[(a-1)*CharScaleW+b]=0xffffffff;
						pixTemp[(a-2)*CharScaleW+b]=0xffffffff;
					}
					if(pixTemp[a*CharScaleW+b]==0xffffffff 
							&& pixTemp[(a-1)*CharScaleW+b]==0xff000000 
							&& pixTemp[(a+1)*CharScaleW+b]==0xff000000 
							&& a+2!=CharScaleH)//不是最下面一行的话，上下行变白
					{
						pixTemp[(a-1)*CharScaleW+b]=0xffffffff;
						pixTemp[(a+1)*CharScaleW+b]=0xffffffff;
					}
				}

			filtedImage=madeImg(pixTemp,CharScaleW,CharScaleH);
			CharScaledImageList.add(filtedImage);
		}
	}
	
	private void charThinning() {
		Image imgTemp;
		int[] pixTemp;
		CharThinImageList.clear();
		for(int i=0;i<CharScaledImageList.size();i++){
			imgTemp=CharScaledImageList.get(i);
			pixTemp=filter(takeImg(imgTemp, CharScaleW, CharScaleH));
			pixTemp=ImageThinning.Thinning(pixTemp, CharScaleW, CharScaleH);
			//重定位
			pixTemp=positionToCenter(pixTemp, CharScaleW, CharScaleH);	
			
			imgTemp=madeImg(pixTemp, CharScaleW, CharScaleH);
			CharThinImageList.add(imgTemp);
		}
	}
	
	//把字符重定位到图片中央位置
	public int[] positionToCenter(int[] pix,int w,int h){
		int[] handled=new int[w*h];            //重定位后的图片像素
		int[] row=new int[w];
		int[] volumn=new int[h];
		int horizonOffset=0,verticalOffset=0;
		int upperBound,bottomBound,leftBound,rightBound,horizonDis,verticalDis;
		for(int i=0;i<h;i++)
			for(int j=0;j<w;j++)
				handled[i*w+j]=0xff000000;        //先全部置为黑色
		
		for(int i=0;i<h;i++)
			for(int j=0;j<w;j++)
				if(pix[i*w+j]!=0xff000000){
					volumn[i]++;                         //列投影
					row[j]++;                            //行投影	
				}
		leftBound=0;
		rightBound=0;
		horizonDis=0;
		for(int i=0;i<w;i++)
			if(row[i]!=0){
				leftBound=i;
				horizonDis=i-1;//水平左补白
				break;
			}
		for(int i=w-1;i>-1;i--)
			if(row[i]!=0){
				rightBound=i;
				break;
			}	
		horizonOffset=(w/2-1)-((rightBound-leftBound+1)/2+horizonDis);//水平偏移量
		upperBound=0;
		bottomBound=0;
		verticalDis=0;
		for(int i=0;i<h;i++)
			if(volumn[i]!=0){
				upperBound=i;
				verticalDis=i-1;//垂直上补白
				break;
			}
		for(int i=h-1;i>-1;i--)
			if(volumn[i]!=0){
				bottomBound=i;
				break;
			}
		verticalOffset=(h/2-1)-((bottomBound-upperBound+1)/2+verticalDis);//垂直偏移量
		//System.out.println("水平位移："+horizonOffset+"  垂直位移："+verticalOffset);
		
		//水平和垂直移动
		for(int i=0;i<h;i++)
			for(int j=0;j<w;j++)
				if(pix[i*w+j]!=0xff000000)
					handled[(i+verticalOffset)*w+j+horizonOffset]=0xffffffff;

		//最外面一圈置为黑像素
		for(int j=0;j<w;j++)
			handled[0*w+j]=0xff000000;
		for(int j=0;j<h;j++)
			handled[j*w+0]=0xff000000;
		for(int j=0;j<h;j++)
			handled[j*w+(w-1)]=0xff000000;
		for(int j=0;j<w;j++)
			handled[(h-1)*w+j]=0xff000000;
		
		return handled;
	}
	
	/**
	 * 预处理，分辨是否具有红头文件特征
	 * 从图片大小、颜色分布、上半部分有红线三个特征来判断
	 */
	private boolean preProcess(String path){
		int white = 0, red = 0, black = 0 ,R, G, B, size;
		
		if (path.toLowerCase().endsWith(".jpg")
				|| path.toLowerCase().endsWith(".png")
				|| path.toLowerCase().endsWith(".gif"))
			originalImg = getImg(path);
		else if (path.toLowerCase().endsWith(".bmp")) {
			BmpParse bp = new BmpParse(path);
			originalImg = bp.loadbitmap();
		}
		else return false;
		
		if (originalImg == null || originalImg.getWidth(null) == -1
				|| originalImg.getHeight(null) == -1) {
			System.out.println(path + "载入失败！");
			return false;
		}
		
		Width=originalImg.getWidth(null);//获得图像宽
		Height=originalImg.getHeight(null);//获得图像高
		
		if(Width <= 200 || Height <= 300){  //大小限制为200*300以上
			System.out.println(path + "大小不足200*300！");
			return false;
		}
		
		originalImgPixels = takeImg(originalImg,Width,Height);
		size = originalImgPixels.length;
		for(int i=0; i<size; i++){
			R=cm.getRed(originalImgPixels[i])& 0xff;
			G=cm.getGreen(originalImgPixels[i])& 0xff;
			B=cm.getBlue(originalImgPixels[i])& 0xff;
			if(R>=220 && G>=220 && B>=220)//白色
				white++;
			else if(R>=200 && G<=150 && B<=150)//红色
				red++;
			else if(R<=80 && G<=80 && B<=80)//黑色
				black++;
		}
		
		if(!((white/(size+0.0)) > 0.6
				&& (red/(size+0.0)) > 0.001
				&& (black/(size+0.0)) > 0.001)){//不满足红白黑颜色特征
			System.out.println(path + "不满足红白黑颜色特征！");
			return false;
		}
		
		grayImgPixels = ib.transGray(originalImgPixels,Width,Height);
		binImgPixels = ib.otsuBinaryzation(grayImgPixels,Width,Height);
		grayImg = madeImg(grayImgPixels,Width,Height);
		binImg = madeImg(binImgPixels,Width,Height);
		
		if(!lineDetect(binImgPixels, Width, Height/3)){
			System.out.println("不满足红线特征！");
			return false;
		}
		
		return true;
	}
	
	public boolean operation(String path){
		if(!preProcess(path))//没有通过预处理
			return false;
		
		//从原先的灰度图像中取出标题部分的像素
		Component comp = ipa.getTitleComponent(binImgPixels,Width,Height);
		if(comp == null)
			return false;
		int n = 0;
		TitleW = comp.getRight()-comp.getLeft()+1+2;
		TitleH = comp.getBottom()-comp.getTop()+1;
		grayTitleImgPixels = new int[TitleW * TitleH];
		for(int j=comp.getTop();j<=comp.getBottom();j++)
			for(int i=comp.getLeft()-1;i<=comp.getRight()+1;i++)
				grayTitleImgPixels[n++] = grayImgPixels[j*Width+i]; 
		grayTitleImg = madeImg(grayTitleImgPixels, TitleW, TitleH);
		
		//对标题部分二值化
		binTitleImgPixels = ib.otsuBinaryzation(grayTitleImgPixels, TitleW, TitleH);
		binTitleImg = madeImg(binTitleImgPixels, TitleW, TitleH);
		
		getChar();
		charScaling();
		charThinning();
		return true;
	}
	
}
