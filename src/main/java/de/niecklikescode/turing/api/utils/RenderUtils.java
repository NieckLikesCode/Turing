package de.niecklikescode.turing.api.utils;

import java.awt.*;
import java.util.Random;

public class RenderUtils {

    public static int getColorByString(String string) {
        Random random = new Random(string.hashCode());
        return new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB();
    }

}
