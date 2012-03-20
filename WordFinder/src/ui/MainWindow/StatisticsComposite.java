package ui.MainWindow;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import com.swtdesigner.SWTResourceManager;

import dataStruct.Result;
import dataStruct.Rule;
import dataStruct.SearchResult;

public class StatisticsComposite extends Composite {

	private Combo combo_range;
	private Button button_cauculate;
	
	private Group group_option;
	private Composite graphicComp;
	private Tree ruleTree;
	private ChartComposite chartComp;
	
	private final int META_SEARCH = 1;
	private final int SPIDER_SEARCH = 2;
	
	public StatisticsComposite(Composite parent) {
		super(parent, SWT.BORDER);
		createContent();
	}

	private void createContent() {
		this.setLayout(new GridLayout());
		
		group_option = new Group(this, SWT.BORDER);
		group_option.setText("选择统计条件");
		group_option.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		button_cauculate = new Button(group_option, SWT.PUSH);
		button_cauculate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				new Thread(new calculateAllRunnable(
							combo_range.getText().equals("元搜索")?META_SEARCH:SPIDER_SEARCH
							)).start();
			}
		});
		button_cauculate.setText("开始统计……");
		button_cauculate.setBounds(274, 27, 97, 22);

		combo_range = new Combo(group_option, SWT.NONE);
		combo_range.setItems(new String[] {"元搜索", "论坛搜索"});
		combo_range.select(0);
		combo_range.setBounds(114, 29, 97, 18);

		final Label label = new Label(group_option, SWT.NONE);
		label.setText("统计范围：");
		label.setBounds(10, 32, 98, 15);
		
		SashForm sash = new SashForm(this, SWT.HORIZONTAL | SWT.BORDER);
		sash.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ruleTree = new Tree(sash, SWT.NONE);
		ruleTree.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(final SelectionEvent e) {
				new Thread(new calculateRuleRunnable(
						((TreeItem)e.item).getText().replaceAll("\\(.*\\)", ""),
						combo_range.getText().equals("元搜索")?META_SEARCH:SPIDER_SEARCH)
						).start();
			}
		});
		ruleTree.setToolTipText("双击显示具体信息分布");
		
		graphicComp = new Composite(sash, SWT.NONE);
		graphicComp.setLayout(new FillLayout());
		
		chartComp = new ChartComposite(graphicComp, SWT.NONE, null, true);	
		
		sash.setWeights(new int[]{20, 80});
	}
	
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	//统计所有规则的结果的线程
	class calculateAllRunnable implements Runnable{
		private Display display = Display.getDefault();
		private JFreeChart chart;
		private int range;   //统计范围，元搜索或论坛搜索
		private String[] ruleNames;
		private int[] resultCount;
		
		public calculateAllRunnable(int range) {
			this.range = range;
		}
		
		public void run() {
			display.asyncExec(new Runnable(){
				public void run() {
					MainWindow.setStatusLabel("正在统计数据……",
							SWTResourceManager.getImage("img/info.gif"));
					button_cauculate.setEnabled(false);
				}
			});
			chart = createChart(getAllRuleDataset(),
					range==META_SEARCH?"元搜索":"论坛搜索");
			display.asyncExec(new Runnable(){
				public void run() {
					if(ruleNames != null){
						if(ruleTree.getItemCount() != 0)ruleTree.removeAll();
						for(int i=0;i<ruleNames.length;i++){
							TreeItem resultItem = new TreeItem(ruleTree, SWT.NONE);
							resultItem.setText(ruleNames[i] + "(" + resultCount[i] + ")");
						}
					}
					
					chartComp.setChart(chart);
					chartComp.forceRedraw();
					MainWindow.setStatusLabel("统计完毕！",
							SWTResourceManager.getImage("img/info.gif"));
					button_cauculate.setEnabled(true);
				}
			});
		}
		
		@SuppressWarnings("deprecation")
		private JFreeChart createChart(CategoryDataset dataset, String type) {
			JFreeChart chart = ChartFactory.createBarChart3D(type + "敏感信息分布", // 图表标题
					"规则", // 目录轴的显示标签
					"信息数量", // 数值轴的显示标签
					dataset, // 数据集
					PlotOrientation.VERTICAL, // 图表方向：水平、垂直
					true, // 是否显示图例(对于简单的柱状图必须是false)
					true, // 是否生成工具
					true // 是否生成URL链接
					);
			CategoryPlot plot = chart.getCategoryPlot();// 获得图表区域对象
			plot.setNoDataMessage("无数据");       

			NumberAxis numberaxis = (NumberAxis) plot.getRangeAxis();     
	        CategoryAxis domainAxis = plot.getDomainAxis();     
	        TextTitle textTitle = chart.getTitle();   
	              
	        textTitle.setFont(new Font("黑体", Font.PLAIN, 20));                
	        domainAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 11));             
	        domainAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));     
	        numberaxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 12));                 
	        numberaxis.setLabelFont(new Font("黑体", Font.PLAIN, 12));                   
	        chart.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 12));  
			
			// 设置图表的颜色
			BarRenderer3D renderer = new BarRenderer3D();
			renderer.setBaseOutlinePaint(Color.red);
			renderer.setSeriesPaint(0, Color.RED);// 柱子的颜色为青色
			renderer.setSeriesOutlinePaint(0, Color.BLACK);// 边框为黑色
			renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());   
	        renderer.setItemLabelFont(new Font("黑体",Font.BOLD,12));//12号黑体加粗   
	        renderer.setItemLabelPaint(Color.black);//字体为黑色 
			renderer.setItemLabelsVisible(true);
			
			plot.setRenderer(renderer);// 使用我们设计的效果
			
			return chart;
		}
		
		private CategoryDataset getAllRuleDataset() {
			DefaultCategoryDataset dataset = new DefaultCategoryDataset(); 
			
			ruleNames = MainWindow.rp.getAllRulenamesAsArray();
			resultCount = new int[ruleNames.length];
			Rule rule;
			SearchResult searchResult = null;
			for(int i = 0; i<ruleNames.length; i++){
				rule = MainWindow.rp.getRuleByName(ruleNames[i]);
				if(range == META_SEARCH)
					searchResult = MainWindow.metaEngineSearcher.search(rule);
				else if(range == SPIDER_SEARCH)
					searchResult = MainWindow.spiderSearcher.search(rule);
				resultCount[i] = (searchResult==null)?0:searchResult.getCountOfResults();
				
				dataset.setValue(resultCount[i],
						ruleNames[i],
						ruleNames[i]);
			}
			
			return dataset;
		}
	}
	
	//统计单条规则的线程
	class calculateRuleRunnable implements Runnable{
		private Display display = Display.getDefault();
		private int range;   //统计范围，元搜索或论坛搜索
		private String ruleName;//规则名称
		private JFreeChart chart;
		private HashMap<String, Integer> sourceCount;
		
		public calculateRuleRunnable(String ruleName, int range) {
			this.ruleName = ruleName;
			this.range = range;
		}
		
		public void run() {
			display.asyncExec(new Runnable(){
				public void run() {
					ruleTree.setEnabled(false);
					MainWindow.setStatusLabel("正在分析规则“" + ruleName + "”的结果数据……",
							SWTResourceManager.getImage("img/info.gif"));
				}
			});
			Rule rule = MainWindow.rp.getRuleByName(ruleName);
			SearchResult searchResult = null;
			if(range == META_SEARCH)
				searchResult = MainWindow.metaEngineSearcher.search(rule);
			else if(range == SPIDER_SEARCH)
				searchResult = MainWindow.spiderSearcher.search(rule);
			sourceCount = getCountBySource(searchResult);

			chart = createChart(getRuleDataset(sourceCount), rule.getName());

			display.asyncExec(new Runnable(){
				public void run() {
					chartComp.setChart(chart);
					chartComp.forceRedraw();
					
					MainWindow.setStatusLabel("规则“" + ruleName + "”的结果数据分析完毕！",
							SWTResourceManager.getImage("img/info.gif"));
					ruleTree.setEnabled(true);
				}
			});
		}
		
		//把一个SearchResult中的结果按来源统计
		private HashMap<String, Integer> getCountBySource(SearchResult searchResult) {
			HashMap<String, Integer> countMap = new HashMap<String, Integer>();
			if(searchResult == null)return countMap;
			
			for(Result result : searchResult.getResults()){
				if(result.getSource() == null)result.setSource("未知");
				if(countMap.containsKey(result.getSource()))
					countMap.put(result.getSource(),countMap.get(result.getSource())+1);
				else
					countMap.put(result.getSource(),1);
			}
			
			return countMap;
		}
		
		@SuppressWarnings("deprecation")
		private JFreeChart createChart(CategoryDataset dataset, String ruleName) {
			JFreeChart chart = ChartFactory.createBarChart3D("规则“" + ruleName + "”的信息分布", // 图表标题
					"来源", // 目录轴的显示标签
					"信息数量", // 数值轴的显示标签
					dataset, // 数据集
					PlotOrientation.VERTICAL, // 图表方向：水平、垂直
					true, // 是否显示图例(对于简单的柱状图必须是false)
					true, // 是否生成工具
					true // 是否生成URL链接
					);
			CategoryPlot plot = chart.getCategoryPlot();// 获得图表区域对象
			plot.setNoDataMessage("无数据");       

			NumberAxis numberaxis = (NumberAxis) plot.getRangeAxis();     
	        CategoryAxis domainAxis = plot.getDomainAxis();     
	        TextTitle textTitle = chart.getTitle();   
	              
	        textTitle.setFont(new Font("黑体", Font.PLAIN, 20));                
	        domainAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 11));             
	        domainAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));     
	        numberaxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 12));                 
	        numberaxis.setLabelFont(new Font("黑体", Font.PLAIN, 12));                   
	        chart.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 12));  
			
			// 设置图表的颜色
			BarRenderer3D renderer = new BarRenderer3D();
			renderer.setBaseOutlinePaint(Color.red);
			renderer.setSeriesPaint(0, Color.RED);// 柱子的颜色为青色
			renderer.setSeriesOutlinePaint(0, Color.BLACK);// 边框为黑色
			renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());   
	        renderer.setItemLabelFont(new Font("黑体",Font.BOLD,12));//12号黑体加粗   
	        renderer.setItemLabelPaint(Color.black);//字体为黑色 
			renderer.setItemLabelsVisible(true);
			
			plot.setRenderer(renderer);// 使用我们设计的效果
			
			return chart;
		}
		
		private CategoryDataset getRuleDataset(HashMap<String, Integer> sourceCount) {
			DefaultCategoryDataset dataset = new DefaultCategoryDataset(); 
			
			Object[] keys = sourceCount.keySet().toArray();
			for(int i = 0; i<keys.length; i++){			
				dataset.setValue(sourceCount.get((String)keys[i]),
						range == META_SEARCH?sourceTranslate((String)keys[i]):(String)keys[i],
								range == META_SEARCH?sourceTranslate((String)keys[i]):(String)keys[i]);
			}
			
			return dataset;
		}
		
		private String sourceTranslate(String source) {
			if(source.equals("baidu"))return "百度";
			else if(source.equals("google"))return "谷歌";
			else if(source.equals("bing"))return "必应";
			else if(source.equals("sogou"))return "搜狗";
			else if(source.equals("soso"))return "搜搜";
			else if(source.equals("yahoo"))return "雅虎";
			else return "有道";
		}
	}
}
