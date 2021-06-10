package ru.hiddenalt.mtbe.process;



import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import ru.hiddenalt.mtbe.schematic.ExtendedMCEditSchematic;

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
        // TODO: make 100% cancel method
        this.pause();
        ProcessManager.remove(this);
    }

    @Override
    public void pause() {
        BaritoneAPI.getProvider().getPrimaryBaritone().getBuilderProcess().pause();
    }

    @Override
    public void resume() {
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
        IBaritone bar = BaritoneAPI.getProvider().getPrimaryBaritone();
        BaritoneAPI.getSettings().mapArtMode.value = true;
        bar.getBuilderProcess().build("schematic", schematic, new BlockPos(startPos.x,startPos.y,startPos.z-1));
    }
}
