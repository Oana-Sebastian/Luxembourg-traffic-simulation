import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.util.List;
import javax.swing.Timer;

public class MapViewer extends JPanel {
    private Map<Integer, NodeInfo> nodes;
    private Map<Integer, List<Arc>> graph;

    private Timer animationTimer;
    private final int WIDTH = 1920;
    private final int KHEIGHT = 1080;
    private List<Car> activeCars = new ArrayList<>();
    private Map<Integer, Integer> congestionMap = new HashMap<>();
    private static final int CONGESTION_THRESHOLD = 3;


    private double scale = 1.0;
    private double offsetX = 0, offsetY=0;
    private Point lastDragPoint = null;
    private double minLon, maxLon, minLat, maxLat;


    public MapViewer(Map<Integer, NodeInfo> nodes,Map<Integer, List<Arc>> graph) {



        this.nodes = nodes;
        this.graph = graph;

        setSize(1920, 1080);

        calculateBounds();

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                lastDragPoint = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastDragPoint != null) {
                    offsetX += e.getX() - lastDragPoint.x;
                    offsetY += e.getY() - lastDragPoint.y;
                    lastDragPoint = e.getPoint();
                    repaint();
                }
            }
        });
        addMouseWheelListener(e -> {
            double delta = e.getPreciseWheelRotation();
            double zoomFactor = (delta > 0) ? 0.9 : 1.1;
            scale *= zoomFactor;
            repaint();
        });

    }

    private void updateCongestion() {
        congestionMap.clear();
        for (Car car : activeCars) {
            int pos = car.getCurrentPosition();
            congestionMap.put(pos, congestionMap.getOrDefault(pos, 0) + 1);
        }
    }

    private void calculateBounds() {
        minLon = Double.MAX_VALUE;
        maxLon = Double.MIN_VALUE;
        minLat = Double.MAX_VALUE;
        maxLat = Double.MIN_VALUE;

        for (NodeInfo node : nodes.values()) {
            if (node.lon < minLon) minLon = node.lon;
            if (node.lon > maxLon) maxLon = node.lon;
            if (node.lat < minLat) minLat = node.lat;
            if (node.lat > maxLat) maxLat = node.lat;
        }
    }


    private int scaleX(int lon) {
        return (int) ((lon - minLon) * WIDTH  / (maxLon - minLon));
    }

    private int scaleY(int lat) {
        return (int) (KHEIGHT - (lat - minLat) * KHEIGHT / (maxLat - minLat));
    }


    private Map<Integer, Integer> dijkstraAvoidingCongestion(Map<Integer, List<Arc>> graph, int start) {
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{start, 0});
        Map<Integer, Integer> dist = new HashMap<>();
        Map<Integer, Integer> prev = new HashMap<>();
        dist.put(start, 0);




        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int node = current[0], cost = current[1];

            if (cost > dist.getOrDefault(node, Integer.MAX_VALUE))
                continue;

            for (Arc arc : graph.getOrDefault(node, new ArrayList<>())) {

                int carsAtDestination = congestionMap.getOrDefault(arc.to, 0);


                int congestionPenalty = 0;
                if (carsAtDestination >= CONGESTION_THRESHOLD) {
                    congestionPenalty = carsAtDestination * 5;
                }

                int newCost = cost + arc.length + congestionPenalty;

                if (newCost < dist.getOrDefault(arc.to, Integer.MAX_VALUE)) {
                    dist.put(arc.to, newCost);
                    prev.put(arc.to, node);
                    pq.add(new int[]{arc.to, newCost});
                }
            }
        }
        return prev;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform transform = new AffineTransform();
        transform.translate(offsetX, offsetY);
        transform.scale(scale, scale);
        g2.setTransform(transform);


        g2.setColor(Color.BLACK);
        for (List<Arc> arcs : graph.values()) {
            for (Arc arc : arcs) {
                NodeInfo from = nodes.get(arc.from);
                NodeInfo to = nodes.get(arc.to);
                if (from != null && to != null) {
                    int x1 = scaleX((int) from.lon);
                    int y1 = scaleY((int) from.lat);
                    int x2 = scaleX((int) to.lon);
                    int y2 = scaleY((int) to.lat);
                    g2.drawLine(x1, y1, x2, y2);
                }
            }
        }

        g2.setStroke(new BasicStroke(2.0f));
        for (Car car : activeCars) {
            if (car.getCurrentIndex() < car.path.size()) {
                NodeInfo position = nodes.get(car.path.get(car.getCurrentIndex()));
                int x = scaleX((int) position.lon);
                int y = scaleY((int) position.lat);

                g2.setColor(car.getColor());
                g2.fillOval(x - 4, y - 4, 8, 8);
            }
        }

    }

    public void selectRandomPath(int carNumber) {
        for(int i=0;i<carNumber;i++) {
            Random rand = new Random();
            int start = rand.nextInt(nodes.size());
            int end = start;
            while (end == start) {
                end = rand.nextInt(nodes.size());
            }

            updateCongestion();
            Map<Integer, Integer> prev = dijkstraAvoidingCongestion(graph, start);

            List<Integer> newPath = new ArrayList<>();
            int path = end;
            while (path != start && prev.containsKey(path)) {
                newPath.add(path);
                path = prev.get(path);
            }
            newPath.add(start);
            Collections.reverse(newPath);

            activeCars.add(new Car(newPath));
        }

        startAnimation();
    }



    private void startAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }

        animationTimer = new Timer(100, e -> {
            updateCarPositions();
            repaint();
        });
        animationTimer.start();
    }

    private void updateCarPositions() {
        Iterator<Car> iterator = activeCars.iterator();
        while (iterator.hasNext()) {
            Car car = iterator.next();
            car.move();
            if (car.hasArrived()) {
                iterator.remove();
            }
        }
    }


}
