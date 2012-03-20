package titleRecgnize;

import java.awt.image.ColorModel;

/*
功能：图像细化，细化图像中的字符笔画
 */

public class ImageThinning {

	private static int HIGH=0xffffffff;
	////////////////////////////////////////////////
	//功能：二值图像的细线化处理
	//参数：image_in：输入图像
	//     image_out：输出图像
	//     xsize：图像宽度
	//     ysize：图像高度
	////////////////////////////////////////////////
	public static int[] Thinning(int[] image_in,int xsize,int ysize){
		int[] ia=new int[9],ic=new int[9],image_out=new int[xsize*ysize];
		int i,ix,iy,m,ir,iv,iw;
		
		for(iy=0;iy<ysize;iy++)
			for(ix=0;ix<xsize;ix++)
				image_out[iy*xsize+ix]=image_in[iy*xsize+ix];
		m=0xff7fffff;
		ir=1;
		while(ir!=0){
			ir=0;
			for(iy=1;iy<ysize-1;iy++)
				for(ix=1;ix<xsize-1;ix++){
					if(image_out[iy*xsize+ix]!=HIGH)
						continue;
					ia[0]=image_out[iy*xsize+ix+1];
					ia[1]=image_out[(iy-1)*xsize+ix+1];
					ia[2]=image_out[(iy-1)*xsize+ix];
					ia[3]=image_out[(iy-1)*xsize+ix-1];
					ia[4]=image_out[iy*xsize+ix-1];
					ia[5]=image_out[(iy+1)*xsize+ix-1];
					ia[6]=image_out[(iy+1)*xsize+ix];
					ia[7]=image_out[(iy+1)*xsize+ix+1];
					for(i=0;i<8;i++){
						if(ia[i]==m){
							ia[i]=HIGH;
							ic[i]=0;
						}
						else{
							if(ia[i]<HIGH)
								ia[i]=0;
							ic[i]=ia[i];
						}
					}
					ia[8]=ia[0];
					ic[8]=ic[0];
					if(ia[0]+ia[2]+ia[4]+ia[6] == HIGH*4)
						continue;
					for(i=0,iv=0,iw=0;i<8;i++){
						if(ia[i]==HIGH)
							iv++;
						if(ic[i]==HIGH)
							iw++;
					}
					if(iv<=1)continue;
					if(iw==0)continue;
					if(cconc(ia)!=1)
						continue;
					if(image_out[(iy-1)*xsize+ix]==m){
						ia[2]=0;
						if(cconc(ia)!=1)continue;
						ia[2]=HIGH;
					}
					if(image_out[iy*xsize+ix-1]==m){
						ia[4]=0;
						if(cconc(ia)!=1)continue;
						ia[4]=HIGH;
					}
					image_out[iy*xsize+ix]=m;
					ir++;
				}
			m++;
		}
		for(iy=0;iy<ysize;iy++)
			for(ix=0;ix<xsize;ix++)
				if(image_out[iy*xsize+ix]<HIGH)
					image_out[iy*xsize+ix]=0xff000000;
		//System.out.println("细化完成！");
		int R,G,B;
		ColorModel cm=ColorModel.getRGBdefault();
		for(i=0;i<image_out.length;i++){
			R=cm.getRed(image_out[i]);
			G=cm.getGreen(image_out[i]);
			B=cm.getBlue(image_out[i]);
			if(R>=220 && G>=220 && B>=220)
				image_out[i]=0xffffffff;
			else
				image_out[i]=0xff000000;
		}
		return image_out;
	}

	////////////////////////////////////////////////
	//功能：计算连接数
	//参数：inb：连接数
	////////////////////////////////////////////////
	private static int cconc(int[] inb){
		int i,icn;
		icn=0;
		
		for(i=0;i<8;i+=2)
			if(inb[i]==0)
				if(inb[i+1]==HIGH || inb[i+2]==HIGH)
					icn++;	
		return icn;
	}
	
}
