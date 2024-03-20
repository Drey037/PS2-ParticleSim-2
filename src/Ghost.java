import java.awt.*;

public class Ghost {
    private int x, y;
    private Image texture_left;
    private Image texture_right;

    private int CHAR_WIDTH = 35;

    private int CHAR_HEIGHT = 39;

    private Boolean isLeft;

    public Ghost(int x, int y, Image texture_left, Image texture_right) {
        this.x = x;
        this.y = y;
        this.texture_left = texture_left;
        this.texture_right = texture_right;
        this.isLeft = true;
    }

    public void move(int dx, int dy) {
        int newX = x + dx;
        int newY = y + dy;

        System.out.println("NEW X and NEW Y: " + newX + ", " + newY);
        if (newX >= 0 && newX <= 1280 - CHAR_WIDTH &&
                newY >= 0 && newY <= 720 - CHAR_HEIGHT) {
            x = newX;
            y = newY;

            System.out.println("Modified X and Y: " + x + ", " + y);
        }
        else {
            if (newX >= 0)
                System.out.println("X must be more than 0");
            if (newX <= 1280 - CHAR_WIDTH)
                System.out.println("X must be less than 1280");
            if (newY >= 0)
                System.out.println("Y must be more than 0");
            if (newY <= 720 - CHAR_HEIGHT)
                System.out.println("Y must be less than 720");
        }
    }

    public void draw(Graphics g) {
        // 39x35 small
        if (isLeft)
            g.drawImage(texture_left, x, translateY(y), CHAR_WIDTH, CHAR_HEIGHT, null);
        else
            g.drawImage(texture_right, x, translateY(y), CHAR_WIDTH, CHAR_HEIGHT, null);
    }

    private int translateY(int y) {
        return (720 - 35) - y; //minus the height
    }

    public void drawMap(Graphics g) {
        // 300 x 270 original
        if (isLeft)
            g.drawImage(texture_left, 640 - (156 / 2), 360 - (140 / 2), 156, 140, null);
        else
            g.drawImage(texture_right, 640 - (156 / 2), 360 - (140 / 2), 156, 140, null);
    }

    public void turnChar(Boolean isLeft) {
        this.isLeft = isLeft;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
