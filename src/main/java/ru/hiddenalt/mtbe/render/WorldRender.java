package ru.hiddenalt.mtbe.render;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.hiddenalt.mtbe.schematic.Schematic;
import ru.hiddenalt.mtbe.schematic.SchematicBlock;

import java.util.concurrent.*;

public class WorldRender {

//    public BlockRenderManager blockRenderManager = new BlockRenderManager(
//        mc.getBakedModelManager().getBlockModels(),
//        BlockColors.create()
//    );

//    public static BlockPos pos;
//    public static Schematic schematic;
//    private static Thread previewThread;
//    private static final Object previewLock = new Object();

//    public static void clearPreviewSchematic(){
//        WorldRender.pos = null;
//        WorldRender.schematic = null;
//
//        if(WorldRender.previewThread != null) {
//            WorldRender.previewThread.interrupt();
//            WorldRender.previewThread = null;
//        }
//    }

//    public static void showPreview(Schematic schematic, BlockPos pos){
//        WorldRender.pos         = pos;
//        WorldRender.schematic   = schematic;

//        int perIteration = 6;
//        int delay = 100;

        /*
        TODO: fix (?)
        java.lang.ArrayIndexOutOfBoundsException: -1
	at it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet.rehash(LongLinkedOpenHashSet.java:1083)
	at net.minecraft.world.chunk.light.LevelPropagator$1.rehash(LevelPropagator.java:29)
	at it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet.add(LongLinkedOpenHashSet.java:368)
	at net.minecraft.world.chunk.light.LevelPropagator.addPendingUpdate(LevelPropagator.java:110)
	at net.minecraft.world.chunk.light.LevelPropagator.updateLevel(LevelPropagator.java:152)
	at net.minecraft.world.chunk.light.LevelPropagator.updateLevel(LevelPropagator.java:122)
	at net.minecraft.world.chunk.light.SkyLightStorage.updateLight(SkyLightStorage.java:273)
	at net.minecraft.world.chunk.light.ChunkLightProvider.doLightUpdates(ChunkLightProvider.java:158)
	at net.minecraft.world.chunk.light.LightingProvider.doLightUpdates(LightingProvider.java:55)
	at net.minecraft.client.render.WorldRenderer.render(WorldRenderer.java:935)
	at net.minecraft.client.render.GameRenderer.renderWorld(GameRenderer.java:624)
	at net.minecraft.client.render.GameRenderer.render(GameRenderer.java:424)
	at net.minecraft.client.MinecraftClient.render(MinecraftClient.java:1007)
	at net.minecraft.client.MinecraftClient.run(MinecraftClient.java:624)
	at net.minecraft.client.main.Main.main(Main.java:187)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at net.fabricmc.loader.game.MinecraftGameProvider.launch(MinecraftGameProvider.java:224)
	at net.fabricmc.loader.launch.knot.Knot.init(Knot.java:141)
	at net.fabricmc.loader.launch.knot.KnotClient.main(KnotClient.java:27)
	at net.fabricmc.devlaunchinjector.Main.main(Main.java:86)
         */
/*
        previewThread = new Thread(() -> {

            try {
                final SchematicBlock[][][] blocks = schematic.getBlocks();
                final MinecraftClient mc = MinecraftClient.getInstance();
                final ClientPlayerEntity player = mc.player;
                final Vec3d playerPos = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

                int width = schematic.getWidth();
                int height = schematic.getHeight();
                int length = schematic.getLength();

                int operations = 0;

                for(int x = 0; x < width; x++) {
                    synchronized (previewLock) {
                        ArrayUtils.reverse(blocks[x]);

                        if (MinecraftClient.getInstance().world == null) return;
//                        if (operations % perIteration == 0) {
//                            Thread.sleep(delay);
//                            operations = 0;
//                        }
//                        operations++;
                    }
                }

                for(int x = 0; x < width; x++) {
                    for(int y = 0; y < height; y++) {
                        for(int z = 0; z < length; z++) {
                            synchronized (previewLock) {
                                if (MinecraftClient.getInstance().world == null) return;
                                if (blocks[x][y][z] == null) continue;
                                Identifier id = blocks[x][y][z].getIdentifier();

                                //TODO: to options: replace minecraft:air
                                if (id.toString().equals("minecraft:air")) continue;

//                                if (operations % perIteration == 0) {
//                                    Thread.sleep(delay);
//                                    operations = 0;
//                                }
//                                operations++;

//                            System.out.println(x+" "+y+" "+z);

                                if (!id.toString().equals("minecraft:air")) {

                                    Block b = Registry.BLOCK.get(id);
//                                    b.postProcessState(FabricBlockSettings.luminance(100), );

                                    //                    Block b = new Block(
                                    //                        FabricBlockSettings.copyOf(b1).luminance(100)
                                    //                    );
                                    //                    b.

                                    assert mc.world != null;
                                    mc.world.setBlockState(new BlockPos(playerPos.x + x, playerPos.y + y, playerPos.z + z), b.getDefaultState());
                                }
                            }
                        }
                    }
                }
                if(previewThread != null) {
                    previewThread.interrupt();
                    previewThread = null;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        });
        previewThread.start();

        */
//    }

//    public static void render(
//            MatrixStack stack,
//            float tickDelta,
//            long limitTime,
//            boolean renderBlockOutline,
//            Camera camera,
//            GameRenderer gameRenderer,
//            LightmapTextureManager lightmapTextureManager,
//            Matrix4f matrix4f,
//
//            CallbackInfo info
//    ){

//        MinecraftClient mc = MinecraftClient.getInstance();
//        ClientPlayerEntity player = mc.player;
//        if(player == null) return;

//        Vec3d pos = player.getPos();


//        if(WorldRender.pos == null || WorldRender.schematic == null) return;

//        int width = schematic.getWidth();
//        int height = schematic.getHeight();
//        int length = schematic.getLength();

        //Description: Unexpected error
        //
        //java.lang.IllegalStateException: Already building!
//        PathRenderer.drawLine(stack, pos.x, pos.y, pos.z, pos.x + width, pos.y + height, pos.z + length);

        /*
        // Bad idea to render all blocks?



        SchematicBlock[][][] blocks = schematic.getBlocks();

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                for(int z = 0; z < length; z++) {
                    if(blocks[x][y][z] == null) continue;
                    Identifier id = blocks[x][y][z].getIdentifier();
                    if(id.toString().equals("minecraft:air")) continue;

                    stack.push();

                    // translate
                    stack.translate(
                            pos.x + x - IRenderer.renderManager.renderPosX(),
                            pos.y + y - IRenderer.renderManager.renderPosY(),
                            pos.z + z - IRenderer.renderManager.renderPosZ()
                    );

                    Block blockInstance = Registry.BLOCK.get(id);
                    BlockState state = blockInstance.getDefaultState();
                    mc.getBlockRenderManager().renderBlock(
                            state,
                            new BlockPos(0,0,0),
                            mc.world,
                            stack,
                            mc.getBufferBuilders().getEntityVertexConsumers().getBuffer(RenderLayer.getCutout()),
                            false,
                            (new Random())
                    );

                    stack.pop();
                }
            }
        }
        */




//        System.out.println("test");

//        glPushAttrib(GL_LIGHTING_BIT);

//        mc.getTextureManager().bindTexture(new Identifier("textures/entity/beacon_beam.png"));
//        RenderSystem.disableDepthTest();
//
//        stack.pop();
//        info.
        /*
        BeaconBlockEntityRenderer.renderLightBeam(
                stack,
                mc.getBufferBuilders().getEntityVertexConsumers(),
                new Identifier("textures/entity/beacon_beam.png"),
                tickDelta,
                1.0F,
                player.world.getTime(),
                0,
                256,
                Color.white.getColorComponents(null),

                // Arguments filled by the private method lol
                0.2F,
                0.25F
        );
        */

//        BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light, int overlay

        //BlockRenderView world, BakedModel model,
        // BlockState state, BlockPos pos,
        // MatrixStack buffer, VertexConsumer vertexConsumer, boolean cull, Random random, long seed, int overlay

//
//        blockRenderer.render();





         // pop

//        RenderSystem.enableDepthTest();

//        glPopAttrib();

//    }
}
