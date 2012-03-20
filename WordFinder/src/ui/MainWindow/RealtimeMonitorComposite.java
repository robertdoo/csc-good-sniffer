package ui.MainWindow;

import java.awt.MouseInfo;
import java.awt.Point;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import com.swtdesigner.SWTResourceManager;

import searchUtility.LookupWarningResultThread;
import ui.MonitorWindow.MonitorWindow;

public class RealtimeMonitorComposite extends Composite {
	private Text text_beginTime, text_endTime;
	private Combo combo_rule;
	
	public RealtimeMonitorComposite(Composite parent) {
		super(parent, SWT.BORDER);
		createContent();
	}
	
	private void createContent() {
		this.setLayout(new GridLayout());
		
		Group group_option = new Group(this, SWT.BORDER);
		group_option.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group_option.setText("查询条件");

		final Label label = new Label(group_option, SWT.NONE);
		label.setText("选择规则：");
		label.setBounds(33, 23, 65, 14);

		final Label label_1 = new Label(group_option, SWT.NONE);
		label_1.setText("选择时间范围：");
		label_1.setBounds(10, 55, 113, 14);

		combo_rule = new Combo(group_option, SWT.READ_ONLY);
		combo_rule.setBounds(104, 20, 113, 22);

		final Label label_2 = new Label(group_option, SWT.NONE);
		label_2.setText("从");
		label_2.setBounds(104, 55, 26, 14);

		final Label label_3 = new Label(group_option, SWT.NONE);
		label_3.setText("到");
		label_3.setBounds(292, 55, 26, 14);

		final Button button_lookup = new Button(group_option, SWT.NONE);
		button_lookup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onSearch();
			}
		});
		button_lookup.setText("查询");
		button_lookup.setBounds(488, 33, 72, 36);

		text_beginTime = new Text(group_option, SWT.BORDER);
		text_beginTime.addMouseListener(new MouseAdapter() {
			public void mouseDown(final MouseEvent e) {
				Point point = MouseInfo.getPointerInfo().getLocation();
				DateDialog dateDlg = new DateDialog(getShell(), point);
				dateDlg.open();
				if(dateDlg.getTime() != null)
					text_beginTime.setText(dateDlg.getTime());
			}
		});
		text_beginTime.setBounds(136, 52, 118, 17);

		text_endTime = new Text(group_option, SWT.BORDER);
		text_endTime.addMouseListener(new MouseAdapter() {
			public void mouseDown(final MouseEvent e) {
				Point point = MouseInfo.getPointerInfo().getLocation();
				DateDialog dateDlg = new DateDialog(getShell(), point);
				dateDlg.open();
				if(dateDlg.getTime() != null)
					text_endTime.setText(dateDlg.getTime());
			}
		});
		text_endTime.setBounds(324, 52, 115, 17);

		final CLabel label_beginTime = new CLabel(group_option, SWT.NONE);
		label_beginTime.addMouseListener(new MouseAdapter() {
			public void mouseDown(final MouseEvent e) {
				Point point = MouseInfo.getPointerInfo().getLocation();
				DateDialog dateDlg = new DateDialog(getShell(), point);
				dateDlg.open();
				if(dateDlg.getTime() != null)
					text_beginTime.setText(dateDlg.getTime());
			}
		});
		label_beginTime.setImage(SWTResourceManager.getImage("img/table.gif"));
		label_beginTime.setBounds(260, 55, 26, 14);

		final CLabel label_endTime = new CLabel(group_option, SWT.NONE);
		label_endTime.addMouseListener(new MouseAdapter() {
			public void mouseDown(final MouseEvent e) {
				Point point = MouseInfo.getPointerInfo().getLocation();
				DateDialog dateDlg = new DateDialog(getShell(), point);
				dateDlg.open();
				if(dateDlg.getTime() != null)
					text_endTime.setText(dateDlg.getTime());
			}
		});
		label_endTime.setImage(SWTResourceManager.getImage("img/table.gif"));
		label_endTime.setBounds(445, 55, 26, 14);

		final Button button_gotoMonitor = new Button(group_option, SWT.NONE);
		button_gotoMonitor.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(MainWindow.monitorWin == null || MainWindow.monitorWin.shell.isDisposed()){
					MainWindow.monitorWin = new MonitorWindow();
					MainWindow.monitorWin.open(MainWindow.user);
				}
				else{
					MainWindow.monitorWin.shell.setVisible(true);
					MainWindow.monitorWin.shell.setActive();
					MainWindow.monitorWin.shell.setMinimized(false);
				}
			}
		});
		button_gotoMonitor.setText("进入实时监控");
		button_gotoMonitor.setImage(SWTResourceManager.getImage("img/monitor(2).png"));
		button_gotoMonitor.setBounds(595, 33, 118, 36);
		
		MainWindow.lookupWarningResultFolder = new CTabFolder(this, SWT.BORDER | SWT.CLOSE);
		MainWindow.lookupWarningResultFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		MainWindow.lookupWarningResultFolder.setMinimumCharacters(10);
	}

	public void loadRules(){
		combo_rule.setItems(MainWindow.rp.getAllRulenamesAsArray());
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	private void onSearch(){
		String ruleName = combo_rule.getText(),
		       begin = text_beginTime.getText(),
		       end = text_endTime.getText();
		if(ruleName.trim().equals("")){
			MessageBox box = new MessageBox(getShell(), SWT.ICON_WARNING);
			box.setText("提示");
			box.setMessage("请先选择规则！");
			box.open();
			return;
		}
		else if(!begin.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")){
			MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR);
			box.setText("错误");
			box.setMessage("开始时间格式错误！");
			box.open();
			return;
		}
		else if(!end.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")){
			MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR);
			box.setText("错误");
			box.setMessage("结束时间格式错误！");
			box.open();
			return;
		}
		else if(end.compareTo(begin) < 0){
			MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR);
			box.setText("错误");
			box.setMessage("错误的时间区间！");
			box.open();
			return;
		}
		new LookupWarningResultThread(ruleName, begin, end).start();
	}
}
