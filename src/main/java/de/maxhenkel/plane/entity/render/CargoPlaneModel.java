package de.maxhenkel.plane.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.maxhenkel.corelib.client.obj.OBJModel;
import de.maxhenkel.corelib.client.obj.OBJModelInstance;
import de.maxhenkel.corelib.client.obj.OBJModelOptions;
import de.maxhenkel.corelib.math.Rotation;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityCargoPlane;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CargoPlaneModel extends AbstractPlaneModel<EntityCargoPlane> {

    private static final List<OBJModelInstance<EntityCargoPlane>> MODELS = Arrays.asList(
            new OBJModelInstance<>(
                    new OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/wheel.obj")
                    ),
                    new OBJModelOptions<>(
                            new ResourceLocation(Main.MODID, "textures/entity/wheel.png"),
                            new Vector3d(-18D / 16D, 2D / 16D, -17.5D / 16D),
                            (plane, matrixStack, partialTicks) -> {
                                matrixStack.scale(1F / 16F, 1F / 16F, 1F / 16F);
                                matrixStack.mulPose(Axis.XP.rotationDegrees(-plane.getWheelRotation(partialTicks)));
                            }
                    )
            ),
            new OBJModelInstance<>(
                    new OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/wheel.obj")
                    ),
                    new OBJModelOptions<>(
                            new ResourceLocation(Main.MODID, "textures/entity/wheel.png"),
                            new Vector3d(18D / 16D, 2D / 16D, -17.5D / 16D),
                            (plane, matrixStack, partialTicks) -> {
                                matrixStack.scale(1F / 16F, 1F / 16F, 1F / 16F);
                                matrixStack.mulPose(Axis.XP.rotationDegrees(-plane.getWheelRotation(partialTicks)));
                            }
                    )
            ),
            new OBJModelInstance<>(
                    new OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/propeller.obj")
                    ),
                    new OBJModelOptions<>(
                            new ResourceLocation("textures/block/spruce_planks.png"),
                            new Vector3d(0D / 16D, 16D / 16D, -29.5D / 16D),
                            (plane, matrixStack, partialTicks) -> {
                                matrixStack.scale(1F / 16F, 1F / 16F, 1F / 16F);
                                matrixStack.mulPose(Axis.ZP.rotationDegrees(-plane.getPropellerRotation(partialTicks)));
                            }
                    )
            )
    );

    private static final List<OBJModelInstance<EntityCargoPlane>> OAK_MODEL = getPlaneModel(new ResourceLocation("textures/block/oak_planks.png"));
    private static final List<OBJModelInstance<EntityCargoPlane>> DARK_OAK_MODEL = getPlaneModel(new ResourceLocation("textures/block/dark_oak_planks.png"));
    private static final List<OBJModelInstance<EntityCargoPlane>> BIRCH_MODEL = getPlaneModel(new ResourceLocation("textures/block/birch_planks.png"));
    private static final List<OBJModelInstance<EntityCargoPlane>> JUNGLE_MODEL = getPlaneModel(new ResourceLocation("textures/block/jungle_planks.png"));
    private static final List<OBJModelInstance<EntityCargoPlane>> ACACIA_MODEL = getPlaneModel(new ResourceLocation("textures/block/acacia_planks.png"));
    private static final List<OBJModelInstance<EntityCargoPlane>> SPRUCE_MODEL = getPlaneModel(new ResourceLocation("textures/block/spruce_planks.png"));
    private static final List<OBJModelInstance<EntityCargoPlane>> WARPED_MODEL = getPlaneModel(new ResourceLocation("textures/block/warped_planks.png"));
    private static final List<OBJModelInstance<EntityCargoPlane>> CRIMSON_MODEL = getPlaneModel(new ResourceLocation("textures/block/crimson_planks.png"));
    // CHANGED
    private static final List<OBJModelInstance<EntityCargoPlane>> WHITE_TERRACOTTA_MODEL = getPlaneModel(new ResourceLocation("textures/block/white_terracotta.png"));
    private static final List<OBJModelInstance<EntityCargoPlane>> LIGHT_GRAY_CONCRETE_MODEL = getPlaneModel(new ResourceLocation("textures/block/light_gray_concrete.png"));
    private static final List<OBJModelInstance<EntityCargoPlane>> WHITE_CONCRETE_MODEL = getPlaneModel(new ResourceLocation("textures/block/white_concrete.png"));
    private static final List<OBJModelInstance<EntityCargoPlane>> LIGHT_BLUE_TERRACOTTA_MODEL = getPlaneModel(new ResourceLocation("textures/block/light_blue_terracotta.png"));
    private static final List<OBJModelInstance<EntityCargoPlane>> ORANGE_TERRACOTTA_MODEL = getPlaneModel(new ResourceLocation("textures/block/orange_terracotta.png"));
    private static final List<OBJModelInstance<EntityCargoPlane>> LIGHT_GRAY_TERRACOTTA_MODEL = getPlaneModel(new ResourceLocation("textures/block/light_gray_terracotta.png"));
    private static final List<OBJModelInstance<EntityCargoPlane>> RED_TERRACOTTA_MODEL = getPlaneModel(new ResourceLocation("textures/block/red_terracotta.png"));
    private static final List<OBJModelInstance<EntityCargoPlane>> GREEN_TERRACOTTA_MODEL = getPlaneModel(new ResourceLocation("textures/block/green_terracotta.png"));

    public CargoPlaneModel(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }


    @Override
    protected void translateName(EntityCargoPlane plane, PoseStack matrixStack, boolean left) {
        if (left) {
            matrixStack.translate(16.01D / 16D, -20D / 16D, -1D);
            matrixStack.mulPose(Axis.YP.rotationDegrees(90F));
        } else {
            matrixStack.translate(-16.01D / 16D, -20D / 16D, -1D);
            matrixStack.mulPose(Axis.YP.rotationDegrees(-90F));
        }
    }

    @Override
    public List<OBJModelInstance<EntityCargoPlane>> getModels(EntityCargoPlane entity) {
        return getModelFromType(entity);
    }

    private static List<OBJModelInstance<EntityCargoPlane>> getModelFromType(EntityCargoPlane plane) {
        switch (plane.getPlaneType()) {
            default:
            case OAK:
                return OAK_MODEL;
            case DARK_OAK:
                return DARK_OAK_MODEL;
            case SPRUCE:
                return SPRUCE_MODEL;
            case JUNGLE:
                return JUNGLE_MODEL;
            case BIRCH:
                return BIRCH_MODEL;
            case ACACIA:
                return ACACIA_MODEL;
            case WARPED:
                return WARPED_MODEL;
            case CRIMSON:
                return CRIMSON_MODEL;
            // CHANGED
            case WHITE_TERRACOTTA:
                return WHITE_TERRACOTTA_MODEL;
            case LIGHT_GRAY_CONCRETE:
                return LIGHT_GRAY_CONCRETE_MODEL;
            case WHITE_CONCRETE:
                return WHITE_CONCRETE_MODEL;
            case LIGHT_BLUE_TERRACOTTA:
                return LIGHT_BLUE_TERRACOTTA_MODEL;
            case ORANGE_TERRACOTTA:
                return ORANGE_TERRACOTTA_MODEL;
            case LIGHT_GRAY_TERRACOTTA:
                return LIGHT_GRAY_TERRACOTTA_MODEL;
            case RED_TERRACOTTA:
                return RED_TERRACOTTA_MODEL;
            case GREEN_TERRACOTTA:
                return GREEN_TERRACOTTA_MODEL;
        }
    }

    private static List<OBJModelInstance<EntityCargoPlane>> getPlaneModel(ResourceLocation texture) {
        List<OBJModelInstance<EntityCargoPlane>> models = new ArrayList<>(MODELS);
        models.add(new OBJModelInstance<>(
                new OBJModel(
                        new ResourceLocation(Main.MODID, "models/entity/cargo_plane.obj")
                ),
                new OBJModelOptions<>(
                        texture,
                        new Vector3d(0D, 8D / 16D, 0D),
                        new Rotation(180F, Axis.YP),
                        (plane, matrixStack, partialTicks) -> {
                        }
                )
        ));
        return models;
    }

}

