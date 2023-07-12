package de.niecklikescode.turing.guis;

import de.niecklikescode.turing.api.gui.CustomGui;
import de.niecklikescode.turing.api.gui.components.implementation.RectElement;

import java.awt.*;

public class ClickGui extends CustomGui {

    private final RectElement panel = new RectElement(new Rectangle(20, 20, 200, 160), 0x18191B);

    public ClickGui() {
        addComponent(panel);
    }

}
