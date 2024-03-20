import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ParticlePanel extends JPanel {
    private long lastUpdateTime;
    private int frames;
    private JLabel fpsLabel;

    private Ghost character;

    private ArrayList<ParticleBatch> particleBatchList;

    public ParticlePanel(ArrayList<ParticleBatch> particleBatchList, Ghost character) {
        this.particleBatchList = particleBatchList;
        setPreferredSize(new Dimension(1280, 720));
        setBackground(new Color(247, 247, 247));
        setLayout(null); // Use null layout to manually position components

        this.character = character;

        // FPS Label
        fpsLabel = new JLabel("DM FPS: 0");
        fpsLabel.setForeground(new Color(173, 200, 220));
        fpsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(fpsLabel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw particles

        for (ParticleBatch batch: particleBatchList) {
            ArrayList<Particle> particleList = batch.getParticles();
            for (Particle particle : particleList) {
                particle.draw(g);
            }
        }

        character.draw(g);

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
            SwingUtilities.invokeLater(() -> fpsLabel.setText("DM FPS: " + fps));
            frames = 0;
            lastUpdateTime = now;
        }
        try {
            Thread.sleep(Math.max(1, (1000 / 60) - (System.currentTimeMillis() - now)));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Add other methods related to particle drawing and FPS counter here
}