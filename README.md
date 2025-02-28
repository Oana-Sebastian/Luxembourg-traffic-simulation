# Luxembourg-traffic-simulation

This project is an interactive urban traffic simulation developed in Java using Swing for the graphical user interface and routing algorithms (Dijkstra) to calculate the shortest path on a map represented by nodes and arcs.

# Description

The project simulates a network of roads in a city, where:

 * Map Display: The map is built from a set of nodes arranged in a square grid and arcs connecting these nodes, representing streets.
 * Vehicles: Cars are generated randomly and animated to move along the calculated routes. Each car is assigned a randomly generated color.
 * Route Calculation: The shortest path between two nodes is calculated using Dijkstra’s algorithm. The system can also apply congestion penalties to avoid highly congested areas, simulating traffic avoidance.
 * Graphical User Interface: The GUI features a dedicated panel for displaying the map and a control panel with buttons. Users can add cars via an input dialog, which allows them to specify the number of vehicles to generate.

# Features
 * Interactive Map: A custom JPanel is used to render the map, which supports translation and scaling.
 * Shortest Path Calculation: Dijkstra’s algorithm (modified to account for traffic congestion) is used to compute the optimal route between two nodes.
 * Vehicle Animation: Vehicles are drawn as colored circles that progress along the calculated path using a javax.swing.Timer for periodic updates.
 * Input Dialog: The "Add Car(s)" button opens an input dialog where users can specify the number of vehicles to add.
 * Traffic Congestion Avoidance: The routing algorithm detects congested areas and adjusts paths to simulate smoother traffic flow.
