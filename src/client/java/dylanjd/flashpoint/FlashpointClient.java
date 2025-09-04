package dylanjd.flashpoint;

import dylanjd.flashpoint.render.LightningTrailClientHandler;
import dylanjd.flashpoint.render.LightningTrailRenderer;
import dylanjd.flashpoint.render.ModRenderers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class FlashpointClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		LightningTrailClientHandler.register();
		ModRenderers.register();
	}
}