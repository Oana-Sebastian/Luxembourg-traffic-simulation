import javax.swing.*;
import java.awt.*;

public class Graph
{
    private static void initUI() throws Exception {

        JFrame f = new JFrame("Luxembourg Map");

        f.setSize(1920,1080);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout());


        XMLParser parser = new XMLParser();
        parser.parseXML("LuxembourgMap.xml");
        MapViewer  mapPanel=new MapViewer(parser.getNodes(), parser.getGraph());
        JPanel controlPanel = new JPanel();
        JButton randomPathButton = new JButton("Add Car(s)");
        randomPathButton.addActionListener(e -> selectNumberOfCars(mapPanel));
        controlPanel.add(randomPathButton);


        f.add(mapPanel, BorderLayout.CENTER);
        f.add(controlPanel, BorderLayout.NORTH);

        f.setVisible(true);
    }

private static void selectNumberOfCars(MapViewer mapPanel)
{
    String carNumber = JOptionPane.showInputDialog("Enter number of cars:");
    if(carNumber!=null&& !carNumber.isEmpty()) {
        if(!carNumber.matches(".[a-zA-Z].*")) {
            int cars = Integer.parseInt(carNumber);
            mapPanel.selectRandomPath(cars);
        }
    }
}

    public static void main(String[] args)
    {

        SwingUtilities.invokeLater(new Runnable() //new Thread()
        {
            public void run()
            {
                try {
                    initUI();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}