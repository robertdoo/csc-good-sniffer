package ui.MainWindow;

import lucene.Search.ResultOrder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.swtdesigner.SWTResourceManager;

import dataStruct.Rule;
import dataStruct.SearchResult;

public class TracebackSpiderComposite extends Composite {

	private Text text_title;
	private CTabFolder resultFolder;
	
	private ResultOrder order = new ResultOrder();
	
	public TracebackSpiderComposite(Composite parent) {
		super(parent, SWT.NONE);
		createContent();
	}

	private void createContent() {
		this.setLayout(new GridLayout());
		
		Composite topComp = new Composite(this, SWT.NONE);
		topComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		topComp.setLayout(new GridLayout(8, true));
		
		Label label = new Label(topComp, SWT.NONE);
		label.setText("在这里输入标题：");
		
		text_title = new Text(topComp, SWT.BORDER);
		text_title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));
		
		ToolBar toolBar = new ToolBar(topComp, SWT.NONE);
		toolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final ToolItem searchItem = new ToolItem(toolBar, SWT.NONE);
		searchItem.setImage(SWTResourceManager.getImage("img/traceback(1).png"));
		searchItem.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				if(text_title.getText().trim().equals("")){
					MessageBox msgBox = new MessageBox(getShell(), SWT.ICON_INFORMATION);
					msgBox.setText("提示信息");
					msgBox.setMessage("请输入要排序的标题。");
					msgBox.open();
					return;
				}
				doSearch();
			}
		});
		
		Composite midComp = new Composite(this, SWT.NONE);
		midComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		midComp.setLayout(new GridLayout());
		
		resultFolder = new CTabFolder(midComp, SWT.BORDER);
		resultFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		resultFolder.setSimple(false);
		resultFolder.setTabHeight(18);
		resultFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
	}
	
	/**
	 * 点击查找按钮时的方法
	 */
	public void doSearch(){
		Rule rule = new Rule("", "任何语言", text_title.getText(), "", "", "任何格式", "", "20", "", "", false, "");;
		new Thread(new spiderSearchRunnable(rule)).start();
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * 搜索爬虫数据的线程
	 */
	class spiderSearchRunnable implements Runnable {
		private Display display = Display.getDefault();
		private CBrowserComposite browser;
		private Rule rule;
		private String title;
		private SearchResult searchResult;

		public spiderSearchRunnable(Rule rule) {
			super();
			this.rule = rule;
		}

		public void run() {
			display.asyncExec(new Runnable() { 
						public void run() {
							CTabItem[] origin = resultFolder.getItems();
							for (int j = 0; j < origin.length; ++j) {
								if (origin[j].getText().equals(rule.getWords())) {
									origin[j].dispose();
								}
							}
							CTabItem newItem = new CTabItem(resultFolder, SWT.CLOSE);
							newItem.setText(rule.getWords());
							newItem.setImage(SWTResourceManager.getImage("img/new.gif"));

							browser = new CBrowserComposite(resultFolder, 2, null);
							newItem.setControl(browser);
							resultFolder.setSelection(newItem);
							browser.setSearchInfo("正在搜索……");
						}
					});
			searchResult = MainWindow.spiderSearcher.search(rule);
			if(searchResult == null){
				display.syncExec(new Runnable() {
					public void run() {
						browser.appendSearchInfo("\n搜索失败！");
						browser.setResultCopy(searchResult);
					}
				});
				return;
			}
			else{
				display.syncExec(new Runnable() {
					public void run() {
						browser.appendSearchInfo("\n搜索完毕，得到"
								+ searchResult.getCountOfResults() + "条结果。");
						browser.appendSearchInfo("正在排序……");
						title = text_title.getText();
					}
				});
			}	
			searchResult = order.order(searchResult, title);
			display.asyncExec(new Runnable() {
				public void run() {
					browser.setResultCopy(searchResult);
					browser.appendSearchInfo("\n与输入的标题相似的有"
							+ searchResult.getCountOfResults() + "条结果。");
				}
			});
		}
	}
}
