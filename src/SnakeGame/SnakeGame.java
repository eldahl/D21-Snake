package SnakeGame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SnakeGame extends Application {

    public static int gridSize = 44;

    public static Label lScore;
    public static Label lSnakeLength;

    public static Timeline gameTick;

    public static Stage gameStage;
    public static Stage mainStage;

    public static Canvas canvas;

    public static boolean inMainMenu = true;

    public static boolean hardMode = false;
    public static boolean insaneMode = false;

    public static Game game;

    @Override
    public void start(Stage stage) throws Exception {
        
        // Main menu stage
        mainStage = new Stage();
        mainStage.setTitle("    ༼ つ ◕_◕ ༽つ Snake ༼ つ ◕_◕ ༽つ Snake");
        mainStage.getIcons().add(new Image("file:res/icon.png"));
        mainStage.setResizable(false);

        VBox mainRoot = new VBox();
        mainRoot.setAlignment(Pos.BOTTOM_CENTER);
        mainRoot.setPadding(new Insets(0, 0, 60, 0));

        // Start game button
        Button mainStartGameBut = new Button();
        mainStartGameBut.setText("- Start Game -");
        mainStartGameBut.setOnAction((e) -> {
            gotoGame();
        });

        // Hard mode checkbox
        CheckBox mainHardCheck = new CheckBox();
        mainHardCheck.setText("Hard mode");
        mainHardCheck.setPadding(new Insets(8,0,0,0));
        mainHardCheck.setOnAction((e) -> {
            hardMode = mainHardCheck.isSelected();
        });
        // Insane mode checkbox
        CheckBox mainInsaneCheck = new CheckBox();
        mainInsaneCheck.setText("Insane mode");
        mainInsaneCheck.setPadding(new Insets(8,0,16,0));
        mainInsaneCheck.setOnAction((e) -> {
            insaneMode = mainInsaneCheck.isSelected();
        });

        // Exit game button
        Button mainExitGameBut = new Button();
        mainExitGameBut.setText("- Exit Game -");
        mainExitGameBut.setOnAction((e) -> {
            exitGame();
        });

        mainRoot.getChildren().add(mainStartGameBut);
        mainRoot.getChildren().add(mainHardCheck);
        mainRoot.getChildren().add(mainInsaneCheck);
        mainRoot.getChildren().add(mainExitGameBut);

        Scene mainScene = new Scene(mainRoot, 400, 300);

        mainStage.setScene(mainScene);
        mainStage.show();


        mainStage.setOnCloseRequest(t -> {
            exitGame();
        });
    }

    public static void setupGameStage(Stage stage) {
        // Game menu stage
        gameStage = stage;

        // Title & nice little icon
        stage.setTitle("       ༼ つ ◕_◕ ༽つ Snake ༼ つ ◕_◕ ༽つ Snake ༼ つ ◕_◕ ༽つ Snake ༼ つ ◕_◕ ༽つ Snake ༼ つ ◕_◕ ༽つ Snake ༼ つ ◕_◕ ༽つ Snake ༼ つ ◕_◕ ༽つ ");
        //stage.setTitle("       (╯°□°）╯︵ ┻━┻  Snake is kill  (╯°□°）╯︵ ┻━┻  Snake is kill  (╯°□°）╯︵ ┻━┻  Snake is kill  (╯°□°）╯︵ ┻━┻ Snake is kill  (╯°□°）╯︵ ┻━┻ ");
        stage.getIcons().add(new Image("file:res/icon.png"));


        // JavaFX
        Pane root = new Pane();
        Scene scene = new Scene(root);

        VBox vbox = new VBox();
        HBox hbox = new HBox();
        lScore = new Label();
        lSnakeLength = new Label();

        lScore.setText("Score: 0 | ");
        lSnakeLength.setText("Snake Length: 0");

        canvas = new Canvas(880, 880);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        hbox.getChildren().add(lScore);
        hbox.getChildren().add(lSnakeLength);
        vbox.getChildren().add(hbox);
        vbox.getChildren().add(canvas);
        root.getChildren().add(vbox);
        stage.setScene(scene);
        stage.setResizable(false); // Resizing not allowed
        stage.show();

        // Make the program exit itself after window is closed
        stage.setOnCloseRequest(t -> {
            exitGame();
        });
    }

    public static void gotoMainMenu() {
        gameStage.hide();
        gameTick.stop();
        gameTick = null;
        game.speedTimer.cancel();
        game.speedTimer = null;
        game.bigHeadTimer.cancel();
        game.bigHeadTimer = null;
        game.insaneTimer.cancel();
        game.insaneTimer = null;
        game.run = false;
        game = null;
        gameStage = null;
        mainStage.show();
        inMainMenu = true;

    }

    public static void gotoGame() {
        mainStage.hide();
        inMainMenu = false;

        gameStage = new Stage();
        setupGameStage(gameStage);

        // Game init
        game = new Game();
        game.init(gameStage, canvas, hardMode, insaneMode);

        if (gameTick == null) {
            // Game tick
            gameTick = new Timeline(new KeyFrame(Duration.millis(200), (e) -> {
                if (game.run)
                    game.update();
            }));
            gameTick.setCycleCount(Timeline.INDEFINITE);
            gameTick.play();
        }

        // Start the game
        game.run = true;
    }

    public static void exitGame() {
        Platform.setImplicitExit(true);
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
