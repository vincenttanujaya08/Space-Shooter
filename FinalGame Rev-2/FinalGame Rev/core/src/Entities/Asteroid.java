package Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Asteroid implements Character{
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public static final int SPEED = 100;
    public static final int WIDTH = 100;
    public static final int HEIGHT = 100;
    private static Texture texture;
    float x, y;
    Rectangle rect;
    public boolean remove = false;
    private int health;
    Player player;
    Sprite sprite;

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public Asteroid(float x, int health,Player player ) {
        this.x = x;
        this.y = Gdx.graphics.getHeight();
        this.rect = new Rectangle(x, y, WIDTH, HEIGHT);
        if (texture == null) {
            texture = new Texture(Gdx.files.internal("asteroid.png"));
        }
        sprite = new Sprite(texture);
        sprite.setSize(this.rect.getWidth(), this.rect.getHeight());
        sprite.setOrigin(WIDTH / 2f, HEIGHT / 2f);
        this.player = player;
        this.health = health;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void update(float deltaTime) {
        y -= SPEED * deltaTime;
        this.rect.y = y;// Update collision rect position
        sprite.setPosition(this.rect.getX(), this.rect.getY());
        if (y < -HEIGHT) {
            remove = true;
        }
    }

    public void render(SpriteBatch batch) {
        sprite.setRotation(sprite.getRotation() + 0.5f);
        sprite.draw(batch);
    }

    public Rectangle getRect() {
        return this.rect;
    }

    @Override
    public void takeDamage(float damage) {
        health -= damage;
        if (health <= 0) {
            remove = true;
        }
    }

    public void applyEffect(float duration){
        //No Effect
    }
}