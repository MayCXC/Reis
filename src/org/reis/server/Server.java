package org.reis.server;

import org.reis.game.Player;
import org.reis.events.Updatable;
import org.reis.game.World;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public abstract class Server implements Runnable, Updatable {
    private World[] worlds;
    private Map<UUID, Player> players;
    private double tick, tock;

    public void putPlayer(UUID identity, Player player) {
        players.put(identity, player);
    }
    public Player getPlayer(UUID identity) {
        return players.get(identity);
    }

    public World getWorld(int world) {
        return worlds[world];
    }

    public Server() {
        worlds = new World[]{new World()};
        players = new TreeMap<>();
    }

    protected abstract boolean quit();
    protected abstract void input();
    protected abstract double time();
    protected abstract void output();

    public void update(double dt) {
        for (World world: worlds) world.update(dt);
    }

    private void step(double time) {
        tick = tock;
        tock = time;
        update(tock - tick);
    }

    public void run() {
        while (!quit()) {
            input();
            step(time());
            output();
        }
    }

    public String toString() {
        return "1/dt="+(int)(10/(tock-tick))/10.0;
    }
}
