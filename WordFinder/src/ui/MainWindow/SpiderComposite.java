package ui.MainWindow;

import java.util.LinkedList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.swtdesigner.SWTResourceManager;

import spider.CreateDir;
import spider.Queue;
import spider.ThreadPoolManager1;
import ui.MonitorWindow.ModifyCategoryDlg;
import ui.MonitorWindow.ModifyEntryAddressDlg;
import ui.MonitorWindow.NewCategoryDlg;
import Filter.BloomFilter1;
import dataStruct.EntryAddress;
import database.LogProcess;

public class SpiderComposite extends Composite {

	private Tree tree_entry;
	private Text text_entryUrl, text_entryName;
	private Table table_entry;
	private CLabel label_spiderImg;
	private Label label_spiderStatus;
	public Button button_startSpider, button_stopSpider;
	private Combo combo_entryCategory;
	private Menu menu_treeEntry;
	
	//爬虫用到的变量///////////////////////////////////////////////////////////////////////
	private int THREAD_NUMBER;// 设置线程数目
	private final int HashTableLength = 1000000;
	public final static long crawlTime = 60000;
	private ThreadPoolManager1 manager;
	private BloomFilter1 bf;
	public boolean crawlFlag = false;
	//////////////////////////////////////////////////////////////////////////////////////
	
	public SpiderComposite(Composite parent) {
		super(parent, SWT.BORDER);
		createContent();
		createTreeEntryMenu();
	}

	private void createContent() {
		this.setLayout(new GridLayout());
		
		Composite comp1 = new Composite(this, SWT.NONE);
		comp1.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp1.setLayout(new GridLayout(5,true));
		Group group_spider = new Group(comp1, SWT.NONE);
		group_spider.setText("爬虫状态");
		final GridData gd_group_spider = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_group_spider.heightHint = 70;
		group_spider.setLayoutData(gd_group_spider);

		label_spiderImg = new CLabel(group_spider, SWT.NONE);
		label_spiderImg.setBounds(33, 27, 117, 106);
		label_spiderImg.setImage(SWTResourceManager.getImage("img/debug_stopping.png"));

		label_spiderStatus = new Label(group_spider, SWT.NONE);
		label_spiderStatus.setText("爬虫已停止！");
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
		
		Group group_entry = new Group(comp1, SWT.NONE);
		group_entry.setText("爬虫正在处理的入口地址");
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
		table_entry.setBounds(10, 23, 470, 192);
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
					MessageBox box = new MessageBox(getShell(), SWT.ICON_WARNING);
					box.setText("非法操作");
					box.setMessage("请先停止爬虫，再对监控列表进行操作！");
					box.open();
				}
			}
		});
		button_removeEntry.setText("移除");
		button_removeEntry.setBounds(10, 221, 64, 24);
		button_removeEntry.setEnabled(false);
		
		Composite comp2 = new Composite(this, SWT.NONE);
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
		button_new.setBounds(95, 217, 68, 24);

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
		tree_entry.setBounds(10, 22, 470, 198);

		button_add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onAddEntry();
			}
		});
		button_add.setText("添加到监控列表");
		button_add.setBounds(146, 226, 114, 24);
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
	 * 创建入口地址树的右键菜单
	 */
	private void createTreeEntryMenu() {
		menu_treeEntry = new Menu(getShell(), SWT.POP_UP);
		
		MenuItem item_add = new MenuItem(menu_treeEntry, SWT.PUSH);
		item_add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				NewCategoryDlg ncd = new NewCategoryDlg(getShell());
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
					ModifyCategoryDlg mcd = new ModifyCategoryDlg(getShell(), selection);
					mcd.open();
					refreshEntry();
				}
				else{
					ModifyEntryAddressDlg mead = new ModifyEntryAddressDlg(getShell(), selection);
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
					if(MessageDialog.openConfirm(getShell(), "删除确认",
							"确定删除分类”" 
							+ selection 
							+ "“，以及其下的所有入口地址吗？")){
						if(MainWindow.ucp.deleteCategory(selection))
							refreshEntry();
					}
				}
				else{
					if(MessageDialog.openConfirm(getShell(), "删除确认",
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
	 * 载入已创建的入口地址
	 */
	public void loadStoredEntry()
	{
		String[] classes = MainWindow.ucp.getAllCategorynamesAsArray();
		if(classes == null)return;
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
							MessageBox box = new MessageBox(getShell(), SWT.ICON_WARNING);
							box.setText("入口为空");
							box.setMessage("请先选择要监控的入口地址！");
							box.open();
						}
					}
				});
				if(THREAD_NUMBER == 0)return;
				display.asyncExec(new Runnable(){
					public void run(){
						label_spiderStatus.setText("正在初始化哈希表……");
						button_startSpider.setEnabled(false);
					}
				});
				bf = new BloomFilter1(HashTableLength);// 创建过滤器
				display.asyncExec(new Runnable(){
					public void run(){
						label_spiderStatus.setText("正在启动爬虫……");
					}
				});
				try{
					boolean flag = false;
					
					manager = new ThreadPoolManager1(THREAD_NUMBER, bf,
							HashTableLength);// 启动线程池

					CreateDir cd = new CreateDir();
					cd.createDir();

					String address;

					System.out.println(THREAD_NUMBER);

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
					
					String operate = "论坛搜索";
					String detail =  "启动爬虫，入口地址个数为：  "+THREAD_NUMBER;
					System.out.println(detail+detail.length());
					LogProcess sl = new LogProcess();
					sl.saveLog(MainWindow.user, operate, detail);
					
					
				}catch(Exception e){
					e.printStackTrace();
				}
				display.asyncExec(new Runnable(){
					public void run(){
						final ToolTip tip = new ToolTip(getShell(), SWT.BALLOON
								| SWT.ICON_INFORMATION); // 显示托盘提示气球
						tip.setAutoHide(true); // 自动隐藏气泡式提示文本
						tip.setMessage("论坛搜索爬虫已启动。");// 设置提示信息
						tip.setText("爬虫启动");
						MainWindow.trayItem.setToolTip(tip);
						tip.setVisible(true); // 显示气泡式提示
						tip.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(SelectionEvent e) {
								tip.setVisible(false);
							}
						});
						
						label_spiderStatus.setText("爬虫正在运行！");
						label_spiderImg.setImage(SWTResourceManager.getImage("img/debug_running.gif"));
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
						label_spiderStatus.setText("正在停止爬虫……");
						button_stopSpider.setEnabled(false);
						THREAD_NUMBER = table_entry.getItemCount();
						
						String operate = "论坛搜索";
						String detail =  "停止爬虫操作！";
						LogProcess sl = new LogProcess();
						sl.saveLog(MainWindow.user, operate, detail);
						
					}
				});
				
				//执行停止线程工作的命令
				crawlFlag = manager.stopThread(THREAD_NUMBER);
				
				display.asyncExec(new Runnable(){
					public void run(){
						final ToolTip tip = new ToolTip(getShell(), SWT.BALLOON
								| SWT.ICON_INFORMATION); // 显示托盘提示气球
						tip.setAutoHide(true); // 自动隐藏气泡式提示文本
						tip.setMessage("论坛搜索爬虫已停止。");// 设置提示信息
						tip.setText("爬虫停止");
						MainWindow.trayItem.setToolTip(tip);
						tip.setVisible(true); // 显示气泡式提示
						tip.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(SelectionEvent e) {
								tip.setVisible(false);
							}
						});
						
						label_spiderStatus.setText("爬虫已停止！");
						label_spiderImg.setImage(SWTResourceManager.getImage("img/debug_stopping.png"));
						button_startSpider.setEnabled(true);
					}
				});
			}
		};
		
		new Thread(runnable).start();
	}
	
	/**
	 * 添加新入口地址
	 */
	private void onNewEntry(){
		if(text_entryUrl.getText().trim().replace("http://", "").equals("")){
			MessageBox box = new MessageBox(getShell(), SWT.ICON_INFORMATION);
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
				MessageBox box = new MessageBox(getShell(), SWT.ICON_INFORMATION);
				box.setText("入口重复");
				box.setMessage("入口地址" + address + "已存在数据库中！");
				box.open();
			}
			else if(MainWindow.eap.addEntryAddress(address, name, category)){
				MessageBox box = new MessageBox(getShell(), SWT.ICON_INFORMATION);
				box.setText("添加成功");
				box.setMessage("入口地址" + address + "已添加到数据库中！");
				box.open();
				for(TreeItem item : tree_entry.getItems())
					if(item.getText().equals(category)){
						TreeItem newItem = new TreeItem(item, SWT.NONE);
						newItem.setText(address);
						break;
					}
				text_entryName.setText("");
				text_entryUrl.setText("http://");
				combo_entryCategory.select(0);
			}
			else{
				MessageBox box = new MessageBox(getShell(), SWT.ICON_INFORMATION);
				box.setText("添加失败");
				box.setMessage("入口地址" + address + "已存在数据库中！");
				box.open();
			}
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
							MessageBox box = new MessageBox(getShell(), SWT.ICON_INFORMATION);
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
							MessageBox box = new MessageBox(getShell(), SWT.ICON_INFORMATION);
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
			MessageBox box = new MessageBox(getShell(), SWT.ICON_WARNING);
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
		MessageBox box = new MessageBox(getShell(), SWT.YES | SWT.NO
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
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
