package titleRecgnize;

import java.awt.image.ColorModel;

public class ImageBinaryzation {
	
	private int pixSum;
	private ColorModel cm;
	private double partion = 0.91;
	
	public double getPartion() {
		return partion;
	}

	public void setPartion(double partion) {
		this.partion = partion;
	}

	public int getPixSum() {
		return pixSum;
	}

	public void setPixSum(int pixSum) {
		this.pixSum = pixSum;
	}

	public ImageBinaryzation(ColorModel cm) {
		if(cm == null)
			cm = ColorModel.getRGBdefault();
		this.cm = cm;
	}

	/**
	 * 求取直方图
	 * @param pixels
	 * @return
	 */
	public int[] getHistogram(int[] pixels){ 
		int[] his = new int[256];
		
		if(pixels == null) {
			System.err.println("Error!Pixels matrix is null!");
			return his;
		}
		pixSum = 0;
		for(int pix : pixels) {
			his[cm.getRed(pix)]++;
			pixSum += cm.getRed(pix);
		}
		
		return his;
	}
	
	/**
	 * 灰度化
	 * @param pixels
	 * @param w
	 * @param h
	 * @return
	 */
	public int[] transGray(int[] pixels, int w, int h) {
		int r, g, b, gray;
		int[] grayPixels = new int[pixels.length];
		
		for(int i=0;i<w*h;i++){
			r = cm.getRed(pixels[i]);
			g = cm.getGreen(pixels[i]);
			b = cm.getBlue(pixels[i]);
			//gray = (int)(0.229*r + 0.587*g + 0.114*b);
			gray = (r + g + b)/3;
			grayPixels[i] = 255<<24 | gray<<16 | gray<<8 | gray;
		}
		
		return grayPixels;
	}
	
	/**
	 * Bersen局部二值化
	 * @param pixels
	 * @param w
	 * @param h
	 * @return
	 */
	public int[] bersenBinaryzation(int[] grayPixels, int w, int h) {
		int[] binPixels = new int[grayPixels.length];
		
		for (int j = 0; j < h; j++)
			for (int i = 0; i < w; i++)
				if (cm.getRed(grayPixels[j*w+i]) <
						getWindowThreshold(grayPixels, w, h, i, j, 2))
					binPixels[j*w+i] = 0xffffffff;
				else
					binPixels[j*w+i] = 0xff000000;
		
		return binPixels;
	}
	
	/**
	 * 求取w邻域的Beresen阈值，即邻域中最大灰度和最小灰度的均值
	 * @param pixels  图像数组
	 * @param width  图像宽
	 * @param height  图像高
	 * @param x  像素点横坐标
	 * @param y  像素点竖坐标
	 * @param w  邻域大小
	 * @return
	 */
	private int getWindowThreshold(int[] pixels, int width, int height,
			                        int x, int y,
			                        int w){
		int min = 255, max = 0, gray;
		for(int j=(y-w<0?0:y-w); j<=(y+w>height-1?height-1:y+w); j++)
			for(int i=(x-w<0?0:x-w); i<=(x+w>width-1?width-1:x+w); i++){
				gray = cm.getRed(pixels[j*width+i]);
				if(gray < min) min = gray;
				if(gray > max) max = gray;
			}
		
		return (min + max)/2;
	}
	
	/**
	 * 迭代法二值化
	 * @param pixels
	 * @param w
	 * @param h
	 * @return
	 */
	public int[] IterateBinaryzation(int[] grayPixels, int w, int h) {
		int Pmax, Pmin, T, T1, SUMo, SUMb, CNTo, CNTb;
		int[] his = getHistogram(grayPixels);
		int[] binPixels = new int[grayPixels.length];
		
		Pmin = 0;
		Pmax = 255;
		for(int i=0;i<256;i++)
			if(his[i] != 0) {
				Pmin = i;
				break;
			}
		for(int i=255;i>-1;i--)
			if(his[i] != 0) {
				Pmax = i;
				break;
			}
		T1 = (Pmin + Pmax)/2;//初始阈值
		
		do {
			T = T1;
			SUMo = 0;
			CNTo = 0;
			if(T > 255) {
				System.err.println("Illegal threshold is " + T);
				break;
			}
			for (int i = 0; i < T; i++) {
				SUMo += his[i] * i;
				CNTo += his[i];
			}
			SUMb = pixSum - SUMo;
			CNTb = w*h - CNTo;
			//新阈值，两部分均值的平均值
			T1 = (int) (((double) SUMo / (double) CNTo + (double) SUMb / (double) CNTb) / 2);
		} while (T != T1);
		
		System.out.println(T);
		
		//T是阈值
		for(int i=0;i<w*h;i++)
			if(cm.getRed(grayPixels[i]) < T)
				binPixels[i] = 0xffffffff;
			else
				binPixels[i] = 0xff000000;
		
		return binPixels;
	}
	
	/**
	 * 结合Berensen局部阈值化的Otsu方法
	 * @param pixels
	 * @param w
	 * @param h
	 * @return
	 */
	public int[] otsu_berensenBinaryzation(int[] grayPixels, int w, int h) {
		int T = getOtsuThreshold(grayPixels, w, h);
		int[] binPixels = new int[grayPixels.length];
		double B = 0.24;

		for(int j=0;j<h;j++)
			for(int i=0;i<w;i++){
			if(cm.getRed(grayPixels[j*w+i]) < (1-B)*T ||
					(cm.getRed(grayPixels[j*w+i]) < getWindowAverage(grayPixels, w, h, i, j, 1) && 
					cm.getRed(grayPixels[j*w+i]) >= (1-B)*T &&
					cm.getRed(grayPixels[j*w+i]) <= (1+B)*T))
				binPixels[j*w+i] = 0xffffffff;
			else if(cm.getRed(grayPixels[j*w+i]) > (1+B)*T ||
					(cm.getRed(grayPixels[j*w+i]) >= getWindowAverage(grayPixels, w, h, i, j, 1) && 
							cm.getRed(grayPixels[j*w+i]) >= (1-B)*T &&
							cm.getRed(grayPixels[j*w+i]) <= (1+B)*T))
				binPixels[j*w+i] = 0xff000000;
			else{
				binPixels[j*w+i] = 0xffff0000;
				System.out.println("There is a exception!");
			}
		}
		
		return binPixels;
	}
	
	/**
	 * otsu法求取全局阈值
	 * @param pixels
	 * @param w
	 * @param h
	 * @return
	 */
	private int getOtsuThreshold(int[] pixels, int w, int h) {
		int n0=0, n1=0, sum0=0, sum1=0, sum=0;
		double u0=0.0, u1=0.0, varMax=0.0, varTmp, threshold = 0;
		
		int[] his = getHistogram(pixels);
		for(int i=0;i<256;i++)sum += i*his[i];
		
		//求取类间最大方差阈值
		for(int i=0;i<256;i++){
			n0 += his[i];
			if(n0 == 0)continue;
			n1 = w*h - n0;
			if(n1 == 0)break;
			sum0 += i*his[i];
			sum1 = sum - sum0;
			u0 = sum0/(n0+0.0);
			u1 = sum1/(n1+0.0);
			varTmp = (double)n0*(double)n1*(u0-u1)*(u0-u1);
			if(varTmp > varMax){
				varMax = varTmp;
				threshold = i;
			}
		}
		
		return (int)threshold;
	}
	
	/**
	 * 求取w邻域均值
	 * @param pixels  图像数组
	 * @param width  图像宽
	 * @param height  图像高
	 * @param x  像素点横坐标
	 * @param y  像素点竖坐标
	 * @param w  邻域大小
	 * @return
	 */
	private double getWindowAverage(int[] pixels, int width, int height,
			                        int x, int y,
			                        int w){
		int sum = 0;
		for(int j=(y-w<0?0:y-w); j<=(y+w>height-1?height-1:y+w); j++)
			for(int i=(x-w<0?0:x-w); i<=(x+w>width-1?width-1:x+w); i++)
				sum += cm.getRed(pixels[j*width+i]);
		double avg = (double)sum/((2*w+1)*(2*w+1)+0.0);
		return avg;
	}
	
	/**
	 * P分位数法二值化
	 * @param grayPixels
	 * @param w
	 * @param h
	 * @return
	 */
	public int[] p_PartionBinaryzation(int[] grayPixels, int w, int h) {
		int sum = 0, threshold = 0;
		int[] binPixels = new int[grayPixels.length];
		int[] his = getHistogram(grayPixels);
		
		for(int i=255;i>-1;i--){
			sum += his[i];
			if(sum/(w*h+0.0) >= partion){
				threshold = i;
				break;
			}
		}
		
		//二值化
		for(int i=0;i<w*h;i++){
			if(cm.getRed(grayPixels[i]) >= threshold)
				binPixels[i] = 0xff000000;
			else
				binPixels[i] = 0xffffffff;
		}
		
		return binPixels;
	}
	
	/**
	 * 最大类间方差法二值化
	 * @param pixels
	 * @param w
	 * @param h
	 * @return
	 */
	public int[] otsuBinaryzation(int[] grayPixels, int w, int h) {
		int n0=0, n1=0, sum0=0, sum1=0, sum=0;
		double u0=0.0, u1=0.0, varMax=0.0, varTmp, threshold = 0;
		int[] binPixels = new int[grayPixels.length];
		
		int[] his = getHistogram(grayPixels);
		for(int i=0;i<256;i++)sum += i*his[i];
		
		//求取类间最大方差阈值
		for(int i=0;i<256;i++){
			n0 += his[i];
			if(n0 == 0)continue;
			n1 = w*h - n0;
			if(n1 == 0)break;
			sum0 += i*his[i];
			sum1 = sum - sum0;
			u0 = sum0/(n0+0.0);
			u1 = sum1/(n1+0.0);
			varTmp = (double)n0*(double)n1*(u0-u1)*(u0-u1);
			if(varTmp > varMax){
				varMax = varTmp;
				threshold = i;
			}
		}
		
		//System.out.println(threshold);
		
		//二值化
		for(int i=0;i<w*h;i++){
			if(cm.getRed(grayPixels[i]) < threshold)
				binPixels[i] = 0xffffffff;
			else
				binPixels[i] = 0xff000000;
		}
		
		return binPixels;
	}
}
