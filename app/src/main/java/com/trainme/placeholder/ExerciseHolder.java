package com.trainme.placeholder;

import android.content.res.Resources;

import com.trainme.R;
import com.trainme.api.model.Cycle;
import com.trainme.api.model.Exercise;

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
public class ExerciseHolder {

    /**
     * An array of sample (placeholder) items.
     */
    public static final List<Exercise> ITEMS = new ArrayList<>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<Integer, Exercise> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 20;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createPlaceholderItem(i));
        }
    }

    private static void addItem(Exercise item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getOrder(), item);
    }

    private static Exercise createPlaceholderItem(int position) {
        return new Exercise(position, 3, 1, null);  // TODO: Ver como manejar el Object exercise que viene de la API
    }

}