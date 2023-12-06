package de.maxhenkel.plane;

import de.maxhenkel.corelib.CommonRegistry;
import de.maxhenkel.plane.entity.*;
import de.maxhenkel.plane.entity.render.BushPlaneModel;
import de.maxhenkel.plane.entity.render.CargoPlaneModel;
import de.maxhenkel.plane.entity.render.PlaneModel;
import de.maxhenkel.plane.events.*;
import de.maxhenkel.plane.gui.ContainerPlane;
import de.maxhenkel.plane.gui.PlaneScreen;
import de.maxhenkel.plane.item.ModItems;
import de.maxhenkel.plane.loottable.CopyPlaneData;
import de.maxhenkel.plane.net.MessageControlPlane;
import de.maxhenkel.plane.net.MessagePlaneGui;
import de.maxhenkel.plane.sound.ModSounds;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.network.simple.SimpleChannel;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.UUID;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "plane";

    public static SimpleChannel SIMPLE_CHANNEL;

    private static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTION_TYPE_REGISTER = DeferredRegister.create(BuiltInRegistries.LOOT_FUNCTION_TYPE, Main.MODID);
    public static final DeferredHolder<LootItemFunctionType, LootItemFunctionType> COPY_PLANE_DATA = LOOT_FUNCTION_TYPE_REGISTER.register("copy_plane_data", () -> new LootItemFunctionType(CopyPlaneData.CODEC));

    public static ServerConfig SERVER_CONFIG;
    public static ClientConfig CLIENT_CONFIG;

    public Main() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CreativeTabEvents::onCreativeModeTabBuildContents);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterCapabilities);

        SERVER_CONFIG = CommonRegistry.registerConfig(ModConfig.Type.SERVER, ServerConfig.class, true);
        CLIENT_CONFIG = CommonRegistry.registerConfig(ModConfig.Type.CLIENT, ClientConfig.class);

        if (FMLEnvironment.dist.isClient()) {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(Main.this::clientSetup);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(Main.this::onRegisterKeyBinds);
        }

        ModItems.init();
        ModSounds.init();
        ENTITY_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
        MENU_TYPE_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new InteractEvents());

        SIMPLE_CHANNEL = CommonRegistry.registerChannel(Main.MODID, "default");
        CommonRegistry.registerMessage(SIMPLE_CHANNEL, 0, MessageControlPlane.class);
        CommonRegistry.registerMessage(SIMPLE_CHANNEL, 1, MessagePlaneGui.class);
    }

    public static KeyMapping PLANE_KEY;
    public static KeyMapping FORWARD_KEY;
    public static KeyMapping BACK_KEY;
    public static KeyMapping LEFT_KEY;
    public static KeyMapping RIGHT_KEY;
    public static KeyMapping UP_KEY;
    public static KeyMapping DOWN_KEY;
    public static KeyMapping START_KEY;
    public static KeyMapping BRAKE_KEY;

    @OnlyIn(Dist.CLIENT)
    public void clientSetup(FMLClientSetupEvent event) {

        MenuScreens.ScreenConstructor factory = (MenuScreens.ScreenConstructor<ContainerPlane, PlaneScreen>) (container, playerInventory, name) -> new PlaneScreen(container, playerInventory, name);
        MenuScreens.register(Main.PLANE_CONTAINER_TYPE.get(), factory);

        NeoForge.EVENT_BUS.register(new KeyEvents());
        NeoForge.EVENT_BUS.register(new RenderEvents());
        // TODO
        /*try {
            Class.forName("mcp.mobius.waila.api.event.WailaRenderEvent");
            MinecraftForge.EVENT_BUS.register(new PluginPlane());
        } catch (ClassNotFoundException e) {
        }*/

        EntityRenderers.register(PLANE_ENTITY_TYPE.get(), manager -> new PlaneModel(manager));
        EntityRenderers.register(CARGO_PLANE_ENTITY_TYPE.get(), manager -> new CargoPlaneModel(manager));
        EntityRenderers.register(BUSH_PLANE_ENTITY_TYPE.get(), manager -> new BushPlaneModel(manager));
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRegisterKeyBinds(RegisterKeyMappingsEvent event) {
        PLANE_KEY = new KeyMapping("key.plane", GLFW.GLFW_KEY_P, "category.plane");
        FORWARD_KEY = new KeyMapping("key.plane_add_thrust", GLFW.GLFW_KEY_I, "category.plane");
        BACK_KEY = new KeyMapping("key.plane_remove_thrust", GLFW.GLFW_KEY_K, "category.plane");
        LEFT_KEY = new KeyMapping("key.plane_left", GLFW.GLFW_KEY_A, "category.plane");
        RIGHT_KEY = new KeyMapping("key.plane_right", GLFW.GLFW_KEY_D, "category.plane");
        UP_KEY = new KeyMapping("key.plane_up", GLFW.GLFW_KEY_S, "category.plane");
        DOWN_KEY = new KeyMapping("key.plane_down", GLFW.GLFW_KEY_W, "category.plane");
        START_KEY = new KeyMapping("key.plane_start", GLFW.GLFW_KEY_R, "category.plane");
        BRAKE_KEY = new KeyMapping("key.plane_brake", GLFW.GLFW_KEY_B, "category.plane");

        event.register(PLANE_KEY);
        event.register(FORWARD_KEY);
        event.register(BACK_KEY);
        event.register(LEFT_KEY);
        event.register(RIGHT_KEY);
        event.register(UP_KEY);
        event.register(DOWN_KEY);
        event.register(START_KEY);
        event.register(BRAKE_KEY);
    }

    private static final DeferredRegister<EntityType<?>> ENTITY_REGISTER = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Main.MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityPlane>> PLANE_ENTITY_TYPE = ENTITY_REGISTER.register("plane", () -> {
        return CommonRegistry.registerEntity(Main.MODID, "plane", MobCategory.MISC, EntityPlane.class, builder -> {
            builder
                    .setTrackingRange(256)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .sized(3.5F, 2F)
                    .setCustomClientFactory((spawnEntity, world) -> new EntityPlane(world));
        });
    });
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCargoPlane>> CARGO_PLANE_ENTITY_TYPE = ENTITY_REGISTER.register("cargo_plane", () -> {
        return CommonRegistry.registerEntity(Main.MODID, "cargo_plane", MobCategory.MISC, EntityCargoPlane.class, builder -> {
            builder
                    .setTrackingRange(256)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .sized(3.5F, 2F)
                    .setCustomClientFactory((spawnEntity, world) -> new EntityCargoPlane(world));
        });
    });
    public static final DeferredHolder<EntityType<?>, EntityType<EntityBushPlane>> BUSH_PLANE_ENTITY_TYPE = ENTITY_REGISTER.register("bush_plane", () -> {
        return CommonRegistry.registerEntity(Main.MODID, "bush_plane", MobCategory.MISC, EntityBushPlane.class, builder -> {
            builder
                    .setTrackingRange(256)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .sized(3.5F, 2F)
                    .setCustomClientFactory((spawnEntity, world) -> new EntityBushPlane(world));
        });
    });

    private static final DeferredRegister<MenuType<?>> MENU_TYPE_REGISTER = DeferredRegister.create(BuiltInRegistries.MENU, Main.MODID);
    public static DeferredHolder<MenuType<?>, MenuType<ContainerPlane>> PLANE_CONTAINER_TYPE = MENU_TYPE_REGISTER.register("plane", () -> {
        return new MenuType<>((IContainerFactory<ContainerPlane>) (windowId, inv, data) -> {
            EntityPlaneSoundBase plane = getPlaneByUUID(inv.player, data.readUUID());
            if (plane == null) {
                return null;
            }
            return new ContainerPlane(windowId, plane, inv);
        }, FeatureFlags.VANILLA_SET);
    });

    @Nullable
    public static EntityPlaneSoundBase getPlaneByUUID(Player player, UUID uuid) {
        double distance = 10D;
        return player.level().getEntitiesOfClass(EntityPlaneSoundBase.class, new AABB(player.getX() - distance, player.getY() - distance, player.getZ() - distance, player.getX() + distance, player.getY() + distance, player.getZ() + distance), entity -> entity.getUUID().equals(uuid)).stream().findAny().orElse(null);
    }

    public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        registerEntityCapabilities(event, PLANE_ENTITY_TYPE);
        registerEntityCapabilities(event, BUSH_PLANE_ENTITY_TYPE);
        registerEntityCapabilities(event, CARGO_PLANE_ENTITY_TYPE);
    }

    private <T extends EntityVehicleBase> void registerEntityCapabilities(RegisterCapabilitiesEvent event, DeferredHolder<EntityType<?>, EntityType<T>> holder) {
        event.registerEntity(Capabilities.FluidHandler.ENTITY, holder.get(), (object, context) -> {
            if (object instanceof IFluidHandler fluidHandler) {
                return fluidHandler;
            }
            return null;
        });
        event.registerEntity(Capabilities.EnergyStorage.ENTITY, holder.get(), (object, context) -> {
            if (object instanceof IEnergyStorage energyStorage) {
                return energyStorage;
            }
            return null;
        });
        event.registerEntity(Capabilities.ItemHandler.ENTITY, holder.get(), (object, context) -> {
            if (object instanceof IItemHandler itemHandler) {
                return itemHandler;
            }
            return null;
        });
    }

}
