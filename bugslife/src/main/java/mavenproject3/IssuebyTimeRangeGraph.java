package mavenproject3;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.ShapeUtilities;

/**
 * In this line chart, a SymbolAxis is used for the range axis.
 */
public class IssuebyTimeRangeGraph extends ApplicationFrame {

    public static boolean flag = false;
    public static Date date1 = new Date();
    public static Date date2 = new Date();

    /**
     * Creates a new demo.
     *
     * @param title the frame title.
     */
    public IssuebyTimeRangeGraph(String title) throws ParseException, IOException {
        super(title);
        JPanel chartPanel = createDemoPanel();
        chartPanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartPanel);
    }

    private static CategoryDataset createDateRange() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        ArrayList<Long> values = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>();
        ReportGeneration.occurrencesDateRange.values().forEach(obj -> values.add(obj));
        ReportGeneration.occurrencesDateRange.keySet().forEach(obj -> keys.add(obj));

        for (int i = 0; i < ReportGeneration.occurrencesDateRange.keySet().size(); i++) {
            dataset.addValue(values.get(i), "", keys.get(i));
        }
        return dataset;
    }

    private static JFreeChart createChart3(CategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createLineChart(
                "Number Of Issue Against Date Range Defined By User", // chart title
                "Date", // domain axis label
                "Number of Issue", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
        );

        chart.setBackgroundPaint(Color.white);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.white);

        // customise the renderer...
        LineAndShapeRenderer renderer
                = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesVisible(1, false);
        renderer.setSeriesShapesVisible(2, true);
        renderer.setSeriesLinesVisible(2, false);
        renderer.setSeriesShape(2, ShapeUtilities.createDiamond(4.0f));
        renderer.setDrawOutlines(true);
        renderer.setUseFillPaint(true);
        renderer.setFillPaint(Color.white);

        return chart;
    }

    /**
     * Creates a panel for the demo (used by SuperDemo.java).
     *
     * @return A panel.
     */
    public static JPanel createDemoPanel() throws ParseException, IOException {
                    JFreeChart chart = createChart3(createDateRange());
                    return new ChartPanel(chart);
    }
    /**
     * Starting point for the demonstration application.
     *
     * @param args ignored.
     */
    public static void GraphIssueFTR() throws ParseException, IOException {
        IssuebyTimeRangeGraph demo = new IssuebyTimeRangeGraph("Number Of Issue Against Date Range Defined By User");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

}
