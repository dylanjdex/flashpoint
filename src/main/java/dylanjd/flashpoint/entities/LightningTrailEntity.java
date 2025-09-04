package dylanjd.flashpoint.entities;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LightningTrailEntity extends Entity {

    // Tracked color as ARGB (e.g., 0xFFFFD100 for yellow)
    private static final TrackedData<Integer> COLOR =
            DataTracker.registerData(LightningTrailEntity.class, TrackedDataHandlerRegistry.INTEGER);

    // Optional velocity hint for renderer
    private static final TrackedData<Float> VX =
            DataTracker.registerData(LightningTrailEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> VY =
            DataTracker.registerData(LightningTrailEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> VZ =
            DataTracker.registerData(LightningTrailEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public int lifetime = 20;        // ticks to live
    public float width = 0.5f;      // visual width
    public float height = 1.0f;     // visual height

    public LightningTrailEntity(EntityType<? extends LightningTrailEntity> type, World world) {
        super(type, world);
        this.noClip = true; // purely visual
    }

    public void setColor(int argb) {
        this.getDataTracker().set(COLOR, argb);
    }

    public int getColor() {
        return this.getDataTracker().get(COLOR);
    }

    public void setVelocityHint(Vec3d v) {
        this.getDataTracker().set(VX, (float) v.x);
        this.getDataTracker().set(VY, (float) v.y);
        this.getDataTracker().set(VZ, (float) v.z);
    }

    public Vec3d getVelocityHint() {
        return new Vec3d(this.getDataTracker().get(VX),
                this.getDataTracker().get(VY),
                this.getDataTracker().get(VZ));
    }

    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(COLOR, 0xFFFFD100);
        builder.add(VX, 0f);
        builder.add(VY, 0f);
        builder.add(VZ, 0f);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age >= lifetime) {
            this.discard(); // remove after lifetime
        }
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readCustomData(ReadView view) {
        this.lifetime = view.getInt("Lifetime", 10);
        this.width = view.getFloat("Width", 0.18f);
        this.height = view.getFloat("Height", 0.28f);
        this.getDataTracker().set(COLOR, view.getInt("Color", 0xFFFFD100));
    }

    @Override
    protected void writeCustomData(WriteView view) {
        view.putInt("Lifetime", this.lifetime);
        view.putFloat("Width", this.width);
        view.putFloat("Height", this.height);
    }

    @Override
    public boolean shouldSave() {
        return false; // don't save to world
    }
}