package xyz.yuanzhi.photo.gui;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.Comparator;

public enum PhotoPosition {
    LEFT_BOTTOM(0, "photo.option.position.left_bottom"),
    LEFT_CENTER(1, "photo.option.position.left_center"),
    LEFT_TOP(2, "photo.option.position.left_top"),
    CENTER_BOTTOM(3, "photo.option.position.center_bottom"),
    CENTER(4, "photo.option.position.center"),
    CENTER_TOP(5, "photo.option.position.center_top"),
    RIGHT_BOTTOM(6, "photo.option.position.right_bottom"),
    RIGHT_CENTER(7, "photo.option.position.right_center"),
    RIGHT_TOP(8, "photo.option.position.right_top");
    

    private static final PhotoPosition[] VALUES = Arrays.stream(values()).sorted(Comparator.
            comparingInt(PhotoPosition::getId)).toArray(PhotoPosition[]::new);
    private final int id;
    private final String translationKey;

    PhotoPosition(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    public int getId() {
        return this.id;
    }

    public MutableText getTranslationText() {
        return Text.translatable(this.translationKey);
    }

    public static PhotoPosition byId(int id) {
        return VALUES[MathHelper.floorMod(id, VALUES.length)];
    }
}
