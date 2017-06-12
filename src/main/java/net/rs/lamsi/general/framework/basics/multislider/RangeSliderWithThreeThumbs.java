package net.rs.lamsi.general.framework.basics.multislider;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;
import static javax.swing.plaf.basic.BasicSliderUI.NEGATIVE_SCROLL;
import static javax.swing.plaf.basic.BasicSliderUI.POSITIVE_SCROLL;

/**
 * An extension of JSlider to select a range of values using two thumb controls.
 * The thumb controls are used to select the lower and upper value of a range
 * with predetermined minimum and maximum values.
 *
 * <p>
 * Note that RangeSlider makes use of the default BoundedRangeModel, which
 * supports an inner range defined by a value and an extent. The upper value
 * returned by RangeSlider is simply the lower value plus the extent.</p>
 */
public class RangeSliderWithThreeThumbs extends JSlider {

    private int secondUpperValue;
    private int secondUpperExtent;
    private PropertyChangeSupport pChangeSup;

    /**
     * Constructs a RangeSlider with default minimum and maximum values of 0 and
     * 100.
     */
    public RangeSliderWithThreeThumbs() {
        pChangeSup = new PropertyChangeSupport(this);
        addSeconUpperPropertyChangeListener(new SecondUpperListener());

        initSlider();
        this.setValue(40);
        this.setUpperValue(60);
        this.setSecondUpperValue(80);

    }

    /**
     * Constructs a RangeSlider with the specified default minimum and maximum
     * values.
     */
    public RangeSliderWithThreeThumbs(int min, int max) {
        super(min, max);
        initSlider();
        this.setValue(40);
        this.setUpperValue(60);
        this.setSecondUpperValue(80);
    }

    /**
     * Initializes the slider by setting default properties.
     */
    private void initSlider() {
        setOrientation(HORIZONTAL);
    }

    /**
     * Overrides the superclass method to install the UI delegate to draw two
     * thumbs.
     */
    @Override
    public void updateUI() {
        setUI(new RangeSliderUI(this));
        // Update UI for slider labels.  This must be called after updating the
        // UI of the slider.  Refer to JSlider.updateUI().
        updateLabelUIs();
    }

    /**
     * Returns the lower value in the range.
     */
    @Override
    public int getValue() {
        return super.getValue();
    }

    /**
     * Sets the lower value in the range.
     */
    @Override
    public void setValue(int value) {
        int oldValue = getValue();
        if (oldValue == value) {
            return;
        }

        // Compute new value and extent to maintain upper value.
        int oldExtent = getExtent();
        int newValue = Math.min(Math.max(getMinimum(), value), oldValue + oldExtent);
        int newExtent = oldExtent + oldValue - newValue;

        // Set new value and extent, and fire a single change event.
        getModel().setRangeProperties(newValue, newExtent, getMinimum(),
                getMaximum(), getValueIsAdjusting());
    }

    /**
     * Returns the upper value in the range.
     */
    public int getUpperValue() {
        return getValue() + getExtent();
    }

    /**
     * Sets the upper value in the range.
     */
    public void setUpperValue(int value) {
        // Compute new extent.
        int lowerValue = getValue();
        int newExtent = Math.min(Math.max(0, value - lowerValue), getMaximum() - lowerValue);

        // Set extent to set upper value.
        setExtent(newExtent);
    }

    //Sets the second UpperValue value in the range.
    public void setSecondUpperValue(int value) {
        int oldValue = this.secondUpperValue;
        this.secondUpperValue = value;

        secondUpperExtent = getSecondUpperValue() - getUpperValue();

        setSecondUpperExtent(secondUpperExtent);
        pChangeSup.firePropertyChange("secondUpperValue", oldValue, value);
    }

    public void addSeconUpperPropertyChangeListener(PropertyChangeListener listener) {
        pChangeSup.addPropertyChangeListener(listener);
    }

    public int getSecondUpperValue() {
        return this.secondUpperValue;
    }

    //Sets the second UpperExtent value in the range.
    public void setSecondUpperExtent(int secondUpperExtent) {
        this.secondUpperExtent = secondUpperExtent;
    }

    public int getSecondUpperExtent() {
        return secondUpperExtent;
    }

    //Listener for secondUpperValue
    public class SecondUpperListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("secondUpperValue")) {
                setSecondUpperValue((int) evt.getNewValue());
            }
        }
    }

    /**
     * UI delegate for the RangeSlider component. RangeSliderUI paints two
     * thumbs, one for the lower value and one for the upper value.
     */
    public class RangeSliderUI extends BasicSliderUI {

        /**
         * Color of selected range.
         */
        private Color rangeColor = Color.BLUE;

        /**
         * Color of selected range Upper Range.
         */
        private Color rangeColor2 = Color.GREEN.darker();

        //Color of selected range second Upper Range.
        private Color rangeColor3 = Color.GREEN.darker();

        /**
         * Location and size of thumb for upper value.
         */
        private Rectangle upperThumbRect;

        //Location and size of thumb for second upper value.
        private Rectangle secondUpperThumbRect;

        /**
         * Indicator that determines whether upper thumb is selected.
         */
        private boolean upperThumbSelected;

        //Indicator that determines whether second upper thumb is selected.
        private boolean secondUpperThumbSelected;

        /**
         * Indicator that determines whether lower thumb is being dragged.
         */
        private transient boolean lowerDragging;
        /**
         * Indicator that determines whether upper thumb is being dragged.
         */
        private transient boolean upperDragging;

        //Indicator that determines whether second upper thumb is being dragged.
        private transient boolean secondUpperDragging;

        /**
         * Constructs a RangeSliderUI for the specified slider component.
         *
         * @param b RangeSlider
         */
        public RangeSliderUI(RangeSliderWithThreeThumbs b) {
            super(b);
        }

        /**
         * Installs this UI delegate on the specified component.
         */
        @Override
        public void installUI(JComponent c) {
            upperThumbRect = new Rectangle();
            secondUpperThumbRect = new Rectangle();
            super.installUI(c);
        }

        /**
         * Creates a listener to handle track events in the specified slider.
         */
        @Override
        protected BasicSliderUI.TrackListener createTrackListener(JSlider slider) {
            return new RangeTrackListener();
        }

        /**
         * Creates a listener to handle change events in the specified slider.
         */
        @Override
        protected ChangeListener createChangeListener(JSlider slider) {
            return new ChangeHandler();
        }

        /**
         * Updates the dimensions for both thumbs.
         */
        @Override
        protected void calculateThumbSize() {
            // Call superclass method for lower thumb size.
            super.calculateThumbSize();

            // Set upper thumb size.
            upperThumbRect.setSize(thumbRect.width, thumbRect.height);
            secondUpperThumbRect.setSize(thumbRect.width, thumbRect.height);
        }

        /**
         * Updates the locations for both thumbs.
         */
        @Override
        protected void calculateThumbLocation() {
            // Call superclass method for lower thumb location.
            super.calculateThumbLocation();

            // Adjust upper value to snap to ticks if necessary.
            if (slider.getSnapToTicks()) {
                // SnapToTicks 
                //do nothing!
            }

            // Calculate upper thumb location. The thumb is centered over its
            // value on the track.
            if (slider.getOrientation() == JSlider.HORIZONTAL) {
                int upperPosition = xPositionForValue(slider.getValue() + slider.getExtent());
                upperThumbRect.x = upperPosition - (upperThumbRect.width / 2);
                upperThumbRect.y = trackRect.y;

                int secondUpperPosition = xPositionForValue(getUpperValue() + getSecondUpperExtent());
                secondUpperThumbRect.x = secondUpperPosition - (secondUpperThumbRect.width / 2);
                secondUpperThumbRect.y = trackRect.y;

            } else {
                int upperPosition = yPositionForValue(slider.getValue() + slider.getExtent());
                upperThumbRect.x = trackRect.x;
                upperThumbRect.y = upperPosition - (upperThumbRect.height / 2);

                int secondUpperPosition = yPositionForValue(getUpperValue() + getSecondUpperExtent());
                secondUpperThumbRect.x = trackRect.x;
                secondUpperThumbRect.y = secondUpperPosition - (secondUpperThumbRect.height / 2);
            }
        }

        /**
         * Returns the size of a thumb.
         */
        @Override
        protected Dimension getThumbSize() {
            return new Dimension(12, 12);
        }

        /**
         * Paints the slider. The selected thumb is always painted on top of the
         * other thumb.
         */
        @Override
        public void paint(Graphics g, JComponent c) {
            super.paint(g, c);

            Rectangle clipRect = g.getClipBounds();
            if (upperThumbSelected) {
                // Paint lower thumb first, then upper thumb.
                if (clipRect.intersects(thumbRect)) {
                    paintLowerThumb(g);
                }
                if (clipRect.intersects(secondUpperThumbRect)) {
                    paintSecondUpperThumb(g);
                }
                if (clipRect.intersects(upperThumbRect)) {
                    paintUpperThumb(g);
                }

            } else if (secondUpperThumbSelected) {
                // Paint upper thumb first, then second upper thumb.
                if (clipRect.intersects(upperThumbRect)) {
                    paintUpperThumb(g);
                }
                if (clipRect.intersects(thumbRect)) {
                    paintLowerThumb(g);
                }

                if (clipRect.intersects(secondUpperThumbRect)) {
                    paintSecondUpperThumb(g);
                }

            } else {
                // Paint second upper thumb first,upper thumb second, then lower thumb.
                if (clipRect.intersects(secondUpperThumbRect)) {
                    paintSecondUpperThumb(g);
                }
                if (clipRect.intersects(upperThumbRect)) {
                    paintUpperThumb(g);
                }
                if (clipRect.intersects(thumbRect)) {
                    paintLowerThumb(g);
                }
            }
        }

        /**
         * Paints the track.
         */
        @Override
        public void paintTrack(Graphics g) {
            // Draw track.
            super.paintTrack(g);

            Rectangle trackBounds = trackRect;

            if (slider.getOrientation() == JSlider.HORIZONTAL) {
                // Determine position of selected range by moving from the middle
                // of one thumb to the other.
                int lowerX = thumbRect.x + (thumbRect.width / 2);
                int upperX = upperThumbRect.x + (upperThumbRect.width / 2);
                //int secondUpperX = secondUpperThumbRect.x + (secondUpperThumbRect.width / 2);

                // Determine track position.
                int cy = (trackBounds.height / 2) - 2;

                // Save color and shift position.
                Color oldColor = g.getColor();
                g.translate(trackBounds.x, trackBounds.y + cy);

                g.setColor(Color.GRAY);
                for (int y = 0; y <= 3; y++) {
                    g.drawLine(thumbRect.x - trackBounds.x, y, 0, y);
                }

                g.setColor(Color.GREEN.darker());
                for (int y = 0; y <= 3; y++) {
                    g.drawLine(upperThumbRect.x - trackBounds.x, y, (int) trackBounds.getWidth(), y);
                }

                g.setColor(Color.RED);
                for (int y = 0; y <= 3; y++) {
                    g.drawLine(secondUpperThumbRect.x - trackBounds.x, y, (int) trackBounds.getWidth(), y);
                }
                // Draw selected range.
                g.setColor(rangeColor);
                for (int y = 0; y <= 3; y++) {
                    g.drawLine(lowerX - trackBounds.x, y, upperX - trackBounds.x, y);
                }

                //for (int y = 0; y <= 3; y++) {
                //    g.drawLine(secondUpperX - upperX, y, secondUpperX - upperX, y);
                //}
                // Restore position and color.
                g.translate(-trackBounds.x, -(trackBounds.y + cy));
                g.setColor(oldColor);

            } else {
                // Determine position of selected range by moving from the middle
                // of one thumb to the other.
                int lowerY = thumbRect.x + (thumbRect.width / 2);
                int upperY = upperThumbRect.x + (upperThumbRect.width / 2);
                int secondUpperY = secondUpperThumbRect.x + (secondUpperThumbRect.width / 2);
                // Determine track position.
                int cx = (trackBounds.width / 2) - 2;

                // Save color and shift position.
                Color oldColor = g.getColor();
                g.translate(trackBounds.x + cx, trackBounds.y);

                // Draw selected range.
                g.setColor(rangeColor);
                for (int x = 0; x <= 3; x++) {
                    g.drawLine(x, lowerY - trackBounds.y, x, upperY - trackBounds.y);
                }

                for (int y = 0; y <= 3; y++) {
                    g.drawLine(secondUpperY - upperY, y, secondUpperY - upperY, y);
                }

                // Restore position and color.
                g.translate(-(trackBounds.x + cx), -trackBounds.y);
                g.setColor(oldColor);
            }
        }

        /**
         * Overrides superclass method to do nothing. Thumb painting is handled
         * within the <code>paint()</code> method.
         */
        @Override
        public void paintThumb(Graphics g) {
            // Do nothing.
        }

        /**
         * Paints the thumb for the lower value using the specified graphics
         * object.
         */
        private void paintLowerThumb(Graphics g) {
            Rectangle knobBounds = thumbRect;
            int w = knobBounds.width;
            int h = knobBounds.height;

            // Create graphics copy.
            Graphics2D g2d = (Graphics2D) g.create();

            // Create default thumb shape.
            Shape thumbShape = createThumbShape(w - 1, h - 1);

            // Draw thumb.
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.translate(knobBounds.x, knobBounds.y);

            g2d.setColor(Color.CYAN);
            g2d.fill(thumbShape);

            g2d.setColor(Color.BLUE);
            g2d.draw(thumbShape);

            // Dispose graphics.
            g2d.dispose();
        }

        /**
         * Paints the thumb for the upper value using the specified graphics
         * object.
         */
        private void paintUpperThumb(Graphics g) {
            Rectangle knobBounds = upperThumbRect;
            int w = knobBounds.width;
            int h = knobBounds.height;

            // Create graphics copy.
            Graphics2D g2d = (Graphics2D) g.create();

            // Create default thumb shape.
            Shape thumbShape = createThumbShape(w - 1, h - 1);

            // Draw thumb.
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.translate(knobBounds.x, knobBounds.y);

            g2d.setColor(Color.PINK);
            g2d.fill(thumbShape);

            g2d.setColor(Color.RED);
            g2d.draw(thumbShape);

            // Dispose graphics.
            g2d.dispose();
        }

        //Paint secondUpperThumb
        private void paintSecondUpperThumb(Graphics g) {
            Rectangle knobBounds = secondUpperThumbRect;
            int w = knobBounds.width;
            int h = knobBounds.height;

            // Create graphics copy.
            Graphics2D g2d = (Graphics2D) g.create();

            // Create default thumb shape.
            Shape thumbShape = createThumbShape(w - 1, h - 1);

            // Draw thumb.
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.translate(knobBounds.x, knobBounds.y);

            g2d.setColor(Color.YELLOW);
            g2d.fill(thumbShape);

            g2d.setColor(Color.RED);
            g2d.draw(thumbShape);

            // Dispose graphics.
            g2d.dispose();
        }

        /**
         * Returns a Shape representing a thumb.
         */
        private Shape createThumbShape(int width, int height) {
            // Use circular shape.
            Ellipse2D shape = new Ellipse2D.Double(0, 0, width, height);
            return shape;
        }

        /**
         * Sets the location of the upper thumb, and repaints the slider. This
         * is called when the upper thumb is dragged to repaint the slider. The
         * <code>setThumbLocation()</code> method performs the same task for the
         * lower thumb.
         */
        private void setUpperThumbLocation(int x, int y) {
            Rectangle upperUnionRect = new Rectangle();
            upperUnionRect.setBounds(upperThumbRect);

            upperThumbRect.setLocation(x, y);

            SwingUtilities.computeUnion(upperThumbRect.x, upperThumbRect.y, upperThumbRect.width, upperThumbRect.height,
                    upperUnionRect);
            slider.repaint(upperUnionRect.x, upperUnionRect.y, upperUnionRect.width, upperUnionRect.height);
        }

        private void setSecondUpperThumbLocation(int x, int y) {
            Rectangle upperUnionRect = new Rectangle();
            upperUnionRect.setBounds(secondUpperThumbRect);

            secondUpperThumbRect.setLocation(x, y);

            SwingUtilities.computeUnion(secondUpperThumbRect.x, secondUpperThumbRect.y, secondUpperThumbRect.width, secondUpperThumbRect.height,
                    upperUnionRect);
            slider.repaint(upperUnionRect.x, upperUnionRect.y, upperUnionRect.width, upperUnionRect.height);
        }

        /**
         * Moves the selected thumb in the specified direction by a block
         * increment. This method is called when the user presses the Page Up or
         * Down keys.
         */
        public void scrollByBlock(int direction) {
            synchronized (slider) {
                int blockIncrement = (slider.getMaximum() - slider.getMinimum()) / 10;
                if (blockIncrement <= 0 && slider.getMaximum() > slider.getMinimum()) {
                    blockIncrement = 1;
                }
                int delta = blockIncrement * ((direction > 0) ? POSITIVE_SCROLL : NEGATIVE_SCROLL);

                if (upperThumbSelected) {
                    int oldValue = ((RangeSliderWithThreeThumbs) slider).getUpperValue();
                    ((RangeSliderWithThreeThumbs) slider).setUpperValue(oldValue + delta);

                } else if (secondUpperThumbSelected) {
                    int oldValue = ((RangeSliderWithThreeThumbs) slider).getSecondUpperValue();
                    ((RangeSliderWithThreeThumbs) slider).setSecondUpperValue(oldValue + delta);
                } else {
                    int oldValue = slider.getValue();
                    slider.setValue(oldValue + delta);
                }
            }
        }

        /**
         * Moves the selected thumb in the specified direction by a unit
         * increment. This method is called when the user presses one of the
         * arrow keys.
         */
        public void scrollByUnit(int direction) {
            synchronized (slider) {
                int delta = 1 * ((direction > 0) ? POSITIVE_SCROLL : NEGATIVE_SCROLL);

                if (upperThumbSelected) {
                    int oldValue = ((RangeSliderWithThreeThumbs) slider).getUpperValue();
                    ((RangeSliderWithThreeThumbs) slider).setUpperValue(oldValue + delta);
                } else if (secondUpperThumbSelected) {
                    int oldValue = ((RangeSliderWithThreeThumbs) slider).getSecondUpperValue();
                    ((RangeSliderWithThreeThumbs) slider).setSecondUpperValue(oldValue + delta);
                } else {
                    int oldValue = slider.getValue();
                    slider.setValue(oldValue + delta);
                }
            }
        }

        /**
         * Listener to handle model change events. This calculates the thumb
         * locations and repaints the slider if the value change is not caused
         * by dragging a thumb.
         */
        public class ChangeHandler implements ChangeListener {

            public void stateChanged(ChangeEvent arg0) {
                if (!lowerDragging && !upperDragging && !secondUpperDragging) {
                    calculateThumbLocation();
                    slider.repaint();
                }
            }
        }

        /**
         * Listener to handle mouse movements in the slider track.
         */
        public class RangeTrackListener extends BasicSliderUI.TrackListener {

            @Override
            public void mouseClicked(MouseEvent e) {
                jumpToClickedPosition(slider, e);
                calculateThumbLocation();

            }

            public void jumpToClickedPosition(JSlider jSlider, MouseEvent evt) {
                BasicSliderUI ui = (BasicSliderUI) jSlider.getUI();
                int pos = ui.valueForXPosition(evt.getPoint().x);

                int minValue = slider.getValue();
                int maxValue = slider.getValue() + slider.getExtent();
                int getSecondUpperValue = getSecondUpperValue();

                int lowerThumbDistance = Math.abs(minValue - pos);
                int upperThumbDistance = Math.abs(maxValue - pos);
                int secondUpperThumbDistance = Math.abs(getSecondUpperValue - pos);

                int min = Collections.min(Arrays.asList(lowerThumbDistance, upperThumbDistance, secondUpperThumbDistance));

                if (min == lowerThumbDistance) {
                    moveLowerThumb();
                } else if (min == upperThumbDistance) {
                    moveUpperThumb();
                } else if (min == secondUpperThumbDistance) {
                    moveSecondUpperThumb();
                }

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!slider.isEnabled()) {
                    return;
                }

                currentMouseX = e.getX();
                currentMouseY = e.getY();

                if (slider.isRequestFocusEnabled()) {
                    slider.requestFocus();
                }

                // Determine which thumb is pressed. If the upper thumb is
                // selected (last one dragged), then check its position first;
                // otherwise check the position of the lower thumb first.
                boolean lowerPressed = false;
                boolean upperPressed = false;
                boolean secondUpperPressed = false;
                if (secondUpperThumbSelected || slider.getMinimum() == getSecondUpperValue()) {
                    if (secondUpperThumbRect.contains(currentMouseX, currentMouseY)) {
                        secondUpperPressed = true;
                    } else if (upperThumbRect.contains(currentMouseX, currentMouseY)) {
                        upperPressed = true;
                    } else if (thumbRect.contains(currentMouseX, currentMouseY)) {
                        lowerPressed = true;
                    }
                } else if (upperThumbSelected || slider.getMinimum() == slider.getValue()) {
                    if (upperThumbRect.contains(currentMouseX, currentMouseY)) {
                        upperPressed = true;
                    } else if (thumbRect.contains(currentMouseX, currentMouseY)) {
                        lowerPressed = true;
                    } else if (secondUpperThumbRect.contains(currentMouseX, currentMouseY)) {
                        secondUpperPressed = true;
                    }
                } else {
                    if (thumbRect.contains(currentMouseX, currentMouseY)) {
                        lowerPressed = true;
                    } else if (upperThumbRect.contains(currentMouseX, currentMouseY)) {
                        upperPressed = true;
                    } else if (secondUpperThumbRect.contains(currentMouseX, currentMouseY)) {
                        secondUpperPressed = true;
                    }
                }

                // Handle lower thumb pressed.
                if (lowerPressed) {
                    switch (slider.getOrientation()) {
                        case JSlider.VERTICAL:
                            offset = currentMouseY - thumbRect.y;
                            break;
                        case JSlider.HORIZONTAL:
                            offset = currentMouseX - thumbRect.x;
                            break;
                    }
                    upperThumbSelected = false;
                    secondUpperThumbSelected = false;
                    lowerDragging = true;
                    return;
                }
                lowerDragging = false;

                // Handle upper thumb pressed.
                if (upperPressed) {
                    switch (slider.getOrientation()) {
                        case JSlider.VERTICAL:
                            offset = currentMouseY - upperThumbRect.y;
                            break;
                        case JSlider.HORIZONTAL:
                            offset = currentMouseX - upperThumbRect.x;
                            break;
                    }
                    secondUpperThumbSelected = false;
                    upperThumbSelected = true;
                    upperDragging = true;
                    return;
                }
                upperDragging = false;

                // Handle secondUpper thumb pressed.
                if (secondUpperPressed) {
                    switch (slider.getOrientation()) {
                        case JSlider.VERTICAL:
                            offset = currentMouseY - secondUpperThumbRect.y;
                            break;
                        case JSlider.HORIZONTAL:
                            offset = currentMouseX - secondUpperThumbRect.x;
                            break;
                    }
                    secondUpperThumbSelected = true;
                    upperThumbSelected = false;
                    secondUpperDragging = true;
                    return;
                }
                secondUpperDragging = false;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lowerDragging = false;
                upperDragging = false;
                secondUpperDragging = false;
                slider.setValueIsAdjusting(false);
                super.mouseReleased(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!slider.isEnabled()) {
                    return;
                }

                currentMouseX = e.getX();
                currentMouseY = e.getY();

                if (lowerDragging) {
                    slider.setValueIsAdjusting(true);
                    moveLowerThumb();
                } else if (upperDragging) {
                    slider.setValueIsAdjusting(true);
                    moveUpperThumb();
                } else if (secondUpperDragging) {
                    slider.setValueIsAdjusting(true);
                    moveSecondUpperThumb();
                }
            }

            @Override
            public boolean shouldScroll(int direction) {
                return false;
            }

            /**
             * Moves the location of the lower thumb, and sets its corresponding
             * value in the slider.
             */
            private void moveLowerThumb() {
                int thumbMiddle = 0;

                switch (slider.getOrientation()) {
                    case JSlider.VERTICAL:
                        int halfThumbHeight = thumbRect.height / 2;
                        int thumbTop = currentMouseY - offset;
                        int trackTop = trackRect.y;
                        int trackBottom = trackRect.y + (trackRect.height - 1);
                        int vMax = yPositionForValue(slider.getValue() + slider.getExtent());

                        // Apply bounds to thumb position.
                        if (drawInverted()) {
                            trackBottom = vMax;
                        } else {
                            trackTop = vMax;
                        }
                        thumbTop = Math.max(thumbTop, trackTop - halfThumbHeight);
                        thumbTop = Math.min(thumbTop, trackBottom - halfThumbHeight);

                        setThumbLocation(thumbRect.x, thumbTop);

                        // Update slider value.
                        thumbMiddle = thumbTop + halfThumbHeight;
                        slider.setValue(valueForYPosition(thumbMiddle));
                        break;

                    case JSlider.HORIZONTAL:
                        int halfThumbWidth = thumbRect.width / 2;
                        int thumbLeft = currentMouseX - offset;
                        int trackLeft = trackRect.x;
                        int trackRight = trackRect.x + (trackRect.width - 1);
                        int hMax = xPositionForValue(slider.getValue() + slider.getExtent());

                        // Apply bounds to thumb position.
                        if (drawInverted()) {
                            trackLeft = hMax;
                        } else {
                            trackRight = hMax;
                        }
                        thumbLeft = Math.max(thumbLeft, trackLeft - halfThumbWidth);
                        thumbLeft = Math.min(thumbLeft, trackRight - halfThumbWidth);

                        setThumbLocation(thumbLeft, thumbRect.y);

                        // Update slider value.
                        thumbMiddle = thumbLeft + halfThumbWidth;
                        slider.setValue(valueForXPosition(thumbMiddle));
                        break;

                    default:
                        return;
                }
            }

            /**
             * Moves the location of the upper thumb, and sets its corresponding
             * value in the slider.
             */
            private void moveUpperThumb() {
                int thumbMiddle = 0;

                switch (slider.getOrientation()) {
                    case JSlider.VERTICAL:
                        int halfThumbHeight = thumbRect.height / 2;
                        int thumbTop = currentMouseY - offset;
                        int trackTop = trackRect.y;
                        int trackBottom = trackRect.y + (trackRect.height - 1);
                        int vMin = yPositionForValue(slider.getValue());

                        // Apply bounds to thumb position.
                        if (drawInverted()) {
                            trackTop = vMin;
                        } else {
                            trackBottom = vMin;
                        }
                        thumbTop = Math.max(thumbTop, trackTop - halfThumbHeight);
                        thumbTop = Math.min(thumbTop, trackBottom - halfThumbHeight);

                        setUpperThumbLocation(thumbRect.x, thumbTop);

                        // Update slider extent.
                        thumbMiddle = thumbTop + halfThumbHeight;
                        slider.setExtent(valueForYPosition(thumbMiddle) - slider.getValue());
                        break;

                    case JSlider.HORIZONTAL:
                        int halfThumbWidth = thumbRect.width / 2;
                        int thumbLeft = currentMouseX - offset;
                        int trackLeft = trackRect.x;
                        int trackRight = trackRect.x + (trackRect.width - 1);
                        int hMin = xPositionForValue(slider.getValue());
                        int hMax = xPositionForValue(getUpperValue() + getSecondUpperExtent());

                        // Apply bounds to thumb position.
                        if (drawInverted()) {
                            trackRight = hMin;
                            trackLeft = hMax;
                        } else {
                            trackLeft = hMin;
                            trackRight = hMax;
                        }
                        thumbLeft = Math.max(thumbLeft, trackLeft - halfThumbWidth);
                        thumbLeft = Math.min(thumbLeft, trackRight - halfThumbWidth);

                        setUpperThumbLocation(thumbLeft, thumbRect.y);

                        // Update slider extent.
                        thumbMiddle = thumbLeft + halfThumbWidth;
                        slider.setExtent(valueForXPosition(thumbMiddle) - slider.getValue());
//                        if (getUpperValue() + getSecondUpperExtent() > getMaximum()) {
//                            setSecondUpperExtent(getSecondUpperValue() - valueForXPosition(thumbMiddle));
//                            setSecondUpperValue(getMaximum());
//                        } else {
                            setSecondUpperExtent(getSecondUpperValue() - valueForXPosition(thumbMiddle));
                            setSecondUpperValue(getUpperValue() + getSecondUpperExtent());
//                        }
                        break;

                    default:
                        return;
                }
            }

            private void moveSecondUpperThumb() {
                int thumbMiddle = 0;

                switch (slider.getOrientation()) {
                    case JSlider.VERTICAL:
                        int halfThumbHeight = thumbRect.height / 2;
                        int thumbTop = currentMouseY - offset;
                        int trackTop = trackRect.y;
                        int trackBottom = trackRect.y + (trackRect.height - 1);
                        int vMin = yPositionForValue(slider.getValue() + slider.getExtent());

                        // Apply bounds to thumb position.
                        if (drawInverted()) {
                            trackTop = vMin;
                        } else {
                            trackBottom = vMin;
                        }
                        thumbTop = Math.max(thumbTop, trackTop - halfThumbHeight);
                        thumbTop = Math.min(thumbTop, trackBottom - halfThumbHeight);

                        setSecondUpperThumbLocation(thumbRect.x, thumbTop);

                        // Update slider extent.
                        thumbMiddle = thumbTop + halfThumbHeight;
                        setSecondUpperExtent(valueForXPosition(thumbMiddle) - getUpperValue());
                        setSecondUpperValue(valueForXPosition(thumbMiddle));
                        break;

                    case JSlider.HORIZONTAL:
                        int halfThumbWidth = thumbRect.width / 2;
                        int thumbLeft = currentMouseX - offset;
                        int trackLeft = trackRect.x;
                        int trackRight = trackRect.x + (trackRect.width - 1);
                        int hMin = xPositionForValue(slider.getValue() + slider.getExtent());

                        // Apply bounds to thumb position.
                        if (drawInverted()) {
                            trackRight = hMin;
                        } else {
                            trackLeft = hMin;
                        }
                        thumbLeft = Math.max(thumbLeft, trackLeft - halfThumbWidth);
                        thumbLeft = Math.min(thumbLeft, trackRight - halfThumbWidth);

                        setSecondUpperThumbLocation(thumbLeft, thumbRect.y);

                        // Update slider extent.
                        thumbMiddle = thumbLeft + halfThumbWidth;
                        setSecondUpperExtent(valueForXPosition(thumbMiddle) - getUpperValue());
                        setSecondUpperValue(valueForXPosition(thumbMiddle));
                        break;

                    default:
                        return;
                }
            }
        }
    }
}