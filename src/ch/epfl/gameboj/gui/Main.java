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
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public final class Main extends Application {
    
    

    public static void main(String[] args) {
        
        Application.launch(args[0]);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        List<String> param = getParameters().getRaw();
        if (param.size() != 1)
            System.exit(1);

        GameBoy gameboy = new GameBoy(Cartridge.ofFile(new File(param.get(0))));

        Map<KeyCode, Joypad.Key> codeKeysMap = new HashMap<>();
        codeKeysMap.put(KeyCode.UP, Key.UP);
        codeKeysMap.put(KeyCode.DOWN, Key.DOWN);
        codeKeysMap.put(KeyCode.RIGHT, Key.RIGHT);
        codeKeysMap.put(KeyCode.LEFT, Key.LEFT);

        Map<String, Joypad.Key> textKeysMap = new HashMap<>();
        textKeysMap.put("A", Key.A);
        textKeysMap.put("B", Key.B);
        textKeysMap.put(" ", Key.SELECT);
        textKeysMap.put("S", Key.START);
        
//        Map<Joypad.Key, > keyPosition =new HashMap<> ();
//        keyPosition.put( Key.A, );
//        keyPosition.put( Key.B);
//        keyPosition.put( Key.SELECT);
//        keyPosition.put( Key.START);
//        
            
        

        ImageView imageview = new ImageView();
        imageview.setFitHeight(2 * LcdController.LCD_HEIGHT);
        imageview.setFitWidth(2 * LcdController.LCD_WIDTH);
        
       
        Image controllerImage = new Image(new FileInputStream("controls.png"));
        ImageView controllerImageView =new ImageView(controllerImage);
        controllerImageView.setPreserveRatio(true);
        controllerImageView.setFitWidth(2*LcdController.LCD_WIDTH);
        Pane controllerPane = new StackPane(controllerImageView);
//        controllerPane.add(controllerImageView,0,0);
        Circle s= new Circle(20,javafx.scene.paint.Color.RED);
       
        s.setTranslateX(62);
        s.setTranslateY(-35);
//        controllerPane.getChildren().add(s);
        
        Circle s2=new Circle(20,javafx.scene.paint.Color.RED);
        s2.setTranslateX(111);
        s2.setTranslateY(-60);
        controllerPane.getChildren().add(s2);
        controllerImageView.setOnMouseClicked(e -> {
            System.out.println(e.getX());
            System.out.println(e.getY());
        });
        
        controllerImageView.setOnKeyPressed(e -> {
            
        });
        
        
        

        controllerImageView.setOnKeyPressed(e -> {
            String keyString= e.getText().toUpperCase();
            Key code = codeKeysMap.get(e.getCode());
            Key text = textKeysMap.get(keyString);
            if (code != null) {
                gameboy.joypad().keyPressed(code);
            } else if (text != null) {
                gameboy.joypad().keyPressed(text);
            } if (keyString.equals("R")) {
                try {
                    start(primaryStage);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } 
            if(keyString.equals("A"))
                controllerPane.getChildren().add(s);
                
        });
        controllerImageView.setOnKeyReleased(e -> {
            String keyString= e.getText().toUpperCase();
            Key code = codeKeysMap.get(e.getCode());
            Key text = textKeysMap.get(e.getText().toUpperCase());
            if (code != null) {
                gameboy.joypad().keyReleased(code);
            } else if (text != null) {
                gameboy.joypad().keyReleased(text);
            }
            if(keyString.equals("A"))
                controllerPane.getChildren().remove(s);
            }) ;
       
       
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