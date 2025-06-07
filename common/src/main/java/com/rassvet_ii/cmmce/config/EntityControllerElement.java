package com.rassvet_ii.cmmce.config;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownControllerElement;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class EntityControllerElement extends AbstractDropdownControllerElement<EntityType<?>, Identifier> {
    private final EntityController entityController;

    public EntityControllerElement(EntityController control, YACLScreen screen, Dimension<Integer> dim) {
        super(control, screen, dim);
        this.entityController = control;
    }

    @Override
    public List<Identifier> computeMatchingValues() {
        return EntityController.getCandidates(this.inputField).toList();
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
