package ui.MonitorWindow;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import dataStruct.EntryAddress;

import ui.MainWindow.MainWindow;

public class ModifyEntryAddressDlg extends Dialog {

	private Text text_1;
	private Text text;
	private Combo combo;
	private String url;
	private EntryAddress entry;
	
	public ModifyEntryAddressDlg(Shell parentShell, String url) {
		super(parentShell);
		this.url = url;
		entry = MainWindow.eap.getEntryAddressByUrl(url);
	}

	/**
	 * Create contents of the dialog
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		
		Composite comp = new Composite(container, SWT.NONE);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Label label = new Label(comp, SWT.NONE);
		label.setText("所属分类：");
		label.setBounds(32, 32, 80, 14);

		final Label label_1 = new Label(comp, SWT.NONE);
		label_1.setText("名称：");
		label_1.setBounds(32, 75, 80, 14);

		final Label label_2 = new Label(comp, SWT.NONE);
		label_2.setText("链接：");
		label_2.setBounds(32, 119, 80, 14);

		combo = new Combo(comp, SWT.READ_ONLY);
		combo.setBounds(118, 29, 134, 22);
		combo.setItems(MainWindow.ucp.getAllCategorynamesAsArray());
		combo.select(combo.indexOf(entry.getCategory()));

		text = new Text(comp, SWT.BORDER);
		text.setBounds(118, 72, 134, 25);
		text.setText(entry.getName());

		text_1 = new Text(comp, SWT.BORDER);
		text_1.setEnabled(false);
		text_1.setBounds(118, 116, 279, 25);
		text_1.setText(entry.getUrl());
		
		
		return container;
	}

	/**
	 * Create contents of the button bar
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	/**
	 * Return the initial size of the dialog
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(492, 265);
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("修改入口地址");
		//设置窗口居中显示
		Rectangle bounds = newShell.getMonitor().getBounds();
		Rectangle rect = newShell.getBounds();
		int x = bounds.x + (bounds.width - rect.width)/2;
		int y = bounds.y + (bounds.height - rect.height)/2;
		newShell.setLocation(x, y);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if(IDialogConstants.CANCEL_ID == buttonId)
			close();
		else{
			String category = combo.getText();
			String name = text.getText();
			if(entry.getCategory().equals(category) && entry.getName().equals(name)){
				close();
			}
			else if(MainWindow.eap.isNameExisted(category, name)){
				MessageBox box = new MessageBox(getParentShell(), SWT.ICON_INFORMATION);
				box.setText("提示");
				box.setMessage("入口地址”" + name + "“在分类”" + category + "“中已存在！");
				box.open();
				text.selectAll();
				text.setFocus();
			}
			else{
				MainWindow.eap.updateEntryAddress(url, name, category);
				close();
			}
		}
	}
}
