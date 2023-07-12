package de.niecklikescode.turing.modules;

import de.niecklikescode.turing.api.modules.Info;
import de.niecklikescode.turing.api.modules.Module;
import org.lwjgl.input.Keyboard;

@Info(description = "Modifies the players velocity", category = Module.Category.PLAYER, keyBind = Keyboard.KEY_M)
public class Velocity extends Module {}
