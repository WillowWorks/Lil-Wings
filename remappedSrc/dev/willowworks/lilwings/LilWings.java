package dev.willowworks.lilwings;

import dev.willowworks.lilwings.client.LilWingsClient;
import dev.willowworks.lilwings.registry.LilWingsBlocks;
import dev.willowworks.lilwings.registry.LilWingsEntities;
import dev.willowworks.lilwings.registry.LilWingsItems;
import dev.willowworks.lilwings.registry.LilWingsParticles;
import dev.willowworks.lilwings.registry.entity.Butterfly;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib3.GeckoLib;

@Mod(LilWings.MODID)
public class LilWings {

    public static final String MODID = "lilwings";

    public LilWings() {
        //TODO Finish Item Entries for book
        //TODO Add recipes for the stuff
        //TODO Add achievements (I can't believe its not butter!) - make butter (Social butterfly) - catch all butterflies
        GeckoLib.initialize();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::init);
        bus.addListener(this::initClient);
        bus.addListener(this::onComplete);
        bus.addListener(LilWingsEntities::attributeEvent);
        bus.addListener(LilWingsClient::addLayers);
        bus.addListener(LilWingsClient::particleEvent);

        LilWingsEntities.ENTITIES.register(bus);
        LilWingsEntities.ITEMS.register(bus);
        LilWingsItems.ITEMS.register(bus);

        LilWingsBlocks.BLOCKS.register(bus);
        LilWingsBlocks.ITEMS.register(bus);
        LilWingsBlocks.TILES.register(bus);
        LilWingsParticles.PARTICLES.register(bus);
        ForgeMod.enableMilkFluid();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void init(FMLCommonSetupEvent event) {
        //TODO Needs spawn rules
        CauldronBehavior.registerBucketBehavior(LilWingsItems.MILK_INTERACTION);
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(Items.MILK_BUCKET, LilWingsItems.FILL_MILK);
        LilWingsItems.MILK_INTERACTION.put(Items.MILK_BUCKET, LilWingsItems.FILL_MILK);
        MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock interactEvent) -> {
            if(interactEvent.getPlayer().isSneaking()) {
                if (interactEvent.getItemStack().isOf(Items.GLASS_BOTTLE)) {
                    Direction face = interactEvent.getFace();
                    BlockPos glassBottlePos = interactEvent.getPos();
                    if(face != null) {
                        glassBottlePos = glassBottlePos.offset(face);
                    }
                    if(!interactEvent.getPlayer().getWorld().getBlockState(glassBottlePos).isAir()) return;
                    interactEvent.getPlayer().getWorld().setBlockState(glassBottlePos, LilWingsBlocks.BUTTERFLY_JAR.get().getDefaultState(), 3);
                    if(!interactEvent.getPlayer().isCreative()) interactEvent.getItemStack().decrement(1);
                    interactEvent.setCanceled(true);
                    interactEvent.setCancellationResult(ActionResult.SUCCESS);
                }
            }
        });
    }

    public void initClient(FMLClientSetupEvent event) {
        LilWingsClient.init();
    }

    public void onComplete(FMLLoadCompleteEvent event) {
        LilWingsEntities.addSpawnPlacements();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void addButterflies(BiomeLoadingEvent event) {
        switch (event.getCategory()) {
            case TAIGA -> addButterfly(event, LilWingsEntities.APONI_BUTTERFLY);
            case JUNGLE -> addButterfly(event, LilWingsEntities.PAINTED_PANTHER_BUTTERFLY);
            case SWAMP -> addButterfly(event, LilWingsEntities.SWAMP_HOPPER_BUTTERFLY);
            case MUSHROOM -> addButterfly(event, LilWingsEntities.SHROOM_SKIPPER_BUTTERFLY);
            case FOREST -> addButterfly(event, LilWingsEntities.RED_APPLEFLY_BUTTERFLY);
            default -> {
                if(event.getName() != null) {
                    switch (event.getName().getPath()) {
                        case "sunflower_plains" -> addButterfly(event, LilWingsEntities.BUTTER_GOLD_BUTTERFLY);
                        case "meadow" -> addButterfly(event, LilWingsEntities.CLOUDY_PUFF_BUTTERFLY);
                        case "flower_forest" -> addButterfly(event, LilWingsEntities.CRYSTAL_PUFF_BUTTERFLY);
                        case "end_highland" -> addButterfly(event, LilWingsEntities.ENDER_WING_BUTTERFLY);
                        case "lush_caves" -> addButterfly(event, LilWingsEntities.GRAYLING_BUTTERFLY);
                        case "frozen_peaks" -> addButterfly(event, LilWingsEntities.WHITE_FOX_BUTTERFLY);
                    }
                }
            }
        }
    }

    private void addButterfly(BiomeLoadingEvent event, Butterfly butterfly) {
        event.getSpawns().spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(butterfly.entityType().get(),75, 2, 2));
    }
}