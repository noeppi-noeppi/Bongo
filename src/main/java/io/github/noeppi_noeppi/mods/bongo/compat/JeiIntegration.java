package io.github.noeppi_noeppi.mods.bongo.compat;

import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class JeiIntegration {

    public static void reloadJeiTooltips() {
        try {
            Class<?> internalClass = Class.forName("mezz.jei.Internal");
            Method getReloadListener = internalClass.getDeclaredMethod("getReloadListener");
            getReloadListener.setAccessible(true);
            Object reloadListener = getReloadListener.invoke(null);
            if (reloadListener instanceof ResourceManagerReloadListener listener) {
                listener.onResourceManagerReload(Minecraft.getInstance().getResourceManager());
            }
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            //
        } catch (Throwable t) {
            BongoMod.getInstance().logger.warn("Could not reload JEI item list: ", t);
        }
    }

    public static void setBookmarks(Set<ItemStack> stacks, Set<ResourceLocation> advancements) {
        try {
            Object bookmarkList = getBookmarkList();
            clearBookmarks(bookmarkList);
            for (ItemStack stack : stacks) {
                addBookmark(bookmarkList, stack);
            }
            for (ResourceLocation advancement : advancements) {
                addBookmark(bookmarkList, getAdvancementIngredient(advancement));
            }
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            //
        } catch (Throwable t) {
            BongoMod.getInstance().logger.warn("Could not modify JEI bookmark list: ", t);
        }
    }

    private static Object getBookmarkList() throws ReflectiveOperationException {
        Class<?> internalClass = Class.forName("mezz.jei.Internal");
        Field jeiRuntimeField = internalClass.getDeclaredField("runtime");
        jeiRuntimeField.setAccessible(true);
        Object jeiRuntime = jeiRuntimeField.get(null);
        Class<?> jeiRuntimeClass = Class.forName("mezz.jei.runtime.JeiRuntime");
        Field bookmarkOverlayField = jeiRuntimeClass.getDeclaredField("bookmarkOverlay");
        bookmarkOverlayField.setAccessible(true);
        Object bookmarkOverlay = bookmarkOverlayField.get(jeiRuntime);
        Class<?> bookmarkOverlayClass = Class.forName("mezz.jei.gui.overlay.bookmarks.BookmarkOverlay");
        Field bookmarkListField = bookmarkOverlayClass.getDeclaredField("bookmarkList");
        bookmarkListField.setAccessible(true);
        return bookmarkListField.get(bookmarkOverlay);
    }

    private static void clearBookmarks(Object bookmarkList) throws ReflectiveOperationException {
        Class<?> bookmarkListClass = Class.forName("mezz.jei.bookmarks.BookmarkList");
        Field objectListField = bookmarkListClass.getDeclaredField("list");
        objectListField.setAccessible(true);
        List<?> objectList = (List<?>) objectListField.get(bookmarkList);
        objectList.clear();
        forceBookmarkUpdate(bookmarkList);
    }

    private static void addBookmark(Object bookmarkList, Object bookmark) throws ReflectiveOperationException {
        if (bookmark != null) {
            Class<?> bookmarkListClass = Class.forName("mezz.jei.bookmarks.BookmarkList");
            Method addMethod = bookmarkListClass.getDeclaredMethod("add", Object.class);
            addMethod.setAccessible(true);
            addMethod.invoke(bookmarkList, bookmark);
        }
    }

    // forces an update. add and clear will do this automatically.
    private static void forceBookmarkUpdate(Object bookmarkList) throws ReflectiveOperationException {
        Class<?> bookmarkListClass = Class.forName("mezz.jei.bookmarks.BookmarkList");
        Method notifyListenersOfChangeMethod = bookmarkListClass.getDeclaredMethod("notifyListenersOfChange");
        notifyListenersOfChangeMethod.setAccessible(true);
        notifyListenersOfChangeMethod.invoke(bookmarkList);
        Field bookmarkConfigField = bookmarkListClass.getDeclaredField("bookmarkConfig");
        bookmarkConfigField.setAccessible(true);
        Object bookmarkConfig = bookmarkConfigField.get(bookmarkList);
        Field ingredientManagerField = bookmarkListClass.getDeclaredField("ingredientManager");
        ingredientManagerField.setAccessible(true);
        Object ingredientManager = ingredientManagerField.get(bookmarkList);
        Field ingredientListField = bookmarkListClass.getDeclaredField("list");
        ingredientListField.setAccessible(true);
        Object ingredientList = ingredientListField.get(bookmarkList);
        Class<?> bookmarkConfigClass = Class.forName("mezz.jei.config.BookmarkConfig");
        Method saveBookmarksMethod = bookmarkConfigClass.getDeclaredMethod("saveBookmarks", Class.forName("mezz.jei.api.runtime.IIngredientManager"), List.class);
        saveBookmarksMethod.setAccessible(true);
        //noinspection JavaReflectionInvocation
        saveBookmarksMethod.invoke(bookmarkConfig, ingredientManager, ingredientList);
    }
    
    @Nullable
    private static Object getAdvancementIngredient(ResourceLocation id) throws ReflectiveOperationException {
        if (!ModList.get().isLoaded("jea")) {
            return null;
        } else {
            Class<?> jeaClass = Class.forName("de.melanx.jea.api.client.Jea");
            Method getAdvancementMethod = jeaClass.getDeclaredMethod("getAdvancement", ResourceLocation.class);
            getAdvancementMethod.setAccessible(true);
            return getAdvancementMethod.invoke(null, id);
        }
    }
}
