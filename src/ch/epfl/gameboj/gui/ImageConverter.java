package ch.epfl.gameboj.gui;
import ch.epfl.gameboj.component.lcd.LcdController;
import ch.epfl.gameboj.component.lcd.LcdImage;
import javafx.scene.image.Image ;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public final class ImageConverter {
    private static final int[] COLOR_MAP = new int[] {
            0xFF_FF_FF_FF, 0xFF_D3_D3_D3, 0xFF_A9_A9_A9, 0xFF_00_00_00
          };
    public static  Image convert (LcdImage other) {
        
        WritableImage result = new WritableImage(LcdController.LCD_WIDTH, LcdController.LCD_HEIGHT);
        PixelWriter writer = result.getPixelWriter() ;
        
        for(int y =0 ;y<other.height(); y++)
            for(int x=0 ; x< other.width(); x++)
                writer.setArgb(x, y, COLOR_MAP[other.get(x, y)]);
        
        return result;
        
    }    

}
