import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JPanel;
//hello
public class Gamepanel extends JPanel implements Runnable, KeyListener{

	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 500, HEIGHT = 500;
	
	private Thread thread;
	
	private boolean running;
	
	private boolean right = true, left = false, up = false, down = false;
	private boolean canMove = true;
	
	private bodyPart b;
	private ArrayList<bodyPart> snake;
	
	private Apple apple;
	private ArrayList<Apple> apples;
	
	private Random r;
	
	private int xCoor = 10, yCoor = 10, size = 5; 
	
	private float frameDelay = 42.5f;
	
	private int fps = 0;
	private int mpf = 0;
	
	public Gamepanel() {
		
		setFocusable(true);
		
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		addKeyListener(this);
		
		snake = new ArrayList<bodyPart>();
		apples = new ArrayList<Apple>();
		
		r = new Random();	
		
		start();
	}
	
	public void start() {
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//All the code for running the game
	public void game() {
		
		if(snake.size() == 0) {
			b = new bodyPart(xCoor, yCoor, 10);
			snake.add(b);
		}
		
		canMove = true;
		
		if(right) xCoor++;
		if(left) xCoor--;
		if(up) yCoor--;
		if(down) yCoor++;
		
		b = new bodyPart(xCoor, yCoor, 10);
		snake.add(b);
			
		if(snake.size() > size) {
			snake.remove(0);
		}
		
		if(apples.size() == 0) {
			int xCoor = r.nextInt(49);
			int yCoor = r.nextInt(49);
			
			apple = new Apple(xCoor, yCoor, 10);
			apples.add(apple);
		}
		
		for(int i = 0 ; i < apples.size(); i ++) {
			if(xCoor == apples.get(i).getxCoor() && yCoor == apples.get(i).getyCoor()) {
				size++;
				apples.remove(i);
				i++;
			}
		}
		
		//collision on body
		for(int i = 0; i < snake.size(); i++) {
			if(xCoor == snake.get(i).getxCoor() && yCoor == snake.get(i).getyCoor()) {
				if(i != snake.size()-1) {
					System.out.println("GAME OVER");
					stop();
				}
			}
		}
		
		
		//collision on border
		if(xCoor < 0 || xCoor > 49 || yCoor < 0 || yCoor > 49) {
			System.out.println("GAME OVER");
			stop();
		}
		
	}
	
	public void paint(Graphics g) {
		
		g.clearRect(0, 0, WIDTH, HEIGHT);
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
//		for(int i = 0; i < WIDTH/10 ; i++) {
//			g.drawLine(i * 10, 0, i* 10, HEIGHT);
//		}
//		
//		for(int i = 0; i < HEIGHT/10 ; i++) {
//			g.drawLine(0, i* 10, HEIGHT, i * 10);
//		}
		for(int i = 0 ; i < snake.size(); i ++) {
			snake.get(i).draw(g);
		}
		for(int i = 0; i < apples.size(); i++) {
			apples.get(i).draw(g);
		}
		
		//Draws the FPS into the window
		g.setColor(Color.WHITE);
		g.drawString("FPS: " + Integer.toString(fps), 10, 10);
		g.drawString("MPF: " + Integer.toString(mpf), 10, 25);
		
		if(!started) {
			g.drawString("Press any key to start!", WIDTH/2 - 52, HEIGHT/2);
		}
		
	}
	
	// These are variables used in calculating the 
	long fpsStartTime;
	long fpsElapsed;
	int count;
	
	/*
	 * Calculates the average time it takes to render a frame (in milliseconds).
	 * This is used for debug
	 */
	int frameTimeIndex = 0;
	float frameTimeSum = 0;
	float[] frameTimes = new float[100];
	float calcAverageFrameTimeMillis(float newTime) {
		frameTimeSum -= frameTimes[frameTimeIndex];
		frameTimeSum += newTime;
		frameTimes[frameTimeIndex] = newTime;
		frameTimeIndex = (frameTimeIndex + 1) % 100;
		return frameTimeSum / 100;
	}
	
	public boolean started = false;
	
	@Override
	public void run() {
		count = 0;
		float beginTime = System.nanoTime() / 1000000.0f;
		while(running) {
			
			fpsStartTime = System.nanoTime();
			
			float elapsedTime = (System.nanoTime() / 1000000.0f) - beginTime; //The amount of time since the last frame was drawn was set
			
			if(elapsedTime >= frameDelay && started) { //Run if the elapsed time is greater than the set delay between frames
				game();
				beginTime = System.nanoTime() / 1000000.0f; //Reset beginTime since a new frame has been drawn
			}
			repaint();
			
			float averageFrameTime = calcAverageFrameTimeMillis(fpsElapsed); //Calculate the averageFrameTime
			if(count > 500000) { //Display average frame rate and milliseconds per frame
				mpf = (int) averageFrameTime;
				fps = (int) (1000.0f / averageFrameTime);
				count = 0;
			}
			
			fpsElapsed = System.nanoTime() - fpsStartTime;
			count++;
		}
		
	}
	
	//key reading
	@Override
	public void keyPressed(KeyEvent e) {
		started = true;
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_RIGHT  && !left && canMove) {
			right = true;
			up = false;
			down = false;
		}
		if(key == KeyEvent.VK_LEFT  && !right && canMove) {
			left = true;
			up = false;
			down = false;
		}
		if(key == KeyEvent.VK_UP  && !down && canMove) {
			up = true;
			left = false;
			right = false;
		}
		if(key == KeyEvent.VK_DOWN  && !up && canMove	) {
			down = true;
			left = false;
			right = false;
		}
		canMove = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
