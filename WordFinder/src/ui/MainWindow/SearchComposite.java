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
		label.setText("����������");
		
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
		button_search.setText("����");
		button_search.setImage(SWTResourceManager.getImage("img/search(1).png"));
		
		SashForm sash_mid = new SashForm(this, SWT.BORDER);
		sash_mid.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		MainWindow.imediateSearchBrowser = new CBrowserComposite(sash_mid, 1, null);
		
		Composite composite = new Composite(sash_mid, SWT.BORDER);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setLayout(new GridLayout(1, false));
		
		final ScrolledForm form = formToolkit.createScrolledForm(composite);
		form.setFont(SWTResourceManager.getFont("΢���ź�", 12, SWT.BOLD));
		form.setText("��ʱ����ѡ��");
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
		ecRule.setFont(SWTResourceManager.getFont("΢���ź�", 11, SWT.NORMAL));
		ecRule.addExpansionListener(ie);
		ecRule.setText("ѡ�����");
		
		final Composite composite_2 = formToolkit.createComposite(ecRule, SWT.NONE);
		ecRule.setClient(composite_2);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));

		ruleTree = new Tree(composite_2, SWT.NONE);
		ruleTree.setFont(SWTResourceManager.getFont("΢���ź�", 10, SWT.NORMAL));
		ruleTree.setToolTipText("˫����ʾ����ϸ��");
		ruleTree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				text_searchWord.setText("");
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				onSelectImediateSearchTheme(); // ������ʾ������
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
		ecEngine.setFont(SWTResourceManager.getFont("΢���ź�", 11, SWT.NORMAL));
		formToolkit.paintBordersFor(ecEngine);
		ecEngine.setText("��������");
		
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
		engineSelectorBaidu.setText("�ٶ�");
		
		engineSelectorBing = new Button(composite_1, SWT.CHECK);
		engineSelectorBing.setSelection(true);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.heightHint = 23;
		engineSelectorBing.setLayoutData(gridData);
		formToolkit.adapt(engineSelectorBing, true, true);
		engineSelectorBing.setText("΢���Ӧ");
		ecEngine.setExpanded(false);
		
		engineSelectorGoogle = new Button(composite_1, SWT.CHECK);
		engineSelectorGoogle.setSelection(true);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.heightHint = 23;
		engineSelectorGoogle.setLayoutData(gridData);
		formToolkit.adapt(engineSelectorGoogle, true, true);
		engineSelectorGoogle.setText("�ȸ�");
		ecEngine.setExpanded(false);
		
		engineSelectorSogou = new Button(composite_1, SWT.CHECK);
		engineSelectorSogou.setSelection(true);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.heightHint = 23;
		engineSelectorSogou.setLayoutData(gridData);
		formToolkit.adapt(engineSelectorSogou, true, true);
		engineSelectorSogou.setText("�ѹ�");
		ecEngine.setExpanded(false);
		
		engineSelectorSoso = new Button(composite_1, SWT.CHECK);
		engineSelectorSoso.setSelection(true);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.heightHint = 23;
		engineSelectorSoso.setLayoutData(gridData);
		formToolkit.adapt(engineSelectorSoso, true, true);
		engineSelectorSoso.setText("����");
		ecEngine.setExpanded(false);
		
		engineSelectorYahoo = new Button(composite_1, SWT.CHECK);
		engineSelectorYahoo.setSelection(true);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.heightHint = 23;
		engineSelectorYahoo.setLayoutData(gridData);
		formToolkit.adapt(engineSelectorYahoo, true, true);
		engineSelectorYahoo.setText("�Ż�");
		ecEngine.setExpanded(false);
		
		engineSelectorYoudao = new Button(composite_1, SWT.CHECK);
		engineSelectorYoudao.setSelection(true);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.heightHint = 23;
		engineSelectorYoudao.setLayoutData(gridData);
		formToolkit.adapt(engineSelectorYoudao, true, true);
		engineSelectorYoudao.setText("�е�");
		ecEngine.setExpanded(false);
		
		sash_mid.setWeights(new int[]{75,25});	
	}

	/**
	 * ������ӻ�ɾ������ʱ��ʱ��������ˢ�¶���
	 * �ڴ�����ʱ������庯��createImediateSearchFolderItem()�е���
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
	 * ���弴ʱ���������ѡ��ĳһ��������ʱ��ʾ�����ݶ���
	 * �ڴ�����ʱ������庯��createImediateSearchFolderItem()�е���
	 */
	private void onSelectImediateSearchTheme()
	{
		ToolTip t = new ToolTip(getShell(), SWT.BALLOON|SWT.ICON_INFORMATION);
		String RuleName = ruleTree.getSelection()[0].getText();
		Rule rule = MainWindow.rp.getRuleByName(RuleName);
		if(rule == null)return;
		
		String fileType = rule.getFileType();
		if (fileType.equals("�κθ�ʽ")) fileType = "�κθ�ʽ";
		else if (fileType.equals("pdf")) fileType = "Adobe Acrobat PDF(.pdf)";
		else if (fileType.equals("doc")) fileType = "΢�� Word(.doc)";
		else if (fileType.equals("xls")) fileType = "΢�� Excel(.xls)";
		else if (fileType.equals("ppt")) fileType = "΢�� Powerpoint(.ppt)";
		else fileType = "RTF �ļ�(.rtf)";
		
		String result =  "��������ȫ���־䣺"+rule.getWords()+"\n"
		                +"�������������ʣ�"+rule.getSysnonymWords()+"\n"
			            +"�������������־䣺"+rule.getSnippet()+"\n"
				        +"�����������־䣺"+rule.getUnwantedWords()+"\n"
				        +"�����趨���ԣ�"+rule.getLanguage()+"\n"
				        +"�����趨�ļ���ʽ��"+fileType+"\n"
				        +"����ָ����վ��"+rule.getSite()+"\n"
				        +"����ָ��������"+rule.getCnt()+"\n"
				        +"�����������"+rule.getCategoryName()+"\n"
				        +"ʹ�ù����ʣ�"+(rule.isRelative()?"��":"��");
		t.setText("�������⣺"+RuleName);
		t.setMessage(result);
		t.setVisible(true);
		t.setAutoHide(true);
	}
	
	private void doSearch(){
		Rule rule;
		if(searchRule && ruleTree.getSelectionCount() != 0){//ѡ���˹���
			TreeItem item = ruleTree.getSelection()[0];
			LinkedList<String> names = MainWindow.cp.getAllCategorynames();
			if(names.indexOf(item.getText()) == -1){//���������
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
				box.setText("ע��");
				box.setMessage("��ѡ����򣬲�����𣬻�������ؼ��ʡ�");
				box.open();
			}
		}
		else if(searchWord && !text_searchWord.getText().trim().equals("")){//ѡ���������
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
			box.setText("ע��");
			box.setMessage("����ѡ����򣬻�������ؼ��ʡ�");
			box.open();
		}
	}
}
