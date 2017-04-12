package Entity;

/**
 * Represents a player of the game.
 */
public class Player {
	protected String name; 
	
	public Player(String name){
		this.name = name;
	}
	
	public String toString(){
		return name;
	}

}
