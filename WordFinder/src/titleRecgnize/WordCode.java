package titleRecgnize;
/*
 * �ַ�������
 * �����ַ��Լ�����ֵ����
 * ���ݹ��캯���Ĳ���������ͬ������ֵ
 */
public class WordCode {
	
	private String word;
	private int[] eigenvalue;

	public WordCode(int number) {
		word="";
		eigenvalue=new int[number];
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int[] getEigenvalue() {
		return eigenvalue;
	}

	public void setEigenvalue(int[] eigenvalue) {
		this.eigenvalue = eigenvalue;
	}
}
