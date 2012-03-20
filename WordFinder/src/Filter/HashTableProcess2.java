package Filter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.BitSet;

public class HashTableProcess2{

	// 测试函数 用来建立空的哈希表
	public static void main(String[] args) {
		HashTableProcess htp = new HashTableProcess(1000000);
		htp.createHashTable();
	}

	int HashTableLength;
	int[] hash = new int[8];
	private BitSet bs0;
	private BitSet bs1;
	private BitSet bs2;
	private BitSet bs3;
	private BitSet bs4;
	private BitSet bs5;
	private BitSet bs6;
	private BitSet bs7;

	public HashTableProcess2(int length,BitSet b0, BitSet b1, BitSet b2, BitSet b3,
			BitSet b4, BitSet b5, BitSet b6, BitSet b7) {
		HashTableLength = length;
		bs0 = new BitSet(length);
		bs1 = new BitSet(length);
		bs2 = new BitSet(length);
		bs3 = new BitSet(length);
		bs4 = new BitSet(length);
		bs5 = new BitSet(length);
		bs6 = new BitSet(length);
		bs7 = new BitSet(length);
	}

	// 保存哈希表的函数（预留）
	public Boolean saveHashTable2() {
               return true;
		
	}

	// 创建哈希表操作
	public Boolean createHashTable2() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					"HashTable2.txt"));
			for (int i = 0; i < HashTableLength; i++) {
				String line = bs0.get(i) + "," + bs1.get(i) + "," + bs2.get(i)
						+ "," + bs3.get(i) + "," + bs4.get(i) + ","
						+ bs5.get(i) + "," + bs6.get(i) + "," + bs7.get(i);
				bw.write(line);
				bw.newLine();
			}

			bw.flush();
			bw.close();
			System.out.println("哈希表创建完毕！");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
