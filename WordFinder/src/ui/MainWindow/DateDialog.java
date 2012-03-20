package ui.MainWindow;

import java.awt.Point;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;

public class DateDialog extends Dialog {

	private Point mousePosition;
	private DateTime dateTime;
	private String time;

	public DateDialog(Shell parentShell, Point mousePosition) {
		super(parentShell);
		this.mousePosition = mousePosition;
	}

	public String getTime() {
		return time;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		Rectangle bounds = newShell.getMonitor().getBounds();
		if((bounds.width - mousePosition.getX()) > 290)
			newShell.setBounds(mousePosition.x, mousePosition.y, 290, 250);
		else
			newShell.setBounds(mousePosition.x - 290, mousePosition.y, 290, 250);
	}

	/**
	 * Create contents of the dialog
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout());
		
		dateTime = new DateTime(container, SWT.CALENDAR);
		dateTime.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		return container;
	}

	/**
	 * Create contents of the button bar
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "È·¶¨",true);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		time = dateTime.getYear() + "-";
		if((dateTime.getMonth()+1)/10 == 0)
			time += "0";
		time += (dateTime.getMonth()+1) + "-";
		if(dateTime.getDay()/10 == 0)
			time += "0";
		time += dateTime.getDay();
		close();
	}

}
