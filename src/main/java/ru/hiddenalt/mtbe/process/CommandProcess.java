package ru.hiddenalt.mtbe.process;


import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.ArrayUtils;
import ru.hiddenalt.mtbe.schematic.Schematic;
import ru.hiddenalt.mtbe.schematic.SchematicBlock;

public class CommandProcess extends Process {

    protected Thread thread;
    protected Schematic schematic;
    protected int delay;
    protected String cmd;
    protected Vec3d startPos;

    protected int commandPerIteration = 3;


    public CommandProcess(Schematic schematic, int delay, String cmd, Vec3d startPos) {
        this.schematic = schematic;
        this.delay = delay;
        this.cmd = cmd;
        this.startPos = startPos;
        this.init();
    }

    public void init(){
        thread = new Thread(() -> {
            SchematicBlock[][][] blocks = schematic.getBlocks();

            int width = schematic.getWidth();
            int height = schematic.getHeight();
            int length = schematic.getLength();


            for(int x = 0; x < width; x++) {
//                for (int y = 0; y < height; y++) {
//                    ArrayUtils.reverse(blocks[x][y]);
//                }
                ArrayUtils.reverse(blocks[x]);
            }
//            ArrayUtils.reverse(blocks);

            int operations = 0;

            for(int x = 0; x < width; x++) {
                for(int y = 0; y < height; y++) {
                    for(int z = 0; z < length; z++) {
                        try {
                            if(blocks[x][y][z] == null) continue;
                            Identifier id = blocks[x][y][z].getIdentifier();
                            //TODO: to options: replace minecraft:air
                            if(id.toString().equals("minecraft:air")) continue;

                            if(operations % commandPerIteration == 0) {
                                Thread.sleep(delay);
                                while (isPaused) Thread.sleep(delay);
                                operations = 0;
                            }
                            operations++;

                            if(MinecraftClient.getInstance().player == null) this.cancel();




                            String namespace = id.getNamespace();
                            String path = id.getPath();
                            String fullID = id.toString();

                            String sendToChat = this.cmd
                                    .replaceAll("%X%", ""+ ((long)(this.startPos.x) + x))
                                    .replaceAll("%Y%", ""+ ((long)(this.startPos.y) + y))
                                    .replaceAll("%Z%", ""+ ((long)(this.startPos.z) + z - 1))
                                    .replaceAll("%NAMESPACE%", ""+namespace)
                                    .replaceAll("%PATH%", ""+path)
                                    .replaceAll("%BLOCK_ID%", ""+fullID)
                                    ;

                            assert MinecraftClient.getInstance().player != null;
                            MinecraftClient.getInstance().player.sendChatMessage(sendToChat);
                        } catch (InterruptedException | NullPointerException e) {
                            //e.printStackTrace();
                        }
                    }
                }
            }
            this.cancel();
        });
    }

    @Override
    public void cancel() {
        ProcessManager.remove(this);
        this.isActive = false;
        this.isPaused = false;
        if(thread != null){
            thread.interrupt();
            thread.stop();
            thread = null;
        }

    }

    @Override
    public void pause() {
        isPaused = true;
    }

    @Override
    public void resume() {
        isPaused = false;
    }

    @Override
    public String getType() {
        return "CommandProcess";
    }

    @Override
    public String getName() {
        return this.getType()+" (w="+schematic.getWidth()+",h="+schematic.getHeight()+",l="+schematic.getLength()+")";
    }

    public Schematic getSchematic() {
        return schematic;
    }

    public void setSchematic(Schematic schematic) {
        this.schematic = schematic;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getCommandPerIteration() {
        return commandPerIteration;
    }

    public void setCommandPerIteration(int commandPerIteration) {
        this.commandPerIteration = commandPerIteration;
    }

    @Override
    public void start(){
        super.start();
        if(this.thread != null){
            this.isActive = true;
            this.isPaused = false;
            this.thread.start();
        }
    }

    public Vec3d getStartPos() {
        return startPos;
    }

    public void setStartPos(Vec3d startPos) {
        this.startPos = startPos;
    }
}
