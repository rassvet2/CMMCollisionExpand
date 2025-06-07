package com.rassvet_ii.cmmce;

import com.google.gson.*;
import com.rassvet_ii.cmmce.config.EntityController;
import dev.architectury.platform.Platform;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class CMMCEConfig {
    public static Screen build(@Nullable Screen parent) {
//        ReloadListenerRegistry.register(ResourceType.CLIENT_RESOURCES, );
        return YetAnotherConfigLib.create(CMMCEConfig.HANDLER, (defaults, config, builder) -> builder
                .title(Text.of("CMM Collision Expand"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.of("General"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Filter Settings"))
                                .option(Option.<FilterMode>createBuilder()
                                        .name(Text.of("Filtering Mode"))
                                        .controller(option -> EnumControllerBuilder.create(option).enumClass(FilterMode.class))
                                        .binding(defaults.filterMode, config::getFilterMode, config::setFilterMode)
                                        .build())
                                .build())
                        .option(ListOption.<EntityType<?>>createBuilder()
                                .name(Text.of("Filter List"))
                                .insertEntriesAtEnd(true)
                                .customController(EntityController::new)
                                .initial(EntityType.CREEPER)
                                .binding(defaults.filterEntities, config::getFilterEntities, config::setFilterEntities)
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Debug Settings"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.of("Debug Collision Box"))
                                        .controller(TickBoxControllerBuilder::create)
                                        .binding(defaults.renderDebugBox, config::renderDebugBox, config::setRenderDebugBox)
                                        .build())
                                .build())
                        .build())
                .save(CMMCEConfig.HANDLER::save)
        ).generateScreen(parent);
    }

    public static final ConfigClassHandler<CMMCEConfig> HANDLER = ConfigClassHandler.createBuilder(CMMCEConfig.class)
            .id(Identifier.of("cmmce:config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(Platform.getConfigFolder().resolve("cmmce.json5"))
                    .setJson5(true)
                    .appendGsonBuilder((builder) -> builder.registerTypeHierarchyAdapter(EntityType.class, new EntityTypeAdapter()))
                    .build())
            .build();

    @SerialEntry
    public FilterMode filterMode = FilterMode.WhiteList;

    @SerialEntry
    public boolean renderDebugBox = false;

    @SerialEntry
    public List<EntityType<?>> filterEntities = List.of(
            EntityType.SHEEP,
            EntityType.COW,
            EntityType.PIG,
            EntityType.CHICKEN,
            EntityType.SLIME,
            EntityType.MAGMA_CUBE,
            EntityType.SILVERFISH,
            EntityType.ENDERMITE
    );

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

    public enum FilterMode {
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
