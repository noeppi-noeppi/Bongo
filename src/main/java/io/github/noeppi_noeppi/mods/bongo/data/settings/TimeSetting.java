package io.github.noeppi_noeppi.mods.bongo.data.settings;

import com.mojang.serialization.*;

import java.util.OptionalInt;
import java.util.stream.Stream;

public sealed interface TimeSetting {
    
    boolean limited();
    OptionalInt limit();
    
    record Time(int time) implements TimeSetting {

        @Override
        public boolean limited() {
            return true;
        }

        @Override
        public OptionalInt limit() {
            return OptionalInt.of(this.time());
        }
    }
    
    record Unlimited() implements TimeSetting {
        
        @Override
        public boolean limited() {
            return false;
        }

        @Override
        public OptionalInt limit() {
            return OptionalInt.empty();
        }
    }

    static MapCodec<TimeSetting> fieldOf(String name) {
        return new MapCodec<>() {

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.of(ops.createString(name));
            }

            @Override
            public <T> DataResult<TimeSetting> decode(DynamicOps<T> ops, MapLike<T> input) {
                T elem = input.get(name);
                if (elem == null) {
                    return DataResult.error(() -> "Missing key: " + name);
                } else {
                    DataResult<Number> num = ops.getNumberValue(elem);
                    if (num.result().isPresent()) {
                        return num.map(n -> new Time(n.intValue()));
                    } else {
                        return ops.getStringValue(elem).flatMap(str -> {
                            if ("unlimited".equalsIgnoreCase(str)) {
                                return DataResult.success(new Unlimited());
                            } else try {
                                return DataResult.success(new Time(Integer.parseInt(str)));
                            } catch (NumberFormatException e) {
                                return DataResult.error(() -> "Invalid time value: " + str);
                            }
                        });
                    }
                }
            }

            @Override
            public <T> RecordBuilder<T> encode(TimeSetting input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                if (input instanceof Time time) {
                    prefix.add(name, ops.createInt(time.time()));
                } else {
                    prefix.add(name, ops.createString("unlimited"));
                }
                return prefix;
            }
        };
    }
}
