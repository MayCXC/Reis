# Reis
Like in *reislust*, which means wanderlust in dutch. Originally implemented in C and pure OpenGL, now in Java with [LWJGL](https://www.lwjgl.org), specifically [GLFW](https://www.glfw.org/) and [JOML](https://github.com/JOML-CI/JOML). All implementations are kept as decoupled and replaceable as possible.

## Idea
One of the bestselling video games of all time was a voxel-based exploration game that started as a java proof of concept. Myself and many others had a great time playing it, but the amount of time and effort put into the game by its community far outmatched that of its original developers, who did not realize its true potential: the mod community. More and more AAA game titles are released unfinished because they do not see the potential in the mod community: instead of trying and failing to release a full game, release a proof of concept, if it is well received immediately complete a mod API, then implement the entire game with it. The mod community will take care of the unfinished parts for free.

## Design Goals
Create a game client and server, create a voxel and physics engine, finish a mod API, then implement an exploration game with that API. Every part of the engine should be accessible by the API, completely decoupled, and compatible with other client and server implementations, in order to take full advantage of potential fans. Release it for free.

## Progress
### Client and server:
-	Runtime single player works, no networking or local multiplayer yet.

### Engine:
-	Voxel rendering works, but is not performant.
-	Physics enables player movement, floor collision, and friction.

### Mod API and game:
-	World and player completed for engine testing, no gameplay yet.

##  Documentation
The documentation for the client, server, and engine should be simple enough to fit in this readme. The API and game will be separate projects with much longer documentation. Here are descriptions of the classes and methods in the client:

------
### ClientLWJGL
*server/client/ClientLWJGL.java*

A client implementation in Java using [GLFW]( https://www.glfw.org/) with [LWJGL](https://www.lwjgl.org/). It takes input from a keyboard and mouse, and displays output with immediate mode OpenGL.
#### main
Starts a client in a new thread, reads GLFW events, and centers the cursor.
#### run
Creates a new keyboard and mouse controller, a new GLFW window, and starts as a server.
#### quit, input, time, output
Extends `org.reis.server` using GLFW.
#### close
Frees memory allocated by GLFW.

------
### ImmediateModeRenderer
*server/client/ImmediateModeRenderer.java*

Renders `org.reis.game.Player`, `org.reis.game.World`, and `String` debug text.

#### render
If given no input, clear the output buffer.

If given an `org.reis.game.Player` input, translate to the position of that player, and rotate to the orientation of that player.

If given an `org.reis.game.World` input, render the blocks of that world.

If given a `String` input, print it as a char buffer for debugging.

------
### KeyboardMouseController
*server/client/KeyboardMouseController.java*

Read the keyboard and mouse with GLFW callbacks, and produce acceleration and inertia vectors.
#### keyboard, mouse
Methods called by the anonymous methods used as GLFW callbacks, called by `glfwWaitEvents` in `ClientLWJGL.main`

------
### Server
*server/Server.java*

An abstract class that stores and updates the states of players and worlds.

#### run
Loop that checks if the server has quit, reads input, updates the server based on the amount of time elapsed since the last update, and writes output.

#### update
Updates all worlds by an interval of time.

#### quit, input, time, output
Abstract methods used in the loop.
