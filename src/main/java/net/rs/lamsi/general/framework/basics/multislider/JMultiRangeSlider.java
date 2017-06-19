package net.rs.lamsi.general.framework.basics.multislider;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import net.rs.lamsi.general.framework.listener.DelayedDocumentListener;
import net.rs.lamsi.general.heatmap.PaintScaleGenerator;

public class JMultiRangeSlider extends JPanel implements MouseMotionListener, MouseListener{

	// possible color markers
	// high hue to small hue (0, red)
	public static final Color[] MARKERS = new Color[]{Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN,Color.YELLOW, Color.ORANGE, Color.RED};
	// with steps in between
	public static final Color[] MARKERS_HALFSTEPS = new Color[]{Color.MAGENTA, new Color(127,0,255), Color.BLUE, new Color(0,127,255), Color.CYAN, 
			new Color(0,255,127), Color.GREEN, new Color(127,255,0), Color.YELLOW, Color.ORANGE, Color.RED};


	protected boolean showValues = false;
	protected boolean useHalfSteps = false;
	protected Color cStart, cEnd;
	// hue values
	protected float hStart, hEnd;
	protected boolean inverted = false;

	protected float[] hue;
	protected float[] position;
	private int selectedPicker = -1;
	
	private int pickerH = 2, pickerW=7;


	private ArrayList<DelayedDocumentListener> listeners;


	/**
	 * Create the panel.
	 */
	public JMultiRangeSlider(boolean showValues2, boolean useHalfSteps) {
		this.showValues  = showValues2;
		this.useHalfSteps = useHalfSteps;
		setLayout(new BorderLayout(0, 0));
		setMinimumSize(new Dimension(125, showValues? 10 : 40));
		setPreferredSize(new Dimension(2000, showValues? 10 : 40));
		setMaximumSize(new Dimension(2000, showValues? 10 : 40));
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		// scale
		JPanel panel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				if(cStart!=null) {
					// sizing
					int w = getWidth()-1;
					int h = getHeight();
					
					// clear
					g.clearRect(0, 0, w, h);
					
					int barH = getBarHeight();
					
					// color gradient fill
					for(int x=1; x<w-1; x++) {
						float p = (x-1.f)/(w-2.f);
						Color c = PaintScaleGenerator.interpolateWeighted(cStart, cEnd, p, hue, position, false);
						g.setColor(c);
						g.fillRect(x, pickerH, 1, barH);
					}

					// box
					g.setColor(Color.black);
					g.drawRect(0, pickerH, w, barH);

					// picker
					if(hue!=null) {
						for(int i=0; i<hue.length; i++) {
							// center position
							int x = 1 + Math.round((w-2)*position[i]) - pickerW/2;
							// fill color
							g.setColor(Color.getHSBColor(hue[i], 1, 1));
							g.fillRect(x, 0, pickerW, barH+2*pickerH);
							g.setColor(i==selectedPicker? Color.white : Color.black);
							g.drawRect(x, 0, pickerW, barH+2*pickerH);
						}
					}
				}
			}

		};
		add(panel, BorderLayout.CENTER);

	}

	/**
	 * bar height without 2*pickerH
	 * @return
	 */
	protected int getBarHeight() {
		return getHeight()-1-pickerH*2;
	}


	/**
	 * x of picker i
	 * @param i
	 * @return
	 */
	public int getX(int i) {
		return 1 + Math.round((getWidth()-2)*position[i]) - pickerW/2;
	}

	/**
	 * bounds of picker i
	 * @param i
	 * @return
	 */
	public Rectangle getBounds(int i) {
		int x = getX(i);
		int h = getHeight();
		int barH = h-4- (showValues? 26 : 0);
		return new Rectangle(x, 0, pickerW, barH+2*pickerH);
	}

	public static float getHue(Color color) {
		return Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null)[0];
	}

	/**
	 * the position of a hue value between two other hue values
	 * @param s
	 * @param e
	 * @param hue
	 * @return
	 */
	public static float getPosition(Color s, Color e, float hue) {
		float hs = getHue(s);
		float he = getHue(e);
		return getPosition(hs, he, hue);
	}
	/**
	 * the position of a hue value between two other hue values
	 * @param s
	 * @param e
	 * @param hue
	 * @return
	 */
	public static float getPosition(float hs, float he, float hue) {
		float min = Math.min(hs, he);
		float max = Math.max(hs, he);
		return (hue-min)/(max-min);
	}

	/**
	 * hue and positions are reset 
	 * try using setup if positions and hue values already exist
	 * @param cStart
	 * @param cEnd
	 */
	public void setColors(Color cStart, Color cEnd) {
		hue = null;
		position = null;
		
		this.cStart = cStart;
		this.cEnd = cEnd;
		// magenta is higher than red
		hStart = getHue(cStart);
		hEnd = getHue(cEnd);
		inverted = hStart<hEnd;

		// from start to end marker
		int startMarker = -1, endMarker = -1;

		// calculate color markers
		Color[] mark = useHalfSteps? MARKERS_HALFSTEPS : MARKERS;
		int size = mark.length;
		for(int i=0; i<size; i++) {
			if(inverted) {
				// from small to high hue values
				if(hStart>=getHue(mark[i]) && startMarker==-1) {
					startMarker = i;
				}
				if(hEnd<=getHue(mark[size-1-i]) && endMarker==-1) {
					endMarker = size-i;
				}
			}
			else {
				// from high to low (hEnd)
				if(hStart<=getHue(mark[size-1-i]) && startMarker ==-1) startMarker = size-i;
				if(hEnd>=getHue(mark[i]) && endMarker==-1) endMarker = i+1;
					
			}
		}

		
		if(startMarker==-1) startMarker = 0;
		if(endMarker!=-1 && Math.abs(startMarker-endMarker)>0) {
			// init hue values
			hue = new float[Math.abs(startMarker-endMarker)];
			position = new float[hue.length];
			for (int i = 0; i < hue.length; i++) {
				if(inverted) hue[i] = getHue(mark[startMarker-i]);
				else hue[i] = getHue(mark[startMarker+i]);
	
				position[i] = getPosition(hStart, hEnd, hue[i]);
				if(!inverted) position[i] = 1.f-position[i];
			}
			hue[hue.length-1] = 0; // lower red
		}
		selectedPicker = -1;
		revalidate();
		repaint();
	}
	

	public void setup(Color cStart, Color cEnd, float[] hue, float[] position) {
		if(hue==null || position==null)
			setColors(cStart, cEnd);
		this.hue = hue;
		this.position = position;
		this.cStart = cStart;
		this.cEnd = cEnd;
		selectedPicker = -1;
		revalidate();
		repaint();
	}
	

	private void setPositionOf(int i, float p) {
		float tmp = p;
		// keep between the next and previous
		if(p<0) p = 0;
		if(i-1>=0 && position[i-1]>p) p = position[i-1];
		if(p>1.f) p = 1.f;
		if(i+1<position.length && position[i+1]<p) p = position[i+1];
		System.out.println("SET "+p+" from "+position[i]+"  xxx was "+tmp);
		position[i] = p;
		
		
		fireChangeEvent();

		revalidate();
		repaint();
	}
	
	private void fireChangeEvent() {
		if(listeners!=null)
			for (DelayedDocumentListener l : listeners) {
				l.startAutoUpdater(null);
			}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton()==MouseEvent.BUTTON1 && selectedPicker==-1) { 
			int x = e.getX();
			int y = e.getY();
			// select 
			for(int i = 0; i<hue.length; i++) {
				if(getBounds(i).contains(x,y)) {
					selectedPicker = i;
					System.out.println("New selected picker "+i);
					break;
				}
			}
			
			if(selectedPicker!=-1) {
				revalidate();
				repaint();
			}
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton()==MouseEvent.BUTTON1 && selectedPicker!=-1) { 
			int x = e.getX();
			float p = (x-1.f)/(getWidth()-2.f);
			// set value
			setPositionOf(selectedPicker, p);
			
			System.out.println("RELEASED "+selectedPicker+" with "+p+" at width="+getWidth());
			// deselect
			selectedPicker = -1;
		}
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		if(selectedPicker!=-1) { 
			int x = e.getX();
			float p = (x-1.f)/(getWidth()-2.f);
			// set value
			setPositionOf(selectedPicker, p);
			
			System.out.println("DRAGGED "+selectedPicker+" with "+p+" at width="+getWidth());
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) {
	}
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * to register changes
	 * @param listener
	 */
	public void addDocumentListener(DelayedDocumentListener listener) {
		if(listeners==null)
			listeners = new ArrayList<DelayedDocumentListener>();
		listeners.add(listener);
	}/**
	 * stops all delayed documentlistener
	 */
	public void stopDelayedListener() {
		if(listeners!=null)
		for(DelayedDocumentListener dl : listeners)
			dl.stop();
	}
	/**
	 * sets the active state for all delayed listeners
	 * @param state
	 */
	public void setDelayedListenerActive(boolean state) {
		if(listeners!=null)
		for(DelayedDocumentListener dl : listeners)
			dl.setActive(state);
	}

	public float[] getHue() {
		return hue;
	}
	public float[] getPositions() {
		return position;
	}

}
