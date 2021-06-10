package ru.hiddenalt.mtbe.process;



import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.schematic.FillSchematic;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import ru.hiddenalt.mtbe.schematic.ExtendedMCEditSchematic;
import ru.hiddenalt.mtbe.schematic.Schematic;
import ru.hiddenalt.mtbe.schematic.SchematicBlock;

public class BaritoneProcess extends Process {

    private ExtendedMCEditSchematic schematic;
    protected Vec3d startPos;

    public BaritoneProcess(ExtendedMCEditSchematic schematic, Vec3d startPos) {
        this.schematic = schematic;
        this.startPos = startPos;
    }

    public ExtendedMCEditSchematic getSchematic() {
        return schematic;
    }

    public void setSchematic(ExtendedMCEditSchematic schematic) {
        this.schematic = schematic;
    }

    public Vec3d getStartPos() {
        return startPos;
    }

    public void setStartPos(Vec3d startPos) {
        this.startPos = startPos;
    }

    @Override
    public void cancel() {
        IBaritone bar = BaritoneAPI.getProvider().getPrimaryBaritone();
        bar.getBuilderProcess().build("",
                new FillSchematic(0,0,0, Registry.BLOCK.get(new Identifier("minecraft:air")).getDefaultState()),
                new Vec3i(0,0,0)
        );
        bar.getBuilderProcess().pause();
        this.isActive = false;
        ProcessManager.remove(this);
    }

    @Override
    public void pause() {
        if(!this.isActive) return;
        this.isPaused = true;
        BaritoneAPI.getProvider().getPrimaryBaritone().getBuilderProcess().pause();
    }

    @Override
    public void resume() {
        if(!this.isActive) return;
        this.isPaused = false;
        BaritoneAPI.getProvider().getPrimaryBaritone().getBuilderProcess().resume();
    }

    @Override
    public String getType() {
        return "BaritoneProcess";
    }

    public String getName(){
        // TODO: return URL
        return this.getType();
    }

    @Override
    public void start() {
        super.start();
        IBaritone bar = BaritoneAPI.getProvider().getPrimaryBaritone();

        if(!bar.getBuilderProcess().isPaused()){
            this.isActive = false;
            this.isPaused = true;

            if(MinecraftClient.getInstance().player != null)
                MinecraftClient.getInstance().player.
                        sendMessage(Text.of("Primary Baritone process is busy!"), false);
            return;
        }
        BaritoneAPI.getSettings().mapArtMode.value = true;

        this.isActive = true;
        this.isPaused = false;

        bar.getBuilderProcess().build("schematic", schematic, new BlockPos(startPos.x,startPos.y,startPos.z-1));
    }
}
