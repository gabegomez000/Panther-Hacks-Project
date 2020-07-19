package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class MyGdxGame extends ApplicationAdapter {
    
        private Texture playerImage; // The player's sprite
        private Texture virusImage0; // The virus sprite 0
        private Texture virusImage1; // The virus sprite 1
        private Texture virusImage2; // The virus sprite 2
        private Texture backgroundImage; // The game background
        private Texture gameoverImage; // The game over screen
        
        private Sound coughSound; // The sound for collecting a virus
        private Music gameMusic; // The game background music
        
        public BitmapFont gameFont; // Used to display text (Score, Lives, etc.)
        
        private SpriteBatch batch; // Used for rendering
        private OrthographicCamera camera; // Used for camera space
        private Rectangle player; // Used for the player's position
        private Array<Rectangle> viruses; // Used for the virus's position
        
        private long lastDropTime; // Used to track time for virus spawning
        private int playerScore = 0; // Used to track viruses collected
        private int playerLives = 3; // Used to track viruses missed
	
	
	public void create () {
            
                // Setting up the textures, background, and sounds
                playerImage = new Texture
                (Gdx.files.internal("doctor.png"));
                virusImage0 = new Texture
                (Gdx.files.internal("virus0.png"));
                virusImage1 = new Texture
                (Gdx.files.internal("virus1.png"));
                virusImage2 = new Texture
                (Gdx.files.internal("virus2.png"));
                backgroundImage = new Texture(Gdx.files.internal
                ("background.png"));
                gameoverImage = new Texture(Gdx.files.internal
                ("gameoverscreen.png"));
                coughSound = Gdx.audio.newSound(Gdx.files.internal
                ("coughsound.mp3"));
                gameMusic = Gdx.audio.newMusic(Gdx.files.internal
                ("gamemusic.mp3"));
                gameFont = new BitmapFont();
                
                // Start playing and looping the game music
                gameMusic.setLooping(true);
                gameMusic.play();
                
                // Set up the game window
                camera = new OrthographicCamera();
                camera.setToOrtho(false, 1920, 1080);
                batch = new SpriteBatch();
                
                // Set up the player's position
                player = new Rectangle();
                player.x = 1920 / 2 - 225/2; // centers the player horizontally
                player.y = 20; // Keeps player slightly above the screen's edge
                player.width = 81; // Player's "hitbox" width
                player.height = 198; // Player's "hitbox" height
                
                // Creating viruses
                viruses = new Array<Rectangle>();
                spawnVirus();
	}
        
        // Used to spawn the viruses
        private void spawnVirus() {
            Rectangle virus = new Rectangle(); // New virus
            virus.x = MathUtils.random(0, 1920-128); // Randomizes virus location
            virus.y = 1080; // Makes sure virus spawns at top of screen
            virus.width = 128; // virus "hitbox" width
            virus.height = 128; // virus "hitbox" height
            viruses.add(virus); // Adds this new virus to array of viruses
            lastDropTime = TimeUtils.nanoTime(); // Tracks virus drop time
   }

	public void render () {
                //Clears the screen before rendering everything
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
                camera.update(); // Tells camera to update
                
            if (playerLives != 0){ // As long as the player has lives remaining
                batch.setProjectionMatrix(camera.combined);
                batch.begin();
                batch.draw(backgroundImage, 0 , 0,
                        Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                        //^This ensures the background is as big as the window
                batch.draw(playerImage, player.x, player.y);
                        // This draws the player's location
                for (Rectangle virus: viruses) {
                    int virustype = MathUtils.random (0, 2);
                    
                    if (virustype == 0) {
                    batch.draw(virusImage0, virus.x, virus.y);
                    } else if (virustype == 1) {
                        batch.draw(virusImage1, virus.x, virus.y);
                    } else {
                        batch.draw (virusImage2, virus.x, virus.y);
                    }
                }       //Draws the viruses on the screen
                gameFont.getData().setScale(4, 4); // Increase size of text
                gameFont.draw(batch, "Viruses Avoided: " + 
                                     playerScore, 0, 1080); // Displays score
                gameFont.draw(batch, "Lives Remaining: " + 
                                     playerLives, 0, 1000); // Displays lives
                batch.end();
                
                // These track the user's input (Mouse or Arrow Keys)
                if(Gdx.input.isTouched()) {
                    Vector3 touchPos = new Vector3();
                    touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                    camera.unproject(touchPos);
                    player.x = touchPos.x - 81 / 2;
                   }
                
                if(Gdx.input.isKeyPressed(Keys.LEFT)){
                    player.x -= 1000 * Gdx.graphics.getDeltaTime();
                }
                if(Gdx.input.isKeyPressed(Keys.RIGHT)){
                    player.x += 1000 * Gdx.graphics.getDeltaTime();
                }
                
                // These make sure the player sprite doesn't leave the screen
                if(player.x < 0) player.x = 0;
                if(player.x > 1920 -225) player.x = 1920 - 225;
                
                // This spawns a new virus if enough time has passed
                // Since the last virus was spawned
                if(TimeUtils.nanoTime() - lastDropTime > 350000000){
                    spawnVirus();
                }
                
                // Tracks iterations of viruses
                for (Iterator<Rectangle> iter = viruses.iterator();
                     iter.hasNext(); ) {
                    Rectangle virus = iter.next();
                    virus.y -= 200 * Gdx.graphics.getDeltaTime();
                    
                    if(virus.y + 225 < 0) { // If virus drops to bottom of screen,
                        iter.remove(); // remove the virus from the game
                        playerScore++;
                    }
                    if(virus.overlaps(player)) { // If virus touches player model,
                        coughSound.play(); // play the virus sound,
                        iter.remove(); // remove the virus from the game, and
                        playerLives--; // increase the score by 1.
                        
                    }
                }
            }
            if (playerLives == 0) {
                Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                batch.begin();
                gameFont.getData().setScale(4, 4); // Increase size of text
                gameFont.draw(batch, "Game Over!", 0, 1070); // Game Over text
                gameFont.draw(batch, "Final Score: " + playerScore,
                              0, 1070-80); // Display Final Score
                gameFont.draw(batch, "Credits:", 0, 400); // Display Credits
                gameFont.draw(batch, "Gabriel Gomez", 0, 320);
                gameFont.draw(batch, "Eric Campillo", 0, 240);
                gameFont.draw(batch, "Mario Platias", 0, 160);
                gameFont.draw(batch, "Oscar Alvarez", 0, 80);
                gameFont.draw(batch, "Tips to prevent Covid-19", 1100, 1070);
                gameFont.draw(batch, "1. Wear a mask", 1100, 1020);
                gameFont.draw(batch, "2. Wash your hands frequently", 1100, 970);
                gameFont.draw(batch, "3. Maintain 6 ft distance", 1100, 920);
                gameFont.draw(batch, "4. Don't gather in large groups", 1100, 870);

                batch.draw(gameoverImage, 1920-399 , 0); // Display Game Over
                batch.end();
            }
        }
	
	public void dispose () {
            virusImage0.dispose();
            virusImage1.dispose();
            virusImage2.dispose();
            playerImage.dispose();
            gameoverImage.dispose();
            backgroundImage.dispose();
            coughSound.dispose();
            gameMusic.dispose();
            batch.dispose();
        }
}