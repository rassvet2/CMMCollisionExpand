package com.rassvet_ii.cmmce.platforms;

public interface ModPlatform {

    String getModloader();

    boolean isModLoaded(String modId);

    abstract class Invalid implements ModPlatform {
        @Override
        public final String getModloader() {
            throw new AssertionError();
        }

        public final boolean isModLoaded(String modId) {
            throw new AssertionError();
        }
    }
}
