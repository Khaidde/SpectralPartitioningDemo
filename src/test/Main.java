package test;

import graphics.Graph;
import graphics.Window;

public class Main {
	
	public static void main(String[] args) {
		Window window = new Window("Spectral Partitioning", 500, 500);

        Graph graph = new Graph(window);

        boolean running = true;
        Thread thread = new Thread(() -> {
            final int FRAME_TIME = 16;
            long delta = 0;
            long now = System.currentTimeMillis();
            while(running) {
                delta += System.currentTimeMillis() - now;
                now = System.currentTimeMillis();
                while (delta >= FRAME_TIME) {
                    delta -= FRAME_TIME;
                    graph.update();
                }
                window.render(graph::render);
            }
        });
        thread.start();
	}

}
