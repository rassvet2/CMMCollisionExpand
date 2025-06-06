package com.rassvet_ii.cmmce;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.Box;
import org.joml.Vector3f;

import java.util.Map;

public class EncompassBoxBuilder implements VertexConsumerProvider {
    private final VertexConsumerProvider source;
    private final Map<RenderLayer, VertexConsumer> layers = new Object2ObjectOpenHashMap<>();
    private final Box.Builder builder = new Box.Builder();
    private final Vector3f buf = new Vector3f();
    private boolean validVertex = false;

    public EncompassBoxBuilder(VertexConsumerProvider source) {
        this.source = source;
    }

    @Override
    public VertexConsumer getBuffer(RenderLayer layer) {
        return layers.computeIfAbsent(layer, k -> (
                layer.getName().startsWith("entity") && !layer.getName().equals("entity_shadow")
                        ? new Wrapper(source.getBuffer(layer), this)
                        : source.getBuffer(layer)
        ));
    }

    public Box build() {
        if (validVertex) builder.encompass(buf);
        return builder.build();
    }

    public VertexConsumerProvider unwrap() {
        return source;
    }

    private void vertex(float x, float y, float z) {
        if (validVertex) builder.encompass(buf);
        buf.set(x, y, z);
        validVertex = buf.isFinite();
        if (buf.lengthSquared() > 100) {
//            System.out.println("Too far vertex. Ignoring.");
            invalidate();
        }
    }

    private void invalidate() {
        validVertex = false;
    }

    private static class Wrapper implements VertexConsumer {
        private final VertexConsumer source;
        private final EncompassBoxBuilder builder;

        public Wrapper(VertexConsumer source, EncompassBoxBuilder builder) {
            this.source = source;
            this.builder = builder;
        }

        @Override
        public VertexConsumer vertex(float x, float y, float z) {
            source.vertex(x, y, z);
            builder.vertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            source.color(red, green, blue, alpha);
            if (alpha != 255) builder.invalidate();
            return this;
        }

        @Override
        public VertexConsumer texture(float u, float v) {
            source.texture(u, v);
            return this;
        }

        @Override
        public VertexConsumer overlay(int u, int v) {
            source.overlay(u, v);
            return this;
        }

        @Override
        public VertexConsumer light(int u, int v) {
            source.light(u, v);
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            source.normal(x, y, z);
            return this;
        }
    }
}
