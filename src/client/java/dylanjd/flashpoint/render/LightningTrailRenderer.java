package dylanjd.flashpoint.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dylanjd.flashpoint.entities.LightningTrailEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class LightningTrailRenderer
        extends EntityRenderer<LightningTrailEntity, LightningTrailRenderer.State> {

    private static final Identifier TEX = Identifier.of("flashpoint","textures/entity/lightning_trail.png");
    private static final RenderLayer TRAIL_LAYER = RenderLayer.getEntityTranslucent(TEX);
    private static final RenderLayer TRAIL_LAYER_GLOW = RenderLayer.getEntityTranslucentEmissive(TEX);

    public static class State extends EntityRenderState {
        public int argb;
        public int lifetime;
        public int ageTicks;
        public double yaw;
        public double pitch;
        public float depth;
        public Quaternionf qPlayer;
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
        s.depth = e.depth;
        s.yaw = e.yaw;
        s.pitch = e.pitch;
        s.qPlayer = e.qPlayer;
        s.ageTicks = e.age;
        s.lifetime = e.lifetime;
        s.argb     = e.getColor();
    }

    @Override
    public void render(State s, MatrixStack matrices, VertexConsumerProvider vcp, int light) {
        matrices.push();


        // move to camera-relative space
        //var cam = this.dispatcher.camera.getPos();
        //var cam = this.dispatcher.camera.getPos();
        //matrices.translate(s.x - cam.x, s.y - cam.y, s.z - cam.z);
        //matrices.multiply(this.dispatcher.getRotation());
        //matrices.multiply(s.qPlayer);
        //matrices.multiply(RotationAxis.);
        float yawRad = (float) Math.toRadians(s.yaw);
        float pitchRad = (float) Math.toRadians(s.pitch);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(-yawRad));
        //matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(pitchRad));
        //matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) s.yaw));
        //matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) s.pitch));
        //matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) s.pitch));
        //matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        //matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0F));
        //System.out.println(s.pitch);
        //matrices.scale(4,2,2);

        // small push forward to prevent Z-fighting / block clipping
        //matrices.translate(0.0, 0.05, 0.05); // 5% of a block in front

        float alphaF = 1f - (s.ageTicks / (float) s.lifetime);
        if (alphaF <= 0f) { matrices.pop(); return; }

        int alpha = (int)(alphaF * 255f);
        int r = (s.argb >> 16) & 0xFF, g = (s.argb >> 8) & 0xFF, b = s.argb & 0xFF;
        float w = s.width, h = s.height;

        float halfW = s.width / 2.0F;
        float halfH = s.height / 2.0F;
        float halfD = s.depth / 2.0F;

        var m = matrices.peek().getPositionMatrix();
        var vc = vcp.getBuffer(TRAIL_LAYER);

        // Front face (Z negative)
        vc.vertex(m, -halfW, -halfH, -halfD).color(r,g,b,alpha).texture(0f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,0f,-1f);
        vc.vertex(m,  halfW, -halfH, -halfD).color(r,g,b,alpha).texture(1f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,0f,-1f);
        vc.vertex(m,  halfW,  halfH, -halfD).color(r,g,b,alpha).texture(1f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,0f,-1f);
        vc.vertex(m, -halfW,  halfH, -halfD).color(r,g,b,alpha).texture(0f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,0f,-1f);

        // Back face (Z positive)
        vc.vertex(m, -halfW, -halfH, halfD).color(r,g,b,alpha).texture(0f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,0f,1f);
        vc.vertex(m,  halfW, -halfH, halfD).color(r,g,b,alpha).texture(1f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,0f,1f);
        vc.vertex(m,  halfW,  halfH, halfD).color(r,g,b,alpha).texture(1f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,0f,1f);
        vc.vertex(m, -halfW,  halfH, halfD).color(r,g,b,alpha).texture(0f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,0f,1f);

        // Left face (X negative)
        vc.vertex(m, -halfW, -halfH, -halfD).color(r,g,b,alpha).texture(0f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(-1f,0f,0f);
        vc.vertex(m, -halfW, -halfH, halfD).color(r,g,b,alpha).texture(1f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(-1f,0f,0f);
        vc.vertex(m, -halfW,  halfH, halfD).color(r,g,b,alpha).texture(1f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(-1f,0f,0f);
        vc.vertex(m, -halfW,  halfH, -halfD).color(r,g,b,alpha).texture(0f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(-1f,0f,0f);

        // Right face (X positive)
        vc.vertex(m, halfW, -halfH, -halfD).color(r,g,b,alpha).texture(0f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(1f,0f,0f);
        vc.vertex(m, halfW, -halfH, halfD).color(r,g,b,alpha).texture(1f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(1f,0f,0f);
        vc.vertex(m, halfW,  halfH, halfD).color(r,g,b,alpha).texture(1f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(1f,0f,0f);
        vc.vertex(m, halfW,  halfH, -halfD).color(r,g,b,alpha).texture(0f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(1f,0f,0f);

        // Top face (Y positive)
        vc.vertex(m, -halfW, halfH, -halfD).color(r,g,b,alpha).texture(0f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,1f,0f);
        vc.vertex(m,  halfW, halfH, -halfD).color(r,g,b,alpha).texture(1f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,1f,0f);
        vc.vertex(m,  halfW, halfH, halfD).color(r,g,b,alpha).texture(1f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,1f,0f);
        vc.vertex(m, -halfW, halfH, halfD).color(r,g,b,alpha).texture(0f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,1f,0f);

        // Bottom face (Y negative)
        vc.vertex(m, -halfW, -halfH, -halfD).color(r,g,b,alpha).texture(0f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,-1f,0f);
        vc.vertex(m,  halfW, -halfH, -halfD).color(r,g,b,alpha).texture(1f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,-1f,0f);
        vc.vertex(m,  halfW, -halfH, halfD).color(r,g,b,alpha).texture(1f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,-1f,0f);
        vc.vertex(m, -halfW, -halfH, halfD).color(r,g,b,alpha).texture(0f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,-1f,0f);

        var vcglow = vcp.getBuffer(TRAIL_LAYER_GLOW);

        // Front face (Z negative)
        vcglow.vertex(m, -halfW, -halfH, -halfD).color(r,g,b,alpha).texture(0f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,0f,-1f);
        vcglow.vertex(m,  halfW, -halfH, -halfD).color(r,g,b,alpha).texture(1f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,0f,-1f);
        vcglow.vertex(m,  halfW,  halfH, -halfD).color(r,g,b,alpha).texture(1f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,0f,-1f);
        vcglow.vertex(m, -halfW,  halfH, -halfD).color(r,g,b,alpha).texture(0f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,0f,-1f);

        // Back face (Z positive)
        vcglow.vertex(m, -halfW, -halfH, halfD).color(r,g,b,alpha).texture(0f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,0f,1f);
        vcglow.vertex(m,  halfW, -halfH, halfD).color(r,g,b,alpha).texture(1f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,0f,1f);
        vcglow.vertex(m,  halfW,  halfH, halfD).color(r,g,b,alpha).texture(1f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,0f,1f);
        vcglow.vertex(m, -halfW,  halfH, halfD).color(r,g,b,alpha).texture(0f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,0f,1f);

        // Left face (X negative)
        vcglow.vertex(m, -halfW, -halfH, -halfD).color(r,g,b,alpha).texture(0f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(-1f,0f,0f);
        vcglow.vertex(m, -halfW, -halfH, halfD).color(r,g,b,alpha).texture(1f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(-1f,0f,0f);
        vcglow.vertex(m, -halfW,  halfH, halfD).color(r,g,b,alpha).texture(1f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(-1f,0f,0f);
        vcglow.vertex(m, -halfW,  halfH, -halfD).color(r,g,b,alpha).texture(0f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(-1f,0f,0f);

        // Right face (X positive)
        vcglow.vertex(m, halfW, -halfH, -halfD).color(r,g,b,alpha).texture(0f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(1f,0f,0f);
        vcglow.vertex(m, halfW, -halfH, halfD).color(r,g,b,alpha).texture(1f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(1f,0f,0f);
        vcglow.vertex(m, halfW,  halfH, halfD).color(r,g,b,alpha).texture(1f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(1f,0f,0f);
        vcglow.vertex(m, halfW,  halfH, -halfD).color(r,g,b,alpha).texture(0f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(1f,0f,0f);

        // Top face (Y positive)
        vcglow.vertex(m, -halfW, halfH, -halfD).color(r,g,b,alpha).texture(0f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,1f,0f);
        vcglow.vertex(m,  halfW, halfH, -halfD).color(r,g,b,alpha).texture(1f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,1f,0f);
        vcglow.vertex(m,  halfW, halfH, halfD).color(r,g,b,alpha).texture(1f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,1f,0f);
        vcglow.vertex(m, -halfW, halfH, halfD).color(r,g,b,alpha).texture(0f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,1f,0f);

        // Bottom face (Y negative)
        vcglow.vertex(m, -halfW, -halfH, -halfD).color(r,g,b,alpha).texture(0f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,-1f,0f);
        vcglow.vertex(m,  halfW, -halfH, -halfD).color(r,g,b,alpha).texture(1f,1f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,-1f,0f);
        vcglow.vertex(m,  halfW, -halfH, halfD).color(r,g,b,alpha).texture(1f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,-1f,0f);
        vcglow.vertex(m, -halfW, -halfH, halfD).color(r,g,b,alpha).texture(0f,0f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0f,-1f,0f);

        matrices.pop();
    }
}
