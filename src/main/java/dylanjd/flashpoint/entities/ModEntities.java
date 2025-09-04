package dylanjd.flashpoint.entities;

import dylanjd.flashpoint.Flashpoint;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;


public class ModEntities {
    public static <T extends Entity>EntityType<T> registerEntity(String namespace, String id, EntityType.Builder<T> type) {
        RegistryKey<EntityType<?>> registryKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(namespace, id));
        return Registry.register(Registries.ENTITY_TYPE, registryKey, type.build(registryKey));
    }
    public static final EntityType<LightningTrailEntity> LIGHTNING_TRAIL = registerEntity(Flashpoint.MOD_ID,"lightning_trail", EntityType.Builder.create(LightningTrailEntity::new, SpawnGroup.MISC));

    public static void init() {
        Flashpoint.LOGGER.info("Registering Mod Entities");
    }
}
