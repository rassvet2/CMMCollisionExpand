package com.rassvet_ii.cmmce;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rassvet_ii.cmmce.mixin.IMultiPhase;
import com.rassvet_ii.cmmce.mixin.IMultiPhaseParameters;
import com.rassvet_ii.cmmce.mixin.ITexture;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.*;

public class EncompassBoxBuilder implements MultiBufferSource {
    private final MultiBufferSource source;
    private final Map<RenderType, VertexConsumer> layers = new Object2ObjectOpenHashMap<>();
    private final AABB.Builder builder = new AABB.Builder();
    private final Vector3f buf = new Vector3f();
    private boolean validVertex = false;

    public EncompassBoxBuilder(MultiBufferSource source) {
        this.source = source;
    }

    @Override
    public @NotNull VertexConsumer getBuffer(RenderType layer) {
        return isEntityBody(layer)
                ? layers.computeIfAbsent(layer, k -> new Wrapper(source.getBuffer(layer), this))
                : source.getBuffer(layer);
    }

//    private static final Set<ResourceLocation> dejavu = Sets.newConcurrentHashSet();
    private boolean isEntityBody(RenderType layer) {
        if (!layer.getName().startsWith("entity") || layer.getName().equals("entity_shadow")) {
            return false;
        }

        @SuppressWarnings("ConstantValue")
        var optional = layer instanceof IMultiPhase phase
                && ((Object) phase.getPhases() instanceof IMultiPhaseParameters params)
                && ((Object) params.getTexture() instanceof ITexture texture)
                ? texture.getId0()
                : Optional.<ResourceLocation>empty();

        // noinspection ConstantValue
        if (optional.isEmpty()) return false;
        var id = optional.get();

//        if (dejavu.add(id)) System.out.println(dejavu);

        // noinspection deprecation
        return !id.equals(TextureAtlas.LOCATION_BLOCKS);
    }

    public AABB build() {
        if (validVertex) builder.include(buf);
        return builder.build();
    }

    public MultiBufferSource unwrap() {
        return source;
    }

    private void vertex(float x, float y, float z) {
        if (validVertex) builder.include(buf);
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
        public @NotNull VertexConsumer addVertex(float x, float y, float z) {
            source.addVertex(x, y, z);
            builder.vertex(x, y, z);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setColor(int red, int green, int blue, int alpha) {
            source.setColor(red, green, blue, alpha);
            if (alpha != 255) builder.invalidate();
            return this;
        }

        @Override
        public @NotNull VertexConsumer setUv(float f, float g) {
            source.setUv(f, g);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setUv1(int i, int j) {
            source.setUv1(i, j);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setUv2(int i, int j) {
            source.setUv2(i, j);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setNormal(float f, float g, float h) {
            source.setNormal(f, g, h);
            return this;
        }
    }
}
