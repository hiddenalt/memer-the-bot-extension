package ru.hiddenalt.mtbe.mixin;

//import baritone.api.BaritoneAPI;
//import baritone.api.BaritoneAPI;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.hiddenalt.mtbe.gui.screen.options.OptionsScreen;

@Mixin(TitleScreen.class)
//public class TitleScreenMixin extends Screen {
public class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "initWidgetsNormal")
    public void initModdedGUI(int y, int spacingY, CallbackInfo cl){
//        new TranslatableText("hahahaha")

//        Text text = new Text() {
//            @Override
//            public Style getStyle() {
//                return null;
//            }
//
//            @Override
//            public String asString() {
//                return "ascascascasc";
//            }
//
//            @Override
//            public List<Text> getSiblings() {
//                return null;
//            }
//
//            @Override
//            public MutableText copy() {
//                return null;
//            }
//
//            @Override
//            public MutableText shallowCopy() {
//                return null;
//            }
//
//            @Override
//            public OrderedText asOrderedText() {
//                return null;
//            }
//        };

//        this.addButton(new ButtonWidget(10, 10, 10, 20, new TranslatableText("hahahaha"), (buttonWidget) -> {
//            assert this.client != null;
//            this.client.openScreen(new SelectWorldScreen(this));
//        }));
//         textures/gui/widgets.png
//
//        ButtonWidget.TooltipSupplier tooltipSupplier = (buttonWidget, matrixStack, i, j) -> {
//            if (!buttonWidget.active) {
//                assert this.client != null;
//                this.renderOrderedTooltip(matrixStack, this.client.textRenderer.wrapLines(new TranslatableText("Memer The Bot: Extension"), Math.max(this.width / 2 - 43, 170)), i, j);
//            }
//        };
//

        ButtonWidget.TooltipSupplier tooltipSupplier = (buttonWidget, matrixStack, i, j) -> {
            if (!buttonWidget.active) {
                assert this.client != null;
                this.renderTooltip(matrixStack, (Text) this.client.textRenderer.wrapLines(new TranslatableText("menu.icon"), Math.max(this.width / 2 - 43, 170)), i, j);
            }
        };

        ((ButtonWidget)this.addButton(new TexturedButtonWidget(10, 10, 20, 20, 0, 0, 20, new Identifier("mtbe:textures/icon-96.png"), 20, 20, (buttonWidget) -> {
            assert this.client != null;
            this.client.openScreen(new OptionsScreen(this));
//            this.client.openScreen(new ColorDefinitionTable(this));
        }, new TranslatableText("narrator.button.language")))).active = true;;



//        tooltipSupplier
    }

}
