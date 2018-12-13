package org.reis.server.client;

import org.joml.Vector2d;
import org.joml.Vector3d;

import java.util.Map;
import java.util.TreeMap;

import static org.lwjgl.glfw.GLFW.*;

class KeyboardMouseController {
    private Map<Integer,Vector3d> senses;
    Vector3d move;
    Vector2d turn;

    KeyboardMouseController() {
        senses = new TreeMap<>();
        senses.put(GLFW_KEY_S, new Vector3d(0, 0,1));
        senses.put(GLFW_KEY_W, new Vector3d(0, 0,-1));
        senses.put(GLFW_KEY_D, new Vector3d(1, 0, 0));
        senses.put(GLFW_KEY_A, new Vector3d(-1, 0,0));
        senses.put(GLFW_KEY_SPACE, new Vector3d(0, 1, 0));
        senses.put(GLFW_KEY_LEFT_SHIFT, new Vector3d(0, -1, 0));

        move = new Vector3d();
        turn = new Vector2d(); // torque?
    }

    void keyboard(int key, int act, int mod) {
        Vector3d sense = senses.get( key );
        if ( sense != null ) {
            if ( act == GLFW_PRESS ) move.add( sense );
            if ( act == GLFW_RELEASE ) move.sub( sense );
        }
    }

    void mouse(double x, double y) {
        turn.sub( x, y );
    }
}
