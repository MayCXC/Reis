package org.reis.server.client;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;

import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.system.MemoryUtil.NULL;

public class ClientLWJGL extends Client implements AutoCloseable {
    private long window;
    private GLFWErrorCallback errorCallback;

    // Make GLFW allocations
    private ClientLWJGL( ) {
        super( );

        errorCallback = GLFWErrorCallback.createPrint( System.err );
        errorCallback.set();

        if ( !glfwInit( ) ) throw new IllegalStateException( "glfwInit returned false" );

        glfwDefaultWindowHints();
//        glfwWindowHint(GLFW_SAMPLES, 4);
        window = glfwCreateWindow( 640, 480,"I'm a rectangle!", NULL, NULL );
        glfwSetInputMode( window, GLFW_CURSOR, GLFW_CURSOR_DISABLED );
        glfwShowWindow( window );
    }

    private KeyboardMouseController controller;
    private ImmediateModeRenderer renderer;

    // Attach GLFW callbacks to the keyboard and mouse controller
    @Override
    public void run( ) {
        controller = new KeyboardMouseController();
        glfwSetKeyCallback( window, GLFWKeyCallback.create( (win,key,scn,act,mod) -> {
            controller.keyboard(key,act,mod);
            if ( key == GLFW_KEY_ESCAPE && act == GLFW_PRESS )
                glfwSetWindowShouldClose( window, true );
        } ) );
        glfwSetCursorPosCallback( window, GLFWCursorPosCallback.create( (win,x,y) ->
            controller.mouse(x,y)
        ) );

        glfwMakeContextCurrent( window );
        glfwSwapInterval( 1 );
        renderer = new ImmediateModeRenderer( );

        glfwSetTime(0);
        super.run();
    }

    // Quit when the OS closes the GLFW window
    @Override
    protected boolean quit( ) {
        return glfwWindowShouldClose( window );
    }

    protected void input( ) {
        getPlayer().look(controller.turn);
        getPlayer().walk(controller.move);

        controller.turn.zero();
    }

    // Read time since glfwInit
    @Override
    protected double time() {
        return glfwGetTime();
    }

    // Clear window buffer, transform frame to player, print debug, and swap window buffer.
    protected void output() {
        renderer.render();
        renderer.render( getPlayer( ) );
        renderer.render(
            "¡I'm a bitmap!" + "\n" +
            "t=" + ((int)(glfwGetTime()*10))/10.0 + "\n" +
            super.toString() + "\n" +
            "v=" + getPlayer().getVelocity() + "\n" +
            "p=" + getPlayer().getPosition() + "\n" +
            "o=" + getPlayer().getOrientation()
        );
        glfwSwapBuffers( window );
    }

    // Free GLFW allocations
    @Override
    public void close( ) {
        glfwFreeCallbacks( window );
        glfwDestroyWindow( window );
        glfwTerminate( );
        errorCallback.free();
    }

    // Start client in a new thread, read GLFW events, center cursor.
    public static void main( String... args ) {
        try ( ClientLWJGL client = new ClientLWJGL( ) ) {
            Thread thread = new Thread( client );
            thread.start( );
            while ( thread.isAlive() ) {
                glfwWaitEvents();
                glfwSetCursorPos( client.window,0,0 );
            }
        }
    }
}