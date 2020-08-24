package main.java.snake;

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
	private boolean faild = false;
	
	private boolean right = true;
	private boolean left = false;
	private boolean up = false;
	private boolean down = false;
	
	private boolean canMove = true;
	
	private bodyPart bPart;
	private ArrayList<bodyPart> snake;
	
	private Apple apple;
	private ArrayList<Apple> apples;
	
	private Random ran;
	
	private int x = 10, y = 10, size = 5; 
	
	private float frameDelay = 42.5f;
	
	private int fps = 0;
	private int mpf = 0;
	
	public Gamepanel() {
		
		setFocusable(true);
		
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		addKeyListener(this);
		
		snake = new ArrayList<bodyPart>();
		apples = new ArrayList<Apple>();
		
		ran = new Random();	
		
		start();
	}
	
	public void start() {
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void stop() {
		running = false;
		faild = true;
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//All the code for running the game
	public void game() {
		
		canMove = true;		
		
		bPart = new bodyPart(x, y, 10);
		snake.add(bPart);
		
		if(snake.size() == 0) {
			bPart = new bodyPart(x, y, 10);
			snake.add(bPart);
		}
		
		if(right) {
			x = x + 1;
		}else if(left) {
			x = x - 1;
		}else if(up) {
			y = y - 1;
		}else if(down) {
			y= y + 1;
		}
		

		
		if(apples.size() == 0) {
			int x = ran.nextInt(49);
			int y = ran.nextInt(49);
			
			apple = new Apple(x,y, 10);
			apples.add(apple);
		}
		
		for(int i = 0 ; i < apples.size(); i ++) {
			if(x == apples.get(i).getx() && y == apples.get(i).gety()) {
				size++;
				apples.remove(i);
				i++;
			}
		}
		
		//collision on body
		for(int i = 0; i < snake.size(); i++) {
			if(x == snake.get(i).getx() && y == snake.get(i).gety()) {
				if(i != snake.size()-1) {
					stop();
				}
			}
		}
		
		//remove excessive snake pixles
		if(snake.size() > size) {
			snake.remove(0);
		}
		
		//collision on border
		if(x < 0) {
			stop();
		}else if(x > 49) {
			stop();
		}else if(y < 0) {
			stop();
		}else if(y > 49) {
			stop();
		}
	}
	
	public void paint(Graphics g) {
		
		g.clearRect(0, 0, WIDTH, HEIGHT);
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		

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
			g.drawString("Press any key to start!", WIDTH/2 - 55, HEIGHT/2);
		}
		if(faild) {
			g.drawString("Game Over", WIDTH/2 - 35, HEIGHT/2);
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
