package io.github.noeppi_noeppi.mods.bongo.compat;

import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JeiIntegration {

    public static void reloadJeiTooltips() {
        if (Minecraft.getInstance().getResourceManager() instanceof SimpleReloadableResourceManager) {
            SimpleReloadableResourceManager resourceManager = (SimpleReloadableResourceManager) Minecraft.getInstance().getResourceManager();
            try {
                Class<?> c = Class.forName("mezz.jei.startup.ClientLifecycleHandler$JeiReloadListener");
                for (IFutureReloadListener listener : resourceManager.reloadListeners) {
                    if (listener instanceof ISelectiveResourceReloadListener && c.isInstance(listener)) {
                        ((ISelectiveResourceReloadListener) listener).onResourceManagerReload(resourceManager);
                    }
                }
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                //
            } catch (Throwable t) {
                BongoMod.getInstance().logger.warn("Could not reload JEI item list: ", t);
            }
        }
    }
    
    public static void setBookmarks(Stream<ItemStack> stacks) {
        try {
            Object bookmarkList = getBookmarkList();
            clearBookmarks(bookmarkList);
            for (ItemStack stack : stacks.collect(Collectors.toSet())) {
                addBookmark(bookmarkList, stack);
            }
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            //
        } catch (Throwable t) {
            BongoMod.getInstance().logger.warn("Could not modify JEI bookmark list: ", t);
        }
    }
    
    private static Object getBookmarkList() throws ReflectiveOperationException {
        Class<?> internalClass = Class.forName("mezz.jei.Internal");
        Field inputHandlerField = internalClass.getDeclaredField("inputHandler");
        inputHandlerField.setAccessible(true);
        Object inputHandler = inputHandlerField.get(null);
        Class<?> inputHandlerClass = Class.forName("mezz.jei.input.InputHandler");
        Field bookmarkListField = inputHandlerClass.getDeclaredField("bookmarkList");
        bookmarkListField.setAccessible(true);
        return bookmarkListField.get(inputHandler);
    }
    
    private static void clearBookmarks(Object bookmarkList) throws ReflectiveOperationException {
        Class<?> bookmarkListClass = Class.forName("mezz.jei.bookmarks.BookmarkList");
        Field objectListField = bookmarkListClass.getDeclaredField("list");
        objectListField.setAccessible(true);
        List<?> objectList = (List<?>) objectListField.get(bookmarkList);
        objectList.clear();
        Field ingredientListField = bookmarkListClass.getDeclaredField("ingredientListElements");
        ingredientListField.setAccessible(true);
        List<?> ingredientList = (List<?>) ingredientListField.get(bookmarkList);
        ingredientList.clear();
        forceBookmarkUpdate(bookmarkList);
    }
    
    private static void addBookmark(Object bookmarkList, ItemStack stack) throws ReflectiveOperationException {
        Class<?> bookmarkListClass = Class.forName("mezz.jei.bookmarks.BookmarkList");
        Method addMethod = bookmarkListClass.getDeclaredMethod("add", Object.class);
        addMethod.setAccessible(true);
        addMethod.invoke(bookmarkList, stack);
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
        Field ingredientListField = bookmarkListClass.getDeclaredField("ingredientListElements");
        ingredientListField.setAccessible(true);
        Object ingredientList = ingredientListField.get(bookmarkList);
        Class<?> bookmarkConfigClass = Class.forName("mezz.jei.config.BookmarkConfig");
        Method saveBookmarksMethod = bookmarkConfigClass.getDeclaredMethod("saveBookmarks", Class.forName("mezz.jei.api.runtime.IIngredientManager"), List.class);
        saveBookmarksMethod.setAccessible(true);
        //noinspection JavaReflectionInvocation
        saveBookmarksMethod.invoke(bookmarkConfig, ingredientManager, ingredientList);
    }
}
