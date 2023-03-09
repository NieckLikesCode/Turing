package de.niecklikescode.turing.api.gui.components;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Element {

    public Element(int color, Interactable interactable) {
        this.interactable = interactable;
        this.color = color;
    }

    @Getter
    private Interactable interactable;

    @Getter
    private int color;

    @Getter
    private List<Element> children = new ArrayList<>();

}
