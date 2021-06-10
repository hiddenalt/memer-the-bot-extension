package ru.hiddenalt.mtbe.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import ru.hiddenalt.mtbe.gui.screen.options.OptionsScreen;
import ru.hiddenalt.mtbe.gui.ui.ButtonWidgetTexturedFix;
import ru.hiddenalt.mtbe.gui.ui.tooltip.SimpleTooltip;
import ru.hiddenalt.mtbe.process.BaritoneProcess;
import ru.hiddenalt.mtbe.process.CommandProcess;
import ru.hiddenalt.mtbe.process.Process;
import ru.hiddenalt.mtbe.process.ProcessManager;
import ru.hiddenalt.mtbe.schematic.ExtendedMCEditSchematic;
import ru.hiddenalt.mtbe.schematic.Schematic;

import java.awt.*;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ActionsScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget customImageURLField;
    private String imageURL = "";

    private int topButtonsOffset = 40;
    private int buttonOffset = 20 * 2;
    private int innerOffset = 25;
    private ButtonWidgetTexturedFix buildImage;
    private ComposeSchematic lastScreen;
    private ProcessesTableWidget processesTableWidget;

    private ButtonWidgetTexturedFix pauseResumeProcess;
    private ButtonWidgetTexturedFix resumeProcess;
    private ButtonWidgetTexturedFix cancelProcess;
    private ButtonWidgetTexturedFix copyProcessInfo;

    public ActionsScreen(Screen parent) {
        super(new TranslatableText("actions.title"));
        this.parent = parent;
    }

    protected void init() {

        this.processesTableWidget = new ActionsScreen.ProcessesTableWidget(this.client, this);
        this.children.add(this.processesTableWidget);

        int px = 5;
        int py = 15;
        int po = 5;
        int pw = 20;
        int ph = 20;

        // Manipulating all actions
        this.addButton(new ButtonWidgetTexturedFix(px + (po + pw) * 0, py, pw, ph, Text.of(""), (buttonWidget) -> {
            ProcessManager.cancelAll();
        },
                new SimpleTooltip(textRenderer, new TranslatableText("actions.processes.cancelAll")),
                new Identifier("mtbe:textures/actions_screen/cancel_all.png"), 0, 0, 20, 20));

        this.addButton(new ButtonWidgetTexturedFix(px + (po + pw) * 1, py, pw, ph, Text.of(""), (buttonWidget) -> {
            ProcessManager.pauseAll();
        },
                new SimpleTooltip(textRenderer, new TranslatableText("actions.processes.pauseAll")),
                new Identifier("mtbe:textures/actions_screen/pause_all.png"), 0, 0, 20, 20));

        this.addButton(new ButtonWidgetTexturedFix(px + (po + pw) * 2, py, pw, ph, Text.of(""), (buttonWidget) -> {
            ProcessManager.resumeAll();
        },
                new SimpleTooltip(textRenderer, new TranslatableText("actions.processes.resumeAll")),
                new Identifier("mtbe:textures/actions_screen/resume_all.png"), 0, 0, 20, 20));

        // Actions for single entry

        px = 5;
        py = 15;
        po = 5;
        pw = 20;
        ph = 20;

        pauseResumeProcess  = new ButtonWidgetTexturedFix(px + po * 2 + (po + pw) * 3, py, pw, ph, Text.of(""), (buttonWidget) -> {
            ProcessesTableWidget.ProcessEntry pe = this.processesTableWidget.getSelected();
            if(pe == null) return;
            Process p = pe.process;
            if(p == null) return;

            if(!p.isActive()) {
                p.start();
                return;
            }

            if (!p.isPaused()) {
                p.pause();
            } else {
                p.resume();
            }
        },
            new SimpleTooltip(textRenderer, new TranslatableText("actions.process.pauseResumeProcess")),
            new Identifier("mtbe:textures/actions_screen/pause_resume_switch.png"), 0, 0, 20, 20);
        pauseResumeProcess.visible = false;
        this.addButton(pauseResumeProcess);

        cancelProcess       = new ButtonWidgetTexturedFix(px + po * 2 + (po + pw) * 4, py, pw, ph, Text.of(""), (buttonWidget) -> {
            ProcessesTableWidget.ProcessEntry pe = this.processesTableWidget.getSelected();
            if(pe == null) return;
            Process p = pe.process;
            if(p == null) return;
            p.cancel();
        },
                new SimpleTooltip(textRenderer, new TranslatableText("actions.process.cancelProcess")),
                new Identifier("mtbe:textures/actions_screen/cancel.png"), 0, 0, 20, 20);
        cancelProcess.visible = false;
        this.addButton(cancelProcess);

        copyProcessInfo     = new ButtonWidgetTexturedFix(px + po * 2 + (po + pw) * 5, py, pw, ph, Text.of(""), (buttonWidget) -> {
            ArrayList<String> info = this.processesTableWidget.getSelected().getRenderingInfo();
            if(info == null) return;

            StringBuilder sb = new StringBuilder();

            for(String s : info)
                sb.append(s).append("\n");

            MinecraftClient.getInstance().keyboard.setClipboard(sb.toString());
        },
                new SimpleTooltip(textRenderer, new TranslatableText("actions.process.copyProcessInfo")),
                new Identifier("mtbe:textures/actions_screen/copy.png"), 0, 0, 20, 20);
        copyProcessInfo.visible = false;
        this.addButton(copyProcessInfo);


        // Build section
        this.addButton(new ButtonWidget(this.width / 2 - 100, topButtonsOffset + innerOffset * 0, 200, 20, new TranslatableText("actions.build.meme.demotivational.poster"), (buttonWidget) -> {
//            assert this.client != null;
//            this.client.openScreen(this.parent);
        }));

        this.addButton(new ButtonWidget(this.width / 2 - 100, topButtonsOffset + innerOffset * 1, 200, 20, new TranslatableText("actions.build.meme.when"), (buttonWidget) -> {
//            assert this.client != null;
//            this.client.openScreen(this.parent);
        }));

        this.addButton(new ButtonWidget(this.width / 2 - 100, topButtonsOffset + innerOffset * 2, 200, 20, new TranslatableText("actions.build.meme.comics"), (buttonWidget) -> {
//            assert this.client != null;
//            this.client.openScreen(this.parent);
        }));

        this.customImageURLField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100,
                topButtonsOffset + innerOffset * 4 + 12,
                200 - 25,
                20,
                Text.of("")
        );
        this.customImageURLField.setSelected(false);
        this.customImageURLField.setMaxLength(99999);
        this.customImageURLField.setText(imageURL);
        this.customImageURLField.setChangedListener(this::onAddressFieldChange);
        this.children.add(this.customImageURLField);



        // this.width / 2 + 100 - 20
        // topButtonsOffset + innerOffset * 5 + 12
        buildImage = new ButtonWidgetTexturedFix(this.width / 2 + 100 - 20, topButtonsOffset + innerOffset * 4 + 12, 20, 20, Text.of(""), (buttonWidget) -> {
            assert this.client != null;
            this.lastScreen = new ComposeSchematic(this.client,this, this.customImageURLField.getText());
            this.client.openScreen(this.lastScreen);
        },
                new SimpleTooltip(textRenderer, new TranslatableText("actions.build.image")),
                new Identifier("mtbe:textures/open_file.png"), 0, 0, 20, 20);
        this.addButton(buildImage);


        if(this.lastScreen != null) {
            this.addButton(new ButtonWidgetTexturedFix(this.width / 2 - 100 - 25, topButtonsOffset + innerOffset * 4 + 12, 20, 20, Text.of(""), (buttonWidget) -> {
                if(this.lastScreen == null) return;
                assert this.client != null;
                this.client.openScreen(this.lastScreen);
            },
                new SimpleTooltip(textRenderer, new TranslatableText("actions.build.reopenLastScreen")),
                new Identifier("mtbe:textures/menu/reopen_last_screen.png"), 0, 0, 20, 20));
        }



        // "Done" button
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - buttonOffset, 200, 20, ScreenTexts.DONE, (buttonWidget) -> {
            assert this.client != null;
            this.client.openScreen(this.parent);
        }));

        // Options button
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - buttonOffset - innerOffset, 200, 20, new TranslatableText("actions.open.options"), (buttonWidget) -> {
            assert this.client != null;
            this.client.openScreen(new OptionsScreen(this));
        }));
    }


    public void showProcessActions(ProcessesTableWidget.ProcessEntry e){
        if(e == null){
            copyProcessInfo.visible = false;
            cancelProcess.visible = false;
            pauseResumeProcess.visible = false;
            return;
        }
        Process process = e.getProcess();

        copyProcessInfo.visible = true;
        cancelProcess.visible = true;
        pauseResumeProcess.visible = true;
    }


    public void filesDragged(List<Path> paths) {
        if(paths.size() > 0){
            this.customImageURLField.setText("file:///"+paths.get(0).toString());
            buildImage.onPress();
        }
    }


    private void onAddressFieldChange(String s) {
        imageURL = s;
    }

    public void tick() {
        this.customImageURLField.tick();
    }

    public void resize(MinecraftClient client, int width, int height) {
        String imageURL = this.customImageURLField.getText();

        this.init(client, width, height);

        this.customImageURLField.setText(imageURL);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.processesTableWidget.render(matrices, mouseX, mouseY, delta);

        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);

        drawStringWithShadow(matrices, this.textRenderer, I18n.translate("actions.section.build", new Object[0]), this.width / 2 - 100, topButtonsOffset - 10, 10526880);

        drawCenteredString(matrices, this.textRenderer, I18n.translate("actions.or", new Object[0]), this.width / 2, topButtonsOffset + innerOffset * 4 - innerOffset / 2, 16777215);
        drawStringWithShadow(matrices, this.textRenderer, I18n.translate("actions.image.url", new Object[0]), this.width / 2 - 100, topButtonsOffset + innerOffset * 4, 10526880);


        this.customImageURLField.render(matrices, mouseX, mouseY, delta);

        super.render(matrices, mouseX, mouseY, delta);
    }













    @Environment(EnvType.CLIENT)
    public class ProcessesTableWidget extends AlwaysSelectedEntryListWidget<ActionsScreen.ProcessesTableWidget.ProcessEntry> {
        protected ActionsScreen screen;
        public static final int rowHeight = 80;

        public ProcessesTableWidget(MinecraftClient client, ActionsScreen screen) {
            super(client, Math.max(180, screen.width / 4), screen.height, 40, screen.height, rowHeight);
//            super(client, screen.width, screen.height, 0, 200, 32);
            this.screen = screen;

            ArrayList<Process> processes = ProcessManager.getAll();

            for(Process p : processes){
                ActionsScreen.ProcessesTableWidget.ProcessEntry entry =
                        new ActionsScreen.ProcessesTableWidget.ProcessEntry(screen, p);
                this.addEntry(entry);
            }

            if (this.getSelected() != null) {
                this.centerScrollOn(this.getSelected());
            }

        }

        protected int getScrollbarPositionX() {
            return this.width - 6;
        }

        public int getRowWidth() {
            return this.width - 30;
        }

        public void setSelected(@Nullable ActionsScreen.ProcessesTableWidget.ProcessEntry entry) {
            super.setSelected(entry);
            this.screen.showProcessActions(entry);
        }

        protected void renderBackground(MatrixStack matrices) {
            super.renderBackground(matrices);
        }

        protected boolean isFocused() {
            return false;
        }


        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            this.fillGradient(
                    matrices,
                    0,
                    this.top,
                    this.width,
                    this.height,
                    (new Color(180, 180, 180,255).getRGB()),
                    (new Color(150,150,150,20).getRGB())
            );



            int i = this.getScrollbarPositionX();
            int j = i + 6;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();

            int k = this.getRowLeft();
            int l = this.top + 4 - (int)this.getScrollAmount();

            this.renderList(matrices, k, l, mouseX, mouseY, delta);

            int o = this.getMaxScroll();
            if (o > 0) {
                RenderSystem.disableTexture();
                int p = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
                p = MathHelper.clamp(p, 32, this.bottom - this.top - 8);
                int q = (int)this.getScrollAmount() * (this.bottom - this.top - p) / o + this.top;
                if (q < this.top) {
                    q = this.top;
                }

                bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                bufferBuilder.vertex(i, this.bottom, 0.0D).texture(0.0F, 1.0F).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(j, this.bottom, 0.0D).texture(1.0F, 1.0F).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(j, this.top, 0.0D).texture(1.0F, 0.0F).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(i, this.top, 0.0D).texture(0.0F, 0.0F).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(i, (q + p), 0.0D).texture(0.0F, 1.0F).color(128, 128, 128, 255).next();
                bufferBuilder.vertex(j, (q + p), 0.0D).texture(1.0F, 1.0F).color(128, 128, 128, 255).next();
                bufferBuilder.vertex(j, q, 0.0D).texture(1.0F, 0.0F).color(128, 128, 128, 255).next();
                bufferBuilder.vertex(i, q, 0.0D).texture(0.0F, 0.0F).color(128, 128, 128, 255).next();
                bufferBuilder.vertex(i, (q + p - 1), 0.0D).texture(0.0F, 1.0F).color(192, 192, 192, 255).next();
                bufferBuilder.vertex((j - 1), (q + p - 1), 0.0D).texture(1.0F, 1.0F).color(192, 192, 192, 255).next();
                bufferBuilder.vertex((j - 1), q, 0.0D).texture(1.0F, 0.0F).color(192, 192, 192, 255).next();
                bufferBuilder.vertex(i, q, 0.0D).texture(0.0F, 0.0F).color(192, 192, 192, 255).next();
                tessellator.draw();
            }

            this.renderDecorations(matrices, mouseX, mouseY);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();



            ArrayList<Process> processes = ProcessManager.getAll();

            //(float) (15 - this.getScrollAmount()),

            DrawableHelper.fill(
                    matrices,
                    0, 0,
                    this.width, this.top,
                    new Color(180, 180, 180,255).getRGB()
            );

            if(this.screen != null && this.screen.textRenderer != null){

                String title = "Processes ("+processes.size()+"):";
                this.screen.textRenderer.drawWithShadow(
                        matrices,
                        title,
                        3,
                        3,
                        Color.yellow.getRGB(),
                        true
                );
            }

        }


        @Environment(EnvType.CLIENT)
        public class ProcessEntry extends net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget.Entry<ActionsScreen.ProcessesTableWidget.ProcessEntry> {
            protected ActionsScreen screen;
            protected Process process;

            protected ArrayList<String> renderingInfo = new ArrayList<>();

            public ProcessEntry(ActionsScreen screen, Process p) {
                this.screen = screen;
                this.process = p;
            }

            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                DrawableHelper.fill(
                        matrices,
                        x - 1, y - 1,
                        x + entryWidth - 3, y + entryHeight + 1,
                        (this.process.isActive() ?
                                (this.process.isPaused() ?
                                        new Color(255, 249, 0, 100).getRGB() :
                                        new Color(42,255, 0, 100).getRGB()
                                ) :
                                (new Color(255, 0, 23, 100).getRGB()))
                );


                int fontSize = 10;
                float half = 14.0F;
                float posX = (float)(x + 5);
                float posY = (float)y + 5;


                renderingInfo = new ArrayList<>();

                renderingInfo.add(process.getName());
                renderingInfo.add("PID: " + process.getPid());
                renderingInfo.add((process.isActive() ? "Active, " + (process.isPaused() ? "paused" : "working") : "Inactive (dead)"));

                if(process instanceof BaritoneProcess){
                    Vec3d p = ((BaritoneProcess) process).getStartPos();
                    ExtendedMCEditSchematic s = ((BaritoneProcess) process).getSchematic();

                    renderingInfo.add("Start: ["+Math.round(p.x)+", "+Math.round(p.y)+", "+Math.round(p.z)+"]");
                    renderingInfo.add("Size: ["+s.widthX()+", "+s.heightY()+", "+s.lengthZ()+"]");
                }

                if(process instanceof CommandProcess){
                    Vec3d p = ((CommandProcess) process).getStartPos();
                    Schematic s = ((CommandProcess) process).getSchematic();

                    int d = ((CommandProcess) process).getDelay();
                    String c = ((CommandProcess) process).getCmd();

                    renderingInfo.add("Start: ["+Math.round(p.x)+", "+Math.round(p.y)+", "+Math.round(p.z)+"]");
                    renderingInfo.add("Size: ["+s.getWidth()+", "+s.getHeight()+", "+s.getLength()+"]");
                    renderingInfo.add("Delay (ms): " + d);
                    renderingInfo.add(c);
                }


                int i = 0;
                for(String s : renderingInfo){
                    int maxSize = 24;
                    if (s.length() > maxSize) {
                        s = s.substring(0, maxSize - 1) + "...";
                    }

                    this.screen.textRenderer.drawWithShadow(matrices, s, posX, posY + i * 10, 16777215, true);
                    i++;
                }
            }

            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    this.onPressed();
                    return true;
                } else {
                    return false;
                }
            }

            private void onPressed() {
                ActionsScreen.ProcessesTableWidget.this.setSelected(this);
            }

            public Process getProcess() {
                return process;
            }

            public void setProcess(Process process) {
                this.process = process;
            }

            public ArrayList<String> getRenderingInfo() {
                return new ArrayList<>(renderingInfo);
            }

            public void setRenderingInfo(ArrayList<String> renderingInfo) {
                this.renderingInfo = renderingInfo;
            }
        }
    }
    
    
    
    
    
    
    
    
    

}
