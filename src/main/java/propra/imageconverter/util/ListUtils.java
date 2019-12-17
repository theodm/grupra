package propra.imageconverter.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ListUtils {
    private ListUtils() {

    }

    public static <T> boolean containsDuplicates(List<T> list) {
        // Wir fügen alles in ein Set hinzu,
        // wenn ein Element nicht hinzugefügt werden
        // kann, bedeutet das, es existiert doppelt.
        Set<T> set = new HashSet<T>();

        for (T obj : list)
            if (!set.add(obj))
                return true;

        return false;
    }
}
