package com.rassvet_ii.cmmce;

import com.rassvet_ii.cmmce.CMMCEConfig.FilterMode;
import com.rassvet_ii.cmmce.config.EntityController;
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
import java.util.function.UnaryOperator;

public class CMMCEConfigScreen {
    public static Screen build(@Nullable Screen parent) {
        return YetAnotherConfigLib.create(CMMCEConfig.HANDLER, (defaults, config, builder) -> builder
                .title(Text.of("CMM Collision Expand"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("options.cmmce.general"))
                        .group(buildFilterSettings(defaults, config))
                        .group(buildFilterList(defaults, config))
                        .group(buildDebugSettings(defaults, config))
                        .group(buildAbout())
                        .build())
                .save(CMMCEConfig.HANDLER::save)
        ).generateScreen(parent);
    }

    private static OptionGroup buildFilterSettings(CMMCEConfig defaults, CMMCEConfig config) {
        return OptionGroup.createBuilder()
                .name(Text.translatable("options.cmmce.filter_settings"))
                .option(Option.<FilterMode>createBuilder()
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
                        .build())
                .option(ButtonOption.createBuilder()
                        .name(Text.of("Button"))
                        .text(Text.of("Text"))
                        .action((screen, b) -> {
                            screen.config.categories().stream()
                                    .flatMap(it -> it.groups().stream())
                                    .filter(it -> it instanceof ListOption<?>)
                                    .filter(it -> it.name().equals(Text.translatable("options.cmmce.filter_list.name")))
                                    .findAny()
                                    .orElseThrow()
                                    .options();
                        })
                        .build())
                .build();
    }

    private static ListOption<?> buildFilterList(CMMCEConfig defaults, CMMCEConfig config) {
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

    private static final UnaryOperator<Style> STRONG = s -> s.withFormatting(Formatting.YELLOW, Formatting.BOLD);
    private static final UnaryOperator<Style> WEAK = s -> s.withFormatting(Formatting.GRAY);

    private static UnaryOperator<Style> url(URI url) {
        return s -> s.withFormatting(Formatting.UNDERLINE).withClickEvent(new ClickEvent.OpenUrl(url));
    }
}
