package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import main.Main;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Frame zum Anzeigen der Leistungskurven
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class DiagrammFrame extends JFrame {

//----------------------- VARIABLEN -----------------------	
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private XYPlot elternPlot;
	private XYSeriesCollection datenLeistungen = new XYSeriesCollection();
	private XYSeriesCollection datenBestzeiten = new XYSeriesCollection();
	private XYDataset datenSammlungLeistungen;
	private XYDataset datenSammlungBestzeiten;
	private XYLineAndShapeRenderer dotRenderer;
	private XYLineAndShapeRenderer lineRenderer;
	
	private Random random = new Random();
	private Color letzteFarbe = Color.GREEN;

//----------------------- KONSTRUKTOREN -----------------------
	/**
	 * Standardkonstruktor
	 */
	public DiagrammFrame() {
		initWindowProperties();
		
		datenSammlungLeistungen = datenLeistungen;
		datenSammlungBestzeiten = datenBestzeiten;
		
		LogAxis xAxis = new LogAxis("Streckenlänge");
	   	xAxis.setBase(10);
	   	xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        JFreeChart chart = ChartFactory.createXYLineChart(
        		"Leistungskurven",
                "Streckenlänge [m]",
                "Geschwindigkeiten [1.000m Zeit]",
                datenSammlungLeistungen,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        
        elternPlot = (XYPlot) chart.getPlot();
        elternPlot.setDomainAxis(xAxis);
        elternPlot.setBackgroundPaint(new Color(0xffffe0));
        elternPlot.setDomainGridlinesVisible(true);
        elternPlot.setDomainGridlinePaint(Color.lightGray);
        elternPlot.setRangeGridlinePaint(Color.lightGray);
        elternPlot.setDataset(0, datenSammlungLeistungen);
        elternPlot.setDataset(1, datenSammlungBestzeiten);

        initDotRenderer();
        initSplineRenderer();
        
        TickUnitSource ticks = NumberAxis.createIntegerTickUnits();
        NumberAxis range = (NumberAxis) elternPlot.getRangeAxis();
        range.setStandardTickUnits(ticks);
        
        ChartPanel cp = new ChartPanel(chart);
        contentPane.add(cp);
        pack();
	}

//----------------------- ÖFFENTLICHE METHODEN -----------------------
	/**
	 * Hinzufügen einer Serie mit Leistungen zum Plot (Scatterplot)
	 * @param serie
	 */
	public void addLeistungsSerie(XYSeries serie) {
		datenLeistungen.addSeries(serie);
		datenSammlungLeistungen = datenLeistungen;
		Color farbe = getRandomColor();
		dotRenderer.setSeriesPaint(datenSammlungLeistungen.getSeriesCount()-1, farbe);
		letzteFarbe = (Color) dotRenderer.getSeriesPaint(datenSammlungLeistungen.getSeriesCount()-1);
	}
	
	/**
	 * Hinzufügen einer Serie mit möglichen Bestleistungen zum Plot (Trendlinie)
	 * @param serie
	 */
	public void addBestzeitenSerie(XYSeries serie) {
		datenBestzeiten.addSeries(serie);
		datenSammlungBestzeiten = datenBestzeiten;
		lineRenderer.setSeriesPaint(datenSammlungBestzeiten.getSeriesCount()-1, letzteFarbe);
	}	
	
//----------------------- PRIVATE METHODEN -----------------------

	private Color getRandomColor(){
		int r = random.nextInt(256);
		int g = random.nextInt(256);
		int b = random.nextInt(256);
		return new Color(r,g,b);
		
	}
	

	/**
	 * Initialisieren der Eigenschaften des Diagramm-Frames
	 */
	private void initWindowProperties() {
		setTitle("Leistungkurven");
		setIconImage(Toolkit.getDefaultToolkit().getImage(DiagrammFrame.class.getResource("/bilder/Diagramm_24x24.png")));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				Main.mainFrame.diagrammController.DiagrammSchließen();
			}
		});
		setBounds(100, 100, 700, 432);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		setExtendedState(Frame.MAXIMIZED_BOTH);
	}
	
	/**
	 * Initialisieren des Renderers für das ScatterPlot der tatsächlichen Leistungen
	 */
	@SuppressWarnings("deprecation")
	private void initDotRenderer() {
		dotRenderer = new XYLineAndShapeRenderer(true, true);
        elternPlot.setRenderer(0, dotRenderer);
        dotRenderer.setBaseLinesVisible(false);
        dotRenderer.setBaseShapesFilled(true);
        dotRenderer.setSeriesVisibleInLegend(false);
       
        formatRenderer(dotRenderer);
	}
	
	/**
	 * Initialisieren des Renderers für das Trenddiagramm der möglichen Bestzeiten
	 */
	private void initSplineRenderer() {
		lineRenderer = new XYLineAndShapeRenderer(true, true);
        elternPlot.setRenderer(1, lineRenderer);
        lineRenderer.setBaseShapesVisible(true);
        lineRenderer.setBaseShapesFilled(true);

        formatRenderer(lineRenderer);
	}
	
	/**
	 * Initialisieren allgemeiner Eigenschaften für den übergebenen Renderer
	 * @param renderer: der zu "formatierende" Renderer
	 */
	@SuppressWarnings("deprecation")
	private void formatRenderer(XYLineAndShapeRenderer renderer) {
		renderer.setShape(new Rectangle(2,2));
		
        // set the renderer's stroke
        Stroke stroke = new BasicStroke(
            2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        renderer.setBaseOutlineStroke(stroke);

        // label the points
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);
        XYItemLabelGenerator generator =
            new StandardXYItemLabelGenerator(
                StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT,
                format, format);
        renderer.setBaseItemLabelGenerator(generator);
        renderer.setBaseItemLabelsVisible(true);
	}
	

}
