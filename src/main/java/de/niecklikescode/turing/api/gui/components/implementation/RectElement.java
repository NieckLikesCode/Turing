package de.niecklikescode.turing.api.gui.components.implementation;

import de.niecklikescode.turing.api.gui.components.Element;
import de.niecklikescode.turing.api.gui.components.Interactable;

import java.awt.*;

public class RectElement extends Element {

    public RectElement(Rectangle bounds, int color) {
        super(color, new Interactable() {
            @Override
            public boolean isOver(int mouseX, int mouseY, float partialTicks) {
                return mouseX > bounds.getMinX() && mouseX < bounds.getMaxX()
                        && mouseY > bounds.getMinY() && mouseY < bounds.getMaxY();
            }
        });
    }
}
