package de.niecklikescode.turing.api.gui.components;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Element {

    public Element(Interactable interactable) {
        this.interactable = interactable;
    }

    @Getter
    private final Interactable interactable;

    @Setter
    @Getter
    private boolean visible = true;

    @Getter
    private final List<Element> children = new ArrayList<>();

    public void render(int mouseX, int mouseY, float partialTicks) {
        if(visible) children.forEach(comp -> comp.render(mouseX, mouseY, partialTicks));
    }

}
