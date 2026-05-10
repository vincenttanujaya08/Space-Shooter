package Entities;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Entity {
    void render(SpriteBatch batch);
    void update(float delta);
}