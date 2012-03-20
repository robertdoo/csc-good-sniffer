package ui.MonitorWindow;

import java.io.File;
import java.util.LinkedList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.swtdesigner.SWTResourceManager;

import dataStruct.EntryAddress;
import dataStruct.Rule;
import dataStruct.WarningResult;
import database.LogProcess;
import searchUtility.CurrentTime;
import searchUtility.MonitorSearchThread;
import spider.CreateDir;
import spider.Queue;
import spider.ThreadPoolManager;
import ui.MainWindow.MainWindow;
import Filter.BloomFilter;

public class MonitorWindow {
	
	private Text text;
	public Button button_startSpider, button_stopSpider, button_save;
	private Combo combo_entryCategory, combo_sensativity;
	private List list_des;
	private Tree tree_entry, tree_src;
	private Text text_entryUrl, text_entryName, text_taskInfo;
	private Table table_entry, checkRuleTable;
	private Label label_spiderStatus;
	private CLabel label_spiderImg;
	private String htmlHead, html, htmlEnd;
	private Menu menuBar, menu_treeEntry;//主菜单
	private WarningPanel warningPanel;//预警信息面板
	private CLabel StatusMsgLabel; //显示状态信息的label
	
	public Shell shell;
	public TrayItem trayItem;         //系统托盘
	public Composite stackComp;
	public StackLayout stackLayout;
	public CTabFolder addEntryFolder, spiderFolder, monitorRuleFolder,
	                  warningFolder, warningHistoryFolder,
	                  statisticsFolder;//功能板块
	
	//预警信息和预警记录用到的控件/////////////////////////////////////////////////////////// 
	private TableViewer monitorInfoTable, monitorHistoryTable; 
	private CheckboxTableViewer checkMonitorInfoTable, checkMonitorHistoryTable; 
	private Browser resultInfoBrowser, resultHistoryBrowser;
	public LinkedList<WarningResult> warnResults = new LinkedList<WarningResult>();
	private LinkedList<WarningResult> historyResults = new LinkedList<WarningResult>();
	private WarningResult currentResult;
	private final String cssPath = new File("htmlStyle.css").getAbsolutePath();
	
	private class MyContentPrivider implements IStructuredContentProvider{
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object arg0) {
			if(arg0 instanceof LinkedList){
				return ((LinkedList)arg0).toArray();
			}
			return null;
		}
		public void dispose() {
		}
		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}
	}
	private class MyLabelProvider implements
	             ITableLabelProvider, ITableFontProvider, ITableColorProvider 
	{
		FontRegistry registry = new FontRegistry(); 
		public Image getColumnImage(Object element, int columnIndex) {   
			if(columnIndex == 0 && ((WarningResult)element).isNew())
				return SWTResourceManager.getImage("img/warning1.png");
	        return null;   
	    }   
	    public String getColumnText(Object element, int columnIndex) {   
	        if(columnIndex == 0)
	        	return ((WarningResult)element).getTitle();
	        else if(columnIndex == 1)
	        	return ((WarningResult)element).getWarningTime();
	        else if(columnIndex == 2)
	        	return ((WarningResult)element).getKeyword();
	        else
	        	return "";
	    }   
	    public Font getFont(Object element, int columnIndex) {   
	    	if(((WarningResult)element).isNew())
				return SWTResourceManager.getFont("", 11, SWT.BOLD);
	    	else
	    		return null;
	    }   
	    public Color getBackground(Object element, int columnIndex) {   
	    	if(((WarningResult)element).isNew())
				return null;
	    	else
	    		return null;
	    }   
	    public Color getForeground(Object element, int columnIndex) {   
	    	if(((WarningResult)element).isNew())
				return SWTResourceManager.getColor(255, 0, 0); 
	    	else
	    		return null;
	    }   
		public void addListener(ILabelProviderListener arg0) {			
		}
		public void dispose() {
		}
		public boolean isLabelProperty(Object arg0, String arg1) {
			return false;
		}
		public void removeListener(ILabelProviderListener arg0) {
		}	
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	//爬虫用到的变量///////////////////////////////////////////////////////////////////////
	int count = 0;
	public String user;//当前用户
	int THREAD_NUMBER;//设置线程数目
	final int HashTableLength = 1000000;
	final static long crawlTime = 60000;
	public boolean crawlFlag = false;
	ThreadPoolManager manager;
	BloomFilter bf = null;//创建过滤器
	public static LinkedList<MonitorSearchThread> checkRuleTask 
	                               = new LinkedList<MonitorSearchThread>(); //存放所有已添加的自动搜索任务
	//////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MonitorWindow window = new MonitorWindow();
			window.open("123");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window
	 */
	public void open(String username) {
		user = username;
		Display display = Display.getDefault();
		createContents();
		initial();//初始化
		addSystemTray();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()){
			if (!display.readAndDispatch()){
				display.sleep();
			}
		}
	}

	/**
	 * 初始化工作
	 */
	protected void initial(){
		//初始化哈希表
		final LoadingDlg welcome = new LoadingDlg(shell);
		new Thread(new Runnable() {
			Display display = Display.getDefault();
			public void run() {
				display.asyncExec(new Runnable(){
					public void run(){
						welcome.getLabel_status().setText("初始化哈希表……");
					}
				});
				bf = new BloomFilter(HashTableLength);
				display.syncExec(new Runnable(){
					public void run(){
						welcome.getProgressBar().setSelection(80);
						welcome.getLabel_status().setText("载入入口地址……");
						loadStoredEntry();
					}
				});
				display.syncExec(new Runnable(){
					public void run(){
						welcome.getProgressBar().setSelection(80);
						welcome.getLabel_status().setText("载入规则……");
						loadRules();
					}
				});
				display.syncExec(new Runnable(){
					public void run(){
						welcome.getProgressBar().setSelection(80);
						welcome.getLabel_status().setText("载入预警记录……");
						loadWarningHistory();
					}
				});
				display.syncExec(new Runnable(){
					public void run(){
						welcome.getLabel_status().setText("正在启动窗口……");
						welcome.getProgressBar().setSelection(100);
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(!welcome.getShell().isDisposed())
							welcome.getShell().dispose();
					}
				});
			}
		}).start();
		welcome.open();
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents()
	{
		shell = new Shell(SWT.SHELL_TRIM);
		shell.setLayout(new FillLayout());
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(final ShellEvent e) {
				if(onExit() == 1)
					e.doit =false;
			}
			public void shellIconified(ShellEvent e) {
				shell.setVisible(false);
			}
		});
		shell.setImage(SWTResourceManager.getImage("img/monitor.png"));
		//设置窗口居中显示
		Rectangle bounds = shell.getMonitor().getBounds();
		shell.setSize(bounds.width*4/5, bounds.height*9/10);
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width)/2;
		int y = bounds.y + (bounds.height - rect.height)/2;
		shell.setLocation(x, y);
		shell.setText("实时监控");
		
		Composite container = new Composite(shell, SWT.NONE);
		container.setLayout(new GridLayout());
		
		///////////////////////////////////////////////////顶部版面////////////////////////////////////////////////////////////
		createTopSurface(container);
		/////////////////////////////////////////////////顶部版面///////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////中间版面///////////////////////////////////////////////////////////
		SashForm sash_mid = new SashForm(container, SWT.HORIZONTAL);
		sash_mid.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		//中左部面板， 点选进入各个功能板块的按钮
		Composite compLeft = new Composite(sash_mid, SWT.NONE);
		compLeft.setLayout(new FillLayout());
		createOptionBlock(compLeft);
		
		//中右部面板，由堆栈管理器管理，放置各个功能板块
		stackComp = new Composite(sash_mid, SWT.NONE);
		stackLayout = new StackLayout();
		stackComp.setLayout(stackLayout);	
		//入口设置板块
		createSpiderBlock(stackComp);
		//规则设置板块
		createMonitorRuleBlock(stackComp);
		//预警信息板块
		createWarningInfoBlock(stackComp);
		//预警记录板块
		createWarningHistoryBlock(stackComp);
		//统计分析板块
		createStatisticsBlock(stackComp);
		stackLayout.topControl = spiderFolder;
		
		sash_mid.setWeights(new int[] {23, 77});
		/////////////////////////////////////////////////中间版面///////////////////////////////////////////////////////////
		
		////////////////////////////////////////////////底部版面////////////////////////////////////////////////////////////////
		createBottomSurface(container);
		////////////////////////////////////////////////底部版面////////////////////////////////////////////////////////////////
		
		////////////////////////////////////////////创建菜单////////////////////////////////////////
		createMenuBar();
		createTreeEntryMenu();
        ////////////////////////////////////////////创建菜单////////////////////////////////////////
		
		warningPanel = new WarningPanel(Display.getDefault(), this);
		
		htmlHead = "<html>\n<head>\n" +
				"<title>敏感信息</title>\n" +
				"<LINK href=\"" + cssPath + "\" rel=stylesheet type=text/css>" +
				"</head>\n<body>\n<table>";
		html = "";
		htmlEnd = "</table>\n</body>\n</html>";
		resultInfoBrowser.setText(htmlHead + html + htmlEnd);
		
	}
	
	/**
	 * 创建顶部的界面，logo标志
	 */
	private void createTopSurface(Composite container){		
		CLabel label_logo = new CLabel(container, SWT.NONE);
		label_logo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label_logo.setBackground(SWTResourceManager.getColor(255, 255, 255));
		label_logo.setAlignment(SWT.LEFT);
		label_logo.setImage(SWTResourceManager.getImage("img/logo2.jpg"));
		
	}
	
	/**
	 * 中左部界面，选项按钮
	 */
	private void createOptionBlock(Composite comp){
		Tree tree_option = new Tree(comp, SWT.BORDER);
		//tree_option.setFont(SWTResourceManager.getFont("", 12, SWT.BOLD));
		tree_option.setBackground(SWTResourceManager.getColor(255, 255, 255));
		
		TreeItem rootItem = new TreeItem(tree_option, SWT.NONE);
		rootItem.setText("实时监控");
		rootItem.setImage(SWTResourceManager.getImage("img/monitor.png"));
		
		final TreeItem item_spider = new TreeItem(rootItem, SWT.NONE);
		item_spider.setText("入口设置");
		item_spider.setImage(SWTResourceManager.getImage("img/forumSearch.png"));
		
		final TreeItem item_rule = new TreeItem(rootItem, SWT.NONE);
		item_rule.setText("规则设置");
		item_rule.setImage(SWTResourceManager.getImage("img/rule.png"));
		
		final TreeItem item_warning = new TreeItem(rootItem, SWT.NONE);
		item_warning.setText("预警信息");
		item_warning.setImage(SWTResourceManager.getImage("img/warningInfo.png"));
		
		final TreeItem item_warningHis = new TreeItem(rootItem, SWT.NONE);
		item_warningHis.setText("预警记录");
		item_warningHis.setImage(SWTResourceManager.getImage("img/history.png"));
		
		/*
		final TreeItem item_statistics = new TreeItem(rootItem, SWT.NONE);
		item_statistics.setText("统计分析");
		item_statistics.setImage(new Image(Display.getDefault(), "img/chart-pie.png"));
		*/
		
		rootItem.setExpanded(true);
		
		tree_option.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(((TreeItem)e.item).equals(item_spider)){
					stackLayout.topControl = spiderFolder;
					stackComp.layout();
				}
				else if(((TreeItem)e.item).equals(item_rule)){
					stackLayout.topControl = monitorRuleFolder;
					stackComp.layout();
				}
				else if(((TreeItem)e.item).equals(item_warning)){
					stackLayout.topControl = warningFolder;
					stackComp.layout();
				}
				else if(((TreeItem)e.item).equals(item_warningHis)){
					stackLayout.topControl = warningHistoryFolder;
					stackComp.layout();
				}
				/*
				else if(((TreeItem)e.item).equals(item_statistics)){
					stackLayout.topControl = statisticsFolder;
					stackComp.layout();
				}
				*/
			}
		});
	}
	
	/**
	 * 创建爬虫控制模块
	 */
	private void createSpiderBlock(Composite comp){
		spiderFolder = new CTabFolder(comp, SWT.NONE);
		spiderFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		spiderFolder.setSimple(false);
		spiderFolder.setTabHeight(20);
		spiderFolder.setSelectionBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		CTabItem spiderItem = new CTabItem(spiderFolder, SWT.NONE);
		spiderItem.setText("入口设置");
		Composite spiderComp = new Composite(spiderFolder, SWT.NONE);
		spiderComp.setLayout(new GridLayout());
		spiderItem.setControl(spiderComp);
		spiderFolder.setSelection(spiderItem);
		
		Composite comp1 = new Composite(spiderComp, SWT.NONE);
		comp1.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp1.setLayout(new GridLayout(5,true));
		Group group_spider = new Group(comp1, SWT.NONE);
		group_spider.setText("监控状态");
		final GridData gd_group_spider = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_group_spider.heightHint = 70;
		group_spider.setLayoutData(gd_group_spider);

		label_spiderImg = new CLabel(group_spider, SWT.NONE);
		label_spiderImg.setBounds(22, 26, 117, 106);
		label_spiderImg.setImage(SWTResourceManager.getImage("img/monitorStop.png"));

		label_spiderStatus = new Label(group_spider, SWT.NONE);
		label_spiderStatus.setText("监控已停止！");
		label_spiderStatus.setBounds(167, 64, 111, 25);

		button_startSpider = new Button(group_spider, SWT.NONE);
		button_startSpider.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onStartSpider();
			}
		});
		button_startSpider.setText("启动");
		button_startSpider.setImage(SWTResourceManager.getImage("img/startSpider.png"));
		button_startSpider.setBounds(34, 150, 73, 36);

		button_stopSpider = new Button(group_spider, SWT.NONE);
		button_stopSpider.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onStopSpider();
			}
		});
		button_stopSpider.setImage(SWTResourceManager.getImage("img/stopSpider.png"));
		button_stopSpider.setText("停止");
		button_stopSpider.setBounds(145, 150, 73, 36);
		button_stopSpider.setEnabled(false);

		final Label labelSpiderTip = new Label(group_spider, SWT.NONE);
		labelSpiderTip.setForeground(SWTResourceManager.getColor(255, 0, 0));
		labelSpiderTip.setText("入口设置完成以后，请点击启动按钮，启动监控。");
		labelSpiderTip.setBounds(22, 212, 264, 25);
		
		Group group_entry = new Group(comp1, SWT.NONE);
		group_entry.setText("正在监控的入口地址");
		final GridData gd_group_entry = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		gd_group_entry.heightHint = 70;
		group_entry.setLayoutData(gd_group_entry);	

		final Button button_removeEntry = new Button(group_entry, SWT.NONE);
		
		table_entry = new Table(group_entry, SWT.BORDER | SWT.FULL_SELECTION);
		table_entry.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(table_entry.getSelectionCount()!=0)
					button_removeEntry.setEnabled(true);
			}
		});
		table_entry.setLinesVisible(true);
		table_entry.setHeaderVisible(true);
		table_entry.setBounds(10, 23, 435, 192);
		TableColumn name = new TableColumn(table_entry, SWT.CENTER);
		name.setText("名称");
		name.setWidth(93);
		TableColumn url = new TableColumn(table_entry, SWT.CENTER);
		url.setText("链接");
		url.setWidth(335);
		
		button_removeEntry.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(button_startSpider.getEnabled() && !button_stopSpider.getEnabled()){
					table_entry.remove(table_entry.getSelectionIndices());
				}
				else{
					MessageBox box = new MessageBox(shell, SWT.ICON_WARNING);
					box.setText("非法操作");
					box.setMessage("请先停止监控，再对监控列表进行操作！");
					box.open();
				}
			}
		});
		button_removeEntry.setText("移除");
		button_removeEntry.setBounds(10, 221, 64, 24);
		button_removeEntry.setEnabled(false);
		
		Composite comp2 = new Composite(spiderComp, SWT.NONE);
		comp2.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp2.setLayout(new GridLayout(5,true));

		final Group group_newEntry = new Group(comp2, SWT.NONE);
		group_newEntry.setText("添加新入口");
		final GridData gd_group_newEntry = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_group_newEntry.heightHint = 70;
		group_newEntry.setLayoutData(gd_group_newEntry);

		final Label label_entryNmae = new Label(group_newEntry, SWT.NONE);
		label_entryNmae.setText("入口名称：");
		label_entryNmae.setBounds(10, 33, 60, 14);

		text_entryName = new Text(group_newEntry, SWT.BORDER);
		text_entryName.setBounds(76, 30, 239, 25);

		final Label label_entryUrl = new Label(group_newEntry, SWT.NONE);
		label_entryUrl.setText("链接地址：");
		label_entryUrl.setBounds(10, 115, 60, 14);

		text_entryUrl = new Text(group_newEntry, SWT.BORDER);
		text_entryUrl.setText("http://");
		text_entryUrl.setBounds(76, 112, 239, 92);

		final Button button_new = new Button(group_newEntry, SWT.NONE);
		button_new.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onNewEntry();
			}
		});
		button_new.setText("添加");
		button_new.setBounds(103, 217, 68, 24);

		final Button button_reset = new Button(group_newEntry, SWT.NONE);
		button_reset.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				text_entryName.setText("");
				text_entryUrl.setText("http://");
				combo_entryCategory.select(0);
			}
		});
		button_reset.setText("重置");
		button_reset.setBounds(10, 217, 68, 24);

		final Label label_entryCategory = new Label(group_newEntry, SWT.NONE);
		label_entryCategory.setText("所属分类：");
		label_entryCategory.setBounds(10, 73, 60, 25);

		combo_entryCategory = new Combo(group_newEntry, SWT.NONE);
		combo_entryCategory.select(0);
		combo_entryCategory.setBounds(76, 70, 113, 22);
		
		final Group group_allEntry = new Group(comp2, SWT.NONE);
		group_allEntry.setText("已建立的入口");
		final GridData gd_group_allEntry = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		gd_group_allEntry.heightHint = 70;
		group_allEntry.setLayoutData(gd_group_allEntry);

		final Button button_add = new Button(group_allEntry, SWT.NONE);
		final Button button_delete = new Button(group_allEntry, SWT.NONE);
		
		tree_entry = new Tree(group_allEntry, SWT.BORDER);
		tree_entry.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(tree_entry.getSelectionCount()!=0){
					button_add.setEnabled(true);
					button_delete.setEnabled(true);
				}
			}
		});
		tree_entry.setBounds(10, 22, 441, 198);

		button_add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onAddEntry();
			}
		});
		button_add.setText("添加到监控列表");
		button_add.setBounds(147, 226, 114, 24);
		button_add.setEnabled(false);

		button_delete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onDeleteEntry();
			}
		});
		button_delete.setText("删除入口地址");
		button_delete.setBounds(10, 226, 114, 24);
		button_delete.setEnabled(false);
	}
	
	/**
	 * 创建监控规则模块
	 */
	private void createMonitorRuleBlock(Composite comp){
		monitorRuleFolder = new CTabFolder(comp, SWT.NONE);
		monitorRuleFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		monitorRuleFolder.setSimple(false);
		monitorRuleFolder.setTabHeight(20);
		monitorRuleFolder.setSelectionBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		CTabItem monitorRuleItem = new CTabItem(monitorRuleFolder, SWT.NONE);
		monitorRuleItem.setText("规则设置");
		Composite monitorRuleComp = new Composite(monitorRuleFolder, SWT.NONE);
		monitorRuleComp.setLayout(new GridLayout());
		monitorRuleItem.setControl(monitorRuleComp);
		monitorRuleFolder.setSelection(monitorRuleItem);
		
		Composite comp1 = new Composite(monitorRuleComp, SWT.NONE);
		comp1.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp1.setLayout(new GridLayout(2, true));
		
		Composite comp11 = new Composite(comp1, SWT.NONE);
		comp11.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp11.setLayout(new GridLayout());
		
		ToolBar toolbar = new ToolBar(comp11, SWT.FLAT);
		toolbar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		ToolItem item_start = new ToolItem(toolbar, SWT.PUSH);
		item_start.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onStartTask();
				onUpdateWarningInfo();
			}
		});
		item_start.setToolTipText("开始");
		item_start.setText("开始");
		item_start.setImage(SWTResourceManager.getImage("img/startTask.png"));
		
		ToolItem item_startAll = new ToolItem(toolbar, SWT.PUSH);
		item_startAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onStartAllTask();
				onUpdateWarningInfo();
			}
		});
		item_startAll.setToolTipText("全部开始");
		item_startAll.setText("全部开始");
		item_startAll.setImage(SWTResourceManager.getImage("img/startAllTask.png"));
		
		new ToolItem(toolbar, SWT.SEPARATOR);
		
		ToolItem item_pause = new ToolItem(toolbar, SWT.PUSH);
		item_pause.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onPauseTask();
				onUpdateWarningInfo();
			}
		});
		item_pause.setToolTipText("暂停");
		item_pause.setText("暂停");
		item_pause.setImage(SWTResourceManager.getImage("img/pauseTask.png"));
		
		ToolItem item_pauseAll = new ToolItem(toolbar, SWT.PUSH);
		item_pauseAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onPauseAllTask();
				onUpdateWarningInfo();
			}
		});
		item_pauseAll.setToolTipText("全部暂停");
		item_pauseAll.setText("全部暂停");
		item_pauseAll.setImage(SWTResourceManager.getImage("img/pauseAllTask.png"));
		
		new ToolItem(toolbar, SWT.SEPARATOR);
		
		ToolItem item_delete = new ToolItem(toolbar, SWT.PUSH);
		item_delete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onDeleteTask();
				onUpdateWarningInfo();
			}
		});
		item_delete.setToolTipText("删除");
		item_delete.setText("删除");
		item_delete.setImage(SWTResourceManager.getImage("img/deleteTask.png"));
		
		ToolItem item_deleteAll = new ToolItem(toolbar, SWT.PUSH);
		item_deleteAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onDeleteAllTask();
				text_taskInfo.setText("");
			}
		});
		item_deleteAll.setToolTipText("全部删除");
		item_deleteAll.setText("全部删除");
		item_deleteAll.setImage(SWTResourceManager.getImage("img/deleteAllTask.png"));
		
		toolbar.pack();
		
		checkRuleTable = new Table(comp11, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		checkRuleTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onUpdateWarningInfo();
			}
		});
		checkRuleTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		checkRuleTable.setHeaderVisible(true);
		checkRuleTable.setLinesVisible(true);
		TableColumn tableColumnTheme = new TableColumn(checkRuleTable, SWT.LEFT);
		tableColumnTheme.setWidth(80);
		tableColumnTheme.setText("规则");
		TableColumn tableColumnStartTime = new TableColumn(checkRuleTable, SWT.LEFT);
		tableColumnStartTime.setWidth(130);
		tableColumnStartTime.setText("任务建立时间");
		TableColumn tableColumnSensativity = new TableColumn(checkRuleTable, SWT.LEFT);
		tableColumnSensativity.setWidth(55);
		tableColumnSensativity.setText("预警值");
		TableColumn tableColumnStatus = new TableColumn(checkRuleTable, SWT.LEFT);
		tableColumnStatus.setWidth(65);
		tableColumnStatus.setText("任务状态");
		
		final Group comp12 = new Group(comp1, SWT.NONE);
		comp12.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp12.setLayout(new GridLayout());
		comp12.setText("任务信息");
		text_taskInfo = new Text(comp12, SWT.CENTER | SWT.WRAP);
		text_taskInfo.setLayoutData(new GridData(GridData.FILL_BOTH));
		text_taskInfo.setText("任务信息");
		text_taskInfo.setEditable(false);
		
		final Group comp2 = new Group(monitorRuleComp, SWT.NONE);
		final GridData gd_comp2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_comp2.heightHint = 120;
		comp2.setLayoutData(gd_comp2);
		comp2.setText("添加新任务");

		final Group group_selectRule = new Group(comp2, SWT.NONE);
		group_selectRule.setText("选择规则");
		group_selectRule.setBounds(10, 22, 423, 238);

		tree_src = new Tree(group_selectRule, SWT.BORDER);
		tree_src.setBounds(10, 21, 228, 207);

		list_des = new List(group_selectRule, SWT.BORDER);
		list_des.setBounds(292, 21, 121, 207);

		final Button button_yes = new Button(group_selectRule, SWT.NONE);
		button_yes.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				TreeItem[] selection = tree_src.getSelection();
				for (int i = 0; i < selection.length; i++){
					TreeItem item = selection[i];
					LinkedList<String> names = MainWindow.cp.getAllCategorynames();			
					if(names.indexOf(item.getText()) == -1){//不是类别名
						if(list_des.indexOf(item.getText()) == -1)
							list_des.add(item.getText());
					}
					else{
						TreeItem[] childItem = item.getItems();
						for(int j=0;j<childItem.length;j++)
							if(list_des.indexOf(childItem[j].getText()) == -1)
								list_des.add(childItem[j].getText());
					}
				}
			}
		});
		button_yes.setText(">>");
		button_yes.setBounds(244, 33, 42, 24);

		final Button button_no = new Button(group_selectRule, SWT.NONE);
		button_no.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				list_des.remove(list_des.getSelectionIndices());
			}
		});
		button_no.setText("<<");
		button_no.setBounds(244, 84, 42, 24);

		final Button button_refreshRule = new Button(group_selectRule, SWT.NONE);
		button_refreshRule.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				loadRules();
			}
		});
		button_refreshRule.setText("刷新");
		button_refreshRule.setBounds(244, 204, 42, 24);

		final Group group_sensativity = new Group(comp2, SWT.NONE);
		group_sensativity.setText("敏感度设置");
		group_sensativity.setBounds(439, 22, 346, 94);

		combo_sensativity = new Combo(group_sensativity, SWT.NONE);
		combo_sensativity.setItems(new String[] {"1.0", "0.9", "0.8", "0.7", "0.6", "0.5", "0.4", "0.3", "0.2", "0.1", "0.0"});
		combo_sensativity.select(0);
		combo_sensativity.setBounds(110, 57, 60, 22);

		final Label label_sensativity = new Label(group_sensativity, SWT.NONE);
		label_sensativity.setFont(SWTResourceManager.getFont("", 11, SWT.BOLD));
		label_sensativity.setText("搜索结果的敏感度达到此值时认为是敏感信息：");
		label_sensativity.setBounds(10, 29, 339, 22);

		final Button button_addTask = new Button(comp2, SWT.NONE);
		button_addTask.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onAddCheckRuleTask();
			}
		});
		button_addTask.setText("添加");
		button_addTask.setImage(SWTResourceManager.getImage("img/newTask.png"));
		button_addTask.setBounds(544, 184, 94, 39);

		text = new Text(comp2, SWT.WRAP | SWT.BORDER);
		text.setEditable(false);
		text.setForeground(SWTResourceManager.getColor(255, 0, 0));
		text.setText("注意：启动任务以后请确保“入口设置”页面中的监控已开启，否则监控将失效。");
		text.setBounds(439, 139, 346, 39);
	}
	
	/**
	 * 创建预警信息模块
	 */
	private void createWarningInfoBlock(Composite comp){
		warningFolder = new CTabFolder(comp, SWT.NONE | SWT.CLOSE);
		warningFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		warningFolder.setSimple(false);
		warningFolder.setTabHeight(20);
		warningFolder.setSelectionBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		final CTabItem warningItem = new CTabItem(warningFolder, SWT.NONE);
		warningItem.setText("预警信息");
		Composite warningComp = new Composite(warningFolder, SWT.NONE);
		warningComp.setLayout(new GridLayout());
		warningItem.setControl(warningComp);
		warningFolder.setSelection(warningItem);
		warningFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void close(final CTabFolderEvent event) {
				CTabItem item = (CTabItem)event.item;
				if(item.equals(warningItem)){
					MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
					box.setText("警告");
					box.setMessage("预警信息界面不能关闭！");
					box.open();
					event.doit = false;
				}
			}
		});
		
		Composite comp1 = new Composite(warningComp, SWT.NONE);
		final GridData gd_comp1 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_comp1.heightHint = 250;
		comp1.setLayoutData(gd_comp1);
		comp1.setLayout(new GridLayout());
		
		Group comp2 = new Group(warningComp, SWT.NONE);
		comp2.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp2.setLayout(new FillLayout());
		comp2.setText("信息明细");
		resultInfoBrowser = new Browser(comp2, SWT.BORDER);
		resultInfoBrowser.setText("");
		
		final Link link_detail = new Link(warningComp, SWT.NONE);
		link_detail.setText("<a>查看详细......</a>");
		link_detail.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				onDisplayResultDetail();
			}
		});
		link_detail.setEnabled(false);
		
		Composite operationComp = new Composite(comp1, SWT.NONE);
		operationComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		operationComp.setLayout(new GridLayout(4,false));
		
		final Button check = new Button(operationComp, SWT.CHECK);
		check.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				checkMonitorInfoTable.setAllChecked(check.getSelection());
			}
		});
		check.setText("全选");
		
		ToolBar toolBar = new ToolBar(operationComp, SWT.FLAT | SWT.RIGHT);
		final ToolItem toolItem_delete = new ToolItem(toolBar, SWT.PUSH);
		toolItem_delete.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				Object[] results = checkMonitorInfoTable.getCheckedElements();
				for(Object r : results){
					//monitorInfoTable.remove((WarningResult)r);
					warnResults.remove((WarningResult)r);
					monitorInfoTable.refresh();
				}
			}
		});
		toolItem_delete.setText("删除");
		toolItem_delete.setImage(SWTResourceManager.getImage("img/delete.gif"));
		
		final ToolItem toolItem_check = new ToolItem(toolBar, SWT.PUSH);
		toolItem_check.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				onDisplayResultDetail();
			}
		});
		toolItem_check.setText("查看");
		toolItem_check.setImage(SWTResourceManager.getImage("img/icon_rule.gif"));
		toolItem_check.setEnabled(false);
		
		final ToolItem toolItem_warnPanel = new ToolItem(toolBar, SWT.PUSH);
		toolItem_warnPanel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				warningPanel.setVisible(true);
				/*
				WarningResult wr = new WarningResult("2010-11-05  22:15:55",
			               "散户就是炮灰_兴业银行(601166)股吧_东方财富网股吧",
			               "http://guba.eastmoney.com/look,601166,6010872184.html.htm",
			               "你们知道吗，投股的散户就是炮灰",
			               "兴业银行",
			               "兴业银行",
			               "E:\\Eclipse开发\\workspace\\WordFinderMonitor\\spider\\html\\guba.eastmoney.com_look,601166,6010872184.html",
			               true);
				LinkedList<WarningResult> list = new LinkedList<WarningResult>();
				list.add(wr);
				addNewWarning(list);
				*/
			}
		});
		toolItem_warnPanel.setText("报警面板");
		toolItem_warnPanel.setImage(SWTResourceManager.getImage("img/warning.png"));
		
		monitorInfoTable = new TableViewer(comp1, SWT.CHECK | SWT.FULL_SELECTION | SWT.BORDER);
		checkMonitorInfoTable = new CheckboxTableViewer(monitorInfoTable.getTable());
		monitorInfoTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		monitorInfoTable.getTable().setHeaderVisible(true);
		monitorInfoTable.getTable().setLinesVisible(true);
		TableColumn tableColumnTheme = new TableColumn(monitorInfoTable.getTable(), SWT.LEFT);
		tableColumnTheme.setWidth(500);
		tableColumnTheme.setText("标题");
		TableColumn tableColumnStartTime = new TableColumn(monitorInfoTable.getTable(), SWT.LEFT);
		tableColumnStartTime.setWidth(200);
		tableColumnStartTime.setText("时间");
		TableColumn tableColumnSensativity = new TableColumn(monitorInfoTable.getTable(), SWT.LEFT);
		tableColumnSensativity.setWidth(200);
		tableColumnSensativity.setText("关键词");
		
		monitorInfoTable.setContentProvider(new MyContentPrivider());
		monitorInfoTable.setLabelProvider(new MyLabelProvider());
		monitorInfoTable.setInput(warnResults);
		monitorInfoTable.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				StructuredSelection select = (StructuredSelection)e.getSelection();
				WarningResult result = (WarningResult)select.getFirstElement();
				if(result == null)return;
				currentResult = result;
				onUpdateResult();
				toolItem_check.setEnabled(true);
				link_detail.setEnabled(true);
			}
		});
		
		
	}
	
	/**
	 * 创建预警记录模块
	 */
	private void createWarningHistoryBlock(Composite comp){
		warningHistoryFolder = new CTabFolder(comp, SWT.NONE | SWT.CLOSE);
		warningHistoryFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		warningHistoryFolder.setSimple(false);
		warningHistoryFolder.setTabHeight(20);
		warningHistoryFolder.setSelectionBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		final CTabItem warningHistoryItem = new CTabItem(warningHistoryFolder, SWT.NONE);
		warningHistoryItem.setText("预警记录");
		Composite warningHistoryComp = new Composite(warningHistoryFolder, SWT.NONE);
		warningHistoryComp.setLayout(new GridLayout());
		warningHistoryItem.setControl(warningHistoryComp);
		warningHistoryFolder.setSelection(warningHistoryItem);
		warningHistoryFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void close(final CTabFolderEvent event) {
				CTabItem item = (CTabItem)event.item;
				if(item.equals(warningHistoryItem)){
					MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
					box.setText("警告");
					box.setMessage("预警记录界面不能关闭！");
					box.open();
					event.doit = false;
				}
			}
		});
		
		Composite comp1 = new Composite(warningHistoryComp, SWT.NONE);
		final GridData gd_comp1 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_comp1.heightHint = 200;
		comp1.setLayoutData(gd_comp1);
		comp1.setLayout(new GridLayout());
		
		Group comp2 = new Group(warningHistoryComp, SWT.NONE);
		comp2.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp2.setLayout(new FillLayout());
		resultHistoryBrowser = new Browser(comp2, SWT.BORDER);
		resultHistoryBrowser.setText("");
		
		final Link link_detail = new Link(warningHistoryComp, SWT.NONE);
		link_detail.setText("<a>查看详细...</a>");
		link_detail.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				onDisplayHistoryResultDetail();
			}
		});
		link_detail.setEnabled(false);
		
		Composite operationComp = new Composite(comp1, SWT.NONE);
		operationComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		operationComp.setLayout(new GridLayout(4,false));
		final Button check = new Button(operationComp, SWT.CHECK);
		check.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				checkMonitorHistoryTable.setAllChecked(check.getSelection());
			}
		});
		check.setText("全选");
		
		final ToolBar toolBar = new ToolBar(operationComp, SWT.FLAT | SWT.RIGHT);
		final ToolItem toolItem_delete = new ToolItem(toolBar, SWT.PUSH);
		toolItem_delete.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				Object[] results = checkMonitorHistoryTable.getCheckedElements();
				if(results.length == 0)return;
				int choice;
				MessageBox box = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION);
				box.setText("注意");
				box.setMessage("删除后无法恢复，确认删除吗？");
				choice = box.open();
				if(choice == SWT.YES){		
					for(Object r : results){
						//monitorHistoryTable.remove((WarningResult)r);
						MainWindow.wrp.deleteResult((WarningResult)r);
						historyResults.remove((WarningResult)r);
						monitorHistoryTable.refresh();
					}
				}
			}
		});
		toolItem_delete.setText("彻底删除");
		toolItem_delete.setImage(SWTResourceManager.getImage("img/delete.gif"));
	
		final ToolItem toolItem_check = new ToolItem(toolBar, SWT.PUSH);
		toolItem_check.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				onDisplayHistoryResultDetail();
			}
		});
		toolItem_check.setText("查看");
		toolItem_check.setImage(SWTResourceManager.getImage("img/icon_rule.gif"));
		toolItem_check.setEnabled(false);
		
		final ToolItem toolItem_refresh = new ToolItem(toolBar, SWT.PUSH);
		toolItem_refresh.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				monitorHistoryTable.refresh();
			}
		});
		toolItem_refresh.setText("刷新 ");
		toolItem_refresh.setImage(SWTResourceManager.getImage("img/refresh.png"));
		
		monitorHistoryTable = new TableViewer(comp1, SWT.CHECK | SWT.FULL_SELECTION | SWT.BORDER);
		checkMonitorHistoryTable = new CheckboxTableViewer(monitorHistoryTable.getTable());
		monitorHistoryTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		monitorHistoryTable.getTable().setHeaderVisible(true);
		monitorHistoryTable.getTable().setLinesVisible(true);
		TableColumn tableColumnTheme = new TableColumn(monitorHistoryTable.getTable(), SWT.LEFT);
		tableColumnTheme.setWidth(500);
		tableColumnTheme.setText("标题");
		TableColumn tableColumnStartTime = new TableColumn(monitorHistoryTable.getTable(), SWT.LEFT);
		tableColumnStartTime.setWidth(200);
		tableColumnStartTime.setText("时间");
		TableColumn tableColumnSensativity = new TableColumn(monitorHistoryTable.getTable(), SWT.LEFT);
		tableColumnSensativity.setWidth(200);
		tableColumnSensativity.setText("关键词");	
		
		monitorHistoryTable.setContentProvider(new MyContentPrivider());
		monitorHistoryTable.setLabelProvider(new MyLabelProvider());
		monitorHistoryTable.setInput(historyResults);
		monitorHistoryTable.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				StructuredSelection select = (StructuredSelection)e.getSelection();
				WarningResult result = (WarningResult)select.getFirstElement();
				if(result == null)return;
				currentResult = result;
				onUpdateHistoryResult();	
				toolItem_check.setEnabled(true);
				link_detail.setEnabled(true);
			}
		});
		
	}
	
	/**
	 * 创建统计分析模块
	 */
	private void createStatisticsBlock(Composite comp){
		statisticsFolder = new CTabFolder(comp, SWT.NONE | SWT.CLOSE);
		statisticsFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		statisticsFolder.setSimple(false);
		statisticsFolder.setTabHeight(20);
		statisticsFolder.setSelectionBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		final CTabItem statisticsItem = new CTabItem(statisticsFolder, SWT.NONE);
		statisticsItem.setText("统计分析");
		Composite statisticsComp 
		              = new PieChartComposite(statisticsFolder, MainWindow.wrp);
		statisticsItem.setControl(statisticsComp);
		statisticsFolder.setSelection(statisticsItem);
	}
	
	/**
	 * 创建底部面板
	 */
	private void createBottomSurface(Composite container){
		SashForm sashForm_bottom = new SashForm(container, SWT.HORIZONTAL);
		sashForm_bottom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sashForm_bottom.SASH_WIDTH = 4;
		                  /////////////////////////////左底部////////////////////////////////////
		Composite bottom_left = new Composite(sashForm_bottom, SWT.NONE);
		bottom_left.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		bottom_left.setLayout(new GridLayout(1, true));
		
		StatusMsgLabel = new CLabel(bottom_left, SWT.NONE);
		StatusMsgLabel.setLayoutData(new GridData(GridData.FILL_BOTH));
		StatusMsgLabel.setImage(SWTResourceManager.getImage("img/info.gif"));
		StatusMsgLabel.setText("实时监控");	
                           /////////////////////////////左底部////////////////////////////////////
		
                           /////////////////////////////右底部////////////////////////////////////
		Composite bottom_right = new Composite(sashForm_bottom, SWT.NONE);
		bottom_right.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		bottom_right.setLayout(new GridLayout(6, true));
		
		Label _SpiderStatusMsgLabel = new Label(bottom_right, SWT.NONE);   //空白版面，占位置
		final GridData gd__SpiderStatusMsgLabel = new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1);
		_SpiderStatusMsgLabel.setLayoutData(gd__SpiderStatusMsgLabel);
		
                           /////////////////////////////右底部////////////////////////////////////
		
		sashForm_bottom.setWeights(new int[] {20, 80});
	}
	
	
	/**
	 * 创建主菜单
	 */
	private void createMenuBar(){
		menuBar = new Menu(shell, SWT.BAR);
		
		MenuItem search = new MenuItem(menuBar, SWT.CASCADE);
		search.setText("操作(&O)");
		search.setAccelerator(SWT.ALT + 'O');
		Menu menu_search = new Menu(shell, SWT.DROP_DOWN);
		search.setMenu(menu_search);
		MenuItem menuItem_exit = new MenuItem(menu_search, SWT.PUSH);
		menuItem_exit.setText("退出");
		menuItem_exit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				onExit();
			}
		});
		
		MenuItem view = new MenuItem(menuBar, SWT.CASCADE);
		view.setText("视图(&V)");
		view.setAccelerator(SWT.ALT + 'V');
		Menu menu_view = new Menu(shell, SWT.DROP_DOWN);
		view.setMenu(menu_view);
		MenuItem spiderItem = new MenuItem(menu_view, SWT.PUSH);
		spiderItem.setText("爬虫控制");
		spiderItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = spiderFolder;
				stackComp.layout();
			}
		});
		MenuItem monitorRuleItem = new MenuItem(menu_view, SWT.PUSH);
		monitorRuleItem.setText("监控管理");
		monitorRuleItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = monitorRuleFolder;
				stackComp.layout();
			}
		});
		MenuItem warningItem = new MenuItem(menu_view, SWT.PUSH);
		warningItem.setText("预警信息");
		warningItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = warningFolder;
				stackComp.layout();
			}
		});
		MenuItem warningHistoryItem = new MenuItem(menu_view, SWT.PUSH);
		warningHistoryItem.setText("预警记录");
		warningHistoryItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = warningHistoryFolder;
				stackComp.layout();
			}
		});
		MenuItem statisticsItem = new MenuItem(menu_view, SWT.PUSH);
		statisticsItem.setText("统计分析");
		statisticsItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = statisticsFolder;
				stackComp.layout();
			}
		});
		
		shell.setMenuBar(menuBar);
	}
	
	
	/**
	 * 创建入口地址树的右键菜单
	 */
	private void createTreeEntryMenu() {
		menu_treeEntry = new Menu(shell, SWT.POP_UP);
		
		MenuItem item_add = new MenuItem(menu_treeEntry, SWT.PUSH);
		item_add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				NewCategoryDlg ncd = new NewCategoryDlg(shell);
				ncd.open();
				refreshEntry();
			}
		});
		item_add.setText("添加新类别");
		
		new MenuItem(menu_treeEntry, SWT.SEPARATOR);
		
		MenuItem item_modify = new MenuItem(menu_treeEntry, SWT.PUSH);
		item_modify.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(tree_entry.getSelectionCount() == 0)return;
				String selection = tree_entry.getSelection()[0].getText();
				if(MainWindow.ucp.isCategoryExisted(selection)){//是类别
					ModifyCategoryDlg mcd = new ModifyCategoryDlg(shell, selection);
					mcd.open();
					refreshEntry();
				}
				else{
					ModifyEntryAddressDlg mead = new ModifyEntryAddressDlg(shell, selection);
					mead.open();
					refreshEntry();
				}
			}
		});
		item_modify.setText("修改");
		
		MenuItem item_delete = new MenuItem(menu_treeEntry, SWT.PUSH);
		item_delete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(tree_entry.getSelectionCount() == 0)return;
				String selection = tree_entry.getSelection()[0].getText();
				if(MainWindow.ucp.isCategoryExisted(selection)){//是类别
					if(MessageDialog.openConfirm(shell, "删除确认",
							"确定删除分类”" 
							+ selection 
							+ "“，以及其下的所有入口地址吗？")){
						MainWindow.ucp.deleteCategory(selection);
						refreshEntry();
					}
				}
				else{
					if(MessageDialog.openConfirm(shell, "删除确认",
							"确定删除入口地址”" 
							+ MainWindow.eap.getEntryAddressNameByUrl(selection) 
							+ "“（" + selection + "）吗？")){
						MainWindow.eap.deleteEntryAddress(selection);
						refreshEntry();
					}
				}
			}
		});
		item_delete.setText("删除");
		
		tree_entry.setMenu(menu_treeEntry);
	}
	
	/**
	 * 添加系统托盘及右键菜单
	 */
	private void addSystemTray()
	{
		SelectionListener sl = new SelectionListener(){
			public void widgetSelected(SelectionEvent e){
				if (!shell.isVisible())
				{
					shell.setVisible(true);
					shell.setMinimized(false);
					shell.setActive();
				}
			}
			public void widgetDefaultSelected(SelectionEvent e){
				if (!shell.isVisible())
				{
					shell.setVisible(true);
					shell.setMinimized(false);
					shell.setActive();
				}
				warningPanel.setText("");
			}
		};
		
		final Tray tray = Display.getDefault().getSystemTray();
		trayItem = new TrayItem(tray, SWT.POP_UP);
		trayItem.setImage(SWTResourceManager.getImage("img/monitor.png"));
		trayItem.setVisible(true);
		trayItem.setToolTipText("实时监控正在运行……");
		trayItem.addSelectionListener(sl);
		
		final Menu trayMenu = new Menu(shell, SWT.NONE);
		final MenuItem showMenuItem = new MenuItem(trayMenu, SWT.PUSH);
		showMenuItem.setText("显示窗口(&s)");
		showMenuItem.addSelectionListener(sl);
		trayMenu.setDefaultItem(showMenuItem);
		
		final MenuItem MinItem = new MenuItem(trayMenu, SWT.PUSH);
		MinItem.setText("最小化到系统托盘运行(&I)");
		MinItem.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				shell.setVisible(false);
			}
		});
		
		new MenuItem(trayMenu, SWT.SEPARATOR);
		
		final MenuItem exitMenuItem = new MenuItem(trayMenu, SWT.PUSH);
		exitMenuItem.setText("退出程序(&x)");
		exitMenuItem.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				onExit();
			}
		});
		trayItem.addMenuDetectListener(new MenuDetectListener(){
			public void menuDetected(MenuDetectEvent e){
				if (shell.isVisible()){
					showMenuItem.setEnabled(false);
					MinItem.setEnabled(true);
				}else{
					showMenuItem.setEnabled(true);
					MinItem.setEnabled(false);
				}
				trayMenu.setVisible(true);
			}
		});
	}
	
	/**
	 * 载入已创建的入口地址
	 */
	private void loadStoredEntry()
	{
		String[] classes = MainWindow.ucp.getAllCategorynamesAsArray();
		combo_entryCategory.setItems(classes);
		combo_entryCategory.select(0);
		for(int i=0;i<classes.length;i++){
			TreeItem category = new TreeItem(tree_entry, SWT.NONE);
			category.setText(classes[i]);
			LinkedList<EntryAddress> addrs
			            = MainWindow.eap.getEntryAddressByCategory(classes[i]);
			for(int j=0;j<addrs.size();j++){
				TreeItem item = new TreeItem(category, SWT.NONE);
				item.setText(addrs.get(j).getUrl());
			}
			category.setExpanded(true);
		}
	}
	
	/**
	 * 载入已创建的规则
	 */
	private void loadRules(){
		if(tree_src.getItemCount() != 0)
			tree_src.removeAll();
		LinkedList<String> classes = MainWindow.cp.getAllCategorynames();
		for(int i=0;i<classes.size();i++){
			TreeItem category = new TreeItem(tree_src, SWT.NONE);
			category.setText(classes.get(i));
			LinkedList<String> rules = MainWindow.rp.getRulenameByCategory(classes.get(i));
			for(int j=0;j<rules.size();j++){
				TreeItem item = new TreeItem(category, SWT.NONE);
				item.setText(rules.get(j));
			}
			category.setExpanded(true);
		}
	}
	
	/**
	 * 载入所有预警记录
	 */
	private void loadWarningHistory(){
		LinkedList<WarningResult> results = MainWindow.wrp.getAllResult();
		for(WarningResult wr : results)
			historyResults.addFirst(wr);
		monitorHistoryTable.refresh();
		
	}
	
	/**
	 * 退出时的动作
	 */
	private int onExit(){
		MessageBox box = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION);
		box.setText("退出实时监控");
		box.setMessage("确认退出实时监控吗？");
		if(box.open() == SWT.YES){
			if(!button_startSpider.getEnabled() && !button_stopSpider.getEnabled()){
				MessageBox box1 = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
				box1.setText("注意");
				box1.setMessage("监控正在停止，现在还不能关闭窗口，请稍等一会儿……");
				box1.open();
				return 1;
			}
			else {
				for(MonitorSearchThread st : checkRuleTask)
					if(!st.getStopFlag())
						st.setStopFlag(true);
				if(crawlFlag)onStopSpider();
				if(!trayItem.isDisposed())
					trayItem.dispose();
				if(!warningPanel.isDisposed())
					warningPanel.dispose();
				
				shell.dispose();
				return 0;
			}
		}
		return 1;
	}
	
	/**
	 * 启动爬虫时的动作
	 */
	private void onStartSpider()
	{
		Runnable runnable  = new Runnable(){
			Display display = Display.getCurrent();
			Queue entryAddress = new Queue();//入口地址队列
			public void run(){	   
				display.asyncExec(new Runnable(){
					public void run(){
						//读入入口地址
						for(TableItem item : table_entry.getItems())
							entryAddress.enQueue(item.getText(1));	
						//获取入口地址个数
						THREAD_NUMBER = entryAddress.getLength();
						if(THREAD_NUMBER == 0){
							MessageBox box = new MessageBox(shell, SWT.ICON_WARNING);
							box.setText("入口为空");
							box.setMessage("请先选择要监控的入口地址！");
							box.open();
						}
					}
				});
				if(THREAD_NUMBER == 0)return;
				display.asyncExec(new Runnable(){
					public void run(){
						label_spiderStatus.setText("爬虫正在启动……");
						button_startSpider.setEnabled(false);
					}
				});
				try{
					boolean flag = false;
					
					//创建爬取文件所存放的目录文件夹
					CreateDir cd = new CreateDir();
					cd.createDir();
					//启动线程池
					manager = new ThreadPoolManager(THREAD_NUMBER,bf,HashTableLength);
					
					String address;
					
					// manager.process("http://bbs.51job.com/");
					while (!entryAddress.isQueueEmpty()) { // 循环爬取入口地址
						address = entryAddress.deQueue();
						flag = manager.process(address);
						while (!flag) {
							System.out.println("waiting....");
							Thread.sleep(60000);
							flag = manager.process(address);
						}
					}
					crawlFlag = true;
					
					String operate = "论坛搜索";
					String detail =  "启动爬虫，入口地址个数为：  "+THREAD_NUMBER;
					System.out.println(detail+detail.length());
					LogProcess sl = new LogProcess();
					sl.saveLog(user, operate, detail);
					
					
				}catch(Exception e){
					e.printStackTrace();
				}
				display.asyncExec(new Runnable(){
					public void run(){
						final ToolTip tip = new ToolTip(shell, SWT.BALLOON
								| SWT.ICON_INFORMATION); // 显示托盘提示气球
						tip.setAutoHide(true); // 自动隐藏气泡式提示文本
						tip.setMessage("对指定入口网址的监控已启动。");// 设置提示信息
						tip.setText("监控启动");
						trayItem.setToolTip(tip);
						tip.setVisible(true); // 显示气泡式提示
						tip.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(SelectionEvent e) {
								tip.setVisible(false);
							}
						});
						
						label_spiderStatus.setText("监控正在运行！");
						label_spiderImg.setImage(SWTResourceManager.getImage("img/monitorRun.png"));
						button_stopSpider.setEnabled(true);
					}
				});
			}
		};
		new Thread(runnable).start();
	}
	
	/**
	 * 终止爬虫时的动作
	 */
	private void onStopSpider()
	{
		Runnable runnable = new Runnable(){
			Display display = Display.getCurrent();
			public void run(){
				display.asyncExec(new Runnable(){
					public void run(){
						label_spiderStatus.setText("正在停止监控……");
						button_stopSpider.setEnabled(false);
						THREAD_NUMBER = table_entry.getItemCount();
						
						String operate = "论坛搜索";
						String detail =  "停止监控操作！";
						LogProcess sl = new LogProcess();
						sl.saveLog(user, operate, detail);
						
					}
				});
				
				//执行停止线程工作的命令
				crawlFlag = manager.stopThread(THREAD_NUMBER);
				
				display.asyncExec(new Runnable(){
					public void run(){
						final ToolTip tip = new ToolTip(shell, SWT.BALLOON
								| SWT.ICON_INFORMATION); // 显示托盘提示气球
						tip.setAutoHide(true); // 自动隐藏气泡式提示文本
						tip.setMessage("监控已停止。");// 设置提示信息
						tip.setText("监控停止");
						trayItem.setToolTip(tip);
						tip.setVisible(true); // 显示气泡式提示
						tip.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(SelectionEvent e) {
								tip.setVisible(false);
							}
						});
						
						label_spiderStatus.setText("监控已停止！");
						label_spiderImg.setImage(SWTResourceManager.getImage("img/monitorStop.png"));
						button_startSpider.setEnabled(true);
					}
				});
			}
		};
		
		new Thread(runnable).start();
	}
	
	/**
	 * 定义启动任务时的动作
	 */
	private void onStartTask()
	{
		int[] select = checkRuleTable.getSelectionIndices();
		for (int i = 0; i < select.length; i++) {
			MonitorSearchThread st = checkRuleTask.get(select[i]);
			if (!st.getIsRunning()) {
				st.setIsRunning(true);
				checkRuleTable.getItem(select[i]).setText(3, "运行中");
				checkRuleTable.getItem(select[i]).setImage(0,
						SWTResourceManager.getImage("img/startStatus.png"));
			}
		}
	}
	
	/**
	 * 启动所有任务
	 */
	private void onStartAllTask(){
		TableItem[] items = checkRuleTable.getItems();
		for (int i = 0; i < items.length; i++){
			MonitorSearchThread st = checkRuleTask.get(i);
			if (!st.getIsRunning()) {
				st.setIsRunning(true);
				checkRuleTable.getItem(i).setText(3, "运行中");
				checkRuleTable.getItem(i).setImage(0,
						SWTResourceManager.getImage("img/startStatus.png"));
			}
		}
	}
	
	/**
	 * 定义暂停任务时的动作
	 */
	private void onPauseTask()
	{
		int[] select = checkRuleTable.getSelectionIndices();
		for (int i = 0; i < select.length; i++){
			MonitorSearchThread st = checkRuleTask.get(select[i]);
			if(st.getIsRunning()){
				st.setIsRunning(false);
				checkRuleTable.getItem(select[i]).setText(3, "暂停");
				checkRuleTable.getItem(select[i]).setImage(0,
						SWTResourceManager.getImage("img/pauseStatus.png"));
			}
		}
	}
	
	/**
	 * 暂停所有任务
	 */
	private void onPauseAllTask(){
		TableItem[] items = checkRuleTable.getItems();
		for (int i = 0; i < items.length; i++){
			MonitorSearchThread st = checkRuleTask.get(i);
			if(st.getIsRunning()){
				st.setIsRunning(false);
				checkRuleTable.getItem(i).setText(3, "暂停");
				checkRuleTable.getItem(i).setImage(0,
						SWTResourceManager.getImage("img/pauseStatus.png"));
			}
		}
	}
	
	/**
	 * 定义删除某一任务时的动作
	 */
	private void onDeleteTask()
	{
		int[] select = checkRuleTable.getSelectionIndices();
		for (int i = 0; i < select.length; i++){
			MonitorSearchThread st = checkRuleTask.get(select[i]);
			if(!st.getStopFlag()){
				st.setStopFlag(true);
			}
		}
		for(int i=select.length-1;i>-1;i--){
			checkRuleTask.remove(select[i]);
		}
		checkRuleTable.remove(select);
		text_taskInfo.setText("");
	}
	
	/**
	 * 删除所有任务
	 */
	private void onDeleteAllTask()
	{
		for (int i = 0; i < checkRuleTask.size(); i++){
			MonitorSearchThread st = checkRuleTask.get(i);
			if(!st.getStopFlag()){
				st.setStopFlag(true);
			}
		}

		checkRuleTask.clear();
		checkRuleTable.removeAll();
	}
	
	/**
	 * 更新任务信息面板
	 */
	private void onUpdateWarningInfo(){
		int index = checkRuleTable.getSelectionIndex();
		if(index == -1){
			text_taskInfo.setText("");
			return;
		}
		MonitorSearchThread st = checkRuleTask.get(index);
		
		text_taskInfo.setText("\n\n\n任务名称：" + st.getRule().getName());
		text_taskInfo.append("\n\n任务状态：" + (st.getIsRunning()?"正在运行":"暂停"));
		text_taskInfo.append("\n\n关键词：" + st.getRule().getWords());
		text_taskInfo.append("\n\n敏感度：" + st.getSensativity());
	}

	private void onUpdateResult(){
		html = "<tr><th width=\"100\">时间：</th><td>" + currentResult.getWarningTime() + "</td></tr>";
		html += "<tr><th width=\"100\">来源网址：</th><td>"
			 + currentResult.getUrl()
			 + "</td></tr>";
		html += "<tr><th width=\"100\">规则：</th><td>" + currentResult.getRuleName() + "</td></tr>";
		html += "<tr><th width=\"100\">关键词：</th><td>" + currentResult.getKeyword() + "</td></tr>";
		html += "<tr><th width=\"100\">标题：</th><td>" + currentResult.getTitle() + "</td></tr>";
		html += "<tr><th width=\"100\">摘要：</th><td>" + currentResult.getAbs() + "</td></tr>";
		resultInfoBrowser.setText(htmlHead + html + htmlEnd);
	}
	
	private void onUpdateHistoryResult(){
		html = "<tr><th width=\"100\">时间：</th><td>" + currentResult.getWarningTime() + "</td></tr>";
		html += "<tr><th width=\"100\">来源网址：</th><td>"
			 + currentResult.getUrl()
			 + "</td></tr>";
		html += "<tr><th width=\"100\">规则：</th><td>" + currentResult.getRuleName() + "</td></tr>";
		html += "<tr><th width=\"100\">关键词：</th><td>" + currentResult.getKeyword() + "</td></tr>";
		html += "<tr><th width=\"100\">标题：</th><td>" + currentResult.getTitle() + "</td></tr>";
		html += "<tr><th width=\"100\">摘要：</th><td>" + currentResult.getAbs() + "</td></tr>";
		resultHistoryBrowser.setText(htmlHead + html + htmlEnd);
	}
	
	/**
	 * 添加一条新的敏感信息，并执行报警
	 */
	public synchronized void addNewWarning(LinkedList<WarningResult> resultList){
		if(resultList == null)
			return;
		
		boolean flag = false;
		for (WarningResult wr : resultList)
			if (MainWindow.wrp.addResult(wr)) {
				warnResults.addFirst(wr);
				historyResults.addFirst(wr);
				flag = true;
			}
		if(flag){
			monitorInfoTable.refresh();
			monitorHistoryTable.refresh();
			currentResult = warnResults.get(0);
			onUpdateResult();
			warningPanel.warning(currentResult);//报警
			monitorInfoTable.getTable().select(0);
		}
	}
	
	/**
	 * 把入口地址树中选中的地址添加到监控列表中
	 */
	private void onAddEntry(){
		if(button_startSpider.getEnabled() && !button_stopSpider.getEnabled()){
			LinkedList<String> classes = MainWindow.ucp.getAllCategorynames();
			TreeItem[] selection = tree_entry.getSelection();
			for(int i=0;i<selection.length;i++){
				if(classes.indexOf(selection[i].getText())==-1){//不是类别名
					//先判断重复
					TableItem[] oldItems = table_entry.getItems();
					for(TableItem item : oldItems)
						if(item.getText(1).equals(selection[i].getText())){
							MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION);
							box.setText("入口重复");
							box.setMessage("入口地址" + selection[i].getText() + "已存在列表中！");
							box.open();
							return;
						}
					EntryAddress ea
					       = MainWindow.eap.getEntryAddressByUrl(selection[i].getText());
					TableItem newAddr = new TableItem(table_entry, SWT.NONE);
					newAddr.setText(new String[]{ea.getName(), ea.getUrl()});
				}
				else{                                 //是类别，分别加
					LinkedList<EntryAddress> eaList =
						MainWindow.eap.getEntryAddressByCategory(selection[i].getText());
					boolean dup;
					for(EntryAddress ea : eaList){
						//先判断重复
						TableItem[] oldItems = table_entry.getItems();
						dup = false;
						for(TableItem item : oldItems)
							if(item.getText(1).equals(ea.getUrl())){
								dup = true;
								break;
							}
						if(dup){
							MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION);
							box.setText("入口重复");
							box.setMessage("入口地址" + ea.getUrl() + "已经在监控列表中了！");
							box.open();
						}
						else{
							TableItem newAddr = new TableItem(table_entry, SWT.NONE);
							newAddr.setText(new String[]{ea.getName(), ea.getUrl()});
						}
					}
				}
			}
		}
		else{
			MessageBox box = new MessageBox(shell, SWT.ICON_WARNING);
			box.setText("非法操作");
			box.setMessage("请先停止爬虫，再对监控列表进行操作！");
			box.open();
		}
	}
	
	/**
	 * 从入口地址树中删除地址
	 */
	private void onDeleteEntry(){
		int choice;
		MessageBox box = new MessageBox(shell, SWT.YES | SWT.NO
				| SWT.ICON_INFORMATION);
		box.setText("提示");
		box.setMessage("确认删除吗？");
		choice = box.open();
		if(choice == SWT.YES){
			String selection = tree_entry.getSelection()[0].getText();
			if (!MainWindow.ucp.isCategoryExisted(selection)) {// 不是类别名
				MainWindow.eap.deleteEntryAddress(selection);
				tree_entry.getSelection()[0].dispose();
			} else { // 是类别
				MainWindow.eap.deleteEntryAddressByCategory(selection);
				tree_entry.getSelection()[0].dispose();
			}
		}
	}
	
	/**
	 * 添加新入口地址
	 */
	private void onNewEntry(){
		if(text_entryUrl.getText().trim().replace("http://", "").equals("")){
			MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION);
			box.setText("输入错误");
			box.setMessage("请输入完整的地址！");
			box.open();
		}
		else{
			String address = text_entryUrl.getText().trim();
			String name = text_entryName.getText().trim();
			String category = combo_entryCategory.getText().trim();
			if(name.equals(""))
				name = address.replaceAll("http://", "").replaceAll("/(.*)", "");
			
			if(MainWindow.eap.isEntryAddressExisted(address)){
				MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION);
				box.setText("入口重复");
				box.setMessage("入口地址" + address + "已存在数据库中！");
				box.open();
			}
			else if(MainWindow.eap.addEntryAddress(address, name, category)){
				MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION);
				box.setText("添加成功");
				box.setMessage("入口地址" + address + "已添加到数据库中！");
				box.open();
				for(TreeItem item : tree_entry.getItems())
					if(item.getText().equals(category)){
						TreeItem newItem = new TreeItem(item, SWT.NONE);
						newItem.setText(address);
						break;
					}
			}
			else{
				MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION);
				box.setText("添加失败");
				box.setMessage("入口地址" + address + "已存在数据库中！");
				box.open();
			}
		}
	}
	
	/**
	 * 添加规则搜索任务时的动作
	 */
	private void onAddCheckRuleTask()
	{
		String time = CurrentTime.getInstance();
		String totalRule = "";
		for (int k = 0; k < list_des.getItemCount(); ++k){
			String RuleName = list_des.getItem(k);
			String senty = combo_sensativity.getText();
			TableItem t = new TableItem(checkRuleTable, SWT.NONE);
			t.setText(new String[]{RuleName, time, senty, "暂停"});
			t.setImage(0, SWTResourceManager.getImage("img/pauseStatus.png"));
			
			Rule rule = MainWindow.rp.getRuleByName(RuleName);
			MonitorSearchThread thread
			      = new MonitorSearchThread(this, rule, combo_sensativity.getText());
			thread.start();
			checkRuleTask.add(thread);
			
			totalRule += (k+1) + ":" + rule.getName()+";";
			
		}
		list_des.removeAll();
		
		String operate = "实时监控";
		String detail =  "添加监控任务，规则为："+totalRule;
		LogProcess sl = new LogProcess();
		sl.saveLog(user, operate, detail);
	}
	
	/**
	 * 查看预警结果的详细信息
	 */
	private void onDisplayResultDetail(){
		CTabItem detaiItem = new CTabItem(warningFolder, SWT.CLOSE);
		detaiItem.setText(currentResult.getTitle());
		ResultDetailComposite rdComp 
		            = new ResultDetailComposite(warningFolder, currentResult);
		detaiItem.setControl(rdComp);
		warningFolder.setSelection(detaiItem);
		
		if(currentResult.isNew()){
			MainWindow.wrp.setResultChecked(currentResult);
			currentResult.setNew(false);
		}
		monitorInfoTable.refresh();
	}
	
	/**
	 * 查看预警记录结果的详细信息
	 */
	private void onDisplayHistoryResultDetail(){
		CTabItem detaiItem = new CTabItem(warningHistoryFolder, SWT.CLOSE);
		detaiItem.setText(currentResult.getTitle());
		ResultDetailComposite rdComp 
		            = new ResultDetailComposite(warningHistoryFolder, currentResult);
		detaiItem.setControl(rdComp);
		warningHistoryFolder.setSelection(detaiItem);
		
		if(currentResult.isNew()){
			MainWindow.wrp.setResultChecked(currentResult);
			currentResult.setNew(false);
		}
		monitorHistoryTable.refresh();
	}
	
	/**
	 * 刷新入口地址页面的操作
	 */
	private void refreshEntry() {
		//更新操作 
		String[] classes = MainWindow.ucp.getAllCategorynamesAsArray();
		String temp = combo_entryCategory.getText();
		combo_entryCategory.setItems(classes);
		combo_entryCategory.select(combo_entryCategory.indexOf(temp));
		//更新入口列表
		tree_entry.removeAll();
		for(int i=0;i<classes.length;i++){
			TreeItem category = new TreeItem(tree_entry, SWT.NONE);
			category.setText(classes[i]);
			LinkedList<EntryAddress> addrs
			            = MainWindow.eap.getEntryAddressByCategory(classes[i]);
			for(int j=0;j<addrs.size();j++){
				TreeItem item = new TreeItem(category, SWT.NONE);
				item.setText(addrs.get(j).getUrl());
			}
			category.setExpanded(true);
		}
	}
	
	public synchronized void setStatusLabel(String text, Image image) {
		StatusMsgLabel.setText(text);
		StatusMsgLabel.setImage(image);
	}
}
