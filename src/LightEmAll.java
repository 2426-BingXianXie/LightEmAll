import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// Represents a light game world
class LightEmAll extends World {
  // a list of columns of GamePieces,
  // i.e., represents the board in column-major order
  ArrayList<ArrayList<GamePiece>> board;
  // a list of all nodes
  ArrayList<GamePiece> nodes;
  // a list of edges of the minimum spanning tree
  ArrayList<Edge> mst;
  // the width and height of the board
  int width;
  int height;
  // the current location of the power station,
  // as well as its effective radius
  int powerRow;
  int powerCol;
  int radius;
  ArrayList<String> directions;
  Random rand;
  boolean allPowered;
  int tick;
  int score;

  LightEmAll(int width, int height) {
    this.width = width;
    this.height = height;
    this.powerRow = 0;
    this.powerCol = 0;
    this.board = new ArrayList<ArrayList<GamePiece>>();
    this.nodes = new ArrayList<GamePiece>();
    this.mst = new ArrayList<Edge>();
    this.rand = new Random();
    this.tick = 0;
    this.score = 0;
    this.allPowered = false;

    this.directions = new ArrayList<String>();
    this.directions.add("left");
    this.directions.add("right");
    this.directions.add("top");
    this.directions.add("bottom");

    // Generate the board with connections and random rotations
    this.generateBoard();
  }

  // Make the scene for this current world based on this board
  // EFFECT: draw all the game components on to the scene
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(this.width * 50, this.height * 50 + 40);

    // Draw scene for the board and bottom text part
    this.drawBoard(scene);
    this.drawBottom(scene);

    // Draw winning scene if all the nodes on this board is powered
    if(this.allPowered) {
      this.drawWin(scene);
    }

    return scene;
  }

  // Create scene for the game board
  // EFFECT: draw the game board on to the given scene
  public void drawBoard(WorldScene scene) {
    for (int i = 0; i < this.board.size(); i++) {
      for (int j = 0; j < this.board.get(i).size(); j++) {
        GamePiece current = this.board.get(i).get(j);
        boolean powerStation = (i == this.powerCol && j == this.powerRow);
        WorldImage currentImage = current.drawPowerGamePiece(50, 5, this.radius);
        scene.placeImageXY(currentImage, (50 * j) + 25, (50 * i) + 25);
      }
    }
  }

  // Create the scene for the bottom part
  // EFFECT: draw the score and time on to the given scene
  public void drawBottom(WorldScene scene) {
    WorldImage scorePart = new TextImage("Score: " + this.score, 20, Color.BLACK);
    WorldImage tickPart = new TextImage("Time : " + this.tick, 20, Color.BLACK);
    scene.placeImageXY(scorePart, this.width * 10, this.height * 50 + 20);
    scene.placeImageXY(tickPart, this.width * 40, this.height * 50 + 20);
  }

  // Create the scene for the win game
  // EFFECT: draw the win and restart text on to the given scene
  public void drawWin(WorldScene scene) {
    WorldImage winText = new TextImage("You Win", 50, Color.GREEN);
    WorldImage restartText = new TextImage("Press r to restart", 50, Color.GREEN);
    scene.placeImageXY(winText, this.width * 50 / 2, this.height * 50 / 2 - 25);
    scene.placeImageXY(restartText, this.width * 50 / 2, this.height * 50 / 2 + 25);
  }

  // Handler to handle key input
  // EFFECT: change this power column and this power row
  public void onKeyEvent(String key) {
    // Reset the game
    if (key.equals("r")) {
      this.resetGame();
      return;
    }

    // Move the power station
    if (!this.allPowered) {
      this.movePowerStation(key);
    }

    // Update the power status on this game board
    this.updatePower();
  }

  // Reset the game
  // EFFECT: change the power row, column, tick, score, allPowered field and generate a new board
  public void resetGame() {
    // Reset the power station position to top-left
    this.powerRow = 0;
    this.powerCol = 0;
    this.tick = 0;
    this.score = 0;
    this.allPowered = false;
    this.generateBoard();
  }

  // Move the power station by the given key 
  // EFFECT: change this power column and this power row
  public void movePowerStation(String key) {
    GamePiece currentPiece = this.board.get(powerCol).get(powerRow);

    if (key.equals("left") && this.powerRow > 0 && currentPiece.left) {
      if (this.getNeighbor(currentPiece, "left").right) {
        this.board.get(powerCol).get(powerRow).updatePowerStation(false);
        this.powerRow--;
        this.score++;
      }
    }
    if (key.equals("right") && this.powerRow < this.width - 1 && currentPiece.right) {
      if (this.getNeighbor(currentPiece, "right").left) {
        this.board.get(powerCol).get(powerRow).updatePowerStation(false);
        this.powerRow++;
        this.score++;
      }
    }
    if (key.equals("up") && this.powerCol > 0 && currentPiece.top) {
      if (this.getNeighbor(currentPiece, "top").bottom) {
        this.board.get(powerCol).get(powerRow).updatePowerStation(false);
        this.powerCol--;
        this.score++;
      }
    }
    if (key.equals("down") && this.powerCol < this.height - 1 && currentPiece.bottom) {
      if (this.getNeighbor(currentPiece, "bottom").top) {
        this.board.get(powerCol).get(powerRow).updatePowerStation(false);
        this.powerCol++;
        this.score++;
      }
    }
  }

  // Update the time for the game
  // EFFECT: change the value of the tick field
  public void onTick() {
    if(!this.allPowered) {
      this.tick++;
    }
  }

  // Handler to handle all mouse input
  // EFFECT: rotate the game piece on the board based on the given position
  public void onMousePressed(Posn pos) {
    // Rotate the game piece according to the given position
    if(pos.x <= this.width * 50 && pos.y <= this.height * 50 && !this.allPowered) {
      int colNum = pos.y / 50;
      int rowNum = pos.x / 50;
      this.board.get(colNum).get(rowNum).rotate();
      this.score++;
    }

    // Update the power status on this board
    this.updatePower();
  }

  // Update the power status of all game pieces
  // EFFECT: change the powered state of the game piece on this board
  public void updatePower() {
    for (ArrayList<GamePiece> column : this.board) {
      for (GamePiece piece : column) {
        piece.updatePowerState(false);
      }
    }

    GamePiece powerSource = this.board.get(this.powerCol).get(this.powerRow);

    powerSource.updatePowerStation(true);
    powerSource.updatePowerState(true);
    powerSource.updateDistanceFromPS(0);;

    ArrayList<GamePiece> queue = new ArrayList<GamePiece>();
    ArrayList<GamePiece> visited = new ArrayList<GamePiece>();

    queue.add(powerSource);
    visited.add(powerSource);

    while (!queue.isEmpty()) {
      GamePiece current = queue.remove(0);

      for (String direction : this.directions) {
        GamePiece neighbor = this.getNeighbor(current, direction);
        if (neighbor != null && !visited.contains(neighbor)) {
          String opposite = this.getOppositeDirection(direction);
          if (current.hasConnectionTo(direction) && neighbor.hasConnectionTo(opposite)) {
            neighbor.updateDistanceFromPS(current.distanceFromPS + 1);
            if (neighbor.distanceFromPS <= this.radius) {
              neighbor.updatePowerState(true);
            }
            queue.add(neighbor);
            visited.add(neighbor);
          }
        }
      }
    }

    this.allPowered = this.allPowered();
  }

  // Updated method that handles board generation
  // EFFECT: mutate the board, nodes, and MST
  public void generateBoard() {
    // Initialize board with all GamePieces
    this.initializeEmptyBoard();

    // Generate MST and set connections
    this.generateConnectionsFromMST();

    // Set radius based on the calculated diameter
    this.radius = this.calculateDiameter() / 2 + 1;

    // Randomly rotate each piece
    this.randomizeRotations();

    // Set power station and update power
    this.board.get(powerCol).get(powerRow).updatePowerStation(true);
    this.updatePower();
  }

  // Initialize board with empty GamePieces 
  // EFFECT: mutate the board, nodes, and MST to default
  public void initializeEmptyBoard() {
    this.board.clear();
    this.nodes.clear();
    this.mst.clear();

    for (int row = 0; row < this.height; row++) {
      ArrayList<GamePiece> rowList = new ArrayList<>();
      for (int col = 0; col < this.width; col++) {
        GamePiece piece = new GamePiece(row, col, false, false, false, false);
        rowList.add(piece);
        this.nodes.add(piece);
      }
      this.board.add(rowList);
    }
  }

  // Generates connections using MST
  // EFFECT: mutate this MST and set connections
  public void generateConnectionsFromMST() {
    // Generate all possible edges with random weights
    ArrayList<Edge> allEdges = this.generateAllPossibleEdges();

    // Sort edges by weight
    Collections.sort(allEdges);

    // Apply Kruskal's algorithm to this find MST
    this.mst = this.buildMinimumSpanningTree(allEdges);

    // Set the connections based on MST edges
    this.setConnectionsFromMST();
  }

  // Generates all possible edges between adjacent nodes with random weights
  public ArrayList<Edge> generateAllPossibleEdges() {
    ArrayList<Edge> edges = new ArrayList<Edge>();
    for (int row = 0; row < this.height; row++) {
      for (int col = 0; col < this.width; col++) {
        GamePiece current = this.board.get(row).get(col);
        if (col < this.width - 1) {
          GamePiece right = this.board.get(row).get(col + 1);
          edges.add(new Edge(current, right, this.rand.nextInt(1000)));
        }
        if (row < this.height - 1) {
          GamePiece bottom = this.board.get(row + 1).get(col);
          edges.add(new Edge(current, bottom, this.rand.nextInt(1000)));
        }
      }
    }
    return edges;
  }

  // Applies Kruskal's algorithm to find the minimum spanning tree
  public ArrayList<Edge> buildMinimumSpanningTree(ArrayList<Edge> edges) {
    ArrayList<Edge> result = new ArrayList<Edge>();
    UnionFind uf = new UnionFind(this.nodes);

    int i = 0;
    while (i < edges.size() && result.size() < this.nodes.size() - 1) {
      Edge edge = edges.get(i);
      GamePiece rootFrom = uf.find(edge.from);
      GamePiece rootTo = uf.find(edge.to);
      if (rootFrom != rootTo) {
        result.add(edge);
        uf.union(rootFrom, rootTo);
      }
      i++;
    }
    return result;
  }

  // Sets connections between pieces based on MST edges
  // EFFECT: mutate the game pieces wires boolean
  public void setConnectionsFromMST() {
    for (Edge edge : this.mst) {
      int rowFrom = edge.from.row;
      int colFrom = edge.from.col;
      int rowTo = edge.to.row;
      int colTo = edge.to.col;

      if (rowFrom == rowTo) {
        if (colFrom < colTo) {
          edge.from.right = true;
          edge.to.left = true;
        }
        else {
          edge.from.left = true;
          edge.to.right = true;
        }
      }
      else {
        if (rowFrom < rowTo) {
          edge.from.bottom = true;
          edge.to.top = true;
        }
        else {
          edge.from.top = true;
          edge.to.bottom = true;
        }
      }
    }
  }

  // Randomly rotate each node on the board
  // EFFECT: mutate the game piece's wire boolean
  public void randomizeRotations() {
    for (ArrayList<GamePiece> row : board) {
      for (GamePiece piece : row) {
        int rotations = this.rand.nextInt(4);
        for (int i = 0; i < rotations; i++) {
          piece.rotate();
        }
      }
    }
  }

  // Check whether all the nodes on the board is powered
  public boolean allPowered() {
    for (int i = 0; i < this.nodes.size(); i++) {
      if (!this.nodes.get(i).powered) {
        return false;
      }
    }
    return true;
  }

  // Get the neighbor game piece of the given game piece of the given direction
  public GamePiece getNeighbor(GamePiece origin, String direction) {
    if (direction.equals("left") && origin.hasConnectionTo(direction)
            && origin.col > 0) {
      return this.board.get(origin.row).get(origin.col - 1);
    }
    if (direction.equals("right") && origin.hasConnectionTo(direction)
            && origin.col < this.width - 1) {
      return this.board.get(origin.row).get(origin.col + 1);
    }
    if (direction.equals("top") && origin.hasConnectionTo(direction)
            && origin.row > 0) {
      return this.board.get(origin.row - 1).get(origin.col);
    }
    if (direction.equals("bottom") && origin.hasConnectionTo(direction)
            && origin.row < this.height - 1) {
      return this.board.get(origin.row + 1).get(origin.col);
    }
    return null;
  }

  // Helper to get the opposite direction string of the given direction
  public String getOppositeDirection(String direction) {
    if (direction.equals("left")) {
      return "right";
    }
    if (direction.equals("right")) {
      return "left";
    }
    if (direction.equals("top")) {
      return "bottom";
    }
    if (direction.equals("bottom")) {
      return "top";
    }
    return null;
  }

  // Calculate the diameter of this board
  public int calculateDiameter() {
    GamePiece start = this.board.get(this.powerCol).get(this.powerRow);
    GamePiece firstBFSend = this.findFurthestNode(start);
    GamePiece secondBFSend = this.findFurthestNode(firstBFSend);

    // Find the max distance and calculate radius
    int diameter = 0;
    for (GamePiece gp : this.nodes) {
      if (gp.distanceFromPS > diameter) {
        diameter = gp.distanceFromPS;
      }
    }

    return diameter;
  }

  // Breath first search to find the farthest node
  // EFFECT: mutate the distance from power station for all game piece in this board
  public GamePiece findFurthestNode(GamePiece start) {
    for (GamePiece gp : this.nodes) {
      gp.updateDistanceFromPS(-1);;
    }

    ArrayList<GamePiece> queue = new ArrayList<GamePiece>();
    start.updateDistanceFromPS(0);
    queue.add(start);
    GamePiece furthest = start;

    while (!queue.isEmpty()) {
      GamePiece current = queue.remove(0);

      for (String direction : this.directions) {
        GamePiece neighbor = this.getNeighbor(current, direction);
        if (neighbor != null && neighbor.distanceFromPS == -1) {
          String opposite = this.getOppositeDirection(direction);
          if (current.hasConnectionTo(direction) && neighbor.hasConnectionTo(opposite)) {
            neighbor.updateDistanceFromPS(current.distanceFromPS + 1);
            queue.add(neighbor);

            if (neighbor.distanceFromPS > furthest.distanceFromPS) {
              furthest = neighbor;
            }
          }
        }
      }
    }

    return furthest;
  }
}
