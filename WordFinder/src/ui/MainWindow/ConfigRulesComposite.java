package ui.MainWindow;

import java.util.LinkedList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.swtdesigner.SWTResourceManager;

import dataStruct.Rule;

public class ConfigRulesComposite extends Composite {

	private Button check_isRelative;
	private Combo combo_category, combo_cnt, combo_language, combo_fileType;
	private Text text_ruleName, text_words, text_snippet, text_unwantedWords, text_site, text_synonymWords;
	private Tree tree_rule;
	private Group group_detail;
	private Menu menu_rule;
	private MenuItem newItem, modifyItem, deleteItem;
	private ToolBar toolBar_rule, toolBar_category;
	private ToolItem newRuleItem, modifyRuleItem, deleteRuleItem,
	                 newRuleCategoryItem, modifyRuleCategoryItem, deleteRuleCategoryItem;
	
	public Tree getTree_rule() {
		return tree_rule;
	}

	public ConfigRulesComposite(Composite parent) {
		super(parent, SWT.NONE);
		createContent();
		createRuleMenu();
	}

	private void createContent() {
		this.setLayout(new GridLayout(2,true));
		
		Group group_rule = new Group(this, SWT.BORDER);
		group_rule.setLayoutData(new GridData(GridData.FILL_BOTH));
		group_rule.setLayout(new GridLayout());
		group_rule.setText("规则列表");
		
		tree_rule = new Tree(group_rule, SWT.NONE);
		tree_rule.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onSelectRule();
			}
		});
		tree_rule.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite detailComp = new Composite(this, SWT.NONE);
		detailComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		detailComp.setLayout(new GridLayout());

		group_detail = new Group(detailComp, SWT.V_SCROLL);
		group_detail.setText("规则明细");
		group_detail.setLayoutData(new GridData(GridData.FILL_BOTH));
		group_detail.setLayout(new FillLayout());
		
		ScrolledComposite scrolledDetailComp = 
			new ScrolledComposite(group_detail, SWT.H_SCROLL | SWT.V_SCROLL);
		Composite comp = new Composite(scrolledDetailComp, SWT.NONE);
		comp.setSize(380, 550);
		scrolledDetailComp.setContent(comp);

		final Label label = new Label(comp, SWT.NONE);
		label.setText("规则名称：");
		label.setBounds(28, 37, 80, 14);

		final Label label_1 = new Label(comp, SWT.NONE);
		label_1.setText("所属类别：");
		label_1.setBounds(28, 78, 80, 14);

		final Label label_2 = new Label(comp, SWT.NONE);
		label_2.setText("包含全部字句：");
		label_2.setBounds(28, 120, 80, 14);
		
		final Label label_12 = new Label(comp, SWT.NONE);
		label_12.setText("关联词：");
		label_12.setBounds(28, 152, 80, 12);

		final Label label_3 = new Label(comp, SWT.NONE);
		label_3.setText("包含完整字句：");
		label_3.setBounds(31, 211, 80, 14);

		final Label label_4 = new Label(comp, SWT.NONE);
		label_4.setText("不包含的字句：");
		label_4.setBounds(31, 248, 80, 14);

		final Label label_5 = new Label(comp, SWT.NONE);
		label_5.setText("搜索条目：");
		label_5.setBounds(31, 284, 80, 14);

		final Label label_6 = new Label(comp, SWT.NONE);
		label_6.setText("语言：");
		label_6.setBounds(31, 329, 80, 14);

		final Label label_7 = new Label(comp, SWT.NONE);
		label_7.setText("网站：");
		label_7.setBounds(31, 383, 80, 14);

		final Label label_8 = new Label(comp, SWT.NONE);
		label_8.setText("文件格式：");
		label_8.setBounds(31, 436, 80, 14);

		text_ruleName = new Text(comp, SWT.BORDER);
		text_ruleName.setBounds(114, 34, 215, 17);

		combo_category = new Combo(comp, SWT.READ_ONLY);
		combo_category.setBounds(114, 75, 91, 22);

		text_words = new Text(comp, SWT.BORDER);
		text_words.setBounds(114, 117, 215, 17);
		
		text_synonymWords = new Text(comp, SWT.WRAP | SWT.BORDER);
		text_synonymWords.setBounds(114, 149, 215, 41);

		text_snippet = new Text(comp, SWT.BORDER);
		text_snippet.setBounds(117, 208, 215, 17);

		text_unwantedWords = new Text(comp, SWT.BORDER);
		text_unwantedWords.setBounds(117, 245, 215, 17);

		combo_cnt = new Combo(comp, SWT.READ_ONLY);
		combo_cnt.setItems(new String[] {"10", "20", "50", "100"});
		combo_cnt.select(0);
		combo_cnt.setBounds(117, 281, 91, 22);

		combo_language = new Combo(comp, SWT.READ_ONLY);
		combo_language.setItems(new String[] {"任何语言", "简体中文", "英文"});
		combo_language.select(0);
		combo_language.setBounds(117, 326, 91, 22);

		text_site = new Text(comp, SWT.BORDER);
		text_site.setBounds(117, 380, 215, 17);

		combo_fileType = new Combo(comp, SWT.READ_ONLY);
		combo_fileType.setItems(new String[] {"任何格式", "Adobe Acrobat PDF(.pdf)", "微软 Word(.doc)", "微软 Excel(.xls)", "微软 Powerpoint(.ppt)", "RTF 文件(.rtf)"});
		combo_fileType.select(0);
		combo_fileType.setBounds(117, 433, 91, 22);

		final Label label_9 = new Label(comp, SWT.NONE);
		label_9.setText("(搜索的网页语言)");
		label_9.setBounds(28, 349, 103, 14);

		final Label label_10 = new Label(comp, SWT.NONE);
		label_10.setText("(搜索以下网站)");
		label_10.setBounds(31, 403, 103, 14);

		final Label label_11 = new Label(comp, SWT.NONE);
		label_11.setText("(显示使用以下文件格式的结果)");
		label_11.setBounds(28, 456, 180, 14);

		toolBar_rule = new ToolBar(detailComp, SWT.BORDER | SWT.RIGHT);
		toolBar_rule.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		newRuleItem = new ToolItem(toolBar_rule, SWT.PUSH);
		newRuleItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(tree_rule.getSelectionCount() == 0){
					NewRuleDlg nrd = new NewRuleDlg(getShell());
					nrd.open();
					load();
				}
				else{
					String selection = tree_rule.getSelection()[0].getText();
					if(!MainWindow.cp.isCategoryExisted(selection))//不是类别
						selection = tree_rule.getSelection()[0].getParentItem().getText();
					NewRuleDlg nrd = new NewRuleDlg(getShell(), selection);
					nrd.open();
					load();
				}
			}
		});
		newRuleItem.setText("新建规则");
		
		modifyRuleItem = new ToolItem(toolBar_rule, SWT.PUSH);
		modifyRuleItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				doSave();
			}
		});
		modifyRuleItem.setText("保存修改");
		
		deleteRuleItem = new ToolItem(toolBar_rule, SWT.PUSH);
		deleteRuleItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(tree_rule.getSelectionCount() == 0)return;
				String selection = tree_rule.getSelection()[0].getText();
				if(MessageDialog.openConfirm(getShell(), "删除规则",
						"确认删除规则“" + selection + "”吗？")){
					if(MainWindow.rp.deleteRule(selection))
						tree_rule.getSelection()[0].dispose();
				}
			}
		});
		deleteRuleItem.setText("删除规则");

		toolBar_category = new ToolBar(group_rule, SWT.BORDER | SWT.RIGHT);
		toolBar_category.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		newRuleCategoryItem = new ToolItem(toolBar_category, SWT.PUSH);
		newRuleCategoryItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				NewRuleCategoryDlg nrcd = new NewRuleCategoryDlg(getShell());
				nrcd.open();
				load();
			}
		});
		newRuleCategoryItem.setText("新建分类");
		
		modifyRuleCategoryItem = new ToolItem(toolBar_category, SWT.PUSH);
		modifyRuleCategoryItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(tree_rule.getSelectionCount() == 0)return;
				String selection = tree_rule.getSelection()[0].getText();
				ModifyRuleCategoryDlg mrcd = new ModifyRuleCategoryDlg(getShell(), selection);
				mrcd.open();
				load();
			}
		});
		modifyRuleCategoryItem.setText("修改分类");
		
		deleteRuleCategoryItem = new ToolItem(toolBar_category, SWT.PUSH);
		deleteRuleCategoryItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(tree_rule.getSelectionCount() == 0)return;
				String selection = tree_rule.getSelection()[0].getText();
				if(MessageDialog.openConfirm(getShell(), "删除规则类别",
						"确认删除分类“" + selection + "”，以及其下所有规则吗？")){
					if(MainWindow.cp.deleteCategory(selection))
						tree_rule.getSelection()[0].dispose();
				}
			}
		});
		deleteRuleCategoryItem.setText("删除分类");

		check_isRelative = new Button(comp, SWT.CHECK);
		check_isRelative.setText("使用关联词");
		check_isRelative.setBounds(31, 487, 93, 16);
		
		text_ruleName.setEnabled(false);
		combo_category.setEnabled(false);
		text_words.setEnabled(false);
		text_snippet.setEnabled(false);
		text_unwantedWords.setEnabled(false);
		combo_cnt.setEnabled(false);
		combo_language.setEnabled(false);
		combo_fileType.setEnabled(false);
		text_site.setEnabled(false);
		check_isRelative.setEnabled(false);
		text_synonymWords.setEnabled(false);

		final Label label_13 = new Label(comp, SWT.NONE);
		label_13.setText("(此项选择可能会对搜索性能产生影响，请慎重使用)");
		label_13.setBounds(28, 509, 301, 12);
	}
	
	/**
	 * 创建规则树的右键菜单
	 */
	private void createRuleMenu() {
		menu_rule = new Menu(getShell(), SWT.POP_UP);
		
		newItem = new MenuItem(menu_rule, SWT.PUSH);
		newItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(tree_rule.getSelectionCount() == 0)return;
				String selection = tree_rule.getSelection()[0].getText();
				
				if(MainWindow.cp.isCategoryExisted(selection)){//是类别
					NewRuleDlg nrd = new NewRuleDlg(getShell(), selection);
					nrd.open();
					load();
				}
				else{
					selection = tree_rule.getSelection()[0].getParentItem().getText();
					NewRuleDlg nrd = new NewRuleDlg(getShell(), selection);
					nrd.open();
					load();
				}
			}
		});
		newItem.setText("新建");
		newItem.setImage(SWTResourceManager.getImage("img/new.gif"));
		
		modifyItem = new MenuItem(menu_rule, SWT.PUSH);
		modifyItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(tree_rule.getSelectionCount() == 0)return;
				String selection = tree_rule.getSelection()[0].getText();
				
				if(MainWindow.cp.isCategoryExisted(selection)){//是类别
					ModifyRuleCategoryDlg mrcd = new ModifyRuleCategoryDlg(getShell(), selection);
					mrcd.open();
					load();
				}
				else{
					text_ruleName.selectAll();
					text_ruleName.setFocus();
				}
			}
		});
		modifyItem.setText("修改");
		modifyItem.setImage(SWTResourceManager.getImage("img/mov.gif"));
		
		deleteItem = new MenuItem(menu_rule, SWT.PUSH);
		deleteItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(tree_rule.getSelectionCount() == 0)return;
				String selection = tree_rule.getSelection()[0].getText();
				
				if(MainWindow.cp.isCategoryExisted(selection)){//是类别
					if(MessageDialog.openConfirm(getShell(), "删除规则类别",
							"确认删除分类“" + selection + "”，以及其下所有规则吗？")){
						MainWindow.cp.deleteCategory(selection);
						tree_rule.getSelection()[0].dispose();
					}
				}
				else{
					if(MessageDialog.openConfirm(getShell(), "删除规则",
							"确认删除规则“" + selection + "”吗？")){
						MainWindow.rp.deleteRule(selection);
						tree_rule.getSelection()[0].dispose();
					}
				}
			}
		});
		deleteItem.setText("删除");
		deleteItem.setImage(SWTResourceManager.getImage("img/delete.gif"));
		
		tree_rule.setMenu(menu_rule);
	}
	
	/**
	 * 载入类别和规则
	 */
	public void load()
	{
		String[] classes = MainWindow.cp.getAllCategorynamesAsArray();
		combo_category.setItems(classes);
		tree_rule.removeAll();
		for(int i=0;i<classes.length;i++){
			TreeItem category = new TreeItem(tree_rule, SWT.NONE);
			category.setText(classes[i]);
			LinkedList<String> rules = MainWindow.rp.getRulenameByCategory(classes[i]);
			for(int j=0;j<rules.size();j++){
				TreeItem item = new TreeItem(category, SWT.NONE);
				item.setText(rules.get(j));
			}
			category.setExpanded(true);
		}
		doReset();
	}
	
	/**
	 * 重置动作
	 */
	private void doReset(){
		text_ruleName.setText("");
		text_words.setText("");
		text_synonymWords.setText("");
		text_snippet.setText("");
		text_unwantedWords.setText("");
		text_site.setText("");
		
		combo_language.select(0);
		combo_fileType.select(0);
		combo_cnt.select(0);
		combo_category.select(0);
		
		check_isRelative.setSelection(false);
		
		modifyRuleItem.setEnabled(false);
		deleteRuleItem.setEnabled(false);
		modifyRuleCategoryItem.setEnabled(false);
		deleteRuleCategoryItem.setEnabled(false);
		
		group_detail.setEnabled(false);
	}
	
	
	
	/**
	 * 选中树中的一条规则时的动作
	 */
	private void onSelectRule() {
		if(tree_rule.getSelectionCount() == 0)return;
		String selection = tree_rule.getSelection()[0].getText();
		Rule rule;
		
		if(MainWindow.cp.isCategoryExisted(selection)){//是类别
			text_ruleName.setEnabled(false);
			combo_category.setEnabled(false);
			text_words.setEnabled(false);
			text_synonymWords.setEnabled(false);
			text_snippet.setEnabled(false);
			text_unwantedWords.setEnabled(false);
			combo_cnt.setEnabled(false);
			combo_language.setEnabled(false);
			combo_fileType.setEnabled(false);
			text_site.setEnabled(false);
			check_isRelative.setEnabled(false);
			doReset();
			combo_category.setText(selection);
			modifyRuleCategoryItem.setEnabled(true);
			deleteRuleCategoryItem.setEnabled(true);
		}
		else{
			//text_ruleName.setEnabled(true);
			combo_category.setEnabled(true);
			text_words.setEnabled(true);
			text_synonymWords.setEnabled(true);
			text_snippet.setEnabled(true);
			text_unwantedWords.setEnabled(true);
			combo_cnt.setEnabled(true);
			combo_language.setEnabled(true);
			combo_fileType.setEnabled(true);
			text_site.setEnabled(true);
			modifyRuleItem.setEnabled(true);
			deleteRuleItem.setEnabled(true);
			check_isRelative.setEnabled(true);
			modifyRuleCategoryItem.setEnabled(false);
			deleteRuleCategoryItem.setEnabled(false);
			rule = MainWindow.rp.getRuleByName(selection);
			text_ruleName.setText(rule.getName());
			combo_category.setText(rule.getCategoryName());
			text_words.setText(rule.getWords());
			text_synonymWords.setText(rule.getSysnonymWords());
			text_snippet.setText(rule.getSnippet());
			text_unwantedWords.setText(rule.getUnwantedWords());
			combo_cnt.setText(rule.getCnt());
			combo_language.setText(rule.getLanguage());
			combo_fileType.setText(getStringOfFiletype(rule.getFileType()));
			text_site.setText(rule.getSite());
			check_isRelative.setSelection(rule.isRelative());
			
			group_detail.setEnabled(true);
		}
	}
	
	private String getStringOfFiletype(String shortType) {
		int index;
		for(index=0;index<combo_fileType.getItemCount();index++)
			if(combo_fileType.getItem(index).indexOf(shortType) != -1)
				break;
		return combo_fileType.getItem(index);
	}
	
	/**
	 * 修改规则的动作
	 */
	private void doSave() {
		if(tree_rule.getSelectionCount() == 0)return;
		String ruleName = text_ruleName.getText(),
	       categoryName = combo_category.getText(),
	       words = text_words.getText(),
	       synonymWords = text_synonymWords.getText(),
	       snippet = text_snippet.getText(),
	       unwantedWords = text_unwantedWords.getText(),
	       language = combo_language.getText(),
	       fileType = combo_fileType.getText().substring(combo_fileType.getText().indexOf(".")+1).replace(")", ""),
	       site = text_site.getText(),
	       searchCount = combo_cnt.getText();
		boolean isRelative = check_isRelative.getSelection();
		if(words.equals(unwantedWords)){
			MessageBox box = new MessageBox(getShell(), SWT.ICON_INFORMATION);
			box.setText("修改规则失败");
			box.setMessage("错误的规则条件，”包含的全部字句“不能全部出现在”不包含的字句“中。");
			box.open();
		}
		else if(MessageDialog.openConfirm(getShell(), "修改规则",
							"确认修改规则”" + ruleName + "“吗？\n" +
							"所属分类：" + categoryName + "\n" +
							"包含全部字句：" + words + "\n" +
							"关联词：" + synonymWords + "\n" +
							"包含完整字句：" + snippet + "\n" +
							"不包含字句：" + unwantedWords + "\n" +
							"搜索条目：" + searchCount + "\n" +
							"语言：" + language + "\n" +
							"网站：" + site + "\n" +
							"文件格式：" + fileType + "\n" +
							"使用关联词：" + (isRelative?"是":"否"))){
			MainWindow.rp.updateRule(ruleName, words, snippet,
					unwantedWords, language, fileType, site, searchCount,
					categoryName, isRelative, synonymWords);
			MessageBox box = new MessageBox(getShell(), SWT.ICON_INFORMATION);
			box.setText("提示");
			box.setMessage("修改规则成功！");
			box.open();
			load();
		}
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
