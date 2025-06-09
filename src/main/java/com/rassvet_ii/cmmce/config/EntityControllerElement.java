package com.rassvet_ii.cmmce.config;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownControllerElement;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class EntityControllerElement extends AbstractDropdownControllerElement<EntityType<?>, ResourceLocation> {
    private final EntityController entityController;

    public EntityControllerElement(EntityController control, YACLScreen screen, Dimension<Integer> dim) {
        super(control, screen, dim);
        this.entityController = control;
    }

    @Override
    public List<ResourceLocation> computeMatchingValues() {
        return EntityController.getCandidates(this.inputField).toList();
    }

    @Override
    public String getString(ResourceLocation identifier) {
        return identifier.toString();
    }

    protected Component getValueText() {
        return !this.inputField.isEmpty() && this.entityController != null
                ? this.inputFieldFocused
                    ? Component.literal(this.inputField)
                    : this.entityController.option().pendingValue().getDescription()
                : super.getValueText();
    }
}
