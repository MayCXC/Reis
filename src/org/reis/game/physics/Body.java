package org.reis.game.physics;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.reis.events.Updatable;
import org.reis.game.Block;
import org.reis.game.World;

public class Body implements Updatable {
    private World world;
    public World getWorld() {
        return world;
    }

    private double mass;
    private Vector3d position, velocity, acceleration;
    private Quaterniond orientation;
    public Body( World world ) {
        this.world = world;
        mass = 1;

        position = new Vector3d( );
        velocity = new Vector3d( );
        acceleration = new Vector3d( );

        orientation = new Quaterniond();
    }

    public void act( Vector3d force ) {
        if(mass != 0)
        acceleration.add( new Vector3d( force ).div( mass ) );
    }
    public void act( Quaterniond force ) {
        orientation.mul( force );
    }

    private void gravity( ) {
        act( new Vector3d( 0,mass*-64.0,0 ) );
    }

    private void drag( ) {
        act( new Vector3d( velocity )
            .normalize( .5*velocity.lengthSquared()*2.0 )
            .negate( )
        );
    }

    public Vector3d getPosition( ) {
        return position;
    }
    public Vector3d getVelocity( ) {
        return velocity;
    }
    public Vector3d getAcceleration( ) {
        return acceleration;
    }
    public Quaterniond getOrientation( ) {
        return orientation;
    }

    public void update( double dt ) {
        if( acceleration.y() <= 0 ) gravity( );

        if ( velocity.lengthSquared() > 1.0/1024.0 ) drag( );
        else velocity.zero();

        velocity.fma( dt, acceleration );
        acceleration.zero();
        position.fma( dt, velocity );

        if( position.y( ) - 2 < 0 ) {
            position.y = 2;
            if( velocity.y() < 0 ) velocity.y = 0;
            if( acceleration.y() < 0 ) acceleration.y = 0;
        }

        Block floor = null;
        try{ floor = world.getBlocks()[(int)position.z()][(int)position.y()-2][(int)position.x()]; }
        catch( ArrayIndexOutOfBoundsException e ){ e=null; }
        if(floor != null) {
            position.y = Math.ceil(position.y);
            if( velocity.y() < 0 ) velocity.y = 0;
            if( acceleration.y() < 0 ) acceleration.y = 0;
        }

        orientation.normalize();
    }
}
