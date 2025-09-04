package dylanjd.flashpoint.render;

import dylanjd.flashpoint.entities.LightningTrailEntity;
import dylanjd.flashpoint.entities.ModEntities;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

public class LightningTrailClientHandler {

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static double lastX = Double.NaN, lastY = Double.NaN, lastZ = Double.NaN;
    private static final double SPAWN_DIST = 0.5;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(c -> spawnTrail());
    }

    private static void spawnTrail() {
        if (CLIENT.player == null || CLIENT.world == null) return;

        Vec3d vel = CLIENT.player.getVelocity();
        if (vel.lengthSquared() < 0.01) return;

        double px = CLIENT.player.getX();
        double py = CLIENT.player.getY() + 1.0;
        double pz = CLIENT.player.getZ();

        if (Double.isNaN(lastX)) {
            lastX = px; lastY = py; lastZ = pz;
            return;
        }

        double dx = px - lastX, dy = py - lastY, dz = pz - lastZ;
        if (dx*dx + dy*dy + dz*dz < SPAWN_DIST*SPAWN_DIST) return;

        LightningTrailEntity trail = new LightningTrailEntity(ModEntities.LIGHTNING_TRAIL, CLIENT.world);
        trail.setPos(px - vel.x*0.25, py + 0.1, pz - vel.z*0.25);
        trail.width = 0.3f;
        trail.height = 0.5f;
        trail.setColor(0xFFFFD100);
        trail.lifetime = 100;
        trail.setVelocityHint(vel);

        CLIENT.world.addEntity(trail);
        CLIENT.world.addParticleClient(ParticleTypes.END_ROD, trail.getX(), trail.getY(), trail.getZ(), 0,0,0);

        lastX = px; lastY = py; lastZ = pz;
    }
}
