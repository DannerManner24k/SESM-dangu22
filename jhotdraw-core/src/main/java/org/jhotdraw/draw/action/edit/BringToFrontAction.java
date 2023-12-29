/*
 * @(#)BringToFrontAction.java
 *
 * Copyright (c) 2003-2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.action.edit;

import org.jhotdraw.draw.action.AbstractSelectedAction;
import org.jhotdraw.draw.figure.Figure;
import java.util.*;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.util.ResourceBundleUtil;
import dk.sdu.mmmi.featuretracer.lib.FeatureEntryPoint;

/**
 * ToFrontAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */

public class BringToFrontAction extends AbstractSelectedAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.bringToFront";

    public BringToFrontAction(DrawingEditor editor) {
        super(editor);
        ResourceBundleUtil
                .getBundle("org.jhotdraw.draw.Labels")
                .configureAction(this, ID);
        super.updateEnabledState();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        final DrawingView view = getView();
        final LinkedList<Figure> figures = new LinkedList<>(view.getSelectedFigures());
        bringToFront(view, figures);
        super.fireUndoableEditHappened(this.getUndoableEdit(view, figures));
    }

    @FeatureEntryPoint("bringToFront feature")
    public static void bringToFront(DrawingView view, Collection<Figure> figures) {
        Drawing drawing = view.getDrawing();
        for (Figure figure : drawing.sort(figures)) {
            drawing.bringToFront(figure);
        }
    }

    private AbstractUndoableEdit getUndoableEdit(DrawingView view, Collection<Figure> figures) {
        ResourceBundleUtil labels
                = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        return new AbstractUndoableEdit() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getPresentationName() {
                return labels.getTextProperty(ID);
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                BringToFrontAction.bringToFront(view, figures);
            }

            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                SendToBackAction.sendToBack(view, figures);
            }
        };
    }
}