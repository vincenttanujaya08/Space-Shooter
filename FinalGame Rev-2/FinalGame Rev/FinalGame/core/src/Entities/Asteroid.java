package Entities;

import Tools.CollisionRect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Asteroid {
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public static final int SPEED = 100;
    public static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static Texture texture;
    float x, y;
    CollisionRect rect;
    public boolean remove = false;
    protected int health;

    public Asteroid(float x, int health) {
        this.x = x;
        this.y = Gdx.graphics.getHeight();
        this.rect = new CollisionRect(x, y, WIDTH, HEIGHT);
        if (texture == null) {
            textureImageLoader();
        }
        this.health = health;
    }

    public void textureImageLoader() {
        texture = new Texture("asteroid.png");
    }

    public void update(float deltaTime) {
        y -= SPEED * deltaTime;
        rect.move(x, y);  // Update collision rect position
        if (y < -HEIGHT) {
            remove = true;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, WIDTH, HEIGHT);
    }

    public CollisionRect getRect() {
        return rect;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            remove = true;
        }
    }
}
