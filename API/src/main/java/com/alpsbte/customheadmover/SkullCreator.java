package com.alpsbte.customheadmover;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;

public abstract class SkullCreator {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Field profileField;
    public static File headsFile;
    public static int pasteOffset = 0;

    protected abstract void setSkull(CustomHead customHead, Block block);
    protected abstract CustomHead getSkull(Block block);
    protected abstract ArrayList<CustomHead> getSkulls(Region region);

    protected void saveSkulls(Player player) {
        if (getWorldEditSelection(player) != null) {
            try {
                ArrayList<CustomHead> heads = getSkulls(getWorldEditSelection(player));
                if (saveHeadsToFile(heads)) {
                    player.sendMessage(Utils.getInfoMessageFormat("Successfully saved §6" + heads.size() + " §aheads!"));
                    return;
                }
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            }
            player.sendMessage(Utils.getErrorMessageFormat("An error occurred while saving heads! Please try again!"));
        } else player.sendMessage(Utils.getErrorMessageFormat("Please select a WorldEdit region!"));
    }

    protected void loadSkulls(Player player) {
        try {
            ArrayList<CustomHead> heads = loadHeadsFromFile();
            for (CustomHead head : heads) {
                setSkull(head, player.getWorld().getBlockAt(head.getX(), head.getY() + pasteOffset, head.getZ()));
            }
            player.sendMessage(Utils.getInfoMessageFormat("Successfully loaded §6" + heads.size() + " §aheads!"));
            return;
        } catch (FileNotFoundException ex) {
            Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
        }
        player.sendMessage(Utils.getErrorMessageFormat("An error occurred while saving heads! Please try again!"));
    }

    /**
     * Creates a player profile for the skull
     * @param b64 base64 texture as String
     * @return game profile with given skin texture
     */
    private static GameProfile createProfile(String b64) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", b64));
        return profile;
    }

    /**
     * Sets the given texture to the skull block
     * @param skull skull block to update the texture
     * @param b64 base64 texture as String
     */
    public static void setTexture(Skull skull, String b64) {
        try {
            if (profileField == null) {
                profileField = skull.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
            }
            profileField.set(skull, createProfile(b64));
            skull.update(true, false);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while updating texture of skull!", ex);
        }
    }

    /**
     * Receives the texture of the given skull block
     * @param skull skull block to receive the texture
     * @return base64 texture as String
     */
    public static String getTexture(Skull skull) {
        try {
            if (profileField == null) {
                profileField = skull.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
            }
            GameProfile gameProfile = (GameProfile) profileField.get(skull);
            Collection<Property> properties = gameProfile.getProperties().get("textures");
            return properties.iterator().next().getValue();
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while updating texture of skull!", ex);
        }
        return null;
    }

    /**
     * Encodes the skin URL to Base64
     * @param url texture URL - for example: <a href="http://textures.minecraft.net/texture/52284e132bfd659bc6ada497c4fa3094cd93231a6b505a12ce7cd5135ba8ff93">...</a>
     * @return base64 as String - for example: aHR0cHM6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUve1widGV4dHVyZXNcIjp7XCJTS0lOXCI6e1widXJsXCI6XCJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzUyMjg0ZTEzMmJmZDY1OWJjNmFkYTQ5N2M0ZmEzMDk0Y2Q5MzIzMWE2YjUwNWExMmNlN2NkNTEzNWJhOGZmOTNcIn19fQ==
     */
    public static String encodeTexture(String url) {
        try {
            URI textureURL = new URI(url);
            return new String(Base64.getEncoder()
                    .encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", textureURL).getBytes(StandardCharsets.UTF_8)));
        } catch (URISyntaxException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while encoding texture!", ex);
        }
        return null;
    }

    /**
     * Decodes Base64 to the skin URL
     * @param b64 base64 as String - for example: aHR0cHM6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUve1widGV4dHVyZXNcIjp7XCJTS0lOXCI6e1widXJsXCI6XCJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzUyMjg0ZTEzMmJmZDY1OWJjNmFkYTQ5N2M0ZmEzMDk0Y2Q5MzIzMWE2YjUwNWExMmNlN2NkNTEzNWJhOGZmOTNcIn19fQ==
     * @return texture URL - for example: <a href="http://textures.minecraft.net/texture/52284e132bfd659bc6ada497c4fa3094cd93231a6b505a12ce7cd5135ba8ff93">...</a>
     */
    public static String decodeTexture(String b64) {
        try {
            return new String(Base64.getDecoder().decode(b64));
        } catch (IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while decoding texture!", ex);
        }
        return null;
    }

    /**
     * Saves the custom heads to a JSON file
     * @param customHeads list of the custom heads
     * @return true if was successful
     * @throws IOException file writing exception
     */
    private static boolean saveHeadsToFile(ArrayList<CustomHead> customHeads) throws IOException {
        if (headsFile.exists() || headsFile.createNewFile()) {
            try (Writer writer = new FileWriter(headsFile)) {
                gson.toJson(customHeads, writer);
            }
            return true;
        }
        return false;
    }

    /**
     * Loads the custom heads from the JSON file
     * @return list of the custom heads
     * @throws FileNotFoundException if JSON file was not found
     */
    private static ArrayList<CustomHead> loadHeadsFromFile() throws FileNotFoundException {
        return gson.fromJson(new FileReader(headsFile), new TypeToken<ArrayList<CustomHead>>(){}.getType());
    }

    /**
     * Gets the player WorldEdit selection
     * @param player bukkit player
     * @return WorldEdit selection as region
     */
    public static Region getWorldEditSelection(Player player) {
        try {
            return WorldEdit.getInstance().getSessionManager().findByName(player.getName()).getSelection(
                    WorldEdit.getInstance().getSessionManager().findByName(player.getName()).getSelectionWorld());
        } catch (IncompleteRegionException | NullPointerException ignore) {}
        return null;
    }
}
