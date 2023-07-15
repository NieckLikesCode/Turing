package de.niecklikescode.turing.api.mixin;

import de.niecklikescode.turing.api.main.Turing;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.handshake.FMLHandshakeMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(FMLHandshakeMessage.ModList.class)
public class MixinModList {

    @Shadow(remap = false) // Forge classes are not obfuscated therefore trying to remap them results in an error
    private Map<String, String> modTags;

    /**
     * @author NieckLikesCode
     * @reason Removes turing from the mod list sent to the server upon connection
     */
    @Overwrite(remap = false)
    public void toBytes(ByteBuf buffer)
    {
        ByteBufUtils.writeVarInt(buffer, modTags.size(), 2);
        for (Map.Entry<String,String> modTag: modTags.entrySet()) // Key holds mod id, value holds mod version
        {
            if(modTag.getKey().equals(Turing.class.getAnnotation(Mod.class).modid())) {
                Turing.getLogger().info("Skipping mod!");
                continue;
            }

            ByteBufUtils.writeUTF8String(buffer, modTag.getKey());
            ByteBufUtils.writeUTF8String(buffer, modTag.getValue());
        }
    }

}
