package Screens;

import Entities.*;
import Tools.CollisionRect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.mygdx.game.MyGdxGame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameScreen implements Screen {
    public static final float SPEED = 300;
    public static final float SHIP_ANIMATION_SPEED = 0.5f;
    public static final int SHIP_WIDTH_PIXEL = 17;
    public static final int SHIP_HEIGHT_PIXEL = 32;
    public static final int SHIP_WIDTH = SHIP_WIDTH_PIXEL * 3;
    public static final int SHIP_HEIGHT = SHIP_HEIGHT_PIXEL * 3;
    public static final float ROLL_TIMER_SWITCH_TIME = 0.15f;
    public static final int ROLL_CENTER = 2;
    public static final int ROLL_MAX_LEFT = 0;
    public static final int ROLL_MAX_RIGHT = 4;
    public static final float SHOOT_COOLDOWN = 0.3f; // Cooldown time in seconds
    public static final float MIN_ASTEROID_SPAWN_TIME = 1.0f;
    public static final float MAX_ASTEROID_SPAWN_TIME = 3.0f;

    Animation<TextureRegion>[] rolls;

    float x;
    float y;
    float stateTime;
    int roll;
    float rollTimer;
    float shootTimer;
    float asteroidSpawnTimer;
    Random random;
    MyGdxGame game;
    ArrayList<Bullet> bullets;
    ArrayList<Asteroid> asteroids;
    ArrayList<Explosion> explosions;
    Texture blank;
    BitmapFont scoreFont;

    float health = 1;
    //0  is death, 1 is full health
    int score;
    Texture shipTexture;
    CollisionRect playerRect;

    public GameScreen(MyGdxGame game) {
        this.game = game;
        y = 10;
        x = MyGdxGame.WIDTH / 2 - SHIP_WIDTH / 2;
        bullets = new ArrayList<>();
        asteroids = new ArrayList<>();
        scoreFont = new BitmapFont(Gdx.files.internal("font/score.fnt"));
        playerRect = new CollisionRect(0,0,SHIP_WIDTH,SHIP_HEIGHT);

        explosions = new ArrayList<>();
        blank = new Texture("blank.png");
        score = 0;

        roll = ROLL_CENTER;
        random = new Random();
        asteroidSpawnTimer = random.nextFloat() * (MAX_ASTEROID_SPAWN_TIME - MIN_ASTEROID_SPAWN_TIME) + MIN_ASTEROID_SPAWN_TIME;
        rollTimer = 0;
        shootTimer = 0; // Initialize shoot timer
        rolls = new Animation[5];

        shipTexture = new Texture("ship.png");
        TextureRegion[][] rollSpriteSheet = TextureRegion.split(shipTexture, SHIP_WIDTH_PIXEL, SHIP_HEIGHT_PIXEL);

        rolls[ROLL_MAX_LEFT] = new Animation<>(SHIP_ANIMATION_SPEED, rollSpriteSheet[2]);
        rolls[1] = new Animation<>(SHIP_ANIMATION_SPEED, rollSpriteSheet[1]);
        rolls[ROLL_CENTER] = new Animation<>(SHIP_ANIMATION_SPEED, rollSpriteSheet[0]);
        rolls[3] = new Animation<>(SHIP_ANIMATION_SPEED, rollSpriteSheet[3]);
        rolls[ROLL_MAX_RIGHT] = new Animation<>(SHIP_ANIMATION_SPEED, rollSpriteSheet[4]);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Update the shoot timer
        shootTimer += delta;

        // Shooting
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && shootTimer >= SHOOT_COOLDOWN) {
            float offset = calculateBulletOffset(roll);
            bullets.add(new Bullet(x + 4));
            bullets.add(new Bullet(x + SHIP_WIDTH - offset));
            shootTimer = 0; // Reset the shoot timer
        }

        // Asteroids spawn
        asteroidSpawnTimer -= delta;
        if (asteroidSpawnTimer <= 0) {
            asteroidSpawnTimer = random.nextFloat() * (MAX_ASTEROID_SPAWN_TIME - MIN_ASTEROID_SPAWN_TIME) + MIN_ASTEROID_SPAWN_TIME;
            asteroids.add(new Asteroid(random.nextInt(Gdx.graphics.getWidth() - Asteroid.WIDTH), 30));
        }

        // Update asteroids
        ArrayList<Asteroid> asteroidToRemove = new ArrayList<>();
        for (Asteroid a : asteroids) {
            a.update(delta);
            if (a.remove) {
                asteroidToRemove.add(a);
            }
        }
        asteroids.removeAll(asteroidToRemove);

        // Update bullets
        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet b : bullets) {
            b.update(delta);
            if (b.remove) {
                bulletsToRemove.add(b);
            }
        }
        bullets.removeAll(bulletsToRemove);

        // Update explosions
        ArrayList<Explosion> explosionsToRemove = new ArrayList<>();
        for (Explosion e : explosions) {
            e.update(delta);
            if (e.remove) {
                explosionsToRemove.add(e);
            }
        }
        explosions.removeAll(explosionsToRemove);

        // Movement
        boolean movingLeft = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean movingRight = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        if (movingLeft) {
            x -= SPEED * Gdx.graphics.getDeltaTime();
            if (x < 0) {
                x = 0;
            }

            updateRollIndex(-1, delta);
        } else if (movingRight) {
            x += SPEED * Gdx.graphics.getDeltaTime();
            if (x + SHIP_WIDTH > Gdx.graphics.getWidth()) {
                x = Gdx.graphics.getWidth() - SHIP_WIDTH;
            }
            updateRollIndex(1, delta);
        } else {
            resetRollIndex(delta);
        }

        //Updating collision rect
        playerRect.move(x,y); Iterator<Asteroid> asteroidCollisionIter = asteroids.iterator();
        while (asteroidCollisionIter.hasNext()) {
            Asteroid a = asteroidCollisionIter.next();
            if (a.getRect().collidesWith(playerRect)) {
                asteroidCollisionIter.remove();
                health -= 0.1f; // Decrease health
                if (health <= 0) {
                    health = 0;
                    System.out.println("Game Over");
                    System.exit(1);
                    // Game over logic or reset here
                }
            }
        }

        // Checking for collisions
        for (Bullet b : bullets) {
            for (Asteroid a : asteroids) {
                if (b.getRect().collidesWith(a.getRect())) {
                    bulletsToRemove.add(b);
                    a.takeDamage(1);  // Decrease asteroid health by 1
                    if (a.remove) {
                        asteroidToRemove.add(a);
                        explosions.add(new Explosion(a.getX(), a.getY()));
                        score += 100;
                    }
                }
            }
        }
        boolean isHit = false;
        for (Asteroid a : asteroids){
            if(a.getRect().collidesWith(playerRect)){
                asteroidToRemove.add(a);
                health -= 0.1;

                if(health < 0){
                    health = 0;
                }
            }
        }

        stateTime += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();

        GlyphLayout scoreLayout = new GlyphLayout(scoreFont, "" + score);
        scoreFont.draw(game.batch, scoreLayout, Gdx.graphics.getWidth() / 2 - scoreLayout.width / 2, Gdx.graphics.getHeight() - scoreLayout.height - 10);

        for (Bullet b : bullets) {
            b.render(game.batch);
        }
        for (Asteroid a : asteroids) {
            a.render(game.batch);
        }
        for (Explosion e : explosions) {
            e.render(game.batch);
        }
        game.batch.draw(blank,0,0,Gdx.graphics.getWidth() * health,5);
        game.batch.draw(rolls[roll].getKeyFrame(stateTime, true), x, y, SHIP_WIDTH, SHIP_HEIGHT);
        game.batch.end();
    }

    private float calculateBulletOffset(int roll) {
        // Linear interpolation between roll states for smoother offset transition
        float maxOffset = 16f;
        float minOffset = 4f;
        float offsetRange = maxOffset - minOffset;
        float rollFactor = (float) roll / ROLL_MAX_RIGHT;
        return minOffset + rollFactor * offsetRange;
    }

    private void updateRollIndex(int direction, float delta) {
        rollTimer += direction * delta;
        if (Math.abs(rollTimer) > ROLL_TIMER_SWITCH_TIME) {
            rollTimer = 0;
            roll += direction;
            if (roll < ROLL_MAX_LEFT) {
                roll = ROLL_MAX_LEFT;
            } else if (roll > ROLL_MAX_RIGHT) {
                roll = ROLL_MAX_RIGHT;
            }
        }
    }

    private void resetRollIndex(float delta) {
        if (roll < ROLL_CENTER) {
            rollTimer += delta;
            if (Math.abs(rollTimer) > ROLL_TIMER_SWITCH_TIME) {
                rollTimer = 0;
                roll++;
            }
        } else if (roll > ROLL_CENTER) {
            rollTimer -= delta;
            if (Math.abs(rollTimer) > ROLL_TIMER_SWITCH_TIME) {
                rollTimer = 0;
                roll--;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        shipTexture.dispose();
    }
}
