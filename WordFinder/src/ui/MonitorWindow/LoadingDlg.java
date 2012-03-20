package ui.MonitorWindow;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import com.swtdesigner.SWTResourceManager;

public class LoadingDlg extends Dialog {

	protected Object result;
	protected Shell shell;
	
	private ProgressBar progressBar;
	private Label label_status;

	public Shell getShell() {
		return shell;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public Label getLabel_status() {
		return label_status;
	}
	
	/**
	 * Create the dialog
	 * @param parent
	 * @param style
	 */
	public LoadingDlg(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Create the dialog
	 * @param parent
	 */
	public LoadingDlg(Shell parent) {
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
		shell = new Shell(getParent(), SWT.BORDER);
		shell.setSize(486, 234);
		//设置窗口居中显示
		Rectangle bounds = shell.getMonitor().getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width)/2;
		int y = bounds.y + (bounds.height - rect.height)/2;
		shell.setLocation(x, y);
		shell.setText("实时监控数据载入界面");

		progressBar = new ProgressBar(shell, SWT.NONE);
		progressBar.setBounds(40, 178, 398, 17);

		final Label label_saperator = new Label(shell, SWT.HORIZONTAL | SWT.SEPARATOR);
		label_saperator.setBounds(10, 158, 460, 14);

		final CLabel label_img = new CLabel(shell, SWT.NONE);
		label_img.setBounds(143, 22, 100, 84);
		label_img.setImage(SWTResourceManager.getImage("img/monitor.png"));

		final Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("宋体", 11, SWT.BOLD));
		label.setText("正在载入预警数据……");
		label.setBounds(143, 131, 189, 17);

		final Label wordfinderLabel = new Label(shell, SWT.NONE);
		wordfinderLabel.setFont(SWTResourceManager.getFont("宋体", 12, SWT.BOLD));
		wordfinderLabel.setText("实时监控");
		wordfinderLabel.setBounds(249, 56, 92, 23);
		
		label_status = new Label(shell, SWT.NONE);
		label_status.setBounds(332, 210, 138, 14);
		//
	}
}
