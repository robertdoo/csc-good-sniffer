package titleRecgnize;

/*
 * ����ģ��
 * �洢һ���־������ĵľ����λ��
 */

public class Distance {

	private String word;
	private int position;
	private double distance;
	
	public Distance(String word, int position, double distance) {
		super();
		this.word = word;
		this.position = position;
		this.distance = distance;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public double getDitance() {
		return distance;
	}
	
	public void setDitance(double distance) {
		this.distance = distance;
	}
	
}
