package org.reis.game;

import org.reis.game.physics.Body;

abstract class Entity extends Body {
    Entity(World world) {
        super(world);
    }
}
