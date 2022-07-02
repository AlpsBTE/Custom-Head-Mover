package com.alpsbte.customheadmover.v1_18_R2;

import com.alpsbte.customheadmover.CustomHead;
import com.alpsbte.customheadmover.INMSHandler;
import com.alpsbte.customheadmover.SkullCreator;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
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
    public void setSkull(CustomHead customHead, Block block) {
        block.setType(customHead.isPlacedOnWall() ? Material.PLAYER_WALL_HEAD : Material.PLAYER_HEAD);
        Skull skull = (Skull) block.getState();
        setTexture(skull, customHead.getB64Texture());
        skull.setRotation(BlockFace.valueOf(customHead.getRotation()));
        skull.update(true, false);
    }

    @Override
    protected CustomHead getSkull(Block block) {
        Skull skull = (Skull) block.getState();
        return new CustomHead(getTexture(skull), block.getLocation(), skull.getRotation(), block.getType() == Material.PLAYER_WALL_HEAD);
    }

    @Override
    protected ArrayList<CustomHead> getSkulls(Region region) {
        World world = Bukkit.getWorld(Objects.requireNonNull(region.getWorld()).getName());
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();

        ArrayList<CustomHead> customHeads = new ArrayList<>();
        for (int i = min.getBlockX(); i <= max.getBlockX(); i++) {
            for (int j = min.getBlockY(); j <= max.getBlockY(); j++) {
                for (int k = min.getBlockZ(); k <= max.getBlockZ(); k++) {
                    Block block = world.getBlockAt(i, j, k);
                    if (block.getType() == Material.PLAYER_HEAD || block.getType() == Material.PLAYER_WALL_HEAD) {
                        customHeads.add(getSkull(block));
                    }
                }
            }
        }
        return customHeads;
    }
}
