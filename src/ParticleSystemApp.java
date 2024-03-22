import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ParticleSystemApp extends JFrame {

    private final Object particleListLock = new Object();

    // GUI attributes
    private ParticlePanel particlePanel;

    private Ghost character;

    private ExplorerPanel explorerPanel;
    private JPanel inputPanel;
    private JTextField startXField, startYField, endXField, endYField, startThetaField, endThetaField, startVelocityField, endVelocityField, nField;
    private JButton submitParticleButton, explorerModeButton;

    private Boolean isExplorerMode;

    private JComboBox<String> batchOptions;

    private JLabel fpsLabel;

    private ArrayList<ParticleBatch> particleBatchList;

    private final int MIN_VELOCITY = 1;
    private final int MAX_VELOCITY = 1000;

    private final int MAX_LOAD = 10;

    private long lastUpdateTime;
    private Image texture_left, texture_right;

    private int frames;
    public ParticleSystemApp() {
        setTitle("Particle Simulation Explorer");
        setSize(1540, 720); // The window itself
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(Color.GRAY); // Set the default window color

        //Initializing the batch list
        isExplorerMode = false;
        particleBatchList = new ArrayList<ParticleBatch>();
        ParticleBatch tempPb = new ParticleBatch();
        tempPb.start();
        particleBatchList.add(tempPb);


        try {
            File imageUrl_left = new File("../PS2-ParticleSim-2/assets/ghost_left.png");
            texture_left = ImageIO.read(imageUrl_left);

            File imageUrl_right = new File("../PS2-ParticleSim-2/assets/ghost_right.png");
            texture_right = ImageIO.read(imageUrl_right);
        } catch (IOException e) {
            e.printStackTrace();
        }
        character = new Ghost(1, 1, texture_left, texture_right);

        // Particle System Panel
        particlePanel = new ParticlePanel(particleBatchList, character);
        explorerPanel = new ExplorerPanel(particleBatchList, character);
        add(particlePanel, BorderLayout.CENTER);

        // Input Panel
        inputPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.setPreferredSize(new Dimension(260, 720)); // Main -> width is - input panel width
        inputPanel.setLayout(new GridLayout(13, 2));
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("PARTICLE SPECIFICATIONS"),
            BorderFactory.createEmptyBorder(2, 2,2, 2)));


        // Particle Input
        nField = new JTextField();
        startXField = new JTextField();
        startYField = new JTextField();
        endXField = new JTextField();
        endYField = new JTextField();
        startThetaField = new JTextField();
        endThetaField = new JTextField();
        startVelocityField = new JTextField();
        endVelocityField = new JTextField();
        submitParticleButton = new JButton("Add Particle");

        // Explorer Mode BUtton
        explorerModeButton = new JButton("Explorer Mode");

        // Batch options to the input panel
        String[] batchOptionsArray = {"Different Points", "Different Angles", "Different Velocities"};
        JComboBox<String> batchOptions = new JComboBox<>(batchOptionsArray);
        batchOptions.setSelectedItem("Default"); // Set default option
        inputPanel.add(new JLabel("Specifications"));
        inputPanel.add(batchOptions);

        // INITIAL INPUT PANEL
        addToInputPanel("Number of Particles:", nField);
        addToInputPanel("Start X:", startXField);
        addToInputPanel("Start Y:", startYField);
        addToInputPanel("End X:", endXField);
        addToInputPanel("End Y:", endYField);
        addToInputPanel("Theta:", startThetaField);
        addToInputPanel("Velocity:", startVelocityField);

        // Method to add a label with border
        
        inputPanel.add(explorerModeButton);
        inputPanel.add(submitParticleButton);
        
        

        // Action listener for the JComboBox
        batchOptions.addActionListener(e -> {
            String selectedOption = (String) batchOptions.getSelectedItem();
            inputPanel.removeAll(); // Clear previous components

            switch (selectedOption) {
                case "Different Points":
                    inputPanel.add(new JLabel("Specifications:"));
                    inputPanel.add(batchOptions);
                    addToInputPanel("Number of Particles:", nField);
                    addToInputPanel("Start X:", startXField);
                    addToInputPanel("Start Y:", startYField);
                    addToInputPanel("End X:", endXField);
                    addToInputPanel("End Y:", endYField);
                    addToInputPanel("Theta:", startThetaField);
                    addToInputPanel("Velocity:", startVelocityField);

                    // Method to add a label with border
                    inputPanel.add(explorerModeButton);
                    inputPanel.add(submitParticleButton);
                    break;
                case "Different Angles":
                    inputPanel.add(new JLabel("Specifications:"));
                    inputPanel.add(batchOptions);

                    addToInputPanel("Number of Particles:", nField);
                    addToInputPanel("X:", startXField);
                    addToInputPanel("Y:", startYField);
                    addToInputPanel("Start Theta:", startThetaField);
                    addToInputPanel("End Theta:", endThetaField); // Reusing velocityField for endTheta
                    addToInputPanel("Velocity:", startVelocityField); // Reusing x1Field for velocity

                    // Method to add a label with border
                    inputPanel.add(explorerModeButton);
                    inputPanel.add(submitParticleButton);
                    break;
                case "Different Velocities":
                    inputPanel.add(new JLabel("Specifications:"));
                    inputPanel.add(batchOptions);

                    addToInputPanel("Number of Particles:", nField);
                    addToInputPanel("X:", startXField);
                    addToInputPanel("Y:", startYField);
                    addToInputPanel("Theta:", startThetaField);
                    addToInputPanel("Start Velocity:", startVelocityField); // Reusing x1Field for velocity
                    addToInputPanel("End Velocity:", endVelocityField); // Reusing x1Field for endVelocity

                    // Method to add a label with border
                    inputPanel.add(explorerModeButton);
                    inputPanel.add(submitParticleButton);
                    break;
                default:
                    break;
            }

            inputPanel.revalidate(); // Update layout
            inputPanel.repaint(); // Redraw panel
        });



        // Particle input handling
        submitParticleButton.addActionListener(e -> {
            try {
                int startX, endX, startY, endY, x, y, n;
                double theta, velocity, startTheta, endTheta, startVelocity, endVelocity;

                switch (batchOptions.getSelectedIndex()) {
                    case 0: // Constant Velocity and Angle
                        // Retrieve start and end points
                        n = Integer.parseInt(nField.getText());
                        startX = Integer.parseInt(startXField.getText());
                        endX = Integer.parseInt(endXField.getText());
                        startY = Integer.parseInt(startYField.getText());
                        endY = Integer.parseInt(endYField.getText());
                        theta = Double.parseDouble(startThetaField.getText());
                        velocity = Double.parseDouble(startVelocityField.getText());

                        // Validate inputs (e.g., check if coordinates are within the window bounds)
                        if (startX < 0 || startX > particlePanel.getWidth() || startY < 0 || startY > particlePanel.getHeight() ||
                                endX < 0 || endX > particlePanel.getWidth() || endY < 0 || endY > particlePanel.getHeight()) {
                            JOptionPane.showMessageDialog(this, "Invalid coordinates!");
                            return;
                        }
                        if (theta < 0 || theta > 360) {
                            JOptionPane.showMessageDialog(this, "Theta must be between 0 and 360 degrees!");
                            return;
                        }
                        if (velocity < 0) {
                            JOptionPane.showMessageDialog(this, "Velocity must be non-negative!");
                            return;
                        }
                        if (velocity < MIN_VELOCITY) {
                            JOptionPane.showMessageDialog(this, "Velocity will be adjusted to " + MIN_VELOCITY);
                            velocity = MIN_VELOCITY;
                        }
                        if (velocity > MAX_VELOCITY) {
                            JOptionPane.showMessageDialog(this, "Velocity will be adjusted to " + MAX_VELOCITY);
                            velocity = MAX_VELOCITY;
                        }

                        addParticlesWithConstantVelocityAndAngle(n, startX, endX, startY, endY, theta, velocity);
                        break;
                    case 1: // Constant Start Point and Velocity
                        // Retrieve start angle and end angle
                        n = Integer.parseInt(nField.getText());
                        x = Integer.parseInt(startXField.getText());
                        y = Integer.parseInt(startYField.getText());
                        startTheta = Double.parseDouble(startThetaField.getText());
                        endTheta = Double.parseDouble(endThetaField.getText());
                        velocity = Double.parseDouble(startVelocityField.getText());

                        // Validate inputs (e.g., check if coordinates are within the window bounds)
                        if (x < 0 || x > particlePanel.getWidth() || y < 0 || y > particlePanel.getHeight()) {
                            JOptionPane.showMessageDialog(this, "Invalid coordinates!");
                            return;
                        }
                        if (startTheta < 0 || startTheta > 360 || endTheta < 0 || endTheta > 360) {
                            JOptionPane.showMessageDialog(this, "Theta must be between 0 and 360 degrees!");
                            return;
                        }
                        if (velocity < 0) {
                            JOptionPane.showMessageDialog(this, "Velocity must be non-negative!");
                            return;
                        }
                        if (velocity < MIN_VELOCITY) {
                            JOptionPane.showMessageDialog(this, "Velocity will be adjusted to " + MIN_VELOCITY);
                            velocity = MIN_VELOCITY;
                        }
                        if (velocity > MAX_VELOCITY) {
                            JOptionPane.showMessageDialog(this, "Velocity will be adjusted to " + MAX_VELOCITY);
                            velocity = MAX_VELOCITY;
                        }

                        addParticlesWithConstantStartPointAndVelocity(n, x, y, velocity, startTheta, endTheta);
                        break;
                    case 2: // Constant Start Point and Angle
                        // Retrieve start velocity and end velocity
                        n = Integer.parseInt(nField.getText());
                        x = Integer.parseInt(startXField.getText());
                        y = Integer.parseInt(startYField.getText());
                        theta = Double.parseDouble(startThetaField.getText());
                        startVelocity = Double.parseDouble(startVelocityField.getText());
                        endVelocity = Double.parseDouble(endVelocityField.getText());

                        if (x < 0 || x > particlePanel.getWidth() || y < 0 || y > particlePanel.getHeight()) {
                            JOptionPane.showMessageDialog(this, "Invalid coordinates!");
                            return;
                        }
                        if (theta < 0 || theta > 360) {
                            JOptionPane.showMessageDialog(this, "Theta must be between 0 and 360 degrees!");
                            return;
                        }
                        if (startVelocity < 0 || endVelocity < 0) {
                            JOptionPane.showMessageDialog(this, "Velocity must be non-negative!");
                            return;
                        }
                        if (startVelocity < MIN_VELOCITY || endVelocity < MIN_VELOCITY) {
                            JOptionPane.showMessageDialog(this, "Minimum velocity should only be " + MIN_VELOCITY);
                            return;
                        }
                        if (startVelocity > MAX_VELOCITY || endVelocity > MAX_VELOCITY) {
                            JOptionPane.showMessageDialog(this, "Maximum velocity should only be " + MAX_VELOCITY);
                            return;
                        }

                        addParticlesWithConstantStartPointAndAngle(n, x, y, theta, startVelocity, endVelocity);
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "Invalid batch option selected.");
                        break;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input format!");
            }
        });

        explorerModeButton.addActionListener(e -> {
            if (isExplorerMode) {
                switchToParticleMode();
            } else {
                switchToExplorerMode();
            }
        });

        add(inputPanel, BorderLayout.EAST);
        pack();

        // FPS Label
//        fpsLabel = new JLabel("FPS: 0");
//        fpsLabel.setForeground(Color.BLACK); // Set text color to white for visibility
//        fpsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
//        particlePanel.setLayout(null); // Use null layout to manually position components
//        particlePanel.add(fpsLabel);



        // Start gamelogic thread
        new Thread(this::gameLoop).start();
    }

    private void gameLoop() {
        final double maxFramesPerSecond = 60.0;
        final long frameTime = (long) (1000 / maxFramesPerSecond);
        long lastFrameTime = System.currentTimeMillis();

        while (true) {
            long currentFrameTime = System.currentTimeMillis();
            long deltaTime = currentFrameTime - lastFrameTime; // Calculate the elapsed time since the last frame
            lastFrameTime = currentFrameTime;

            // Update each particle's position based on deltaTime
            synchronized (particleListLock) {
                for (ParticleBatch batch : particleBatchList) {
                    for (Particle particle : batch.getParticles()) { // Assuming getParticles() gives access to the particles in the batch
                        particle.update(deltaTime);
                    }
                }
            }


            if (!isExplorerMode) {
                SwingUtilities.invokeLater(particlePanel::repaint);
            } else {
                SwingUtilities.invokeLater(explorerPanel::repaint);
            }

            long endTime = System.currentTimeMillis();
            long frameDuration = endTime - currentFrameTime;
            long sleepTime = frameTime - frameDuration;

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }




    // Helper methods for batch particle addition
    private void addParticlesWithConstantVelocityAndAngle(int n, int startX, int endX, int startY, int endY, double theta, double velocity) {
        double totalDistance = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
        double increment = totalDistance / (n - 1);
        double unitVectorX = (endX - startX) / totalDistance;
        double unitVectorY = (endY - startY) / totalDistance;
        // Add particles with uniform distance
        double currentX = startX;
        double currentY = startY;


        int remainingCount = n;


        // Add particles to existing batches that are not full yet
        if (!particleBatchList.isEmpty()) {
            for (ParticleBatch batch : particleBatchList) {
                if (batch.isFull())
                    break;
                else {
                    ArrayList<Particle> pList = new ArrayList<>();
                    int numNeeded = MAX_LOAD - batch.getNumParticles(); // Number of particles to be added to this batch

                    // If the number of available space in batch is more than the remaining count to add, then add only what is remaining
                    if (numNeeded > remainingCount) {
                        numNeeded = remainingCount;
                        remainingCount = 0;
                    } else
                        remainingCount -= numNeeded; // Get the number of remaining

                    for (int i = 0; i < numNeeded; i++) {
                        pList.add(new Particle((int) Math.round(currentX), (int) Math.round(currentY), velocity, theta));
                        currentX += increment * unitVectorX;
                        currentY += increment * unitVectorY;
                    }

                    synchronized (particleListLock) {
                        // Add particles to current existing batch
                        batch.addNewParticles(pList);

                        // TEMP PRINT TODO: REMOVE AFTER TEST
                        //System.out.println("ADDED to existing batch particle num: " + pList.size());
                        // END TEMP PRINT

                        // Update particle system
                        //particlePanel.repaint();
                    }
                }
            }
        }
            // Add the remaining count to new batches if there are still
        while (remainingCount > 0) {
            ArrayList<Particle> xList = new ArrayList<>();
            ParticleBatch pb = new ParticleBatch();
            pb.start();



            for (int i = 0; i < MAX_LOAD; i++) {
                if (remainingCount > 0) {
                    xList.add(new Particle((int) Math.round(currentX), (int) Math.round(currentY), velocity, theta));
                    currentX += increment * unitVectorX;
                    currentY += increment * unitVectorY;
                    remainingCount--;

                    //System.out.println(i);
                } else
                    break;
            }
            synchronized (particleListLock) {
                // Add a new batch
                pb.clearParticles();
                pb.addNewParticles(xList);
                particleBatchList.add(pb);



                // TEMP PRINT TODO: REMOVE AFTER TEST
                //System.out.println("ADDED NEW BATCH with particle num: " + pb.getNumParticles());
                // END TEMP PRINT

                // Update particle system
                //particlePanel.repaint();
            }
        }

        // TEMP PRINT TODO: REMOVE AFTER TEST
//        System.out.println("THREAD NUM START");
//        for (ParticleBatch batch : particleBatchList)
//            System.out.println("Thread num: " + batch.getNumParticles());
//        System.out.println("THREAD NUM END");
        // END TEMP PRINT

        // Sort list
        Collections.sort(particleBatchList, new ParticleBatchComparator());

    }

    private void addParticlesWithConstantStartPointAndVelocity(int n, int x, int y, double velocity, double startTheta, double endTheta) {
        double dTheta = (endTheta - startTheta) / (double) n; // Angular increment in degrees
        double incTheta = startTheta; // Current angle in degrees

        int remainingCount = n;

        if (!particleBatchList.isEmpty()) {
            for (ParticleBatch batch : particleBatchList) {
                if (batch.isFull()) break;
                else {
                    ArrayList<Particle> pList = new ArrayList<>();
                    int numNeeded = MAX_LOAD - batch.getNumParticles();

                    if (numNeeded > remainingCount) {
                        numNeeded = remainingCount;
                        remainingCount = 0;
                    } else {
                        remainingCount -= numNeeded;
                    }

                    for (int i = 0; i < numNeeded; i++) {
                        pList.add(new Particle(x, y, velocity, incTheta)); // The Particle constructor will handle the conversion
                        incTheta += dTheta;
                    }

                    synchronized (particleListLock) {
                        batch.addNewParticles(pList);
                    }
                }
            }
        }

        while (remainingCount > 0) {
            ArrayList<Particle> xList = new ArrayList<>();
            ParticleBatch pb = new ParticleBatch();
            pb.start();

            for (int i = 0; i < MAX_LOAD; i++) {
                if (remainingCount > 0) {
                    xList.add(new Particle(x, y, velocity, incTheta)); // The Particle constructor will handle the conversion
                    incTheta += dTheta;
                    remainingCount--;
                } else break;
            }
            synchronized (particleListLock) {
                pb.clearParticles();
                pb.addNewParticles(xList);
                particleBatchList.add(pb);
            }
        }

        // Sort list if needed and update your particle system accordingly
    }


    private void addParticlesWithConstantStartPointAndAngle(int n, int x, int y, double theta, double startVelocity, double endVelocity) {
        double dVelocity = (endVelocity - startVelocity) / (double) n;
        double incVelo = startVelocity;
        int remainingCount = n;

        // Add particles to existing batches that are not full yet
        if (!particleBatchList.isEmpty()) {
            for (ParticleBatch batch : particleBatchList) {
                if (batch.isFull())
                    break;
                else {
                    ArrayList<Particle> pList = new ArrayList<>(); // Clear list before adding particles
                    int numNeeded = MAX_LOAD - batch.getNumParticles(); // Number of particles to be added to this batch

                    // If the number of available space in batch is more than the remaining count to add, then add only what is remaining
                    if (numNeeded > remainingCount) {
                        numNeeded = remainingCount;
                        remainingCount = 0;
                    } else
                        remainingCount -= numNeeded; // Get the number of remaining

                    for (int i = 0; i < numNeeded; i++) {
                        pList.add(new Particle(x, y, incVelo, theta));
                        incVelo += dVelocity;
                    }

                    synchronized (particleListLock) {
                        // Add particles to current existing batch
                        batch.addNewParticles(pList);

                        // TEMP PRINT TODO: REMOVE AFTER TEST
                        //System.out.println("ADDED to existing batch particle num: " + pList.size());
                        // END TEMP PRINT
                    }
                }
            }
        }

        // Add the remaining count to new batches if there are still
        while (remainingCount > 0) {
            ArrayList<Particle> xList = new ArrayList<>();
            ParticleBatch pb = new ParticleBatch();
            pb.start();

            for (int i = 0; i < MAX_LOAD; i++) {
                if (remainingCount > 0) {
                    xList.add(new Particle(x, y, incVelo, theta));
                    incVelo += dVelocity;
                    remainingCount--;
                } else
                    break;
            }

            synchronized (particleListLock) {
                // Add a new batch
                pb.clearParticles();
                pb.addNewParticles(xList);
                particleBatchList.add(pb);

                // TEMP PRINT TODO: REMOVE AFTER TEST
                //System.out.println("ADDED NEW BATCH with particle num: " + pb.getNumParticles());
                // END TEMP PRINT
            }

        }

        // TEMP PRINT TODO: REMOVE AFTER TEST
//        System.out.println("THREAD NUM START");
//        for (ParticleBatch batch : particleBatchList)
//            System.out.println("Thread particle num: " + batch.getNumParticles());
//        System.out.println("THREAD NUM END");
        // END TEMP PRINT

        // Sort list
        Collections.sort(particleBatchList, new ParticleBatchComparator());

        // Update particle system
        //particlePanel.repaint();

    }

    private void addParticles(int x, int y, double theta, double velocity) {
        // Create array of particles
        ArrayList<Particle> pList = new ArrayList<>();
        pList.add(new Particle(x, y, velocity, theta));

        // Add particle to an existing batch that is not full yet
        if (!particleBatchList.isEmpty() &&
                !particleBatchList.get(0).isFull()) {
            for (ParticleBatch pb : particleBatchList) {
                if (pb.isFull())
                    break;
                else
                    synchronized (particleListLock) {
                        pb.addNewParticles(pList);
                    }
            }
        }

        else {
            synchronized (particleListLock) {
                ParticleBatch pb = new ParticleBatch();
                particleBatchList.add(pb);
                pb.start();
                pb.addNewParticles(pList);
            }
        }

        // TEMP PRINT TODO: REMOVE AFTER TEST
//        System.out.println("THREAD NUM START");
//        for (ParticleBatch batch : particleBatchList)
//            System.out.println("Thread particle num: " + batch.getNumParticles());
//        System.out.println("THREAD NUM END");
        // END TEMP PRINT


        // Sort list
        Collections.sort(particleBatchList, new ParticleBatchComparator());

        // Update particle system
        //particlePanel.repaint();
    }

    private void addToInputPanel(String labelText, JTextField textField) {
        JLabel label = new JLabel(labelText);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Adding border
        inputPanel.add(label);
        inputPanel.add(textField);
    }

    private void switchToExplorerMode() {
        remove(particlePanel);
        add(explorerPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        isExplorerMode = true;

        // Disable all components in inputPanel
        toggleInputPanelComponents(false);
        // Disable the explorer mode button
        explorerModeButton.setEnabled(true);
        explorerPanel.requestFocusInWindow();
    }

    private void switchToParticleMode() {
        remove(explorerPanel);
        add(particlePanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        isExplorerMode = false;

        // Disable all components in inputPanel
        toggleInputPanelComponents(true);
    }

    private void toggleInputPanelComponents(boolean enabled) {
        for (Component component : inputPanel.getComponents()) {
            component.setEnabled(enabled);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ParticleSystemApp app = new ParticleSystemApp();
            app.setVisible(true);
        });
    }
}

class ParticleBatchComparator implements Comparator<ParticleBatch> {
    @Override
    public int compare(ParticleBatch batch1, ParticleBatch batch2) {
        return Integer.compare(batch1.getNumParticles(), batch2.getNumParticles());
    }
}

