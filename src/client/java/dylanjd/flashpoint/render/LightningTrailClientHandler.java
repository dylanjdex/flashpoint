package dylanjd.flashpoint.render;

import dylanjd.flashpoint.entities.ModEntities;
import dylanjd.flashpoint.entities.LightningTrailEntity;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.math.Vec3d;

public class LightningTrailClientHandler {

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    private static double lastSpawnX = Double.NaN;
    private static double lastSpawnY = Double.NaN;
    private static double lastSpawnZ = Double.NaN;
    private static final double SPAWN_DISTANCE = 0.5; // blocks between trail entities

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> spawnTrail());
        System.out.println("END_CLIENT_TICK event triggered");
    }

    private static void spawnTrail() {
        if (CLIENT.player == null || CLIENT.world == null) return;
        //System.out.println("spawnTrail method triggered and check passed");

        Vec3d velocity = CLIENT.player.getVelocity();
        if (velocity.lengthSquared() < 0.01) return; // not moving enough
        //System.out.println("velocity check also passed");

        double px = CLIENT.player.getX();
        double py = CLIENT.player.getY() + 1;
        double pz = CLIENT.player.getZ();

        if (Double.isNaN(lastSpawnX)) {
            lastSpawnX = px;
            lastSpawnY = py;
            lastSpawnZ = pz;
            return;
        }
        //System.out.println("last spawn check passed");

        double dx = px - lastSpawnX;
        double dy = py - lastSpawnY;
        double dz = pz - lastSpawnZ;
        if (dx * dx + dy * dy + dz * dz < SPAWN_DISTANCE * SPAWN_DISTANCE) return;
        //System.out.println("sufficient distance calculation check passed");

        LightningTrailEntity trail = new LightningTrailEntity(ModEntities.LIGHTNING_TRAIL, CLIENT.world);
//        PigEntity pig = new PigEntity(EntityType.PIG, CLIENT.world);

        trail.updatePosition(
                px - velocity.x * 0.25,
                py + 0.1,
                pz - velocity.z * 0.25
        );
        trail.width = 0.18f;
        trail.height = 0.28f;
        trail.setColor(0xFFFF0000);
        trail.lifetime = 100;
//        pig.updatePosition(
//                px - velocity.x * 0.25,
//                py + 0.1,
//                pz - velocity.z * 0.25
//        );

        trail.setVelocityHint(velocity);
        trail.setColor(0xFFFFD100);

        CLIENT.world.addEntity(trail);
//        CLIENT.world.spawnEntity(pig);
        System.out.println("actually spawn entity");
//        System.out.println(pig.getPos());

        lastSpawnX = px;
        lastSpawnY = py;
        lastSpawnZ = pz;
    }
}
