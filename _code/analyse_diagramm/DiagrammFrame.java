package analyse_diagramm;

import java.awt.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.labels.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.*;

import model.*;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta), Gerit Wagner
 */
public class DiagrammFrame extends JFrame {

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
	
	protected DiagrammController controller;
	protected AthletenListe athletenliste;
	
	public DiagrammFrame(AthletenListe athletenliste) {
		this.athletenliste = athletenliste;
		// ggf. athletenliste.addObserver(this)
		controller = new DiagrammController(this, athletenliste);		

        initWindowProperties();
		datenSammlungLeistungen = datenLeistungen;
		datenSammlungBestzeiten = datenBestzeiten;
		JFreeChart chart = initChart();
		loadChart(chart);
        controller.openAllAthletes();

    	setEnabled(true);
		setVisible(true);
	}

	private JFreeChart initChart(){
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
        return chart;
	}
	
	private void loadChart(JFreeChart chart){
		initDotRenderer();
        initSplineRenderer();
        
        TickUnitSource ticks = NumberAxis.createIntegerTickUnits();
        NumberAxis range = (NumberAxis) elternPlot.getRangeAxis();
        range.setStandardTickUnits(ticks);
        
        ChartPanel cp = new ChartPanel(chart);
        contentPane.add(cp);
        pack();		
	}
	protected void release(){
		// ggf. athletenliste.deleteObserver(this);
		athletenliste = null;
		controller.release();
		controller = null;
		setEnabled(false);
		dispose();
	}
	
	protected void addLeistungsSerie(XYSeries serie) {
		datenLeistungen.addSeries(serie);
		datenSammlungLeistungen = datenLeistungen;
		Color farbe = getRandomColor();
		dotRenderer.setSeriesPaint(datenSammlungLeistungen.getSeriesCount()-1, farbe);
		letzteFarbe = (Color) dotRenderer.getSeriesPaint(datenSammlungLeistungen.getSeriesCount()-1);
	}
	
	protected void addBerechneteBestzeitenUndTrendlinie(XYSeries serie) {
		datenBestzeiten.addSeries(serie);
		datenSammlungBestzeiten = datenBestzeiten;
		lineRenderer.setSeriesPaint(datenSammlungBestzeiten.getSeriesCount()-1, letzteFarbe);
	}	
	
	private Color getRandomColor(){
		int r = random.nextInt(256);
		int g = random.nextInt(256);
		int b = random.nextInt(256);
		return new Color(r,g,b);
	}
	
	private void initWindowProperties() {
		setTitle("Leistungkurven");
		setIconImage(Toolkit.getDefaultToolkit().getImage(DiagrammFrame.class.getResource("/bilder/Diagramm_24x24.png")));
		addWindowListener(controller);

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