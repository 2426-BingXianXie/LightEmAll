import tester.*;

// Main application class to run the LightEmAll game
public class App {
  public static void main(String[] args) {
    LightEmAll game = new LightEmAll(10, 10);
    game.bigBang(500, 540, 1);
  }
}