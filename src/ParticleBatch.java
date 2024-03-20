import java.util.ArrayList;

public class ParticleBatch extends Thread {
    // Particles assigned to this thread
    private ArrayList<Particle> particles;

    private final Object particleListLock = new Object();

    private int numParticles;

    private final int MAX_LOAD = 10;

    public ParticleBatch() {
        this.particles = new ArrayList<>();
        this.numParticles = particles.size();
    }

    public ArrayList<Particle> getParticles() {
        return particles;
    }

    public int getNumParticles() {
        return particles.size();
    }

    public boolean isFull() {
        return particles.size() == MAX_LOAD;
    }

    public void addNewParticles(ArrayList<Particle> newParticles) {
        particles.addAll(newParticles);
        numParticles += newParticles.size();
    }

    public void clearParticles() {
        particles = new ArrayList<>();
    }

    @Override
    public void run() {
        while(true) {
            synchronized(particleListLock) {
                for (Particle particle : particles)
                    particle.update();

                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                }
            }
        }
    }

}
