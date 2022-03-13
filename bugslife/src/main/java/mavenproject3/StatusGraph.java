package mavenproject3;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.SymbolAxis;
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
public class StatusGraph extends ApplicationFrame {

    public static boolean flag = false;
    public static Date date1 = new Date();
    public static Date date2 = new Date();

    /**
     * Creates a new demo.
     *
     * @param title the frame title.
     */
    public StatusGraph(String title) throws ParseException, IOException {
        super(title);
        JPanel chartPanel = createDemoPanel();
        chartPanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartPanel);
    }

    private static CategoryDataset createStatus() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(ReportGeneration.numOpen, "", "Open");
        dataset.addValue(ReportGeneration.numResolved, "", "Resolved");
        dataset.addValue(ReportGeneration.numClose, "", "Close");
        dataset.addValue(ReportGeneration.numInProgress, "", "In Progress");
        return dataset;
    }

    private static JFreeChart createChart1(CategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createLineChart(
                "Number Of Issue Against Status Weekly", // chart title
                "Status", // domain axis label
                "Number Of Issue", // range axis label
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

    public static JPanel createDemoPanel() throws ParseException, IOException {

        JFreeChart chart = createChart1(createStatus());
        return new ChartPanel(chart);

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args ignored.
     */
    public static void GraphStatus() throws ParseException, IOException {
        StatusGraph demo = new StatusGraph("Number Of Issue Against Status Weekly");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

}
