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
        // 300 x 270 original
        // 156 Width, 140 height
        if (isLeft)
            g.drawImage(texture_left, 640 - (CHAR_MAP_WIDTH / 2), 360 - (CHAR_MAP_HEIGHT / 2), CHAR_MAP_WIDTH, CHAR_MAP_HEIGHT, null);
        else
            g.drawImage(texture_right, 640 - (CHAR_MAP_WIDTH / 2), 360 - (CHAR_MAP_HEIGHT / 2), CHAR_MAP_WIDTH, CHAR_MAP_HEIGHT, null);

        // For 19 rows and 33 columns - 37 height 39 width
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
