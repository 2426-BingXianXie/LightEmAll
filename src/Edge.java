// Represents a connection between two GamePieces with a weight value
class Edge implements Comparable<Edge> {
  GamePiece from;
  GamePiece to;
  int weight;

  Edge(GamePiece from, GamePiece to, int weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }

  // Compares edges by weight for sorting in Kruskal's algorithm
  public int compareTo(Edge other) {
    return this.weight - other.weight;
  }
}