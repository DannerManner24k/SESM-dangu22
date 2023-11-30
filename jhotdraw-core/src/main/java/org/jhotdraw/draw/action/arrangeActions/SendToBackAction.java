/*
 * @(#)SendToBackAction.java
 *
 * Copyright (c) 2003-2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.action.arrangeActions;

import dk.sdu.mmmi.featuretracer.lib.FeatureEntryPoint;
import org.jhotdraw.draw.action.AbstractSelectedAction;
import org.jhotdraw.draw.figure.Figure;
import java.util.*;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * SendToBackAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SendToBackAction extends AbstractSelectedAction implements ArrangeService {
    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.sendToBack";

    public SendToBackAction(){}

    @Override
    public SendToBackAction createWithEditor(DrawingEditor editor) {
        SendToBackAction action = new SendToBackAction(editor);
        return action;
    }

    /**
     * Creates a new instance.
     */
    @FeatureEntryPoint(value = "sendToBackAction")
    public SendToBackAction(DrawingEditor editor) {
        super(editor);
        ResourceBundleUtil.getRResourceBundleUtilLabels().configureAction(this, ID);
        updateEnabledState();
    }

    @FeatureEntryPoint(value = "sendToBack_actionPreformed")
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        final DrawingView view = getView();
        final LinkedList<Figure> figures = new LinkedList<>(view.getSelectedFigures());
        sendToBack(view, figures);
        fireUndoableEditHappened(new AbstractUndoableEdit() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getPresentationName() {
                return ResourceBundleUtil.getRResourceBundleUtilLabels().getTextProperty(ID);
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                SendToBackAction.sendToBack(view, figures);
            }

            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                BringToFrontAction.bringToFront(view, figures);
            }
        });
    }

    public static void sendToBack(DrawingView view, Collection<Figure> figures) {
        Drawing drawing = view.getDrawing();
        for (Figure figure : drawing.sort(figures)) {
            drawing.sendToBack(figure);
        }
    }

    @Override
    public String getID() {
        return ID;
    }
}