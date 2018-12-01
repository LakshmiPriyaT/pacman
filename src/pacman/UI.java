package pacman;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import pacman.entities.BonusPickable;
import pacman.slots.Corridor;
import pacman.slots.GhostDoor;
import pacman.slots.Wall;

import pacman.entities.PacMan;
import pacman.entities.Ghost;
import pacman.entities.Pickable;


import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import java.lang.Object;


public class UI extends Application implements Observer {
    static private GraphicsContext graphicsContext;
    static private Game game;
    static private int windowWidth = 900;
    static private int windowHeight = 900;
    static private Image pacmanImage;
    static private Image pacmanLeftImage;
    static private Image blueGhostImage;
    static private Image greenGhostImage;
    static private Image pickableImage;
    static private Image BonusPickableImage;
    final private double imageRatio = 0.7;

    private void drawImage(Image image, int slotSize, int paddingLeft, int paddingTop, int x, int y, boolean enableRatio) {
        double topLeftPadding = enableRatio ? (slotSize * (1 - imageRatio) / 2) : 0;
        double slotSizeScaled = enableRatio ? slotSize * imageRatio : slotSize;

        graphicsContext.drawImage(
                image,
                topLeftPadding + paddingLeft + x * slotSize,
                topLeftPadding + paddingTop + y * slotSize,
                slotSizeScaled,
                slotSizeScaled
        );
    }

    public void update(Observable observable, Object arg) {
        Slot[][] matrix = game.getMatrix();

        int slotSize = Math.max(windowWidth / game.getWidth(), windowHeight / game.getHeight());

        int paddingLeft = (windowWidth - slotSize * game.getWidth()) / 2;
        int paddingTop = (windowHeight - slotSize * game.getHeight()) / 2;

        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[x].length; y++) {
                Slot slot = matrix[x][y];
                if (slot instanceof Wall) {
                    graphicsContext.setFill(Color.BLUE);
                } else if (slot instanceof Corridor) {
                    graphicsContext.setFill(Color.BLACK);
                } else if (slot instanceof GhostDoor) {
                    graphicsContext.setFill(Color.PINK);
                } else {
                    graphicsContext.setFill(Color.BLACK);
                }
                graphicsContext.fillRect(paddingLeft + x * slotSize, paddingTop + y * slotSize, slotSize, slotSize);
            }
        }

        ArrayList<Entity> entities = game.getEntities();

        for (Entity entity : entities) {
            if (entity instanceof PacMan) {
                PacMan pacMan = (PacMan) entity;

                drawImage(pacMan.getDirection() == Direction.LEFT ? pacmanLeftImage : pacmanImage, slotSize, paddingLeft, paddingTop, pacMan.getPosition().x, pacMan.getPosition().y, true);
            } else if (entity instanceof Ghost) {
                drawImage(blueGhostImage, slotSize, paddingLeft, paddingTop, entity.getPosition().x, entity.getPosition().y, true);
            } else if (entity instanceof Pickable) {
                drawImage(pickableImage, slotSize, paddingLeft, paddingTop, entity.getPosition().x, entity.getPosition().y, false);
            }
            else if (entity instanceof BonusPickable) {
                drawImage(BonusPickableImage, slotSize, paddingLeft, paddingTop, entity.getPosition().x, entity.getPosition().y, false);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Canvas canvas = new Canvas(windowWidth, windowHeight);
        graphicsContext = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);

        primaryStage.setTitle("PacMan");
        primaryStage.setResizable(false);
        Scene scene = new Scene(root, Color.BLACK);

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case UP:
                case Z:
                    game.playerMove(Direction.UP);
                    break;
                case DOWN:
                case S:
                    game.playerMove(Direction.DOWN);
                    break;
                case LEFT:
                case Q:
                    game.playerMove(Direction.LEFT);
                    break;
                case RIGHT:
                case D:
                    game.playerMove(Direction.RIGHT);
                    break;
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();

        pacmanImage = new Image("file:assets/sprites/pacman.png");
        pacmanLeftImage = new Image("file:assets/sprites/pacman-left.png");
        pickableImage = new Image("file:assets/sprites/pickable.png");
 	    BonusPickableImage = new Image("file:assets/sprites/mega-pickable.png");
        blueGhostImage = new Image("file:assets/sprites/blue-ghost.png");
        greenGhostImage = new Image("file:assets/sprites/green-ghost.png");

        game.load();
    }

    void bootstrap(Game g) {
        game = g;

        String[] args = new String[0];
        launch(args);
    }
}
