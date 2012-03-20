package ui.MainWindow;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.swtdesigner.SWTResourceManager;

import dataStruct.ImageSearchResult;

import titleRecgnize.Recognizer;

public class ImageSearchComposite extends Composite {

	private Tree resultTree;
	private TreeItem itemWords, itemTitle, itemSite, itemLocalPath;
	private CLabel imgLabel, StatusMsgLabel;
	private ToolItem openFolderItem;
	
	public ScanThread scanThread = new ScanThread();
	
	private Recognizer rec = new Recognizer();
	
	public ImageSearchComposite(Composite parent) {
		super(parent, SWT.BORDER);
		createContent();
	}

	private void createContent() {
		this.setLayout(new GridLayout());
		
		Composite topComp = new Composite(this, SWT.NONE);
		topComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		topComp.setLayout(new FillLayout());
		
		ToolBar toolBar = new ToolBar(topComp, SWT.FLAT | SWT.RIGHT);
		
		/*
		scanItem = new ToolItem(toolBar, SWT.NONE);
		scanItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				
			}
		});
		scanItem.setText("搜索");
		scanItem.setImage(SWTResourceManager.getImage("img/spectacles.png"));
		*/
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		openFolderItem = new ToolItem(toolBar, SWT.NONE);
		openFolderItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onOpenFolder();
			}
		});
		openFolderItem.setText("打开图片文件夹");
		openFolderItem.setImage(SWTResourceManager.getImage("img/folder.png"));
		
		toolBar.pack();
		
		SashForm sash = new SashForm(this, SWT.BORDER);
		sash.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		CTabFolder tabFolder = new CTabFolder(sash, SWT.BORDER);
		tabFolder.setTabHeight(18);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tabFolder.setSimple(false);
		
		CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
		tabItem.setText("图片搜索结果");	
		resultTree = new Tree(tabFolder, SWT.BORDER | SWT.VIRTUAL);
		resultTree.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(final SelectionEvent e) {
				onSelectTreeItem();
			}
		});
		tabItem.setControl(resultTree);
		tabFolder.setSelection(tabItem);
		
		ScrolledComposite imageComp = new ScrolledComposite(sash, SWT.H_SCROLL | SWT.V_SCROLL);
		
		imgLabel = new CLabel(imageComp, SWT.NONE);
		imgLabel.setSize(600, 800);
		imgLabel.setText("没有预览");
		imgLabel.setAlignment(SWT.CENTER);
		imageComp.setContent(imgLabel);
		
		sash.setWeights(new int[]{40, 60});
		
		StatusMsgLabel = new CLabel(this, SWT.NONE);
		StatusMsgLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
	
	public void loadImageResult() {
		for(ImageSearchResult result : MainWindow.ip.getAllExaminedResult()){
			itemWords = new TreeItem(resultTree, SWT.NONE);
			itemWords.setText("图片标题：" + result.getExaminedResult());
			itemTitle = new TreeItem(itemWords, SWT.NONE);
			itemTitle.setText("图片名称：" + result.getTitle());
			itemSite = new TreeItem(itemWords, SWT.NONE);
			itemSite.setText("来源网址：" + result.getSite());
			itemLocalPath = new TreeItem(itemWords, SWT.NONE);
			itemLocalPath.setText("本地预览：" + result.getLocalPath());
		}
	}
	
	private void onOpenFolder() {
		try {		
			Runtime.getRuntime().exec("Explorer.exe " + 
					System.getProperty("user.dir") + "\\Data\\data1\\img\\");
		} catch (IOException e) {
			System.err.println("打开图片所在文件夹时出现异常~！");
		}
	}
	
	private void onSelectTreeItem() {
		TreeItem item = resultTree.getSelection()[0];
		String text = item.getText();
		Image img;
		if(text.replaceAll("：.*", "").equals("图片名称")){
			img = SWTResourceManager.getImage(System.getProperty("user.dir")
					+ "\\Data\\data1\\img\\"
					+ text.replaceAll(".*：", ""));
			imgLabel.setSize(img.getImageData().width+10, img.getImageData().height+10);
			imgLabel.setText("");
			imgLabel.setImage(img);
		}
		else if(text.replaceAll("：.*", "").equals("来源网址")){
			try {
				java.net.URI uri=new java.net.URI(text.replaceAll(".*：", ""));
				java.awt.Desktop.getDesktop().browse(uri);
			} catch (IOException e1) {
				System.err.println("打开图片来源网站时出现异常~！");
			} catch (URISyntaxException urie) {
				System.err.println("打开图片来源网站时出现异常~！");
			}
		}
		else if(text.replaceAll("：.*", "").equals("本地预览")){
			try {
				Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler file:" + text.replaceAll(".*：", ""));
			} catch (IOException e2) {
				System.err.println("打开图片本地预览时出现异常~！");
			}
		}
	}
	
	/**
	 * 检索图片结果的线程
	 */
	public class ScanThread extends Thread{
		private Display display = Display.getDefault();
		private LinkedList<ImageSearchResult> resultList;
		private ImageSearchResult result;
		private boolean stopFlag = false;
		
		public boolean isStopFlag() {
			return stopFlag;
		}

		public synchronized void setStopFlag(boolean stopFlag) {
			this.stopFlag = stopFlag;
			if(stopFlag){
				System.out.println("图片处理线程停止！");
				notify();
			}
		}

		private void doSearch() {
			resultList = MainWindow.ip.getAllUnexaminedResult();

			for(int i=0;i<resultList.size();i++){
				result = resultList.get(i);
				display.asyncExec(new Runnable(){
					public void run(){
						StatusMsgLabel.setText("共有" 
								+ resultList.size()
								+ "条图片记录待处理，当前正在处理第" 
								+  resultList.indexOf(result)
								+ "条……");
					}
				});
				
				String examinedResult = "xxxxx";
				try{
					examinedResult = rec.recognizeImage(result.getLocalPath());
				} catch(Exception e) {
					examinedResult = "非红头文件！";
					System.err.println("识别图片" + result.getLocalPath() + "时出现异常！");
					System.err.println(e.toString());
				}
					
				result.setExaminedResult(examinedResult);
				MainWindow.ip.updateExaminedResult(result.getLocalPath()
						, result.getExaminedResult());
				
				if(!result.getExaminedResult().equals("非红头文件！")){
					display.asyncExec(new Runnable(){
						public void run(){
							itemWords = new TreeItem(resultTree, SWT.NONE);
							itemWords.setText("图片标题：" + result.getExaminedResult());
							itemTitle = new TreeItem(itemWords, SWT.NONE);
							itemTitle.setText("图片名称：" + result.getTitle());
							itemSite = new TreeItem(itemWords, SWT.NONE);
							itemSite.setText("来源网址：" + result.getSite());
							itemLocalPath = new TreeItem(itemWords, SWT.NONE);
							itemLocalPath.setText("本地预览：" + result.getLocalPath());
							
						}
					});
				}
			}
			display.asyncExec(new Runnable(){
				public void run(){
					StatusMsgLabel.setText("处理完毕！");
				}
			});
		}
		
		public synchronized void run() {
			System.out.println("图片处理线程开始运行……");
			while(!stopFlag){
				doSearch();
				try {
					wait(300000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
