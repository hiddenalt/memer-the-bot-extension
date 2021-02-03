package ru.hiddenalt.mtbe.mixin;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.hiddenalt.mtbe.gui.screen.ingame.ActionsScreen;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "initWidgets")
    public void initWidgets(CallbackInfo ci){
        ((ButtonWidget)this.addButton(new TexturedButtonWidget(10, 10, 20, 20, 0, 0, 20, new Identifier("mtbe:textures/icon-96.png"), 20, 20, (buttonWidget) -> {
            assert this.client != null;
            this.client.openScreen(new ActionsScreen(this));
//            this.client.openScreen(new BlocksTableScreen(this));







//            int test = BaritoneAPI.getProvider().hashCode();
//            System.out.println(test);
//            BaritoneAPI.getSettings().chatDebug.value = false;
//            BaritoneAPI.getProvider().


//            IBaritone bar = BaritoneAPI.getProvider().getPrimaryBaritone();
//            ICustomGoalProcess proc = bar.getCustomGoalProcess();
//            bar.getPathingBehavior().forceCancel();

            // []
//            System.out.println(Arrays.toString(BaritoneAPI.getSettings().buildIgnoreBlocks.value.toArray()));
            // []
//            System.out.println(Arrays.toString(BaritoneAPI.getSettings().blocksToAvoid.value.toArray()));
            //[Block{minecraft:crafting_table}, Block{minecraft:furnace}, Block{minecraft:chest}, Block{minecraft:trapped_chest}]
//            System.out.println(Arrays.toString(BaritoneAPI.getSettings().blocksToAvoidBreaking.value.toArray()));


            // IT WORKS!!!
//            try {
//                Schematic test = new Schematic(3,5,1);
//                test.fillWith(new SchematicBlock(3));
//                test.saveAsTemp();

//                IPlayerContext player = bar.getPlayerContext();
//                Vec3d vec = player.playerFeetAsVec();
//                bar.getBuilderProcess().build(test.getTempFilename(), new BlockPos(vec.x,vec.y,vec.z));
//            } catch (IOException e) {
//                System.out.println("Something went wrong!");
//                e.printStackTrace();
//            }


//            bar.

//            proc.setGoalAndPath(new GoalXZ(-183, 242));
        }, new TranslatableText("narrator.button.language")))).active = true;;
    }




}
