import java.awt.*;
import java.util.List;
import java.util.Random;

public class Car {
    List<Integer> path;
    private int currentIndex = 0;
    private Color color;

    Car(List<Integer> path) {
        this.path = path;
        this.color = generateRandomColor();
    }

    boolean hasArrived() {
        return currentIndex >= path.size() - 1;
    }
    public void move() {
        if (currentIndex < path.size() - 1) {
            currentIndex++;
        }
    }

    public int getCurrentPosition() {
        return path.get(currentIndex);
    }

    public int getCurrentIndex()
    {
        return currentIndex;
    }

    public Color getColor() {
        return color;
    }

    private Color generateRandomColor() {
        Random rand = new Random();
        return new Color(rand.nextInt(156), rand.nextInt(156), rand.nextInt(156));
    }
}




