package com.rassvet_ii.cmmce;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Optional;

public class BBExpander {
    public static boolean shouldExpand(Entity entity) {
//        return EntityType.getId(entity.getType()).equals(Identifier.of("minecraft:sheep"));
        return true;
    }

    @Nullable
    public static Box expand(Entity entity, Box box) {
        var encompassBox = (((ICMMCEBoxHolder) entity)).cmmce$getEncompassBox();
        if (encompassBox == null) return box;

        return box.union(encompassBox.offset(entity.getPos()));
    }

    public static <E extends Entity, R extends EntityRenderer<E, S>, S extends EntityRenderState> Optional<Box> createEncompassBox(
            E entity,
            R renderer,
            S state
    ) {
        if (!shouldExpand(entity)) return Optional.empty();

        var builder = new EncompassBoxBuilder();
        renderer.render(
                state, new MatrixStack(),
                layer -> (layer.getName().startsWith("entity"))
                        ? builder
                        : DoNothingVertexConsumer.INSTANCE,
                15
        );

        return Optional.of(builder.build());
    }

    private static class DoNothingVertexConsumer implements VertexConsumer {
        public static final DoNothingVertexConsumer INSTANCE = new DoNothingVertexConsumer();

        @Override
        public VertexConsumer vertex(float x, float y, float z) {
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            return this;
        }

        @Override
        public VertexConsumer texture(float u, float v) {
            return this;
        }

        @Override
        public VertexConsumer overlay(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer light(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            return this;
        }
    }
    private static class EncompassBoxBuilder extends DoNothingVertexConsumer {
        private final Box.Builder box = new Box.Builder();
        private final Vector3f buf = new Vector3f();
        private boolean shouldAdd = false;

        public Box build() {
            if (shouldAdd) box.encompass(buf);
            return this.box.build();
        }

        @Override
        public VertexConsumer vertex(float x, float y, float z) {
            if (shouldAdd) box.encompass(buf);
            buf.set(x, y, z);
            shouldAdd = buf.isFinite() && buf.lengthSquared() < 10;
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            shouldAdd = shouldAdd && alpha == 255;
            return this;
        }
    }
}
