package ru.hiddenalt.mtbe.mixin;



import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.hiddenalt.mtbe.gui.screen.options.OptionsScreen;
import ru.hiddenalt.mtbe.gui.ui.ButtonWidgetTexturedFix;
import ru.hiddenalt.mtbe.gui.ui.tooltip.SimpleTooltip;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "initWidgetsNormal")
    public void initModdedGUI(int y, int spacingY, CallbackInfo cl){
        this.addButton(new ButtonWidgetTexturedFix(10, 10, 20, 20, Text.of(""), (buttonWidget) -> {
            assert this.client != null;
            this.client.openScreen(new OptionsScreen(this));
        },
            new SimpleTooltip(textRenderer, new TranslatableText("menu.tooltip")),
            new Identifier("mtbe:textures/icon-96.png"), 0, 0, 20, 20));
    }

}
