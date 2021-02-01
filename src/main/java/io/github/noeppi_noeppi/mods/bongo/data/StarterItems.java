package io.github.noeppi_noeppi.mods.bongo.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class StarterItems {
    private static final ResourceLocation DATA_LOCATION = new ResourceLocation(BongoMod.getInstance().modid, "starter_items.json");
    private static final Set<ItemStack> ITEMS = new HashSet<>();

    public static void give(PlayerEntity player) {
        World world = player.world;
        ITEMS.forEach(item -> {
            if (!player.inventory.addItemStackToInventory(item.copy())) {
                world.addEntity(new ItemEntity(world, player.getPosX(), player.getPosY(), player.getPosZ(), item.copy()));
            }
        });
    }

    public static void loadItems(IResourceManager rm) throws IOException {
        ITEMS.clear();

        IResource location = rm.getResource(DATA_LOCATION);
        String string = IOUtils.toString(new InputStreamReader(location.getInputStream(), StandardCharsets.UTF_8));
        JsonObject json = JSONUtils.fromJson(string);

        if (json.has("items")) {
            JsonArray items = json.getAsJsonArray("items");
            items.forEach(item -> {
                ItemStack stack = CraftingHelper.getItemStack((JsonObject) item, true);
                ITEMS.add(stack);
            });
        }
    }
}
