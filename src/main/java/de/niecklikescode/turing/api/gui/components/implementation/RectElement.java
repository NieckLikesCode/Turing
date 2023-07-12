package de.niecklikescode.turing.api.gui.components.implementation;

import de.niecklikescode.turing.api.gui.components.Element;
import de.niecklikescode.turing.api.gui.components.Interactable;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public class RectElement extends Element {

    @Setter
    @Getter
    private int color;

    public RectElement(Rectangle bounds, int color) {
        super((mouseX, mouseY, partialTicks) -> mouseX > bounds.getMinX() && mouseX < bounds.getMaxX()
                && mouseY > bounds.getMinY() && mouseY < bounds.getMaxY());
    }
}
