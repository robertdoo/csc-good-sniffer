package ui.MainWindow;

import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import searchUtility.ImediateSearchThread;

import com.swtdesigner.SWTResourceManager;

import dataStruct.Rule;

public class SearchComposite extends Composite {
	
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Button engineSelectorBaidu, engineSelectorBing, engineSelectorGoogle,
                   engineSelectorSogou, engineSelectorSoso, engineSelectorYahoo,
                   engineSelectorYoudao;
	private Text text_searchWord;
	private Tree ruleTree;
	
	private boolean searchWord = false, searchRule = false;
	
	public SearchComposite(Composite parent) {
		super(parent, SWT.BORDER);
		createContent();
	}

	private void createContent() {
		this.setLayout(new GridLayout());
		
		Composite topComp = new Composite(this, SWT.BORDER_DASH);
		topComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		topComp.setLayout(new GridLayout(3,false));
		
		final Label label = new Label(topComp, SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText("快速搜索：");
		
		text_searchWord = new Text(topComp, SWT.BORDER);
		text_searchWord.addFocusListener(new FocusAdapter() {
			public void focusGained(final FocusEvent e) {
				searchWord = true;
				searchRule = false;
			}
		});
		final GridData gd_text_searchWord = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_text_searchWord.widthHint = 582;
		text_searchWord.setLayoutData(gd_text_searchWord);
		
		Button button_search = new Button(topComp, SWT.NONE);
		button_search.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				doSearch();
			}
		});
		final GridData gd_button_search = new GridData(SWT.FILL, SWT.CENTER, true, false);
		button_search.setLayoutData(gd_button_search);
		button_search.setText("搜索");
		button_search.setImage(SWTResourceManager.getImage("img/search(1).png"));
		
		SashForm sash_mid = new SashForm(this, SWT.BORDER);
		sash_mid.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		MainWindow.imediateSearchBrowser = new CBrowserComposite(sash_mid, 1, null);
		
		Composite composite = new Composite(sash_mid, SWT.BORDER);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setLayout(new GridLayout(1, false));
		
		final ScrolledForm form = formToolkit.createScrolledForm(composite);
		form.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		form.setText("即时搜索选项");
		form.setShowFocusedControl(true);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gridData.heightHint = 319;
		form.setLayoutData(gridData);
		form.getBody().setLayout(new GridLayout(1, false));
		
		Composite compSeparator = formToolkit.createCompositeSeparator(form.getBody());
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.heightHint = 4;
		compSeparator.setLayoutData(gridData);
		formToolkit.paintBordersFor(compSeparator);
		
		IExpansionListener ie = new IExpansionListener() {
			public void expansionStateChanged(ExpansionEvent e) {
				onThemeRefresh();
				form.reflow(true);
			}
			public void expansionStateChanging(ExpansionEvent e) {
			}
		};
		
		ExpandableComposite ecRule = formToolkit.createExpandableComposite(form.getBody(), ExpandableComposite.TWISTIE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 4);
		gridData.heightHint = 143;
		ecRule.setLayoutData(gridData);
		ecRule.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		ecRule.addExpansionListener(ie);
		ecRule.setText("选择规则");
		
		final Composite composite_2 = formToolkit.createComposite(ecRule, SWT.NONE);
		ecRule.setClient(composite_2);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));

		ruleTree = new Tree(composite_2, SWT.NONE);
		ruleTree.setFont(SWTResourceManager.getFont("微软雅黑", 10, SWT.NORMAL));
		ruleTree.setToolTipText("双击显示规则细节");
		ruleTree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				text_searchWord.setText("");
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				onSelectImediateSearchTheme(); // 设置提示性气泡
			}
		});
		ruleTree.addFocusListener(new FocusAdapter() {
			public void focusGained(final FocusEvent e) {
				searchWord = false;
				searchRule = true;
			}
		});
		
		ExpandableComposite ecEngine = formToolkit.createExpandableComposite(form.getBody(), ExpandableComposite.TWISTIE);
		ecEngine.addExpansionListener(ie);
		ecEngine.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		ecEngine.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		formToolkit.paintBordersFor(ecEngine);
		ecEngine.setText("搜索引擎");
		
		Composite composite_1 = formToolkit.createComposite(ecEngine, SWT.NONE);
		formToolkit.paintBordersFor(composite_1);
		ecEngine.setClient(composite_1);
		composite_1.setLayout(new GridLayout(2, false));
		
		engineSelectorBaidu = new Button(composite_1, SWT.CHECK);
		engineSelectorBaidu.setSelection(true);  
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.heightHint = 23;
		engineSelectorBaidu.setLayoutData(gridData);
		formToolkit.adapt(engineSelectorBaidu, true, true);
		engineSelectorBaidu.setText("百度");
		
		engineSelectorBing = new Button(composite_1, SWT.CHECK);
		engineSelectorBing.setSelection(true);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.heightHint = 23;
		engineSelectorBing.setLayoutData(gridData);
		formToolkit.adapt(engineSelectorBing, true, true);
		engineSelectorBing.setText("微软必应");
		ecEngine.setExpanded(false);
		
		engineSelectorGoogle = new Button(composite_1, SWT.CHECK);
		engineSelectorGoogle.setSelection(true);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.heightHint = 23;
		engineSelectorGoogle.setLayoutData(gridData);
		formToolkit.adapt(engineSelectorGoogle, true, true);
		engineSelectorGoogle.setText("谷歌");
		ecEngine.setExpanded(false);
		
		engineSelectorSogou = new Button(composite_1, SWT.CHECK);
		engineSelectorSogou.setSelection(true);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.heightHint = 23;
		engineSelectorSogou.setLayoutData(gridData);
		formToolkit.adapt(engineSelectorSogou, true, true);
		engineSelectorSogou.setText("搜狗");
		ecEngine.setExpanded(false);
		
		engineSelectorSoso = new Button(composite_1, SWT.CHECK);
		engineSelectorSoso.setSelection(true);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.heightHint = 23;
		engineSelectorSoso.setLayoutData(gridData);
		formToolkit.adapt(engineSelectorSoso, true, true);
		engineSelectorSoso.setText("搜搜");
		ecEngine.setExpanded(false);
		
		engineSelectorYahoo = new Button(composite_1, SWT.CHECK);
		engineSelectorYahoo.setSelection(true);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.heightHint = 23;
		engineSelectorYahoo.setLayoutData(gridData);
		formToolkit.adapt(engineSelectorYahoo, true, true);
		engineSelectorYahoo.setText("雅虎");
		ecEngine.setExpanded(false);
		
		engineSelectorYoudao = new Button(composite_1, SWT.CHECK);
		engineSelectorYoudao.setSelection(true);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.heightHint = 23;
		engineSelectorYoudao.setLayoutData(gridData);
		formToolkit.adapt(engineSelectorYoudao, true, true);
		engineSelectorYoudao.setText("有道");
		ecEngine.setExpanded(false);
		
		sash_mid.setWeights(new int[]{75,25});	
	}

	/**
	 * 定义添加或删除主题时即时搜索面板的刷新动作
	 * 在创建即时搜索面板函数createImediateSearchFolderItem()中调用
	 */
	private void onThemeRefresh()
	{
		ruleTree.removeAll();
		LinkedList<String> classes = MainWindow.cp.getAllCategorynames();
		for(int i=0;i<classes.size();i++){
			TreeItem category = new TreeItem(ruleTree, SWT.NONE);
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
	 * 定义即时搜索面版中选中某一搜索主题时显示的气泡动作
	 * 在创建即时搜索面板函数createImediateSearchFolderItem()中调用
	 */
	private void onSelectImediateSearchTheme()
	{
		ToolTip t = new ToolTip(getShell(), SWT.BALLOON|SWT.ICON_INFORMATION);
		String RuleName = ruleTree.getSelection()[0].getText();
		Rule rule = MainWindow.rp.getRuleByName(RuleName);
		if(rule == null)return;
		
		String fileType = rule.getFileType();
		if (fileType.equals("任何格式")) fileType = "任何格式";
		else if (fileType.equals("pdf")) fileType = "Adobe Acrobat PDF(.pdf)";
		else if (fileType.equals("doc")) fileType = "微软 Word(.doc)";
		else if (fileType.equals("xls")) fileType = "微软 Excel(.xls)";
		else if (fileType.equals("ppt")) fileType = "微软 Powerpoint(.ppt)";
		else fileType = "RTF 文件(.rtf)";
		
		String result =  "搜索包含全部字句："+rule.getWords()+"\n"
		                +"搜索包含关联词："+rule.getSysnonymWords()+"\n"
			            +"搜索包含完整字句："+rule.getSnippet()+"\n"
				        +"搜索不包含字句："+rule.getUnwantedWords()+"\n"
				        +"搜索设定语言："+rule.getLanguage()+"\n"
				        +"搜索设定文件格式："+fileType+"\n"
				        +"搜索指定网站："+rule.getSite()+"\n"
				        +"搜索指定条数："+rule.getCnt()+"\n"
				        +"所属规则类别："+rule.getCategoryName()+"\n"
				        +"使用关联词："+(rule.isRelative()?"是":"否");
		t.setText("搜索主题："+RuleName);
		t.setMessage(result);
		t.setVisible(true);
		t.setAutoHide(true);
	}
	
	private void doSearch(){
		Rule rule;
		if(searchRule && ruleTree.getSelectionCount() != 0){//选择了规则
			TreeItem item = ruleTree.getSelection()[0];
			LinkedList<String> names = MainWindow.cp.getAllCategorynames();
			if(names.indexOf(item.getText()) == -1){//不是类别名
				rule = MainWindow.rp.getRuleByName(item.getText());
				new ImediateSearchThread(rule
						,engineSelectorBaidu.getSelection()
	                    ,engineSelectorBing.getSelection()
	                    ,engineSelectorGoogle.getSelection()
	                    ,engineSelectorSogou.getSelection()
	                    ,engineSelectorSoso.getSelection()
	                    ,engineSelectorYahoo.getSelection()
	                    ,engineSelectorYoudao.getSelection()).start();
			}
			else{
				/*
				TreeItem[] childItem = item.getItems();
				for(int j=0;j<childItem.length;j++){
					rule = MainWindow.rp.getRuleByName(childItem[j].getText());
					new ImediateSearchThread(rule
							,engineSelectorBaidu.getSelection()
		                    ,engineSelectorBing.getSelection()
		                    ,engineSelectorGoogle.getSelection()
		                    ,engineSelectorSogou.getSelection()
		                    ,engineSelectorSoso.getSelection()
		                    ,engineSelectorYahoo.getSelection()
		                    ,engineSelectorYoudao.getSelection()).start();
				}
				*/
				MessageBox box = new MessageBox(getShell(), SWT.ICON_INFORMATION);
				box.setText("注意");
				box.setMessage("请选择规则，不是类别，或是输入关键词。");
				box.open();
			}
		}
		else if(searchWord && !text_searchWord.getText().trim().equals("")){//选择了输入框
			rule = new Rule(text_searchWord.getText(),
					        "",
					        text_searchWord.getText(),
					        "",
					        "",
					        "",
					        "",
					        "20",
					        "",
					        "",
					        false,
					        "");
			new ImediateSearchThread(rule
					,engineSelectorBaidu.getSelection()
                    ,engineSelectorBing.getSelection()
                    ,engineSelectorGoogle.getSelection()
                    ,engineSelectorSogou.getSelection()
                    ,engineSelectorSoso.getSelection()
                    ,engineSelectorYahoo.getSelection()
                    ,engineSelectorYoudao.getSelection()).start();
		}
		else{
			MessageBox box = new MessageBox(getShell(), SWT.ICON_INFORMATION);
			box.setText("注意");
			box.setMessage("请先选择规则，或是输入关键词。");
			box.open();
		}
	}
}
