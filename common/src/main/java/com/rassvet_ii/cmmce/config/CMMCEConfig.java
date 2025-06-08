package com.rassvet_ii.cmmce.config;

import com.google.gson.*;
import dev.architectury.platform.Platform;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class CMMCEConfig {
    public static final ConfigClassHandler<CMMCEConfig> HANDLER = ConfigClassHandler.createBuilder(CMMCEConfig.class)
            .id(ResourceLocation.parse("cmmce:config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(Platform.getConfigFolder().resolve("cmmce.json5"))
                    .setJson5(true)
                    .appendGsonBuilder((builder) -> builder.registerTypeHierarchyAdapter(EntityType.class, new EntityTypeAdapter()))
                    .build())
            .build();

    @SerialEntry
    public FilterMode filterMode = FilterMode.Enable;

    @SerialEntry
    public boolean renderDebugBox = false;

    @SerialEntry
    public List<EntityType<?>> filterEntities = List.of();
    public static final List<EntityType<?>> PRESET = List.of(
            EntityType.ARMADILLO,
            EntityType.AXOLOTL,
//            EntityType.BAT,
            EntityType.BEE, // maybe
            EntityType.BLAZE,
            EntityType.BREEZE,
            EntityType.CAT,
            EntityType.CAVE_SPIDER,
            EntityType.CHICKEN,
            EntityType.COD,
            EntityType.COW,
            EntityType.DOLPHIN,
            EntityType.ENDERMITE,
            EntityType.FOX,
            EntityType.FROG,
            EntityType.GLOW_SQUID,
            EntityType.GOAT,
            EntityType.GUARDIAN,
            EntityType.HOGLIN,
            EntityType.MAGMA_CUBE,
            EntityType.MOOSHROOM,
            EntityType.OCELOT,
            EntityType.PANDA,
            EntityType.PARROT,
            EntityType.PHANTOM,
            EntityType.PIG,
            EntityType.POLAR_BEAR,
            EntityType.PUFFERFISH,
            EntityType.RABBIT,
            EntityType.SALMON,
            EntityType.SHEEP,
            EntityType.SHULKER,
            EntityType.SILVERFISH,
            EntityType.SLIME,
            EntityType.SNIFFER,
            EntityType.SPIDER,
            EntityType.SQUID,
            EntityType.TADPOLE,
//            EntityType.TROPICAL_FISH
            EntityType.TURTLE,
            EntityType.VEX,
//            EntityType.WARDEN
            EntityType.WOLF,
            EntityType.ZOGLIN
    );

//    public List<EntityType<?>> filterOverride = List.of(
//            EntityType.ENDER_DRAGON
//    );

    public FilterMode getFilterMode() {
        return filterMode;
    }

    public void setFilterMode(FilterMode filterMode) {
        this.filterMode = filterMode;
    }

    public boolean renderDebugBox() {
        return renderDebugBox;
    }

    public void setRenderDebugBox(boolean renderDebugBox) {
        this.renderDebugBox = renderDebugBox;
    }

    public List<EntityType<?>> getFilterEntities() {
        return filterEntities.stream().filter(Objects::nonNull).toList();
    }

    public void setFilterEntities(List<EntityType<?>> filterEntities) {
        this.filterEntities = filterEntities;
    }

    public enum FilterMode implements NameableEnum {
        Enable,
        BlackList,
        WhiteList,
        Disable;

        public boolean shouldExpand(boolean containedInFilter) {
            return switch (this) {
                case Enable -> true;
                case BlackList -> !containedInFilter;
                case WhiteList -> containedInFilter;
                case Disable -> false;
            };
        }

        @Override
        public Component getDisplayName() {
            return switch (this) {
                case Enable -> Component.literal("✔ ").copy()
                        .append(Component.translatable("options.cmmce.filtering_mode.enabled"));
                case BlackList -> Component.literal("\uD83D\uDCDD ").copy()
                        .append(Component.translatable("options.cmmce.filtering_mode.black_list"));
                case WhiteList -> Component.literal("\uD83D\uDCDD ").copy()
                        .append(Component.translatable("options.cmmce.filtering_mode.white_list"));
                case Disable -> Component.literal("✖ ").copy()
                        .append(Component.translatable("options.cmmce.filtering_mode.disabled"));
            };
        }

        public Component getDisplayNameWithoutIcon() {
            return switch (this) {
                case Enable -> Component.translatable("options.cmmce.filtering_mode.enabled");
                case BlackList -> Component.translatable("options.cmmce.filtering_mode.black_list");
                case WhiteList -> Component.translatable("options.cmmce.filtering_mode.white_list");
                case Disable -> Component.translatable("options.cmmce.filtering_mode.disabled");
            };
        }

        public Component getDescriptionText() {
            UnaryOperator<Style> style = s -> s.applyFormats(ChatFormatting.YELLOW, ChatFormatting.BOLD);
            return switch (this) {
                case Enable -> Component.translatable("options.cmmce.filtering_mode.template",
                        Component.translatable("options.cmmce.filtering_mode.enabled").withStyle(style),
                        Component.translatable("options.cmmce.filtering_mode.enabled.description"));
                case BlackList -> Component.translatable("options.cmmce.filtering_mode.template",
                        Component.translatable("options.cmmce.filtering_mode.black_list").withStyle(style),
                        Component.translatable("options.cmmce.filtering_mode.black_list.description"));
                case WhiteList -> Component.translatable("options.cmmce.filtering_mode.template",
                        Component.translatable("options.cmmce.filtering_mode.white_list").withStyle(style),
                        Component.translatable("options.cmmce.filtering_mode.white_list.description"));
                case Disable -> Component.translatable("options.cmmce.filtering_mode.template",
                        Component.translatable("options.cmmce.filtering_mode.disabled").withStyle(style),
                        Component.translatable("options.cmmce.filtering_mode.disabled.description"));
            };
        }
    }

    public static class EntityTypeAdapter
            implements JsonSerializer<EntityType<?>>, JsonDeserializer<EntityType<?>> {

        @Override
        public EntityType<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return EntityType.byString(json.getAsString()).orElse(null);
        }

        @Override
        public JsonElement serialize(EntityType<?> src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive((EntityType.getKey(src).toString()));
        }
    }
}
