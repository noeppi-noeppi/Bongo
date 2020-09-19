package io.github.noeppi_noeppi.mods.bongo.util;

import com.google.gson.*;
import net.minecraft.nbt.*;

public class NbtToJson {

    public static JsonElement getJson(INBT nbt, boolean byteToBool) {
        if (nbt instanceof EndNBT) {
            return JsonNull.INSTANCE;
        } else if (nbt instanceof CompoundNBT) {
            JsonObject obj = new JsonObject();
            for (String key : ((CompoundNBT) nbt).keySet())
                obj.add(key, getJson(((CompoundNBT) nbt).get(key), byteToBool));
            return obj;
        } else if (nbt instanceof ListNBT) {
            JsonArray arr = new JsonArray();
            for (int i = 0; i < ((ListNBT) nbt).size(); i++)
                arr.add(getJson(((ListNBT) nbt).get(i), byteToBool));
            return arr;
        } else if (nbt instanceof ByteNBT) {
            if (byteToBool && (((ByteNBT) nbt).getByte() == 0 || ((ByteNBT) nbt).getByte() == 1)) {
                return ((ByteNBT) nbt).getByte() == 0 ? new JsonPrimitive(false) : new JsonPrimitive(true);
            } else {
                return new JsonPrimitive(((ByteNBT) nbt).getByte());
            }
        } else if (nbt instanceof DoubleNBT) {
            return new JsonPrimitive(((DoubleNBT) nbt).getDouble());
        } else if (nbt instanceof FloatNBT) {
            return new JsonPrimitive(((FloatNBT) nbt).getFloat());
        } else if (nbt instanceof IntNBT) {
            return new JsonPrimitive(((IntNBT) nbt).getInt());
        } else if (nbt instanceof LongNBT) {
            return new JsonPrimitive(((LongNBT) nbt).getLong());
        } else if (nbt instanceof ShortNBT) {
            return new JsonPrimitive(((ShortNBT) nbt).getShort());
        } else if (nbt instanceof StringNBT) {
            return new JsonPrimitive(nbt.getString());
        } else if (nbt instanceof ByteArrayNBT) {
            JsonArray arr = new JsonArray();
            for (byte b : ((ByteArrayNBT) nbt).getByteArray())
                arr.add(b);
            return arr;
        } else if (nbt instanceof IntArrayNBT) {
            JsonArray arr = new JsonArray();
            for (int i : ((IntArrayNBT) nbt).getIntArray())
                arr.add(i);
            return arr;
        } else if (nbt instanceof LongArrayNBT) {
            JsonArray arr = new JsonArray();
            for (long l : ((LongArrayNBT) nbt).getAsLongArray())
                arr.add(l);
            return arr;
        } else  {
            throw new IllegalArgumentException("NBT type unknown: " + nbt.getClass());
        }
    }
}
