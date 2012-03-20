package ui.MainWindow;

import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import com.swtdesigner.SWTResourceManager;

import dataStruct.Rule;

import searchUtility.CurrentTime;
import searchUtility.AutoSearchThread;

public class AutoSearchComposite extends Composite {
	private Text text_taskInfo;
	private Table autoSearchTaskTable;
	private List list_des;
	private Tree tree_src;
	private Button engineSelectorBaidu, engineSelectorBing, engineSelectorGoogle,
                   engineSelectorSogou, engineSelectorSoso, engineSelectorYahoo,
                   engineSelectorYoudao;
	private Spinner spinner_day, spinner_hour, spinner_minute;
	
	public AutoSearchComposite(Composite parent) {
		super(parent, SWT.BORDER);
		createContent();
	}
	
	private void createContent() {
		this.setLayout(new GridLayout());
		
		Composite comp1 = new Composite(this, SWT.NONE);
		final GridData gd_comp1 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_comp1.heightHint = 300;
		comp1.setLayoutData(gd_comp1);
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
		item_start.setToolTipText("��ʼ");
		item_start.setText("��ʼ");
		item_start.setImage(SWTResourceManager.getImage("img/startTask.png"));
		
		ToolItem item_startAll = new ToolItem(toolbar, SWT.PUSH);
		item_startAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onStartAllTask();
				onUpdateWarningInfo();
			}
		});
		item_startAll.setToolTipText("ȫ����ʼ");
		item_startAll.setText("ȫ����ʼ");
		item_startAll.setImage(SWTResourceManager.getImage("img/startAllTask.png"));	
		
		new ToolItem(toolbar, SWT.SEPARATOR);
		
		ToolItem item_pause = new ToolItem(toolbar, SWT.PUSH);
		item_pause.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onPauseTask();
				onUpdateWarningInfo();
			}
		});
		item_pause.setToolTipText("��ͣ");
		item_pause.setText("��ͣ");
		item_pause.setImage(SWTResourceManager.getImage("img/pauseTask.png"));
		
		ToolItem item_pauseAll = new ToolItem(toolbar, SWT.PUSH);
		item_pauseAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onPauseAllTask();
				onUpdateWarningInfo();
			}
		});
		item_pauseAll.setToolTipText("ȫ����ͣ");
		item_pauseAll.setText("ȫ����ͣ");
		item_pauseAll.setImage(SWTResourceManager.getImage("img/pauseAllTask.png"));
		
		new ToolItem(toolbar, SWT.SEPARATOR);
	
		ToolItem item_delete = new ToolItem(toolbar, SWT.PUSH);
		item_delete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onDeleteTask();
				onUpdateWarningInfo();
			}
		});
		item_delete.setToolTipText("ɾ��");
		item_delete.setText("ɾ��");
		item_delete.setImage(SWTResourceManager.getImage("img/deleteTask.png"));
		
		ToolItem item_deleteAll = new ToolItem(toolbar, SWT.PUSH);
		item_deleteAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onDeleteAllTask();
				text_taskInfo.setText("");
			}
		});
		item_deleteAll.setToolTipText("ȫ��ɾ��");
		item_deleteAll.setText("ȫ��ɾ��");
		item_deleteAll.setImage(SWTResourceManager.getImage("img/deleteAllTask.png"));
		
		toolbar.pack();
		
		autoSearchTaskTable = new Table(comp11, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		autoSearchTaskTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onUpdateWarningInfo();
			}
		});
		autoSearchTaskTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		autoSearchTaskTable.setHeaderVisible(true);
		autoSearchTaskTable.setLinesVisible(true);
		TableColumn tableColumnTheme = new TableColumn(autoSearchTaskTable, SWT.LEFT);
		tableColumnTheme.setWidth(80);
		tableColumnTheme.setText("��������");
		TableColumn tableColumnStartTime = new TableColumn(autoSearchTaskTable, SWT.LEFT);
		tableColumnStartTime.setWidth(130);
		tableColumnStartTime.setText("������ʱ��");
		TableColumn tableColumnSensativity = new TableColumn(autoSearchTaskTable, SWT.LEFT);
		tableColumnSensativity.setWidth(65);
		tableColumnSensativity.setText("ʱ����");
		TableColumn tableColumnStatus = new TableColumn(autoSearchTaskTable, SWT.LEFT);
		tableColumnStatus.setWidth(65);
		tableColumnStatus.setText("����״̬");
		
		final Group comp12 = new Group(comp1, SWT.NONE);
		comp12.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp12.setLayout(new GridLayout());
		comp12.setText("������Ϣ");
		text_taskInfo = new Text(comp12, SWT.CENTER | SWT.WRAP);
		text_taskInfo.setLayoutData(new GridData(GridData.FILL_BOTH));
		text_taskInfo.setText("������Ϣ");
		text_taskInfo.setEditable(false);
		
		ScrolledComposite comp2 = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
		final GridData gd_comp2 = new GridData(GridData.FILL_BOTH);
		comp2.setLayoutData(gd_comp2);
		comp2.setLayout(new FillLayout());
		
		final Group group_newTask = new Group(comp2, SWT.NONE);
		group_newTask.setSize(900, 315);
		comp2.setContent(group_newTask);
		group_newTask.setText("���������");

		final Group group_selectRule = new Group(group_newTask, SWT.NONE);
		group_selectRule.setText("ѡ�����");
		group_selectRule.setBounds(10, 22, 355, 238);

		tree_src = new Tree(group_selectRule, SWT.BORDER);
		tree_src.setBounds(10, 21, 161, 207);

		list_des = new List(group_selectRule, SWT.BORDER);
		list_des.setBounds(225, 21, 120, 207);

		final Button button_yes = new Button(group_selectRule, SWT.NONE);
		button_yes.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				TreeItem[] selection = tree_src.getSelection();
				for (int i = 0; i < selection.length; i++){
					TreeItem item = selection[i];
					LinkedList<String> names = MainWindow.cp.getAllCategorynames();			
					if(names.indexOf(item.getText()) == -1){//���������
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
		button_yes.setBounds(177, 34, 42, 24);

		final Button button_no = new Button(group_selectRule, SWT.NONE);
		button_no.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				list_des.remove(list_des.getSelectionIndices());
			}
		});
		button_no.setText("<<");
		button_no.setBounds(177, 77, 42, 24);

		final Button button_refreshRule = new Button(group_selectRule, SWT.NONE);
		button_refreshRule.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				loadRules();
			}
		});
		button_refreshRule.setText("ˢ��");
		button_refreshRule.setBounds(177, 204, 42, 24);

		final Group group_selectEngine = new Group(group_newTask, SWT.NONE);
		group_selectEngine.setText("ѡ����������");
		group_selectEngine.setBounds(371, 22, 401, 178);

		engineSelectorBaidu = new Button(group_selectEngine, SWT.CHECK | SWT.FLAT);
		engineSelectorBaidu.setSelection(true);
		engineSelectorBaidu.setImage(SWTResourceManager.getImage("img/baidu.gif"));
		engineSelectorBaidu.setBounds(10, 23, 101, 29);
		engineSelectorBing = new Button(group_selectEngine, SWT.CHECK | SWT.FLAT);
		engineSelectorBing.setSelection(true);
		engineSelectorBing.setImage(SWTResourceManager.getImage("img/bing.gif"));
		engineSelectorBing.setBounds(148, 23, 101, 29);
		engineSelectorGoogle = new Button(group_selectEngine, SWT.CHECK | SWT.FLAT);
		engineSelectorGoogle.setSelection(true);
		engineSelectorGoogle.setImage(SWTResourceManager.getImage("img/google.gif"));
		engineSelectorGoogle.setBounds(290, 23, 101, 29);
		engineSelectorSogou = new Button(group_selectEngine, SWT.CHECK | SWT.FLAT);
		engineSelectorSogou.setSelection(true);
		engineSelectorSogou.setImage(SWTResourceManager.getImage("img/sogou.gif"));
		engineSelectorSogou.setBounds(10, 69, 101, 29);
		engineSelectorSoso = new Button(group_selectEngine, SWT.CHECK | SWT.FLAT);
		engineSelectorSoso.setSelection(true);
		engineSelectorSoso.setImage(SWTResourceManager.getImage("img/soso.gif"));
		engineSelectorSoso.setBounds(148, 69, 101, 29);
		engineSelectorYahoo = new Button(group_selectEngine, SWT.CHECK | SWT.FLAT);
		engineSelectorYahoo.setSelection(true);
		engineSelectorYahoo.setImage(SWTResourceManager.getImage("img/yahoo.gif"));
		engineSelectorYahoo.setBounds(290, 69, 101, 29);
		engineSelectorYoudao = new Button(group_selectEngine, SWT.CHECK | SWT.FLAT);
		engineSelectorYoudao.setSelection(true);
		engineSelectorYoudao.setImage(SWTResourceManager.getImage("img/youdao.gif"));
		engineSelectorYoudao.setBounds(10, 117, 101, 34);

		final Button button_selectAll = new Button(group_selectEngine, SWT.CHECK | SWT.FLAT);
		button_selectAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				engineSelectorBaidu.setSelection(button_selectAll.getSelection());
				engineSelectorBing.setSelection(button_selectAll.getSelection());
				engineSelectorGoogle.setSelection(button_selectAll.getSelection());
				engineSelectorSogou.setSelection(button_selectAll.getSelection());
				engineSelectorSoso.setSelection(button_selectAll.getSelection());
				engineSelectorYahoo.setSelection(button_selectAll.getSelection());
				engineSelectorYoudao.setSelection(button_selectAll.getSelection());
			}
		});
		button_selectAll.setSelection(true);
		button_selectAll.setText("ȫѡ");
		button_selectAll.setBounds(306, 152, 85, 16);
		
		final Button button_addTask = new Button(group_newTask, SWT.NONE);
		button_addTask.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onAddAutoSearchTask();
			}
		});
		button_addTask.setText("���");
		button_addTask.setImage(SWTResourceManager.getImage("img/newTask.png"));
		button_addTask.setBounds(320, 266, 94, 34);

		final Group group_interval = new Group(group_newTask, SWT.NONE);
		group_interval.setText("����ʱ����");
		group_interval.setBounds(371, 206, 401, 54);

		spinner_day = new Spinner(group_interval, SWT.NONE);
		spinner_day.setMaximum(30);
		spinner_day.setBounds(37, 23, 60, 17);

		final Label label = new Label(group_interval, SWT.NONE);
		label.setText("��");
		label.setBounds(103, 23, 35, 14);

		spinner_hour = new Spinner(group_interval, SWT.NONE);
		spinner_hour.setMaximum(23);
		spinner_hour.setBounds(144, 23, 60, 17);

		final Label label_1 = new Label(group_interval, SWT.NONE);
		label_1.setText("Сʱ");
		label_1.setBounds(210, 23, 35, 14);

		spinner_minute = new Spinner(group_interval, SWT.NONE);
		spinner_minute.setIncrement(5);
		spinner_minute.setMinimum(5);
		spinner_minute.setMaximum(59);
		spinner_minute.setBounds(251, 23, 60, 17);

		final Label label_2 = new Label(group_interval, SWT.NONE);
		label_2.setText("����");
		label_2.setBounds(317, 23, 39, 14);		
	}

	/**
	 * ������������ʱ�Ķ���
	 */
	private void onStartTask()
	{
		int[] select = autoSearchTaskTable.getSelectionIndices();
		for (int i = 0; i < select.length; i++) {
			AutoSearchThread st = MainWindow.autoSearchTaskList.get(select[i]);
			if (!st.getIsRunning()) {
				st.setIsRunning(true);
				autoSearchTaskTable.getItem(select[i]).setText(3, "������");
				autoSearchTaskTable.getItem(select[i]).setImage(0,
						SWTResourceManager.getImage("img/startStatus.png"));
			}
		}
	}
	
	/**
	 * ������������
	 */
	private void onStartAllTask(){
		TableItem[] items = autoSearchTaskTable.getItems();
		for (int i = 0; i < items.length; i++){
			AutoSearchThread st = MainWindow.autoSearchTaskList.get(i);
			if (!st.getIsRunning()) {
				st.setIsRunning(true);
				autoSearchTaskTable.getItem(i).setText(3, "������");
				autoSearchTaskTable.getItem(i).setImage(0,
						SWTResourceManager.getImage("img/startStatus.png"));
			}
		}
	}
	
	/**
	 * ������ͣ����ʱ�Ķ���
	 */
	private void onPauseTask()
	{
		int[] select = autoSearchTaskTable.getSelectionIndices();
		for (int i = 0; i < select.length; i++){
			AutoSearchThread st = MainWindow.autoSearchTaskList.get(select[i]);
			if(st.getIsRunning()){
				st.setIsRunning(false);
				autoSearchTaskTable.getItem(select[i]).setText(3, "��ͣ");
				autoSearchTaskTable.getItem(select[i]).setImage(0,
						SWTResourceManager.getImage("img/pauseStatus.png"));
			}
		}
	}
	
	/**
	 * ��ͣ��������
	 */
	private void onPauseAllTask(){
		TableItem[] items = autoSearchTaskTable.getItems();
		for (int i = 0; i < items.length; i++){
			AutoSearchThread st = MainWindow.autoSearchTaskList.get(i);
			if(st.getIsRunning()){
				st.setIsRunning(false);
				autoSearchTaskTable.getItem(i).setText(3, "��ͣ");
				autoSearchTaskTable.getItem(i).setImage(0,
						SWTResourceManager.getImage("img/pauseStatus.png"));
			}
		}
	}
	
	/**
	 * ����ɾ��ĳһ����ʱ�Ķ���
	 */
	private void onDeleteTask()
	{
		int[] select = autoSearchTaskTable.getSelectionIndices();
		for (int i = 0; i < select.length; i++){
			AutoSearchThread st = MainWindow.autoSearchTaskList.get(select[i]);
			if(!st.getStopFlag()){
				st.setStopFlag(true);
			}
		}
		for(int i=select.length-1;i>-1;i--){
			MainWindow.autoSearchTaskList.remove(select[i]);
		}
		autoSearchTaskTable.remove(select);
		text_taskInfo.setText("");
	}
	
	/**
	 * ɾ����������
	 */
	private void onDeleteAllTask()
	{
		for (int i = 0; i < MainWindow.autoSearchTaskList.size(); i++){
			AutoSearchThread st = MainWindow.autoSearchTaskList.get(i);
			if(!st.getStopFlag()){
				st.setStopFlag(true);
			}
		}

		MainWindow.autoSearchTaskList.clear();
		autoSearchTaskTable.removeAll();
	}
	
	/**
	 * ����������Ϣ���
	 */
	private void onUpdateWarningInfo(){
		int index = autoSearchTaskTable.getSelectionIndex();
		if(index == -1){
			text_taskInfo.setText("");
			return;
		}
		AutoSearchThread st = MainWindow.autoSearchTaskList.get(index);
		
		text_taskInfo.setText("\n\n\n�������ƣ�" + st.getRule().getName());
		text_taskInfo.append("\n\n����״̬��" + (st.getIsRunning()?"��������":"��ͣ"));
		text_taskInfo.append("\n\n�ؼ��ʣ�" + st.getRule().getWords());
		text_taskInfo.append("\n\n���жȣ�" + st.getSensativity());
		text_taskInfo.append("\n\nʹ�õ��������棺");
		if(st.isBaidu_selected())text_taskInfo.append("�ٶ�  ");
		if(st.isBing_selected())text_taskInfo.append("��Ӧ  ");
		if(st.isGoogle_selected())text_taskInfo.append("�ȸ�  ");
		if(st.isSogou_selected())text_taskInfo.append("�ѹ�  ");
		if(st.isSoso_selected())text_taskInfo.append("����  ");
		if(st.isYahoo_selected())text_taskInfo.append("�Ż�  ");
		if(st.isYoudao_selected())text_taskInfo.append("�е�  ");
	}
	
	/**
	 * ����Զ���������ʱ�Ķ���
	 */
	private void onAddAutoSearchTask()
	{
		String time = CurrentTime.getInstance();
		String totalRule = "";
		String interval = (spinner_day.getText().equals("0") ? "" : (spinner_day.getText()+"�� "))+
        (spinner_hour.getText().equals("0") ? "" : (spinner_hour.getText()+"Сʱ "))+
        (spinner_minute.getText().equals("0") ? "" : (spinner_minute.getText()+"����"));
		for (int k = 0; k < list_des.getItemCount(); ++k){
			String RuleName = list_des.getItem(k);
			TableItem t = new TableItem(autoSearchTaskTable, SWT.NONE);
			t.setText(new String[]{RuleName, time, interval, "��ͣ"});
			t.setImage(0, SWTResourceManager.getImage("img/pauseStatus.png"));
			
			long inter = (Integer.parseInt(spinner_day.getText())*86400 +
		             Integer.parseInt(spinner_hour.getText())*3600 +
		             Integer.parseInt(spinner_minute.getText())*60)
		             * 1000;     //�����������λΪms
			Rule rule = MainWindow.rp.getRuleByName(RuleName);
			AutoSearchThread thread = new AutoSearchThread(rule, inter
					                             ,engineSelectorBaidu.getSelection()
					                             ,engineSelectorBing.getSelection()
					                             ,engineSelectorGoogle.getSelection()
					                             ,engineSelectorSogou.getSelection()
					                             ,engineSelectorSoso.getSelection()
					                             ,engineSelectorYahoo.getSelection()
					                             ,engineSelectorYoudao.getSelection());
			thread.start();
			MainWindow.autoSearchTaskList.add(thread);
			
			totalRule += (k+1) + ":" + rule.getName()+";";
			
		}
		list_des.removeAll();
		
	}
	
	/**
	 * �����Ѵ����Ĺ���
	 */
	public void loadRules(){
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
}
