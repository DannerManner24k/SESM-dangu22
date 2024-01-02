/*
 * @(#)FloatingTextField.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.text;

import org.jhotdraw.draw.figure.TextHolderFigure;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import org.jhotdraw.draw.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.event.FigureListener;

/**
 * A <em>floating text field</em> that is used to edit a {@link TextHolderFigure}.
 * <p>
 * {@code FloatingTextField} requires a two step initialization:
 * In a first step the overlay is created and in a
 * second step it can be positioned.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p>
 * <em>Framework</em><br>
 * The text creation and editing tools and the {@code TextHolderFigure}
 * interface define together the contracts of a smaller framework inside of the
 * JHotDraw framework for structured drawing editors.<br>
 * Contract: {@link TextHolderFigure}, {@link org.jhotdraw.draw.tool.TextCreationTool},
 * {@link org.jhotdraw.draw.tool.TextAreaCreationTool},
 * {@link org.jhotdraw.draw.tool.TextEditingTool},
 * {@link org.jhotdraw.draw.tool.TextAreaEditingTool}, {@link FloatingTextField},
 * {@link FloatingTextArea}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id: FloatingTextField.java -1 $
 */

public class FloatingTextField {

    private TextHolderFigure editedFigure;
    private JTextField textField;
    private DrawingView view;
    private FigureListener figureHandler = new FigureAdapter() {
        @Override
        public void attributeChanged(FigureEvent e) {
            updateWidget();
        }
    };

    public FloatingTextField() {
        textField = new JTextField(20);
    }

    public void requestFocus() {
        textField.requestFocus();
    }

    /**
     * Creates the overlay for the given Container using a
     * specific font.
     */
    public void createOverlay(DrawingView view, TextHolderFigure figure) {
        if (view == null || figure == null) {
            throw new IllegalArgumentException("view and figure must not be null.");
        }
        view.getComponent().add(textField, 0);
        textField.setText(figure.getText());
        textField.setColumns(figure.getTextColumns());
        textField.selectAll();
        textField.setVisible(true);
        editedFigure = figure;
        editedFigure.addFigureListener(figureHandler);
        this.view = view;
        updateWidget();
    }

    public Insets getInsets() {
        return textField.getInsets();
    }

    /**
     * Adds an action listener
     */
    public void addActionListener(ActionListener listener) {
        textField.addActionListener(listener);
    }

    /**
     * Gets the text contents of the overlay.
     */
    public String getText() {
        return textField.getText();
    }

    /**
     * Gets the preferred size of the overlay.
     */
    public Dimension getPreferredSize(int cols) {
        textField.setColumns(cols);
        return textField.getPreferredSize();
    }

    /**
     * Removes the overlay.
     */
    public void endOverlay() {
        view.getComponent().requestFocus();
        if (textField != null) {
            textField.setVisible(false);
            view.getComponent().remove(textField);
            Rectangle bounds = textField.getBounds();
            view.getComponent().repaint(bounds.x, bounds.y, bounds.width, bounds.height);
        }
        if (editedFigure != null) {
            editedFigure.removeFigureListener(figureHandler);
            editedFigure = null;
        }
    }

    protected void updateWidget() {
        setTextFieldFont();
        setTextFieldColors();
        setTextFieldBounds();
    }


    protected void setTextFieldFont() {
        Font font = editedFigure.getFont();
        font = font.deriveFont(font.getStyle(), (float) (editedFigure.getFontSize() * view.getScaleFactor()));
        textField.setFont(font);
    }

    protected void setTextFieldColors() {
        textField.setForeground(editedFigure.getTextColor());
        textField.setBackground(editedFigure.getFillColor());
    }

    protected void setTextFieldBounds() {
        Rectangle2D.Double fDrawBounds = editedFigure.getBounds();
        Point2D.Double fDrawLoc = new Point2D.Double(fDrawBounds.getX(), fDrawBounds.getY());

        AffineTransform transform = editedFigure.get(TRANSFORM);
        if (transform != null) {
            transform.transform(fDrawLoc, fDrawLoc);
        }

        Point fViewLoc = view.drawingToView(fDrawLoc);
        Rectangle fViewBounds = view.drawingToView(fDrawBounds);
        fViewBounds.x = fViewLoc.x;
        fViewBounds.y = fViewLoc.y;

        Dimension tfDim = textField.getPreferredSize();
        Insets tfInsets = textField.getInsets();

        float fontBaseline = calculateFontBaseline(textField.getFont());
        double fBaseline = calculateFBaseline(editedFigure, view);

        textField.setBounds(
                fViewBounds.x - tfInsets.left,
                fViewBounds.y - tfInsets.top - (int) (fontBaseline - fBaseline),
                Math.max(fViewBounds.width + tfInsets.left + tfInsets.right, tfDim.width),
                Math.max(fViewBounds.height + tfInsets.top + tfInsets.bottom, tfDim.height)
        );
    }

    protected float calculateFontBaseline(final Font font) {
        Graphics graphics = textField.getGraphics();
        FontMetrics fontMetrics = graphics.getFontMetrics(font);
        return fontMetrics.getMaxAscent();
    }

    protected double calculateFBaseline(final TextHolderFigure editedFigure, final DrawingView view) {
        double baseline = editedFigure.getBaseline();
        double scaleFactor = view.getScaleFactor();
        return baseline * scaleFactor;
    }

}
