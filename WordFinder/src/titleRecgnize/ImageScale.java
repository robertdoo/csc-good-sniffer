package titleRecgnize;

/*
 ���ܣ���ͼƬ����Ϊw*h��С

 ������img��Ҫ���ŵ�Image����
 dstW:Ŀ��ͼ���
 dstH:Ŀ��ͼ���
 comp���������������Applet
 */

import java.awt.*;
import java.awt.image.*;

public class ImageScale {
	
	/*
	 * ʵ�ֹ������Ľӿ�
	 */
	static class BWFilter extends RGBImageFilter {
		public BWFilter() {
			canFilterIndexColorModel = true;
		}

		public int filterRGB(int x, int y, int rgb) {
			int R, G, B,color;
			ColorModel cm=ColorModel.getRGBdefault();
			if (x == -1);
			R=cm.getRed(rgb)& 0xff;
			G=cm.getGreen(rgb)& 0xff;
			B=cm.getBlue(rgb)& 0xff;
			if(R>=100 && G>=100 && B>=100)
				color=0xffffffff;
			else
				color=0xff000000;
			return color;
		}
	}

	// ///////////////////////////////////////////////////////////////
	// ���ٽ���ֵ�㷨----�����������С������
	// ������img��Ҫ���ŵ�Image����
	// dstW:Ŀ��ͼ���
	// dstH:Ŀ��ͼ���
	// comp���������������Applet
	// ///////////////////////////////////////////////////////////////
	static public Image simpleScale(Image img, int dstW, int dstH) {
		OperateImage OI = new OperateImage();
		int[] scaled, src;
		double widthFactor, heightFactor;
		int srcX = 0, srcY = 0, srcW, srcH;
		src = OI.takeImg(img, img.getWidth(null), img.getHeight(null));
		scaled = new int[dstW * dstH];// ������ź��ͼƬ
		srcW = img.getWidth(null);
		srcH = img.getHeight(null);

		widthFactor = srcW / (dstW + 0.0);
		// System.out.println("widthFactor:"+widthFactor);
		heightFactor = srcH / (dstH + 0.0);
		// System.out.println("heightFactor:"+heightFactor);
		for (int a = 0; a < dstH; a++)
			for (int b = 0; b < dstW; b++) {
				if ((b * widthFactor) % 1 >= 0.5)
					srcX = (int) (b * widthFactor) + 1;
				else
					srcX = (int) (b * widthFactor);
				if ((a * heightFactor) % 1 >= 0.5)
					srcY = (int) (a * heightFactor) + 1;
				else
					srcY = (int) (a * heightFactor);
				if (srcX > srcW - 1)
					srcX = srcW - 1;
				if (srcY > srcH - 1)
					srcY = srcH - 1;
				scaled[a * dstW + b] = src[srcY * srcW + srcX];
			}
		//System.out.println("���ٽ���ֵ�㷨��ɣ�");
		return OI.madeImg(scaled, dstW, dstH);
	}

	// ///////////////////////////////////////////////////////////////
	// ���ٽ���ֵ�㷨--���������С������
	// ������img��Ҫ���ŵ�Image����
	// dstW:Ŀ��ͼ���
	// dstH:Ŀ��ͼ���
	// comp���������������Applet
	// ///////////////////////////////////////////////////////////////
	static public Image simpleScaleSpcial(Image img, int dstW, int dstH) {
		OperateImage OI = new OperateImage();
		int[] scaled, src;
		double widthFactor, heightFactor, tempX, tempY, distance, tempDis;
		double srcX_float = 0.0, srcY_float = 0.0;// �����С������
		int srcX_int = 0, srcY_int = 0;// �������������
		int srcX = 0, srcY = 0, srcW, srcH;
		src = OI.takeImg(img, img.getWidth(null), img.getHeight(null));
		scaled = new int[dstW * dstH];// ������ź��ͼƬ
		srcW = img.getWidth(null);
		srcH = img.getHeight(null);

		widthFactor = srcW / (dstW + 0.0);
		// System.out.println("widthFactor:"+widthFactor);
		heightFactor = srcH / (dstH + 0.0);
		// System.out.println("heightFactor:"+heightFactor);
		for (int a = 0; a < dstH; a++)
			for (int b = 0; b < dstW; b++) {
				distance = Double.MAX_VALUE;
				tempX = b * widthFactor;
				tempY = a * heightFactor;
				srcX_int = (int) tempX;
				srcX_float = tempX % 1;
				srcY_int = (int) tempY;
				srcY_float = tempY % 1;
				tempDis = (srcX_float * srcX_float) + (srcY_float * srcY_float);
				if (tempDis < distance)
				// ���Ͻ� (0,0)
				{
					srcX = srcX_int;
					srcY = srcY_int;
					distance = tempDis;
				}
				tempDis = ((srcX_float - 1) * (srcX_float - 1)) + (srcY_float * srcY_float);
				if (tempDis < distance)
				// ���Ͻ� (1,0)
				{
					srcX = srcX_int + 1;
					srcY = srcY_int;
					distance = tempDis;
				}
				tempDis = (srcX_float * srcX_float) + ((srcY_float - 1) * (srcY_float - 1));
				if (tempDis < distance)
				// ���½� (0,1)
				{
					srcX = srcX_int;
					srcY = srcY_int + 1;
					distance = tempDis;
				}
				tempDis = ((srcX_float - 1) * (srcX_float - 1)) + ((srcY_float - 1) * (srcY_float - 1));
				if (tempDis < distance)
				// ���½� (1,1)
				{
					srcX = srcX_int + 1;
					srcY = srcY_int + 1;
				}
				if (srcX > srcW - 1)
					srcX = srcW - 1;
				if (srcY > srcH - 1)
					srcY = srcH - 1;
				scaled[a * dstW + b] = src[srcY * srcW + srcX];
			}
		//System.out.println("���ٽ���ֵ�㷨(����)��ɣ�");
		return OI.madeImg(scaled, dstW, dstH);
	}

	// ///////////////////////////////////////////////////////////////
	// ˫�����ڲ�ֵ�㷨
	// ������img��Ҫ���ŵ�Image����
	// dstW:Ŀ��ͼ���
	// dstH:Ŀ��ͼ���
	// comp���������������Applet
	//
	// ��ʽ��f(i+u,j+v) = (1-u)(1-v)f(i,j) + (1-u)vf(i,j+1) + u(1-v)f(i+1,j) +
	// uvf(i+1,j+1)
	//
	// ///////////////////////////////////////////////////////////////
	static public Image doubleLinearScale(Image img, int dstW, int dstH) {
		OperateImage OI = new OperateImage();
		Image imgTemp;
		int[] scaled, src;
		int srcW, srcH;
		int R,G,B;
		double widthFactor, heightFactor, tempX, tempY;
		//double srcX_float = 0.0, srcY_float = 0.0;// �����С������
		//int srcX_int = 0, srcY_int = 0;// �������������
		src = OI.takeImg(img, img.getWidth(null), img.getHeight(null));
		ColorModel cm=ColorModel.getRGBdefault();
		for(int j=0;j<src.length;j++){
			R=cm.getRed(src[j]);
			G=cm.getGreen(src[j]);
			B=cm.getBlue(src[j]);
			if(R>=200 && G>=200 && B>=200)
				src[j]=0xffffffff;
			else
				src[j]=0xff000000;
		}
		scaled = new int[dstW * dstH];// ������ź��ͼƬ
		srcW = img.getWidth(null);
		srcH = img.getHeight(null);

		widthFactor = srcW / (dstW + 0.0);
		// System.out.println("widthFactor:"+widthFactor);
		heightFactor = srcH / (dstH + 0.0);
		// System.out.println("heightFactor:"+heightFactor);
		for (int a = 0; a < dstH; a++)
			for (int b = 0; b < dstW; b++) {
				tempX = b * widthFactor;
				tempY = a * heightFactor;
				scaled[a * dstW + b] = getDestPixle(src, srcW, srcH, tempX, tempY);
			}
		//System.out.println("˫�����ڲ�ֵ�㷨��ɣ�");
		imgTemp=OI.madeImg(scaled, dstW, dstH);
		ImageFilter filter=new BWFilter();
		return Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(imgTemp.getSource(),filter));
		//return imgTemp;
	}

	static public int getDestPixle(int[] srcPix,int srcW,int srcH,double x,double y){
		int dstColor=0;
		//ColorModel cm=ColorModel.getRGBdefault();
		double srcX_float=0.0,srcY_float=0.0;// �����С������
		int srcX_int=0,srcY_int=0;// �������������
		int pix1,pix2,pix3,pix4;
		int R,G,B,R1,G1,B1,R2,G2,B2,R3,G3,B3,R4,G4,B4;
		if(x<srcW-1 && y<srcH-1)
		{
			srcX_int=(int)x;
		    srcX_float=x%1;
		    srcY_int=(int)y;
		    srcY_float=y%1;
			pix1=srcPix[srcY_int*srcW+srcX_int];
			pix2=srcPix[(srcY_int+1)*srcW+srcX_int];
			pix3=srcPix[srcY_int*srcW+(srcX_int+1)];
			pix4=srcPix[(srcY_int+1)*srcW+(srcX_int+1)];
			R1=(pix1 & 0xff0000)>>16;
			G1=(pix1 & 0x00ff00)>>8;
			B1=(pix1 & 0x0000ff);
			R2=(pix2 & 0xff0000)>>16;
			G2=(pix2 & 0x00ff00)>>8;
			B2=(pix2 & 0x0000ff);
			R3=(pix3 & 0xff0000)>>16;
			G3=(pix3 & 0x00ff00)>>8;
			B3=(pix3 & 0x0000ff);
			R4=(pix4 & 0xff0000)>>16;
			G4=(pix4 & 0x00ff00)>>8;
			B4=(pix4 & 0x0000ff);
			R = (int) ((1 - srcX_float) * (1 - srcY_float)* R1
			        + (1 - srcX_float)  * srcY_float * R2
					+ srcX_float  * (1 - srcY_float)  * R3
					+ srcX_float  * srcY_float * R4);
			G = (int) ((1 - srcX_float) * (1 - srcY_float)* G1
			        + (1 - srcX_float)  * srcY_float * G2
					+ srcX_float  * (1 - srcY_float)  * G3
					+ srcX_float  * srcY_float * G4);
			B = (int) ((1 - srcX_float) * (1 - srcY_float)* B1
			        + (1 - srcX_float)  * srcY_float * B2
					+ srcX_float  * (1 - srcY_float)  * B3
					+ srcX_float  * srcY_float * B4);
			dstColor=0xff000000 | (R<<16) | (G<<8) | B;
		}
		else if(x>=srcW-1 && y<srcH-1)// ��Խ��
		{
			srcX_int = srcW - 1;
			srcY_int = (int) y;
			srcY_float = y % 1;
			pix1=srcPix[srcY_int*srcW+srcX_int];
			pix2=srcPix[(srcY_int+1)*srcW+srcX_int];
			R1=(pix1 & 0xff0000)>>16;
			G1=(pix1 & 0x00ff00)>>8;
			B1=(pix1 & 0x0000ff);
			R2=(pix2 & 0xff0000)>>16;
			G2=(pix2 & 0x00ff00)>>8;
			B2=(pix2 & 0x0000ff);
			R = (int) ((1-srcY_float)*R1 + srcY_float*R2);
			G = (int) ((1-srcY_float)*G1 + srcY_float*G2);
			B = (int) ((1-srcY_float)*B1 + srcY_float*B2);
			dstColor=0xff000000 | (R<<16) | (G<<8) | B;
		}
		else if(x<srcW-1 && y>=srcH-1)// ��Խ��
		{
			srcX_int = (int) x;
			srcX_float = x % 1;
			srcY_int = srcH - 1;
			pix1=srcPix[srcY_int*srcW+srcX_int];
			pix3=srcPix[srcY_int*srcW+(srcX_int+1)];
			R1=(pix1 & 0xff0000)>>16;
			G1=(pix1 & 0x00ff00)>>8;
			B1=(pix1 & 0x0000ff);
			R3=(pix3 & 0xff0000)>>16;
			G3=(pix3 & 0x00ff00)>>8;
			B3=(pix3 & 0x0000ff);
			R = (int)((1-srcX_float)*R1 + srcX_float*R3);
			G = (int)((1-srcX_float)*G1 + srcX_float*G3);
			B = (int)((1-srcX_float)*B1 + srcX_float*B3);
			dstColor=0xff000000 | (R<<16) | (G<<8) | B;
		}
		else if(x>=srcW-1 && y>=srcH-1)// ����Խ��{
		{
			srcX_int = srcW - 1;
			srcY_int = srcH - 1;
			dstColor=srcPix[srcY_int * srcW + srcX_int];
		}
		return dstColor;
	}
}
