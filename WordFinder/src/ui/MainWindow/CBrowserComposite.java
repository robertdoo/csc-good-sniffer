package ui.MainWindow;

import java.io.File;

import lucene.Search.ResultOrder;
import lucene.Search.ResultOutput;
import lucene.Search.ResultSensativity;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import com.swtdesigner.SWTResourceManager;

import dataStruct.SearchResult;
import database.LogProcess;


public class CBrowserComposite extends Composite {

	private SearchResult searchResult = null, resultCopy = null
	                     , tempResult = null;//搜索结果
	private ResultOutput HTMLPrinter = null;//用于输出HTML的对象
	private ResultOrder order = null;//用于排序的对象
	private ResultSensativity sensa = null;//计算敏感度的对象
	private int pageNum;//总页数
	private int curPage;//当前页号
	private int count = 10;//每页显示结果数
	private int begin, end;//每一页的开始项和结束项
	private String html = "";//浏览器显示的内容
	
	private Browser browser;
	private Combo combo_count, combo_page;//显示结果数，当前页码，敏感度
	private Text textTitle;//输入标题的文本框
	private Button buttonPre, buttonNext;
	private Button check_onlySensative;
	private Button btn_forback, btn_order;  //工具栏按钮
	private SashForm sash;
	private CTabFolder searchInfoFolder;
	private ToolBar bar;
	private Text searchInfoText;
	private Composite browserComp;
	private int style;//浏览面板的类型，1用于公共搜索引擎，2用于论坛搜索引擎
	
	public CBrowserComposite(Composite parent, int style, SearchResult sr) {
		super(parent, SWT.NONE);
		this.style = style;
		this.searchResult = sr;
		this.resultCopy = sr;
		this.HTMLPrinter = new ResultOutput();
		this.order = new ResultOrder();
		this.sensa = new ResultSensativity();
		createContent();
	}
	
	public SearchResult getResultCopy() {
		return resultCopy;
	}

	public void setResultCopy(SearchResult searchResult) {
		this.resultCopy = searchResult;
		this.searchResult = resultCopy;
		outResult();
	}

	public void setSearchInfo(String text) {
		searchInfoText.setText(text);
	}
	
	public void appendSearchInfo(String text) {
		searchInfoText.append(text);
	}
	
	protected void createContent(){
		this.setLayout(new GridLayout());
		
		sash = new SashForm(this, SWT.NONE);
		sash.setLayoutData(new GridData(GridData.FILL_BOTH));
		sash.setOrientation(SWT.VERTICAL);
		
		browserComp = new Composite(sash, SWT.NONE);
		browserComp.setLayout(new GridLayout());
		
		///////////////////////////////顶部面板//////////////////////////////////////
		Composite top = new Composite(browserComp, SWT.NONE);
		top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		top.setLayout(new GridLayout(15,true));
		
		btn_forback = new Button(top, SWT.FLAT);
		final GridData gd_forback = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		btn_forback.setLayoutData(gd_forback);
		btn_forback.setText("后退");
		btn_forback.setImage(SWTResourceManager.getImage("img/backward.gif"));
		btn_forback.setToolTipText("后退到搜索结果");
		btn_forback.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//后退到搜索结果
				searchResult = resultCopy;
				outResult();
			}
		});

		textTitle = new Text(top, SWT.BORDER);
		textTitle.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		final GridData gd_title = new GridData(SWT.FILL, SWT.CENTER, true, false, 8, 1);
		textTitle.setLayoutData(gd_title);
		
		btn_order = new Button(top, SWT.FLAT);
		final GridData gd_order = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		btn_order.setLayoutData(gd_order);
		btn_order.setText("排序");
		btn_order.setImage(SWTResourceManager.getImage("img/order_tiny.gif"));
		btn_order.setToolTipText("按时间排序");
		btn_order.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//按标题排序
				doOrder();
			}
		});
		
		Label label1 = new Label(top, SWT.NONE);
		final GridData gd_label = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_label.horizontalSpan = 2;
		label1.setLayoutData(gd_label);
		label1.setText("显示结果条数：");
		
		combo_count = new Combo(top, SWT.READ_ONLY);
		combo_count.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				count = Integer.parseInt(combo_count.getText());
				outResult();
			}
		});
		final GridData gd_combo_count = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_combo_count.horizontalSpan = 1;
		combo_count.setLayoutData(gd_combo_count);
		combo_count.setItems(new String[]{"10","20","50"});
		combo_count.setBackgroundMode(SWT.INHERIT_DEFAULT);
		combo_count.select(0);		
        ///////////////////////////////顶部面板//////////////////////////////////////
		
		//////////////////////////////中间的浏览器///////////////////////////////////
		browser = new Browser(browserComp, SWT.NONE);
		browser.setLayoutData(new GridData(GridData.FILL_BOTH));
		browser.setText(html);
		//////////////////////////////中间的浏览器///////////////////////////////////
		
		/////////////////////////////底部面板////////////////////////////////////////
		Composite bottom = new Composite(browserComp, SWT.NONE);
		bottom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		bottom.setLayout(new GridLayout(10,true));
		
		check_onlySensative = new Button(bottom, SWT.CHECK);
		check_onlySensative.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(check_onlySensative.getSelection()){
					tempResult = searchResult;
					searchResult = sensa.sensativeFilter(searchResult);
					outResult();
				}
				else{
					searchResult = tempResult;
					outResult();
				}
			}
		});
		final GridData gd_onlySensative = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_onlySensative.heightHint = 15;
		gd_onlySensative.horizontalSpan = 2;
		check_onlySensative.setLayoutData(gd_onlySensative);
		check_onlySensative.setText("只显示预警信息");
		
		Composite sensaCombo_buttonPre = new Composite(bottom, SWT.NONE);   //空白版面，占位置
		final GridData gd_sensaCombo_buttonPre = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_sensaCombo_buttonPre.heightHint = 15;
		gd_sensaCombo_buttonPre.horizontalSpan = 3;
		sensaCombo_buttonPre.setLayoutData(gd_sensaCombo_buttonPre);
		
		buttonPre = new Button(bottom, SWT.NONE);
		buttonPre.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonPre.setText("上一页");
		buttonPre.setEnabled(false);
		buttonPre.addSelectionListener(new SelectionAdapter(){  
			public void widgetSelected(SelectionEvent e){
				//上一页
				pagePrivious();
			}
		});
		
		Label label2 = new Label(bottom, SWT.NONE);
		label2.setAlignment(SWT.RIGHT);
		label2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label2.setText("转到第");
		
		combo_page = new Combo(bottom, SWT.NONE);
		combo_page.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				pageGoto();
			}
		});
		combo_page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label label3 = new Label(bottom, SWT.NONE);
		label3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label3.setText("页");
		
		buttonNext = new Button(bottom, SWT.NONE);
		buttonNext.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonNext.setText("下一页");
		buttonNext.setEnabled(false);
		buttonNext.addSelectionListener(new SelectionAdapter(){  
			public void widgetSelected(SelectionEvent e){
				//下一页
				pageNext();
			}
		});
        /////////////////////////////底部面板////////////////////////////////////////
		
		searchInfoFolder = new CTabFolder(sash, SWT.BORDER);
		searchInfoFolder.setMinimizeVisible(true);
		searchInfoFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void minimize(final CTabFolderEvent event) {
				sash.setMaximizedControl(browserComp);
				bar.setVisible(true);
			}
		});
		CTabItem searchInfoItem = new CTabItem(searchInfoFolder, SWT.NONE);
		searchInfoItem.setText("搜索信息");
		searchInfoItem.setImage(SWTResourceManager.getImage("img/console.gif"));
		searchInfoText = new Text(searchInfoFolder, SWT.WRAP | SWT.V_SCROLL);
		searchInfoText.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		searchInfoItem.setControl(searchInfoText);
		searchInfoFolder.setSelection(searchInfoItem);
		
		sash.setWeights(new int[]{75,25});
		
		bar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		bar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ToolItem restoreItem = new ToolItem(bar, SWT.PUSH);
		restoreItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				sash.setMaximizedControl(null);
				bar.setVisible(false);
			}
		});
		restoreItem.setText("搜索信息");
		restoreItem.setImage(SWTResourceManager.getImage("img/console.gif"));
		bar.setVisible(true);
		sash.setMaximizedControl(browserComp);
		
		//outWait();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	/**
	 * 输出即时搜索欢迎画面
	 */
	public void outImediateWelcome(){
		html = "<h3 align='center'>即时搜索</h3>"
				+ "<p align='center'>即时搜索的主要功能是根据选择的规则立即进行搜索，并返回结果。</p>"
				+ "<p align='center'>如有关于<u>即时搜索</u>的更多问题，请参阅帮助文档。</p>";
		browser.setText(html);
		btn_forback.setEnabled(false);
		textTitle.setEnabled(false);
		btn_order.setEnabled(false);
		combo_count.setEnabled(false);
		combo_page.setEnabled(false);
		check_onlySensative.setEnabled(false);
	}
	
	/**
	 * 输出定时搜索欢迎画面
	 */
	public void outTimedWelcome(){
		html = "<h3 align='center'>定时搜索</h3>"
				+ "<p align='center'>定时搜索的主要功能是根据选择的规则和时间间隔自动进行搜索，并返回结果。</p>"
				+ "<p align='center'>自动对每次的搜索结果进行更新提醒和预警提醒，达到实时监控的目的。</p>"
				+ "<p align='center'>如有关于<u>定时搜索</u>的更多问题，请参阅帮助文档。</p>";
		browser.setText(html);
		btn_forback.setEnabled(false);
		textTitle.setEnabled(false);
		btn_order.setEnabled(false);
		combo_count.setEnabled(false);
		combo_page.setEnabled(false);
		check_onlySensative.setEnabled(false);
	}
	
	/**
	 * 输出论坛即时搜索欢迎画面
	 */
	public void outForumImediateWelcome(){
		html = "<h3 align='center'>论坛搜索</h3>"
				+ "<p align='center'>论坛搜索是针对特定的网域（如论坛或网站）中的内容进行检索，抓取网域中的内容到本地进行分析。</p>"
				+ "<p align='center'>如果发现跟敏感信息相关的内容，就进行预警提醒，达到实时监控的目的。</p>"
				+ "<p align='center'>如有关于<u>论坛搜索</u>的更多问题，请参阅帮助文档。</p>";
		browser.setText(html);
		btn_forback.setEnabled(false);
		textTitle.setEnabled(false);
		btn_order.setEnabled(false);
		combo_count.setEnabled(false);
		combo_page.setEnabled(false);
		check_onlySensative.setEnabled(false);
	}
	
	/**
	 * 输出论坛定时搜索欢迎画面
	 */
	public void outForumTimedWelcome(){
		html = "<h3 align='center'>论坛实时监控</h3>"
				+ "<p align='center'>论坛搜索是针对特定的网域（如论坛或网站）中的内容进行检索，抓取网域中的内容到本地进行分析。</p>"
				+ "<p align='center'>如果发现跟敏感信息相关的内容，就进行预警提醒，达到实时监控的目的。</p>"
				+ "<p align='center'>如有关于<u>论坛搜索</u>的更多问题，请参阅帮助文档。</p>";
		browser.setText(html);
		btn_forback.setEnabled(false);
		textTitle.setEnabled(false);
		btn_order.setEnabled(false);
		combo_count.setEnabled(false);
		combo_page.setEnabled(false);
		check_onlySensative.setEnabled(false);
	}
	
	/**
	 * 输出等待画面
	 */
	public void outWait(){
		String imgPath = new File("img/wait.png").getAbsolutePath();
		html = "<h1 align='center'><img src='" + imgPath + "'></h1>"
				+ "<p align='center'><table><tr><td>系统正在处理……</td></tr></table></p>";
		browser.setText(html);
		btn_forback.setEnabled(false);
		textTitle.setEnabled(false);
		btn_order.setEnabled(false);
		combo_count.setEnabled(false);
		combo_page.setEnabled(false);
		check_onlySensative.setEnabled(false);
	}
	
	/**
	 * 输出搜索失败画面
	 */
	public void outError(){
		String imgPath = new File("img/error.png").getAbsolutePath();
		html = "<h1 align='center'><img src='" + imgPath + "'></h1>"
				+ "<p align='center'>搜索失败！</p>"
				+ "<p align='center'>原因可能是以下之一：1、网络连接存在异常；"
				+ "<br>2、搜索引擎对于提交的规则没有搜索结果。</p>"
				+ "<p align='center'>您可以选择尝试再次搜索，或者参阅帮助文档中的<u>搜索结果</u>部分</p>";
		browser.setText(html);
	}
	
	/**
	 * 输出搜索结果，显示第一页
	 */
	private void outResult()
	{
		html = "";
		if(searchResult == null){
			System.out.println("没有搜索结果~！");	
			html = HTMLPrinter.outputResultToHtmlStringAsTable_abstract(searchResult, begin, end);	
			browser.setText(html);
			
			String user = MainWindow.user;
			String operate = "论坛搜索";
			String detail =  "显示结果失败：没有搜索结果~ ";
			LogProcess sl = new LogProcess();
			sl.saveLog(user, operate, detail);
		}
		else{
			
			if(searchResult.getResults().size() % count == 0)
				pageNum = searchResult.getResults().size()/count;
			else
				pageNum = searchResult.getResults().size()/count + 1;
			curPage = 1;
			begin = 1;
			end = Math.min(count, searchResult.getResults().size());
			
			combo_page.removeAll();
			for(int i=1;i<=pageNum;i++)
				combo_page.add(String.valueOf(i));
			combo_page.select(curPage-1);
				
			buttonPre.setEnabled(false);
			if(pageNum > curPage)
				buttonNext.setEnabled(true);
			else
				buttonNext.setEnabled(false);
			
			if(style == 1){
				html += HTMLPrinter.outputResultToHtmlStringAsTable(searchResult, begin, end);	
			}
			if(style == 2){
				//html = searchProcesser.outputResultToHtmlString(searchResult, begin, end, "order", "");	
				html += HTMLPrinter.outputResultToHtmlStringAsTable_abstract(searchResult, begin, end);	
			}
			if(searchResult.getCountOfResults() != 0)
				html += ("<br><p align='center'><table><tr><td>-----------当前是第 <font color='red'>"
					+ curPage + " </font>页，共 " + pageNum + " 页-----------</td></tr></table></p>");
			browser.setText(html);

			buttonPre.setEnabled(false);
			if(pageNum > curPage)
				buttonNext.setEnabled(true);
			else
				buttonNext.setEnabled(false);
			
			if(searchResult.isSensative())
				check_onlySensative.setEnabled(true);
			
			String user = MainWindow.user;
			String operate = "论坛搜索";
			String detail =  "显示结果成功！ ";
			LogProcess sl = new LogProcess();
			sl.saveLog(user, operate, detail);
		}
		
		btn_forback.setEnabled(true);
		textTitle.setEnabled(true);
		btn_order.setEnabled(true);
		combo_count.setEnabled(true);
		combo_page.setEnabled(true);
		
	}
	
	/**
	 * 结果按标题排序
	 */
	private void doOrder() {
		html = "";
		
		String title = textTitle.getText();
		searchResult = order.order(searchResult, title);
		outResult();
		
		textTitle.setText("");
	}
	
	
	/**
	 * 输出上一页结果
	 */
	private void pagePrivious()
	{
		curPage--;
		if(curPage > 1)
			buttonPre.setEnabled(true);
		else
			buttonPre.setEnabled(false);
		if(curPage < pageNum)
			buttonNext.setEnabled(true);
		else
			buttonNext.setEnabled(false);
		
		begin = (curPage-1)*count + 1;
		end = Math.min((curPage*count), searchResult.getResults().size());
		combo_page.select(curPage-1);
		
		if(searchResult.getResults().size()!=resultCopy.getResults().size()){
			html += "<i>根据系统设置，部分结果可能未予显示。</i>";
		}
		if(style == 1){
			html = HTMLPrinter.outputResultToHtmlStringAsTable(searchResult, begin, end);	
		}
		if(style == 2){
			//html = searchProcesser.outputResultToHtmlString(searchResult, begin, end, "order", "");	
			html = HTMLPrinter.outputResultToHtmlStringAsTable_abstract(searchResult, begin, end);	
		}
		html += ("<br><p align='center'><table><tr><td>-----------当前是第 <font color='red'>"
				+ curPage + " </font>页，共 " + pageNum + " 页-----------</td></tr></table></p>");
		browser.setText(html);
	}
	
	/**
	 * 输出下一页结果
	 */
	private void pageNext()
	{
		curPage++;
		if(curPage < pageNum)
			buttonNext.setEnabled(true);
		else
			buttonNext.setEnabled(false);
		if(curPage > 1)
			buttonPre.setEnabled(true);
		else
			buttonPre.setEnabled(false);
		
		begin = (curPage-1)*count + 1;
		end = Math.min((curPage*count), searchResult.getResults().size());
		combo_page.select(curPage-1);
		
		if(searchResult.getResults().size()!=resultCopy.getResults().size()){
			html += "<i>根据系统设置，部分结果可能未予显示。</i>";
		}
		if(style == 1){
			html = HTMLPrinter.outputResultToHtmlStringAsTable(searchResult, begin, end);	
		}
		if(style == 2){
			//html = searchProcesser.outputResultToHtmlString(searchResult, begin, end, "order", "");	
			html = HTMLPrinter.outputResultToHtmlStringAsTable_abstract(searchResult, begin, end);	
		}
		html += ("<br><p align='center'><table><tr><td>-----------当前是第 <font color='red'>"
				+ curPage + " </font>页，共 " + pageNum + " 页-----------</td></tr></table></p>");
		browser.setText(html);
	}
	
	/**
	 * 输出指定页的结果
	 */
	private void pageGoto(){
		curPage = Integer.parseInt(combo_page.getText());
		if(curPage < pageNum)
			buttonNext.setEnabled(true);
		else
			buttonNext.setEnabled(false);
		if(curPage > 1)
			buttonPre.setEnabled(true);
		else
			buttonPre.setEnabled(false);
		
		begin = (curPage-1)*count + 1;
		end = Math.min((curPage*count), searchResult.getResults().size());
		
		if(style == 1){
			html = HTMLPrinter.outputResultToHtmlStringAsTable(searchResult, begin, end);	
		}
		if(style == 2){
			//html = searchProcesser.outputResultToHtmlString(searchResult, begin, end, "order", "");	
			html = HTMLPrinter.outputResultToHtmlStringAsTable_abstract(searchResult, begin, end);	
		}
		html += ("<br><p align='center'><table><tr><td>-----------当前是第 <font color='red'>"
				+ curPage + " </font>页，共 " + pageNum + " 页-----------</td></tr></table></p>");
		browser.setText(html);
	}
}
