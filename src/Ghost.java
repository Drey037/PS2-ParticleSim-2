import java.awt.*;

public class Ghost {
    private int x, y;
    private Image texture_left;
    private Image texture_right;

    private int CHAR_WIDTH = 9;

    private int CHAR_HEIGHT = 9;

    private final int CHAR_MAP_WIDTH = 39;
    private final int CHAR_MAP_HEIGHT = 37;

    private Boolean isLeft;

    private final int WIDTH = 1280;
    private final int HEIGHT = 720;

    private final int SCREEN_WIDTH = 33;
    private final int SCREEN_HEIGHT = 19;
    private final int screenX;
    private final int screenY;

    public Ghost(int x, int y, Image texture_left, Image texture_right) {
        this.x = x;
        this.y = y;
        this.texture_left = texture_left;
        this.texture_right = texture_right;
        this.isLeft = true;

        this.screenX = WIDTH / 2 - (CHAR_MAP_WIDTH / 2);
        this.screenY = HEIGHT / 2 - (CHAR_MAP_HEIGHT / 2);
    }

    public void move(int dx, int dy) {
        int newX = x + dx;
        int newY = y + dy;

        if (newX >= 0 && newX <= 1280 &&
                newY >= 0 && newY <= 720) {
            x = newX;
            y = newY;
            System.out.println("X and Y: " + newX + ", " + newY);
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
        return (720 - CHAR_HEIGHT) - y; //minus the height
    }

    public void drawMap(Graphics g) {
        if (isLeft) {
            g.drawImage(texture_left, screenX, screenY, CHAR_MAP_WIDTH, CHAR_MAP_HEIGHT, null);
        }
        else {
            g.drawImage(texture_right, screenX, screenY, CHAR_MAP_WIDTH, CHAR_MAP_HEIGHT, null);
        }

    }

    public int getScreenX() {
        return screenX;
    }

    public int getScreenY() {
        return screenY;
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
