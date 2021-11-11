package com.trainme.placeholder;

import android.content.res.Resources;

import com.trainme.R;

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
public class PlaceholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    public static final List<PlaceholderItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<Integer, PlaceholderItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createPlaceholderItem(i));
        }
    }

    private static void addItem(PlaceholderItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static PlaceholderItem createPlaceholderItem(int position) {
        return new PlaceholderItem(position, "Routine Name", "Routine Description", R.drawable.ic_baseline_account_circle_24, 1000000);
    }

    /**
     * A placeholder item representing a piece of content.
     */
    public static class PlaceholderItem {
        public final String name;
        public final Integer id;
        public final String description;
        public final int imageSrc;
        public final int color;

        public PlaceholderItem(Integer id, String name, String description, int ImageSrc, int color) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.imageSrc = ImageSrc;
            this.color = color;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}