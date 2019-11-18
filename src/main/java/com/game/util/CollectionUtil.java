package com.game.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class CollectionUtil {
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;

    }

    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;

    }

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;

    }

    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;

    }

    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;

    }

    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;

    }

    public static <T> T[] toArrayWithShallowCopy(Collection<T> collection, T[] contents) {
        if (isEmpty(collection) || contents == null) {
            return null;
        }

        final int collectionSize = collection.size();
        final int contentsSize = contents.length;
        if (collectionSize > contentsSize) {
            @SuppressWarnings("unchecked") T[] result =
                    (T[]) Array.newInstance(contents.getClass().getComponentType(), collectionSize);
            contents = result;
        }

        int index = 0;
        Iterator<T> iter = collection.iterator();
        while (iter.hasNext() && index < contents.length) {
            contents[index++] = iter.next();
        }

        return contents;
    }
}
