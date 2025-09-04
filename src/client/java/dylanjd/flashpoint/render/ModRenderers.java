package dylanjd.flashpoint.render;

import dylanjd.flashpoint.entities.ModEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ModRenderers {
    public static void register() {
        EntityRendererRegistry.register(ModEntities.LIGHTNING_TRAIL, LightningTrailRenderer::new);
    }
}
