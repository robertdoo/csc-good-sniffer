package titleRecgnize;
/*
 * 字符特征类
 * 包括字符以及特征值数组
 * 根据构造函数的参数建立不同的特征值
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
