package com.rassvet_ii.cmmce.config;

import com.google.gson.*;
import dev.architectury.platform.Platform;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class CMMCEConfig {
    public static final ConfigClassHandler<CMMCEConfig> HANDLER = ConfigClassHandler.createBuilder(CMMCEConfig.class)
            .id(Identifier.of("cmmce:config"))
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
        public Text getDisplayName() {
            return switch (this) {
                case Enable -> Text.of("✔ ").copy()
                        .append(Text.translatable("options.cmmce.filtering_mode.enabled"));
                case BlackList -> Text.of("\uD83D\uDCDD ").copy()
                        .append(Text.translatable("options.cmmce.filtering_mode.black_list"));
                case WhiteList -> Text.of("\uD83D\uDCDD ").copy()
                        .append(Text.translatable("options.cmmce.filtering_mode.white_list"));
                case Disable -> Text.of("✖ ").copy()
                        .append(Text.translatable("options.cmmce.filtering_mode.disabled"));
            };
        }

        public Text getDisplayNameWithoutIcon() {
            return switch (this) {
                case Enable -> Text.translatable("options.cmmce.filtering_mode.enabled");
                case BlackList -> Text.translatable("options.cmmce.filtering_mode.black_list");
                case WhiteList -> Text.translatable("options.cmmce.filtering_mode.white_list");
                case Disable -> Text.translatable("options.cmmce.filtering_mode.disabled");
            };
        }

        public Text getDescriptionText() {
            UnaryOperator<Style> style = s -> s.withFormatting(Formatting.YELLOW, Formatting.BOLD);
            return switch (this) {
                case Enable -> Text.translatable("options.cmmce.filtering_mode.template",
                        Text.translatable("options.cmmce.filtering_mode.enabled").styled(style),
                        Text.translatable("options.cmmce.filtering_mode.enabled.description"));
                case BlackList -> Text.translatable("options.cmmce.filtering_mode.template",
                        Text.translatable("options.cmmce.filtering_mode.black_list").styled(style),
                        Text.translatable("options.cmmce.filtering_mode.black_list.description"));
                case WhiteList -> Text.translatable("options.cmmce.filtering_mode.template",
                        Text.translatable("options.cmmce.filtering_mode.white_list").styled(style),
                        Text.translatable("options.cmmce.filtering_mode.white_list.description"));
                case Disable -> Text.translatable("options.cmmce.filtering_mode.template",
                        Text.translatable("options.cmmce.filtering_mode.disabled").styled(style),
                        Text.translatable("options.cmmce.filtering_mode.disabled.description"));
            };
        }
    }

    public static class EntityTypeAdapter
            implements JsonSerializer<EntityType<?>>, JsonDeserializer<EntityType<?>> {

        @Override
        public EntityType<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return EntityType.get(json.getAsString()).orElse(null);
        }

        @Override
        public JsonElement serialize(EntityType<?> src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive((EntityType.getId(src).toString()));
        }
    }
}
