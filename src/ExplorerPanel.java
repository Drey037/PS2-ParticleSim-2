import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ExplorerPanel extends JPanel {
    private long lastUpdateTime;
    private int frames;
    private JLabel fpsLabel;
    private ArrayList<ParticleBatch> particleBatchList;

    private Ghost character;

    private int map_x;
    private int map_y;

    private final int CHAR_MAP_WIDTH = 39;
    private final int CHAR_MAP_HEIGHT = 37;

    // CANVAS SETTINGS
    private final int ROW = 19;
    private final int COL = 33;
    private final int WIDTH = 1280;
    private final int HEIGHT = 720;

    private final int ZOOMX = WIDTH / COL;
    private final int ZOOMY = HEIGHT / ROW;

    public ExplorerPanel(ArrayList<ParticleBatch> particleBatchList, Ghost character) {
        this.particleBatchList = particleBatchList;
        setPreferredSize(new Dimension(1280, 720));
        //setBackground(new Color(247, 247, 247));
        setBackground(new Color(247, 247, 247));
        setLayout(null); // Use null layout to manually position components

        map_x = (CHAR_MAP_WIDTH / 2);
        map_y = (CHAR_MAP_HEIGHT / 2);

        // Load character texture
        this.character = character;

        // FPS Label
        fpsLabel = new JLabel("EM FPS: 0");
        fpsLabel.setForeground(new Color(173, 200, 220));
        fpsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(fpsLabel);

        // Add key listeners for character movement
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int dx = 0, dy = 0;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_UP:
                        dy = 1;
                        //System.out.println("UP");
                        break;
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN:
                        dy = -1;
                        //System.out.println("RIGHT");
                        break;
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_LEFT:
                        dx = -1;
                        character.turnChar(true);
                        //System.out.println("LEFT");
                        break;
                    case KeyEvent.VK_D:
                    case KeyEvent.VK_RIGHT:
                        dx = 1;
                        character.turnChar(false);
                        //System.out.println("DOWN");
                        break;
                }
                character.move(dx, dy);
                repaint(); // Redraw the panel to reflect the character's new position
            }
        });

        // Set focusable to true to receive key events
        setFocusable(true);
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {

        g.setColor(Color.WHITE);
        g.fillRect(0,0, WIDTH, HEIGHT);

        Graphics2D g2d = (Graphics2D) g.create();

        int viewX = character.getX() - COL / 2;
        int viewY = HEIGHT - character.getY() - ROW / 2;

        // Draw black bars if viewport is out of bounds
        drawVoid(g2d, viewX, viewY);

        // Adjust viewport to ensure it's within bounds
        //viewX = Math.max(0, Math.min(viewX, WIDTH - COL));
        //viewY = Math.max(0, Math.min(viewY, HEIGHT - ROW));

        // Draw particles
        for (ParticleBatch batch : particleBatchList) {
            ArrayList<Particle> particleList = batch.getParticles();
            for (Particle particle : particleList) {
                int relX = particle.getX() - viewX;
                int relY = HEIGHT - particle.getY() - viewY;

                if (relX >= 0 && relX < COL && relY >= 0 && relY < ROW) {
                    int pX = relX * ZOOMX;
                    int pY = relY * ZOOMY;
                    g2d.setColor(particle.getColor());
                    System.out.println("particle Entered");
                    g2d.fillRect(pX, pY, particle.getSize() * 4, particle.getSize() * 4);
                }
            }
        }

        // Draw the character
        character.drawMap(g); // Use the transformed Graphics2D object
        // Dispose of the Graphics2D object to free up resources
        g2d.dispose();

        // Update FPS label position dynamically
        fpsLabel.setBounds(getWidth() - 110, getHeight() - 30, 100, 20);

        // Run a function after paintComponent is done
        SwingUtilities.invokeLater(this::runFPSCounter);
    }

    private void drawVoid(Graphics2D graphics, int viewX, int viewY) {
        graphics.setColor(Color.BLACK);
        // Adjust for character size

        // Adjust the viewport calculations to consider the character's size
        if (viewX <= 0) {
            graphics.fillRect(0, 0, Math.abs(viewX) * ZOOMX, HEIGHT);
        }
        if (viewY <= 0) {
            graphics.fillRect(0, 0, WIDTH, Math.abs(viewY) * ZOOMY);
        }
        if (viewX + COL >= WIDTH) {
            int overflowWidth = (viewX + COL - WIDTH) * ZOOMX - 12;
            graphics.fillRect(WIDTH - overflowWidth, 0, overflowWidth, HEIGHT);
        }
        if (viewY + ROW >= HEIGHT) {
            int overflowHeight = (viewY + ROW - HEIGHT) * ZOOMY - 10;
            graphics.fillRect(0, HEIGHT - overflowHeight, WIDTH, overflowHeight);
        }
    }


    private void runFPSCounter() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastUpdateTime;
        frames++;

        if (elapsed >= 500) { // 0.5 seconds
            final int fps = (int) ((frames * 1000) / elapsed);
            SwingUtilities.invokeLater(() -> fpsLabel.setText("EM FPS: " + fps));
            frames = 0;
            lastUpdateTime = now;
        }
        try {
            Thread.sleep(Math.max(1, (1000 / 60) - (System.currentTimeMillis() - now)));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}