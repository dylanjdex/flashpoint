package dylanjd.flashpoint.render;

import dylanjd.flashpoint.entities.LightningTrailEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class LightningTrailRenderer
        extends EntityRenderer<LightningTrailEntity, LightningTrailRenderer.LightningTrailRenderState> {

    private static final RenderLayer TRAIL_LAYER =
            RenderLayer.getEntityTranslucent(Identifier.of("flashpoint", "textures/entity/lightning_trail.png"));

    public LightningTrailRenderer(EntityRendererFactory.Context ctx) { super(ctx); }

    public static class LightningTrailRenderState extends EntityRenderState {
        public LightningTrailEntity entity;
    }

    @Override
    public LightningTrailRenderState createRenderState() { return new LightningTrailRenderState(); }

    @Override
    public void updateRenderState(LightningTrailEntity entity, LightningTrailRenderState state, float tickDelta) {
        state.x = entity.getX();
        state.y = entity.getY();
        state.z = entity.getZ();
        state.width = entity.width;
        state.height = entity.height;
        state.entity = entity;
    }

    @Override
    public void render(LightningTrailRenderState s, MatrixStack matrices, VertexConsumerProvider vcp, int light) {
        LightningTrailEntity entity = s.entity;
        VertexConsumer vc = vcp.getBuffer(TRAIL_LAYER);
        Vec3d cam = this.dispatcher.camera.getPos();

        int trailSize = entity.trailPoints.size();
        System.out.println(trailSize);
        if (trailSize < 2) return;

        for (int i = 0; i < trailSize - 1; i++) {
            Vec3d start = entity.trailPoints.get(i).subtract(cam);
            Vec3d end = entity.trailPoints.get(i+1).subtract(cam);

            float ageFactor = (float)i / (float)trailSize;
            int a = (int)((1.0f - ageFactor) * 255f);
            int argb = entity.getColor();
            int r = (argb >> 16) & 0xFF;
            int g = (argb >> 8) & 0xFF;
            int b = argb & 0xFF;

            float width = s.width * (1.0f - ageFactor);

            drawSegment(matrices, vc, start, end, width, r, g, b, a, light);
        }
    }

    private void drawSegment(MatrixStack matrices, VertexConsumer vc, Vec3d start, Vec3d end,
                             float width, int r, int g, int b, int a, int light) {

        Vec3d diff = end.subtract(start).normalize();
        Vec3d perp = diff.crossProduct(new Vec3d(0,1,0)).multiply(width / 2);
        var m = matrices.peek().getPositionMatrix();

        vc.vertex(m, (float)(start.x + perp.x), (float)(start.y + perp.y), (float)(start.z + perp.z))
                .color(r, g, b, a).texture(0f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0,1,0);
        vc.vertex(m, (float)(start.x - perp.x), (float)(start.y - perp.y), (float)(start.z - perp.z))
                .color(r, g, b, a).texture(1f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0,1,0);
        vc.vertex(m, (float)(end.x - perp.x), (float)(end.y - perp.y), (float)(end.z - perp.z))
                .color(r, g, b, a).texture(1f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0,1,0);
        vc.vertex(m, (float)(end.x + perp.x), (float)(end.y + perp.y), (float)(end.z + perp.z))
                .color(r, g, b, a).texture(0f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0,1,0);
        System.out.println("drew segment");
    }
}
