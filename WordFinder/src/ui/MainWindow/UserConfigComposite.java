package ui.MainWindow;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import com.swtdesigner.SWTResourceManager;

import database.ConnDB;
import database.LogProcess;

public class UserConfigComposite extends Composite {

	private Text username, password;
	private Combo combo_indentity;
	private Table userInfoTable;
	private Menu menu;
	
	private ConnDB connDB;
	private String[] tableHeader = { "用户名", "密码", "注册身份" };
	
	/* 编辑器部件 */
	private TableCursor cursor;
	private ControlEditor editor;
	private Text text;
	private Combo combo;
	
	public UserConfigComposite(Composite parent) {
		super(parent, SWT.BORDER);
		connDB = new ConnDB("userinfo");
		createContent();
		//
	}

	private void createContent() {
		this.setLayout(new GridLayout());

		SashForm userInfoComp = new SashForm(this, SWT.NONE);
		userInfoComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		userInfoComp.setSashWidth(2);
		userInfoComp.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		userInfoComp.setOrientation(SWT.VERTICAL);
		userInfoComp.setLayout(new GridLayout());

		Group group_add = new Group(userInfoComp, SWT.BORDER);
		group_add.setLayoutData(new GridData(GridData.FILL_BOTH));
		group_add.setLayout(new GridLayout(3, false));
		group_add.setText("添加用户");

		Label label3 = new Label(group_add, SWT.NONE);
		label3.setText("注册身份：");
		combo_indentity = new Combo(group_add, SWT.READ_ONLY);
		combo_indentity.setItems(new String[] { "普通用户", "审计员", "管理员" });
		combo_indentity.select(0);
		new Label(group_add, SWT.NONE);

		Label label1 = new Label(group_add, SWT.NONE);
		label1.setText("用户名：");
		username = new Text(group_add, SWT.BORDER);
		username.setLayoutData(new GridData(187, SWT.DEFAULT));
		new Label(group_add, SWT.NONE);

		Label label2 = new Label(group_add, SWT.NONE);
		label2.setText("密码：");
		password = new Text(group_add, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(187, SWT.DEFAULT));
		new Label(group_add, SWT.NONE);

		new Label(group_add, SWT.NONE);

		Button btn_reset = new Button(group_add, SWT.NONE);
		btn_reset.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				username.setText("");
				password.setText("");
				combo_indentity.select(0);
			}
		});
		btn_reset.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		btn_reset.setText("重置");

		Button btn_add = new Button(group_add, SWT.NONE);
		btn_add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				doRegister();
			}
		});
		btn_add.setText("添加");

		Group group_del = new Group(userInfoComp, SWT.BORDER);
		group_del.setLayoutData(new GridData(GridData.FILL_BOTH));
		group_del.setLayout(new GridLayout(1, true));
		group_del.setText("修改用户信息");

		userInfoTable = new Table(group_del, SWT.FULL_SELECTION);
		final GridData gd_userInfoTable = new GridData(SWT.FILL, SWT.FILL,
				true, true);
		userInfoTable.setLayoutData(gd_userInfoTable);
		userInfoTable.setLinesVisible(true);
		userInfoTable.setHeaderVisible(true);

		for (int i = 0; i < tableHeader.length; i++) {
			TableColumn column = new TableColumn(userInfoTable, SWT.NONE);
			column.setWidth(100);
			column.setText(tableHeader[i]);
			column.setMoveable(true);
		}
		//创建可编辑的控件
		cursor = new TableCursor(userInfoTable, SWT.NONE);
		createMenu();
		cursor.setMenu(menu);
		cursor.setToolTipText("单击右键修改");
		editor = new ControlEditor(cursor);
		editor.grabHorizontal = true;
		editor.grabVertical = true;

		Button refresh = new Button(group_del, SWT.NONE);
		refresh.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onRefresh();
			}
		});
		refresh.setText("刷新结果");

		userInfoComp.setWeights(new int[] { 30, 70 });
	}

	private void createMenu() {
		menu = new Menu(getShell(), SWT.POP_UP);

		MenuItem modify = new MenuItem(menu, SWT.PUSH);
		modify.setText("修改");
		modify.setImage(SWTResourceManager.getImage("img/new.gif"));
		modify.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				int index = userInfoTable.getSelectionIndex();
				if (index < userInfoTable.getItemCount() && index > -1){
					onSetEditor();		
				}
			}
		});

		MenuItem del = new MenuItem(menu, SWT.PUSH);
		del.setText("删除");
		del.setImage(SWTResourceManager.getImage("img/delete.gif"));
		del.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				int index = userInfoTable.getSelectionIndex();
				if (index < userInfoTable.getItemCount() && index > -1) {		
					onDeleteUser();
				}
			}
		});
	}

	/**
	 * 注册事件
	 */
	public void doRegister() {
		String name = username.getText().trim();
		String code = password.getText().trim();
		String identity = combo_indentity.getText().trim();

		if (name.trim().equals("")) {
			MessageDialog.openError(getShell(), "操作失败", "用户名不能为空！");
			return;
		} else if (code.trim().equals("")) {
			MessageDialog.openError(getShell(), "操作失败", "密码不能为空！");
			return;
		}

		if(MainWindow.up.isUserExisted(name)){
			MessageDialog.openError(getShell(), "添加失败", "用户名" + name + "已存在！");
			return;
		}

		if(MainWindow.up.addUser(name, code, identity)){		
			MessageDialog.openInformation(getShell(), "添加成功", "新用户添加成功！");
			TableItem item = new TableItem(userInfoTable, SWT.NONE);
			item.setText(new String[] { name, code, identity });
			combo_indentity.select(0);
			username.setText("");
			password.setText("");
			
			String username = MainWindow.user;
			String operate = "用户管理";
			String detail = "添加成功，用户名为： " + name + ",密码为：" + code + ",身份为："
					+ identity;
			LogProcess sl = new LogProcess();
			sl.saveLog(username, operate, detail);
		}
	}

	/**
	 * 刷新动作
	 */
	public void onRefresh() {
		userInfoTable.removeAll();
		if (!connDB.connectToDB())
			System.err.println("数据库连接异常~！");
		String sqlstr = "select * from registerinfo";
		ResultSet rs = connDB.executeQuery(sqlstr);
		try {
			while (rs.next()) {
				TableItem item = new TableItem(userInfoTable, SWT.NONE);
				item.setText(new String[] { rs.getString("UserName"),
						rs.getString("Password"), rs.getString("Identity") });
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		connDB.closeConnection();
	}

	/**
	 * 设置编辑器
	 */
	public void onSetEditor() {
		if (cursor.getColumn() == 2) {
			combo = new Combo(cursor, SWT.NONE);
			combo.setItems(new String[]{ "普通用户", "审计员", "管理员" });
			TableItem row = cursor.getRow();
			combo.setText(row.getText(cursor.getColumn()));
			combo.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.character == SWT.CR) {
						TableItem row = cursor.getRow();
						row.setText(cursor.getColumn(), combo.getText());
						MainWindow.up.updateIdentity(row.getText(0), combo.getText());
						
						String username = MainWindow.user;
						String operate = "用户管理";
						String detail = "修改用户信息 ，修改" + row.getText(0) + "的注册身份为"
								+ combo.getText();
						LogProcess sl = new LogProcess();
						sl.saveLog(username, operate, detail);
						System.out.println(detail);
						
						combo.dispose();
					}
					if (e.character == SWT.ESC) {
						combo.dispose();
					}
				}
			});
			combo.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					TableItem row = cursor.getRow();
					row.setText(cursor.getColumn(), combo.getText());
					MainWindow.up.updateIdentity(row.getText(0), combo.getText());
					
					String username = MainWindow.user;
					String operate = "用户管理";
					String detail = "修改用户信息 ，修改" + row.getText(0) + "的注册身份为"
							+ combo.getText();
					LogProcess sl = new LogProcess();
					sl.saveLog(username, operate, detail);
					System.out.println(detail);
					
					combo.dispose();
				}
			});
			editor.setEditor(combo);
			combo.setFocus();
		} 
		else if(cursor.getColumn() == 1) {
			text = new Text(cursor, SWT.NONE);
			TableItem row = cursor.getRow();
			text.setText(row.getText(cursor.getColumn()));
			text.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.character == SWT.CR) {
						TableItem row = cursor.getRow();
						
						String username = MainWindow.user;
						String operate = "用户管理";
						String detail = "修改用户信息 ，修改" + row.getText(0) + "的" 
						        + "密码为"
								+ text.getText();
						LogProcess sl = new LogProcess();
						sl.saveLog(username, operate, detail);
						System.out.println(detail);
						
						row.setText(cursor.getColumn(), text.getText());
						MainWindow.up.updatePassword(row.getText(0), text.getText());
						text.dispose();
					}
					if (e.character == SWT.ESC) {
						text.dispose();
					}
				}
			});
			text.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					TableItem row = cursor.getRow();
					
					String username = MainWindow.user;
					String operate = "用户管理";
					String detail = "修改用户信息 ，修改" + row.getText(0) + "的" 
					        + "密码为"
							+ text.getText();
					LogProcess sl = new LogProcess();
					sl.saveLog(username, operate, detail);
					System.out.println(detail);
					
					row.setText(cursor.getColumn(), text.getText());
					MainWindow.up.updatePassword(row.getText(0), text.getText());
					text.dispose();
				}
			});
			editor.setEditor(text);
			text.setFocus();
			text.selectAll();
		}
		else {
			text = new Text(cursor, SWT.NONE);
			TableItem row = cursor.getRow();
			text.setText(row.getText(cursor.getColumn()));
			text.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.character == SWT.CR) {
						TableItem row = cursor.getRow();
						
						String username = MainWindow.user;
						String operate = "用户管理";
						String detail = "修改用户信息 ，修改" + row.getText(0) + "的" 
						        + "用户名为"
								+ text.getText();
						LogProcess sl = new LogProcess();
						sl.saveLog(username, operate, detail);
						System.out.println(detail);
						
						MainWindow.up.updateUsername(row.getText(0), text.getText());
						row.setText(cursor.getColumn(), text.getText());
						text.dispose();
					}
					if (e.character == SWT.ESC) {
						text.dispose();
					}
				}
			});
			text.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					TableItem row = cursor.getRow();
					
					String username = MainWindow.user;
					String operate = "用户管理";
					String detail = "修改用户信息 ，修改" + row.getText(0) + "的" 
					        + "用户名为"
							+ text.getText();
					LogProcess sl = new LogProcess();
					sl.saveLog(username, operate, detail);
					System.out.println(detail);
					
					MainWindow.up.updateUsername(row.getText(0), text.getText());
					row.setText(cursor.getColumn(), text.getText());
					text.dispose();
				}
			});
			editor.setEditor(text);
			text.setFocus();
			text.selectAll();
		}
	}
	
	/**
	 * 删除用户
	 */
	public void onDeleteUser(){
		if(MessageDialog.openConfirm(getShell(), "确认删除", "确认删除吗？数据删除后无法恢复。")){
			TableItem row = cursor.getRow();
			if(MainWindow.up.deleteUser(row.getText(0))){
				
				MessageDialog.openError(getShell(), "删除成功", "用户" + row.getText(0)
						+ "的所有信息都已经从数据库中删除！");
				
				String username = MainWindow.user;
				String operate = "用户管理";
				String detail = "删除用户信息 ，删除的用户名为" + row.getText(0);
				LogProcess sl = new LogProcess();
				sl.saveLog(username, operate, detail);
				
				userInfoTable.remove(userInfoTable.getSelectionIndex());
			}
		}
	}
}
