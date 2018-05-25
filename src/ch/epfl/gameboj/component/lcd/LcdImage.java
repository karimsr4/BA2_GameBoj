package ch.epfl.gameboj.component.lcd;

import static ch.epfl.gameboj.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ch.epfl.gameboj.bits.Bits;

/**
 * Classe représentant une image Game Boy
 * 
 * @author Karim HADIDANE (271018)
 * @author Ahmed JELLOULI (274056)
 */
public final class LcdImage {

    private final List<LcdImageLine> lines;
    private final int width;
    private final int height;

    /**
     * construit une image GameBoy
     * 
     * @param width
     *            largeur de l'image
     * @param height
     *            hauteur de l'image
     * @param lines
     *            les lignes de l'image
     * @throws IllegalArgumentException
     *             si le nombre des lignes est différent de la hauteur
     * 
     */
    public LcdImage(int width, int height, List<LcdImageLine> lines) {

        checkArgument(lines.size() == height);

        this.width = width;
        this.height = height;
        this.lines = Collections
                .unmodifiableList(new ArrayList<LcdImageLine>(lines));

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        return (o instanceof LcdImage) && lines.equals(((LcdImage) o).lines);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return lines.hashCode();

    }

    /**
     * retourne, sous la forme d'un entier compris entre 0 et 3, la couleur d'un
     * pixel d'index (x, y) donné
     * 
     * @param x
     *            index dans la ligne
     * @param y
     *            index de la ligne
     * @return la couleur d'un pixel d'index (x,y)
     * @throws IndexOutOfBoundsException
     *             si l'index n'est pas valide
     */
    public int get(int x, int y) {

        LcdImageLine line = lines.get(y);

        return Bits.set(0, 1, line.getMsb().testBit(x))
                | Bits.set(0, 0, line.getLsb().testBit(x));

    }

    /**
     * retourne la largeur de l'image
     * 
     * @return la largeur de l'image
     */
    public int width() {
        return width;
    }

    /**
     * retourne la hauteur de l'image
     * 
     * @return la hauteur de l'image
     */
    public int height() {
        return height;
    }

    /**
     * Classe représentant un bâtisseur d'image Game Boy
     * 
     * @author Karim HADIDANE (271018)
     * @author Ahmed JELLOULI (274056)
     */
    public final static class Builder {

        private final List<LcdImageLine> lines;
        private final int height, width;
        private boolean isBuilded;

        /**
         * construit un bâtisseur d'image
         * 
         * @param width
         *            largeur de l'image
         * @param height
         *            hauteur de l'image
         */
        public Builder(int width, int height) {
            lines = new ArrayList<>(Collections.nCopies(height,
                    new LcdImageLine.Builder(width).build()));
            this.width = width;
            this.height = height;
            isBuilded = false;
        }

        /**
         * change la ligne d'index donné en la ligne donnèe
         * 
         * @param index
         *            index donné
         * @param newLine
         *            nouvelle ligne
         * @return ce bâtisseur
         * @throws IndexOutOfBoundsException
         *             si l'index n'est pas valide
         * @throws IllegalStateException
         *             si la méthode est appelée après que l'image
         *             est construite
         */
        public Builder setLine(int index, LcdImageLine newLine) {
            if (isBuilded)
                throw new IllegalStateException();
            Objects.checkIndex(index, height);
            checkArgument(newLine.size() == width);
            lines.set(index, newLine) ;
            return this;

        }

        /**
         * construit l'image associée à ce batisseur
         * 
         * @return l'image construite
         * @throws IllegalStateException
         *             si la méthode est appelée après que l'image
         *             est construite
         */
        public LcdImage build() {
            if (isBuilded)
                throw new IllegalStateException();
            return new LcdImage(width, height, lines);
        }
    }

}
