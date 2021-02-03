package ru.hiddenalt.mtbe.schematic;

import com.flowpowered.nbt.ByteArrayTag;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ShortTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Schematic {
    protected SchematicBlock[][][] blocks;
    protected int width = 0;
    protected int height = 0;
    protected int length = 0;
    protected String tempName = "tmp.schematic";

    public Schematic() {
        this.setSize(1, 1, 1);
    }

    public Schematic(int x, int y, int z) {
        this.setSize(x, y, z);
    }

    public String getTempFilename() {
        return this.tempName;
    }

    public void fillWith(SchematicBlock block) {
        for(int i = 0; i < this.width; ++i) {
            for(int j = 0; j < this.height; ++j) {
                for(int k = 0; k < this.length; ++k) {
                    block.setPos(i, j, k);
                    this.insertBlock(block);
                }
            }
        }

    }

    public void insertBlock(SchematicBlock block) {
        int x = block.x;
        int y = block.y;
        int z = block.z;
        if (x >= 0 && y >= 0 && z >= 0 && x < this.width && y < this.height && z < this.length) {
            this.blocks[x][y][z] = block;
        }

    }

    public void saveAsTemp() throws IOException {
        this.saveAs(this.getTempFilename());
    }

    public void saveAs(String filename) throws IOException {
        File file = new File("schematics/" + filename);
        Map<String, Tag<?>> schematic = new HashMap();
        int width = this.width;
        int height = this.height;
        int length = this.length;
        byte[] blocks = new byte[width * height * length];
        byte[] data = new byte[width * height * length];
        schematic.put("Width", new ShortTag("Width", (short)width));
        schematic.put("Height", new ShortTag("Height", (short)height));
        schematic.put("Length", new ShortTag("Length", (short)length));

        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                for(int z = 0; z < length; ++z) {
                    int index = y * width * length + z * width + x;
                    blocks[index] = 0;
                    data[index] = 0;
                    SchematicBlock b = this.blocks[x][y][z];
                    if (b != null) {
                        blocks[index] = b.getId();
                        data[index] = b.getVariation();
                    }
                }
            }
        }

        schematic.put("Materials", new StringTag("Materials", "Alpha"));
        schematic.put("Blocks", new ByteArrayTag("Blocks", blocks));
        schematic.put("Data", new ByteArrayTag("Data", data));
        schematic.put("Name", new StringTag("Name", "wow"));
        schematic.put("Author", new StringTag("Author", "me"));
        schematic.put("Created", new StringTag("Created", "2020-12-12"));
        CompoundTag schematicTag = new CompoundTag("", new CompoundMap(schematic));
        NBTOutputStream stream = new NBTOutputStream(new FileOutputStream(file));
        stream.writeTag(schematicTag);
        stream.close();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getLength() {
        return this.length;
    }

    public void setSize(int width, int height, int length) {
        this.width = width;
        this.height = height;
        this.length = length;
        this.blocks = new SchematicBlock[width][height][length];

        for(int i = 0; i < width; ++i) {
            for(int j = 0; j < height; ++j) {
                for(int k = 0; k < length; ++k) {
                    this.blocks[i][j][k] = new SchematicBlock(0, i, j, k);
                }
            }
        }

    }

    public SchematicBlock[][][] getBlocks() {
        return this.blocks;
    }
}
