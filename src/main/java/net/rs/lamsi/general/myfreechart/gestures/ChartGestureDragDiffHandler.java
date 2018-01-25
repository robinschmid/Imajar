/*
 * Copyright 2006-2015 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MZmine 2; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package net.rs.lamsi.general.myfreechart.gestures;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.AxisEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.PlotOrientation;

import net.rs.lamsi.general.myfreechart.ChartLogics;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Button;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Entity;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Event;
import net.rs.lamsi.general.myfreechart.gestures.ChartGesture.Key;

/**
 * The {@link ChartGestureDragDiffHandler} consumes primary mouse events to generate
 * {@link ChartGestureDragDiffEvent}s. These events are then processed by one or multiple
 * {@link Consumer}s. Each Consumer has a specific {@link Key} filter. Key and Consumer array have
 * to be sorted accordingly.
 * 
 * @author Robin Schmid (robinschmid@uni-muenster.de)
 */
public class ChartGestureDragDiffHandler extends ChartGestureHandler {

	public enum Orientation {
		VERTICAL, HORIZONTAL;
	}

	protected Key[] key;
	protected Consumer<ChartGestureDragDiffEvent> dragDiffHandler[];
	// default orientation
	protected Orientation orient = Orientation.HORIZONTAL;
	protected boolean isActive = false;

	public ChartGestureDragDiffHandler(ChartGesture.Entity entity, Button button, Key[] key,
			Consumer<ChartGestureDragDiffEvent> dragDiffHandler[]) {
		this(entity, button, key, dragDiffHandler, null);
	}

	public ChartGestureDragDiffHandler(ChartGesture.Entity entity, Button button, Key[] key,
			Consumer<ChartGestureDragDiffEvent> dragDiffHandler[], Orientation defaultOrientation) {
		super(new ChartGesture(entity, new Event[] {Event.DRAGGED, Event.RELEASED, Event.PRESSED},
				button, Key.ALL));
		/**
		 * Handles PRESSED, DRAGGED, RELEASED Events Fires the correct DragDiffHandlers for the Key
		 * filter
		 */
		setConsumer(createConsumer());
		// super() finished
		this.key = key;
		this.dragDiffHandler = dragDiffHandler;
		this.orient = defaultOrientation;
	}

	/**
	 * use default orientation or orientation of axis
	 * 
	 * @param event
	 * @return
	 */
	public Orientation getOrientation(ChartGestureEvent event) {
		ChartEntity ce = event.getEntity();
		if (ce instanceof AxisEntity) {
			JFreeChart chart = event.getChartPanel().getChart();
			PlotOrientation orient = PlotOrientation.HORIZONTAL;
			if (chart.getXYPlot() != null)
				orient = chart.getXYPlot().getOrientation();
			else if (chart.getCategoryPlot() != null)
				orient = chart.getCategoryPlot().getOrientation();

			Entity entity = event.getGesture().getEntity();
			if ((entity.equals(Entity.DOMAIN_AXIS) && orient.equals(PlotOrientation.VERTICAL))
					|| (entity.equals(Entity.RANGE_AXIS) && orient.equals(PlotOrientation.HORIZONTAL)))
				return Orientation.HORIZONTAL;
			else
				return Orientation.VERTICAL;
		}
		return orient;
	}

	@Override
	public boolean filter(ChartGesture g) {
		boolean b =  getGesture().filter(g);
		// only listen to events (drag,release,...) if in key list
		b = b && Stream.of(key).anyMatch(k -> k.filter(g.getKey()));

		if(g.getEvent()[0].equals(Event.RELEASED)) {
			stop();
			return false;
		}

		return b;
	}

	public void stop() {
		if(chartPanel!=null && isActive)
			chartPanel.setMouseZoomable(wasMouseZoomable);
		last = null;
		isActive = false;
	}
	
	/**
	 * Handle PRESSED, DRAGGED, RELEASED events to generate drag diff events
	 * 
	 * @return
	 */
	// variables
	boolean wasMouseZoomable = false;
	private Point2D last = null, first = null;
	private ChartGestureEvent startEvent = null, lastEvent = null;
	private ChartPanel chartPanel = null;
	private Consumer<ChartGestureEvent> createConsumer() {
		return new Consumer<ChartGestureEvent>() {

			@Override
			public void accept(ChartGestureEvent event) {
				chartPanel = event.getChartPanel();
				JFreeChart chart = chartPanel.getChart();
				MouseEvent e = event.getMouseEvent();
				ValueAxis axis = event.getAxis();

				// released?
				if (event.checkEvent(Event.RELEASED)) {
					chartPanel.setMouseZoomable(wasMouseZoomable);
					last = null;
					isActive = false;
				} else if (event.checkEvent(Event.PRESSED)) {
					isActive = true;
					// get data space coordinates
					last = ChartLogics.mouseXYToPlotXY(chartPanel, e.getX(), e.getY());
					first = last;
					startEvent = event;
					lastEvent = event;
					if (last != null) {
						wasMouseZoomable = ChartLogics.isMouseZoomable(chartPanel);
						chartPanel.setMouseZoomable(false);
					}
				} else if (event.checkEvent(Event.DRAGGED)) {
					if (last != null) {
						// get data space coordinates
						Point2D released = ChartLogics.mouseXYToPlotXY(chartPanel, e.getX(), e.getY());
						if (released != null) {
							double offset = 0;
							double start = 0;
							// scroll x
							Orientation o = getOrientation(event);
							if(o!=null) {
								if (o.equals(Orientation.HORIZONTAL)) {
									offset = -(released.getX() - last.getX());
									start = first.getX();
								}
								// scroll y
								else {
									offset = -(released.getY() - last.getY());
									start = first.getY();
								}
							}

							// new dragdiff event
							ChartGestureDragDiffEvent dragEvent = new ChartGestureDragDiffEvent(startEvent,
									lastEvent, event, start, offset, orient);
							// scroll / zoom / do anything with this new event
							// choose handler by key filter
							for (int i = 0; i < dragDiffHandler.length; i++)
								if (key[i].filter(event.getMouseEvent()))
									dragDiffHandler[i].accept(dragEvent);
							// set last event
							lastEvent = event;
							// save updated last
							last = ChartLogics.mouseXYToPlotXY(chartPanel, e.getX(), e.getY());
						}
					}
				}
			}
		};
	}
}
