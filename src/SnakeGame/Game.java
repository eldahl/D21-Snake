package SnakeGame;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Game {

    private GridObject[][] grid;
    private Snake snake;
    private HashMap<String, Image> gfx;
    private AudioPlayer ap;
    private Canvas canvas;
    private GraphicsContext gc;
    private Stage stage;

    public boolean run = false;
    private boolean hardMode = false;
    private boolean hardModeBigHead = false;
    private boolean insaneMode = false;
    private boolean insaneModeRotated = false;

    public int score = 0;
    public int snakeLength = 0;

    public Timer speedTimer = new Timer();
    public Timer insaneTimer = new Timer();
    public Timer bigHeadTimer = new Timer();

    Vector2 lastTailPos;

    public void init(Stage stage, Canvas canvas, boolean hardMode, boolean insaneMode) {

        this.hardMode = hardMode;
        this.insaneMode = insaneMode;

        // Audio files to load
        String[] audioFiles = {
                "res/audio/eat.wav",
                "res/audio/die.mp3"
        };

        gfx = new HashMap<>();
        grid = new GridObject[20][20];
        snake = new Snake(grid, gfx);
        ap = new AudioPlayer(audioFiles, stage);
        this.gc = canvas.getGraphicsContext2D();
        this.canvas = canvas;
        this.stage = stage;

        // === Snake Head ===
        gfx.put("head-up", new Image("file:res/gfx/head-up.png", 44, 44, false, true));
        gfx.put("head-right", new Image("file:res/gfx/head-right.png", 44, 44, false, true));
        gfx.put("head-down", new Image("file:res/gfx/head-down.png", 44, 44, false, true));
        gfx.put("head-left", new Image("file:res/gfx/head-left.png", 44, 44, false, true));
        // === Snake body ===
        gfx.put("body-v", new Image("file:res/gfx/body-vertical.png", 44, 44, false, true));
        gfx.put("body-h", new Image("file:res/gfx/body-horizontal.png", 44, 44, false, true));
        gfx.put("body-ne", new Image("file:res/gfx/body-ne.png", 44, 44, false, true));
        gfx.put("body-se", new Image("file:res/gfx/body-se.png", 44, 44, false, true));
        gfx.put("body-sw", new Image("file:res/gfx/body-sw.png", 44, 44, false, true));
        gfx.put("body-nw", new Image("file:res/gfx/body-nw.png", 44, 44, false, true));
        // === Snake Tail ===
        gfx.put("tail-up", new Image("file:res/gfx/tail-up.png", 44, 44, false, true));
        gfx.put("tail-right", new Image("file:res/gfx/tail-right.png", 44, 44, false, true));
        gfx.put("tail-down", new Image("file:res/gfx/tail-down.png", 44, 44, false, true));
        gfx.put("tail-left", new Image("file:res/gfx/tail-left.png", 44, 44, false, true));
        // === Playing field grid ===
        gfx.put("grid", new Image("file:res/gfx/grid.png"));
        // === Objects ===
        gfx.put("apple", new Image("file:res/gfx/apple.png", 44, 44, false, true));
        gfx.put("speedapple", new Image("file:res/gfx/speedapple.png", 44, 44, false, true));
        gfx.put("sizeapple", new Image("file:res/gfx/sizeapple.png", 44, 44, false, true));
        // === Hard mode ===
        gfx.put("head-up-big", new Image("file:res/gfx/head-up.png", 88, 88, false, true));
        gfx.put("head-right-big", new Image("file:res/gfx/head-right.png", 88, 88, false, true));
        gfx.put("head-down-big", new Image("file:res/gfx/head-down.png", 88, 88, false, true));
        gfx.put("head-left-big", new Image("file:res/gfx/head-left.png", 88, 88, false, true));

        // Add Snake objects to grid
        for(GridObject go : snake.getSnakeGOs()) {
            grid[go.pos.x][go.pos.y] = go;
        }

        // First food
        spawnFood();

        // Because of ghost snake object that is left behind
        grid[3][12] = null;

        // Setup input
        stage.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            KeyCode k = key.getCode();
            if (k == KeyCode.UP || k == KeyCode.W) {
                if(snake.direction != Direction.down)
                    snake.direction = Direction.up;
            }
            if (k == KeyCode.DOWN || k == KeyCode.S) {
                if(snake.direction != Direction.up)
                    snake.direction = Direction.down;
            }
            if (k == KeyCode.LEFT || k == KeyCode.A) {
                if(snake.direction != Direction.right)
                    snake.direction = Direction.left;
            }
            if (k == KeyCode.RIGHT || k == KeyCode.D) {
                if(snake.direction != Direction.left)
                    snake.direction = Direction.right;
            }
            if (k == KeyCode.ESCAPE) {
                SnakeGame.gotoMainMenu();
            }
            // Debug
            if(k == KeyCode.F) {
                run = !run;
            }
        });
    }

    public void update() {
        // Insane mode
        if(insaneMode){
            if (!insaneModeRotated && score % 10 == 0 && score != 0) {
                // Random rotate screen
                Random rnd = new Random();
                if (rnd.nextInt(2) == 1) {
                    canvas.setRotate(90);
                } else {
                    canvas.setRotate(-90);
                }
                insaneModeRotated = true;

                // Switch back to normal after 25 sec
                insaneTimer.cancel();
                insaneTimer = new Timer();
                insaneTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        insaneModeRotated = false;
                        canvas.setRotate(0);
                    }
                }, 12000);
            }
        }

        // Pre move logic
        Vector2 headPos = snake.getSnakeGOs().get(0).pos;
        Vector2 newHeadPos = new Vector2(headPos.x, headPos.y);
        switch (snake.direction) {
            case up -> {
                newHeadPos.y -= 1;
            }
            case down -> {
                newHeadPos.y += 1;
            }
            case left -> {
                newHeadPos.x -= 1;
            }
            case right -> {
                newHeadPos.x += 1;
            }
        }

        // Check for potential out of bounds
        if(newHeadPos.x < 0 || newHeadPos.y < 0 || newHeadPos.x >= 20 || newHeadPos.y >= 20)  {
            gameOver();
            return;
        }

        GridObject go = grid[newHeadPos.x][newHeadPos.y];
        if(go != null) {
            // If object ahead is solid, snake dies
            if(go.solid) {
                gameOver();
                return;
            }
            // If not it is other objects like food
            else if(go.gfxName.equals("apple")) {
                // Increment score by 1
                score++;
                SnakeGame.lScore.setText("Score: " + score + " | ");
                // Play snake eat sound
                stage.fireEvent(new GameEvent(GameEvent.snake_eat));
                // Grow snake
                snake.growQueue++;
                // Destroy GridObject
                go = null;
                // Spawn a new one
                spawnFood();
            }
            else if(go.gfxName.equals("speedapple")) {
                // Increment score by 1
                score++;
                SnakeGame.lScore.setText("Score: " + score + " | ");
                // Play snake eat sound
                stage.fireEvent(new GameEvent(GameEvent.snake_eat));
                // Grow snake
                snake.growQueue++;
                // Destroy GridObject
                go = null;
                // Spawn a new one
                spawnFood();

                // Speed buff
                SnakeGame.gameTick.setRate(2);
                speedTimer.cancel();
                speedTimer = new Timer();
                speedTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        SnakeGame.gameTick.setRate(1);
                    }
                }, 8000);
            }
            else if(go.gfxName.equals("sizeapple")) {
                // Increment score by 1
                score++;
                SnakeGame.lScore.setText("Score: " + score + " | ");
                // Play snake eat sound
                stage.fireEvent(new GameEvent(GameEvent.snake_eat));
                // Grow snake
                snake.growQueue++;
                // Destroy GridObject
                go = null;
                // Spawn a new one
                spawnFood();

                hardModeBigHead = true;
                bigHeadTimer.cancel();
                bigHeadTimer = new Timer();
                bigHeadTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        hardModeBigHead = false;
                        snake.getSnakeGOs().get(0).offset = new Vector2(0,0);
                    }
                }, 8000);
            }
        }

        // Post move logic
        // Move & grow snake
        snake.MoveAndGrow(newHeadPos);

        // Hacky fix, so we don't get ghost objects in the trail of the snake
        if(lastTailPos != null)
            grid[lastTailPos.x][lastTailPos.y] = null;
        lastTailPos = snake.getSnakeGOs().get(snake.getSnakeGOs().size()-1).pos;

        // Update snake GO's in grid
        for(GridObject g : snake.getSnakeGOs()) {
            grid[g.pos.x][g.pos.y] = g;
        }

        // Update snake length
        snakeLength = snake.getSnakeGOs().size();
        SnakeGame.lSnakeLength.setText("Snake Length: " + snakeLength);

        // Update snake looks so it looks right
        snake.UpdateGraphics(hardModeBigHead);

        // Draw grid
        gc.drawImage(gfx.get("grid"), 0, 0);
        // Draw GridObjects
        for(int i = 0; i < grid.length; i++) {
            for(int j = 0; j < grid[i].length; j++) {
                GridObject obj = grid[i][j];
                if(obj != null && obj.shouldDraw) {
                    gc.drawImage(gfx.get(obj.gfxName), obj.pos.x * SnakeGame.gridSize + obj.offset.x, obj.pos.y * SnakeGame.gridSize + obj.offset.y);
                }
            }
        }
    }

    public void gameOver() {
        stage.fireEvent(new GameEvent(GameEvent.snake_die));
        run = false;
        stage.setTitle("       (╯°□°）╯︵ ┻━┻  Snake is kill  (╯°□°）╯︵ ┻━┻  Snake is kill  (╯°□°）╯︵ ┻━┻  Snake is kill  (╯°□°）╯︵ ┻━┻ Snake is kill  (╯°□°）╯︵ ┻━┻ ");
        System.out.println("Game Over");
    }

    public void spawnFood() {
        while(true) {
            Random rnd = new Random();
            int x = rnd.nextInt(20);
            int y = rnd.nextInt(20);

            if(grid[x][y] == null) {

                GridObject apple = new GridObject();
                if(hardMode) {
                    int n = rnd.nextInt(3);
                    if(n == 0)
                        apple.gfxName = "apple";
                    if(n == 1)
                        apple.gfxName = "speedapple";
                    if(n == 2)
                        apple.gfxName = "sizeapple";
                }
                else
                    apple.gfxName = "apple";

                apple.pos = new Vector2(x, y);
                grid[x][y] = apple;

                // Invisible apple after 10 sec in hard mode
                if(hardMode) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            apple.shouldDraw = false;
                        }
                    }, 10000);
                }
                break;
            }
        }
    }
}
