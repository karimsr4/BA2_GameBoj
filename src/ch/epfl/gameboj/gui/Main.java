package ch.epfl.gameboj.gui;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.gameboj.GameBoy;
import ch.epfl.gameboj.component.Joypad;
import ch.epfl.gameboj.component.Joypad.Key;
import ch.epfl.gameboj.component.cartridge.Cartridge;
import ch.epfl.gameboj.component.lcd.LcdController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }
    

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        List<String> param = getParameters().getRaw();
        if (param.size() != 1)
            System.exit(1);
        
        GameBoy gameboy = new GameBoy(Cartridge.ofFile(new File(param.get(0))));
        
        Map <KeyCode , Joypad.Key>  codeKeysMap= new HashMap<>();
        codeKeysMap.put(KeyCode.UP, Key.UP);
        codeKeysMap.put(KeyCode.DOWN, Key.DOWN);
        codeKeysMap.put(KeyCode.RIGHT, Key.RIGHT);
        codeKeysMap.put(KeyCode.LEFT, Key.LEFT);
        
        Map <String , Joypad.Key> textKeysMap = new HashMap<>();
        textKeysMap.put("A", Key.A);
        textKeysMap.put("B", Key.B);
        textKeysMap.put(" ", Key.SELECT);
        textKeysMap.put("S", Key.START);
        
        
        
        Image image = ImageConverter
                .convert(gameboy.lcdController().currentImage());
        ImageView imageview = new ImageView();
        imageview.setFitHeight(2 * LcdController.LCD_HEIGHT);
        imageview.setFitWidth(2 * LcdController.LCD_WIDTH);
        imageview.setImage(image);
        imageview.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            String text = e.getText().toUpperCase();
            if(codeKeysMap.containsKey(code)) {
                gameboy.joypad().keyPressed(codeKeysMap.get(code));
            }else if (textKeysMap.containsKey(text)) {
                gameboy.joypad().keyPressed(textKeysMap.get(text));
            }
        });
        imageview.setOnKeyReleased(e -> {
            KeyCode code = e.getCode();
            String text = e.getText().toUpperCase();
            if(codeKeysMap.containsKey(code)) {
                gameboy.joypad().keyReleased(codeKeysMap.get(code));
            }else if (textKeysMap.containsKey(text)) {
                gameboy.joypad().keyReleased(textKeysMap.get(text));
            }
        });
        
        BorderPane pane = new BorderPane(imageview);
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
        imageview.requestFocus();
        

    }

}
