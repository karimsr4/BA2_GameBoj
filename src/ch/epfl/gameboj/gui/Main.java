package ch.epfl.gameboj.gui;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.gameboj.GameBoy;
import ch.epfl.gameboj.component.Joypad;
import ch.epfl.gameboj.component.Joypad.Key;
import ch.epfl.gameboj.component.cartridge.Cartridge;
import ch.epfl.gameboj.component.lcd.LcdController;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public final class Main extends Application {

    private final static Map<KeyCode, Joypad.Key> codeKeyMap = computeCodeKeyMap();
    private final static Map<String, Joypad.Key> textKeyMap = computeTextKeyMap();

    private final static Map<Joypad.Key, Shape> shapeMap = computeShapeMap();

    public static void main(String[] args) {

        Application.launch(args[0]);
    }

    private static Map<Joypad.Key, Shape> computeShapeMap() {

        Map<Joypad.Key, Shape> map = new HashMap<>();

        Circle a = new Circle(19.5, Color.RED);
        a.setTranslateX(111);
        a.setTranslateY(-58);
        map.put(Key.A, a);

        Circle b = new Circle(19.5, Color.RED);
        b.setTranslateX(61);
        b.setTranslateY(-35);
        map.put(Key.B, b);

        Rectangle up = new Rectangle(25, 25);
        up.setTranslateX(-93);
        up.setTranslateY(-67);
        map.put(Key.UP, up);

        Rectangle down = new Rectangle(25, 25);
        down.setTranslateX(-93);
        down.setTranslateY(-20);
        map.put(Key.DOWN, down);

        Rectangle right = new Rectangle(25, 25);
        right.setTranslateX(-70);
        right.setTranslateY(-44);
        map.put(Key.RIGHT, right);

        Rectangle left = new Rectangle(25, 25);
        left.setTranslateX(-118);
        left.setTranslateY(-44);
        map.put(Key.LEFT, left);

        Rectangle start = new Rectangle(30, 8);
        start.setRotate(-26);
        start.setTranslateX(7);
        start.setTranslateY(32);
        map.put(Key.START, start);

        Rectangle select = new Rectangle(30, 8);
        select.setRotate(-26);
        select.setTranslateX(-45);
        select.setTranslateY(32);
        map.put(Key.SELECT, select);

        return map;

    }

    private static Map<String, Key> computeTextKeyMap() {
        Map<String, Key> map = new HashMap<>();
        map.put("A", Key.A);
        map.put("B", Key.B);
        map.put(" ", Key.SELECT);
        map.put("S", Key.START);
        return map;
    }

    private static Map<KeyCode, Key> computeCodeKeyMap() {
        Map<KeyCode, Key> map = new HashMap<>();
        map.put(KeyCode.UP, Key.UP);
        map.put(KeyCode.DOWN, Key.DOWN);
        map.put(KeyCode.RIGHT, Key.RIGHT);
        map.put(KeyCode.LEFT, Key.LEFT);
        return map;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        List<String> param = getParameters().getRaw();
        if (param.size() != 1)
            System.exit(1);

        GameBoy gameboy = new GameBoy(Cartridge.ofFile(new File(param.get(0))));

        ImageView imageview = new ImageView();
        imageview.setFitHeight(2 * LcdController.LCD_HEIGHT);
        imageview.setFitWidth(2 * LcdController.LCD_WIDTH);

        Image controllerImage = new Image(new FileInputStream("controls.png"));
        ImageView controllerImageView = new ImageView(controllerImage);
        controllerImageView.setPreserveRatio(true);
        controllerImageView.setFitWidth(2 * LcdController.LCD_WIDTH);
        Pane controllerPane = new StackPane(controllerImageView);

        controllerImageView.setOnKeyPressed(e -> {
            String keyString = e.getText().toUpperCase();
            Key code = codeKeyMap.get(e.getCode());
            Key text = textKeyMap.get(keyString);
            if (code != null) {
                gameboy.joypad().keyPressed(code);
                if (!controllerPane.getChildren().contains(shapeMap.get(code)))
                    controllerPane.getChildren().add(shapeMap.get(code));
            } else if (text != null) {
                gameboy.joypad().keyPressed(text);
                if (!controllerPane.getChildren().contains(shapeMap.get(text)))
                    controllerPane.getChildren().add(shapeMap.get(text));
            }
            if (keyString.equals("R")) {
                try {
                    start(primaryStage);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

        });
        controllerImageView.setOnKeyReleased(e -> {
            String keyString = e.getText().toUpperCase();
            Key code = codeKeyMap.get(e.getCode());
            Key text = textKeyMap.get(e.getText().toUpperCase());
            if (code != null) {
                gameboy.joypad().keyReleased(code);
                controllerPane.getChildren().remove(shapeMap.get(code));
            } else if (text != null) {
                gameboy.joypad().keyReleased(text);
                controllerPane.getChildren().remove(shapeMap.get(text));
            }

        });

        BorderPane pane = new BorderPane(imageview);
        pane.setBottom(controllerPane);

        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        long start = System.nanoTime();

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                long elapsed = now - start;
                long cycles = (long) (elapsed * GameBoy.CYCLES_PER_NANOSECOND);
                gameboy.runUntil(cycles);
                imageview.setImage(ImageConverter
                        .convert(gameboy.lcdController().currentImage()));
            }
        };

        timer.start();

        primaryStage.show();
        controllerImageView.requestFocus();

    }

}