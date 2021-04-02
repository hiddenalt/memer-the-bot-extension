package ru.hiddenalt.mtbe.mixin;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.hiddenalt.mtbe.gui.gui.HUD;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {

    @Shadow public abstract void tick();

    @Inject(at = @At("RETURN"), method = "render")
    public void render(MatrixStack matrices, float tickDelta, CallbackInfo info) {
        HUD.render(matrices, tickDelta);
    }
}
