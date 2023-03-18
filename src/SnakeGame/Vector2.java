package SnakeGame;

public class Vector2 {
    public int x;
    public int y;

    public Vector2(Vector2 vec) {
        this.x = vec.x;
        this.y = vec.y;
    }

    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 add(Vector2 arg) {
        return new Vector2(this.x + arg.x, this.y + arg.y);
    }

    public Vector2 sub(Vector2 arg) {
        return new Vector2(this.x - arg.x, this.y - arg.y);
    }
}
