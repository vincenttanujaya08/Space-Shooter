package Screens;

import Entities.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.MyGdxGame;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameScreen implements Screen {
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
    int highscore;

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

    int score;
    Texture shipTexture;
    Rectangle playerRect, blankRect;
    private final Texture[] backgrounds = new Texture[4];
    private float[] backgroundOffset = {0, 0, 0, 0};
    private float backgroundMaxSpeed;
    private final int height = 1080;
    private final int width = 1920;
    private final SpriteBatch batch;

    // Player instance
    Player player;

    public GameScreen(MyGdxGame game) {
        this.game = game;
        y = 75;
        x = MyGdxGame.WIDTH / 2 - SHIP_WIDTH / 2;
        //Load Background
        backgrounds[0] = new Texture("img/black.jpeg");
        backgrounds[1] = new Texture("img/Starscape01.png");
        backgrounds[2] = new Texture("img/Starscape02.png");
        backgrounds[3] = new Texture("img/Starscape03.png");
        backgroundMaxSpeed = height / 4;
        bullets = new ArrayList<>();
        asteroids = new ArrayList<>();
        scoreFont = new BitmapFont(Gdx.files.internal("font/score.fnt"));
        playerRect = new Rectangle(Gdx.graphics.getWidth()/2 - SHIP_WIDTH,75, SHIP_WIDTH, SHIP_HEIGHT);
        blankRect = new Rectangle(0,0,Gdx.graphics.getWidth(),25);
        batch = new SpriteBatch();

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

        player = new Player(1.0f, 400.0f); // Initialize player with full health and normal speed
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Update the shoot timer
        shootTimer += delta;

        // Update player status
        player.update(delta);

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
            int rand = random.nextInt(0,4);
            if (rand == 1) {
                asteroids.add(new PoisonAsteroid(random.nextInt(Gdx.graphics.getWidth() - Asteroid.WIDTH), 70,player));
            } else if(rand == 3){
                asteroids.add(new SlowAsteroid(random.nextInt(Gdx.graphics.getWidth() - Asteroid.WIDTH), 100, player));
            }else{
                asteroids.add(new Asteroid(random.nextInt(Gdx.graphics.getWidth() - Asteroid.WIDTH), 120,player));
            }
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
        boolean movingLeft = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean movingRight = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        if (movingLeft) {
            x -= player.getSpeed() * Gdx.graphics.getDeltaTime();
            playerRect.x = x;
            if (x < 0) {
                x = 0;
                playerRect.x = x;
            }

            updateRollIndex(-1, delta);
        } else if (movingRight) {
            x += player.getSpeed() * Gdx.graphics.getDeltaTime();
            playerRect.x = x;
            if (x + SHIP_WIDTH > Gdx.graphics.getWidth()) {
                x = Gdx.graphics.getWidth() - SHIP_WIDTH;
                playerRect.x = x;
            }
            updateRollIndex(1, delta);
        } else {
            resetRollIndex(delta);
        }

        if (player.getHealth() <= 0) {
            game.HSdata.add(score);
            System.out.println("Game Over");
            game.setScreen(new MenuScreen(game));
        }

        // Updating collision rect
        Iterator<Asteroid> asteroidCollisionIter = asteroids.iterator();
        while (asteroidCollisionIter.hasNext()) {
            Asteroid a = asteroidCollisionIter.next();

            if (a.getRect().overlaps(playerRect) ) {
                asteroidCollisionIter.remove();
                explosions.add(new Explosion(a.getX(), a.getY()));
                if (a instanceof PoisonAsteroid) {
                    a.applyEffect(Player.POISON_DURATION);
                }
                if(a instanceof SlowAsteroid){
                    a.applyEffect(Player.SLOW_DURATION);
                    player.takeDamage(0.05f);
                }else {
                    player.takeDamage(0.1f); // Decrease health
                }
            }

            if (a.getRect().overlaps(blankRect)) {
                asteroidCollisionIter.remove();
                if (a instanceof PoisonAsteroid) {
                    a.applyEffect(Player.POISON_DURATION);
                }
                if(a instanceof SlowAsteroid){
                    a.applyEffect(Player.SLOW_DURATION);
                    player.takeDamage(0.05f);
                }else {
                    player.takeDamage(0.1f); // Decrease health
                }
            }
        }


        // Checking for collisions
        for (Bullet b : bullets) {
            for (Asteroid a : asteroids) {
                if (b.getRect().overlaps(a.getRect())) {
                    bulletsToRemove.add(b);
                    System.out.println("Before:" + a.getHealth());
                    a.takeDamage(10);
                    System.out.println("After: " + a.getHealth());
                    if (a.remove) {
                        asteroidToRemove.add(a);
                        explosions.add(new Explosion(a.getX(), a.getY()));
                        score += 100;
                        if (a instanceof PoisonAsteroid) {
                            player.heal(0.05f); // Heal a small amount if PoisonAsteroid is destroyed
                        }
                    }
                }
            }
        }
        bullets.removeAll(bulletsToRemove);
        asteroids.removeAll(asteroidToRemove);

        stateTime += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        renderBackground(delta);

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

        // Render health bar
        game.batch.setColor(player.getHealthBarColor());
        game.batch.draw(blank, 0, 0, Gdx.graphics.getWidth() * player.getHealth(), 25);
        game.batch.setColor(1, 1, 1, 1); // Reset color to white

        game.batch.draw(rolls[roll].getKeyFrame(stateTime, true), x, y, SHIP_WIDTH, SHIP_HEIGHT);
        game.batch.end();


    }

    private void renderBackground(float delta) {
        // Update background offsets based on their scroll speed
        for (int i = 0; i < backgrounds.length; i++) {
            backgroundOffset[i] += delta * backgroundMaxSpeed / (8 / (i + 1));

            // If the background has scrolled off the screen, reset its position
            if (backgroundOffset[i] > height) {
                backgroundOffset[i] = 0;
            }

            // Draw the background layer twice to create seamless loop
            game.batch.draw(backgrounds[i], 0, -backgroundOffset[i], width, height);
            game.batch.draw(backgrounds[i], 0, -backgroundOffset[i] + height, width, height);
        }
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

