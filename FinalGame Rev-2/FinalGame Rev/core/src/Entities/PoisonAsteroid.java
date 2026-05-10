package Entities;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PoisonAsteroid extends Asteroid {

    private static Texture poisonTexture = new Texture("asteroid4.png");
    Sprite sprite;

    public PoisonAsteroid(float x, int health, Player player) {
        super(x, health, player);
        sprite = new Sprite(poisonTexture);
        sprite.setSize(this.rect.getWidth(), this.rect.getHeight());
        sprite.setOrigin(WIDTH / 2f, HEIGHT / 2f);
    }


    @Override
    public void render(SpriteBatch batch) {
        sprite.setRotation(sprite.getRotation() - 0.250f);
        sprite.draw(batch);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        sprite.setPosition(this.rect.getX(), this.rect.getY());
    }

    @Override
    public void applyEffect(float duration){
        player.applyPoison(duration);
    }


}