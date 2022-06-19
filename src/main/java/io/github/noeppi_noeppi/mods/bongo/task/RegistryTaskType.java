package io.github.noeppi_noeppi.mods.bongo.task;


import com.mojang.serialization.MapCodec;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Comparator;
import java.util.Objects;

public abstract class RegistryTaskType<T> implements TaskType<T> {
    
    private final Class<T> cls;
    private final IForgeRegistry<T> registry;

    protected RegistryTaskType(Class<T> cls, IForgeRegistry<T> registry) {
        this.cls = cls;
        this.registry = registry;
    }

    @Override
    public Class<T> taskClass() {
        return this.cls;
    }

    @Override
    public MapCodec<T> codec() {
        return this.registry.getCodec().fieldOf("value");
    }

    @Override
    public Comparator<T> order() {
        return Comparator.comparing(this.registry::getKey, Util.COMPARE_RESOURCE);
    }

    @Override
    public boolean shouldComplete(ServerPlayer player, T element, T compare) {
        return Objects.equals(element, compare);
    }
}
