package ui.MainWindow;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.swtdesigner.SWTResourceManager;

import database.ConnDB;
import database.LogProcess;
import database.SaveLogAsJxl;

public class LogComposite extends Composite {

	private Combo combo_8;
	private Combo combo_7;
	private Combo combo_6;
	private Combo combo_5;
	private Combo combo_4;
	private Combo combo_3;
	private Combo combo_2;
	private Combo combo;
	private String date;
	private StringBuffer log;
	private boolean haveMessage;
	private Menu menu;
	private Table table;
	
	public LogComposite(Composite parent) {
		super(parent, SWT.BORDER);
		date = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
		log = new StringBuffer();
		haveMessage = false;
		createContent();
	}

	private void createContent() {
		final Button button_2 = new Button(this, SWT.NONE);
		button_2.setBounds(623, 48, 85, 31);
		button_2.setText("导出结果");
		button_2.setEnabled(false);
		button_2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
				dialog.setFilterPath("./");
				dialog.setFilterExtensions(new String[]{".xls"});
				dialog.setFilterNames(new String[]{".xls"});
				String file = dialog.open();
				if(file==null)return;
				
				System.out.println(dialog.getFilterPath());
				System.out.println(dialog.getFileName());
				SaveLogAsJxl al = new SaveLogAsJxl(log,dialog.getFilterPath()+"\\"+dialog.getFileName());
				al.saveLogAsJxl();
				
				MessageDialog.openInformation(getShell(), "导出完毕","日志已导出到" + 
						dialog.getFilterPath()+"\\"+dialog.getFileName());
			}
		});

		final Button button = new Button(this, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				String userName = combo.getText().trim();
				String beginTime = getBeginTimeAsString();
				String endTime = getEndTimeAsString();
				String Operate = combo_8.getText();
				if (beginTime == null || endTime == null) {
					MessageDialog.openError(getShell(), "提示用户信息", "日期有误，请重新选择！");
				} else {
					boolean haveMessage = doSearch(Operate, userName,
							beginTime, endTime);
					if (haveMessage == false){
						MessageDialog.openInformation(getShell(), "提示用户信息",
						"没有相关操作信息！");
						button_2.setEnabled(false);
					}else{
						button_2.setEnabled(true);
					}
						
					if (userName == "")
						userName = "所有用户";
					String user = MainWindow.user;
					String operate = "审计工作";
					String detail = "审计的用户为 ：" + userName + ",时间为：" + beginTime
							+ "到" + endTime;
					LogProcess sl = new LogProcess();
					sl.saveLog(user, operate, detail);
				}
			}
		});
		button.setText("开始查询");
		button.setBounds(623, 11, 85, 31);

		final Label label_user = new Label(this, SWT.NONE);
		label_user.setText("查询用户");
		label_user.setBounds(33, 17, 111, 25);
		label_user.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NORMAL));

		final Label label_startTime = new Label(this, SWT.NONE);
		label_startTime.setText("起始时间");
		label_startTime.setBounds(33, 50, 111, 25);
		label_startTime.setFont(SWTResourceManager
				.getFont("宋体", 11, SWT.NORMAL));

		final Label label_endTime = new Label(this, SWT.NONE);
		label_endTime.setBounds(33, 88, 111, 31);
		label_endTime.setText("终止时间");
		label_endTime.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NORMAL));

		table = new Table(this,SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(10, 159, 774, 365);

		final TableColumn newColumnTableColumn = new TableColumn(table,
				SWT.NONE);
		newColumnTableColumn.setWidth(100);
		newColumnTableColumn.setText("用户名");

		final TableColumn newColumnTableColumn_1 = new TableColumn(table,
				SWT.NONE);
		newColumnTableColumn_1.setWidth(100);
		newColumnTableColumn_1.setText("操作名称");

		//new TableCursor(table, SWT.NONE);

		final TableColumn newColumnTableColumn_2 = new TableColumn(table,
				SWT.NONE);
		newColumnTableColumn_2.setWidth(162);
		newColumnTableColumn_2.setText("操作时间");
		
		final TableColumn newColumnTableColumn_3 = new TableColumn(table,
				SWT.NONE);
		newColumnTableColumn_3.setWidth(445);
		newColumnTableColumn_3.setText("描述");

		combo = new Combo(this, SWT.FLAT);
		combo.setBounds(150, 14, 90, 25);
		combo.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NORMAL));

		combo_2 = new Combo(this, SWT.FLAT);
		combo_2.setBounds(150, 47, 65, 25);
		String[] years = new String[] { "2010", "2011", "2012", "2013", "2014",
				"2015", "2016", "2017", "2018", "2019", "2020" };
		combo_2.setItems(years);
		combo_2.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NORMAL));
		combo_2.setText(date.substring(0, 4));

		combo_3 = new Combo(this, SWT.FLAT);
		combo_3.setBounds(257, 47, 54, 25);
		String[] mounth = new String[] { "01", "02", "03", "04", "05", "06",
				"07", "08", "09", "10", "11", "12" };
		combo_3.setItems(mounth);
		combo_3.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NORMAL));
		combo_3.setText(date.substring(5, 7));

		combo_4 = new Combo(this, SWT.FLAT);
		combo_4.setBounds(342, 47, 54, 25);
		String[] day = new String[] { "01", "02", "03", "04", "05", "06", "07",
				"08", "09", "10", "11", "12", "13", "14", "15", "16", "17",
				"18", "19", "20", "21", "22", "23", "24", "25", "26", "27",
				"28", "29", "30", "31" };
		combo_4.setItems(day);
		combo_4.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NORMAL));
		combo_4.setText(date.substring(8, 10));

		combo_5 = new Combo(this, SWT.FLAT);
		combo_5.setBounds(150, 82, 65, 25);
		combo_5.setItems(years);
		combo_5.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NORMAL));
		combo_5.setText(date.substring(0, 4));

		combo_6 = new Combo(this, SWT.FLAT);
		combo_6.setBounds(257, 82, 54, 25);
		combo_6.setItems(mounth);
		combo_6.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NORMAL));
		combo_6.setText(date.substring(5, 7));

		combo_7 = new Combo(this, SWT.FLAT);
		combo_7.setBounds(342, 82, 54, 25);
		combo_7.setItems(day);
		combo_7.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NORMAL));
		combo_7.setText(date.substring(8, 10));

		final Label label = new Label(this, SWT.NONE);
		label.setText("年");
		label.setBounds(221, 50, 30, 22);
		label.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NORMAL));

		final Label label_1 = new Label(this, SWT.NONE);
		label_1.setBounds(317, 50, 30, 22);
		label_1.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NORMAL));
		label_1.setText("月");

		final Label label_2 = new Label(this, SWT.NONE);
		label_2.setBounds(402, 50, 30, 22);
		label_2.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NORMAL));
		label_2.setText("日");

		final Label label_3 = new Label(this, SWT.NONE);
		label_3.setBounds(221, 86, 30, 22);
		label_3.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NORMAL));
		label_3.setText("年");

		final Label label_4 = new Label(this, SWT.NONE);
		label_4.setBounds(317, 86, 30, 22);
		label_4.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NORMAL));
		label_4.setText("月");

		final Label label_5 = new Label(this, SWT.NONE);
		label_5.setBounds(402, 86, 30, 22);
		label_5.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NORMAL));
		label_5.setText("日");

		final Label label_user_1 = new Label(this, SWT.NONE);
		label_user_1.setBounds(246, 14, 65, 25);
		label_user_1.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NORMAL));
		label_user_1.setText("查询操作");

		combo_8 = new Combo(this, SWT.FLAT);
		combo_8.setBounds(321, 14, 111, 25);
		combo_8.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NORMAL));
		String[] operates = new String[] { "登录", "论坛搜索", "定时搜索", "即时搜索",
				"图片搜索", "用户管理", "审计工作" };
		combo_8.setItems(operates);

		createMenu();
	}

	public void loadUsers() {
		combo.setItems(MainWindow.up.getAllUsers());
	}
	
	private boolean doSearch(String Operate, String userName, String beginTime,
			String endTime) {
		// TableItem ti = new TableItem(table,SWT.LEFT);
		haveMessage = false;
		System.out.println("开始查询！");
		int begintime = Integer.parseInt(beginTime);
		int endtime = Integer.parseInt(endTime);
		String user;
		String operate;
		String detail;
		String time;
		String sql;
		
		ConnDB connDB = new ConnDB("log");
		connDB.connectToDB();
	
		// table.clearAll();
		TableItem[] items = table.getItems();
		for (int i = items.length - 1; i >= 0; i--) {
			table.remove(i);
		}
		
		log.append("用户名@@@"+"操作类别@@@"+"操作时间@@@"+"细节描述&&&");
		for (int i = begintime; i <= endtime; i++) {
			String tableName = "log" + i;
			// System.out.println(tableName);
			if (connDB.isTableExist(tableName)) {
				// System.out.println(userName);
				if (userName.equals("") && Operate.equals(""))
					sql = "select user,operate,time,detail from " + tableName;
				else if (!userName.equals("") && Operate.equals(""))
					sql = "select user,operate,time,detail from " + tableName
							+ " where user='" + userName + "'";
				else if (userName.equals("") && !Operate.equals(""))
					sql = "select user,operate,time,detail from " + tableName
							+ " where operate='" + Operate + "'";
				else
					sql = "select user,operate,time,detail from " + tableName
							+ " where user='" + userName + "'&&" + "operate='"
							+ Operate + "'";
				ResultSet rs = connDB.executeQuery(sql);
				try {
					while (rs.next()) {
						user = rs.getString("user");
						operate = rs.getString("operate");
						detail = rs.getString("detail");
						time = rs.getTimestamp("time").toString();
						new TableItem(table, SWT.NONE).setText(new String[] {
								user, operate, time, detail });
						log.append(user+"@@@"+operate+"@@@"+time+"@@@"+detail+"&&&") ;
						haveMessage = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		connDB.closeConnection();
		return haveMessage;
	}

	public String getBeginTimeAsString() {
		String time = "";
		String Year = combo_2.getText();
		System.out.println(Year.length());
		if (Year.length() != 4)
			return null;

		String Mounth = combo_3.getText();
		System.out.println(Mounth.length());
		if (Mounth.length() == 0)
			return null;

		String Day = combo_4.getText();
		System.out.println(Day.length());
		if (Day.length() == 0)
			return null;

		try {
			int year = Integer.parseInt(Year);
			int mounth = Integer.parseInt(Mounth);
			int day = Integer.parseInt(Day);
			if (year > 2020 || year < 0)
				return null;
			if (mounth > 12 || mounth <= 0)
				return null;
			if (day > 31 || day <= 0)
				return null;

			if (mounth < 10 && Mounth.length() == 1)
				Mounth = "0" + Mounth;
			if (day < 10 && Day.length() == 1)
				Day = "0" + Day;

			time = Year + Mounth + Day;

			return time;
		} catch (Exception e) {
			return null;
		}
	}

	public String getEndTimeAsString() {
		String time = "";
		String Year = combo_5.getText();
		if (Year.length() != 4)
			return null;
		String Mounth = combo_6.getText();
		if (Mounth.length() == 0)
			return null;
		String Day = combo_7.getText();
		if (Day.length() == 0)
			return null;
		time = Year + Mounth + Day;
		try {
			int year = Integer.parseInt(Year);
			int mounth = Integer.parseInt(Mounth);
			int day = Integer.parseInt(Day);
			if (year > 2020 || year < 0)
				return null;
			if (mounth > 12 || mounth <= 0)
				return null;
			if (day > 31 || day <= 0)
				return null;

			if (mounth < 10 && Mounth.length() == 1)
				Mounth = "0" + Mounth;
			if (day < 10 && Day.length() == 1)
				Day = "0" + Day;

			time = Year + Mounth + Day;

			return time;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void createMenu(){
		menu = new Menu(getShell(), SWT.POP_UP);
		table.setMenu(menu);
		
		
		MenuItem del = new MenuItem(menu, SWT.PUSH);
		del.setText("删除");
		del.setImage(SWTResourceManager.getImage("img/delete.gif"));
		del.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				System.out.println(table.getSelectionIndex());
				TableItem item = table.getItem(table.getSelectionIndex());
				String time = item.getText(2);
				String tableName = "log"+time.substring(0, time.indexOf(" ")).replaceAll("-", "");
				LogProcess lp = new LogProcess();
				if(!lp.deleteLog(tableName, time))
					MessageDialog.openError(getShell(), "提示用户信息", "删除信息出错！");
				else MessageDialog.openInformation(getShell(), "提示用户信息", "删除成功！");
				lp.getConnDB().closeConnection();
				
				table.remove(table.getSelectionIndex());
				
			}
		});
	}

}
