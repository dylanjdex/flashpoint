package dylanjd.flashpoint.render;

import dylanjd.flashpoint.Flashpoint;
import dylanjd.flashpoint.entities.LightningTrailEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class LightningTrailRenderer
        extends EntityRenderer<LightningTrailEntity, LightningTrailRenderer.State> {

    private static final Identifier TEX = Identifier.of("flashpoint","textures/entity/lightning_trail.png");
    private static final RenderLayer TRAIL_LAYER = RenderLayer.getEntityTranslucent(TEX);

    public static class State extends EntityRenderState {
        public int argb;
        public int lifetime;
        public int ageTicks;
    }

    public LightningTrailRenderer(EntityRendererFactory.Context ctx) { super(ctx); }

    @Override
    public State createRenderState() { return new State(); }

    @Override
    public void updateRenderState(LightningTrailEntity e, State s, float tickDelta) {
        s.x = e.getX();
        s.y = e.getY();
        s.z = e.getZ();
        s.width  = e.width;   // your visual quad size
        s.height = e.height;

        s.ageTicks = e.age;
        s.lifetime = e.lifetime;
        s.argb     = e.getColor();
    }

    @Override
    public void render(State s, MatrixStack matrices, VertexConsumerProvider vcp, int light) {
        matrices.push();

        // move to camera-relative space
        var cam = this.dispatcher.camera.getPos();
        matrices.translate(s.x - cam.x, s.y - cam.y, s.z - cam.z);

        matrices.multiply(this.dispatcher.getRotation());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));

        // small push forward to prevent Z-fighting / block clipping
        matrices.translate(0.0, 0.0, 0.05); // 5% of a block in front

        float alphaF = 1f - (s.ageTicks / (float) s.lifetime);
        if (alphaF <= 0f) { matrices.pop(); return; }

        int a = (int)(alphaF * 255f);
        int r = (s.argb >> 16) & 0xFF, g = (s.argb >> 8) & 0xFF, b = s.argb & 0xFF;
        float w = s.width, h = s.height;

        float halfW = s.width / 2.0F;
        float halfH = s.height / 2.0F;

        var m = matrices.peek().getPositionMatrix();
        var vc = vcp.getBuffer(TRAIL_LAYER);

        vc.vertex(m, -halfW, -halfH, 0.0F).color(r,g,b,a).texture(0f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,1f,0f);
        vc.vertex(m,  halfW, -halfH, 0.0F).color(r,g,b,a).texture(1f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,1f,0f);
        vc.vertex(m,  halfW,  halfH, 0.0F).color(r,g,b,a).texture(1f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,1f,0f);
        vc.vertex(m, -halfW,  halfH, 0.0F).color(r,g,b,a).texture(0f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,1f,0f);

        matrices.pop();
    }
}
