package com.trainme.placeholder;

import android.content.res.Resources;

import com.trainme.R;
import com.trainme.api.model.Cycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class CycleHolders {

    /**
     * An array of sample (placeholder) items.
     */
    public static final List<Cycle> ITEMS = new ArrayList<>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<Integer, Cycle> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createPlaceholderItem(i));
        }
    }

    private static void addItem(Cycle item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getOrder(), item);
    }

    private static Cycle createPlaceholderItem(int position) {
        return new Cycle("Name", "Warmup", position, 3);
    }

}