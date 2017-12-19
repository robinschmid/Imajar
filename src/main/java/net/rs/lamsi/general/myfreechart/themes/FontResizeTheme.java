package net.rs.lamsi.general.myfreechart.themes;

import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.PeriodAxis;
import org.jfree.chart.axis.PeriodAxisLabelInfo;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.Block;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.LabelBlock;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.CombinedRangeCategoryPlot;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.CompositeTitle;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.chart.util.ParamChecks;

public class FontResizeTheme {

	/** The name of this theme. */
	private String name;

	private AffineTransform at;

	public FontResizeTheme(double f) {
		at = AffineTransform.getScaleInstance(f, f);
	}

	/**
	 * Returns the name of this theme.
	 *
	 * @return The name of this theme.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Applies this theme to the supplied chart.
	 *
	 * @param chart  the chart ({@code null} not permitted).
	 */
	public void apply(JFreeChart chart) {
		ParamChecks.nullNotPermitted(chart, "chart");
		TextTitle title = chart.getTitle();
		if (title != null) {
			title.setFont(title.getFont().deriveFont(at));
		}

		int subtitleCount = chart.getSubtitleCount();
		for (int i = 0; i < subtitleCount; i++) {
			applyToTitle(chart.getSubtitle(i));
		}

		// now process the plot if there is one
		Plot plot = chart.getPlot();
		if (plot != null) {
			applyToPlot(plot);
		}
	}

	/**
	 * Applies the attributes of this theme to the specified title.
	 *
	 * @param title  the title.
	 */
	protected void applyToTitle(Title title) {
		if (title instanceof TextTitle) {
			TextTitle tt = (TextTitle) title;
			tt.setFont(tt.getFont().deriveFont(at));
		}
		else if (title instanceof LegendTitle) {
			LegendTitle lt = (LegendTitle) title;
			lt.setItemFont(lt.getItemFont().deriveFont(at));
			if (lt.getWrapper() != null) {
				applyToBlockContainer(lt.getWrapper());
			}
		}
		else if (title instanceof PaintScaleLegend) {
			PaintScaleLegend psl = (PaintScaleLegend) title;
			ValueAxis axis = psl.getAxis();
			if (axis != null) {
				applyToValueAxis(axis);
			}
		}
		else if (title instanceof CompositeTitle) {
			CompositeTitle ct = (CompositeTitle) title;
			BlockContainer bc = ct.getContainer();
			List blocks = bc.getBlocks();
			Iterator iterator = blocks.iterator();
			while (iterator.hasNext()) {
				Block b = (Block) iterator.next();
				if (b instanceof Title) {
					applyToTitle((Title) b);
				}
			}
		}
	}

	/**
	 * Applies the attributes of this theme to the specified container.
	 *
	 * @param bc  a block container ({@code null} not permitted).
	 */
	protected void applyToBlockContainer(BlockContainer bc) {
		Iterator iterator = bc.getBlocks().iterator();
		while (iterator.hasNext()) {
			Block b = (Block) iterator.next();
			applyToBlock(b);
		}
	}

	/**
	 * Applies the attributes of this theme to the specified block.
	 *
	 * @param b  the block.
	 */
	protected void applyToBlock(Block b) {
		if (b instanceof Title) {
			applyToTitle((Title) b);
		}
		else if (b instanceof LabelBlock) {
			LabelBlock lb = (LabelBlock) b;
			lb.setFont(lb.getFont().deriveFont(at));
		}
	}

	/**
	 * Applies the attributes of this theme to a plot.
	 *
	 * @param plot  the plot ({@code null}).
	 */
	protected void applyToPlot(Plot plot) {
		ParamChecks.nullNotPermitted(plot, "plot");

		// now handle specific plot types (and yes, I know this is some
		// really ugly code that has to be manually updated any time a new
		// plot type is added - I should have written something much cooler,
		// but I didn't and neither did anyone else).
		if (plot instanceof PiePlot) {
			applyToPiePlot((PiePlot) plot);
		}
		else if (plot instanceof MultiplePiePlot) {
			applyToMultiplePiePlot((MultiplePiePlot) plot);
		}
		else if (plot instanceof CategoryPlot) {
			applyToCategoryPlot((CategoryPlot) plot);
		}
		else if (plot instanceof XYPlot) {
			applyToXYPlot((XYPlot) plot);
		}
		else if (plot instanceof FastScatterPlot) {
			applyToFastScatterPlot((FastScatterPlot) plot);
		}
		else if (plot instanceof MeterPlot) {
			applyToMeterPlot((MeterPlot) plot);
		}
		else if (plot instanceof ThermometerPlot) {
			applyToThermometerPlot((ThermometerPlot) plot);
		}
		else if (plot instanceof SpiderWebPlot) {
			applyToSpiderWebPlot((SpiderWebPlot) plot);
		}
		else if (plot instanceof PolarPlot) {
			applyToPolarPlot((PolarPlot) plot);
		}
	}

	/**
	 * Applies the attributes of this theme to a {@link PiePlot} instance.
	 * This method also clears any set values for the section paint, outline
	 * etc, so that the theme's {@link DrawingSupplier} will be used.
	 *
	 * @param plot  the plot ({@code null} not permitted).
	 */
	protected void applyToPiePlot(PiePlot plot) {
		plot.setLabelFont(plot.getLabelFont().deriveFont(at));
	}

	/**
	 * Applies the attributes of this theme to a {@link MultiplePiePlot}.
	 *
	 * @param plot  the plot ({@code null} not permitted).
	 */
	protected void applyToMultiplePiePlot(MultiplePiePlot plot) {
		apply(plot.getPieChart());
	}

	/**
	 * Applies the attributes of this theme to a {@link CategoryPlot}.
	 *
	 * @param plot  the plot ({@code null} not permitted).
	 */
	protected void applyToCategoryPlot(CategoryPlot plot) {
		// process all domain axes
		int domainAxisCount = plot.getDomainAxisCount();
		for (int i = 0; i < domainAxisCount; i++) {
			CategoryAxis axis = plot.getDomainAxis(i);
			if (axis != null) {
				applyToCategoryAxis(axis);
			}
		}

		// process all range axes
		int rangeAxisCount = plot.getRangeAxisCount();
		for (int i = 0; i < rangeAxisCount; i++) {
			ValueAxis axis = plot.getRangeAxis(i);
			if (axis != null) {
				applyToValueAxis(axis);
			}
		}

		// process all renderers
		int rendererCount = plot.getRendererCount();
		for (int i = 0; i < rendererCount; i++) {
			CategoryItemRenderer r = plot.getRenderer(i);
			if (r != null) {
				applyToCategoryItemRenderer(r);
			}
		}

		if (plot instanceof CombinedDomainCategoryPlot) {
			CombinedDomainCategoryPlot cp = (CombinedDomainCategoryPlot) plot;
			Iterator iterator = cp.getSubplots().iterator();
			while (iterator.hasNext()) {
				CategoryPlot subplot = (CategoryPlot) iterator.next();
				if (subplot != null) {
					applyToPlot(subplot);
				}
			}
		}
		if (plot instanceof CombinedRangeCategoryPlot) {
			CombinedRangeCategoryPlot cp = (CombinedRangeCategoryPlot) plot;
			Iterator iterator = cp.getSubplots().iterator();
			while (iterator.hasNext()) {
				CategoryPlot subplot = (CategoryPlot) iterator.next();
				if (subplot != null) {
					applyToPlot(subplot);
				}
			}
		}
	}

	/**
	 * Applies the attributes of this theme to a {@link XYPlot}.
	 *
	 * @param plot  the plot ({@code null} not permitted).
	 */
	protected void applyToXYPlot(XYPlot plot) {
		// process all domain axes
		int domainAxisCount = plot.getDomainAxisCount();
		for (int i = 0; i < domainAxisCount; i++) {
			ValueAxis axis = plot.getDomainAxis(i);
			if (axis != null) {
				applyToValueAxis(axis);
			}
		}

		// process all range axes
		int rangeAxisCount = plot.getRangeAxisCount();
		for (int i = 0; i < rangeAxisCount; i++) {
			ValueAxis axis = plot.getRangeAxis(i);
			if (axis != null) {
				applyToValueAxis(axis);
			}
		}

		// process all renderers
		int rendererCount = plot.getRendererCount();
		for (int i = 0; i < rendererCount; i++) {
			XYItemRenderer r = plot.getRenderer(i);
			if (r != null) {
				applyToXYItemRenderer(r);
			}
		}

		// process all annotations
		Iterator iter = plot.getAnnotations().iterator();
		while (iter.hasNext()) {
			XYAnnotation a = (XYAnnotation) iter.next();
			applyToXYAnnotation(a);
		}

		if (plot instanceof CombinedDomainXYPlot) {
			CombinedDomainXYPlot cp = (CombinedDomainXYPlot) plot;
			Iterator iterator = cp.getSubplots().iterator();
			while (iterator.hasNext()) {
				XYPlot subplot = (XYPlot) iterator.next();
				if (subplot != null) {
					applyToPlot(subplot);
				}
			}
		}
		if (plot instanceof CombinedRangeXYPlot) {
			CombinedRangeXYPlot cp = (CombinedRangeXYPlot) plot;
			Iterator iterator = cp.getSubplots().iterator();
			while (iterator.hasNext()) {
				XYPlot subplot = (XYPlot) iterator.next();
				if (subplot != null) {
					applyToPlot(subplot);
				}
			}
		}
	}

	/**
	 * Applies the attributes of this theme to a {@link FastScatterPlot}.
	 * 
	 * @param plot  the plot ({@code null} not permitted).
	 */
	protected void applyToFastScatterPlot(FastScatterPlot plot) {
		ValueAxis xAxis = plot.getDomainAxis();
		if (xAxis != null) {
			applyToValueAxis(xAxis);
		}
		ValueAxis yAxis = plot.getRangeAxis();
		if (yAxis != null) {
			applyToValueAxis(yAxis);
		}

	}

	/**
	 * Applies the attributes of this theme to a {@link PolarPlot}.  This
	 * method is called from the {@link #applyToPlot(Plot)} method.
	 *
	 * @param plot  the plot ({@code null} not permitted).
	 */
	protected void applyToPolarPlot(PolarPlot plot) {
		plot.setAngleLabelFont(plot.getAngleLabelFont().deriveFont(at));
		ValueAxis axis = plot.getAxis();
		if (axis != null) {
			applyToValueAxis(axis);
		}
	}

	/**
	 * Applies the attributes of this theme to a {@link SpiderWebPlot}.
	 *
	 * @param plot  the plot ({@code null} not permitted).
	 */
	protected void applyToSpiderWebPlot(SpiderWebPlot plot) {
		plot.setLabelFont(plot.getLabelFont().deriveFont(at));
	}

	/**
	 * Applies the attributes of this theme to a {@link MeterPlot}.
	 *
	 * @param plot  the plot ({@code null} not permitted).
	 */
	protected void applyToMeterPlot(MeterPlot plot) {
		plot.setValueFont(plot.getValueFont().deriveFont(at));
		plot.setTickLabelFont(plot.getTickLabelFont().deriveFont(at));
	}

	/**
	 * Applies the attributes for this theme to a {@link ThermometerPlot}.
	 * This method is called from the {@link #applyToPlot(Plot)} method.
	 *
	 * @param plot  the plot.
	 */
	protected void applyToThermometerPlot(ThermometerPlot plot) {
		plot.setValueFont(plot.getValueFont().deriveFont(at));
		ValueAxis axis = plot.getRangeAxis();
		if (axis != null) {
			applyToValueAxis(axis);
		}
	}

	/**
	 * Applies the attributes for this theme to a {@link CategoryAxis}.
	 *
	 * @param axis  the axis ({@code null} not permitted).
	 */
	protected void applyToCategoryAxis(CategoryAxis axis) {
		axis.setLabelFont(axis.getLabelFont().deriveFont(at));
		axis.setTickLabelFont(axis.getTickLabelFont().deriveFont(at));
		if (axis instanceof SubCategoryAxis) {
			SubCategoryAxis sca = (SubCategoryAxis) axis;
			sca.setSubLabelFont(sca.getSubLabelFont().deriveFont(at));
		}
	}

	/**
	 * Applies the attributes for this theme to a {@link ValueAxis}.
	 *
	 * @param axis  the axis ({@code null} not permitted).
	 */
	protected void applyToValueAxis(ValueAxis axis) {
		axis.setLabelFont(axis.getLabelFont().deriveFont(at));
		axis.setTickLabelFont(axis.getTickLabelFont().deriveFont(at));
		if (axis instanceof SymbolAxis) {
			applyToSymbolAxis((SymbolAxis) axis);
		}
		if (axis instanceof PeriodAxis) {
			applyToPeriodAxis((PeriodAxis) axis);
		}
	}

	/**
	 * Applies the attributes for this theme to a {@link SymbolAxis}.
	 *
	 * @param axis  the axis ({@code null} not permitted).
	 */
	protected void applyToSymbolAxis(SymbolAxis axis) {
	}

	/**
	 * Applies the attributes for this theme to a {@link PeriodAxis}.
	 *
	 * @param axis  the axis ({@code null} not permitted).
	 */
	protected void applyToPeriodAxis(PeriodAxis axis) {
		PeriodAxisLabelInfo[] info = axis.getLabelInfo();
		for (int i = 0; i < info.length; i++) {
			PeriodAxisLabelInfo e = info[i];
			PeriodAxisLabelInfo n = new PeriodAxisLabelInfo(e.getPeriodClass(),
					e.getDateFormat(), e.getPadding(), e.getLabelFont().deriveFont(at),
					e.getLabelPaint(), e.getDrawDividers(),
					e.getDividerStroke(), e.getDividerPaint());
			info[i] = n;
		}
		axis.setLabelInfo(info);
	}


	/**
	 * Applies the settings of this theme to the specified renderer.
	 *
	 * @param renderer  the renderer ({@code null} not permitted).
	 */
	protected void applyToCategoryItemRenderer(CategoryItemRenderer renderer) {
		ParamChecks.nullNotPermitted(renderer, "renderer");

		renderer.setBaseItemLabelFont(renderer.getBaseItemLabelFont().deriveFont(at));
	}

	/**
	 * Applies the settings of this theme to the specified renderer.
	 *
	 * @param renderer  the renderer ({@code null} not permitted).
	 */
	protected void applyToXYItemRenderer(XYItemRenderer renderer) {
		ParamChecks.nullNotPermitted(renderer, "renderer");
		renderer.setBaseItemLabelFont(renderer.getBaseItemLabelFont().deriveFont(at));
	}

	/**
	 * Applies the settings of this theme to the specified annotation.
	 *
	 * @param annotation  the annotation.
	 */
	protected void applyToXYAnnotation(XYAnnotation annotation) {
		ParamChecks.nullNotPermitted(annotation, "annotation");
		if (annotation instanceof XYTextAnnotation) {
			XYTextAnnotation xyta = (XYTextAnnotation) annotation;
			xyta.setFont(xyta.getFont().deriveFont(at));
		}
	}
}
