package dylanjd.flashpoint.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.LinkedList;

public class LightningTrailEntity extends Entity {

    private static final TrackedData<Integer> COLOR =
            DataTracker.registerData(LightningTrailEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public final LinkedList<Vec3d> trailPoints = new LinkedList<>();

    public int lifetime = 20;
    public int ageTicks = 0;

    // visual dimensions
    public float width = 0.5f;
    public float height = 1.0f;

    public LightningTrailEntity(EntityType<? extends LightningTrailEntity> type, World world) {
        super(type, world);
        this.noClip = true;
        // add initial trail point so renderer has something
        this.trailPoints.add(this.getPos());
        this.trailPoints.add(this.getPos().add(0, 0.01, 0));
        //System.out.println("added init trail points" + trailPoints.size());
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(COLOR, 0xFFFFD100);
    }

    public void setColor(int argb) { this.getDataTracker().set(COLOR, argb); }
    public int getColor() { return this.getDataTracker().get(COLOR); }

    public void setVelocityHint(Vec3d v) {
        // optional for renderer
        this.velocityHint = v;
    }

    public Vec3d getVelocityHint() {
        return velocityHint;
    }

    private Vec3d velocityHint = Vec3d.ZERO;

    @Override
    public void tick() {
        super.tick();
        ageTicks++;

        // append current position to trail
        trailPoints.addFirst(this.getPos());
        while (trailPoints.size() > 20) trailPoints.removeLast();
        //System.out.println("appended");
        if (ageTicks >= lifetime) discard();
    }

    @Override
    protected void readCustomData(ReadView view) {
        this.lifetime = view.getInt("Lifetime", 20);
        this.ageTicks = view.getInt("AgeTicks", 0);
        this.setColor(view.getInt("Color", 0xFFFFD100));
    }

    @Override
    protected void writeCustomData(WriteView view) {
        view.putInt("Lifetime", lifetime);
        view.putInt("AgeTicks", ageTicks);
        view.putInt("Color", getColor());
    }

    @Override
    public boolean shouldSave() { return false; }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    public net.minecraft.entity.EntityDimensions getDimensions(net.minecraft.entity.EntityPose pose) {
        return net.minecraft.entity.EntityDimensions.changing(width, height);
    }

    @Override
    public boolean damage(ServerWorld world, net.minecraft.entity.damage.DamageSource source, float amount) {
        return false;
    }
}
