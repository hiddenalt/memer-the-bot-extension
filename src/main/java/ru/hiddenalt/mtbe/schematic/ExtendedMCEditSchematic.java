package ru.hiddenalt.mtbe.schematic;

import baritone.utils.schematic.StaticSchematic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class ExtendedMCEditSchematic extends StaticSchematic {
    public ExtendedMCEditSchematic(CompoundTag schematic) {
        String type = schematic.getString("Materials");
        if (!type.equals("Alpha")) {
            throw new IllegalStateException("bad schematic " + type);
        } else {
            this.x = schematic.getInt("Width");
            this.y = schematic.getInt("Height");
            this.z = schematic.getInt("Length");

            CompoundTag reference = schematic.getCompound("Reference");
//            schematic.

            byte[] blocks = schematic.getByteArray("Blocks");
            byte[] additional = null;
            int z;
            if (schematic.contains("AddBlocks")) {
                byte[] addBlocks = schematic.getByteArray("AddBlocks");
                additional = new byte[addBlocks.length * 2];

                for(z = 0; z < addBlocks.length; ++z) {
                    additional[z * 2 + 0] = (byte)(addBlocks[z] >> 4 & 15);
                    additional[z * 2 + 1] = (byte)(addBlocks[z] >> 0 & 15);
                }
            }

            this.states = new BlockState[this.x][this.z][this.y];

            for(int y = 0; y < this.y; ++y) {
                for(z = 0; z < this.z; ++z) {
                    for(int x = 0; x < this.x; ++x) {
                        int blockInd = (y * this.z + z) * this.x + x;
                        int blockID = blocks[blockInd] & 255;
                        if (additional != null) {
                            blockID |= additional[blockInd] << 8;
                        }

                        Identifier identifier = new Identifier("minecraft:air");
                        if(reference.contains(""+blockID)){
                            // parsed value has " chars for some reason
                            String toID = reference.get(""+blockID).toString().replaceAll("\"", "");
                            identifier = Identifier.tryParse(toID) == null ? identifier : Identifier.tryParse(toID);
                        }

                        Block block = Registry.BLOCK.get(identifier);
                        this.states[x][z][y] = block.getDefaultState();
                    }
                }
            }

        }
    }
}
