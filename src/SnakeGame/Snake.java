package SnakeGame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.HashMap;

public class Snake {
    ArrayList<GridObject> snakeObjects;
    Direction direction;
    GridObject[][] grid;
    HashMap<String, Image> gfx;

    public int growQueue = 0;

    public Snake(GridObject[][] grid, HashMap<String, Image> gfx) {
        // Create initial snake
        snakeObjects = new ArrayList<>();
        direction = Direction.up;
        this.grid = grid;
        this.gfx = gfx;

        GridObject head = new GridObject();
        GridObject body = new GridObject();
        GridObject tail = new GridObject();

        head.pos.x = 3;
        head.pos.y = 10;
        head.solid = true;
        body.pos.x = 3;
        body.pos.y = 11;
        body.solid = true;
        tail.pos.x = 3;
        tail.pos.y = 12;
        tail.solid = true;

        head.gfxName = "head-up";
        tail.gfxName = "tail-up";

        snakeObjects.add(head);
        snakeObjects.add(body);
        snakeObjects.add(tail);
    }

    public ArrayList<GridObject> getSnakeGOs() {
        return snakeObjects;
    }

    public void draw(GraphicsContext gc) {
        for(GridObject s : snakeObjects) {
            if(s.shouldDraw) {
                gc.drawImage(gfx.get(s.gfxName), s.pos.x * SnakeGame.gridSize, s.pos.y * SnakeGame.gridSize);
            }
        }
    }

    public void MoveAndGrow(Vector2 newHeadPos) {
        Vector2 nextPos = newHeadPos;
        for(int i = 0; i < snakeObjects.size(); i++) {

            // Move each object along the snake
            Vector2 lastPos = snakeObjects.get(i).pos;
            snakeObjects.get(i).pos = nextPos;
            nextPos = lastPos;

            // If we are at the last object of the snake and we recently picked up a fruit, grow by one object
            if(i == snakeObjects.size() - 1 && growQueue > 0) {
                GridObject newSnakeGO = new GridObject();
                newSnakeGO.pos = lastPos;
                newSnakeGO.solid = true;
                snakeObjects.add(newSnakeGO);
                growQueue--;
            }
        }
    }

    public void UpdateGraphics(boolean doBigHead) {
        for(int i = 0; i < snakeObjects.size(); i++) {
            // Head
            if(i == 0) {
                // Determine head graphics from current direction
                if(!doBigHead) {
                    switch (direction) {
                        case up -> snakeObjects.get(0).gfxName    = "head-up";
                        case down -> snakeObjects.get(0).gfxName  = "head-down";
                        case left -> snakeObjects.get(0).gfxName  = "head-left";
                        case right -> snakeObjects.get(0).gfxName = "head-right";
                    }
                }
                else {
                    switch (direction) {
                        case up -> {
                            snakeObjects.get(0).gfxName = "head-up-big";
                            snakeObjects.get(0).offset = new Vector2(-22, -44);
                        }
                        case down -> {
                            snakeObjects.get(0).gfxName = "head-down-big";
                            snakeObjects.get(0).offset = new Vector2(-22, 0);
                        }
                        case left -> {
                            snakeObjects.get(0).gfxName = "head-left-big";
                            snakeObjects.get(0).offset = new Vector2(-44, -22);
                        }
                        case right -> {
                            snakeObjects.get(0).gfxName = "head-right-big";
                            snakeObjects.get(0).offset = new Vector2(0, -22);
                        }
                    }
                }
                continue;
            }

            // Tail
            if(i == snakeObjects.size() - 1) {
                // Determine tail graphics from second-last body part's position
                Vector2 deltaBodyTail = snakeObjects.get(snakeObjects.size()-2).pos.sub(snakeObjects.get(snakeObjects.size()-1).pos);
                if      (deltaBodyTail.y > 0)  { snakeObjects.get(i).gfxName = "tail-down"; }
                else if (deltaBodyTail.y < 0)  { snakeObjects.get(i).gfxName = "tail-up"; }
                else if (deltaBodyTail.x > 0)  { snakeObjects.get(i).gfxName = "tail-right"; }
                else  /*(deltaBodyTail.x < 0)*/{ snakeObjects.get(i).gfxName = "tail-left"; }
                continue;
            }

            // Body
            // Get previous, current, and next body parts location
            GridObject prev = snakeObjects.get(i-1);
            GridObject curr = snakeObjects.get(i);
            GridObject next = snakeObjects.get(i+1);

            // Calculate relative positions, mabye delta is the wrong word here...
            Vector2 deltaCurrToPrev = curr.pos.sub(prev.pos);
            Vector2 deltaCurrToNext = curr.pos.sub(next.pos);

            // Truth table of relative body part position
            Direction prevDir;
            Direction nextDir;
            if      (deltaCurrToPrev.y > 0)  { prevDir = Direction.up; }
            else if (deltaCurrToPrev.y < 0)  { prevDir = Direction.down; }
            else if (deltaCurrToPrev.x > 0)  { prevDir = Direction.left; }
            else  /*(deltaCurrToPrev.x < 0)*/{ prevDir = Direction.right; }

            if      (deltaCurrToNext.y > 0)  { nextDir = Direction.up; }
            else if (deltaCurrToNext.y < 0)  { nextDir = Direction.down; }
            else if (deltaCurrToNext.x > 0)  { nextDir = Direction.left; }
            else  /*(deltaCurrToNext.x < 0)*/{ nextDir = Direction.right; }

            // Conditional truth table of what graphics to put on the currently looked at body part of the snake
            if(prevDir == Direction.up && nextDir == Direction.down || prevDir == Direction.down && nextDir == Direction.up)
                curr.gfxName = "body-v";
            else if(prevDir == Direction.left && nextDir == Direction.right || prevDir == Direction.right && nextDir == Direction.left)
                curr.gfxName = "body-h";
            else if(prevDir == Direction.up && nextDir == Direction.right || prevDir == Direction.right && nextDir == Direction.up)
                curr.gfxName = "body-ne";
            else if(prevDir == Direction.up && nextDir == Direction.left || prevDir == Direction.left && nextDir == Direction.up)
                curr.gfxName = "body-nw";
            else if(prevDir == Direction.down && nextDir == Direction.right || prevDir == Direction.right && nextDir == Direction.down)
                curr.gfxName = "body-se";
            else if(prevDir == Direction.down && nextDir == Direction.left || prevDir == Direction.left && nextDir == Direction.down)
                curr.gfxName = "body-sw";
        }
    }
}
