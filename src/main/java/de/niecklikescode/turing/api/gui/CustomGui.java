package de.niecklikescode.turing.api.gui;

import de.niecklikescode.turing.api.gui.components.Element;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomGui extends GuiScreen {

    private final List<Element> components = new ArrayList<>();

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        // Render custom elements
        components
                .stream()
                .filter(Element::isVisible)
                .forEach(comp -> comp.render(mouseX, mouseY, partialTicks));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    protected void addComponent(Element component) {
        components.add(component);
    };

}
