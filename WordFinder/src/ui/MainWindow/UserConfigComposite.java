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
	private String[] tableHeader = { "�û���", "����", "ע�����" };
	
	/* �༭������ */
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
		group_add.setText("����û�");

		Label label3 = new Label(group_add, SWT.NONE);
		label3.setText("ע����ݣ�");
		combo_indentity = new Combo(group_add, SWT.READ_ONLY);
		combo_indentity.setItems(new String[] { "��ͨ�û�", "���Ա", "����Ա" });
		combo_indentity.select(0);
		new Label(group_add, SWT.NONE);

		Label label1 = new Label(group_add, SWT.NONE);
		label1.setText("�û�����");
		username = new Text(group_add, SWT.BORDER);
		username.setLayoutData(new GridData(187, SWT.DEFAULT));
		new Label(group_add, SWT.NONE);

		Label label2 = new Label(group_add, SWT.NONE);
		label2.setText("���룺");
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
		btn_reset.setText("����");

		Button btn_add = new Button(group_add, SWT.NONE);
		btn_add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				doRegister();
			}
		});
		btn_add.setText("���");

		Group group_del = new Group(userInfoComp, SWT.BORDER);
		group_del.setLayoutData(new GridData(GridData.FILL_BOTH));
		group_del.setLayout(new GridLayout(1, true));
		group_del.setText("�޸��û���Ϣ");

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
		//�����ɱ༭�Ŀؼ�
		cursor = new TableCursor(userInfoTable, SWT.NONE);
		createMenu();
		cursor.setMenu(menu);
		cursor.setToolTipText("�����Ҽ��޸�");
		editor = new ControlEditor(cursor);
		editor.grabHorizontal = true;
		editor.grabVertical = true;

		Button refresh = new Button(group_del, SWT.NONE);
		refresh.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onRefresh();
			}
		});
		refresh.setText("ˢ�½��");

		userInfoComp.setWeights(new int[] { 30, 70 });
	}

	private void createMenu() {
		menu = new Menu(getShell(), SWT.POP_UP);

		MenuItem modify = new MenuItem(menu, SWT.PUSH);
		modify.setText("�޸�");
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
		del.setText("ɾ��");
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
	 * ע���¼�
	 */
	public void doRegister() {
		String name = username.getText().trim();
		String code = password.getText().trim();
		String identity = combo_indentity.getText().trim();

		if (name.trim().equals("")) {
			MessageDialog.openError(getShell(), "����ʧ��", "�û�������Ϊ�գ�");
			return;
		} else if (code.trim().equals("")) {
			MessageDialog.openError(getShell(), "����ʧ��", "���벻��Ϊ�գ�");
			return;
		}

		if(MainWindow.up.isUserExisted(name)){
			MessageDialog.openError(getShell(), "���ʧ��", "�û���" + name + "�Ѵ��ڣ�");
			return;
		}

		if(MainWindow.up.addUser(name, code, identity)){		
			MessageDialog.openInformation(getShell(), "��ӳɹ�", "���û���ӳɹ���");
			TableItem item = new TableItem(userInfoTable, SWT.NONE);
			item.setText(new String[] { name, code, identity });
			combo_indentity.select(0);
			username.setText("");
			password.setText("");
			
			String username = MainWindow.user;
			String operate = "�û�����";
			String detail = "��ӳɹ����û���Ϊ�� " + name + ",����Ϊ��" + code + ",���Ϊ��"
					+ identity;
			LogProcess sl = new LogProcess();
			sl.saveLog(username, operate, detail);
		}
	}

	/**
	 * ˢ�¶���
	 */
	public void onRefresh() {
		userInfoTable.removeAll();
		if (!connDB.connectToDB())
			System.err.println("���ݿ������쳣~��");
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
	 * ���ñ༭��
	 */
	public void onSetEditor() {
		if (cursor.getColumn() == 2) {
			combo = new Combo(cursor, SWT.NONE);
			combo.setItems(new String[]{ "��ͨ�û�", "���Ա", "����Ա" });
			TableItem row = cursor.getRow();
			combo.setText(row.getText(cursor.getColumn()));
			combo.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.character == SWT.CR) {
						TableItem row = cursor.getRow();
						row.setText(cursor.getColumn(), combo.getText());
						MainWindow.up.updateIdentity(row.getText(0), combo.getText());
						
						String username = MainWindow.user;
						String operate = "�û�����";
						String detail = "�޸��û���Ϣ ���޸�" + row.getText(0) + "��ע�����Ϊ"
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
					String operate = "�û�����";
					String detail = "�޸��û���Ϣ ���޸�" + row.getText(0) + "��ע�����Ϊ"
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
						String operate = "�û�����";
						String detail = "�޸��û���Ϣ ���޸�" + row.getText(0) + "��" 
						        + "����Ϊ"
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
					String operate = "�û�����";
					String detail = "�޸��û���Ϣ ���޸�" + row.getText(0) + "��" 
					        + "����Ϊ"
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
						String operate = "�û�����";
						String detail = "�޸��û���Ϣ ���޸�" + row.getText(0) + "��" 
						        + "�û���Ϊ"
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
					String operate = "�û�����";
					String detail = "�޸��û���Ϣ ���޸�" + row.getText(0) + "��" 
					        + "�û���Ϊ"
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
	 * ɾ���û�
	 */
	public void onDeleteUser(){
		if(MessageDialog.openConfirm(getShell(), "ȷ��ɾ��", "ȷ��ɾ��������ɾ�����޷��ָ���")){
			TableItem row = cursor.getRow();
			if(MainWindow.up.deleteUser(row.getText(0))){
				
				MessageDialog.openError(getShell(), "ɾ���ɹ�", "�û�" + row.getText(0)
						+ "��������Ϣ���Ѿ������ݿ���ɾ����");
				
				String username = MainWindow.user;
				String operate = "�û�����";
				String detail = "ɾ���û���Ϣ ��ɾ�����û���Ϊ" + row.getText(0);
				LogProcess sl = new LogProcess();
				sl.saveLog(username, operate, detail);
				
				userInfoTable.remove(userInfoTable.getSelectionIndex());
			}
		}
	}
}
