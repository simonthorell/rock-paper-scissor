public class App {
    public static void main(String[] args) throws Exception {
        GUI gui = new GUI();
        Handler handler = new Handler();

        gui.setHandler(handler);
        handler.setGUI(gui);
        
        gui.window();

    }
}
