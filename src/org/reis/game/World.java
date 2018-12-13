package org.reis.game;

import org.reis.events.Updatable;

import java.util.LinkedList;
import java.util.List;

public class World implements Updatable {
    private Block[][][] blocks;
    private List<Entity> entities;
    // Fill new world with random blocks for demo
    public World() {
        blocks = new Block[16][16][16];
        for(int z=0; z<blocks.length; z++)
        for(int y=0; y<blocks[z].length; y++)
        for(int x=0; x<blocks[z][y].length; x++)
        if(Math.random() < .16)
            blocks[z][y][x] = new Block();
        entities = new LinkedList<>();
    }

    public Block[][][] getBlocks() {
        return blocks;
    }
    public void addEntity(Entity entity) {
        entities.add(entity);
    }
    public List<Entity> getEntities() {
        return entities;
    }

    @Override
    public void update(double dt) {
        for(Entity entity: entities) entity.update(dt);
    }
}
