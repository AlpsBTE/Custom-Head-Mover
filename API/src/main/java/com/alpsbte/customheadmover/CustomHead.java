package com.alpsbte.customheadmover;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public final class CustomHead {
    private final String texture;
    private final String world;
    private final int x, y, z;
    private final String rotation;
    private final boolean isPlacedOnWall;

    public CustomHead(String texture, Location location, BlockFace rotation, boolean isPlacedOnWall) {
        this.texture = texture;
        this.world = location.getWorld().getName();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.rotation = rotation.name();
        this.isPlacedOnWall = isPlacedOnWall;
    }

    public String getB64Texture() {
        return texture;
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getRotation() {
        return rotation;
    }

    public boolean isPlacedOnWall() {
        return isPlacedOnWall;
    }
}
