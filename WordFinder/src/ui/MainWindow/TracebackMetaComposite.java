package ui.MainWindow;

import java.util.LinkedList;

import lucene.Search.ResultFilter;
import lucene.Search.ResultOrder;
import lucene.Search.ResultOutput;
import lucene.Search.ResultSensativity;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import com.swtdesigner.SWTResourceManager;

import dataStruct.Result;
import dataStruct.Rule;
import dataStruct.SearchResult;

import metaSearchEngine.BaiduExtract;
import metaSearchEngine.BingExtract;
import metaSearchEngine.GoogleExtract;
import metaSearchEngine.SogouExtract;
import metaSearchEngine.SosoExtract;
import metaSearchEngine.YahooExtract;
import metaSearchEngine.YoudaoExtract;

public class TracebackMetaComposite extends Composite {

	private Text text_title;
	private Label statusLabel;
	private CTabFolder resultFolder;
	
	private ResultOutput HTMLPrinter = new ResultOutput();
	private ResultSensativity sensa = new ResultSensativity();
	private ResultFilter filter = new ResultFilter();
	private ResultOrder order = new ResultOrder();
	
	public TracebackMetaComposite(Composite parent) {
		super(parent, SWT.BORDER);
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
		
		Composite bottomComp = new Composite(this, SWT.NONE);
		bottomComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		bottomComp.setLayout(new GridLayout());
		
		statusLabel = new Label(bottomComp, SWT.NONE);
		statusLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	class searchComposite extends Composite{
		Browser browser;
		Text resultStatus;
		CLabel searchEngine1, searchEngine2, searchEngine3, searchEngine4,
		       searchEngine5, searchEngine6, searchEngine7;
		CLabel status1, status2, status3, status4, status5, status6,
		       status7;
		
		public searchComposite(Composite parent, int style){
			super(parent, style);
			createContent();
		}
		
		private void createContent(){
			this.setLayout(new GridLayout());
			SashForm sash = new SashForm(this, SWT.NONE);
			sash.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			browser = new Browser(sash, SWT.BORDER);
			browser.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			SashForm statusComp = new SashForm(sash, SWT.NONE);
			statusComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			statusComp.setOrientation(SWT.VERTICAL);
			
			Group engineGroup = new Group(statusComp, SWT.NONE);
			engineGroup.setLayout(new GridLayout(2, false));
			engineGroup.setText("搜索引擎状态");
			status1 = new CLabel(engineGroup, SWT.NONE);
			status1.setImage(SWTResourceManager.getImage("img/info.gif"));
			searchEngine1 = new CLabel(engineGroup, SWT.NONE);
			searchEngine1.setImage(SWTResourceManager.getImage("img/baidu.gif"));
			searchEngine1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			status2 = new CLabel(engineGroup, SWT.NONE);
			status2.setImage(SWTResourceManager.getImage("img/info.gif"));
			searchEngine2 = new CLabel(engineGroup, SWT.NONE);
			searchEngine2.setImage(SWTResourceManager.getImage("img/bing.gif"));
			searchEngine2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			status3 = new CLabel(engineGroup, SWT.NONE);
			status3.setImage(SWTResourceManager.getImage("img/info.gif"));
			searchEngine3 = new CLabel(engineGroup, SWT.NONE);
			searchEngine3.setImage(SWTResourceManager.getImage("img/google.gif"));
			searchEngine3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			status4 = new CLabel(engineGroup, SWT.NONE);
			status4.setImage(SWTResourceManager.getImage("img/info.gif"));
			searchEngine4 = new CLabel(engineGroup, SWT.NONE);
			searchEngine4.setImage(SWTResourceManager.getImage("img/sogou.gif"));
			searchEngine4.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			status5 = new CLabel(engineGroup, SWT.NONE);
			status5.setImage(SWTResourceManager.getImage("img/info.gif"));
			searchEngine5 = new CLabel(engineGroup, SWT.NONE);
			searchEngine5.setImage(SWTResourceManager.getImage("img/soso.gif"));
			searchEngine5.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			status6 = new CLabel(engineGroup, SWT.NONE);
			status6.setImage(SWTResourceManager.getImage("img/info.gif"));
			searchEngine6 = new CLabel(engineGroup, SWT.NONE);
			searchEngine6.setImage(SWTResourceManager.getImage("img/yahoo.gif"));
			searchEngine6.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			status7 = new CLabel(engineGroup, SWT.NONE);
			status7.setImage(SWTResourceManager.getImage("img/info.gif"));
			searchEngine7 = new CLabel(engineGroup, SWT.NONE);
			searchEngine7.setImage(SWTResourceManager.getImage("img/youdao.gif"));
			searchEngine7.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			Group resultStatusGroup = new Group(statusComp, SWT.NONE);
			resultStatusGroup.setLayout(new GridLayout());
			resultStatusGroup.setText("搜索结果信息");
			resultStatus = new Text(resultStatusGroup, SWT.WRAP);
			resultStatus.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			statusComp.setWeights(new int[]{60, 40});
			
			sash.setWeights(new int[]{74, 26});
		}
	}

	/**
	 * 点击查找按钮时的方法
	 */
	public void doSearch(){
		Rule rule = new Rule("", "任何语言", text_title.getText(), "", "", "任何格式", "", "20", "", "", false, "");;
		new Thread(new metaSearchRunnable(rule)).start();
	}
	
	/**
	 * 元搜索的线程
	 */
	class metaSearchRunnable implements Runnable {
		private Display display = Display.getDefault();
		private searchComposite sComp;
		private Rule rule;
		private String title, html;
		private BaiduExtract baidu = new BaiduExtract(false);
		private BingExtract bing = new BingExtract(false);
		private GoogleExtract google = new GoogleExtract(false);
		private SogouExtract sogou = new SogouExtract(false);
		private SosoExtract soso = new SosoExtract(false);
		private YahooExtract yahoo = new YahooExtract(false);
		private YoudaoExtract youdao = new YoudaoExtract(false);
		private SearchResult searchResult;
		private LinkedList<Result> resultList;

		public metaSearchRunnable(Rule rule) {
			super();
			this.rule = rule;
		}

		public void run() {
			// 用百度搜索
			display.syncExec(new Runnable() { 
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

							sComp = new searchComposite(resultFolder, SWT.NONE);
							newItem.setControl(sComp);
							resultFolder.setSelection(newItem);
							sComp.status1.setImage(SWTResourceManager.getImage("img/info.gif"));
							sComp.searchEngine1.setText("正在搜索……");
							statusLabel.setText("百度正在搜索……");
						}
					});
			int status = 0;
			status = baidu.doSearch(rule, rule.getCnt());
			if (status == 1) {
				display.syncExec(new Runnable() {
					public void run() {
						sComp.status1.setImage(SWTResourceManager.getImage("img/close.gif"));
						sComp.searchEngine1.setText("");
						
						sComp.resultStatus.append("\n百度搜索失败。");
						statusLabel.setText("百度搜索失败。");
					}
				});
	
			} 
			else {
				if(resultList == null)
					resultList = new LinkedList<Result>();
				for(Result r : baidu.results)
					resultList.add(r);
				display.syncExec(new Runnable() {
					public void run() {
						sComp.status1.setImage(SWTResourceManager.getImage("img/correct.png"));
						sComp.searchEngine1.setText("");
						
						sComp.resultStatus.append("\n百度搜索完毕，得到" + baidu.results.size() + "条结果。");
						statusLabel.setText("百度搜索完毕,共返回" + baidu.results.size() + "条结果。");
					}
				});
			}
			
			// 用必应搜索
			display.syncExec(new Runnable() { 
						public void run() {
							sComp.status2.setImage(SWTResourceManager.getImage("img/info.gif"));
							sComp.searchEngine2.setText("正在搜索……");
							statusLabel.setText("必应正在搜索……");
						}
					});
			status = bing.doSearch(rule, rule.getCnt());
			if (status == 1) {
				display.syncExec(new Runnable() {
					public void run() {
						sComp.status2.setImage(SWTResourceManager.getImage("img/close.gif"));
						sComp.searchEngine2.setText("");
						
						sComp.resultStatus.append("\n必应搜索失败。");
						statusLabel.setText("必应搜索失败。");
					}
				});
	
			} 
			else {
				if(resultList == null)
					resultList = new LinkedList<Result>();
				for(Result r : bing.results)
					resultList.add(r);
				display.syncExec(new Runnable() {
					public void run() {
						sComp.status2.setImage(SWTResourceManager.getImage("img/correct.png"));
						sComp.searchEngine2.setText("");
						
						sComp.resultStatus.append("\n必应搜索完毕，得到" + bing.results.size() + "条结果。");
						statusLabel.setText("必应搜索完毕,共返回" + bing.results.size() + "条结果。");
					}
				});
			}
			
			// 用谷歌搜索
			display.syncExec(new Runnable() { 
						public void run() {
							sComp.status3.setImage(SWTResourceManager.getImage("img/info.gif"));
							sComp.searchEngine3.setText("正在搜索……");
							statusLabel.setText("谷歌正在搜索……");
						}
					});
			status = google.doSearch(rule, rule.getCnt());
			if (status == 1) {
				display.syncExec(new Runnable() {
					public void run() {
						sComp.status3.setImage(SWTResourceManager.getImage("img/close.gif"));
						sComp.searchEngine3.setText("");
						
						sComp.resultStatus.append("\n谷歌搜索失败。");
						statusLabel.setText("谷歌搜索失败。");
					}
				});
	
			} 
			else {
				if(resultList == null)
					resultList = new LinkedList<Result>();
				for(Result r : google.results)
					resultList.add(r);
				display.syncExec(new Runnable() {
					public void run() {
						sComp.status3.setImage(SWTResourceManager.getImage("img/correct.png"));
						sComp.searchEngine3.setText("");
						
						sComp.resultStatus.append("\n谷歌搜索完毕，得到" + google.results.size() + "条结果。");
						statusLabel.setText("谷歌搜索完毕,共返回" + google.results.size() + "条结果。");
					}
				});
			}
			
			// 用搜狗搜索
			display.syncExec(new Runnable() { 
						public void run() {
							sComp.status4.setImage(SWTResourceManager.getImage("img/info.gif"));
							sComp.searchEngine4.setText("正在搜索……");
							statusLabel.setText("搜狗正在搜索……");
						}
					});
			status = sogou.doSearch(rule, rule.getCnt());
			if (status == 1) {
				display.syncExec(new Runnable() {
					public void run() {
						sComp.status4.setImage(SWTResourceManager.getImage("img/close.gif"));
						sComp.searchEngine4.setText("");
						
						sComp.resultStatus.append("\n搜狗搜索失败。");
						statusLabel.setText("搜狗搜索失败。");
					}
				});
	
			} 
			else {
				if(resultList == null)
					resultList = new LinkedList<Result>();
				for(Result r : sogou.results)
					resultList.add(r);
				display.syncExec(new Runnable() {
					public void run() {
						sComp.status4.setImage(SWTResourceManager.getImage("img/correct.png"));
						sComp.searchEngine4.setText("");
						
						sComp.resultStatus.append("\n搜狗搜索完毕，得到" + sogou.results.size() + "条结果。");
						statusLabel.setText("搜狗搜索完毕,共返回" + sogou.results.size() + "条结果。");
					}
				});
			}
			
			// 用飕飕搜索
			display.syncExec(new Runnable() { 
						public void run() {
							sComp.status5.setImage(SWTResourceManager.getImage("img/info.gif"));
							sComp.searchEngine5.setText("正在搜索……");
							statusLabel.setText("飕飕正在搜索……");
						}
					});
			status = soso.doSearch(rule, rule.getCnt());
			if (status == 1) {
				display.syncExec(new Runnable() {
					public void run() {
						sComp.status5.setImage(SWTResourceManager.getImage("img/close.gif"));
						sComp.searchEngine5.setText("");
						
						sComp.resultStatus.append("\n飕飕搜索失败。");
						statusLabel.setText("飕飕搜索失败。");
					}
				});
	
			} 
			else {
				if(resultList == null)
					resultList = new LinkedList<Result>();
				for(Result r : soso.results)
					resultList.add(r);
				display.syncExec(new Runnable() {
					public void run() {
						sComp.status5.setImage(SWTResourceManager.getImage("img/correct.png"));
						sComp.searchEngine5.setText("");
						
						sComp.resultStatus.append("\n飕飕搜索完毕，得到" + soso.results.size() + "条结果。");
						statusLabel.setText("飕飕搜索完毕,共返回" + soso.results.size() + "条结果。");
					}
				});
			}
			
			// 用雅虎搜索
			display.syncExec(new Runnable() { 
						public void run() {
							sComp.status6.setImage(SWTResourceManager.getImage("img/info.gif"));
							sComp.searchEngine6.setText("正在搜索……");
							statusLabel.setText("雅虎正在搜索……");
						}
					});
			status = yahoo.doSearch(rule, rule.getCnt());
			if (status == 1) {
				display.syncExec(new Runnable() {
					public void run() {
						sComp.status6.setImage(SWTResourceManager.getImage("img/close.gif"));
						sComp.searchEngine6.setText("");
						
						sComp.resultStatus.append("\n雅虎搜索失败。");
						statusLabel.setText("雅虎搜索失败。");
					}
				});
	
			} 
			else {
				if(resultList == null)
					resultList = new LinkedList<Result>();
				for(Result r : yahoo.results)
					resultList.add(r);
				display.syncExec(new Runnable() {
					public void run() {
						sComp.status6.setImage(SWTResourceManager.getImage("img/correct.png"));
						sComp.searchEngine6.setText("");
						
						sComp.resultStatus.append("\n雅虎搜索完毕，得到" + yahoo.results.size() + "条结果。");
						statusLabel.setText("雅虎搜索完毕,共返回" + yahoo.results.size() + "条结果。");
					}
				});
			}
			
			// 用有道搜索
			display.syncExec(new Runnable() { 
						public void run() {
							sComp.status7.setImage(SWTResourceManager.getImage("img/info.gif"));
							sComp.searchEngine7.setText("正在搜索……");
							statusLabel.setText("有道正在搜索……");
						}
					});
			status = youdao.doSearch(rule, rule.getCnt());
			if (status == 1) {
				display.syncExec(new Runnable() {
					public void run() {
						sComp.status7.setImage(SWTResourceManager.getImage("img/close.gif"));
						sComp.searchEngine7.setText("");
						
						sComp.resultStatus.append("\n有道搜索失败。");
						statusLabel.setText("有道搜索失败。");
					}
				});
	
			} 
			else {
				if(resultList == null)
					resultList = new LinkedList<Result>();
				for(Result r : youdao.results)
					resultList.add(r);
				display.syncExec(new Runnable() {
					public void run() {
						sComp.status7.setImage(SWTResourceManager.getImage("img/correct.png"));
						sComp.searchEngine7.setText("");
						
						sComp.resultStatus.append("\n有道搜索完毕，得到" + youdao.results.size() + "条结果。");
						statusLabel.setText("有道搜索完毕,共返回" + youdao.results.size() + "条结果。");
					}
				});
			}
			
			//最后的处理
			if (resultList != null) {
				searchResult = new SearchResult(0, rule, resultList);

				// 过滤处理
				display.syncExec(new Runnable() {
					public void run() {
						sComp.resultStatus.append("\n共得到" + resultList.size()
								+ "条结果。");
						statusLabel.setText("正在过滤搜索结果……");
					}
				});
				searchResult = filter.filter(searchResult);
				// 排序
				display.syncExec(new Runnable() {
					public void run() {
						sComp.resultStatus.append("\n过滤相同结果后得到"
								+ searchResult.getResults().size() + "条结果。");
						statusLabel.setText("正在排序……");
						title = text_title.getText();
					}
				});
				searchResult = sensa.calculateSensativity(searchResult);
				searchResult = order.order(searchResult, title);
				html = HTMLPrinter.outputResultToHtmlString(searchResult);
				display.syncExec(new Runnable() {
					public void run() {
						sComp.browser.setText(html);
						sComp.resultStatus.append("\n与输入的标题相似的有"
								+ searchResult.getResults().size() + "条结果。");

						statusLabel.setText("全部搜索引擎搜索完毕。");
					}
				});
			}
			else{
				html = HTMLPrinter.outputResultToHtmlString(searchResult);
				display.syncExec(new Runnable() {
					public void run() {
						sComp.browser.setText(html);

						statusLabel.setText("全部搜索引擎搜索完毕。");
					}
				});
			}
		}
	}
}
