package database;

import java.io.File;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class SaveLogAsJxl {

	private int logLength;
	private String[] log;
	private String savePath;

	public static void main(String[] args) {

	}

	public SaveLogAsJxl(StringBuffer log, String savePath) {
		this.log = log.toString().split("&&&");
		this.savePath = savePath;
		logLength = this.log.length;
	}

	public boolean saveLogAsJxl() {
		try {
			WritableWorkbook book = Workbook.createWorkbook(new File(savePath));
			// ������Ϊ����һҳ���Ĺ���������0��ʾ���ǵ�һҳ
			WritableSheet sheet = book.createSheet("��һҳ", 0);

			for (int i = 0; i < logLength; i++) {
				String[] detailes = log[i].split("@@@");
				for (int j = 0; j < detailes.length; j++) {
					Label label = new Label(j, i, detailes[j]);
					sheet.addCell(label);
				}

			}
			book.write();
			book.close();
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}

	}
}
