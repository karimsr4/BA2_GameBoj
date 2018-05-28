package ch.epfl.gameboj.gui;

import java.awt.image.BufferedImage;

import ch.epfl.gameboj.component.lcd.LcdController;
import ch.epfl.gameboj.component.lcd.LcdImage;
import javafx.scene.image.Image ;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public final class ImageConverter {
    private static final int[] COLOR_MAP = new int[] {
            0xFF_FF_FF_FF, 0xFF_D3_D3_D3, 0xFF_A9_A9_A9, 0xFF_00_00_00
          };
    private static final int[] COLOR_MAP_BUFF = new int[] {
            0xFF_FF_FF, 0xD3_D3_D3, 0xA9_A9_A9, 0x00_00_00
          };

    public static  Image convert (LcdImage other) {
        
        WritableImage result = new WritableImage(LcdController.LCD_WIDTH, LcdController.LCD_HEIGHT);
        PixelWriter writer = result.getPixelWriter() ;
        
        for(int y =0 ;y<other.height(); ++y)
            for(int x=0 ; x< other.width(); ++x)
                writer.setArgb(x, y, COLOR_MAP[other.get(x, y)]);
        
        return result;
        
    } 
public static  BufferedImage Bufferedconvert (LcdImage other) {
        
        BufferedImage result =new BufferedImage(other.width(),other.height() , BufferedImage.TYPE_INT_RGB);
        
        
        for(int y =0 ;y<other.height(); y++)
            for(int x=0 ; x< other.width(); x++)
                result.setRGB(x, y, COLOR_MAP_BUFF[other.get(x, y)]);
        
        return result;
        
    }
    

}
