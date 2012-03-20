package Filter;


import Filter.GeneralHashFunctionLibrary;

//进行哈希运算的类
public class Filter extends GeneralHashFunctionLibrary {
	private int length;
	
	public Filter(int length){
		this.length = length;
	}
	public Filter(){
		
	}
	public void setPrime(int length) {
		this.length = length;
	}
	//分别计算字符串对应的8个哈希值
	int[] hash;
	public int[] BF(String url) {
		hash = new int[8];
		hash[0] = (int) RSHash(url, length);
		hash[1] = (int) JSHash(url, length);
		hash[2] = (int) BKDRHash(url, length);
		hash[3] = (int) SDBMHash(url, length);
		hash[4] = (int) DJBHash(url, length);
		hash[5] = (int) DEKHash(url, length);
		hash[6] = (int) FNVHash(url, length);
		hash[7] = (int) APHash(url, length);

		return hash;

	}
}
