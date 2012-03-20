package ui.MonitorWindow;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.LabelBlock;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.PieDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;

import database.WarningResultProcess;


public class PieChartComposite extends Composite {

	private WarningResultProcess wrp;
	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public PieChartComposite(Composite parent, WarningResultProcess wrp) {
		super(parent, SWT.NONE);
		this.wrp = wrp;
		this.setLayout(new FillLayout());
		createContent();
		//
	}

	public void createContent() {
		JFreeChart chart = createChart();
		final ChartComposite frame = new ChartComposite(this, SWT.NONE, chart,
				true);
		frame.pack();
	}
	
	@SuppressWarnings("deprecation")
	private JFreeChart createChart(){
		PieDataset dataSet = wrp.getStatisticsDataset();
		JFreeChart jfreechart = ChartFactory.createPieChart("预警信息分布图",
				                                            dataSet,
				                                            false,
				                                            true,
				                                            false);
		
		PiePlot pieplot = (PiePlot)jfreechart.getPlot();
        pieplot.setLabelFont(new Font("宋体", 0, 12));
        pieplot.setNoDataMessage("无数据");
        pieplot.setCircular(true);
        pieplot.setLabelGap(0.02D);
        pieplot.setBackgroundPaint(new Color(199,237,204));
                pieplot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0} {2}",
                NumberFormat.getNumberInstance(),
                new DecimalFormat("0.00%")));
        pieplot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator("{0} {2}"));  
        LegendTitle legendtitle = new LegendTitle(jfreechart.getPlot());
        BlockContainer blockcontainer = new BlockContainer(new BorderArrangement());
        blockcontainer.setBorder(new BlockBorder(1.0D, 1.0D, 1.0D, 1.0D));
        LabelBlock labelblock = new LabelBlock("按规则统计:", new Font("宋体", 1, 14));
        labelblock.setPadding(5D, 5D, 5D, 5D);
        blockcontainer.add(labelblock, RectangleEdge.TOP);
        BlockContainer blockcontainer1 = legendtitle.getItemContainer();
        blockcontainer1.setPadding(2D, 10D, 5D, 2D);
        blockcontainer.add(blockcontainer1);
        legendtitle.setWrapper(blockcontainer);
        legendtitle.setPosition(RectangleEdge.RIGHT);
        legendtitle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        jfreechart.addSubtitle(legendtitle);
        jfreechart.setBackgroundPaint(new Color(199,237,204));

		return jfreechart;
	}
	
}
