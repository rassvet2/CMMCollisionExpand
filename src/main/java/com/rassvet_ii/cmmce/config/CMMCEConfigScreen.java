package com.rassvet_ii.cmmce.config;

import com.rassvet_ii.cmmce.Constants;
import com.rassvet_ii.cmmce.config.CMMCEConfig.FilterMode;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.gui.controllers.LabelController;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class CMMCEConfigScreen {
    public static Screen build(@Nullable Screen parent) {
        Holders holders = new Holders();
        return YetAnotherConfigLib.create(CMMCEConfig.HANDLER, (defaults, config, builder) -> builder
                .title(Component.literal("CMM Collision Expand"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("options.cmmce.general"))
                        .group(buildFilterSettings(defaults, config, holders))
                        .group(holders.filterList.set(buildFilterList(defaults, config)))
                        .group(buildDebugSettings(defaults, config))
                        .group(buildAbout())
                        .build())
                .save(CMMCEConfig.HANDLER::save)
        ).generateScreen(parent);
    }

    private static OptionGroup buildFilterSettings(
            CMMCEConfig defaults, CMMCEConfig config, Holders holders
    ) {
        return OptionGroup.createBuilder()
                .name(Component.translatable("options.cmmce.filter_settings"))
                .option(holders.filterMode.set(Option.<FilterMode>createBuilder()
                        .name(Component.translatable("options.cmmce.filtering_mode.name"))
                        .description(OptionDescription.createBuilder()
                                .text(Component.translatable("options.cmmce.filtering_mode.description"))
                                .text(Component.empty())
                                .text(FilterMode.Enable.getDescriptionText())
                                .text(FilterMode.BlackList.getDescriptionText())
                                .text(FilterMode.WhiteList.getDescriptionText())
                                .text(FilterMode.Disable.getDescriptionText())
                                .build())
                        .controller(option -> EnumControllerBuilder.create(option).enumClass(FilterMode.class))
                        .binding(defaults.filterMode, config::getFilterMode, config::setFilterMode)
                        .addListener((option, event) ->
                                holders.filterList.get().ifPresent((it) -> it.setAvailable(switch (option.pendingValue()) {
                                    case Enable, Disable -> false;
                                    case BlackList, WhiteList -> true;
                                })))
                        .build()))
                .option(ButtonOption.createBuilder()
                        .name(Component.translatable("options.cmmce.load_preset.name"))
                        .text(Component.literal("▶"))
                        .description(OptionDescription.createBuilder()
                                .text(Component.translatable("options.cmmce.load_preset.description.line0"))
                                .text(Component.translatable("options.cmmce.load_preset.description.line1",
                                        FilterMode.WhiteList.getDisplayNameWithoutIcon().copy().withStyle(STRONG)))
                                .text(Component.translatable("options.cmmce.load_preset.description.line2").withStyle(WEAK))
                                .build())
                        .action((screen, b) -> {
                            holders.filterMode.get().orElseThrow().requestSet(FilterMode.WhiteList);
                            holders.filterList.get().orElseThrow().requestSet(CMMCEConfig.PRESET);
                        })
                        .build())
                .build();
    }

    private static ListOption<EntityType<?>> buildFilterList(
            CMMCEConfig defaults, CMMCEConfig config
    ) {
        return ListOption.<EntityType<?>>createBuilder()
                .name(Component.translatable("options.cmmce.filter_list.name"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("options.cmmce.filter_list.description")
                                .withStyle(ChatFormatting.UNDERLINE))
                        .text(Component.empty())
                        .text(FilterMode.Enable.getDescriptionText())
                        .text(FilterMode.BlackList.getDescriptionText())
                        .text(FilterMode.WhiteList.getDescriptionText())
                        .text(FilterMode.Disable.getDescriptionText())
                        .build())
                .insertEntriesAtEnd(true)
                .customController(EntityController::new)
                .initial(EntityType.CREEPER)
                .binding(defaults.filterEntities, config::getFilterEntities, config::setFilterEntities)
                .build();
    }

    private static OptionGroup buildDebugSettings(CMMCEConfig defaults, CMMCEConfig config) {
        return OptionGroup.createBuilder()
                .name(Component.translatable("options.cmmce.debug_settings"))
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("options.cmmce.debug_box_render.name"))
                        .description(OptionDescription.of(Component.translatable("options.cmmce.debug_box_render.description")))
                        .controller(TickBoxControllerBuilder::create)
                        .binding(defaults.renderDebugBox, config::renderDebugBox, config::setRenderDebugBox)
                        .build())
                .build();
    }

    private static OptionGroup buildAbout() {

        List<Component> lines = List.of(
                Component.translatable("options.cmmce.about.line0",
                        Component.translatable("options.cmmce.about.line0.cmmrp")
                                .withStyle(STRONG).withStyle(url(Constants.CMMRP_LINK))),
                Component.translatable("options.cmmce.about.line1",
                        Component.translatable("options.cmmce.about.line1.github")
                                .withStyle(url(Constants.CMMCE_LINK))),
                Component.empty(),
                Component.translatable("options.cmmce.about.credits").copy().withStyle(s -> s.withBold(true)),
                Component.empty(),
                buildCredit(
                        Component.translatable("options.cmmce.about.cmmrp"),
                        Constants.CMMRP_LINK),
                Component.empty(),
                buildCredit(
                        Component.translatable("options.cmmce.about.yacl"),
                        Constants.YACL_LINK),
                Component.empty(),
                buildCredit(
                        Component.translatable("options.cmmce.about.architectury"),
                        Constants.ARCH_LINK),
                Component.empty(),
                buildCredit(
                        Component.translatable("options.cmmce.about.stonecutter"),
                        Constants.SC_LINK),
                Component.empty(),
                buildCredit(
                        Component.translatable("options.cmmce.about.stonecutter_template"),
                        Constants.SCT_LINK));

        return OptionGroup.createBuilder()
                .name(Component.translatable("options.cmmce.about"))
                .option(Option.<Component>createBuilder()
                        .name(Component.translatable("options.cmmce.about"))
                        .description(OptionDescription.createBuilder().text(lines).build())
                        .customController(LabelController::new)
                        .binding(Binding.immutable(ComponentUtils.formatList(lines, Component.literal("\n"))))
                        .build())
                .build();
    }

    private static Component buildCredit(
            Component title,
//            Component description,
            URI link
    ) {
        return ComponentUtils.formatList(List.of(
                title.copy().withStyle(STRONG).withStyle(url(link)),
                Component.literal(link.toString()).copy().withStyle(WEAK).withStyle(url(link))
                ), Component.literal("\n"));
    }

    private static class Holder<T> {
        private @Nullable T value = null;

        public Optional<T> get() {
            return Optional.ofNullable(value);
        }

        public T set(T value) {
            this.value = value;
            return value;
        }
    }

    private static class Holders {
        public final Holder<Option<FilterMode>> filterMode = new Holder<>();
        public final Holder<ListOption<EntityType<?>>> filterList = new Holder<>();
    }

    private static final UnaryOperator<Style> STRONG = s -> s.applyFormats(ChatFormatting.YELLOW, ChatFormatting.BOLD);
    private static final UnaryOperator<Style> WEAK = s -> s.applyFormats(ChatFormatting.GRAY);

    private static UnaryOperator<Style> url(URI url) {
        return s -> s.applyFormats(ChatFormatting.UNDERLINE)
                //? if >= 1.21.5 {
                .withClickEvent(new ClickEvent.OpenUrl(url));
        //?} else {
        /*.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url.toString()));
         *///?}

    }
}
