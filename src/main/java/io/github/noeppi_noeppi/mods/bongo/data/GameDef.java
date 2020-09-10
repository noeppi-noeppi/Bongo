package io.github.noeppi_noeppi.mods.bongo.data;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GameDef {

    public static final Map<ResourceLocation, GameDef> GAMES = new HashMap<>();

    public final ResourceLocation id;
    private final WinCondition winCondition;
    private final List<Pair<Integer, Task>> tasks;
    private final int totalWeight;
    private final CompoundNBT nbt;

    public GameDef(ResourceLocation id, CompoundNBT nbt) {
        this.id = id;
        this.tasks = new ArrayList<>();
        int totalWeight = 0;
        if (nbt.contains("tasks", Constants.NBT.TAG_LIST)) {
            ListNBT taskList = nbt.getList("tasks", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < taskList.size(); i++) {
                CompoundNBT compound = taskList.getCompound(i);
                int weight = compound.getInt("weight");
                if (weight <= 0) weight = 1;

                Task task = Task.empty();
                task.deserializeNBT(compound);
                tasks.add(Pair.of(weight, task));
                totalWeight += weight;
            }
        }
        if (nbt.contains("winCondition", Constants.NBT.TAG_STRING)) {
            winCondition = WinCondition.getWin(nbt.getString("winCondition"));
        } else {
            winCondition = WinCondition.DEFAULT;
        }
        this.totalWeight = totalWeight;
        this.nbt = nbt;
    }

    public String createBongo(Bongo bongo) {
        if (bongo.running())
            bongo.stop();
        if (tasks.size() < 25)
            return "bongo.cmd.create.less";

        Random random = new Random();
        int weightLeft = totalWeight;
        List<Task> theTasks = new ArrayList<>();
        while (theTasks.size() < 25) {
            int rand = random.nextInt(weightLeft);
            int weightCounted = 0;
            for (Pair<Integer, Task> pair : tasks) {
                if (!theTasks.contains(pair.getRight())) {
                    weightCounted += pair.getLeft();
                    if (weightCounted > rand) {
                        theTasks.add(pair.getRight());
                        weightLeft -= pair.getLeft();
                        break;
                    }
                }
            }
        }
        bongo.setWin(winCondition, true);
        bongo.setTasks(theTasks);
        return null;
    }

    public CompoundNBT getNbt() {
        return nbt.copy();
    }

    public static void loadGameDefs(IResourceManager rm) throws IOException {
        GAMES.clear();

        Collection<ResourceLocation> ids = rm.getAllResourceLocations("bingos", file -> file.endsWith(".json"));

        for (ResourceLocation id : ids) {
            String realPath;
            if (id.getPath().contains("/")) {
                realPath = id.getPath().substring(id.getPath().lastIndexOf('/') + 1);
            } else {
                realPath = id.getPath();
            }
            if (realPath.endsWith(".json"))
                realPath = realPath.substring(0, realPath.length() - 5);

            ResourceLocation realId = new ResourceLocation(id.getNamespace(), realPath);

            IResource resource = rm.getResource(id);

            String string = IOUtils.toString(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            CompoundNBT nbt;
            try {
                nbt = JsonToNBT.getTagFromJson(string);
            } catch (CommandSyntaxException e) {
                throw new IOException("Could not read JSON-NBT: " + e.getMessage(), e);
            }
            GAMES.put(realId, new GameDef(realId, nbt));
        }
    }
}
