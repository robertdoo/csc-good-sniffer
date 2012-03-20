package titleRecgnize;

/*
 * ʶ����
 */

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class Recognizer {
	
	private WordCode[] sacode,percode,percodetemp,gridcode,gridcodetemp;//�ֿ�ı�Ե����ֵ����������ֵ
	private OperateImage OptImg;
	private LinkedList<Distance> SACdistance,PCdistance,GCdistance;//��Ե����ֵ��������   ��������ֵ��������
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
		
		sacode=readInSACode("��������ֵ/��Ե����ֵ.txt");                 //�����Ե����ֵ
		percode=readInPeripheryCode("��������ֵ/��Χ����ֵ.txt");          //������Χ����ֵ
		gridcode=readInGridCode("��������ֵ/64��������ֵ.txt");           //������������ֵ
	}


	/**
	 * �������ܣ���txt�ļ��ж����ַ��ı�Ե����ֵ
	 * ������path����txt�ļ���·��
	 * ���أ���ʼ���õ�WordCode���飬һ��WordCode��¼����һ���ַ���������ֵ
	 */
	private WordCode[] readInSACode(String path){
		WordCode[] word=new WordCode[2500];
		String str,num,first;
		int count;
		int p,n;//p�ַ�����λ��
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
	 * �������ܣ���txt�ļ��ж����ַ�����Χ����ֵ���Թ�ϣ��ʽ����
	 * ������path����txt�ļ���·��
	 * ���أ���ʼ���õ�WordCode���飬һ��WordCode��¼����һ���ַ���������ֵ
	 */
	private WordCode[] readInPeripheryCode(String path){
		WordCode[] word=new WordCode[65600];
		String str,num,first;
		byte[] wordbyte;
		int count,hash;
		int p,n;//p�ַ�����λ��
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
				//Ϊ������ȡ�����ù�ϣ��ʽ����
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
	 * �������ܣ���txt�ļ��ж����ַ�����������ֵ���Թ�ϣ��ʽ����
	 * ������path����txt�ļ���·��
	 * ���أ���ʼ���õ�WordCode���飬һ��WordCode��¼����һ���ַ���������ֵ
	 */
	private WordCode[] readInGridCode(String path){
		WordCode[] word=new WordCode[65600];
		String str,num,first;
		byte[] wordbyte;
		int count,hash;
		int p,n;//p�ַ�����λ��
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
				//Ϊ������ȡ�����ù�ϣ��ʽ����
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
	 * �������ܣ�����һ���ַ�������ֵ����һ���ַ��ĸ�������ֵ����С����
	 * ������vector1����Ҫ������ַ�����ֵ
	 *      vector2������������ֵ����
	 * ���أ����о�������С��һ��
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
	 * �������ܣ���ʼ����Ե����ֵ��������
	 * ������vector����Ҫ�����ַ��ĸ�������ֵ
	 *      word������Ե����ֵ�ֿ�
	 * ���أ�
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
	 * �������ܣ���ʼ����Χ����ֵ��������
	 * ������vector����Ҫ�����ַ��ĸ�������ֵ
	 *      word������Ե����ֵ�ֿ�
	 * ���أ�
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
	 * �������ܣ���ʼ����������ֵ��������
	 * ������vector����Ҫ�����ַ��ĸ�������ֵ
	 *      word������������ֵ�ֿ�
	 * ���أ�
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
	 * ȡ��ͼƬ����������
	 * @param image
	 * @return
	 */
	public int[] getCharPixels(Image image){
		int[] pix = null;
		width=image.getWidth(null);
		height=image.getHeight(null);
		if(width==-1 || height==-1)
			System.out.println("����ʧ��!");
		else{
			pix=OptImg.takeImg(image, width, height);
			pix=OptImg.filter(pix);
		}
		return pix;
	}
	
	/**
	 * ʶ����
	 * ��Ե����ֵ��Ϊһ�����࣬��Χ����ֵ��Ϊ��������
	 */
	public String recgnizeChar(Image image){
		int num;
		String minchars;
		byte[] wordbyte;	
		int[] samplePix;
		int[][] sampleFloatPerCode,sampleFloatGridCode,sampleFloatSACode;
		
		samplePix = getCharPixels(image);
		//��ȡ��Ե����ֵ
		sampleFloatSACode = CalSurroundingAreaCode.getBigMiFloatAreaCode(
				samplePix, width, height);
		//��ȡ��Χ����ֵ
		sampleFloatPerCode = CalPeripheryCode.getBigMiFloatPeripheryCode(
				samplePix, width, height);
		//��ȡ��������ֵ
		sampleFloatGridCode = CalGridCode.getBigMi64FloatGridCode(
				samplePix, width, height);
		
		//һ������
		//�����ַ����ֿ�������ģ��ı�Ե����ֵ����
		calSACDistance(sampleFloatSACode, sacode);
		Comparator<Distance> dComp = new disComparator();
		//����������
		Collections.sort(SACdistance, dComp);

		//ȡ������С��500��
		percodetemp = new WordCode[500];
		for (int i = 0; i < 500; i++) {
			minchars = SACdistance.get(i).getWord();
			wordbyte = minchars.getBytes();
			num = (wordbyte[0] + 256) * 256 + (wordbyte[1] + 256);
			percodetemp[i] = percode[num];
		}

		//��������
		//�����ַ����ֿ�������ģ�����Χ����ֵ����
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
			return "�Ǻ�ͷ�ļ���";
		
		front10wordsList.clear();
		String title="";
		for(Image img : OptImg.getCharThinImageList())
			title += recgnizeChar(img);
		
		return title;
	}
	
}
