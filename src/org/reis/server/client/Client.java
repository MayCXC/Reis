package org.reis.server.client;

import org.reis.game.Player;
import org.reis.server.Server;

import java.util.UUID;

abstract class Client extends Server {
    private UUID identifier;

    private UUID getIdentifier( ) {
        return identifier;
    }

    // Local multi player
    Player getPlayer( ) {
        return getPlayer( getIdentifier( ) );
    }

    Client( ) {
        super( );
        this.identifier = UUID.randomUUID( );
        putPlayer( getIdentifier( ), new Player( getWorld(0) ) );
        // Singleton equivalent player world
        getPlayer().getWorld().addEntity( getPlayer() );
    }
}
