package ru.hiddenalt.mtbe.schematic;

import net.minecraft.block.Block;
import net.minecraft.datafixer.fix.ItemIdFix;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SchematicBlock {
    protected int id;
    protected int variation;
    protected int x;
    protected int y;
    protected int z;
    protected Identifier identifier;

    public SchematicBlock(int id, int x, int y, int z) {
        this(id);
        this.setPos(x, y, z);
    }

    public SchematicBlock(int id) {
        this(Identifier.tryParse(ItemIdFix.fromId(id)));
    }

    public SchematicBlock(Identifier identifier) {
        this.id = 0;
        this.variation = 0;
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.identifier = identifier;
        // TODO: remove id
        this.id = (byte)0;
    }

    public int getId() {
        return this.id;
    }

    public int getVariation() {
        return this.variation;
    }

    public Block getBlockInstance() {
        return (Block)Registry.BLOCK.get(this.identifier);
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public void setPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
