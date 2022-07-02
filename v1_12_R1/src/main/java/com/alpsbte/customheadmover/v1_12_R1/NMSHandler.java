package com.alpsbte.customheadmover.v1_12_R1;

import com.alpsbte.customheadmover.CustomHead;
import com.alpsbte.customheadmover.INMSHandler;
import com.alpsbte.customheadmover.SkullCreator;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;

public class NMSHandler extends SkullCreator implements INMSHandler {
    @Override
    public void saveCustomHeads(Player player) {
        this.saveSkulls(player);
    }

    @Override
    public void loadCustomHeads(Player player) {
        this.loadSkulls(player);
    }

    @Override
    protected void setSkull(CustomHead customHead, Block block) {
        block.setType(Material.SKULL);
        Skull skull = (Skull) block.getState();
        org.bukkit.material.Skull skullMaterial = (org.bukkit.material.Skull) skull.getData();
        BlockFace facingDirection = BlockFace.valueOf(customHead.getRotation());
        if (customHead.isPlacedOnWall()) {
            skullMaterial.setFacingDirection(facingDirection);
        } else skull.setRotation(facingDirection);
        skull.setSkullType(SkullType.PLAYER);
        setTexture(skull, customHead.getB64Texture());
        skull.update(true, false);
    }

    @Override
    protected CustomHead getSkull(Block block) {
        Skull skull = (Skull) block.getState();
        org.bukkit.material.Skull skullMaterial = (org.bukkit.material.Skull) skull.getData();
        boolean isPlacedOnWall = skullMaterial.getFacing() != BlockFace.SELF;
        return new CustomHead(getTexture(skull), block.getLocation(), isPlacedOnWall ? skullMaterial.getFacing() : skull.getRotation(), isPlacedOnWall);
    }

    @Override
    protected ArrayList<CustomHead> getSkulls(Region region) {
        World world = Bukkit.getWorld(Objects.requireNonNull(region.getWorld()).getName());
        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();

        ArrayList<CustomHead> customHeads = new ArrayList<>();
        for (int i = min.getBlockX(); i <= max.getBlockX(); i++) {
            for (int j = min.getBlockY(); j <= max.getBlockY(); j++) {
                for (int k = min.getBlockZ(); k <= max.getBlockZ(); k++) {
                    Block block = world.getBlockAt(i, j, k);
                    if (block.getType() == Material.SKULL) {
                        customHeads.add(getSkull(block));
                    }
                }
            }
        }
        return customHeads;
    }
}
