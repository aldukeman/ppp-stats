package ppp.stats.models;

import java.util.Arrays;
import java.util.Optional;

public interface ITextChannel {
    public enum Type {
        UNKNOWN(-1),
        DM(0),
        GROUP_DM(1),
        CHANNEL(2);

        final private int value;

        Type(int value) {
            this.value = value;
        }

        public static Optional<Type> valueOf(int value) {
            return Arrays.stream(Type.values())
                .filter(t -> t.value == value)
                .findFirst();
        }
    }

    String getName();
    long getId();
    Type getType();
}
