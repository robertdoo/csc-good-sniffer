package ui.MonitorWindow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.*;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import com.swtdesigner.SWTResourceManager;

import dataStruct.WarningResult;

public class WarningPanel extends Shell {

	private final Shell shell = this;
	private MonitorWindow win;
	private Link link_title, link_url, link_time, link_rule, link_keyword;
	private Label label_count;
	private Button button_needWarning;
	
	private boolean flashOn = false;
	private int count = 0;
	
	private final String SOUND_NAME = "sounds/alarm.wav";

	public boolean isFlashOn() {
		return flashOn;
	}

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			WarningPanel shell = new WarningPanel(display, null);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell
	 * @param display
	 * @param style
	 */
	public WarningPanel(Display display, MonitorWindow win) {
		super(display, SWT.CLOSE);
		this.win = win;
		createContents();
		setVisible(false);
		addShellListener(new ShellAdapter() {
			public void shellClosed(final ShellEvent e) {
				setVisible(false);
				e.doit = false;
			}
		});
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		setText("新的敏感信息");
		setImage(SWTResourceManager.getImage("img/warning.png"));
		//设置窗口大小和位置
		Rectangle bounds = this.getMonitor().getBounds();
		setSize(413, 283);
		Rectangle rect = this.getBounds();
		int x = bounds.width - rect.width - 5;
		int y = bounds.height - rect.height - 25;
		setLocation(x, y);
		OS.SetWindowPos(this.handle, OS.HWND_TOPMOST, x, y, 413, 292, SWT.NULL); 

		final Composite composite = new Composite(this, SWT.NONE);
		composite.setBounds(10, 10, 385, 239);

		final Label label_title = new Label(composite, SWT.NONE);
		label_title.setFont(SWTResourceManager.getFont("", 11, SWT.BOLD));
		label_title.setText("标题：");
		label_title.setBounds(8, 87, 56, 14);

		link_title = new Link(composite, SWT.NONE);
		link_title.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				win.shell.setVisible(true);
				win.shell.setActive();
				win.shell.setMinimized(false);
				win.stackLayout.topControl = win.warningFolder;
				win.stackComp.layout();
			}
		});
		link_title.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		link_title.setBounds(68, 87, 307, 14);

		link_url = new Link(composite, SWT.NONE);
		link_url.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				try {
					java.net.URI uri=new java.net.URI(
							link_url.getText().replaceAll("<a>", "").replaceAll("</a>", ""));
					java.awt.Desktop.getDesktop().browse(uri);
				} catch (IOException e1) {
					System.err.println("打开图片来源网站时出现异常~！");
				} catch (URISyntaxException urie) {
					System.err.println("打开图片来源网站时出现异常~！");
				}
			}
		});
		link_url.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		link_url.setBounds(68, 112, 307, 16);

		final Label label_url = new Label(composite, SWT.NONE);
		label_url.setFont(SWTResourceManager.getFont("", 11, SWT.BOLD));
		label_url.setText("网址：");
		label_url.setBounds(8, 112, 56, 14);

		final Label label_time = new Label(composite, SWT.NONE);
		label_time.setFont(SWTResourceManager.getFont("", 11, SWT.BOLD));
		label_time.setText("时间：");
		label_time.setBounds(8, 57, 56, 14);

		link_time = new Link(composite, SWT.NONE);
		link_time.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		link_time.setBounds(68, 57, 307, 14);

		final Label label_rule = new Label(composite, SWT.NONE);
		label_rule.setFont(SWTResourceManager.getFont("", 11, SWT.BOLD));
		label_rule.setText("规则：");
		label_rule.setBounds(8, 137, 56, 14);

		final Label label_keyword = new Label(composite, SWT.NONE);
		label_keyword.setFont(SWTResourceManager.getFont("", 11, SWT.BOLD));
		label_keyword.setText("关键词：");
		label_keyword.setBounds(8, 165, 56, 14);

		link_rule = new Link(composite, SWT.NONE);
		link_rule.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		link_rule.setBounds(68, 137, 307, 14);

		link_keyword = new Link(composite, SWT.NONE);
		link_keyword.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		link_keyword.setBounds(70, 165, 305, 14);

		final Label label = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		label.setText("Label");
		label.setBounds(8, 185, 367, 14);

		final Button button_open = new Button(composite, SWT.NONE);
		button_open.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				win.shell.setVisible(true);
				win.shell.setActive();
				win.shell.setMinimized(false);
				win.stackLayout.topControl = win.warningFolder;
				win.stackComp.layout();
			}
		});
		button_open.setText("打开主窗口");
		button_open.setBounds(203, 205, 83, 24);

		final Button button_close = new Button(composite, SWT.NONE);
		button_close.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				setVisible(false);
			}
		});
		button_close.setText("忽略");
		button_close.setBounds(292, 205, 83, 24);

		final Label label_1 = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		label_1.setText("Label");
		label_1.setBounds(8, 34, 367, 14);

		final Label label_2 = new Label(composite, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label_2.setText("有");
		label_2.setBounds(30, 10, 34, 21);

		label_count = new Label(composite, SWT.CENTER);
		label_count.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_count.setFont(SWTResourceManager.getFont("黑体", 12, SWT.BOLD));
		label_count.setText("0");
		label_count.setBounds(68, 9, 47, 19);

		final Label label_4 = new Label(composite, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label_4.setText("条敏感信息需要处理，以下是第一条。");
		label_4.setBounds(121, 10, 254, 19);

		button_needWarning = new Button(composite, SWT.CHECK);
		button_needWarning.setText("不再提醒");
		button_needWarning.setBounds(8, 205, 85, 16);
	}
	
	public void warning(WarningResult result)
	{
		count = 0;
		for(WarningResult wr : win.warnResults)
			if(wr.isNew())
				count++;
		label_count.setText(String.valueOf(count));
		link_time.setText(result.getWarningTime());
		link_title.setText("<a>"+result.getTitle()+"</a>");
		link_url.setText("<a>"+result.getUrl()+"</a>");
		link_rule.setText(result.getRuleName());
		link_keyword.setText(result.getKeyword());
		
		if(!button_needWarning.getSelection()){
			setVisible(true);
			//Display.getDefault().beep();
			playSound();
			flash();
		}
		
	}
	
	/**
	 * 播放声音
	 * @param Filename
	 */
	public void playSound() { 
		File file = null;
		try{ 
			file = new File(SOUND_NAME);
			InputStream in = new FileInputStream(file);//FIlename 是你加载的声音文件如(“game.wav”) 
			AudioStream as = new AudioStream(in); 
			AudioPlayer.player.start(as);//用静态成员player.start播放音乐 
		} catch(FileNotFoundException e){ 
			System.out.print("没有找到" + file.getAbsolutePath()); 
		} catch(IOException e){ 
			System.out.print("播放报警声音时IO出现异常!");
		}
	}
	
	/**
	 * 闪动窗口
	 */
	public void flash(){
		if(!flashOn)
			new Thread(new flashRunnable()).start();
	}
	
	//闪动的线程
	class flashRunnable implements Runnable {
		Display display = Display.getDefault();
		
		public void run(){
			flashOn = true;
			display.syncExec(new Runnable(){
				public void run(){
					shell.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				}
			});
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			display.syncExec(new Runnable(){
				public void run(){
					shell.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				}
			});
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			display.syncExec(new Runnable(){
				public void run(){
					shell.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				}
			});
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			display.syncExec(new Runnable(){
				public void run(){
					shell.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				}
			});
			flashOn = false;
		}
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
