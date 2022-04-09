package xyz.yuanzhi.photo.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.yuanzhi.photo.Photo;

public class PhotoEntity extends BlockEntity  {

    private String url = "";
    private float width = 1.0f;
    private float height = 1.0f;
    private float XOffset = 0.0f;
    private float YOffset = 0.0f;
    private float ZOffset = 0.0f;
    private int positionType = 0;



    public void setUrl(String url) {
        this.url = url;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setXOffset(float XOffset) {
        this.XOffset = XOffset;
    }

    public void setYOffset(float YOffset) {
        this.YOffset = YOffset;
    }

    public void setZOffset(float ZOffset) {
        this.ZOffset = ZOffset;
    }

    public String getUrl() {
        return url;
    }

    public int getPositionType() {
        return positionType;
    }

    public void setPositionType(int positionType) {
        this.positionType = positionType;
    }

    public float getWidth() {
        return width;
    }
    
    public float getHeight() {
        return height;
    }

    public float getXOffset() {
        return XOffset;
    }

    public float getYOffset() {
        return YOffset;
    }

    public float getZOffset() {
        return ZOffset;
    }

    public PhotoEntity(BlockPos pos, BlockState state) {
        super(Photo.PHOTO_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putString("url", url);
        nbt.putFloat("width", width);
        nbt.putFloat("height", height);
        nbt.putFloat("x_off", XOffset);
        nbt.putFloat("y_off", YOffset);
        nbt.putFloat("z_off", ZOffset);
        nbt.putInt("position_type", positionType);

    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        url = nbt.getString("url");
        width = nbt.getFloat("width");
        height = nbt.getFloat("height");
        XOffset = nbt.getFloat("x_off");
        YOffset = nbt.getFloat("y_off");
        ZOffset = nbt.getFloat("z_off");
        positionType = nbt.getInt("position_type");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }



}
