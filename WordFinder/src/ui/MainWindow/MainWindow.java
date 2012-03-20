package ui.MainWindow;

import java.util.LinkedList;

import lucene.Index.IndexProcesser;
import lucene.Search.SearchProcesser;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import searchUtility.AutoSearchThread;
import spider.CreateDir;
import spider.SpiderConf;
import systemConfig.SystemParameter;
import ui.MonitorWindow.MonitorWindow;

import Filter.BloomFilter2;

import com.swtdesigner.SWTResourceManager;

import database.*;

public class MainWindow {

	public Shell shell;
	public static TrayItem trayItem;         //系统托盘
	public Composite stackComp;
	public StackLayout stackLayout;
	public CTabFolder loginFolder, lookupFolder, systemInfoFolder,//各个功能板块
	                  searchFolder, spiderFolder,
	                  ruleConfigFolder, monitorFolder, traceBackFolder,
	                  userConfigFolder, systemConfigFolder, logFolder,
	                  statisticsFolder, imageSearchFolder;
	public static CTabFolder lookupResultFolder, lookupSpiderResultFolder,
	                         lookupWarningResultFolder;
	public static CBrowserComposite imediateSearchBrowser;
	public LookupResultComposite lookupComp;
	public LookupSpiderResultComposite lookupSpiderComp;
	public ConfigRulesComposite configRuleComp;
	public SearchComposite searchComp;
	public AutoSearchComposite autoSearchComp;
	public SpiderComposite spiderComp;
	public StatisticsComposite statisticsComp;
	public UserConfigComposite userConfigComp;
	public LogComposite logComp;
	public RealtimeMonitorComposite monitorComp;
	public SystemConfigComposite systemConfigComp;
	public ImageSearchComposite imageComp;
	public Link loginInfo;
	public static MonitorWindow monitorWin;    //实时监控窗口
	
	private Menu menuBar;//主菜单
	private MenuItem lookupItem, searchItem, spiderItem, imageItem,
	                 configRuleItem, monitorItem, tracebackItem,
	                 statisticsItem, systemInfoItem, userConfigItem,
	                 systemConfigItem, logItem;
	private Tree tree_exit, tree_user, tree_manager, tree_auditor;
	private Text text_password;      //存放用户输入密码的文本框
	private Text text_username;      //存放用户输入用户名的文本框
	private Combo combo_identity;    //用户登录身份
	private Label label_loginStatus;
	private static boolean statusInUse = false;
	private static CLabel StatusMsgLabel; //显示状态信息的label
	
	public static String user;
	
	//当前登录用户的信息
	private String identity;
	private String username;
	private String password;
	
	//操作数据库的对象/////////////////////////////////////////////////////////////////////
	public static RuleProcess rp;
    public static CategoryProcess cp;
    public static UserProcess up;
    public static EntryAddressProcess eap;
    public static UrlCategoryProcess ucp;
    public static WarningResultProcess wrp;
    public static MetaEngineResultProcess merp;
    public static SynonymWordsProcess swp;
    public static ImageProcess ip;
    ////////////////////////////////////////////////////////////////////////////////////
    
    //自动搜索的所有线程/////////////////////////////////////////////////////////////////
    public static LinkedList<AutoSearchThread> autoSearchTaskList 
                                    = new LinkedList<AutoSearchThread>();
    ////////////////////////////////////////////////////////////////////////////////////
    
    //操作lucene索引的对象/////////////////////////////////////////////////////////////////
    public static IndexProcesser meIndexProcesser;
    public static SearchProcesser metaEngineSearcher;
    public static SearchProcesser spiderSearcher;
    public static SearchProcesser monitorSearcher;
    ////////////////////////////////////////////////////////////////////////////////////
    
    //过滤器////////////////////////////////////////////////////////////////////////////
    public static BloomFilter2 bf2;
    ////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window
	 */
	public void open() {
		final Display display = Display.getDefault();
		SystemParameter.loadFromFile();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	
	/**
	 * 初始化数据库操作对象和Lucene操作对象
	 */
	private void initialDBConnection(){
		up = new UserProcess();
	    eap = new EntryAddressProcess();
	    ucp = new UrlCategoryProcess();
	    wrp = new WarningResultProcess();
	    merp = new MetaEngineResultProcess();
	    swp = new SynonymWordsProcess();
	    ip = new ImageProcess();
	    
	    meIndexProcesser = new IndexProcesser(2);
	    metaEngineSearcher = new SearchProcesser(2);
	    spiderSearcher = new SearchProcesser(SpiderConf.DEEP_CRAWLER);
	    monitorSearcher = new SearchProcesser(SpiderConf.MAINPAGE_CRAWLER);
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		shell = new Shell(SWT.SHELL_TRIM);
		shell.setLayout(new FillLayout());
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(final ShellEvent e) {
				//关闭窗口时的动作 	
				if(onExit() == 0)
					System.exit(0);
				else
					e.doit =false;
			}
			public void shellIconified(ShellEvent e) {
				shell.setVisible(false);
			}
		});
		shell.setImage(SWTResourceManager.getImage("img/mix.gif"));
		//设置窗口居中显示
		Rectangle bounds = shell.getMonitor().getBounds();
		shell.setSize(bounds.width*4/5, bounds.height*9/10);
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width)/2;
		int y = bounds.y + (bounds.height - rect.height)/2;
		shell.setLocation(x, y);
		shell.setText("网络敏感信息实时预警系统");
		
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
		compLeft.setLayout(new GridLayout());
		createOptionBlock(compLeft);
		
		//中右部面板，由堆栈管理器管理，放置各个功能板块
		stackComp = new Composite(sash_mid, SWT.NONE);
		stackLayout = new StackLayout();
		stackComp.setLayout(stackLayout);
		
		sash_mid.setWeights(new int[] {20, 80});
		/////////////////////////////////////////////////中间版面///////////////////////////////////////////////////////////
		
		////////////////////////////////////////////////底部版面////////////////////////////////////////////////////////////////
		createBottomSurface(container);
		////////////////////////////////////////////////底部版面////////////////////////////////////////////////////////////////
		
		////////////////////////////////////////////创建菜单////////////////////////////////////////
		createMenuBar();
        ////////////////////////////////////////////////////////////////////////////////////
		
		///////////////////////////////////////////各个功能模块///////////////////////////////////////
		//退出模块
		createExitBlock(stackComp);
		
		//规则管理模块
		createRuleConfigBlock(stackComp);
		//元搜索模块
		createSearchBlock(stackComp);
		//论坛搜索模块
		createSpiderBlock(stackComp);
		//图片搜索模块
		createImageSearchBlock(stackComp);
		//信息查询模块
		createLookupResultBlock(stackComp);
		//信息溯源模块
		createTraceBackBlock(stackComp);
		//统计分析模块
		createStatisticsBlock(stackComp);
		//系统信息模块
		createSystemInfoBlock(stackComp);
	
		//用户管理模块
		createUserConfigBlock(stackComp);
		//系统配置模块
		createSystemConfigBlock(stackComp);
		//查看日志模块
		createLogBlock(stackComp);
		
		stackLayout.topControl = loginFolder;
		stackComp.layout();
		////////////////////////////////////////////////////////////////////////////////////////////
		
		ConnDB connDB = new ConnDB("userinfo");
		if (!connDB.connectToDB()) {
			MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
			box.setText("错误");
			box.setMessage("数据库连接异常，请检查数据库连接设置！");
			box.open();
			shell.dispose();
			System.exit(0);
		}
		initialDBConnection();
		CreateDir cd = new CreateDir();
		cd.createDir();
		
        ////////////////////////////////////////////创建系统托盘///////////////////////////////
		addSystemTray();
        ////////////////////////////////////////////////////////////////////////////////////
	}

	/**
	 * 创建顶部的界面，logo标志
	 */
	private void createTopSurface(Composite container){		
		Composite topComp = new Composite(container, SWT.NONE);
		topComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		topComp.setLayout(new GridLayout(2,true));
		topComp.setBackground(SWTResourceManager.getColor(255, 255, 255));
		
		CLabel label_logo = new CLabel(topComp, SWT.NONE);
		final GridData gd_label_logo = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_label_logo.widthHint = 40;
		label_logo.setLayoutData(gd_label_logo);
		label_logo.setBackground(SWTResourceManager.getColor(255, 255, 255));
		label_logo.setAlignment(SWT.LEFT);
		label_logo.setImage(SWTResourceManager.getImage("img/logo2.jpg"));
		
		Composite tagComp = new Composite(topComp, SWT.NONE);
		final GridData gd_tagComp = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_tagComp.heightHint = 40;
		tagComp.setLayoutData(gd_tagComp);
		tagComp.setBackground(SWTResourceManager.getColor(255, 255, 255));

		label_loginStatus = new Label(tagComp, SWT.RIGHT);
		label_loginStatus.setText("您尚未登录");
		label_loginStatus.setBounds(183, 26, 316, 14);
		label_loginStatus.setBackground(SWTResourceManager.getColor(255, 255, 255));

	}
	
	/**
	 * 中左部界面，选项按钮
	 */
	private void createOptionBlock(Composite comp){
		tree_exit = new Tree(comp, SWT.BORDER);
		tree_exit.setLayoutData(new GridData(GridData.FILL_BOTH));
		tree_exit.setBackground(SWTResourceManager.getColor(255, 255, 255));
		
		final TreeItem item_exit = new TreeItem(tree_exit, SWT.NONE);
		item_exit.setText("退出");
		item_exit.setImage(SWTResourceManager.getImage("img/exit.png"));
		
		tree_exit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(((TreeItem)e.item).equals(item_exit)){
					if(onExit() == 0)
						System.exit(0);
				}
			}
		});
		
		/////////////////////////////////////////////////////////////////////////////////
		
		tree_user = new Tree(comp, SWT.BORDER);
		tree_user.setLayoutData(new GridData(GridData.FILL_BOTH));
		tree_user.setBackground(SWTResourceManager.getColor(255, 255, 255));
		tree_user.setEnabled(false);
		
		final TreeItem item_ruleConfig = new TreeItem(tree_user, SWT.NONE);
		item_ruleConfig.setText("规则管理");
		item_ruleConfig.setImage(SWTResourceManager.getImage("img/rule.png"));
		
		final TreeItem item_metaSearch = new TreeItem(tree_user, SWT.NONE);
		item_metaSearch.setText("元搜索");
		item_metaSearch.setImage(SWTResourceManager.getImage("img/search.png"));
		
		final TreeItem item_spider = new TreeItem(tree_user, SWT.NONE);
		item_spider.setText("论坛搜索");
		item_spider.setImage(SWTResourceManager.getImage("img/forumSearch.png"));
		
		final TreeItem item_image = new TreeItem(tree_user, SWT.NONE);
		item_image.setText("图片搜索");
		item_image.setImage(SWTResourceManager.getImage("img/image_search.GIF"));
		
		final TreeItem item_lookup = new TreeItem(tree_user, SWT.NONE);
		item_lookup.setText("信息查询");
		item_lookup.setImage(SWTResourceManager.getImage("img/lookup.png"));
		
		final TreeItem item_monitor = new TreeItem(tree_user, SWT.NONE);
		item_monitor.setText("实时监控");
		item_monitor.setImage(SWTResourceManager.getImage("img/monitor.png"));
		
		final TreeItem item_traceBack = new TreeItem(tree_user, SWT.NONE);
		item_traceBack.setText("信息溯源");
		item_traceBack.setImage(SWTResourceManager.getImage("img/telescope.png"));
		
		final TreeItem item_statistics = new TreeItem(tree_user, SWT.NONE);
		item_statistics.setText("统计分析");
		item_statistics.setImage(SWTResourceManager.getImage("img/statistics.png"));
			
		final TreeItem item_systemInfo = new TreeItem(tree_user, SWT.NONE);
		item_systemInfo.setText("系统信息");
		item_systemInfo.setImage(SWTResourceManager.getImage("img/systemInfo.png"));
		
		tree_user.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(((TreeItem)e.item).equals(item_lookup)){
					stackLayout.topControl = lookupFolder;
					stackComp.layout();
					lookupComp.loadRules();
					lookupSpiderComp.loadRules();
				}
				else if(((TreeItem)e.item).equals(item_metaSearch)){
					stackLayout.topControl = searchFolder;
					stackComp.layout();
				}
				else if(((TreeItem)e.item).equals(item_spider)){
					stackLayout.topControl = spiderFolder;
					stackComp.layout();
				}
				else if(((TreeItem)e.item).equals(item_image)){
					stackLayout.topControl = imageSearchFolder;
					stackComp.layout();
				}
				else if(((TreeItem)e.item).equals(item_ruleConfig)){
					stackLayout.topControl = ruleConfigFolder;
					stackComp.layout();
				}
				else if(((TreeItem)e.item).equals(item_monitor)){
					if(monitorWin == null || MainWindow.monitorWin.shell.isDisposed()){
						monitorWin = new MonitorWindow();
						monitorWin.open(MainWindow.user);
					}
					else{
						monitorWin.shell.setVisible(true);
						monitorWin.shell.setActive();
						monitorWin.shell.setMinimized(false);
					}
				}
				else if(((TreeItem)e.item).equals(item_traceBack)){
					stackLayout.topControl = traceBackFolder;
					stackComp.layout();
				}
				else if(((TreeItem)e.item).equals(item_statistics)){
					stackLayout.topControl = statisticsFolder;
					stackComp.layout();
				}
				else if(((TreeItem)e.item).equals(item_systemInfo)){
					stackLayout.topControl = systemInfoFolder;
					stackComp.layout();
				}
			}
		});
		
        /////////////////////////////////////////////////////////////////////////////////
		
		tree_manager = new Tree(comp, SWT.BORDER);
		tree_manager.setLayoutData(new GridData(GridData.FILL_BOTH));
		tree_manager.setBackground(SWTResourceManager.getColor(255, 255, 255));
		tree_manager.setEnabled(false);
		
		final TreeItem item_userConfig = new TreeItem(tree_manager, SWT.NONE);
		item_userConfig.setText("用户管理");
		item_userConfig.setImage(SWTResourceManager.getImage("img/users.png"));
		
		final TreeItem item_systemConfig = new TreeItem(tree_manager, SWT.NONE);
		item_systemConfig.setText("系统配置");
		item_systemConfig.setImage(SWTResourceManager.getImage("img/config.png"));
		
		tree_manager.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(((TreeItem)e.item).equals(item_systemConfig)){
					stackLayout.topControl = systemConfigFolder;
					stackComp.layout();
				}
				else if(((TreeItem)e.item).equals(item_userConfig)){
					stackLayout.topControl = userConfigFolder;
					stackComp.layout();
				}
			}
		});
		
		///////////////////////////////////////////////////////////////////////////////
		
		tree_auditor = new Tree(comp, SWT.BORDER);
		tree_auditor.setLayoutData(new GridData(GridData.FILL_BOTH));
		tree_auditor.setBackground(SWTResourceManager.getColor(255, 255, 255));
		tree_auditor.setEnabled(false);
		
		final TreeItem item_log = new TreeItem(tree_auditor, SWT.NONE);
		item_log.setText("查看日志");
		item_log.setImage(SWTResourceManager.getImage("img/log.png"));
		
		tree_manager.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(((TreeItem)e.item).equals(item_log)){
					stackLayout.topControl = logFolder;
					stackComp.layout();
				}
			}
		});
	}
	
	/**
	 * 创建登录模块
	 */
	private void createExitBlock(Composite comp) {
		loginFolder = new CTabFolder(comp, SWT.NONE);
		loginFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		loginFolder.setSimple(false);
		loginFolder.setTabHeight(20);
		loginFolder.setSelectionBackground(
				SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));	
		CTabItem loginItem = new CTabItem(loginFolder, SWT.NONE);
		loginItem.setText("登录");
		Composite loginComp = new ScrolledComposite(loginFolder, SWT.NONE);
		loginItem.setControl(loginComp);
		loginFolder.setSelection(loginItem);	
		
		final Group wordFinderGroup = new Group(loginComp, SWT.NONE);
		wordFinderGroup.setText("登录信息");
		wordFinderGroup.setBounds(135, 163, 367, 136);
		
		Label label_2 = new Label(wordFinderGroup, SWT.NONE);
		label_2.setBounds(54, 27, 55, 17);
		label_2.setText("登录身份：");
		combo_identity = new Combo(wordFinderGroup, SWT.READ_ONLY);
		combo_identity.setItems(new String[] {"普通用户", "审计员", "管理员"});
		combo_identity.setBounds(126, 19, 154, 25);
		combo_identity.select(0);
		combo_identity.setFocus();
		
		final Label label = new Label(wordFinderGroup, SWT.NONE);
		label.setBounds(54, 62, 48, 17);
		label.setText("用户名：");		
		text_username = new Text(wordFinderGroup, SWT.BORDER);
		text_username.setBounds(126, 55, 154, 25);
		
		final Label label_1 = new Label(wordFinderGroup, SWT.NONE);
		label_1.setBounds(54, 102, 36, 17);
		label_1.setText("密码：");
		text_password = new Text(wordFinderGroup, SWT.BORDER);
		text_password.setBounds(126, 95, 154, 25);
		text_password.setEchoChar('*');
		text_password.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR){
					doLogin();
				}
			}
		});
		
		wordFinderGroup.setTabList(new Control[]{label_2, combo_identity, text_username, text_password});
				
		final Button button = new Button(loginComp, SWT.NONE);
		button.setText("登录");
		button.setBounds(313, 315, 76, 25);
		button.addSelectionListener(new SelectionAdapter(){   //点击按钮验证输入是否正确，正确则登陆主界面
			public void widgetSelected(SelectionEvent e){
				doLogin();
			}
		});

		final Button button_1 = new Button(loginComp, SWT.NONE);    //清除将输入的所有字符一律清除
		button_1.setBounds(415, 315, 76, 25);
		button_1.setText("重置");
		button_1.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				text_username.setText("");
				text_password.setText("");
			}
		});			
	}
	
	/**
	 * 创建信息查询模块
	 */
	private void createLookupResultBlock(Composite comp) {
		lookupFolder = new CTabFolder(comp, SWT.NONE);
		lookupFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		lookupFolder.setSimple(false);
		lookupFolder.setTabHeight(20);
		lookupFolder.setSelectionBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem lookupItem = new CTabItem(lookupFolder, SWT.NONE);
		lookupItem.setText("元搜索信息查询");
		lookupComp = new LookupResultComposite(lookupFolder);
		lookupItem.setControl(lookupComp);
		lookupFolder.setSelection(lookupItem);	
		
		CTabItem lookupSpiderItem = new CTabItem(lookupFolder, SWT.NONE);
		lookupSpiderItem.setText("论坛搜索信息查询");
		lookupSpiderComp = new LookupSpiderResultComposite(lookupFolder);
		lookupSpiderItem.setControl(lookupSpiderComp);
	}
	
	/**
	 * 创建规则管理模块
	 */
	private void createRuleConfigBlock(Composite comp) {
		ruleConfigFolder = new CTabFolder(comp, SWT.NONE);
		ruleConfigFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		ruleConfigFolder.setSimple(false);
		ruleConfigFolder.setTabHeight(20);
		ruleConfigFolder.setSelectionBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		final CTabItem configRuleItem = new CTabItem(ruleConfigFolder, SWT.NONE);
		configRuleItem.setText("规则管理");
		configRuleComp = new ConfigRulesComposite(ruleConfigFolder);
		configRuleItem.setControl(configRuleComp);
		
		ruleConfigFolder.setSelection(configRuleItem);
	}
	
	/**
	 * 创建关联词设置模块
	 */
	/*
	private void createSynonymBlock(Composite comp) {
		synonymFolder = new CTabFolder(comp, SWT.NONE);
		synonymFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		synonymFolder.setSimple(false);
		synonymFolder.setTabHeight(20);
		synonymFolder.setSelectionBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		final CTabItem synonymItem = new CTabItem(synonymFolder, SWT.NONE);
		synonymItem.setText("关联词设置");
		synonymComp = new SynonymComposite(synonymFolder);
		synonymItem.setControl(synonymComp);
		
		synonymFolder.setSelection(synonymItem);
	}
	*/
	
	/**
	 * 创建即时搜索模块
	 */
	private void createSearchBlock(Composite comp) {
		searchFolder = new CTabFolder(comp, SWT.NONE);
		searchFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		searchFolder.setSimple(false);
		searchFolder.setTabHeight(20);
		searchFolder.setSelectionBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem searchItem = new CTabItem(searchFolder, SWT.NONE);
		searchItem.setText("即时搜索");
		Composite searchComp = new SearchComposite(searchFolder);
		searchItem.setControl(searchComp);
		searchFolder.setSelection(searchItem);
		
		CTabItem autoSearchItem = new CTabItem(searchFolder, SWT.NONE);
		autoSearchItem.setText("自动搜索");
		autoSearchComp = new AutoSearchComposite(searchFolder);
		autoSearchComp.setLayout(new GridLayout());
		autoSearchItem.setControl(autoSearchComp);
	}
	
	/**
	 * 创建自动搜索模块
	 */
	/*
	private void createAutoSearchBlock(Composite comp) {
		autoSearchFolder = new CTabFolder(comp, SWT.NONE);
		autoSearchFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		autoSearchFolder.setSimple(false);
		autoSearchFolder.setTabHeight(20);
		autoSearchFolder.setSelectionBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem autoSearchItem = new CTabItem(autoSearchFolder, SWT.NONE);
		autoSearchItem.setText("建立搜索任务");
		autoSearchComp = new AutoSearchComposite(autoSearchFolder);
		autoSearchComp.setLayout(new GridLayout());
		autoSearchItem.setControl(autoSearchComp);
		autoSearchFolder.setSelection(autoSearchItem);
		
		CTabItem resultItem = new CTabItem(autoSearchFolder, SWT.NONE);
		resultItem.setText("搜索结果");
		autoSearchResultFolder = new CTabFolder(autoSearchFolder, SWT.BORDER);
		autoSearchResultFolder.setMinimumCharacters(10);
		
		resultItem.setControl(autoSearchResultFolder);
	}
	*/
	
	/**
	 * 创建论坛搜索模块
	 */
	private void createSpiderBlock(Composite comp) {
		spiderFolder = new CTabFolder(comp, SWT.NONE);
		spiderFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		spiderFolder.setSimple(false);
		spiderFolder.setTabHeight(20);
		spiderFolder.setSelectionBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem spiderItem = new CTabItem(spiderFolder, SWT.NONE);
		spiderItem.setText("建立搜索任务");
		spiderComp = new SpiderComposite(spiderFolder);
		spiderComp.setLayout(new GridLayout());
		spiderItem.setControl(spiderComp);
		spiderFolder.setSelection(spiderItem);
	}
	
	/**
	 * 创建图片搜索模块
	 */
	private void createImageSearchBlock(Composite comp) {
		imageSearchFolder = new CTabFolder(comp, SWT.NONE);
		imageSearchFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		imageSearchFolder.setSimple(false);
		imageSearchFolder.setTabHeight(20);
		imageSearchFolder.setSelectionBackground(
				SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem imageSearchItem = new CTabItem(imageSearchFolder, SWT.NONE);
		imageSearchItem.setText("图片搜索");
		imageComp = new ImageSearchComposite(imageSearchFolder);
		imageSearchItem.setControl(imageComp);
		imageSearchFolder.setSelection(imageSearchItem);
	}
	
	/**
	 * 创建实时监控模块
	 */
	/*
	private void createWarnHisBlock(Composite comp) {
		monitorFolder = new CTabFolder(comp, SWT.NONE);
		monitorFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		monitorFolder.setSimple(false);
		monitorFolder.setTabHeight(20);
		monitorFolder.setSelectionBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		monitorFolder.setMinimumCharacters(10);
		
		CTabItem monitorItem = new CTabItem(monitorFolder, SWT.NONE);
		monitorItem.setText("预警信息查询");
		monitorComp = new RealtimeMonitorComposite(monitorFolder);
		monitorComp.setLayout(new GridLayout());
		monitorItem.setControl(monitorComp);
		monitorFolder.setSelection(monitorItem);
	}
	*/
	
	/**
	 * 创建信息溯源模块
	 */
	private void createTraceBackBlock(Composite comp) {
		traceBackFolder = new CTabFolder(comp, SWT.NONE);
		traceBackFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		traceBackFolder.setSimple(false);
		traceBackFolder.setTabHeight(20);
		traceBackFolder.setSelectionBackground(
				SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem traceBackMetaItem = new CTabItem(traceBackFolder, SWT.NONE);
		traceBackMetaItem.setText("元搜索");
		Composite traceBackMetaComp = new TracebackMetaComposite(traceBackFolder);
		traceBackMetaItem.setControl(traceBackMetaComp);
		traceBackFolder.setSelection(traceBackMetaItem);
		
		CTabItem traceBackSpiderItem = new CTabItem(traceBackFolder, SWT.NONE);
		traceBackSpiderItem.setText("论坛搜索");
		Composite traceBackSpiderComp = new TracebackSpiderComposite(traceBackFolder);
		traceBackSpiderItem.setControl(traceBackSpiderComp);
	}
	
	/**
	 * 创建统计分析模块
	 */
	private void createStatisticsBlock(Composite comp) {
		statisticsFolder = new CTabFolder(comp, SWT.NONE);
		statisticsFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		statisticsFolder.setSimple(false);
		statisticsFolder.setTabHeight(20);
		statisticsFolder.setSelectionBackground(
				SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem statisticsItem = new CTabItem(statisticsFolder, SWT.NONE);
		statisticsItem.setText("统计分析");
		Composite statisticsComp = new StatisticsComposite(statisticsFolder);
		statisticsItem.setControl(statisticsComp);
		statisticsFolder.setSelection(statisticsItem);	
	}
	
	/**
	 * 创建系统信息模块
	 */
	private void createSystemInfoBlock(Composite comp) {
		systemInfoFolder = new CTabFolder(comp, SWT.NONE);
		systemInfoFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		systemInfoFolder.setSimple(false);
		systemInfoFolder.setTabHeight(20);
		systemInfoFolder.setSelectionBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem systemInfoItem = new CTabItem(systemInfoFolder, SWT.NONE);
		systemInfoItem.setText("系统信息");
		Composite systemInfoComp = new SystemInfoComposite(systemInfoFolder);
		systemInfoItem.setControl(systemInfoComp);
		systemInfoFolder.setSelection(systemInfoItem);	
	}
	
	/**
	 * 创建用户管理模块
	 */
	private void createUserConfigBlock(Composite comp) {
		userConfigFolder = new CTabFolder(comp, SWT.NONE);
		userConfigFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		userConfigFolder.setSimple(false);
		userConfigFolder.setTabHeight(20);
		userConfigFolder.setSelectionBackground(
				SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem userConfigItem = new CTabItem(userConfigFolder, SWT.NONE);
		userConfigItem.setText("用户管理");
		ScrolledComposite userConfigScrollComp = 
			new ScrolledComposite(userConfigFolder, SWT.H_SCROLL | SWT.V_SCROLL);
		userConfigScrollComp.setLayout(new FillLayout());
		userConfigItem.setControl(userConfigScrollComp);
		userConfigFolder.setSelection(userConfigItem);	
		userConfigComp = 
			new UserConfigComposite(userConfigScrollComp);
		userConfigComp.setSize(800, 600);
		userConfigScrollComp.setContent(userConfigComp);
	}
	
	/**
	 * 创建系统配置模块
	 */
	private void createSystemConfigBlock(Composite comp) {
		systemConfigFolder = new CTabFolder(comp, SWT.NONE);
		systemConfigFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		systemConfigFolder.setSimple(false);
		systemConfigFolder.setTabHeight(20);
		systemConfigFolder.setSelectionBackground(
				SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem systemConfigItem = new CTabItem(systemConfigFolder, SWT.NONE);
		systemConfigItem.setText("系统配置");
		ScrolledComposite systemConfigScrollComp = 
			new ScrolledComposite(systemConfigFolder, SWT.H_SCROLL | SWT.V_SCROLL);
		systemConfigScrollComp.setLayout(new FillLayout());
		systemConfigItem.setControl(systemConfigScrollComp);
		systemConfigFolder.setSelection(systemConfigItem);	
		systemConfigComp = new SystemConfigComposite(systemConfigScrollComp);
		systemConfigComp.setSize(950, 650);
		systemConfigScrollComp.setContent(systemConfigComp);
	}
	
	/**
	 * 创建查看日志模块
	 */
	private void createLogBlock(Composite comp){
		logFolder = new CTabFolder(comp, SWT.NONE);
		logFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		logFolder.setSimple(false);
		logFolder.setTabHeight(20);
		logFolder.setSelectionBackground(
				SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem logItem = new CTabItem(logFolder, SWT.NONE);
		logItem.setText("查看日志");
		ScrolledComposite logScrollComp = 
			new ScrolledComposite(logFolder, SWT.H_SCROLL | SWT.V_SCROLL);
		logScrollComp.setLayout(new FillLayout());
		logItem.setControl(logScrollComp);
		logFolder.setSelection(logItem);	
		logComp = new LogComposite(logScrollComp);
		logComp.setSize(950, 650);
		logScrollComp.setContent(logComp);
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
                           /////////////////////////////左底部////////////////////////////////////
		
                           /////////////////////////////右底部////////////////////////////////////
		Composite bottom_right = new Composite(sashForm_bottom, SWT.NONE);
		bottom_right.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		bottom_right.setLayout(new GridLayout(6, true));
		
		Label _SpiderStatusMsgLabel = new Label(bottom_right, SWT.NONE);   //空白版面，占位置
		final GridData gd__SpiderStatusMsgLabel = new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1);
		_SpiderStatusMsgLabel.setLayoutData(gd__SpiderStatusMsgLabel);
		
                           /////////////////////////////右底部////////////////////////////////////
		
		sashForm_bottom.setWeights(new int[] {50, 50});
	}
	
	/**
	 * 创建主菜单
	 */
	private void createMenuBar(){
		menuBar = new Menu(shell, SWT.BAR);
		
		MenuItem operation = new MenuItem(menuBar, SWT.CASCADE);
		operation.setText("操作(&O)");
		operation.setAccelerator(SWT.ALT + 'O');
		Menu menu_operation = new Menu(shell, SWT.DROP_DOWN);
		operation.setMenu(menu_operation);
		MenuItem menuItem_addRule = new MenuItem(menu_operation, SWT.PUSH);
		menuItem_addRule.setText("新建规则");
		menuItem_addRule.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = ruleConfigFolder;
				stackComp.layout();
				NewRuleDlg nrd = new NewRuleDlg(shell, configRuleComp.getTree_rule().getItem(0).getText());
				nrd.open();
				configRuleComp.load();
			}
		});
		MenuItem menuItem_addRuleCategory = new MenuItem(menu_operation, SWT.PUSH);
		menuItem_addRuleCategory.setText("新建规则分类");
		menuItem_addRuleCategory.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = ruleConfigFolder;
				stackComp.layout();
				NewRuleCategoryDlg nrcd = new NewRuleCategoryDlg(shell);
				nrcd.open();
				configRuleComp.load();
			}
		});
		new MenuItem(menu_operation, SWT.SEPARATOR);
		MenuItem menuItem_exit = new MenuItem(menu_operation, SWT.PUSH);
		menuItem_exit.setText("退出");
		menuItem_exit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(onExit() == 0)
					System.exit(0);
			}
		});
		
		MenuItem view = new MenuItem(menuBar, SWT.CASCADE);
		view.setText("视图(&V)");
		view.setAccelerator(SWT.ALT + 'V');
		Menu menu_view = new Menu(shell, SWT.DROP_DOWN);
		view.setMenu(menu_view);
		configRuleItem = new MenuItem(menu_view, SWT.PUSH);
		configRuleItem.setText("规则管理");
		configRuleItem.setEnabled(false);
		configRuleItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = ruleConfigFolder;
				stackComp.layout();
			}
		});
		searchItem = new MenuItem(menu_view, SWT.PUSH);
		searchItem.setText("元搜索");
		searchItem.setEnabled(false);
		searchItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = searchFolder;
				stackComp.layout();
				autoSearchComp.loadRules();
			}
		});
		/*
		searchItem = new MenuItem(menu_view, SWT.PUSH);
		searchItem.setText("即时搜索");
		searchItem.setEnabled(false);
		searchItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = searchFolder;
				stackComp.layout();
			}
		});
		autoSearchItem = new MenuItem(menu_view, SWT.PUSH);
		autoSearchItem.setText("自动搜索");
		autoSearchItem.setEnabled(false);
		autoSearchItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = autoSearchFolder;
				stackComp.layout();
				autoSearchComp.loadRules();
			}
		});
		*/
		spiderItem = new MenuItem(menu_view, SWT.PUSH);
		spiderItem.setText("论坛搜索");
		spiderItem.setEnabled(false);
		spiderItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = spiderFolder;
				stackComp.layout();
			}
		});
		imageItem = new MenuItem(menu_view, SWT.PUSH);
		imageItem.setText("图片搜索");
		imageItem.setEnabled(false);
		imageItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = imageSearchFolder;
				stackComp.layout();
			}
		});
		lookupItem = new MenuItem(menu_view, SWT.PUSH);
		lookupItem.setText("信息查询");
		lookupItem.setEnabled(false);
		lookupItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = lookupFolder;
				stackComp.layout();
				lookupComp.loadRules();
			}
		});
		monitorItem = new MenuItem(menu_view, SWT.PUSH);
		monitorItem.setText("实时监控");
		monitorItem.setEnabled(false);
		monitorItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				/*
				stackLayout.topControl = monitorFolder;
				stackComp.layout();
				monitorComp.loadRules();
				*/
				if(monitorWin == null || MainWindow.monitorWin.shell.isDisposed()){
					monitorWin = new MonitorWindow();
					monitorWin.open(MainWindow.user);
				}
				else{
					monitorWin.shell.setVisible(true);
					monitorWin.shell.setActive();
					monitorWin.shell.setMinimized(false);
				}
			}
		});
		tracebackItem = new MenuItem(menu_view, SWT.PUSH);
		tracebackItem.setText("信息溯源");
		tracebackItem.setEnabled(false);
		tracebackItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = traceBackFolder;
				stackComp.layout();
			}
		});
		statisticsItem = new MenuItem(menu_view, SWT.PUSH);
		statisticsItem.setText("统计分析");
		statisticsItem.setEnabled(false);
		statisticsItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = statisticsFolder;
				stackComp.layout();
			}
		});
		systemInfoItem = new MenuItem(menu_view, SWT.PUSH);
		systemInfoItem.setText("系统信息");
		systemInfoItem.setEnabled(false);
		systemInfoItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = systemInfoFolder;
				stackComp.layout();
			}
		});
		new MenuItem(menu_view, SWT.SEPARATOR);
		userConfigItem = new MenuItem(menu_view, SWT.PUSH);
		userConfigItem.setText("用户管理");
		userConfigItem.setEnabled(false);
		userConfigItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = userConfigFolder;
				stackComp.layout();
			}
		});
		systemConfigItem = new MenuItem(menu_view, SWT.PUSH);
		systemConfigItem.setText("系统配置");
		systemConfigItem.setEnabled(false);
		systemConfigItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = systemConfigFolder;
				stackComp.layout();
			}
		});
		new MenuItem(menu_view, SWT.SEPARATOR);
		logItem = new MenuItem(menu_view, SWT.PUSH);
		logItem.setText("查看日志");
		logItem.setEnabled(false);
		logItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = logFolder;
				stackComp.layout();
			}
		});
		/*
		MenuItem window = new MenuItem(menuBar, SWT.CASCADE);
		window.setText("窗口(&W)");
		window.setAccelerator(SWT.ALT + 'w');
		Menu menu_window = new Menu(shell, SWT.DROP_DOWN);
		window.setMenu(menu_window);
		gotoMonitorItem = new MenuItem(menu_window, SWT.PUSH);
		gotoMonitorItem.setText("实时监控窗口");
		gotoMonitorItem.setImage(new Image(Display.getDefault(), "img/monitor(1).png"));
		gotoMonitorItem.setEnabled(false);
		gotoMonitorItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(monitorWin == null || MainWindow.monitorWin.shell.isDisposed()){
					monitorWin = new MonitorWindow();
					monitorWin.open(MainWindow.user);
				}
				else{
					monitorWin.shell.setVisible(true);
					monitorWin.shell.setActive();
					monitorWin.shell.setMinimized(false);
				}
			}
		});
		*/
		MenuItem about = new MenuItem(menuBar, SWT.CASCADE);
		about.setText("关于(&A)");
		about.setAccelerator(SWT.ALT + 'A');
		Menu menu_about = new Menu(shell, SWT.DROP_DOWN);
		about.setMenu(menu_about);
		MenuItem aboutItem = new MenuItem(menu_about, SWT.PUSH);
		aboutItem.setText("关于软件");
		aboutItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AboutDlg ad = new AboutDlg(shell, SWT.BORDER);
				ad.open();
			}
		});
		
		shell.setMenuBar(menuBar);
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
			}
		};
		
		final Tray tray = Display.getDefault().getSystemTray();
		trayItem = new TrayItem(tray, SWT.POP_UP);
		trayItem.setImage(SWTResourceManager.getImage("img/mix.gif"));
		trayItem.setVisible(true);
		trayItem.setToolTipText("系统正在运行……");
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
				if(onExit() == 0)
					System.exit(0);
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
	 * 处理登录的事件
	 */
	private void doLogin()
	{
		identity = combo_identity.getText().trim();
		username=text_username.getText().trim();
		password=text_password.getText().trim();
		
		if (username.equals("administrator")
				&& password.equals("administrator") && identity.equals("管理员")) {
			user = username;
			onManagerLogin();
		}
		else if (!up.isLegalUser(username, password, identity)) // 用户名不存在或者用户名、密码、登录身份三者不一致
		{
			MessageDialog.openError(shell, "错误登录信息", "对不起，登录信息有误，请检查！");
			text_username.setText("");
			text_password.setText("");

			String user = username;
			String operate = "登录";
			String detail = "登录出错！";
			LogProcess sl = new LogProcess();
			sl.saveLog(user, operate, detail);
		}
		else {
			user = username;
			String user = username;
			String operate = "登录";
			String detail = "登录成功！" + "登录身份为" + combo_identity.getText().trim();
			LogProcess sl = new LogProcess();
			sl.saveLog(user, operate, detail);

			if (identity.equals("普通用户")){
				onUserLogin();
			}
			else if (identity.equals("审计员")){
				onAuditorLogin();
			}
			else if (identity.equals("管理员")){
				onManagerLogin();
			}
			
			combo_identity.select(0);
			text_username.setText("");
			text_password.setText("");
		}
	}
	
	/**
	 * 普通用户登录事件
	 */
	private void onUserLogin() {
		MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION);
		box.setText("登录成功");
		box.setMessage("普通用户：" + username + "登录成功！");
		box.open();
		
		//初始化
		final LoadingDlg welcome = new LoadingDlg(shell);
		new Thread(new Runnable() {
			Display display = Display.getDefault();
			public void run() {
				rp = new RuleProcess(user);
			    cp = new CategoryProcess(user);
				display.syncExec(new Runnable(){
					public void run(){
						welcome.progressBar.setSelection(20);
						welcome.label_status.setText("载入规则……");
						lookupComp.loadRules();
						lookupSpiderComp.loadRules();
					    configRuleComp.load();
					    autoSearchComp.loadRules();
					}
				});
				display.syncExec(new Runnable(){
					public void run(){
					    welcome.progressBar.setSelection(40);
						welcome.label_status.setText("载入图片搜索结果……");
						imageComp.loadImageResult();
					}
				});
				display.syncExec(new Runnable(){
					public void run(){
					    welcome.progressBar.setSelection(60);
						welcome.label_status.setText("载入入口地址……");
						spiderComp.loadStoredEntry();
					}
				});
				display.syncExec(new Runnable(){
					public void run(){
					    welcome.progressBar.setSelection(80);
						welcome.label_status.setText("初始化哈希表……");
					}
				});
				bf2 = new BloomFilter2(1000000);
				display.syncExec(new Runnable(){
					public void run(){
						welcome.progressBar.setSelection(100);
						welcome.label_status.setText("载入完毕!");
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if(!welcome.getShell().isDisposed())
							welcome.getShell().dispose();
					}
				});
			}
		}).start();
		welcome.open();
		
		tree_user.setEnabled(true);
		tree_auditor.setEnabled(false);
		tree_manager.setEnabled(false);
		lookupItem.setEnabled(true);
		searchItem.setEnabled(true);
		//autoSearchItem.setEnabled(true);
		spiderItem.setEnabled(true);
		imageItem.setEnabled(true);
        configRuleItem.setEnabled(true);
        monitorItem.setEnabled(true);
        tracebackItem.setEnabled(true);
        statisticsItem.setEnabled(true);
        systemInfoItem.setEnabled(true);
        userConfigItem.setEnabled(false);
        systemConfigItem.setEnabled(false);
        logItem.setEnabled(false);
        //gotoMonitorItem.setEnabled(true);
		label_loginStatus.setText("欢迎：" + username 
				+ "，上次登录时间：" + up.getLoginTime(user));
		up.setLoginTime(user);
		tree_user.setFocus();
		tree_user.select(tree_user.getItem(0));
		stackLayout.topControl = ruleConfigFolder;
		stackComp.layout();
		
		//扫描图片的后台线程开始执行
		imageComp.scanThread.start();
	}
	
	/**
	 * 审计员登录事件
	 */
	private void onAuditorLogin() {
		MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION);
		box.setText("登录成功");
		box.setMessage("审计员：" + username + "登录成功！");
		box.open();
		
		//初始化
		final LoadingDlg welcome = new LoadingDlg(shell);
		new Thread(new Runnable() {
			Display display = Display.getDefault();
			public void run() {
				display.syncExec(new Runnable(){
					public void run(){
						welcome.progressBar.setSelection(20);
						welcome.label_status.setText("载入数据……");
						logComp.loadUsers();
					}
				});
				display.syncExec(new Runnable(){
					public void run(){
						welcome.progressBar.setSelection(100);
						welcome.label_status.setText("载入完毕!");
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if(!welcome.getShell().isDisposed())
							welcome.getShell().dispose();
					}
				});
			}
		}).start();
		welcome.open();
		
		tree_user.setEnabled(false);
		tree_auditor.setEnabled(true);
		tree_manager.setEnabled(false);
		lookupItem.setEnabled(false);
		searchItem.setEnabled(false);
		//autoSearchItem.setEnabled(false);
		spiderItem.setEnabled(false);
		imageItem.setEnabled(false);
        configRuleItem.setEnabled(false);
        monitorItem.setEnabled(false);
        tracebackItem.setEnabled(false);
        statisticsItem.setEnabled(false);
        systemInfoItem.setEnabled(false);
        userConfigItem.setEnabled(false);
        systemConfigItem.setEnabled(false);
        logItem.setEnabled(true);
        //gotoMonitorItem.setEnabled(false);
		label_loginStatus.setText("欢迎：" + username 
				+ "，上次登录时间：" + up.getLoginTime(user));
		up.setLoginTime(user);
		tree_auditor.setFocus();
		tree_auditor.select(tree_auditor.getItem(0));
		stackLayout.topControl = logFolder;
		stackComp.layout();
	}
	
	/**
	 * 管理员登录事件
	 */
	private void onManagerLogin() {
		MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION);
		box.setText("登录成功");
		box.setMessage("管理员：" + username + "登录成功！");
		box.open();
		
		//初始化
		final LoadingDlg welcome = new LoadingDlg(shell);
		new Thread(new Runnable() {
			Display display = Display.getDefault();
			public void run() {
				display.syncExec(new Runnable(){
					public void run(){
						welcome.progressBar.setSelection(20);
						welcome.label_status.setText("载入所有用户数据……");
					}
				});
				display.syncExec(new Runnable(){
					public void run(){
						userConfigComp.onRefresh();
					}
				});
				display.syncExec(new Runnable(){
					public void run(){
						welcome.progressBar.setSelection(100);
						welcome.label_status.setText("载入完毕!");
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if(!welcome.getShell().isDisposed())
							welcome.getShell().dispose();
					}
				});
			}
		}).start();
		welcome.open();
		
		tree_user.setEnabled(false);
		tree_auditor.setEnabled(false);
		tree_manager.setEnabled(true);
		lookupItem.setEnabled(false);
		searchItem.setEnabled(false);
		//autoSearchItem.setEnabled(false);
		spiderItem.setEnabled(false);
		imageItem.setEnabled(false);
        configRuleItem.setEnabled(false);
        monitorItem.setEnabled(false);
        tracebackItem.setEnabled(false);
        statisticsItem.setEnabled(false);
        systemInfoItem.setEnabled(false);
        userConfigItem.setEnabled(true);
        systemConfigItem.setEnabled(true);
        logItem.setEnabled(false);
        //gotoMonitorItem.setEnabled(false);
		label_loginStatus.setText("欢迎：" + username 
				+ "，上次登录时间：" + up.getLoginTime(user));
		up.setLoginTime(user);
		tree_manager.select(tree_manager.getItem(0));
		stackLayout.topControl = userConfigFolder;
		stackComp.layout();
	}
	
	/**
	 * 退出时的事件
	 */
	private int onExit() {
		MessageBox box = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION);
		box.setText("退出系统");
		box.setMessage("确认退出系统吗？");
		if(box.open() == SWT.YES){
			if(monitorWin != null && !monitorWin.shell.isDisposed() && !monitorWin.button_startSpider.getEnabled()
					&& monitorWin.button_stopSpider.getEnabled()){
				MessageBox box1 = new MessageBox(shell, SWT.ICON_WARNING);
				box1.setText("提示");
				box1.setMessage("检测到实时监控爬虫尚未完全停止，为了系统安全，现在不能关闭窗口。");
				box1.open();
				return 1;
			}
			else if(!spiderComp.button_startSpider.getEnabled()
					&& spiderComp.button_stopSpider.getEnabled()){
				MessageBox box1 = new MessageBox(shell, SWT.ICON_WARNING);
				box1.setText("提示");
				box1.setMessage("检测到论坛搜索爬虫尚未完全停止，为了系统安全，现在不能关闭窗口。");
				box1.open();
				return 1;
			}
			else{
				//结束线程
				if(!imageComp.scanThread.isStopFlag())
					imageComp.scanThread.setStopFlag(true);
				for(AutoSearchThread t : autoSearchTaskList)
					if(!t.getStopFlag())t.setStopFlag(true);
				
				//关闭数据库连接
				if(rp != null)rp.getConnDB().closeConnection();
				if(cp != null)cp.getConnDB().closeConnection();
				if(up != null)up.getConnDB().closeConnection();
				if(eap != null)eap.getConnDB().closeConnection();
				if(ucp != null)ucp.getConnDB().closeConnection();
				if(wrp != null)wrp.getConnDB().closeConnection();
				if(merp != null)merp.getConnDB().closeConnection();
				//if(swp != null)swp.getConnDB().closeConnection();
				if(ip != null)ip.getConnDB().closeConnection();
				
				if(!trayItem.isDisposed())
					trayItem.dispose();
				if(monitorWin != null && !monitorWin.trayItem.isDisposed())
					monitorWin.trayItem.dispose();
				
				SWTResourceManager.dispose();
				shell.dispose();
				
				System.out.println("退出系统");
				return 0;
			}	
		}
		return 1;
	}
	
	public static void setStatusLabel(String text, Image image) {
		if(statusInUse)return;
		statusInUse = true;
		StatusMsgLabel.setText(text);
		StatusMsgLabel.setImage(image);
		statusInUse = false;
	}
}
