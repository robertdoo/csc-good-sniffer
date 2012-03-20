package ui.MonitorWindow;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.swtdesigner.SWTResourceManager;

public class AboutDlg extends Dialog {

	protected Object result;
	protected Shell shell;

	/**
	 * Create the dialog
	 * @param parent
	 * @param style
	 */
	public AboutDlg(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Create the dialog
	 * @param parent
	 */
	public AboutDlg(Shell parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * Open the dialog
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}

	/**
	 * Create contents of the dialog
	 */
	protected void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setSize(394, 261);
		//���ô��ھ�����ʾ
		Rectangle bounds = shell.getMonitor().getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width)/2;
		int y = bounds.y + (bounds.height - rect.height)/2;
		shell.setLocation(x, y);
		shell.setText("�����ض���վʵʱ���ϵͳ");
		shell.setImage(SWTResourceManager.getImage("img/monitor.png"));

		final Label label_separator = new Label(shell, SWT.HORIZONTAL | SWT.SEPARATOR);
		label_separator.setBounds(10, 170, 368, 12);

		final Label label_logo = new Label(shell, SWT.NONE);
		label_logo.setBounds(28, 29, 78, 76);
		label_logo.setImage(SWTResourceManager.getImage("img/monitor(3).png"));

		final Label label_specify = new Label(shell, SWT.NONE);
		label_specify.setText("��ϵͳ����ض���վ��ʵʱ���ֵ�������Ϣ���ܹ���ʱȫ��ؽ���\n��ȡ�ͱ�����ʵ�ֶ���վ���ݵļ�ء�");
		label_specify.setBounds(28, 124, 350, 40);

		final Button button_ok = new Button(shell, SWT.NONE);
		button_ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				shell.dispose();
			}
		});
		button_ok.setText("ȷ��");
		button_ok.setBounds(258, 188, 102, 27);

		final Label label_name = new Label(shell, SWT.NONE);
		label_name.setFont(SWTResourceManager.getFont("����", 10, SWT.NONE));
		label_name.setText("�ض���վʵʱ���ϵͳ ");
		label_name.setBounds(139, 28, 221, 25);

		final Label label_version = new Label(shell, SWT.NONE);
		label_version.setFont(SWTResourceManager.getFont("����", 10, SWT.NONE));
		label_version.setText("�汾��1.0 ");
		label_version.setBounds(139, 59, 221, 21);

		final Label label_copyright = new Label(shell, SWT.NONE);
		label_copyright.setFont(SWTResourceManager.getFont("����", 10, SWT.NONE));
		label_copyright.setText("��Ȩ���У��Ͼ�����ѧ�����ѧԺ");
		label_copyright.setBounds(139, 86, 221, 20);
		
	}

}
