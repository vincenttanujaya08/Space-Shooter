package Entities;

import Tools.CollisionRect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Bullet {
    private static final float SPEED = 500;
    private static final int WIDTH = 3;
    private static final int HEIGHT = 12;
    private static final float STARTING_Y = 15;

    private Texture texture;
    private float x, y;
    public boolean remove = false;
    CollisionRect rect;

    public Bullet(float x) {
        this.x = x;
        this.y = STARTING_Y;
        this.rect = new CollisionRect(x, y, WIDTH, HEIGHT);
        this.texture = new Texture("bullet.png");
    }

    public void update(float delta) {
        y += SPEED * delta;
        rect.move(x, y);  // Update collision rect position
        if (y > Gdx.graphics.getHeight()) {
            remove = true;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, WIDTH, HEIGHT);
    }

    public CollisionRect getRect() {
        return rect;
    }

    public void dispose() {
        texture.dispose();
    }
}
