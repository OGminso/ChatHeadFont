package net.minso.chathead.API;

/**
 * Enum representing different sources from which to retrieve player skin information.
 * These sources determine where the skin data will be fetched from (e.g., Mojang, Crafatar, Minotar).
 */
public enum SkinSource {
    /**
     * Represents the source for retrieving player skin information from Mojang.
     * Skin data will be fetched directly from Mojang's session server.
     */
    MOJANG,

    /**
     * Represents the source for retrieving player skin information from Crafatar.
     * Skin data will be fetched from the Crafatar service.
     */
    CRAFATAR,

    /**
     * Represents the source for retrieving player skin information from Minotar.
     * Skin data will be fetched from the Minotar service.
     */
    MINOTAR

    //TODO Add support to get skin from playerprofile
}
