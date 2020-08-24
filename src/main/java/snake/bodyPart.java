package main.java.snake;

import java.awt.Color;
import java.awt.Graphics;

public class bodyPart {

	private int x , y, width, height;
	
	public bodyPart(int x, int y, int tileSize) {
		this.x = x;
		this.y = y;
		width = tileSize;
		height = tileSize;	
	}
	
	public void tick() {
		
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.YELLOW);
		g.fillRect(x * width, y * height, width, height);
	}
	
	public int getx() {
		return x;
	}

	public void setx(int x) {
		this.x = x;
	}

	public int gety() {
		return y;
	}

	public void sety(int y) {
		this.y = y;
	}
}
