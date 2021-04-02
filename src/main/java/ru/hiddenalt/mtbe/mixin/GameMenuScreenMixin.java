package ru.hiddenalt.mtbe.mixin;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.hiddenalt.mtbe.gui.screen.ingame.ActionsScreen;
import ru.hiddenalt.mtbe.gui.ui.ButtonWidgetTexturedFix;
import ru.hiddenalt.mtbe.gui.ui.tooltip.SimpleTooltip;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "initWidgets")
    public void initWidgets(CallbackInfo ci){
        this.addButton(new ButtonWidgetTexturedFix(10, 10, 20, 20, Text.of(""), (buttonWidget) -> {
            assert this.client != null;
            this.client.openScreen(new ActionsScreen(this));
        },
            new SimpleTooltip(textRenderer, new TranslatableText("menu.tooltip")),
            new Identifier("mtbe:textures/icon-96.png"), 0, 0, 20, 20));
    }

}
