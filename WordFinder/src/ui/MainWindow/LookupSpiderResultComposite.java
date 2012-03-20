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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import com.swtdesigner.SWTResourceManager;

import searchUtility.LookupSpiderResultThread;

public class LookupSpiderResultComposite extends Composite {

	private Text text_beginTime, text_endTime;
	private Combo combo_rule;
	
	public LookupSpiderResultComposite(Composite parent) {
		super(parent, SWT.BORDER);
		createContent();
	}
	
	private void createContent() {
		this.setLayout(new GridLayout());
		
		Group group_option = new Group(this, SWT.BORDER);
		group_option.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group_option.setText("��ѯ����");

		final Label label = new Label(group_option, SWT.NONE);
		label.setText("ѡ�����");
		label.setBounds(179, 23, 65, 14);

		final Label label_1 = new Label(group_option, SWT.NONE);
		label_1.setText("ѡ��ʱ�䷶Χ��");
		label_1.setBounds(71, 55, 92, 14);

		combo_rule = new Combo(group_option, SWT.READ_ONLY);
		combo_rule.setBounds(250, 20, 113, 22);

		final Label label_2 = new Label(group_option, SWT.NONE);
		label_2.setText("��");
		label_2.setBounds(169, 55, 26, 14);

		final Label label_3 = new Label(group_option, SWT.NONE);
		label_3.setText("��");
		label_3.setBounds(357, 55, 26, 14);

		final Button button_lookup = new Button(group_option, SWT.NONE);
		button_lookup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onSearch();
			}
		});
		button_lookup.setText("��ѯ");
		button_lookup.setImage(SWTResourceManager.getImage("img/lookup(1).png"));
		button_lookup.setBounds(553, 33, 72, 36);

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
		text_beginTime.setBounds(201, 52, 118, 17);

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
		text_endTime.setBounds(389, 52, 115, 17);

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
		label_beginTime.setBounds(325, 55, 26, 14);

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
		label_endTime.setBounds(510, 55, 26, 14);
		
		MainWindow.lookupSpiderResultFolder = new CTabFolder(this, SWT.BORDER | SWT.CLOSE);
		MainWindow.lookupSpiderResultFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		MainWindow.lookupSpiderResultFolder.setMinimumCharacters(10);
		MainWindow.lookupSpiderResultFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				combo_rule.select(combo_rule.indexOf(MainWindow.lookupSpiderResultFolder.getSelection().getText().replace("��̳��������", "")));
			}
		});
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
			box.setText("��ʾ");
			box.setMessage("����ѡ�����");
			box.open();
			return;
		}
		else if(!begin.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")){
			MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR);
			box.setText("����");
			box.setMessage("��ʼʱ���ʽ����");
			box.open();
			return;
		}
		else if(!end.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")){
			MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR);
			box.setText("����");
			box.setMessage("����ʱ���ʽ����");
			box.open();
			return;
		}
		else if(end.compareTo(begin) < 0){
			MessageBox box = new MessageBox(getShell(), SWT.ICON_ERROR);
			box.setText("����");
			box.setMessage("�����ʱ�����䣡");
			box.open();
			return;
		}
		new LookupSpiderResultThread(ruleName, begin, end).start();
	}

}
