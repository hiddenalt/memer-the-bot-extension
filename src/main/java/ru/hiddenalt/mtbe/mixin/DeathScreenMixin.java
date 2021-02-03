package ru.hiddenalt.mtbe.mixin;


import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DeathScreenMixin.class)
public class DeathScreenMixin extends DeathScreen {
    public DeathScreenMixin(@Nullable Text message, boolean isHardcore) {
        super(message, isHardcore);
    }
//    public DeathScreenMixin(MinecraftClient minecraftClient) {
//        super(minecraftClient);
//    }

//    @Inject(at = @At("RETURN"), method = "render")
//    @Inject(at = @At("HEAD"), method = "<init>()V")
//    @Inject(at = @At("HEAD"), method = "mouseClicked", remap = false)
//    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfo cl){
//        System.out.println("QQQQQQQQ");
//    }



}
