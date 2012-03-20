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
	                     , tempResult = null;//�������
	private ResultOutput HTMLPrinter = null;//�������HTML�Ķ���
	private ResultOrder order = null;//��������Ķ���
	private ResultSensativity sensa = null;//�������жȵĶ���
	private int pageNum;//��ҳ��
	private int curPage;//��ǰҳ��
	private int count = 10;//ÿҳ��ʾ�����
	private int begin, end;//ÿһҳ�Ŀ�ʼ��ͽ�����
	private String html = "";//�������ʾ������
	
	private Browser browser;
	private Combo combo_count, combo_page;//��ʾ���������ǰҳ�룬���ж�
	private Text textTitle;//���������ı���
	private Button buttonPre, buttonNext;
	private Button check_onlySensative;
	private Button btn_forback, btn_order;  //��������ť
	private SashForm sash;
	private CTabFolder searchInfoFolder;
	private ToolBar bar;
	private Text searchInfoText;
	private Composite browserComp;
	private int style;//����������ͣ�1���ڹ����������棬2������̳��������
	
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
		
		///////////////////////////////�������//////////////////////////////////////
		Composite top = new Composite(browserComp, SWT.NONE);
		top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		top.setLayout(new GridLayout(15,true));
		
		btn_forback = new Button(top, SWT.FLAT);
		final GridData gd_forback = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		btn_forback.setLayoutData(gd_forback);
		btn_forback.setText("����");
		btn_forback.setImage(SWTResourceManager.getImage("img/backward.gif"));
		btn_forback.setToolTipText("���˵��������");
		btn_forback.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//���˵��������
				searchResult = resultCopy;
				outResult();
			}
		});

		textTitle = new Text(top, SWT.BORDER);
		textTitle.setFont(SWTResourceManager.getFont("����", 12, SWT.NONE));
		final GridData gd_title = new GridData(SWT.FILL, SWT.CENTER, true, false, 8, 1);
		textTitle.setLayoutData(gd_title);
		
		btn_order = new Button(top, SWT.FLAT);
		final GridData gd_order = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		btn_order.setLayoutData(gd_order);
		btn_order.setText("����");
		btn_order.setImage(SWTResourceManager.getImage("img/order_tiny.gif"));
		btn_order.setToolTipText("��ʱ������");
		btn_order.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//����������
				doOrder();
			}
		});
		
		Label label1 = new Label(top, SWT.NONE);
		final GridData gd_label = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_label.horizontalSpan = 2;
		label1.setLayoutData(gd_label);
		label1.setText("��ʾ���������");
		
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
        ///////////////////////////////�������//////////////////////////////////////
		
		//////////////////////////////�м�������///////////////////////////////////
		browser = new Browser(browserComp, SWT.NONE);
		browser.setLayoutData(new GridData(GridData.FILL_BOTH));
		browser.setText(html);
		//////////////////////////////�м�������///////////////////////////////////
		
		/////////////////////////////�ײ����////////////////////////////////////////
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
		check_onlySensative.setText("ֻ��ʾԤ����Ϣ");
		
		Composite sensaCombo_buttonPre = new Composite(bottom, SWT.NONE);   //�հװ��棬ռλ��
		final GridData gd_sensaCombo_buttonPre = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_sensaCombo_buttonPre.heightHint = 15;
		gd_sensaCombo_buttonPre.horizontalSpan = 3;
		sensaCombo_buttonPre.setLayoutData(gd_sensaCombo_buttonPre);
		
		buttonPre = new Button(bottom, SWT.NONE);
		buttonPre.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonPre.setText("��һҳ");
		buttonPre.setEnabled(false);
		buttonPre.addSelectionListener(new SelectionAdapter(){  
			public void widgetSelected(SelectionEvent e){
				//��һҳ
				pagePrivious();
			}
		});
		
		Label label2 = new Label(bottom, SWT.NONE);
		label2.setAlignment(SWT.RIGHT);
		label2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label2.setText("ת����");
		
		combo_page = new Combo(bottom, SWT.NONE);
		combo_page.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				pageGoto();
			}
		});
		combo_page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label label3 = new Label(bottom, SWT.NONE);
		label3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label3.setText("ҳ");
		
		buttonNext = new Button(bottom, SWT.NONE);
		buttonNext.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonNext.setText("��һҳ");
		buttonNext.setEnabled(false);
		buttonNext.addSelectionListener(new SelectionAdapter(){  
			public void widgetSelected(SelectionEvent e){
				//��һҳ
				pageNext();
			}
		});
        /////////////////////////////�ײ����////////////////////////////////////////
		
		searchInfoFolder = new CTabFolder(sash, SWT.BORDER);
		searchInfoFolder.setMinimizeVisible(true);
		searchInfoFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void minimize(final CTabFolderEvent event) {
				sash.setMaximizedControl(browserComp);
				bar.setVisible(true);
			}
		});
		CTabItem searchInfoItem = new CTabItem(searchInfoFolder, SWT.NONE);
		searchInfoItem.setText("������Ϣ");
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
		restoreItem.setText("������Ϣ");
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
	 * �����ʱ������ӭ����
	 */
	public void outImediateWelcome(){
		html = "<h3 align='center'>��ʱ����</h3>"
				+ "<p align='center'>��ʱ��������Ҫ�����Ǹ���ѡ��Ĺ����������������������ؽ����</p>"
				+ "<p align='center'>���й���<u>��ʱ����</u>�ĸ������⣬����İ����ĵ���</p>";
		browser.setText(html);
		btn_forback.setEnabled(false);
		textTitle.setEnabled(false);
		btn_order.setEnabled(false);
		combo_count.setEnabled(false);
		combo_page.setEnabled(false);
		check_onlySensative.setEnabled(false);
	}
	
	/**
	 * �����ʱ������ӭ����
	 */
	public void outTimedWelcome(){
		html = "<h3 align='center'>��ʱ����</h3>"
				+ "<p align='center'>��ʱ��������Ҫ�����Ǹ���ѡ��Ĺ����ʱ�����Զ����������������ؽ����</p>"
				+ "<p align='center'>�Զ���ÿ�ε�����������и������Ѻ�Ԥ�����ѣ��ﵽʵʱ��ص�Ŀ�ġ�</p>"
				+ "<p align='center'>���й���<u>��ʱ����</u>�ĸ������⣬����İ����ĵ���</p>";
		browser.setText(html);
		btn_forback.setEnabled(false);
		textTitle.setEnabled(false);
		btn_order.setEnabled(false);
		combo_count.setEnabled(false);
		combo_page.setEnabled(false);
		check_onlySensative.setEnabled(false);
	}
	
	/**
	 * �����̳��ʱ������ӭ����
	 */
	public void outForumImediateWelcome(){
		html = "<h3 align='center'>��̳����</h3>"
				+ "<p align='center'>��̳����������ض�����������̳����վ���е����ݽ��м�����ץȡ�����е����ݵ����ؽ��з�����</p>"
				+ "<p align='center'>������ָ�������Ϣ��ص����ݣ��ͽ���Ԥ�����ѣ��ﵽʵʱ��ص�Ŀ�ġ�</p>"
				+ "<p align='center'>���й���<u>��̳����</u>�ĸ������⣬����İ����ĵ���</p>";
		browser.setText(html);
		btn_forback.setEnabled(false);
		textTitle.setEnabled(false);
		btn_order.setEnabled(false);
		combo_count.setEnabled(false);
		combo_page.setEnabled(false);
		check_onlySensative.setEnabled(false);
	}
	
	/**
	 * �����̳��ʱ������ӭ����
	 */
	public void outForumTimedWelcome(){
		html = "<h3 align='center'>��̳ʵʱ���</h3>"
				+ "<p align='center'>��̳����������ض�����������̳����վ���е����ݽ��м�����ץȡ�����е����ݵ����ؽ��з�����</p>"
				+ "<p align='center'>������ָ�������Ϣ��ص����ݣ��ͽ���Ԥ�����ѣ��ﵽʵʱ��ص�Ŀ�ġ�</p>"
				+ "<p align='center'>���й���<u>��̳����</u>�ĸ������⣬����İ����ĵ���</p>";
		browser.setText(html);
		btn_forback.setEnabled(false);
		textTitle.setEnabled(false);
		btn_order.setEnabled(false);
		combo_count.setEnabled(false);
		combo_page.setEnabled(false);
		check_onlySensative.setEnabled(false);
	}
	
	/**
	 * ����ȴ�����
	 */
	public void outWait(){
		String imgPath = new File("img/wait.png").getAbsolutePath();
		html = "<h1 align='center'><img src='" + imgPath + "'></h1>"
				+ "<p align='center'><table><tr><td>ϵͳ���ڴ�����</td></tr></table></p>";
		browser.setText(html);
		btn_forback.setEnabled(false);
		textTitle.setEnabled(false);
		btn_order.setEnabled(false);
		combo_count.setEnabled(false);
		combo_page.setEnabled(false);
		check_onlySensative.setEnabled(false);
	}
	
	/**
	 * �������ʧ�ܻ���
	 */
	public void outError(){
		String imgPath = new File("img/error.png").getAbsolutePath();
		html = "<h1 align='center'><img src='" + imgPath + "'></h1>"
				+ "<p align='center'>����ʧ�ܣ�</p>"
				+ "<p align='center'>ԭ�����������֮һ��1���������Ӵ����쳣��"
				+ "<br>2��������������ύ�Ĺ���û�����������</p>"
				+ "<p align='center'>������ѡ�����ٴ����������߲��İ����ĵ��е�<u>�������</u>����</p>";
		browser.setText(html);
	}
	
	/**
	 * ��������������ʾ��һҳ
	 */
	private void outResult()
	{
		html = "";
		if(searchResult == null){
			System.out.println("û���������~��");	
			html = HTMLPrinter.outputResultToHtmlStringAsTable_abstract(searchResult, begin, end);	
			browser.setText(html);
			
			String user = MainWindow.user;
			String operate = "��̳����";
			String detail =  "��ʾ���ʧ�ܣ�û���������~ ";
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
				html += ("<br><p align='center'><table><tr><td>-----------��ǰ�ǵ� <font color='red'>"
					+ curPage + " </font>ҳ���� " + pageNum + " ҳ-----------</td></tr></table></p>");
			browser.setText(html);

			buttonPre.setEnabled(false);
			if(pageNum > curPage)
				buttonNext.setEnabled(true);
			else
				buttonNext.setEnabled(false);
			
			if(searchResult.isSensative())
				check_onlySensative.setEnabled(true);
			
			String user = MainWindow.user;
			String operate = "��̳����";
			String detail =  "��ʾ����ɹ��� ";
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
	 * �������������
	 */
	private void doOrder() {
		html = "";
		
		String title = textTitle.getText();
		searchResult = order.order(searchResult, title);
		outResult();
		
		textTitle.setText("");
	}
	
	
	/**
	 * �����һҳ���
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
			html += "<i>����ϵͳ���ã����ֽ������δ����ʾ��</i>";
		}
		if(style == 1){
			html = HTMLPrinter.outputResultToHtmlStringAsTable(searchResult, begin, end);	
		}
		if(style == 2){
			//html = searchProcesser.outputResultToHtmlString(searchResult, begin, end, "order", "");	
			html = HTMLPrinter.outputResultToHtmlStringAsTable_abstract(searchResult, begin, end);	
		}
		html += ("<br><p align='center'><table><tr><td>-----------��ǰ�ǵ� <font color='red'>"
				+ curPage + " </font>ҳ���� " + pageNum + " ҳ-----------</td></tr></table></p>");
		browser.setText(html);
	}
	
	/**
	 * �����һҳ���
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
			html += "<i>����ϵͳ���ã����ֽ������δ����ʾ��</i>";
		}
		if(style == 1){
			html = HTMLPrinter.outputResultToHtmlStringAsTable(searchResult, begin, end);	
		}
		if(style == 2){
			//html = searchProcesser.outputResultToHtmlString(searchResult, begin, end, "order", "");	
			html = HTMLPrinter.outputResultToHtmlStringAsTable_abstract(searchResult, begin, end);	
		}
		html += ("<br><p align='center'><table><tr><td>-----------��ǰ�ǵ� <font color='red'>"
				+ curPage + " </font>ҳ���� " + pageNum + " ҳ-----------</td></tr></table></p>");
		browser.setText(html);
	}
	
	/**
	 * ���ָ��ҳ�Ľ��
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
		html += ("<br><p align='center'><table><tr><td>-----------��ǰ�ǵ� <font color='red'>"
				+ curPage + " </font>ҳ���� " + pageNum + " ҳ-----------</td></tr></table></p>");
		browser.setText(html);
	}
}
