package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.MyGdxGame;

public class MenuScreen implements Screen {
    private final Camera camera;
    private final Viewport vp;
    private final SpriteBatch batch;
    private final Texture[] backgrounds = new Texture[4];
    private Texture playImg, exitImg, leaderImg, earthImg, asteHP, shipImg, judul,play2img,exit2img, leader2Img;
    private float[] backgroundOffset = {0, 0, 0, 0};
    private float backgroundMaxSpeed;
    private final int width = 1920;
    private final int height = 1080;
    private int buttonHeight = 150;
    private int buttonWidth = 400;
    private int leaderWidth = 200;
    private Rectangle play, exit, leader;
    private Sprite sprite, sprite1;
    private Animation<TextureRegion> playerIdle;

    private float stateTime;
    private MyGdxGame game;
    BitmapFont GameNameFont;

    String gameName = "Protect The Earth";



    public MenuScreen(MyGdxGame myGdxGame) {
        this.game = myGdxGame;
        camera = new OrthographicCamera();
        vp = new FitViewport(width, height, camera);
        batch = new SpriteBatch();
        GameNameFont = new BitmapFont(Gdx.files.internal("font/score.fnt"));

        // Load textures
        backgrounds[0] = new Texture("img/black.jpeg");
        backgrounds[1] = new Texture("img/Starscape01.png");
        backgrounds[2] = new Texture("img/Starscape02.png");
        backgrounds[3] = new Texture("img/Starscape03.png");
        playImg = new Texture("img/Play Button.png");
        play2img = new Texture("img/PlayButton2.png");
        exitImg = new Texture("img/Exit Button.png");
        exit2img = new Texture("img/ExitButton2.png");
        leaderImg = new Texture("img/buttonLeaderboard.png");
        earthImg = new Texture("img/earth.png");
        asteHP = new Texture("img/asteroidBigHealth.png");
        shipImg = new Texture("img/ship.png");
//        judul = new Texture("img/Judul.jpg");
        leader2Img = new Texture("img/buttonLeaderboard2.png");

        // Initialize speeds and positions
        backgroundMaxSpeed = height / 4;
        sprite = new Sprite(earthImg);
        sprite.setPosition(-200, -500);
        sprite.setSize(1000, 1000);
        sprite.setOrigin(500, 500);
        sprite1 = new Sprite(asteHP);
        sprite1.setPosition(width - width / 4f, height - height / 4f - 100);
        sprite1.setSize(200, 200);
        sprite1.setOrigin(100, 100);

        // Initialize button rectangles
        int buttonWidth = 400;
        int buttonHeight = 150;

        play = new Rectangle( width /2 - buttonWidth / 2,  height / 2 - 75, buttonWidth, buttonHeight);
        exit = new Rectangle(width / 2 - buttonWidth / 2,  height / 2 - 300, buttonWidth, buttonHeight);
        leader = new Rectangle(width - leaderWidth / 2 - 400, height / 3 - 175, leaderWidth, leaderWidth);

        // Initialize animation
        TextureRegion[][] rollSpriteSheet = TextureRegion.split(shipImg, 17, 32);
        TextureRegion[] idle = new TextureRegion[2];
        playerIdle = new Animation<>(0.4f, idle);
        int idx = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 2; j++) {
                idle[idx++] = rollSpriteSheet[i][j];
            }
        }
        stateTime = 0;
    }

    @Override
    public void render(float delta) {
        camera.update();
        sprite.setRotation(sprite.getRotation() - 0.125F);
        sprite1.setRotation(sprite1.getRotation() + 0.125F);
        stateTime += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        mainMenu(delta);
        batch.end();
    }

    private void mainMenu(float delta) {
        renderBackground(delta);
        GlyphLayout nameGameLayout = new GlyphLayout(GameNameFont, gameName);
        GameNameFont.draw(batch, nameGameLayout, Gdx.graphics.getWidth() / 2 - nameGameLayout.width / 2, Gdx.graphics.getHeight() - nameGameLayout.height - 10);
        GameNameFont.getData().setScale(1.5f);

//        batch.draw(judul, width / 2f - 300, height - height / 3f, 600, 300);
        batch.draw(playImg, play.x, play.y, play.width, play.height);
        batch.draw(exitImg, exit.x, exit.y, exit.width, exit.height);
        batch.draw(leaderImg, leader.x, leader.y, leader.width, leader.height);
        sprite.draw(batch);
        sprite1.draw(batch);
        TextureRegion current = playerIdle.getKeyFrame(stateTime, true);
        batch.draw(current, width / 8f, height - height / 3f, 150, 300);

        if(Gdx.input.getX() <= exit.x + exit.width && Gdx.input.getX() >= exit.x && height - Gdx.input.getY() >= exit.y
                && height - Gdx.input.getY() <= exit.y + 165) {
            batch.draw(exit2img, exit.x, exit.y, exit.width, exit.height);
            if (Gdx.input.isTouched()) {
                Gdx.app.exit();
            }
        }

        if(Gdx.input.getX() < play.x + play.width && Gdx.input.getX() > play.x && height - Gdx.input.getY() > play.y
                && height - Gdx.input.getY() < play.y + 165) {
            batch.draw(play2img, play.x, play.y, play.width, play.height);
            if (Gdx.input.isTouched()) {
                game.setScreen(new GameScreen(game));
            }
        }

        if(Gdx.input.getX() < leader.x + leaderWidth && Gdx.input.getX() > 1400 && height - Gdx.input.getY() > leader.y
                && height - Gdx.input.getY() < leader.y + leaderWidth) {
            batch.draw(leader2Img, leader.x, leader.y, leader.width, leader.height);
            if (Gdx.input.isTouched()) {
                game.setScreen(new LeaderboardScreen(game));
            }
        }
    }

    private void renderBackground(float delta) {
        for (int i = 0; i < backgroundOffset.length; i++) {
            backgroundOffset[i] += delta * backgroundMaxSpeed / (8 / (i + 1));
            if (backgroundOffset[i] > height) {
                backgroundOffset[i] = 0;
            }
            batch.draw(backgrounds[i], 0, -backgroundOffset[i], width, height);
            batch.draw(backgrounds[i], 0, -backgroundOffset[i] + height, width, height);
        }
    }

    @Override
    public void resize(int w, int h) {
        vp.update(w, h, true);
        batch.setProjectionMatrix(camera.combined);
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
        batch.dispose();
        for (Texture background : backgrounds) {
            background.dispose();
        }
    }
    @Override
    public void show() {
    }
}
