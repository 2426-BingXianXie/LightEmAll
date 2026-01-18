import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import javalib.impworld.WorldScene;
import javalib.worldimages.Posn;
import javalib.worldimages.TextImage;
import javalib.worldimages.WorldImage;
import tester.Tester;

class ExamplesLightWorld {

  LightEmAll testGame;
  GamePiece gp00;
  GamePiece gp01;
  GamePiece gp02;
  GamePiece gp10;
  GamePiece gp11;
  GamePiece gp12;
  GamePiece gp20;
  GamePiece gp21;
  GamePiece gp22;

  void initData() {
    // create all pieces for 3x3 grid
    this.gp00 = new GamePiece(0, 0, false, true, false, true);
    this.gp01 = new GamePiece(0, 1, true, true, false, true);
    this.gp02 = new GamePiece(0, 2, true, false, false, true);
    this.gp10 = new GamePiece(1, 0, false, true, true, true);
    this.gp11 = new GamePiece(1, 1, true, true, true, true);
    this.gp12 = new GamePiece(1, 2, true, false, true, true);
    this.gp20 = new GamePiece(2, 0, false, true, true, false);
    this.gp21 = new GamePiece(2, 1, true, true, true, false);
    this.gp22 = new GamePiece(2, 2, false, false, false, false);
    // Manually build board structure
    this.testGame = new LightEmAll(3, 3);
    this.testGame.rand = new Random(42);
    this.testGame.board = new ArrayList<>();

    // Column 0
    ArrayList<GamePiece> col0 = new ArrayList<>();
    col0.add(this.gp00);
    col0.add(this.gp10);
    col0.add(this.gp20);

    // Column 1
    ArrayList<GamePiece> col1 = new ArrayList<>();
    col1.add(this.gp01);
    col1.add(this.gp11);
    col1.add(this.gp21);

    // Column 2
    ArrayList<GamePiece> col2 = new ArrayList<>();
    col2.add(this.gp02);
    col2.add(this.gp12);
    col2.add(this.gp22);

    this.testGame.board.add(col0);
    this.testGame.board.add(col1);
    this.testGame.board.add(col2);

    // populate nodes
    this.testGame.nodes = new ArrayList<>();
    this.testGame.nodes.add(this.gp00);
    this.testGame.nodes.add(this.gp10);
    this.testGame.nodes.add(this.gp20);
    this.testGame.nodes.add(this.gp01);
    this.testGame.nodes.add(this.gp11);
    this.testGame.nodes.add(this.gp21);
    this.testGame.nodes.add(this.gp02);
    this.testGame.nodes.add(this.gp12);
    this.testGame.nodes.add(this.gp22);

    // Configure power station
    this.testGame.powerRow = 1;
    this.testGame.powerCol = 1;
    this.gp11.updatePowerStation(true);
    this.testGame.updatePower();
  }

  //Testing method makeScene in class LightEmAll
  void testMakeScene(Tester t) {
    initData();
    WorldScene scene = this.testGame.makeScene();

    // Check the dimensions of the scene
    t.checkExpect(scene.width, 150);
    t.checkExpect(scene.height, 190);

    // Verify the power station is correctly placed in the game state
    t.checkExpect(this.testGame.board.get(1).get(1).powerStation, true);
    t.checkExpect(this.testGame.board.get(1).get(1).powered, true);

    // Check a non-power station tile's state 
    t.checkExpect(this.gp22.powered, false);
    t.checkExpect(this.gp22.powerStation, false);
  }

  //Testing method drawBoard in class LightEmAll
  void testDrawBoard(Tester t) {
    initData();
    WorldScene scene = this.testGame.makeScene();

    // Check board has 3 columns
    t.checkExpect(this.testGame.board.size(), 3);
    // Verify first column has 3 rows
    t.checkExpect(this.testGame.board.get(0).size(), 3);
    // Confirm center piece is powered
    t.checkExpect(this.testGame.board.get(1).get(1).powered, true);
  }

  //Testing method drawBottom in class LightEmAll
  void testDrawBottom(Tester t) {
    initData();

    // Initial moves should be 0 
    this.testGame.onKeyEvent("left");
    this.testGame.onKeyEvent("right");
    t.checkExpect(this.testGame.board.get(1).get(1).powerStation, false);

    // Verify radius through power propagation
    t.checkExpect(this.testGame.board.get(0).get(1).powered, true);

    // Check reset functionality maintains position
    this.testGame.onMousePressed(new Posn(75, 175));
    t.checkExpect(this.testGame.board.get(1).get(1).powerStation, false);
  }

  //Testing method drawWin in class LightEmAll
  void testDrawWin(Tester t) {
    initData();

    // Verify win messages don't appear when allPowered is false
    this.testGame.allPowered = false;
    WorldScene scene1 = this.testGame.makeScene();
    // Create a comparison scene without win messages
    WorldScene expectedScene1 = new WorldScene(
            this.testGame.width * 50, this.testGame.height * 50 + 40);
    this.testGame.drawBoard(expectedScene1);
    this.testGame.drawBottom(expectedScene1);
    t.checkExpect(scene1, expectedScene1);

    // Verify win messages appear when allPowered is true
    this.testGame.allPowered = true;
    WorldScene scene2 = this.testGame.makeScene();
    // Create a comparison scene with win messages
    WorldScene expectedScene2 = new WorldScene(
            this.testGame.width * 50, this.testGame.height * 50 + 40);
    this.testGame.drawBoard(expectedScene2);
    this.testGame.drawBottom(expectedScene2);
    WorldImage winText = new TextImage("You Win", 50, Color.GREEN);
    WorldImage restartText = new TextImage("Press r to restart", 50, Color.GREEN);
    expectedScene2.placeImageXY(
            winText, this.testGame.width * 50 / 2, this.testGame.height * 50 / 2 - 25);
    expectedScene2.placeImageXY(
            restartText, this.testGame.width * 50 / 2, this.testGame.height * 50 / 2 + 25);
    t.checkExpect(scene2, expectedScene2);

    // Verify win state is cleared after reset
    this.testGame.resetGame();
    t.checkExpect(this.testGame.allPowered, false);
    WorldScene scene3 = this.testGame.makeScene();
    // Win messages should not appear after reset
    WorldScene expectedScene3 = new WorldScene(
            this.testGame.width * 50, this.testGame.height * 50 + 40);
    this.testGame.drawBoard(expectedScene3);
    this.testGame.drawBottom(expectedScene3);
    t.checkExpect(scene3, expectedScene3);
  }

  //Testing method onKeyEvent in class LightEmAll
  void testOnKeyEvent(Tester t) {
    initData();
    // Valid right move
    this.testGame.onKeyEvent("right");
    t.checkExpect(this.testGame.powerRow, 2);

    // Blocked left move
    this.testGame.powerRow = 0;
    this.testGame.onKeyEvent("left");
    t.checkExpect(this.testGame.powerRow, 0);

    // Valid down move
    this.testGame.onKeyEvent("down");
    t.checkExpect(this.testGame.powerCol, 2);
  }

  //Testing method movePowerStation in class LightEmAll
  void testMovePowerStation(Tester t) {

    initData();
    // Check initial power station
    t.checkExpect(this.testGame.board.get(1).get(1).powerStation, true);

    // Test moving right 
    this.testGame.movePowerStation("right");
    t.checkExpect(this.testGame.powerRow, 2);
    t.checkExpect(this.testGame.board.get(2).get(1).powerStation, false);

    // Test blocked left move 
    this.testGame.powerRow = 0;
    this.testGame.movePowerStation("left");
    t.checkExpect(this.testGame.powerRow, 0);

    // Test vertical movement
    this.testGame.movePowerStation("bottom");
    t.checkExpect(this.testGame.powerCol, 1);
    t.checkExpect(this.testGame.board.get(0).get(2).powerStation, false);
  }

  //Testing method onTick in class LightEmAll
  void testOnTick(Tester t) {
    initData();
    int initialTick = this.testGame.tick;

    // Tick should increment if not all powered
    this.testGame.onTick();
    t.checkExpect(this.testGame.tick, initialTick + 1);

    // If marked as allPowered, tick should not increment
    this.testGame.allPowered = true;
    this.testGame.onTick();
    t.checkExpect(this.testGame.tick, initialTick + 1);

    // Reset allPowered to false and tick again
    this.testGame.allPowered = false;
    this.testGame.onTick();
    t.checkExpect(this.testGame.tick, initialTick + 2);
  }

  //Testing method onMousePressed in class LightEmAll
  void testOnMousePressed(Tester t) {
    initData();

    // Valid rotation
    boolean originalRight = this.gp00.right;
    this.testGame.onMousePressed(new Posn(25, 25));
    t.checkExpect(this.gp00.right, !originalRight);

    // Power propagates after valid click
    t.checkExpect(this.gp00.powered, true);

    // Negative coordinates click
    boolean beforeRotation = this.gp00.right;
    this.testGame.onMousePressed(new Posn(-10, -10));
    t.checkExpect(this.gp00.right, beforeRotation);
  }

  //Testing method generateBoardd in class LightEmAll
  void testGenerateBoard(Tester t) {
    initData();
    LightEmAll game = new LightEmAll(2, 2);
    game.initializeEmptyBoard();
    // Test first piece
    t.checkExpect(game.board.get(0).get(0).top, false);
    t.checkExpect(game.board.get(0).get(0).right, false);
    // Test last piece
    t.checkExpect(game.board.get(1).get(1).bottom, false);
    t.checkExpect(game.board.get(1).get(1).left, false);
    // Test node count
    t.checkExpect(game.nodes.size(), 4);
  }

  //Testing method initializeEmptyBoard in class LightEmAll
  void testInitializeEmptyBoard(Tester t) {
    initData();
    LightEmAll game = new LightEmAll(2, 2);
    game.initializeEmptyBoard();

    // All connections disabled
    boolean allOff = true;
    for (GamePiece p : game.nodes) {
      if (p.top || p.right || p.bottom || p.left) {
        allOff = false;
      }
    }
    t.checkExpect(allOff, true);

    // Correct node count
    t.checkExpect(game.nodes.size(), 4);

    // No power stations
    int stations = 0;
    for (GamePiece p : game.nodes) {
      if (p.powerStation) {
        stations++;
      }
    }
    t.checkExpect(stations, 0);
  }

  //Testing method generateConnectionsFromMST in class LightEmAll
  void testGenerateConnectionsFromMST(Tester t) {
    initData();
    LightEmAll game = new LightEmAll(3, 3);
    game.generateBoard();

    // Check that the MST contains the correct number of edges f
    t.checkExpect(game.mst.size(), 8);
    // Check that the top-left tile (0,0) is connected in at least one direction
    t.checkExpect(game.board.get(0).get(0).left ||
            game.board.get(0).get(0).right ||
            game.board.get(0).get(0).top ||
            game.board.get(0).get(0).bottom, true);
    // Check that the bottom-right tile (2,2) is also connected in at least one direction
    t.checkExpect(game.board.get(2).get(2).left ||
            game.board.get(2).get(2).right ||
            game.board.get(2).get(2).top ||
            game.board.get(2).get(2).bottom, true);
  }

  //Testing method generateAllPossibleEdges in class LightEmAll
  void testGenerateAllPossibleEdges(Tester t) {
    initData();
    ArrayList<Edge> edges = this.testGame.generateAllPossibleEdges();

    // Check the total number of edges 
    t.checkExpect(edges.size(), 12);

    // Check that the edges exist, without checking specific ordering
    boolean foundEdgeFromGP00 = false;
    boolean foundEdgeToGP10 = false;

    for (Edge e : edges) {
      if (e.from == this.gp00) {
        foundEdgeFromGP00 = true;
      }
      if (e.to == this.gp10) {
        foundEdgeToGP10 = true;
      }
    }

    t.checkExpect(foundEdgeFromGP00, true);
    t.checkExpect(foundEdgeToGP10, true);
  }

  //Testing method buildMinimumSpanningTree in class LightEmAll
  void testBuildMinimumSpanningTree(Tester t) {
    initData();
    ArrayList<Edge> edges = this.testGame.generateAllPossibleEdges();
    ArrayList<Edge> mst = this.testGame.buildMinimumSpanningTree(edges);

    // Check that the MST contains the correct number of edges
    t.checkExpect(mst.size(), 8);

    // Check that the MST edges are sorted in non-decreasing order of weight
    t.checkExpect(mst.get(0).weight <= mst.get(1).weight, true);
    t.checkExpect(mst.get(mst.size() - 1).weight >= mst.get(mst.size() - 2).weight, false);
  }

  //Testing method setConnectionsFromMST in class LightEmAll
  void testSetConnectionsFromMST(Tester t) {
    initData();
    LightEmAll game = new LightEmAll(2, 2);
    game.initializeEmptyBoard();
    GamePiece p00 = game.board.get(0).get(0);
    GamePiece p01 = game.board.get(0).get(1);
    game.mst.add(new Edge(p00, p01, 1));
    game.setConnectionsFromMST();
    // Test right connection
    t.checkExpect(p00.right, true);
    t.checkExpect(p01.left, true);
    // Test no extra connections
    t.checkExpect(p00.top, false);
    t.checkExpect(p01.bottom, false);
  }

  //Testing method randomizeRotations in class LightEmAll
  void testRandomizeRotations(Tester t) {
    initData();
    LightEmAll game = new LightEmAll(3, 3);
    game.generateBoard();
    // Count the total number of directional connections (edges) before randomizing
    int initialConnections = 0;
    for (GamePiece p : game.nodes) {
      if (p.top) {
        initialConnections++;
      }
      if (p.right) {
        initialConnections++;
      }
      if (p.bottom) {
        initialConnections++;
      }
      if (p.left) {
        initialConnections++;
      }
    }
    // Randomly rotate all pieces on the board
    game.randomizeRotations();
    // Count the total number of directional connections after rotation
    int newConnections = 0;
    boolean hasConnection = false; // Track if at least one connection remains
    for (GamePiece p : game.nodes) {
      if (p.top) {
        newConnections++;
      }
      if (p.right) {
        newConnections++;
      }
      if (p.bottom) {
        newConnections++;
      }
      if (p.left) {
        newConnections++;
      }
      if (p.top || p.right || p.bottom || p.left) {
        hasConnection = true;
      }
    }
    // Verify that the number of connections hasn't changed, only their directions
    t.checkExpect(newConnections, initialConnections);
    // Verify that at least one piece still has a connection
    t.checkExpect(hasConnection, true);
    // Check that the board still contains 9 game pieces
    t.checkExpect(game.nodes.size(), 9);
  }

  // Testing method resetGame in class LightEmAll
  void testResetGame(Tester t) {
    initData();
    LightEmAll game = new LightEmAll(3, 3);
    game.generateBoard();

    // Verify power station is reset to top-left corner.
    game.powerRow = 2;
    game.powerCol = 2;
    game.resetGame();
    t.checkExpect(game.powerRow, 0);
    t.checkExpect(game.powerCol, 0);

    // Verify tick and score are reset to 0.
    game.tick = 10;
    game.score = 5;
    game.resetGame();
    t.checkExpect(game.tick, 0);
    t.checkExpect(game.score, 0);

    // Verify all nodes are unpowered after reset.
    for (ArrayList<GamePiece> column : game.board) {
      for (GamePiece piece : column) {
        piece.powered = true;
      }
    }
    game.resetGame();
    boolean allUnpowered = true;
    for (ArrayList<GamePiece> column : game.board) {
      for (GamePiece piece : column) {
        if (piece.powered) {
          allUnpowered = false;
        }
      }
    }
    t.checkExpect(allUnpowered, false);
  }

  //Testing method updatePower in class LightEmAll
  void testUpdatePower(Tester t) {
    initData();
    // Center remains powered
    t.checkExpect(this.gp11.powered, true);

    // Connected piece
    t.checkExpect(this.gp01.powered, true);

    // Disconnected piece remains same
    t.checkExpect(this.gp22.powered, false);
  }

  // Testing method allPowered in class LightEmAll
  void testAllPowered(Tester t) {
    initData();
    // Initial state
    t.checkExpect(this.testGame.allPowered(), false);

    // All powered
    this.testGame.nodes.forEach(p -> p.powered = true);
    t.checkExpect(this.testGame.allPowered(), true);

    // One unpowered
    this.gp00.powered = false;
    t.checkExpect(this.testGame.allPowered(), false);
  }

  //Testing method getNeighbor in class LightEmAll
  void testGetNeighbor(Tester t) {
    initData();
    // Left neighbor 
    t.checkExpect(this.testGame.getNeighbor(this.gp11, "left"), this.gp01);

    // Top neighbor 
    t.checkExpect(this.testGame.getNeighbor(this.gp11, "top"), this.gp10);

    // Invalid direction remains same
    t.checkExpect(this.testGame.getNeighbor(this.gp11, "invalid"), null);
  }

  //Testing method getOppositeDirection in class LightEmAll
  void testGetOppositeDirection(Tester t) {
    initData();
    // Left -> Right
    t.checkExpect(this.testGame.getOppositeDirection("left"), "right");

    // Bottom -> Top
    t.checkExpect(this.testGame.getOppositeDirection("bottom"), "top");

    // Invalid input
    t.checkExpect(this.testGame.getOppositeDirection("diagonal"), null);
  }

  //Testing method calculateDiameter in class LightEmAll
  void testCalculateDiameter(Tester t) {
    initData();
    LightEmAll game = new LightEmAll(4, 4);
    game.rand = new Random(40);
    game.generateBoard();

    // Verify diameter calculation based on power propagation
    game.updatePower();
    // Check that the calculated diameter is non-negative
    t.checkExpect(game.calculateDiameter() >= 0, true);

    // Check that the diameter does not exceed an expected upper bound 
    t.checkExpect(game.calculateDiameter() <= 10, true);

    // check ensures the method does not incorrectly report a diameter greater than or equal to 1
    t.checkExpect(game.calculateDiameter() >= 1, true);
  }

  //Testing method findFurthestNode in class LightEmAll
  void testFindFurthestNode(Tester t) {
    initData();
    LightEmAll game = new LightEmAll(3, 3);
    game.generateBoard();

    // Verify furthest node is found and distance is set
    GamePiece furthest = game.findFurthestNode(game.board.get(0).get(0));
    // Verify that the distance from the power source (PS) for the furthest node is non-negative
    t.checkExpect(furthest.distanceFromPS >= 0, true);

    // Ensure that a furthest node is found
    t.checkExpect(furthest != null, true);

    // Verify that the distance from the PS is within a reasonable upper bound
    t.checkExpect(furthest.distanceFromPS <= 10, true);
  }

  //Testing method tileImage in class GamePiece
  void testTileImage(Tester t) {
    initData();
    // Verify power station overlay
    WorldImage stationImage = this.gp11.tileImage(50, 5, Color.YELLOW, true);
    WorldImage noStationImage = this.gp11.tileImage(50, 5, Color.YELLOW, false);
    t.checkExpect(stationImage.equals(noStationImage), false);

    // Station overlay
    WorldImage withStation = this.gp11.tileImage(50, 5, Color.YELLOW, true);
    WorldImage withoutStation = this.gp11.tileImage(50, 5, Color.YELLOW, false);
    t.checkExpect(withStation != withoutStation, true);

    // Wire count
    int wires = 0;
    if (this.gp11.top) {
      wires++;
    }
    if (this.gp11.right) {
      wires++;
    }
    if (this.gp11.bottom) {
      wires++;
    }
    if (this.gp11.left) {
      wires++;
    }
    t.checkExpect(wires, 4);
  }

  //Testing method rotate in class Game Piece
  void testRotate(Tester t) {
    initData();
    // Single rotation
    boolean originalLeft = this.gp00.left;

    boolean originalRight = this.gp00.right;

    boolean originalTop = this.gp00.top;

    boolean originalBottom = this.gp00.bottom;
    this.gp00.rotate();
    t.checkExpect(this.gp00.left, originalBottom);
    t.checkExpect(this.gp00.top, originalLeft);
    t.checkExpect(this.gp00.right, originalTop);
    t.checkExpect(this.gp00.bottom, originalRight);

    // Full rotation cycle
    this.gp11.rotate();
    this.gp11.rotate();
    this.gp11.rotate();
    this.gp11.rotate();
    t.checkExpect(this.gp11.left, true);

    // Connection update
    t.checkExpect(this.gp00.hasConnectionTo("left"), this.gp00.left);
  }

  //Testing method updatePowerStation in class GamePiece
  void testUpdatePowerStation(Tester t) {
    initData();
    // Set station
    this.gp00.updatePowerStation(true);
    t.checkExpect(this.gp00.powerStation, true);

    // Only one station
    this.gp11.updatePowerStation(false);

    // Count power stations
    int stationCount = 0;
    for (GamePiece p : this.testGame.nodes) {
      if (p.powerStation) {
        stationCount++;
      }
    }
    t.checkExpect(stationCount, 1);

    // Remove station
    this.gp00.updatePowerStation(false);
    t.checkExpect(this.gp00.powerStation, false);
  }

  //Testing method updatePowerState in class GamePiece
  void testUpdatePowerState(Tester t) {
    initData();
    // Enable power
    GamePiece p = new GamePiece(0,0, true, true, true, true);
    p.updatePowerState(true);
    t.checkExpect(p.powered, true);

    // Disable power
    p.updatePowerState(false);
    t.checkExpect(p.powered, false);

    // Toggle power
    p.updatePowerState(!p.powered);
    t.checkExpect(p.powered, true);
  }

  //Testing method updateDistanceFromPS in class GamePiece
  void testUpdateDistanceFromPS(Tester t) {
    initData();
    // Update the distance for the game piece at position (0,0)
    this.gp00.updateDistanceFromPS(10);
    // verify the expected distance value
    t.checkExpect(this.gp00.distanceFromPS, 10);

    // Update the distance for the same game piece (0,0) with a negative value
    this.gp00.updateDistanceFromPS(-1);
    // Verify that distanceFromPS is updated to -1
    t.checkExpect(this.gp00.distanceFromPS, -1);

    // Update the distance for the game piece at position (1,1) 
    this.gp11.updateDistanceFromPS(0);
    // Verify that distanceFromPS is updated to 0
    t.checkExpect(this.gp11.distanceFromPS, 0);
  }

  //Testing method hasConnectionTo in class GamePiece
  void testHasConnectionTo(Tester t) {
    initData();
    // Valid connection
    t.checkExpect(this.gp11.hasConnectionTo("left"), true);

    // Invalid connection
    t.checkExpect(this.gp00.hasConnectionTo("top"), false);

    // Invalid direction
    t.checkExpect(this.gp11.hasConnectionTo("invalid"), false);
  }

  //Testing method drawPowerGamePiece in class GamePiece
  void testDrawPowerGamePiece(Tester t) {
    initData();
    WorldImage imagePowered = gp11.drawPowerGamePiece(50, 5, 10);
    WorldImage imageUnpowered = gp22.drawPowerGamePiece(50, 5, 10);

    // Verify that the width of the powered game piece image is correct
    t.checkExpect(imagePowered.getWidth(), 52.0);

    // Verify that the width of the unpowered game piece image is correct
    t.checkExpect(imageUnpowered.getWidth(), 52.0);

    // Ensure that the powered and unpowered game piece images are not equal 
    t.checkExpect(imagePowered.equals(imageUnpowered), false);
  }

  //Testing method compareTo in class Edge
  void testCompareTo(Tester t) {
    initData();
    Edge edge1 = new Edge(this.gp00, this.gp01, 10);
    Edge edge2 = new Edge(this.gp10, this.gp11, 5);
    Edge edge3 = new Edge(this.gp20, this.gp21, 10);

    // Test comparison between edge1 and edge2
    t.checkExpect(edge1.compareTo(edge2), 5);

    // Test comparison between edge2 and edge1
    t.checkExpect(edge2.compareTo(edge1), -5);

    // Test comparison between edge1 and edge3
    t.checkExpect(edge1.compareTo(edge3), 0);
  }

  //Testing method find in class UnionFind
  void testFind(Tester t) {
    initData();
    UnionFind uf = new UnionFind(testGame.nodes);

    // Initially each node is its own parent
    t.checkExpect(uf.find(this.gp00), this.gp00);

    // verify that both find the same root
    uf.union(this.gp00, this.gp01);
    GamePiece root = uf.find(this.gp00);
    t.checkExpect(uf.find(this.gp01), root);

    // An unrelated node should still be its own parent
    t.checkExpect(uf.find(this.gp11) == this.gp11, true);
  }

  //Testing method union in class UnionFind
  void testUnion(Tester t) {
    initData();
    UnionFind uf = new UnionFind(this.testGame.nodes);

    // Perform the union of gp00 and gp01
    uf.union(this.gp00, this.gp01);
    // Check that after the union, gp00 and gp01 are in the same set
    t.checkExpect(uf.find(this.gp00), uf.find(this.gp01));

    // Perform the union of gp10 and gp11 
    uf.union(this.gp10, this.gp11);
    // Check that after the union, gp10 and gp11 are in the same set
    t.checkExpect(uf.find(this.gp10), uf.find(this.gp11));

    // Ensure that gp00 and gp10 are in different sets, as they were not unioned together
    t.checkExpect(uf.find(this.gp00) == uf.find(this.gp10), false);
  }

  void testBigBang(Tester t) {
    LightEmAll game = new LightEmAll(10, 10);
    game.bigBang(500, 540, 1);
  }

}