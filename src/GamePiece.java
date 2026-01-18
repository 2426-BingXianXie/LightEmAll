import java.awt.Color;
import javalib.worldimages.*;

// Represents a game piece
class GamePiece {
  // in logical coordinates, with the origin
  // at the top-left corner of the screen
  int row;
  int col;
  // whether this GamePiece is connected to the
  // adjacent left, right, top, or bottom pieces
  boolean left;
  boolean right;
  boolean top;
  boolean bottom;
  // whether the power station is on this piece
  boolean powerStation;
  boolean powered;
  int distanceFromPS;

  GamePiece(int row, int col, boolean left, boolean right, boolean top, boolean bottom) {
    this.row = row;
    this.col = col;
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
    this.powerStation = false;
    this.powered = false;
    this.distanceFromPS = 0;
  }

  //Generate an image of this, the given GamePiece.
  // - size: the size of the tile, in pixels
  // - wireWidth: the width of wires, in pixels
  // - wireColor: the Color to use for rendering wires on this
  // - hasPowerStation: if true, draws a fancy star on this tile to represent the power station
  //
  WorldImage tileImage(int size, int wireWidth, Color wireColor, boolean hasPowerStation) {
    // Start tile image off as a blue square with a wire-width square in the middle,
    // to make image "cleaner" (will look strange if tile has no wire, but that can't be)
    WorldImage image = new OverlayImage(
            new RectangleImage(wireWidth, wireWidth, OutlineMode.SOLID, wireColor),
            new RectangleImage(size, size, OutlineMode.SOLID, Color.DARK_GRAY));
    WorldImage vWire = new RectangleImage(wireWidth, (size + 1) / 2, OutlineMode.SOLID, wireColor);
    WorldImage hWire = new RectangleImage((size + 1) / 2, wireWidth, OutlineMode.SOLID, wireColor);

    if (this.top) {
      image = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.TOP, vWire, 0, 0, image);
    }
    if (this.right) {
      image = new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.MIDDLE, hWire, 0, 0, image);
    }
    if (this.bottom) {
      image = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.BOTTOM, vWire, 0, 0, image);
    }
    if (this.left) {
      image = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.MIDDLE, hWire, 0, 0, image);
    }
    if (hasPowerStation) {
      image = new OverlayImage(
              new OverlayImage(
                      new StarImage(size / 3, 7, OutlineMode.OUTLINE, new Color(255, 128, 0)),
                      new StarImage(size / 3, 7, OutlineMode.SOLID, new Color(0, 255, 255))),
              image);
    }
    return new OverlayImage(image, new RectangleImage(size+2, size+2, OutlineMode.SOLID, Color.BLACK));
  }

  // Rotate the wire status in this game piece
  // EFFECT: change the left, right, top, bottom field
  public void rotate() {
    boolean temp = this.left;
    this.left = this.bottom;
    this.bottom = this.right;
    this.right = this.top;
    this.top = temp;
  }

  // Update the power station state of this game piece
  // EFFECT: change the power station state of this game piece to the given state
  public void updatePowerStation(boolean state) {
    this.powerStation = state;
  }

  // Update the power state of this game piece
  // EFFECT: change the power state of this game piece to the given state
  public void updatePowerState(boolean state) {
    this.powered = state;
  }

  // Update the distance from power station of this game piece
  // EFFECT: change the distance from power station of this game piece
  public void updateDistanceFromPS(int distance) {
    this.distanceFromPS = distance;
  }

  // Check if this has a wire of the given direction
  public boolean hasConnectionTo(String direction) {
    if (direction.equals("left")) {
      return this.left;
    }
    if (direction.equals("right")) {
      return this.right;
    }
    if (direction.equals("top")) {
      return this.top;
    }
    if (direction.equals("bottom")) {
      return this.bottom;
    }
    return false;
  }

  // Draws the game piece with coloring based on its powered state and distance from power
  public WorldImage drawPowerGamePiece(int size, int wireWidth, int radius) {
    Color wireColor;

    if (this.powered) {
      int distanceRatio = 0;
      if (this.distanceFromPS != -1 && radius > 0) {
        distanceRatio = Math.min(255, Math.max(0, this.distanceFromPS * 8));
      }
      wireColor = new Color(255, 255 - distanceRatio, 0);
    }
    else {
      wireColor = Color.LIGHT_GRAY;
    }
    return this.tileImage(size, wireWidth, wireColor, this.powerStation);
  }
}