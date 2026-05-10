package Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Bullet implements Entity{
    private static final float SPEED = 500;
    private static final int WIDTH = 3;
    private static final int HEIGHT = 12;
    private static final float STARTING_Y = 90;

    private Texture texture;
    private float x, y;
    public boolean remove = false;
    Rectangle rect;

    public Bullet(float x) {
        this.x = x;
        this.y = STARTING_Y;
        this.rect = new Rectangle(x, y, WIDTH, HEIGHT);
        this.texture = new Texture("bullet.png");
    }

    @Override
    public void update(float delta) {
        y += SPEED * delta;
        this.rect.y = y;  // Update collision rect position
        if (y > Gdx.graphics.getHeight()) {
            remove = true;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, WIDTH, HEIGHT);
    }

    public Rectangle getRect() {
        return this.rect;
    }

    public void dispose() {
        texture.dispose();
    }
}