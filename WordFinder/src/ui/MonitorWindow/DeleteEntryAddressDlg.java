package ui.MonitorWindow;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import ui.MainWindow.MainWindow;

import dataStruct.EntryAddress;

public class DeleteEntryAddressDlg extends Dialog {

	private String url;
	private EntryAddress entry;
	
	public DeleteEntryAddressDlg(Shell parentShell, String url) {
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
		label.setBounds(27, 29, 433, 14);
		label.setText("确定删除入口地址”" + entry.getName()
				+ "“（" + entry.getUrl() + "）吗？");
		
		return container;
	}

	/**
	 * Create contents of the button bar
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定",
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				"取消", false);
	}

	/**
	 * Return the initial size of the dialog
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(496, 179);
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("删除入口地址");
		//设置窗口居中显示
		Rectangle bounds = newShell.getMonitor().getBounds();
		Rectangle rect = newShell.getBounds();
		int x = bounds.x + (bounds.width - rect.width)/2;
		int y = bounds.y + (bounds.height - rect.height)/2;
		newShell.setLocation(x, y);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.CANCEL_ID == buttonId)
			close();
		else {
			MainWindow.eap.deleteEntryAddress(url);
			close();
		}
	}
}
