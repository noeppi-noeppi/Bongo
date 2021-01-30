package io.github.noeppi_noeppi.mods.bongo.network;

public enum BongoMessageType {
	START,
	STOP,
	CREATE,
    // Will force to update everything on the client that may only be updated depending on the message type.
    FORCE,
    GENERIC
}
