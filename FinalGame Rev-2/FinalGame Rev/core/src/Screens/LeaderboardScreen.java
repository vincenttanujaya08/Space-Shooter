package Screens;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGdxGame;

import java.awt.*;
import java.util.Collections;

public class LeaderboardScreen implements Screen{
    private MyGdxGame game;
    private OrthographicCamera camera;
    private Texture backButtonImg, backButtonImg2;
    private SpriteBatch batch;
    private Texture[] leaderboardBackground = new Texture[4];
    private Rectangle backButton;
    private final int height = 1080;
    private final int width = 1920;

    private final int backButtonSize = 250;
    BitmapFont highscoreFont;

    public LeaderboardScreen(MyGdxGame game){
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, MyGdxGame.WIDTH, MyGdxGame.HEIGHT);

        leaderboardBackground[0] = new Texture(Gdx.files.internal("img/black.jpeg"));
        leaderboardBackground[1] = new Texture(Gdx.files.internal("img/Starscape01.png"));
        leaderboardBackground[2] = new Texture(Gdx.files.internal("img/Starscape02.png"));
        leaderboardBackground[3] = new Texture(Gdx.files.internal("img/Starscape03.png"));
        backButtonImg = new Texture(Gdx.files.internal("img/backButton.png"));
        backButtonImg2 = new Texture("img/backbutton2.png");
        highscoreFont = new BitmapFont(Gdx.files.internal("font/score.fnt"));
        batch = new SpriteBatch();
        backButton = new Rectangle(100, 100, backButtonSize, backButtonSize);
    }


    @Override
    public void show() {

    }

    int lokasi = Gdx.graphics.getHeight() - Gdx.graphics.getHeight()/3;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        GlyphLayout highscoreLayout = new GlyphLayout(highscoreFont, "HIGH SCORE");
        highscoreFont.draw(batch, highscoreLayout, Gdx.graphics.getWidth() / 2 - highscoreLayout.width / 2, Gdx.graphics.getHeight() - highscoreLayout.height - 10);
        highscoreFont.getData().setScale(1.5f);
        System.out.println("Highscore");
        game.HSdata.sort(Collections.reverseOrder());
        for (int i = 0; i < game.HSdata.size(); i++) {
            System.out.println(game.HSdata.get(i));
            GlyphLayout highscoreDataLayout = new GlyphLayout(highscoreFont, (i+1)+". "+ game.HSdata.get(i));
            highscoreFont.draw(batch, highscoreDataLayout, Gdx.graphics.getWidth() / 2 - highscoreLayout.width / 2, lokasi);
            lokasi -= 100;
            highscoreFont.getData().setScale(1.5f);
        }
        lokasi = Gdx.graphics.getHeight() - Gdx.graphics.getHeight()/3;

        batch.draw(backButtonImg, backButton.x, backButton.y, backButtonSize, backButtonSize);

        if (Gdx.input.getX() > backButton.x && Gdx.input.getX() < backButton.x + 265 && (float) height - Gdx.input.getY() < backButton.y + 300
                && height - Gdx.input.getY() > backButton.y) {
            batch.draw(backButtonImg2, backButton.x, backButton.y, backButton.width, backButton.height);
            if(Gdx.input.isTouched()){
                game.setScreen(new MenuScreen(game));
            }
        }

        batch.end();
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

    }
}