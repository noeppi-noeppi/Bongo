package io.github.noeppi_noeppi.mods.bongo.compat.jei;

import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class JeiIntegration {

    public static void setBookmarks(Set<ItemStack> stacks, Set<ResourceLocation> advancements) {
        if (!ModList.get().isLoaded("jei")) return;
        try {
            Object bookmarkList = getBookmarkList();
            clearBookmarks(bookmarkList);
            
            for (ItemStack stack : stacks) {
                addBookmark(bookmarkList, VanillaTypes.ITEM_STACK, stack);
            }
            
            if (ModList.get().isLoaded("jea")) {
                Object advancementType = Class.forName("de.melanx.jea.api.client.Jea").getField("ADVANCEMENT_TYPE").get(null);
                for (ResourceLocation advancement : advancements) {
                    addBookmark(bookmarkList, advancementType, getAdvancementIngredient(advancement));
                }
            }
            forceBookmarkUpdate(bookmarkList);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            e.printStackTrace();
        } catch (Throwable t) {
            BongoMod.logger.warn("Could not modify JEI bookmark list: ", t);
        }
    }

    private static Object getBookmarkList() throws ReflectiveOperationException {
        Object jeiRuntime = BongoJeiPlugin.runtime;
        if (jeiRuntime == null) throw new IllegalStateException("JEI runtime not available.");
        Class<?> jeiRuntimeClass = Class.forName("mezz.jei.library.runtime.JeiRuntime");
        Field bookmarkOverlayField = jeiRuntimeClass.getDeclaredField("bookmarkOverlay");
        bookmarkOverlayField.setAccessible(true);
        Object bookmarkOverlay = bookmarkOverlayField.get(jeiRuntime);
        Class<?> bookmarkOverlayClass = Class.forName("mezz.jei.gui.overlay.bookmarks.BookmarkOverlay");
        Field bookmarkListField = bookmarkOverlayClass.getDeclaredField("bookmarkList");
        bookmarkListField.setAccessible(true);
        return bookmarkListField.get(bookmarkOverlay);
    }

    private static void clearBookmarks(Object bookmarkList) throws ReflectiveOperationException {
        Class<?> bookmarkListClass = Class.forName("mezz.jei.gui.bookmarks.BookmarkList");
        Field objectListField = bookmarkListClass.getDeclaredField("list");
        objectListField.setAccessible(true);
        List<?> objectList = (List<?>) objectListField.get(bookmarkList);
        objectList.clear();
        forceBookmarkUpdate(bookmarkList);
    }

    private static void addBookmark(Object bookmarkList, Object ingredientType, Object bookmark) throws ReflectiveOperationException {
        if (bookmark != null) {
            Class<?> bookmarkListClass = Class.forName("mezz.jei.gui.bookmarks.BookmarkList");
            Field ingredientmanagerField = bookmarkListClass.getDeclaredField("ingredientManager");
            ingredientmanagerField.setAccessible(true);
            Object ingredientManager = ingredientmanagerField.get(bookmarkList);
            Class<?> typedIngredientClass = Class.forName("mezz.jei.library.ingredients.TypedIngredient");
            Method createMethod = typedIngredientClass.getDeclaredMethod("createAndFilterInvalid", IIngredientManager.class, IIngredientType.class, Object.class);
            createMethod.setAccessible(true);
            //noinspection JavaReflectionInvocation,unchecked
            Optional<Object> typedIngredient = (Optional<Object>) createMethod.invoke(null, ingredientManager, ingredientType, bookmark);
            if (typedIngredient.isPresent()) {
                Method addMethod = bookmarkListClass.getDeclaredMethod("add", ITypedIngredient.class);
                addMethod.setAccessible(true);
                try {
                    //noinspection JavaReflectionInvocation
                    addMethod.invoke(bookmarkList, typedIngredient.get());
                } catch (Exception e) {
                    // Something seems to throw an exception here occasionally.
                    // Ignore it, so the other elements can be added as well.
                }
            } else {
                BongoMod.logger.warn("Failed to create JEI ingredient of bookmark object: " + bookmark);
            }
        }
    }

    // forces an update. clear will do this automatically.
    private static void forceBookmarkUpdate(Object bookmarkList) throws ReflectiveOperationException {
        Class<?> bookmarkListClass = Class.forName("mezz.jei.gui.bookmarks.BookmarkList");
        Method notifyListenersOfChangeMethod = bookmarkListClass.getDeclaredMethod("notifyListenersOfChange");
        notifyListenersOfChangeMethod.setAccessible(true);
        try {
            notifyListenersOfChangeMethod.invoke(bookmarkList);
        } catch (Exception e) {
            // Something seems to throw an exception here occasionally.
            // Ignore it, so the other update work is done.
        }
        Field bookmarkConfigField = bookmarkListClass.getDeclaredField("bookmarkConfig");
        bookmarkConfigField.setAccessible(true);
        Object bookmarkConfig = bookmarkConfigField.get(bookmarkList);
        Field ingredientmanagerField = bookmarkListClass.getDeclaredField("ingredientManager");
        ingredientmanagerField.setAccessible(true);
        Object ingredientManager = ingredientmanagerField.get(bookmarkList);
        Field ingredientListField = bookmarkListClass.getDeclaredField("list");
        ingredientListField.setAccessible(true);
        Object ingredientList = ingredientListField.get(bookmarkList);
        Class<?> bookmarkConfigClass = Class.forName("mezz.jei.gui.config.BookmarkConfig");
        Method saveBookmarksMethod = bookmarkConfigClass.getDeclaredMethod("saveBookmarks", IIngredientManager.class, List.class);
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
