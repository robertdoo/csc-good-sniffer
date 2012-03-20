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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ui.MainWindow.MainWindow;

public class ModifyCategoryDlg extends Dialog {

	private Text text;
	private String categoryName;
	/**
	 * Create the dialog
	 * @param parentShell
	 */
	public ModifyCategoryDlg(Shell parentShell, String categoryName) {
		super(parentShell);
		this.categoryName = categoryName;
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
		label.setText("输入新名称：");
		label.setBounds(10, 26, 87, 14);

		text = new Text(comp, SWT.BORDER);
		text.setBounds(103, 23, 228, 25);
		text.setText(categoryName);
		text.selectAll();
		text.setFocus();
		
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
		return new Point(400, 179);
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("修改类别名称");
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
			String name = text.getText();
			if(name.equals(categoryName)){
				close();
			}
			else if(MainWindow.ucp.isCategoryExisted(name)){
				MessageBox box = new MessageBox(getParentShell(), SWT.ICON_INFORMATION);
				box.setText("提示");
				box.setMessage("类别" + name + "已存在！不能存在重复类别。");
				box.open();
				text.selectAll();
				text.setFocus();
			}
			else{
				MainWindow.ucp.updateCategoryName(name, categoryName);
				MainWindow.eap.updateEntryAddressCategory(name, categoryName);
				close();
			}
		}
	}
}
