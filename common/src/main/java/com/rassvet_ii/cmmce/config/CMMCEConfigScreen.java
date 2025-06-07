package com.rassvet_ii.cmmce.config;

import com.rassvet_ii.cmmce.Constants;
import com.rassvet_ii.cmmce.config.CMMCEConfig.FilterMode;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.gui.controllers.LabelController;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EntityType;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class CMMCEConfigScreen {
    public static Screen build(@Nullable Screen parent) {
        Holders holders = new Holders();
        return YetAnotherConfigLib.create(CMMCEConfig.HANDLER, (defaults, config, builder) -> builder
                .title(Text.of("CMM Collision Expand"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("options.cmmce.general"))
                        .tooltip(Text.of("General Settings"))
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
                .name(Text.translatable("options.cmmce.filter_settings"))
                .option(holders.filterMode.set(Option.<FilterMode>createBuilder()
                        .name(Text.translatable("options.cmmce.filtering_mode.name"))
                        .description(OptionDescription.createBuilder()
                                .text(Text.translatable("options.cmmce.filtering_mode.description"))
                                .text(Text.empty())
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
                        .name(Text.translatable("options.cmmce.load_preset.name"))
                        .text(Text.of("▶"))
                        .description(OptionDescription.createBuilder()
                                .text(Text.translatable("options.cmmce.load_preset.description.line0"))
                                .text(Text.translatable("options.cmmce.load_preset.description.line1",
                                        FilterMode.WhiteList.getDisplayNameWithoutIcon().copy().styled(STRONG)))
                                .text(Text.translatable("options.cmmce.load_preset.description.line2").styled(WEAK))
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
                .name(Text.translatable("options.cmmce.filter_list.name"))
                .description(OptionDescription.createBuilder()
                        .text(Text.translatable("options.cmmce.filter_list.description")
                                .formatted(Formatting.UNDERLINE))
                        .text(Text.empty())
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
                .name(Text.translatable("options.cmmce.debug_settings"))
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("options.cmmce.debug_box_render.name"))
                        .description(OptionDescription.of(Text.translatable("options.cmmce.debug_box_render.description")))
                        .controller(TickBoxControllerBuilder::create)
                        .binding(defaults.renderDebugBox, config::renderDebugBox, config::setRenderDebugBox)
                        .build())
                .build();
    }

    private static OptionGroup buildAbout() {

        List<Text> lines = List.of(
                Text.translatable("options.cmmce.about.line0",
                        Text.translatable("options.cmmce.about.line0.cmmrp")
                                .styled(STRONG).styled(url(Constants.CMMRP_LINK))),
                Text.translatable("options.cmmce.about.line1",
                        Text.translatable("options.cmmce.about.line1.github")
                                .styled(url(Constants.CMMCE_LINK))),
                Text.empty(),
                Text.translatable("options.cmmce.about.credits").copy().styled(s -> s.withBold(true)),
                Text.empty(),
                Text.translatable("options.cmmce.about.cmmrp").copy().styled(STRONG).styled(url(Constants.CMMRP_LINK)),
                Text.of(Constants.CMMRP_LINK.toString()).copy().styled(WEAK).styled(url(Constants.CMMRP_LINK)),
                Text.empty(),
                Text.translatable("options.cmmce.about.yacl").copy().styled(STRONG).styled(url(Constants.YACL_LINK)),
                Text.of(Constants.YACL_LINK.toString()).copy().styled(WEAK).styled(url(Constants.YACL_LINK)),
                Text.empty()
        );

        return OptionGroup.createBuilder()
                .name(Text.translatable("options.cmmce.about"))
                .option(Option.<Text>createBuilder()
                        .name(Text.translatable("options.cmmce.about"))
                        .description(OptionDescription.createBuilder().text(lines).build())
                        .customController(LabelController::new)
                        .binding(Binding.immutable(Texts.join(lines, Text.of("\n"))))
                        .build())
                .build();
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

    private static final UnaryOperator<Style> STRONG = s -> s.withFormatting(Formatting.YELLOW, Formatting.BOLD);
    private static final UnaryOperator<Style> WEAK = s -> s.withFormatting(Formatting.GRAY);

    private static UnaryOperator<Style> url(URI url) {
        return s -> s.withFormatting(Formatting.UNDERLINE).withClickEvent(new ClickEvent.OpenUrl(url));
    }
}
