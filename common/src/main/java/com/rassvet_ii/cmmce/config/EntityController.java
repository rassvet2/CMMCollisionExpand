package com.rassvet_ii.cmmce.config;

import com.google.common.base.Strings;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownController;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class EntityController extends AbstractDropdownController<EntityType<?>> {
    public EntityController(Option<EntityType<?>> option) {
        super(option);
    }

    @Override
    public String getString() {
        return EntityType.getId(this.option.pendingValue()).toString();
    }

    @Override
    public void setFromString(String value) {
        this.option.requestSet(EntityType.get(value).orElse(this.option.pendingValue()));
    }

    @Override
    public Text formatValue() {
        return Text.literal(this.getString());
    }

    @Override
    public boolean isInputValid(String input) {
//        return EntityType.get(input).isPresent();
        return true;
    }

    @Override
    protected String getValidValue(String value, int offset) {
        return getCandidates(value)
                .skip(offset)
                .findFirst()
                .map(Identifier::toString)
                .orElseGet(this::getString);
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new EntityControllerElement(this, screen, widgetDimension);
    }

    public static Stream<Identifier> getCandidates(String value) {
        String[] sep = value.split(":", 2);
        Optional<String> namespace = sep.length == 1 ? Optional.empty() : Optional.of(sep[0]);
        String path = (namespace.isPresent() ? sep[1] : sep[0]);

        return Registries.ENTITY_TYPE.getIds().stream()
                .filter((id) -> {
                    if (namespace.isPresent() && !id.getNamespace().contains(namespace.get())) return false;
                    if (path.isBlank()) return true;
                    String[] paths = (namespace.isPresent() ? sep[1] : sep[0]).split("_");
                    return Arrays.stream(paths).allMatch(id.getPath()::contains);
                })
                .sorted(Comparator.<Identifier, Integer>comparing((id) -> Strings.commonPrefix(path, id.getPath()).length())
                        .thenComparing(Identifier::compareTo));
    }
}
