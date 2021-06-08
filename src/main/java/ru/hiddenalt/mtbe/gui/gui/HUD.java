package ru.hiddenalt.mtbe.gui.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.hiddenalt.mtbe.process.Process;
import ru.hiddenalt.mtbe.process.ProcessManager;

import java.awt.*;
import java.util.ArrayList;

public class HUD {

    public static void render(MatrixStack matrices, float tickDelta){
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextureManager tm = minecraftClient.getTextureManager();

//        minecraftClient.gameRenderer.getCamera().

        ArrayList<Process> processes = ProcessManager.getAll();
        if(processes.size() > 0) {
            tm.bindTexture(new Identifier("mtbe:textures/builder_icon.png"));
            DrawableHelper.drawTexture(matrices, 10, 10, 0, 0, 32, 32, 32, 32);


            int x = 10 + 32 + 2;
            int y = 10 + 32 / 2 - 20 / 2;
            DrawableHelper.drawTextWithShadow(matrices, minecraftClient.textRenderer, Text.of("MTBE is building..."), x, y, Color.yellow.getRGB());

            y = 10 + 32 / 2 - 20 / 2 + 10;
            DrawableHelper.drawTextWithShadow(matrices, minecraftClient.textRenderer, Text.of("Running processes: "+processes.size()), x, y,Color.white.getRGB());

            int maxLines = 3;

            x = 10;
            y = 10 + 32 + 2;
            int i = 0;
            for(Process p : processes) {
                DrawableHelper.drawTextWithShadow(matrices, minecraftClient.textRenderer, Text.of("#"+(i+1)+": "+p.getName()+" (PID: "+p.getPid()+")"), x, y + i * 10,Color.white.getRGB());
                i++;
                if((i + 1) > maxLines && processes.size() > maxLines){
                    DrawableHelper.drawTextWithShadow(matrices, minecraftClient.textRenderer, Text.of("and "+(processes.size() - maxLines)+" more..."), x, y+ i * 10, Color.white.getRGB());
                    break;
                }
            }
        }
    }

}
