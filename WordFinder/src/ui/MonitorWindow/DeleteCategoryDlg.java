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

public class DeleteCategoryDlg extends Dialog {

	private String categoryName;
	
	public DeleteCategoryDlg(Shell parentShell, String categoryName) {
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
		label.setText("确定删除分类”" + categoryName + "“，以及其下的所有入口地址吗？");
		label.setBounds(22, 25, 331, 14);
		
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
		return new Point(416, 175);
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("删除类别");
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
		else {
			MainWindow.ucp.deleteCategory(categoryName);
			close();
		}
	}
}
