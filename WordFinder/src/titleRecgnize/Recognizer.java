package titleRecgnize;

/*
 * 识别器
 */

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class Recognizer {
	
	private WordCode[] sacode,percode,percodetemp,gridcode,gridcodetemp;//字库的边缘特征值和网格特征值
	private OperateImage OptImg;
	private LinkedList<Distance> SACdistance,PCdistance,GCdistance;//边缘特征值距离数组   网格特征值距离数组
	private int width,height;
	
	private LinkedList<String> front10wordsList;
	
	class disComparator implements Comparator<Distance>{
		public int compare(Distance d1, Distance d2){
			if(d1.getDitance() > d2.getDitance())
				return 1;
			else if(d1.getDitance() < d2.getDitance())
				return -1;
			else
				return 0;
		}
	}
	
	public LinkedList<Distance> getSACdistance() {
		return SACdistance;
	}

	public LinkedList<Distance> getPCdistance() {
		return PCdistance;
	}

	public LinkedList<Distance> getGCdistance() {
		return GCdistance;
	}

	public LinkedList<String> getFront10wordsList() {
		return front10wordsList;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public OperateImage getOptImg() {
		return OptImg;
	}

	public void setOptImg(OperateImage optImg) {
		OptImg = optImg;
	}

	public Recognizer() {
		SACdistance=new LinkedList<Distance>();
		PCdistance=new LinkedList<Distance>();
		GCdistance=new LinkedList<Distance>();
		OptImg=new OperateImage();
		
		front10wordsList = new LinkedList<String>();
		
		sacode=readInSACode("汉字特征值/边缘特征值.txt");                 //读入边缘特征值
		percode=readInPeripheryCode("汉字特征值/外围特征值.txt");          //读入外围特征值
		gridcode=readInGridCode("汉字特征值/64网格特征值.txt");           //读入网格特征值
	}


	/**
	 * 函数功能：从txt文件中读入字符的边缘特征值
	 * 参数：path——txt文件的路径
	 * 返回：初始化好的WordCode数组，一条WordCode记录代表一个字符及其特征值
	 */
	private WordCode[] readInSACode(String path){
		WordCode[] word=new WordCode[2500];
		String str,num,first;
		int count;
		int p,n;//p字符串读位置
		int[] temp;
		
		File file=new File(path);
		BufferedReader br = null;
		try{
			count=0;
			br=new BufferedReader(new FileReader(file));
			while(br.readLine()!=null)
				count++;
			br.close();
			br=new BufferedReader(new FileReader(file));
			for(int i=0;i<count;i++){
				temp=new int[4];
				str=br.readLine();
				first=str.charAt(0)+"";
				word[i]=new WordCode(4);
				word[i].setWord(first);
				p=2;
				n=0;
				while(p<str.length()){
					num="";
					while(str.charAt(p)!=' '){
						num+=str.charAt(p);
						p++;
					}
					if(str.charAt(p)==' '){
						temp[n]=Integer.valueOf(num);
						n++;
						p++;
					}
				}
				word[i].setEigenvalue(temp);
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return word;
	}
	
	/**
	 * 函数功能：从txt文件中读入字符的外围特征值，以哈希方式保存
	 * 参数：path——txt文件的路径
	 * 返回：初始化好的WordCode数组，一条WordCode记录代表一个字符及其特征值
	 */
	private WordCode[] readInPeripheryCode(String path){
		WordCode[] word=new WordCode[65600];
		String str,num,first;
		byte[] wordbyte;
		int count,hash;
		int p,n;//p字符串读位置
		int[] temp;
		
		File file=new File(path);
		BufferedReader br = null;
		try{
			count=0;
			br=new BufferedReader(new FileReader(file));
			while(br.readLine()!=null)
				count++;
			br.close();
			br=new BufferedReader(new FileReader(file));
			for(int i=0;i<count;i++){
				temp=new int[64];
				str=br.readLine();
				first=str.charAt(0)+"";
				wordbyte=first.getBytes();
				//为方便提取，采用哈希方式保存
				hash=(wordbyte[0]+256)*256+(wordbyte[1]+256);
				word[hash]=new WordCode(64);
				word[hash].setWord(first);
				p=2;
				n=0;
				while(p<str.length()){
					num="";
					while(str.charAt(p)!=' '){
						num+=str.charAt(p);
						p++;
					}
					if(str.charAt(p)==' '){
						temp[n]=Integer.valueOf(num);
						n++;
						p++;
					}
				}
				word[hash].setEigenvalue(temp);
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return word;
	}
	
	/**
	 * 函数功能：从txt文件中读入字符的网格特征值，以哈希方式保存
	 * 参数：path——txt文件的路径
	 * 返回：初始化好的WordCode数组，一条WordCode记录代表一个字符及其特征值
	 */
	private WordCode[] readInGridCode(String path){
		WordCode[] word=new WordCode[65600];
		String str,num,first;
		byte[] wordbyte;
		int count,hash;
		int p,n;//p字符串读位置
		int[] temp;
		
		File file=new File(path);
		BufferedReader br = null;
		try{
			count=0;
			br=new BufferedReader(new FileReader(file));
			while(br.readLine()!=null)
				count++;
			br.close();
			br=new BufferedReader(new FileReader(file));	
			for(int i=0;i<count;i++){
				temp=new int[64];
				str=br.readLine();
				first=str.charAt(0)+"";
				wordbyte=first.getBytes();
				//为方便提取，采用哈希方式保存
				hash=(wordbyte[0]+256)*256+(wordbyte[1]+256);
				word[hash]=new WordCode(64);
				word[hash].setWord(first);
				p=2;
				n=0;
				while(p<str.length()){
					num="";
					while(str.charAt(p)!=' '){
						num+=str.charAt(p);
						p++;
					}
					if(str.charAt(p)==' '){
						temp[n]=Integer.valueOf(num);
						n++;
						p++;
					}
				}
				word[hash].setEigenvalue(temp);
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return word;
	}
	
	/**
	 * 函数功能：计算一个字符的特征值与另一个字符的浮动特征值的最小距离
	 * 参数：vector1——要计算的字符特征值
	 *      vector2——浮动特征值数组
	 * 返回：所有距离中最小的一个
	 */
	public double calDistance(int[] vector1,int[][] vector2){
		double dis ,sum,currentDis;
		int[] temp;
		dis=Double.MAX_VALUE;
		for(int i=0;i<vector2.length;i++){
			sum=0;
			temp=vector2[i];
			for(int j=0;j<vector1.length;j++){
				sum+=((vector1[j]-temp[j])*(vector1[j]-temp[j]));
			}
			currentDis=Math.sqrt(sum);
			if(currentDis<dis)
				dis=currentDis;
		}
		
		return dis;
	}
	
	/**
	 * 函数功能：初始化边缘特征值距离数组
	 * 参数：vector——要计算字符的浮动特征值
	 *      word——边缘特征值字库
	 * 返回：
	 */
	public void calSACDistance(int[][] vector, WordCode[] word) {
		int[] temp;
		double dis;
		Distance d;

		SACdistance.clear();
		for (int i = 0; i < word.length; i++) {
			temp = word[i].getEigenvalue();
			dis=calDistance(temp, vector);
			d = new Distance(word[i].getWord(), i ,dis);
			SACdistance.add(d);
			
		}
	}
	
	/**
	 * 函数功能：初始化外围特征值距离数组
	 * 参数：vector——要计算字符的浮动特征值
	 *      word——边缘特征值字库
	 * 返回：
	 */
	public void calPCDistance(int[][] vector, WordCode[] word) {
		int[] temp;
		double dis;
		Distance d;

		PCdistance.clear();
		for (int i = 0; i < word.length; i++) {
			temp = word[i].getEigenvalue();
			dis = calDistance(temp, vector);
			d=new Distance(word[i].getWord(), i ,dis);
			PCdistance.add(d);
		}
	}
	
	/**
	 * 函数功能：初始化网格特征值距离数组
	 * 参数：vector——要计算字符的浮动特征值
	 *      word——网格特征值字库
	 * 返回：
	 */
	public void calGCDistance(int[][] vector, WordCode[] word) {
		int[] temp;
		double dis;
		Distance d;

		GCdistance.clear();
		for (int i = 0; i < word.length; i++) {
			temp = word[i].getEigenvalue();
			dis = calDistance(temp, vector);
			d=new Distance(word[i].getWord(), i ,dis);
			GCdistance.add(d);
		}
	}
	
	/**
	 * 取得图片的像素数组
	 * @param image
	 * @return
	 */
	public int[] getCharPixels(Image image){
		int[] pix = null;
		width=image.getWidth(null);
		height=image.getHeight(null);
		if(width==-1 || height==-1)
			System.out.println("载入失败!");
		else{
			pix=OptImg.takeImg(image, width, height);
			pix=OptImg.filter(pix);
		}
		return pix;
	}
	
	/**
	 * 识别器
	 * 边缘特征值作为一级分类，外围特征值作为二级分类
	 */
	public String recgnizeChar(Image image){
		int num;
		String minchars;
		byte[] wordbyte;	
		int[] samplePix;
		int[][] sampleFloatPerCode,sampleFloatGridCode,sampleFloatSACode;
		
		samplePix = getCharPixels(image);
		//提取边缘特征值
		sampleFloatSACode = CalSurroundingAreaCode.getBigMiFloatAreaCode(
				samplePix, width, height);
		//提取外围特征值
		sampleFloatPerCode = CalPeripheryCode.getBigMiFloatPeripheryCode(
				samplePix, width, height);
		//提取网格特征值
		sampleFloatGridCode = CalGridCode.getBigMi64FloatGridCode(
				samplePix, width, height);
		
		//一级分类
		//计算字符与字库中所有模板的边缘特征值距离
		calSACDistance(sampleFloatSACode, sacode);
		Comparator<Distance> dComp = new disComparator();
		//按升序排序
		Collections.sort(SACdistance, dComp);

		//取距离最小的500个
		percodetemp = new WordCode[500];
		for (int i = 0; i < 500; i++) {
			minchars = SACdistance.get(i).getWord();
			wordbyte = minchars.getBytes();
			num = (wordbyte[0] + 256) * 256 + (wordbyte[1] + 256);
			percodetemp[i] = percode[num];
		}

		//二级分类
		//计算字符与字库中所有模板的外围特征值距离
		calPCDistance(sampleFloatPerCode, percodetemp);
		Collections.sort(PCdistance, dComp);
		gridcodetemp = new WordCode[70];
		for (int i = 0; i < 70; i++) {
			minchars = PCdistance.get(i).getWord();
			wordbyte = minchars.getBytes();
			num = (wordbyte[0] + 256) * 256 + (wordbyte[1] + 256);
			gridcodetemp[i] = gridcode[num];
		}

		calGCDistance(sampleFloatGridCode, gridcodetemp);
		Collections.sort(GCdistance, dComp);
		String result = GCdistance.get(0).getWord();
		
		String front10wordsString = "";
		for(int i=0;i<10;i++)
			front10wordsString += GCdistance.get(i).getWord();
		front10wordsList.add(front10wordsString);
			
		return result;
	}
	
	public String recognizeImage (String path) throws Exception {
		if(!OptImg.operation(path))
			return "非红头文件！";
		
		front10wordsList.clear();
		String title="";
		for(Image img : OptImg.getCharThinImageList())
			title += recgnizeChar(img);
		
		return title;
	}
	
}
