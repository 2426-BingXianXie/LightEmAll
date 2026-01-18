import java.util.ArrayList;
import java.util.HashMap;

// Represents a data structure that manages connected components for Kruskal's algorithm
class UnionFind {
  HashMap<GamePiece, GamePiece> parent;

  // Initializes each GamePiece to be its own parent 
  UnionFind(ArrayList<GamePiece> nodes) {
    parent = new HashMap<GamePiece, GamePiece>();
    for (GamePiece node : nodes) {
      parent.put(node, node);
    }
  }

  // Finds root of component with path compression
  // EFFECT: updates parent pointers for all nodes in the path to point directly to the root
  GamePiece find(GamePiece node) {
    if (parent.get(node) != node) {
      parent.put(node, find(parent.get(node)));
    }
    return parent.get(node);
  }

  // Merges two components by attaching root of one to root of the other
  // EFFECT: updates the parent pointer of rootA to point to rootB
  void union(GamePiece a, GamePiece b) {
    GamePiece rootA = find(a);
    GamePiece rootB = find(b);
    if (rootA != rootB) {
      parent.put(rootA, rootB);
    }
  }
}