package game;

import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a level of the game
 */
public class Level {
	
	/**
	 * The default tile size
	 */
	public static final int DEFAULT_TILE_SIZE = 40;
	
	/**
	 * Represents the path to the levels
	 */
	public static final String LEVEL_FILE_PATH = "Levels/";
	
	/**
	 * The name of the level
	 */
	private final String name;
	
	
	/**
	 * The description of the level
	 */
	private final String description;
	
	/**
	 * Is the level unlocked or not ?
	 * <tt>true</tt> if we haven't finished the level yet
	 * <tt>false</tt> else
	 */
	private boolean isLock;
	
	/**
	 * contains the entire level loaded from a file
	 */
	private List<String> loadedLevel;
	
	/**
	 * The width of the tiles
	 */
	private int tileWidth;
	
	/**
	 * The height of the tiles
	 */
	private int tileHeight;
	
	/**
	 * The offset on the x axis of the tiles
	 */
	private int offsetX;
	
	/**
	 * The offset on the y axis of the tiles
	 */
	private int offsetY;
	
	/**
	 * Stores the position (the tile indexes in the file) of all the collected strawberries
	 */
	private Set<Integer> strawberriesCollectedPositions;
	
	/**
	 * Creates a new level with given properties
	 * @param theName the name of the level
	 * @param theDescription the description of the level
	 */
	public Level(String theName, String theDescription) {
		this.name = theName;
		this.description = theDescription;
		this.isLock = true;
		this.loadedLevel = new ArrayList<String>();
		this.strawberriesCollectedPositions = new HashSet<Integer>();
		this.init();
		this.tileWidth = this.tileHeight = Level.DEFAULT_TILE_SIZE;
	}
	

	/**
	 * Initializes the level attributes
	 */
	public void init() {
		this.offsetX = 0;
		this.offsetY = 0;
	}
	
	/**
	 * Load the level in memory
	 * @param br The BufferedReader used to read a level
	 * @throws FileNotFoundException If the corresponding file does not exist 
	 * @throws IOException If an error occurs during the reading of the file
	 */
	public void load(BufferedReader br) throws FileNotFoundException, IOException{
		this.loadedLevel = new ArrayList<String>();
		this.strawberriesCollectedPositions.clear();
		
		String line;
		
		//For each line of the file
		while((line = br.readLine()) != null) {
			this.loadedLevel.add(line);
		}
		
		br.close();
	}
	
	/**
	 * is the level locked ?
	 * @return <tt>true</tt> the level has not been finished yet or <tt>false</tt> the level has been finished
	 */
	public boolean isLocked() {
		return this.isLock;
	}
	
	/**
	 * Unlocks the current level
	 */
	public void unlock() {
		this.isLock = false;
	}
	
	/**
	 * Gives the name of the level
	 * @return the name of the level
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Gives the description of the level
	 * @return the description of the level
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Gets the width of the tiles
	 * @return the width of the tiles
	 */
	public int getTileWidth() {
		return this.tileWidth;
	}
	
	/**
	 * Gets the height of the tiles
	 * @return the height of the tiles
	 */
	public int getTileHeight() {
		return this.tileHeight;
	}
	
	/**
	 * Displays the level at the screen
	 * @param g the graphical component
	 * @param gameWidth the width of the game
	 * @param gameHeight the height of the game
	 */
	public void display(Graphics g, int gameWidth, int gameHeight) {
		int y = gameHeight - this.tileHeight;
		int tileIndex = 0;
		
		//Each tile of each line is represented by a character in memory
		for(int level = this.loadedLevel.size() - 1; level >= 0; level--) {
			String line = this.loadedLevel.get(level);
			for(int x = 0; x < line.length(); x++) {
				
				//If the current tile is a wall
				if(line.charAt(x) == EnumTiles.Wall.charRepresentation) {
					g.setColor(EnumTiles.Wall.tileColor);
					g.fillRect((x * this.tileWidth) + this.offsetX, y + this.offsetY, this.tileWidth, this.tileHeight);
					
				}
				
				//If it's the end
				if(line.charAt(x) == EnumTiles.End.charRepresentation) {
					g.setColor(EnumTiles.End.tileColor);
					g.fillRect((x * this.tileWidth) + this.offsetX,  y + this.offsetY, this.tileWidth, this.tileHeight);
					
				}
				
				//If it's a strawberry
				if(line.charAt(x) == EnumTiles.Strawberries.charRepresentation && (!this.strawberriesCollectedPositions.contains(tileIndex))) {
					g.setColor(EnumTiles.Strawberries.tileColor);
					g.fillRect((x * this.tileWidth) + this.offsetX,  y + this.offsetY, this.tileWidth, this.tileHeight);
				}
				
				
				if(line.charAt(x) == EnumTiles.Wall.charRepresentation || line.charAt(x) == EnumTiles.End.charRepresentation || line.charAt(x) == EnumTiles.Strawberries.charRepresentation) {
					tileIndex++;
				}
			}
			y -= this.tileHeight;
		}
	}
	
	/**
	 * Loads all the levels of the game
	 * @return an array containing all the levels
	 * @throws LevelException If the LevelNames.txt file does not contain enough information about the level
	 * @throws FileNotFoundException If the LevelNames.txt file does not exists
	 * @throws IOException If an error is thrown during the reading
	 */
	public static Level[] loadAllLevels() throws LevelException, FileNotFoundException, IOException {
		List<Level> levels = new ArrayList<Level>();
		String fileName = Level.LEVEL_FILE_PATH +"LevelNames.txt";

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		
		String line;
		int i = 0;
		while((line = br.readLine()) != null) {
			String[] levelInformations = line.split(",");
			if(levelInformations.length == 2) {
				levels.add(new Level((i+1) +"_" +levelInformations[0], levelInformations[1]));
			} else {
				br.close();
				throw new LevelException();
			}
			i++;
		}
		
		br.close();

		return levels.toArray(new Level[levels.size()]);
	}

	/**
	 * Returns the initial position of the character
	 * @param gameHeight the height of the game screen
	 * @param gameWidth the width of the game screen
	 * @return The initial position of the player
	 */
	public Position getInitialPlayerPosition(int gameHeight, int gameWidth) {
		Position pos = null;
		int y = gameHeight - this.tileHeight;
		boolean initialPositionFound = false;
		int level = this.loadedLevel.size() - 1;
		while(level >= 0 && !initialPositionFound) {
			String line = this.loadedLevel.get(level);
			int x = 0;
			while(x < line.length() && !initialPositionFound) {
				
				//If the current tile is the player
				if(line.charAt(x) == EnumTiles.Player.charRepresentation) {
					pos = new Position(x * this.tileWidth, y);
					initialPositionFound = true;
				}
				x++;
			}
			y -= this.tileHeight;
			level--;
		}
		
		//If the player position is not visible on the screen, the tiles are scrolling
		
		while(((pos.getX() + this.offsetX) > (gameWidth / 2)) && !this.maximumScrollReached(gameWidth)) {
			this.scrollX(1);
		}
		
		pos.addToX(this.offsetX);
		
		while(((pos.getY() + this.offsetY) < (gameHeight / 3))) {
			this.scrollY(1);
		}
		
		pos.addToY(this.offsetY);
		
		return pos;
	}
	
	/**
	 * Scrolls the level tiles on the x axis
	 * @param direction the direction of the scroll
	 */
	public void scrollX(int direction) {
		if(direction == EnumMovements.RIGHT.value) { //Moves to the right
			this.offsetX -= Character.MOVING_SPEED;
		} else if(direction == EnumMovements.LEFT.value) { //Moves to the left
			this.offsetX += Character.MOVING_SPEED;
		}
	}
	
	/**
	 * Scrolls the level tiles on the y axis
	 * @param direction the direction of the scroll
	 */
	public void scrollY(int direction) {
		if(direction > 0) { //Moves Up
			this.offsetY += Character.JUMPING_SPEED;
		} else if(direction < 0) { //Moves Down
			this.offsetY -= Character.JUMPING_SPEED;
		}
	}
	
	/**
	 * Returns the value of the offset on the x axis
	 * @return the value of the offset on the x axis
	 */
	public int getOffsetX() {
		return this.offsetX;
	}
	
	/**
	 * Returns the value of the offset on the y axis
	 * @return the value of the offset on the y axis
	 */
	public int getOffsetY() {
		return this.offsetY;
	}
	
	/**
	 * Allows us to know if we've reached the edge of the level
	 * @param gameWidth the width of the screen
	 * @return <tt>true</tt> if the end is reached, <tt>false</tt> else
	 */
	public boolean maximumScrollReached(int gameWidth) {
		int xMax = 0;
		for(int line = 0; line < this.loadedLevel.size(); line++) {
			for(int x = 0; x < this.loadedLevel.get(line).length(); x++) {
				if(this.loadedLevel.get(line).charAt(x) == 'x') {
					if(((x * this.tileWidth) + this.offsetX) > xMax)
						xMax = (x * this.tileWidth) + this.offsetX;
				}
			}
		}
		
		return (xMax <= gameWidth);
	}
	
	@Override
	/**
	 * Gets the String representation of a level
	 * @return the level with String format
	 */
	public String toString() {
		return this.loadedLevel.toString();
	}
	
	/**
	 * Gets the 2d representation of a level
	 * @return the 2d representation of a level
	 */
	public String[] getMap() {
		String level = this.toString();
		
		//Taking the brackets off the string
		level = level.substring(1);
		level = level.substring(0, level.length() - 1);
		
		String[] layers = level.split(",");
		
		return layers;
	}

	/**
	 * Saves the position of all the collected strawberries
	 * @param set the list containing the position of the strawberries
	 */
	public void registerCollectedStrawberries(Set<Integer> set) {
		this.strawberriesCollectedPositions.addAll(set);
	}

}
