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

    private final int ORIGIN_X = 640;
    private final int ORIGIN_Y = -2520;
    private int map_x;
    private int map_y;

    private final int CHAR_MAP_WIDTH = 39;
    private final int CHAR_MAP_HEIGHT = 37;
    public ExplorerPanel(ArrayList<ParticleBatch> particleBatchList, Ghost character) {
        this.particleBatchList = particleBatchList;
        setPreferredSize(new Dimension(1280, 720));
        //setBackground(new Color(247, 247, 247));
        setBackground(Color.BLACK);
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
                int map_dx = 0, map_dy = 0;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_UP:
                        dy = 5;
                        map_dy = 20;
                        //System.out.println("UP");
                        break;
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN:
                        dy = -5;
                        map_dy = -20;
                        //System.out.println("RIGHT");
                        break;
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_LEFT:
                        dx = -5;
                        map_dx = -20;
                        character.turnChar(true);
                        //System.out.println("LEFT");
                        break;
                    case KeyEvent.VK_D:
                    case KeyEvent.VK_RIGHT:
                        dx = 5;
                        map_dx = 20;
                        character.turnChar(false);
                        //System.out.println("DOWN");
                        break;
                }
                character.move(dx, dy);
                updateMap(map_dx, map_dy);
                System.out.println("Map X and Y: " + map_x + ", " + map_y);
                repaint(); // Redraw the panel to reflect the character's new position
            }
        });

        // Set focusable to true to receive key events
        setFocusable(true);
        requestFocusInWindow();
    }

    private void updateMap(int dx, int dy) {
        int newX = map_x + dx;
        int newY = map_y + dy;

        if (newX >= CHAR_MAP_WIDTH / 2 && newX <= (1280 * 4 - (CHAR_MAP_WIDTH / 2)) &&  newY >= CHAR_MAP_HEIGHT / 2 && newY <= (720 * 4 - (CHAR_MAP_HEIGHT / 2))) {
            map_x = newX;
            map_y = newY;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Create a Graphics2D object from the Graphics object
        Graphics2D g2d = (Graphics2D) g.create();
        // Set the background color to white
        g2d.setColor(Color.WHITE);
        g2d.fillRect(ORIGIN_X - map_x, ORIGIN_Y + map_y, getWidth() * 4, getHeight() * 4);

        // Draw particles
        for (ParticleBatch batch : particleBatchList) {
            ArrayList<Particle> particleList = batch.getParticles();
            for (Particle particle : particleList) {
                particle.drawMap(g2d); // Use the transformed Graphics2D object
            }
        }

        // Draw the character
        character.drawMap(g2d); // Use the transformed Graphics2D object

        // Dispose of the Graphics2D object to free up resources
        g2d.dispose();

        // Update FPS label position dynamically
        fpsLabel.setBounds(getWidth() - 110, getHeight() - 30, 100, 20);

        // Run a function after paintComponent is done
        SwingUtilities.invokeLater(this::runFPSCounter);
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