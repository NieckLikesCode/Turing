package de.niecklikescode.turing.modules;

import de.niecklikescode.turing.api.events.TickEvent;
import de.niecklikescode.turing.api.modules.Info;
import de.niecklikescode.turing.api.modules.Module;
import de.niecklikescode.turing.api.utils.Timer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.Random;

import static de.niecklikescode.turing.api.utils.ReflectionUtils.invokePrivate;

// Took some heavy inspiration (stole) from this post: https://hackforums.net/showthread.php?tid=5484955
// no complicated statistics but a nice insight nonetheless
@Info(name = "Auto Clicker", keyBind = Keyboard.KEY_F, description = "Automatically clicks for you", category = Module.Category.COMBAT)
public class AutoClicker extends Module {

    private final Timer timer = new Timer();
    private final Random random = new Random();

    // Clicking statistics are hardcoded for now until I implement a click recorder and/or settings
    final double deviation = 36, mean = 155;
    final int min = 110, max = 387;

    // Use tick event to not mess up the packet order which is very common to check for anti cheats
    @SubscribeEvent
    public void onTick(TickEvent event) {
        if(getLocalPlayer() == null || getWorld() == null) return;
        if(MC.currentScreen != null || !Mouse.isButtonDown(0)) return;

        double randGauss = (random.nextGaussian() * deviation);

        long delayPreClamp = Math.round(randGauss + mean);
        long delay = (long) MathHelper.clamp_double(delayPreClamp, min, max);

        // TODO: Invoking functions using reflections seems to bypass mixins. Since criticals cancels attack via mixins we need a workaround for it
        timer.invokeIfComplete(delay, this::sendLeftClick);
    }

    private void sendLeftClick() {
        try {
            invokePrivate(MC, "clickMouse", "aw");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
