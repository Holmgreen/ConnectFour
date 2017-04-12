package Entity;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Coin extends Ellipse2D.Double{
	private static final long serialVersionUID = 1L;
	private double y;
	private double yvel;
	private double yacc = 1;
	private double targetY;
	private double distFrac; // The percentual difference from drawn point and actual point
	
	private boolean falling;
	public Coin(){
		super();
		falling = false;
	}
	
	public void draw(Graphics2D g){
		if(falling){
			y += yvel;
			yvel += yacc;
			distFrac = y/targetY;
			if(y >= targetY){
				distFrac = 1;
				y = targetY;
				falling = false;
			}
		}
		updatePos();
		g.fill(this);
	}
	
	public void updatePos(){
		setFrame(getX(), y, getWidth(), getHeight());
	}
	
	public void startFall(){
		falling = true;
		y = 0;
		yvel = 0; // Initial falling speed
	}
	
	public void setTarget(double targetY){
		this.targetY = targetY;
	}
	
	public void updateFrame(double newx, double newy, double w, double h){
		setFrame(newx, newy, w, h);
		y = newy*distFrac;
		//targetY = newy;
	}
}
