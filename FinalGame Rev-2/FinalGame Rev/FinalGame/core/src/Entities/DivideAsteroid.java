//package Entities;
//
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import java.util.ArrayList;
//
//public class DivideAsteroid extends Asteroid {
//    private ArrayList<Asteroid> asteroids;
//
//    public DivideAsteroid(float x, ArrayList<Asteroid> asteroids) {
//        super(x, 2); // Health multiplier of 2
//        this.asteroids = asteroids;
//    }
//
//    @Override
//    protected void onDeath() {
//        super.onDeath();
//        asteroids.add(new SmallAsteroid(x));
//        asteroids.add(new SmallAsteroid(x + WIDTH / 2));
//    }
//}
