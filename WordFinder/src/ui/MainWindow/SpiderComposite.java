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
	
	//�����õ��ı���///////////////////////////////////////////////////////////////////////
	private int THREAD_NUMBER;// �����߳���Ŀ
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
		group_spider.setText("����״̬");
		final GridData gd_group_spider = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_group_spider.heightHint = 70;
		group_spider.setLayoutData(gd_group_spider);

		label_spiderImg = new CLabel(group_spider, SWT.NONE);
		label_spiderImg.setBounds(33, 27, 117, 106);
		label_spiderImg.setImage(SWTResourceManager.getImage("img/debug_stopping.png"));

		label_spiderStatus = new Label(group_spider, SWT.NONE);
		label_spiderStatus.setText("������ֹͣ��");
		label_spiderStatus.setBounds(167, 64, 111, 25);

		button_startSpider = new Button(group_spider, SWT.NONE);
		button_startSpider.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onStartSpider();
			}
		});
		button_startSpider.setText("����");
		button_startSpider.setImage(SWTResourceManager.getImage("img/startSpider.png"));
		button_startSpider.setBounds(34, 150, 73, 36);

		button_stopSpider = new Button(group_spider, SWT.NONE);
		button_stopSpider.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onStopSpider();
			}
		});
		button_stopSpider.setImage(SWTResourceManager.getImage("img/stopSpider.png"));
		button_stopSpider.setText("ֹͣ");
		button_stopSpider.setBounds(145, 150, 73, 36);
		button_stopSpider.setEnabled(false);
		
		Group group_entry = new Group(comp1, SWT.NONE);
		group_entry.setText("�������ڴ������ڵ�ַ");
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
		name.setText("����");
		name.setWidth(93);
		TableColumn url = new TableColumn(table_entry, SWT.CENTER);
		url.setText("����");
		url.setWidth(335);
		
		button_removeEntry.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(button_startSpider.getEnabled() && !button_stopSpider.getEnabled()){
					table_entry.remove(table_entry.getSelectionIndices());
				}
				else{
					MessageBox box = new MessageBox(getShell(), SWT.ICON_WARNING);
					box.setText("�Ƿ�����");
					box.setMessage("����ֹͣ���棬�ٶԼ���б���в�����");
					box.open();
				}
			}
		});
		button_removeEntry.setText("�Ƴ�");
		button_removeEntry.setBounds(10, 221, 64, 24);
		button_removeEntry.setEnabled(false);
		
		Composite comp2 = new Composite(this, SWT.NONE);
		comp2.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp2.setLayout(new GridLayout(5,true));

		final Group group_newEntry = new Group(comp2, SWT.NONE);
		group_newEntry.setText("��������");
		final GridData gd_group_newEntry = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_group_newEntry.heightHint = 70;
		group_newEntry.setLayoutData(gd_group_newEntry);

		final Label label_entryNmae = new Label(group_newEntry, SWT.NONE);
		label_entryNmae.setText("������ƣ�");
		label_entryNmae.setBounds(10, 33, 60, 14);

		text_entryName = new Text(group_newEntry, SWT.BORDER);
		text_entryName.setBounds(76, 30, 239, 25);

		final Label label_entryUrl = new Label(group_newEntry, SWT.NONE);
		label_entryUrl.setText("���ӵ�ַ��");
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
		button_new.setText("���");
		button_new.setBounds(95, 217, 68, 24);

		final Button button_reset = new Button(group_newEntry, SWT.NONE);
		button_reset.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				text_entryName.setText("");
				text_entryUrl.setText("http://");
				combo_entryCategory.select(0);
			}
		});
		button_reset.setText("����");
		button_reset.setBounds(10, 217, 68, 24);

		final Label label_entryCategory = new Label(group_newEntry, SWT.NONE);
		label_entryCategory.setText("�������ࣺ");
		label_entryCategory.setBounds(10, 73, 60, 25);

		combo_entryCategory = new Combo(group_newEntry, SWT.NONE);
		combo_entryCategory.select(0);
		combo_entryCategory.setBounds(76, 70, 113, 22);
		
		final Group group_allEntry = new Group(comp2, SWT.NONE);
		group_allEntry.setText("�ѽ��������");
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
		button_add.setText("��ӵ�����б�");
		button_add.setBounds(146, 226, 114, 24);
		button_add.setEnabled(false);

		button_delete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onDeleteEntry();
			}
		});
		button_delete.setText("ɾ����ڵ�ַ");
		button_delete.setBounds(10, 226, 114, 24);
		button_delete.setEnabled(false);
	}
	
	/**
	 * ������ڵ�ַ�����Ҽ��˵�
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
		item_add.setText("��������");
		
		new MenuItem(menu_treeEntry, SWT.SEPARATOR);
		
		MenuItem item_modify = new MenuItem(menu_treeEntry, SWT.PUSH);
		item_modify.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(tree_entry.getSelectionCount() == 0)return;
				String selection = tree_entry.getSelection()[0].getText();
				if(MainWindow.ucp.isCategoryExisted(selection)){//�����
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
		item_modify.setText("�޸�");
		
		MenuItem item_delete = new MenuItem(menu_treeEntry, SWT.PUSH);
		item_delete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(tree_entry.getSelectionCount() == 0)return;
				String selection = tree_entry.getSelection()[0].getText();
				if(MainWindow.ucp.isCategoryExisted(selection)){//�����
					if(MessageDialog.openConfirm(getShell(), "ɾ��ȷ��",
							"ȷ��ɾ�����ࡱ" 
							+ selection 
							+ "�����Լ����µ�������ڵ�ַ��")){
						if(MainWindow.ucp.deleteCategory(selection))
							refreshEntry();
					}
				}
				else{
					if(MessageDialog.openConfirm(getShell(), "ɾ��ȷ��",
							"ȷ��ɾ����ڵ�ַ��" 
							+ MainWindow.eap.getEntryAddressNameByUrl(selection) 
							+ "����" + selection + "����")){
						MainWindow.eap.deleteEntryAddress(selection);
						refreshEntry();
					}
				}
			}
		});
		item_delete.setText("ɾ��");
		
		tree_entry.setMenu(menu_treeEntry);
	}
	
	/**
	 * �����Ѵ�������ڵ�ַ
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
	 * ��������ʱ�Ķ���
	 */
	private void onStartSpider()
	{
		Runnable runnable  = new Runnable(){
			Display display = Display.getCurrent();
			Queue entryAddress = new Queue();//��ڵ�ַ����
			public void run(){	   
				display.asyncExec(new Runnable(){
					public void run(){
						//������ڵ�ַ
						for(TableItem item : table_entry.getItems())
							entryAddress.enQueue(item.getText(1));	
						//��ȡ��ڵ�ַ����
						THREAD_NUMBER = entryAddress.getLength();
						if(THREAD_NUMBER == 0){
							MessageBox box = new MessageBox(getShell(), SWT.ICON_WARNING);
							box.setText("���Ϊ��");
							box.setMessage("����ѡ��Ҫ��ص���ڵ�ַ��");
							box.open();
						}
					}
				});
				if(THREAD_NUMBER == 0)return;
				display.asyncExec(new Runnable(){
					public void run(){
						label_spiderStatus.setText("���ڳ�ʼ����ϣ����");
						button_startSpider.setEnabled(false);
					}
				});
				bf = new BloomFilter1(HashTableLength);// ����������
				display.asyncExec(new Runnable(){
					public void run(){
						label_spiderStatus.setText("�����������桭��");
					}
				});
				try{
					boolean flag = false;
					
					manager = new ThreadPoolManager1(THREAD_NUMBER, bf,
							HashTableLength);// �����̳߳�

					CreateDir cd = new CreateDir();
					cd.createDir();

					String address;

					System.out.println(THREAD_NUMBER);

					// manager.process("http://bbs.51job.com/");
					while (!entryAddress.isQueueEmpty()) { // ѭ����ȡ��ڵ�ַ
						address = entryAddress.deQueue();
						flag = manager.process(address);
						while (!flag) {
							System.out.println("waiting....");
							Thread.sleep(60000);
							flag = manager.process(address);
						}
					}
					
					String operate = "��̳����";
					String detail =  "�������棬��ڵ�ַ����Ϊ��  "+THREAD_NUMBER;
					System.out.println(detail+detail.length());
					LogProcess sl = new LogProcess();
					sl.saveLog(MainWindow.user, operate, detail);
					
					
				}catch(Exception e){
					e.printStackTrace();
				}
				display.asyncExec(new Runnable(){
					public void run(){
						final ToolTip tip = new ToolTip(getShell(), SWT.BALLOON
								| SWT.ICON_INFORMATION); // ��ʾ������ʾ����
						tip.setAutoHide(true); // �Զ���������ʽ��ʾ�ı�
						tip.setMessage("��̳����������������");// ������ʾ��Ϣ
						tip.setText("��������");
						MainWindow.trayItem.setToolTip(tip);
						tip.setVisible(true); // ��ʾ����ʽ��ʾ
						tip.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(SelectionEvent e) {
								tip.setVisible(false);
							}
						});
						
						label_spiderStatus.setText("�����������У�");
						label_spiderImg.setImage(SWTResourceManager.getImage("img/debug_running.gif"));
						button_stopSpider.setEnabled(true);
					}
				});
			}
		};
		new Thread(runnable).start();
	}
	
	/**
	 * ��ֹ����ʱ�Ķ���
	 */
	private void onStopSpider()
	{
		Runnable runnable = new Runnable(){
			Display display = Display.getCurrent();
			public void run(){
				display.asyncExec(new Runnable(){
					public void run(){
						label_spiderStatus.setText("����ֹͣ���桭��");
						button_stopSpider.setEnabled(false);
						THREAD_NUMBER = table_entry.getItemCount();
						
						String operate = "��̳����";
						String detail =  "ֹͣ���������";
						LogProcess sl = new LogProcess();
						sl.saveLog(MainWindow.user, operate, detail);
						
					}
				});
				
				//ִ��ֹͣ�̹߳���������
				crawlFlag = manager.stopThread(THREAD_NUMBER);
				
				display.asyncExec(new Runnable(){
					public void run(){
						final ToolTip tip = new ToolTip(getShell(), SWT.BALLOON
								| SWT.ICON_INFORMATION); // ��ʾ������ʾ����
						tip.setAutoHide(true); // �Զ���������ʽ��ʾ�ı�
						tip.setMessage("��̳����������ֹͣ��");// ������ʾ��Ϣ
						tip.setText("����ֹͣ");
						MainWindow.trayItem.setToolTip(tip);
						tip.setVisible(true); // ��ʾ����ʽ��ʾ
						tip.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(SelectionEvent e) {
								tip.setVisible(false);
							}
						});
						
						label_spiderStatus.setText("������ֹͣ��");
						label_spiderImg.setImage(SWTResourceManager.getImage("img/debug_stopping.png"));
						button_startSpider.setEnabled(true);
					}
				});
			}
		};
		
		new Thread(runnable).start();
	}
	
	/**
	 * �������ڵ�ַ
	 */
	private void onNewEntry(){
		if(text_entryUrl.getText().trim().replace("http://", "").equals("")){
			MessageBox box = new MessageBox(getShell(), SWT.ICON_INFORMATION);
			box.setText("�������");
			box.setMessage("�����������ĵ�ַ��");
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
				box.setText("����ظ�");
				box.setMessage("��ڵ�ַ" + address + "�Ѵ������ݿ��У�");
				box.open();
			}
			else if(MainWindow.eap.addEntryAddress(address, name, category)){
				MessageBox box = new MessageBox(getShell(), SWT.ICON_INFORMATION);
				box.setText("��ӳɹ�");
				box.setMessage("��ڵ�ַ" + address + "����ӵ����ݿ��У�");
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
				box.setText("���ʧ��");
				box.setMessage("��ڵ�ַ" + address + "�Ѵ������ݿ��У�");
				box.open();
			}
		}
	}
	
	/**
	 * ����ڵ�ַ����ѡ�еĵ�ַ��ӵ�����б���
	 */
	private void onAddEntry(){
		if(button_startSpider.getEnabled() && !button_stopSpider.getEnabled()){
			LinkedList<String> classes = MainWindow.ucp.getAllCategorynames();
			TreeItem[] selection = tree_entry.getSelection();
			for(int i=0;i<selection.length;i++){
				if(classes.indexOf(selection[i].getText())==-1){//���������
					//���ж��ظ�
					TableItem[] oldItems = table_entry.getItems();
					for(TableItem item : oldItems)
						if(item.getText(1).equals(selection[i].getText())){
							MessageBox box = new MessageBox(getShell(), SWT.ICON_INFORMATION);
							box.setText("����ظ�");
							box.setMessage("��ڵ�ַ" + selection[i].getText() + "�Ѵ����б��У�");
							box.open();
							return;
						}
					EntryAddress ea
					       = MainWindow.eap.getEntryAddressByUrl(selection[i].getText());
					TableItem newAddr = new TableItem(table_entry, SWT.NONE);
					newAddr.setText(new String[]{ea.getName(), ea.getUrl()});
				}
				else{                                 //����𣬷ֱ��
					LinkedList<EntryAddress> eaList =
						MainWindow.eap.getEntryAddressByCategory(selection[i].getText());
					boolean dup;
					for(EntryAddress ea : eaList){
						//���ж��ظ�
						TableItem[] oldItems = table_entry.getItems();
						dup = false;
						for(TableItem item : oldItems)
							if(item.getText(1).equals(ea.getUrl())){
								dup = true;
								break;
							}
						if(dup){
							MessageBox box = new MessageBox(getShell(), SWT.ICON_INFORMATION);
							box.setText("����ظ�");
							box.setMessage("��ڵ�ַ" + ea.getUrl() + "�Ѿ��ڼ���б����ˣ�");
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
			box.setText("�Ƿ�����");
			box.setMessage("����ֹͣ���棬�ٶԼ���б���в�����");
			box.open();
		}
	}
	
	/**
	 * ����ڵ�ַ����ɾ����ַ
	 */
	private void onDeleteEntry(){
		int choice;
		MessageBox box = new MessageBox(getShell(), SWT.YES | SWT.NO
				| SWT.ICON_INFORMATION);
		box.setText("��ʾ");
		box.setMessage("ȷ��ɾ����");
		choice = box.open();
		if(choice == SWT.YES){
			String selection = tree_entry.getSelection()[0].getText();
			if (!MainWindow.ucp.isCategoryExisted(selection)) {// ���������
				MainWindow.eap.deleteEntryAddress(selection);
				tree_entry.getSelection()[0].dispose();
			} else { // �����
				MainWindow.eap.deleteEntryAddressByCategory(selection);
				tree_entry.getSelection()[0].dispose();
			}
		}
	}
	
	/**
	 * ˢ����ڵ�ַҳ��Ĳ���
	 */
	private void refreshEntry() {
		//���²��� 
		String[] classes = MainWindow.ucp.getAllCategorynamesAsArray();
		String temp = combo_entryCategory.getText();
		combo_entryCategory.setItems(classes);
		combo_entryCategory.select(combo_entryCategory.indexOf(temp));
		//��������б�
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
