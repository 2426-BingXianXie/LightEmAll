# Light Em All - Electrical Circuit Puzzle Game

An interactive puzzle game where players connect electrical circuits by rotating tiles to power all nodes on a dynamically generated board. Built with Java using advanced algorithms including Kruskal's Minimum Spanning Tree and Breadth-First Search for optimal gameplay experience.

**Author**: BingXian Xie  
**Course**: CS2510 - Fundamentals of Computer Science II  
**Institution**: Northeastern University  
**Semester**: Spring 2025

---

## 📋 Table of Contents

- [Overview](#overview)
- [Game Mechanics](#game-mechanics)
- [Technology Stack](#technology-stack)
- [System Architecture](#system-architecture)
- [Installation & Setup](#installation--setup)
- [How to Play](#how-to-play)
- [Implementation Details](#implementation-details)
- [Key Algorithms](#key-algorithms)
- [Testing](#testing)
- [Skills Demonstrated](#skills-demonstrated)
- [Screenshots](#screenshots)
- [Academic Integrity](#academic-integrity)
- [Contact](#contact)

---

## Overview

Light Em All is a sophisticated puzzle game that challenges players to illuminate all nodes on a board by creating a complete electrical circuit. The game dynamically generates solvable puzzles using graph theory algorithms and provides an engaging visual experience with power propagation effects.

### Key Features

- **Dynamic Puzzle Generation** - Uses Kruskal's algorithm to generate unique solvable mazes
- **Power Propagation System** - Breadth-First Search for realistic power flow simulation
- **Interactive Gameplay** - Click to rotate tiles, arrow keys to move power station
- **Progressive Difficulty** - Adaptive radius based on board diameter calculation
- **Visual Feedback** - Color-coded power levels with gradient effects
- **Score Tracking** - Real-time move counter and elapsed time display

### Game Objectives

- **Primary Goal**: Connect all tiles to power every node on the board
- **Challenge**: Minimize moves and time to achieve the best score
- **Strategy**: Plan rotations carefully to create efficient power paths
- **Win Condition**: All nodes illuminated within the power station's radius

---

## Game Mechanics

### Core Components

#### GamePiece
Each tile on the board with four potential wire directions:
- **Connections**: Top, Right, Bottom, Left (boolean states)
- **State**: Powered (true/false), Power Station (true/false)
- **Position**: Row and column coordinates
- **Distance**: Steps from power source for color gradient

#### LightEmAll World
Main game engine managing:
- **Board**: 2D ArrayList of GamePieces in column-major order
- **Power Station**: Movable source of electricity
- **Radius**: Effective power propagation distance
- **Scoring**: Move counter and elapsed time tracking

#### Edge & UnionFind
Graph algorithm components:
- **Edge**: Weighted connection between two GamePieces
- **UnionFind**: Disjoint-set data structure for Kruskal's algorithm

### Gameplay Flow

```
1. Board Generation
   ├── Create empty NxM grid
   ├── Generate random weighted edges
   ├── Apply Kruskal's MST algorithm
   ├── Randomly rotate all tiles
   └── Calculate optimal radius

2. Player Interaction
   ├── Mouse Click → Rotate tile
   ├── Arrow Keys → Move power station
   └── Press 'R' → Reset game

3. Power Propagation
   ├── BFS from power station
   ├── Update powered states
   ├── Apply color gradients
   └── Check win condition
```

---

## Technology Stack

### Core Technologies
- **Java 11** - Primary programming language
- **JavaLib World** - Game framework for animation and interaction
- **JavaLib WorldImages** - Image rendering library
- **JUnit/Tester** - Testing framework

### Development Tools
- **Eclipse/IntelliJ IDEA** - IDE
- **Git** - Version control
- **Design Patterns** - MVC architecture

### Key Libraries
```java
import javalib.impworld.*;      // World-based animation
import javalib.worldimages.*;   // Image rendering
import tester.*;                // Testing framework
import java.awt.Color;          // Color management
import java.util.*;             // Collections framework
```

---

## System Architecture

### Class Diagram

```
┌─────────────────┐
│  LightEmAll     │
│   (extends      │
│    World)       │
├─────────────────┤
│ - board         │───┐
│ - nodes         │   │
│ - mst           │   │  Contains
│ - powerRow/Col  │   │
│ - radius        │   │
│ - score, tick   │   │
└─────────────────┘   │
                      │
                      ▼
         ┌──────────────────┐
         │   GamePiece      │
         ├──────────────────┤
         │ - row, col       │
         │ - left, right    │
         │ - top, bottom    │
         │ - powered        │
         │ - powerStation   │
         │ - distanceFromPS │
         └──────────────────┘
                ▲
                │ From/To
                │
         ┌──────────────┐
         │     Edge     │
         ├──────────────┤
         │ - from       │
         │ - to         │
         │ - weight     │
         └──────────────┘
                │
                │ Uses
                ▼
         ┌──────────────┐
         │  UnionFind   │
         ├──────────────┤
         │ - parent     │
         │ + find()     │
         │ + union()    │
         └──────────────┘
```

### Data Flow

```
User Input → Event Handler → Game State Update → Power Propagation → Visual Rendering
```

---

## Installation & Setup

### Prerequisites

1. **Java Development Kit (JDK) 11+**
   ```bash
   java -version  # Verify installation
   ```

2. **JavaLib World Library**
    - Download from course resources
    - Add to project classpath

3. **IDE** (Eclipse or IntelliJ IDEA)
    - Configure Java project
    - Import JavaLib library

### Setup Instructions

1. **Clone or Download Project**
   ```bash
   # If using Git
   git clone <repository-url>
   cd light-em-all
   ```

2. **Import to IDE**
    - **Eclipse**: File → Import → Existing Projects
    - **IntelliJ**: File → Open → Select project directory

3. **Add JavaLib to Build Path**
    - Right-click project → Build Path → Add External JARs
    - Select `javalib.jar` file

4. **Compile and Run**
   ```java
   // Run the main method in ExamplesLightWorld class
   public void testBigBang(Tester t) {
       LightEmAll game = new LightEmAll(10, 10);
       game.bigBang(500, 540, 1);
   }
   ```

---

## How to Play

### Starting the Game

```java
LightEmAll game = new LightEmAll(width, height);
game.bigBang(width * 50, height * 50 + 40, 1);
```

**Parameters**:
- `width`: Number of columns (recommended: 5-15)
- `height`: Number of rows (recommended: 5-15)
- Window size automatically scales to board dimensions

### Controls

| Action | Control | Description |
|--------|---------|-------------|
| **Rotate Tile** | Mouse Click | Click any tile to rotate wires 90° clockwise |
| **Move Power Station** | Arrow Keys | Use ↑↓←→ to move the power source |
| **Reset Game** | R Key | Generate new board and reset score |
| **Quit Game** | ESC or Close | Exit the game |

### Objective

1. **Connect All Nodes**: Rotate tiles to create continuous power paths
2. **Stay Within Radius**: All nodes must be within power station's reach
3. **Minimize Moves**: Lower score indicates better performance
4. **Watch the Timer**: Complete puzzles quickly for challenge

### Winning

- **Win Condition**: All nodes powered (turn yellow/orange)
- **Visual Feedback**: "You Win" message appears in green
- **Reset Option**: Press 'R' to play again
- **Score Display**: Final moves and time shown

### Strategy Tips

1. **Start from Power Station**: Work outward systematically
2. **Plan Ahead**: Visualize connections before rotating
3. **Use Radius Wisely**: Keep critical paths within reach
4. **Corner Tiles**: Often require fewer rotations
5. **BFS Approach**: Connect nodes layer by layer

---

## Implementation Details

### Minimum Spanning Tree Generation

**Algorithm**: Kruskal's Algorithm with Union-Find

```java
public ArrayList<Edge> buildMinimumSpanningTree(ArrayList<Edge> edges) {
    ArrayList<Edge> result = new ArrayList<Edge>();
    UnionFind uf = new UnionFind(this.nodes);
    
    Collections.sort(edges);  // Sort by weight
    
    for (Edge edge : edges) {
        GamePiece rootFrom = uf.find(edge.from);
        GamePiece rootTo = uf.find(edge.to);
        
        if (rootFrom != rootTo) {  // No cycle
            result.add(edge);
            uf.union(rootFrom, rootTo);
            
            if (result.size() == nodes.size() - 1) break;  // MST complete
        }
    }
    return result;
}
```

**Why MST?**
- Guarantees connected graph (solvable puzzle)
- Minimizes redundant connections
- Creates interesting puzzle patterns
- Enables unique board generation

### Power Propagation System

**Algorithm**: Breadth-First Search (BFS)

```java
public void updatePower() {
    // Reset all power states
    for (ArrayList<GamePiece> column : this.board) {
        for (GamePiece piece : column) {
            piece.updatePowerState(false);
        }
    }
    
    // Start BFS from power station
    GamePiece powerSource = this.board.get(this.powerCol).get(this.powerRow);
    ArrayList<GamePiece> queue = new ArrayList<GamePiece>();
    ArrayList<GamePiece> visited = new ArrayList<GamePiece>();
    
    queue.add(powerSource);
    visited.add(powerSource);
    powerSource.updatePowerState(true);
    powerSource.updateDistanceFromPS(0);
    
    while (!queue.isEmpty()) {
        GamePiece current = queue.remove(0);
        
        for (String direction : directions) {
            GamePiece neighbor = this.getNeighbor(current, direction);
            String opposite = this.getOppositeDirection(direction);
            
            if (neighbor != null && !visited.contains(neighbor)) {
                if (current.hasConnectionTo(direction) && 
                    neighbor.hasConnectionTo(opposite)) {
                    
                    neighbor.updateDistanceFromPS(current.distanceFromPS + 1);
                    
                    if (neighbor.distanceFromPS <= this.radius) {
                        neighbor.updatePowerState(true);
                        queue.add(neighbor);
                        visited.add(neighbor);
                    }
                }
            }
        }
    }
}
```

**BFS Benefits**:
- O(V + E) time complexity (V = nodes, E = edges)
- Accurate distance calculation for gradient effects
- Realistic power flow simulation
- Efficient radius-based limiting

### Diameter Calculation

**Purpose**: Determine optimal power radius for solvable puzzles

```java
public int calculateDiameter() {
    GamePiece start = this.board.get(this.powerCol).get(this.powerRow);
    
    // First BFS: Find furthest node from start
    GamePiece firstBFSend = this.findFurthestNode(start);
    
    // Second BFS: Find furthest node from firstBFSend
    GamePiece secondBFSend = this.findFurthestNode(firstBFSend);
    
    // Maximum distance found = diameter
    int diameter = 0;
    for (GamePiece gp : this.nodes) {
        if (gp.distanceFromPS > diameter) {
            diameter = gp.distanceFromPS;
        }
    }
    
    return diameter;
}
```

**Why Two BFS?**
- Single BFS may not find true diameter
- First BFS finds one end of longest path
- Second BFS from that end finds true diameter
- Radius set to `(diameter / 2) + 1` for balanced gameplay

### Visual Color Gradient

```java
public WorldImage drawPowerGamePiece(int size, int wireWidth, int radius) {
    Color wireColor;
    
    if (this.powered) {
        int distanceRatio = Math.min(255, Math.max(0, this.distanceFromPS * 8));
        wireColor = new Color(255, 255 - distanceRatio, 0);  // Yellow to Orange
    } else {
        wireColor = Color.LIGHT_GRAY;
    }
    
    return this.tileImage(size, wireWidth, wireColor, this.powerStation);
}
```

**Gradient Effect**:
- Nodes closer to power station: Bright yellow (255, 255, 0)
- Nodes farther away: Orange (255, 200, 0) → Red (255, 0, 0)
- Unpowered nodes: Light gray
- Power station: Cyan star overlay

---

## Key Algorithms

### 1. Kruskal's Minimum Spanning Tree

**Purpose**: Generate connected, solvable puzzle boards

**Complexity**: O(E log E) where E = number of edges

**Process**:
1. Generate all possible edges with random weights
2. Sort edges by weight (ascending)
3. Use Union-Find to detect cycles
4. Add edge if it doesn't create a cycle
5. Stop when MST has (V-1) edges

**Code Highlight**:
```java
// Union-Find with Path Compression
GamePiece find(GamePiece node) {
    if (parent.get(node) != node) {
        parent.put(node, find(parent.get(node)));  // Path compression
    }
    return parent.get(node);
}
```

### 2. Breadth-First Search (BFS)

**Purpose**: Power propagation and distance calculation

**Complexity**: O(V + E)

**Applications**:
- Real-time power state updates
- Distance-based color gradients
- Radius enforcement
- Win condition checking

### 3. Graph Diameter Calculation

**Purpose**: Determine optimal power radius

**Complexity**: O(V + E) - Two BFS runs

**Algorithm**:
1. Run BFS from arbitrary start node
2. Find furthest node from start (Node A)
3. Run BFS from Node A
4. Maximum distance = diameter

---

## Testing

### Test Coverage

Comprehensive test suite with **50+ test methods** covering:

#### Game Logic Tests
- `testMakeScene()` - Scene rendering
- `testOnKeyEvent()` - Input handling
- `testOnMousePressed()` - Mouse interaction
- `testOnTick()` - Game loop timing
- `testUpdatePower()` - Power propagation

#### Board Generation Tests
- `testGenerateBoard()` - Full board creation
- `testInitializeEmptyBoard()` - Empty grid setup
- `testGenerateConnectionsFromMST()` - MST application
- `testRandomizeRotations()` - Tile randomization

#### Algorithm Tests
- `testBuildMinimumSpanningTree()` - MST correctness
- `testCalculateDiameter()` - Diameter accuracy
- `testFindFurthestNode()` - BFS correctness
- `testUnionFind()` - Union-Find operations

#### GamePiece Tests
- `testRotate()` - Wire rotation logic
- `testDrawPowerGamePiece()` - Visual rendering
- `testHasConnectionTo()` - Connection checking
- `testUpdatePowerState()` - State management

### Running Tests

```java
// In ExamplesLightWorld class
public static void main(String[] args) {
    Tester.run(new ExamplesLightWorld());
}
```

**Test Framework**: JUnit-style with custom Tester class

**Verification Methods**:
- `t.checkExpect()` - Value equality
- `t.checkRange()` - Numeric ranges
- `t.checkException()` - Error handling

---

