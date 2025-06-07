package com.rassvet_ii.cmmce.config;

import com.google.common.collect.Maps;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownControllerElement;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public class EntityControllerElement extends AbstractDropdownControllerElement<EntityType<?>, Identifier> {
    private final EntityController entityController;
    protected EntityType<?> currentItem = null;
    protected Map<Identifier, EntityType<?>> matchingEntities = Maps.newHashMap();

    public EntityControllerElement(EntityController control, YACLScreen screen, Dimension<Integer> dim) {
        super(control, screen, dim);
        this.entityController = control;
    }

    @Override
    public List<Identifier> computeMatchingValues() {
        List<Identifier> identifiers = EntityController.getCandidates(this.inputField).toList();
        this.currentItem = EntityType.get(this.inputField).orElse(null);

        for(Identifier id : identifiers) {
            this.matchingEntities.put(id, Registries.ENTITY_TYPE.get(id));
        }

        System.out.println(identifiers);
        return identifiers;
    }

    @Override
    public String getString(Identifier identifier) {
        return identifier.toString();
    }

    protected Text getValueText() {
        return !this.inputField.isEmpty() && this.entityController != null
                ? this.inputFieldFocused
                    ? Text.literal(this.inputField)
                    : this.entityController.option().pendingValue().getName()
                : super.getValueText();
    }
}
