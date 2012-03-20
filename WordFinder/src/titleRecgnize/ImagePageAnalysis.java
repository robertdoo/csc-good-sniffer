package titleRecgnize;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class ImagePageAnalysis {
	private int[] pixelTags;//像素标记
	private LinkedList<Component> compList;//放组件的列表
	private int width, height;
	private int[] pixels;

	private class compComparator implements Comparator<Component> {

		@Override
		public int compare(Component c1, Component c2) {
			if(c1.getTop() < c2.getTop())
				return -1;
			else if (c1.getTop() > c2.getTop()) {
				return 1;
			}
			else return 0;
		}
		
	}

	/**
	 * 标记连通域
	 */
	private void signConnectArea() {
		int tag, count = 1;
		boolean isChanged;
		
		//第一步：标记像素点
		//System.out.println("开始标记……");
		for(int j=0;j<height/3;j++)
			for(int i=0;i<width;i++)
				if(pixels[j*width+i] == 0xffffffff) {//是目标像素点
					tag = getMinNeighbor(i, j);
					pixelTags[j*width+i] = (tag==0?count++:tag);
				}
		//System.out.println("标记结束" + count + "个区域");
		
		//第二布：目标区聚类
		do{
			//System.out.println("目标区聚类");
			isChanged = false;
			for(int j=0;j<height/3;j++)
				for(int i=0;i<width;i++)
					if(pixelTags[j*width+i] != 0) {
						tag = getMinNeighbor(i, j);
						if(pixelTags[j*width+i] != tag) {
							pixelTags[j*width+i] = tag;
							isChanged = true;
						}
					}
			//System.out.println("聚类结束一次");
		}while(isChanged);
	}
	
	/**
	 * 取8邻域最小标号值
	 * @param x
	 * @param y
	 * @return
	 */
	private int getMinNeighbor(int x, int y) {
		int minNeighbor = Integer.MAX_VALUE;
		
		for(int i=(y-1<0?0:y-1);i<=(y+1>=height?y:y+1);i++)
			for(int j=(x-1<0?0:x-1);j<=(x+1>=width-1?x:x+1);j++) {
				if(i==y && j==x)continue;
				if(pixelTags[i*width+j] == 0)continue;
				if(pixelTags[i*width+j] < minNeighbor)
					minNeighbor = pixelTags[i*width+j];
			}
		
		return minNeighbor==Integer.MAX_VALUE?0:minNeighbor;
	}
	
	/**
	 * 提取连通域
	 */
	private void pickComponent() {
		int tag;
		Component comp;
		HashMap<Integer, Component> compMap = new HashMap<Integer, Component>();
		
		for(int j=0;j<height/3;j++)
			for(int i=0;i<width;i++)
				if(pixelTags[j*width+i] != 0) {//目标像素
					tag = pixelTags[j*width+i];
					comp = compMap.get(tag);
					if(comp == null) {   //没有找到，建立新的组件
						comp = new Component(tag, i, j);
						compMap.put(tag, comp);
					}
					else {   //找到了，把新的像素加入组件中
						comp.addNewPoint(i, j);
					}
				}
		
		compList = new LinkedList<Component>();
		Iterator<Integer> iterator = compMap.keySet().iterator();
		while(iterator.hasNext()){
			compList.add(compMap.get(iterator.next()));
		}
		
	}
	
	/**
	 * 合并组件
	 */
	private void mergeComponent() {
		Component comp1, comp2;
		int w, h;
		boolean isMerged;

		//初步剔除噪声
		for(int i=0;i<compList.size();) {
			comp1 = compList.get(i);
			w = (comp1.getRight()-comp1.getLeft()<=0?1:comp1.getRight()-comp1.getLeft());
			h = (comp1.getBottom()-comp1.getTop()<=0?1:comp1.getBottom()-comp1.getTop());

			//长宽比例悬殊
			if(h/w > 25 || w/h > 25)
				compList.remove(i);
			//尺寸过大
			else if(h > 180 || w > 180)
				compList.remove(i);
			//尺寸过小
			else if(h < 4 && w < 4)
				compList.remove(i);
			else {
				i++;
			}
		}
		
		//把分离的部分合并为完整组件
		do{
			//System.out.println("合并组件");
			isMerged = false;
			for(int i=0;i<compList.size();i++) {
				comp1 = compList.get(i);
				for(int j=i+1;j<compList.size();) {
					comp2 = compList.get(j);
					if(comp1.include(comp2)){
						compList.remove(j);
						isMerged = true;
					}
					else if(comp1.overlap(comp2)) {
						comp1.mergeWith(comp2);
						compList.remove(j);
						isMerged = true;
					}
					/**/
					else if(comp1.verticalNear(comp2)) {
						comp1.mergeWith(comp2);
						compList.remove(j);
						isMerged = true;
					}
					
					else
						j++;
				}
			}
			//System.out.println("合并组件完成一次");
		} while(isMerged);
		
		//合并为一个个文本行
		do{
			//System.out.println("合并文本行");
			isMerged = false;
			for(int i=0;i<compList.size();i++) {
				comp1 = compList.get(i);
				for(int j=i+1;j<compList.size();) {
					comp2 = compList.get(j);
					if(comp1.horizontalNear(comp2)){
						comp1.mergeWith(comp2);
						compList.remove(j);
						isMerged = true;
					}
					else
						j++;
				}
			}
			//System.out.println("合并文本行完成一次");
		} while(isMerged);
		
		//剔除掉最后的干扰
		for(int i=0;i<compList.size();) {
			comp1 = compList.get(i);
			w = (comp1.getRight()-comp1.getLeft()<=0?1:comp1.getRight()-comp1.getLeft());
			h = (comp1.getBottom()-comp1.getTop()<=0?1:comp1.getBottom()-comp1.getTop());

			if(w < 9 || h < 9)
				compList.remove(i);
			else {
				i++;
			}
		}
		
		//把意外没有合并的部分合并起来
		do{
			isMerged = false;
			for(int i=0;i<compList.size();i++) {
				comp1 = compList.get(i);
				for(int j=i+1;j<compList.size();) {
					comp2 = compList.get(j);
					if(comp1.include(comp2)){
						compList.remove(j);
						isMerged = true;
					}
					else
						j++;
				}
			}
		} while(isMerged);
	}
	
	
	public void analysis() {
		signConnectArea();
		pickComponent();
		mergeComponent();
	}
	
	/**
	 * 提取标题组件
	 */
	public Component getTitleComponent(int[] binPixels, int width, int height) {
		pixelTags = new int[width*height];
		this.pixels = binPixels;
		this.width = width;
		this.height = height;
		
		analysis();
		
		Component component = null;
		compComparator comparator = new compComparator();
		Collections.sort(compList, comparator);
		
		for(Component comp : compList)
			if(Math.abs((comp.getLeft()+comp.getRight())/2 - width/2) < 80) {
				component = comp;
				break;
			}
		
		return component;
	}
}
