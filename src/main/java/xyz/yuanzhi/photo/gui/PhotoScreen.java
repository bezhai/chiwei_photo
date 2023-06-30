package xyz.yuanzhi.photo.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import xyz.yuanzhi.photo.Photo;
import xyz.yuanzhi.photo.block.PhotoEntity;
import xyz.yuanzhi.photo.utils.PictureDownloader;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class PhotoScreen extends Screen {

    private static final Text URL_TEXT = Text.translatable("text.photo.photo_url");
    private static final Text WIDTH = Text.translatable("text.photo.width");
    private static final Text HEIGHT = Text.translatable("text.photo.height");
    private static final Text X_OFF = Text.translatable("text.photo.x_off");
    private static final Text Y_OFF = Text.translatable("text.photo.y_off");
    private static final Text Z_OFF = Text.translatable("text.photo.z_off");

    private Text photoSizeTip;
    private final boolean showTip;

    private final PhotoEntity photoEntity;
    private PhotoPosition positionType;

    private TextFieldWidget urlTextField;
    private TextFieldWidget widthTextField;
    private TextFieldWidget heightTextField;
    private TextFieldWidget xTextField;
    private TextFieldWidget yTextField;
    private TextFieldWidget zTextField;

    private CyclingButtonWidget<PhotoPosition> positionCyclingButtonWidget;
    private ButtonWidget doneButton;
    private ButtonWidget cancelButton;

    public PhotoScreen(PhotoEntity photoEntity) {
        super(Text.translatable(Photo.PHOTO_BLOCK.getTranslationKey()));
        this.photoEntity = photoEntity;
        this.positionType = PhotoPosition.byId(photoEntity.getPositionType());
        PictureDownloader.PictureData pictureData = PictureDownloader.getInstance().getPicture(photoEntity.getUrl());
        if (pictureData != null) {
            photoSizeTip = Text.translatable("text.photo.tip", pictureData.width, pictureData.height);
            showTip = true;
        } else {
            showTip = false;
        }
    }

    @Override
    public void tick() {
        this.urlTextField.tick();
        this.widthTextField.tick();
        this.heightTextField.tick();
        this.xTextField.tick();
        this.yTextField.tick();
        this.zTextField.tick();
    }


    @Override
    protected void init() {
        this.client.keyboard.setRepeatEvents(true);

        this.urlTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 120, 56, 240, 20, Text.translatable("text.photo.photo_url"));
        this.urlTextField.setMaxLength(3000);
        this.urlTextField.setText(photoEntity.getUrl());
        this.urlTextField.setTextFieldFocused(true);
        this.addSelectableChild(this.urlTextField);

        this.widthTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 120, 96 + getYDelta(), 100, 20, Text.translatable("text.photo.width"));
        this.widthTextField.setMaxLength(8);
        this.widthTextField.setText(String.valueOf(photoEntity.getWidth()));
        this.addSelectableChild(this.widthTextField);
        this.widthTextField.setChangedListener(this::onFloatChange);

        this.heightTextField = new TextFieldWidget(this.textRenderer, this.width / 2 + 20, 96 + getYDelta(), 100, 20, Text.translatable("text.photo.height"));
        this.heightTextField.setMaxLength(8);
        this.heightTextField.setText(String.valueOf(photoEntity.getHeight()));
        this.addSelectableChild(this.heightTextField);
        this.heightTextField.setChangedListener(this::onFloatChange);

        this.xTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 120, 136 + getYDelta(), 70, 20, Text.translatable("text.photo.x_off"));
        this.xTextField.setMaxLength(8);
        this.xTextField.setText(String.valueOf(photoEntity.getXOffset()));
        this.addSelectableChild(this.xTextField);
        this.xTextField.setChangedListener(this::onFloatChange);

        this.yTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 120 + 70 + 15, 136 + getYDelta(), 70, 20, Text.translatable("text.photo.y_off"));
        this.yTextField.setMaxLength(8);
        this.yTextField.setText(String.valueOf(photoEntity.getYOffset()));
        this.addSelectableChild(this.yTextField);
        this.yTextField.setChangedListener(this::onFloatChange);

        this.zTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 120 + 140 + 30, 136 + getYDelta(), 70, 20, Text.translatable("text.photo.z_off"));
        this.zTextField.setMaxLength(8);
        this.zTextField.setText(String.valueOf(photoEntity.getZOffset()));
        this.addSelectableChild(this.zTextField);
        this.zTextField.setChangedListener(this::onFloatChange);

        this.positionCyclingButtonWidget = this.addDrawableChild(CyclingButtonWidget.builder(PhotoPosition::getTranslationText).values(PhotoPosition.values()).omitKeyText().
                initially(this.positionType).build(this.width / 2 - 120 - 2, 166+getYDelta(), 80, 20, Text.translatable("photo.option.position"), (button, positionType) -> {
                    this.positionType = positionType;
                }));

        this.doneButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 4 - 150, this.height / 4 + 120 + 21,
                150, 20, ScreenTexts.DONE, (button) -> this.commitAndClose()));
        this.cancelButton = this.addDrawableChild(new ButtonWidget(this.width / 2 + 4, this.height / 4 + 120 + 21,
                150, 20, ScreenTexts.CANCEL, (button) -> this.close()));
    }

    protected void commitAndClose() {
        photoEntity.setUrl(this.urlTextField.getText());
        photoEntity.setWidth(getFloat(this.widthTextField.getText(), photoEntity.getWidth()));
        photoEntity.setHeight(getFloat(this.heightTextField.getText(), photoEntity.getHeight()));
        photoEntity.setXOffset(getFloat(this.xTextField.getText(), photoEntity.getXOffset()));
        photoEntity.setYOffset(getFloat(this.yTextField.getText(), photoEntity.getYOffset()));
        photoEntity.setZOffset(getFloat(this.zTextField.getText(), photoEntity.getZOffset()));
        photoEntity.setPositionType(this.positionType.getId());
        this.syncSettingsToServer(photoEntity);

        Objects.requireNonNull(this.client).setScreen(null);
    }

    protected void syncSettingsToServer(PhotoEntity photoEntity) {
        Objects.requireNonNull(Objects.requireNonNull(this.client).getNetworkHandler()).
                sendPacket(new UpdatePhotoC2SPacket(photoEntity.getPos(), photoEntity.getUrl(),
                        photoEntity.getWidth(), photoEntity.getHeight(), photoEntity.getXOffset(),
                        photoEntity.getYOffset(), photoEntity.getZOffset(), photoEntity.getPositionType()));
    }

    private void onFloatChange(String text) {
        this.doneButton.active =
                textCheck(this.widthTextField, num -> num.compareTo(new BigDecimal(0)) > 0)
                        && textCheck(this.heightTextField, num -> num.compareTo(new BigDecimal(0)) > 0)
                        && textCheck(this.xTextField)
                        && textCheck(this.yTextField)
                        && textCheck(this.zTextField);
    }

    private static boolean textCheck(TextFieldWidget textFieldWidget, Function<BigDecimal, Boolean> consumer) {
        boolean valid = isValidNum(textFieldWidget.getText(), consumer);
        // 当不合法时将颜色设为红色
        if (valid) {
            textFieldWidget.setEditableColor(14737632);
        } else {
            textFieldWidget.setEditableColor(15674887);
        }
        return valid;
    }

    private static boolean textCheck(TextFieldWidget textFieldWidget) {
        boolean valid = isValidNum(textFieldWidget.getText());
        // 当不合法时将颜色设为红色
        if (valid) {
            textFieldWidget.setEditableColor(14737632);
        } else {
            textFieldWidget.setEditableColor(15674887);
        }
        return valid;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 17, 16777215);
        drawTextWithShadow(matrices, this.textRenderer, URL_TEXT, this.width / 2 - 120, 43, 10526880);
        if (showTip) {
            drawTextWithShadow(matrices, this.textRenderer, photoSizeTip, this.width / 2 - 120, 81, 10526880);
        }
        drawTextWithShadow(matrices, this.textRenderer, WIDTH, this.width / 2 - 120, 84 + getYDelta(), 10526880);
        drawTextWithShadow(matrices, this.textRenderer, HEIGHT, this.width / 2 + 20, 84 + getYDelta(), 10526880);
        drawTextWithShadow(matrices, this.textRenderer, X_OFF, this.width / 2 - 120, 124 + getYDelta(), 10526880);
        drawTextWithShadow(matrices, this.textRenderer, Y_OFF, this.width / 2 - 120 + 85, 124 + getYDelta(), 10526880);
        drawTextWithShadow(matrices, this.textRenderer, Z_OFF, this.width / 2 - 120 + 85 * 2, 124 + getYDelta(), 10526880);
        this.urlTextField.render(matrices, mouseX, mouseY, delta);
        this.widthTextField.render(matrices, mouseX, mouseY, delta);
        this.heightTextField.render(matrices, mouseX, mouseY, delta);
        this.xTextField.render(matrices, mouseX, mouseY, delta);
        this.yTextField.render(matrices, mouseX, mouseY, delta);
        this.zTextField.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private static float getFloat(String str, float defaultVal) {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return defaultVal;
        }

    }

    private static boolean isValidNum(String str, Function<BigDecimal, Boolean> consumer) {
        try {
            BigDecimal res = new BigDecimal(str);
            return consumer.apply(res);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isValidNum(String str) {
        try {
            String res = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private int getYDelta() {
        return showTip ? 14 : 0;
    }
}
