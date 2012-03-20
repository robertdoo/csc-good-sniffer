package titleRecgnize;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class ImagePageAnalysis {
	private int[] pixelTags;//���ر��
	private LinkedList<Component> compList;//��������б�
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
	 * �����ͨ��
	 */
	private void signConnectArea() {
		int tag, count = 1;
		boolean isChanged;
		
		//��һ����������ص�
		//System.out.println("��ʼ��ǡ���");
		for(int j=0;j<height/3;j++)
			for(int i=0;i<width;i++)
				if(pixels[j*width+i] == 0xffffffff) {//��Ŀ�����ص�
					tag = getMinNeighbor(i, j);
					pixelTags[j*width+i] = (tag==0?count++:tag);
				}
		//System.out.println("��ǽ���" + count + "������");
		
		//�ڶ�����Ŀ��������
		do{
			//System.out.println("Ŀ��������");
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
			//System.out.println("�������һ��");
		}while(isChanged);
	}
	
	/**
	 * ȡ8������С���ֵ
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
	 * ��ȡ��ͨ��
	 */
	private void pickComponent() {
		int tag;
		Component comp;
		HashMap<Integer, Component> compMap = new HashMap<Integer, Component>();
		
		for(int j=0;j<height/3;j++)
			for(int i=0;i<width;i++)
				if(pixelTags[j*width+i] != 0) {//Ŀ������
					tag = pixelTags[j*width+i];
					comp = compMap.get(tag);
					if(comp == null) {   //û���ҵ��������µ����
						comp = new Component(tag, i, j);
						compMap.put(tag, comp);
					}
					else {   //�ҵ��ˣ����µ����ؼ��������
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
	 * �ϲ����
	 */
	private void mergeComponent() {
		Component comp1, comp2;
		int w, h;
		boolean isMerged;

		//�����޳�����
		for(int i=0;i<compList.size();) {
			comp1 = compList.get(i);
			w = (comp1.getRight()-comp1.getLeft()<=0?1:comp1.getRight()-comp1.getLeft());
			h = (comp1.getBottom()-comp1.getTop()<=0?1:comp1.getBottom()-comp1.getTop());

			//�����������
			if(h/w > 25 || w/h > 25)
				compList.remove(i);
			//�ߴ����
			else if(h > 180 || w > 180)
				compList.remove(i);
			//�ߴ��С
			else if(h < 4 && w < 4)
				compList.remove(i);
			else {
				i++;
			}
		}
		
		//�ѷ���Ĳ��ֺϲ�Ϊ�������
		do{
			//System.out.println("�ϲ����");
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
			//System.out.println("�ϲ�������һ��");
		} while(isMerged);
		
		//�ϲ�Ϊһ�����ı���
		do{
			//System.out.println("�ϲ��ı���");
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
			//System.out.println("�ϲ��ı������һ��");
		} while(isMerged);
		
		//�޳������ĸ���
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
		
		//������û�кϲ��Ĳ��ֺϲ�����
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
	 * ��ȡ�������
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
