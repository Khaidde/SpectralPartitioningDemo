package graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.util.Queue;
import java.util.LinkedList;
import java.util.function.Consumer;

class Mouse implements MouseListener, MouseMotionListener {

    public final Position currentPos = new Position();
    private boolean isLeftClicked = false;
    private boolean isRightClicked = false;

    public boolean isLeftClicked() {
        if (isLeftClicked) {
            isLeftClicked = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean isRightClicked() {
        if (isRightClicked) {
            isRightClicked = false;
            return true;
        } else {
            return false;
        }
    }

    public void mouseClicked(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {
        switch (e.getButton()) {
            case (MouseEvent.BUTTON1):
                this.isLeftClicked = true;
                break;
            case (MouseEvent.BUTTON3):
                this.isRightClicked = true;
                break;
            default:
                break;
        }
    }

    public void mouseReleased(MouseEvent e) {
        switch (e.getButton()) {
            case (MouseEvent.BUTTON1):
                this.isLeftClicked = false;
                break;
            case (MouseEvent.BUTTON3):
                this.isRightClicked = false;
                break;
            default:
                break;
        }
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mouseDragged(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {
        this.currentPos.x = e.getX();
        this.currentPos.y = e.getY();
    }
}

public class Window {

    private int width;
    private int height;
    private final Canvas canvas;
    private final JFrame frame;

    private final Mouse mouse;

    private BufferStrategy bufferStrategy;
    private Graphics graphics2D;

    public Window(String title, int width, int height) {
        this.width = width;
        this.height = height;
        this.canvas = new Canvas();

        this.frame = new JFrame(title);
        this.frame.getContentPane().setPreferredSize(new Dimension(width, height)); //Set size of window
        this.frame.setResizable(false); //Lock screen dimensions
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //Allow window to be closed through the red x button

        this.mouse = new Mouse();
        this.canvas.addMouseListener(mouse);
        this.canvas.addMouseMotionListener(mouse);

        this.frame.add(canvas); //Add graphical canvas onto window which can be drawn on
        this.frame.setVisible(true); //Display the window
        this.frame.pack(); //Apply all changes

        setupCanvas();
    }

    public Mouse getMouse() {
        return mouse;
    }

    public void setupCanvas() {
        this.canvas.createBufferStrategy(3); //Window has three rendering buffers which cycle to create smoothness (prevent window from flashing)
        bufferStrategy = canvas.getBufferStrategy(); //Object which records and stores data pertaining to buffering
    }

    public void clearScreen() {
        //Clears the screen by filling the entire screen with a blank rectangle
        graphics2D = bufferStrategy.getDrawGraphics();
        graphics2D.setColor(Color.BLACK);
        graphics2D.fillRect(0, 0, width, height);
    }

    public void render(Consumer<Graphics> action) {
        this.clearScreen();
        action.accept(graphics2D);
        this.dispose();
    }

    public void dispose() {
        //Draw all graphics to the window
        bufferStrategy.show();
        graphics2D.dispose();
    }

}

