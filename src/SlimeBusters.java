/* SLIME BUSTERS
* A fun one player game where you capture slimes in order to rescue the princess
* @author James Lee and Bellamy Too
* @version January 22, 2012
*/

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import hsa.*;
import javax.swing.Timer;

// The "SlimeBusters" class.
public class SlimeBusters extends JFrame
{
    // Screen size
    final int SCREEN_WIDTH = 600;
    final int SCREEN_HEIGHT = 600;

    // Font
    Font font1 = new Font ("Arial", Font.ITALIC, 20);

    // Images for each screen and selection
    boolean atMenu = false;
    Image menuScreen = new ImageIcon ("../Images/menuscreen.gif").getImage ();
    boolean instructions = false;
    Image instructionScreen = new ImageIcon ("../Images/instructions.gif").getImage ();
    boolean store = false;
    Image storeScreen = new ImageIcon ("../Images/store.gif").getImage ();
    boolean bestiary = false;
    Image bestiaryScreen = new ImageIcon ("../Images/bestiary.gif").getImage ();
    boolean introduction = true;
    Image introductionScreen = new ImageIcon ("../Images/introduction.gif").getImage ();
    Image background = new ImageIcon ("../Images/slimetube.jpg").getImage ();

    // Achievements
    boolean achievements = false;
    int[] achieved = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    Image achievementScreen = new ImageIcon ("../Images/achievements.gif").getImage ();
    Image splatter = new ImageIcon ("../Images/splatter.gif").getImage ();
    String[] achievementName = new String [20];
    String[] required = new String [20];
    int achievementCounter = 0;
    int achievementNumber = 0;
    boolean achievementInfo;

    // Game start and game end
    boolean newGame = false;
    boolean gameOver = false;
    Image gameOverScreen = new ImageIcon ("../Images/gameover.gif").getImage ();
    boolean gameWon = false;
    Image gameWonScreen = new ImageIcon ("../Images/gamewon.gif").getImage ();
    boolean caughtMotherSlime = false;
    Image cage = new ImageIcon ("../Images/cage.gif").getImage ();
    Image princess = new ImageIcon ("../Images/princess.gif").getImage ();
    boolean resetGame = true;

    // Hero
    Image hero = new ImageIcon ("../Images/hero0.gif").getImage ();
    final int heroSpawnX = SCREEN_WIDTH / 2;
    final int heroSpawnY = SCREEN_HEIGHT / 2;
    int heroX = heroSpawnX;
    int heroY = heroSpawnY;
    final int startingStamina = 400;
    int stamina = startingStamina;
    int baseMovementSpeed = 3;
    int movementSpeed = baseMovementSpeed;
    int movementCounter = 0;
    int swingCooldown = 0;
    boolean swing = false;

    // Score and cash
    int bestSwing = 0;
    int combo = 0;
    int scoreToBeAdded = 0;
    int cash = 0;
    int score = 0;

    // Mouse
    int mouseX, mouseY;

    // Slime information
    Image currentSlime;
    String[] slimes = {"greenSlime", "greenSlime", "greenSlime", "greenSlime", "greenSlime", "greenSlime", "greenSlime",
	"acidSlime", "acidSlime", "acidSlime", "stickySlime", "stickySlime", "stickySlime", "mediumSlime", "mediumSlime",
	"mediumSlime", "giantSlime", "sharpShooterSlime", "mutantSlime", "motherSlime"};
    int[] slimeX = new int [20];
    int[] slimeY = new int [20];
    int[] splatterX = new int [7];
    int[] splatterY = new int [7];
    int[] slimeType = {5, 5, 5, 5, 5, 5, 5, 10, 10, 10, 15, 15, 15, 25, 25, 25, 50, 60, 100, 240};
    int[] slimeDirection = new int [20];
    int[] slimeMovementCounter = new int [20];
    int[] slimeSpeed = {5, 5, 5, 5, 5, 5, 5, 7, 7, 7, 4, 4, 4, 10, 10, 10, 17, 5, 17, 20};
    boolean spawnSlime = false;

    // Slime abilities
    Rectangle[] slimeAbilities = new Rectangle [7];
    boolean inAcid = false;
    int acidDamage = 1;
    boolean inSticky = false;
    int slowSpeed = 10;
    Image stickySplatter = new ImageIcon ("../Images/stickyslimesplatter.gif").getImage ();
    Image acidSplatter = new ImageIcon ("../Images/acidslimesplatter.gif").getImage ();
    Image mutantSplatter = new ImageIcon ("../Images/mutantslimesplatter.gif").getImage ();
    boolean stickySplatterTrue = false;
    boolean acidSplatterTrue = false;
    boolean mutantSplatterTrue = false;
    boolean engulfed;

    // Items/Upgrades
    final int[] bucketPrice = {100, 200, 400, 800};
    final int[] sprintPrice = {100, 175, 250, 325, 400};
    final int[] suitPrice = {100, 200, 300, 400, 500};
    final int[] medicalKitPrice = {150, 300, 450};
    int currentBucket = 1;
    int allowedToCatch = 15;
    int currentSprint = 0;
    int currentSuit = 0;
    int currentMedicalKit = 0;
    Image bucket;
    Image medicalKit;
    int bucketX = heroX + hero.getWidth (null) - 10;
    int bucketY = heroY + hero.getHeight (null) - 3;
    int healCounter = 0;
    int healSpeed = 250;

    // Timer
    boolean timerOn = false;
    int time = 0;
    int delayTime = 0;

    public SlimeBusters ()
    {
	super ("Slime Busters");

	// Location
	setLocation (100, 50);

	// Set up for the game
	Container contentPane = getContentPane ();
	contentPane.add (new DrawingPanel (), BorderLayout.CENTER);
    }


    // Inner class for the drawing area
    private class DrawingPanel extends JPanel
    {
	private Timer timer;
	private boolean timerOn;
	private int time;
	private int spawnOverTimeCounter;

	/** Constructs a new DrawingPanel object
	  */
	public DrawingPanel ()
	{
	    // Add mouse listeners  to the drawing panel
	    this.addMouseListener (new MouseHandler ());
	    this.addMouseMotionListener (new MouseMotionHandler ());

	    setPreferredSize (new Dimension (SCREEN_WIDTH, SCREEN_HEIGHT));
	    setResizable (false);

	    // Keyboard input
	    this.setFocusable (true);
	    this.addKeyListener (new KeyHandler ());
	    this.requestFocusInWindow ();

	    timer = new Timer (10, new TimerEventHandler ());
	}

	// An inner class to deal with the timer events
	private class TimerEventHandler implements ActionListener
	{
	    public void actionPerformed (ActionEvent event)
	    {
		// Run all of the timer events, cause the hero to move, all the slimes to move etc.
		// They are in the timer method to maintain a constant speed no matter how
		// fast someone's computer is
		if (timerOn = true)
		{
		    time++;

		    for (int i = 0 ; i < 5 ; i++)
		    {
			slimeAI ();
			moveHero ();

			if (swing)
			{
			    if (swingCooldown > 75)
			    {
				swingCooldown--;
				bucketX--;
			    }

			    if (swingCooldown > 0 && swingCooldown <= 75)
			    {
				swingCooldown--;
				bucketX++;
			    }
			}

			paintImmediately (0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		    }

		    updateStamina ();

		    if (time % 1 == 0)
		    {
			inAcid = false;
			inSticky = false;
		    }
		}
	    }
	}

	/* Swings the bucket for the player, capturing any slimes in its range
	* @param g the graphics
	*/
	public void swingBucket (Graphics g)
	{
	    Rectangle bucketSwing = new Rectangle (bucketX, bucketY, bucket.getWidth (null), bucket.getHeight (null));
	    Rectangle[] slimeToCheck = new Rectangle [slimeX.length];

	    for (int i = 0 ; i < slimeType.length ; i++)
	    {
		currentSlime = new ImageIcon ("../Images/" + slimes [i] + ".gif").getImage ();

		slimeToCheck [i] = new Rectangle (slimeX [i], slimeY [i],
			currentSlime.getWidth (null), currentSlime.getHeight (null));

		// Check collision between bucket and slime
		if (bucketSwing.intersects (slimeToCheck [i]) && !caughtMotherSlime && allowedToCatch >= slimeType [i])
		{
		    scoreToBeAdded += slimeType [i];


		    if (slimeType [i] != 240)
		    {
			slimeReset (i);
		    }

		    combo++;
		    achievements (i);

		    // If the slime caught was the mother slime
		    if (slimeType [i] == 240)
		    {
			caughtMotherSlime = true;
		    }
		}

		// Add to the score and cash, and also record to achievements
		if (swingCooldown == 0)
		{
		    swing = false;
		    cash += scoreToBeAdded * combo;
		    score += scoreToBeAdded * combo;


		    if (scoreToBeAdded * combo > bestSwing)
			bestSwing = scoreToBeAdded * combo;

		    combo = 0;
		    scoreToBeAdded = 0;
		}
	    }
	}

	/** Move the images
	* @param image the image to move
	*/
	public void moveHero ()
	{
	    movementSpeed = baseMovementSpeed - currentSprint / 2;

	    // Follow cursor if not swinging bucket, not engulfed, and not in a sticky splatter
	    if (!swing && !engulfed && !inSticky)
	    {
		if (movementCounter == movementSpeed)
		{
		    if (heroX + hero.getWidth (null) / 2 < mouseX && (heroX + hero.getWidth (null)) < SCREEN_WIDTH)
		    {
			heroX++;
		    }
		    else if (heroX + hero.getWidth (null) / 2 > mouseX && heroX > 0)
		    {
			heroX--;
		    }
		    if (heroY + hero.getHeight (null) / 2 < mouseY && (heroY + hero.getHeight (null)) < SCREEN_HEIGHT)
		    {
			heroY++;
		    }
		    else if (heroY + hero.getHeight (null) / 2 > mouseY && heroY > 0)
		    {
			heroY--;
		    }
		}

		// Slow down the players movement speed
		if (movementCounter < movementSpeed)
		    movementCounter++;
		else
		    movementCounter = 0;

		// Match the bucket coordinates to the hero
		bucketX = heroX + hero.getWidth (null) - 10;
		bucketY = heroY + hero.getHeight (null) - 3;
	    }

	    // If the player is engulfed
	    if (engulfed)
	    {
		heroX = slimeX [16] + 5;
		heroY = slimeY [16] + 20;
	    }
	}


	/** Updates the player's stamina, also allows them to take damage
	*/
	public void updateStamina ()
	{
	    Rectangle heroSize = new Rectangle (heroX + 12, heroY + 4, 30, 11);
	    Rectangle[] slimeToCheck = new Rectangle [slimeX.length];

	    // Heal the player
	    if (stamina < 400 && currentMedicalKit > 0)
	    {
		if (healCounter < healSpeed - currentMedicalKit * 50)
		    healCounter += 10;
		else
		{
		    stamina++;
		    healCounter = 0;
		}
	    }

	    // Take damage from contact with the slimes
	    for (int i = 0 ; i < 20 ; i++)
	    {
		slimeToCheck [i] = new Rectangle (slimeX [i], slimeY [i],
			new ImageIcon ("../Images/" + slimes [i] + ".gif").getImage ().getWidth (null),
			new ImageIcon ("../Images/" + slimes [i] + ".gif").getImage ().getHeight (null));

		slimeToCheck [16] = new Rectangle (slimeX [16] + 15, slimeY [16] + 15, 30, 30);

		if (heroSize.intersects (slimeToCheck [i]) && time % 15 == 0)
		{
		    if (!caughtMotherSlime)
		    {
			if (slimeType [i] > 100)
			{
			    stamina -= (50 - currentSuit);
			}
			else if (slimeType [i] > 25)
			{
			    stamina -= (20 - currentSuit);
			    if (engulfed)
				stamina -= (15 - currentSuit);
			}
			else if (slimeType [i] > 5)
			{
			    stamina -= (15 - currentSuit);
			}
			if (slimeType [i] == 50)
			{
			    engulfed = true;
			}
		    }
		}
	    }

	    // Lose the game if player has no more stamina left
	    if (stamina <= 0)
	    {
		gameOver = true;
	    }

	    // Lose hp if the player is in acid
	    if (inAcid)
		stamina--;
	}

	/* Draws the players stamina
	* @param g the graphics
	*/
	public void drawStamina (Graphics g)
	{
	    g.setColor (Color.black);
	    g.fillRect (90, 21, 420, 22);
	    g.setColor (Color.red);
	    g.fillRect (100, 25, stamina, 15);
	}


	/** rotates an image
	*@param g the graphics
	*/
	public void rotation (Graphics g)
	{
	    if (!engulfed)
	    {
		// We need a Graphics2D object for rotate
		Graphics2D g2D = (Graphics2D) g;

		// Calculate the angle in relation to the mouse
		double angle = Math.abs (Math.asin ((mouseY - (heroY + hero.getHeight (null) / 2)) /
			    Math.sqrt ((mouseX - (heroX + hero.getWidth (null) / 2)) *
				(mouseX - (heroX + hero.getWidth (null) / 2)) +
				(mouseY - (heroY + hero.getHeight (null) / 2)) *
				(mouseY - (heroY + hero.getHeight (null) / 2)))));

		// Four basic angles
		if (mouseX == heroX + hero.getWidth (null) / 2 && mouseY < heroY + hero.getHeight (null) / 2)
		    angle = Math.PI;
		else if (mouseX == heroX + hero.getWidth (null) / 2 && mouseY >= heroY + hero.getHeight (null) / 2)
		    angle = 0;
		else if (mouseY == heroY + hero.getHeight (null) / 2 && mouseX < heroX + hero.getWidth (null) / 2)
		    angle = -1 * (Math.PI / 2);
		else if (mouseY == heroY + hero.getHeight (null) / 2 && mouseX > heroX + hero.getWidth (null) / 2)
		    angle = Math.PI / 2;

		// Other angles
		if (mouseY < heroY + hero.getHeight (null) / 2 && mouseX > heroX + hero.getWidth (null) / 2)
		    angle += Math.PI / 2;
		else if (mouseY < heroY + hero.getHeight (null) / 2 && mouseX < heroX + hero.getWidth (null) / 2)
		    angle = -1 * angle - (Math.PI / 2);
		else if (mouseY > heroY + hero.getHeight (null) / 2 && mouseX > heroX + hero.getWidth (null) / 2)
		    angle = -1 * angle + (Math.PI / 2);
		else if (mouseY > heroY + hero.getHeight (null) / 2 && mouseX < heroX + hero.getWidth (null) / 2)
		    angle -= Math.PI / 2;

		// Rotate the hero
		g2D.rotate (-angle, heroX + hero.getWidth (null) / 2, heroY + hero.getHeight (null) / 2);
		g2D.drawImage (hero, heroX, heroY, this);
		g2D.drawImage (bucket, bucketX, bucketY, this);
		g2D.rotate (angle, heroX + hero.getWidth (null) / 2, heroY + hero.getHeight (null) / 2);
	    }
	}

	/* Resets the position and the direction of the slime
	* @param whichSlime which slime to reset
	*/
	public void slimeReset (int whichSlime)
	{
	    // Assign the direction that the slime will be moving (0 = left to right, 1 = right to left,
	    // 2 = up to down, 3 = down to up, 4 = follow player)
	    slimeDirection [whichSlime] = (int) (Math.random () * 5);

	    // Assign starting coordinates for slimes
	    if (slimeDirection [whichSlime] == 0 || slimeDirection [whichSlime] == 4)
	    {
		slimeX [whichSlime] = (int) (Math.random () * -200 - 100);
		slimeY [whichSlime] = (int) (Math.random () * 600);
	    }
	    else if (slimeDirection [whichSlime] == 1)
	    {
		slimeX [whichSlime] = (int) (Math.random () * 100 + 600);
		slimeY [whichSlime] = (int) (Math.random () * 600);
	    }
	    else if (slimeDirection [whichSlime] == 2)
	    {
		slimeY [whichSlime] = (int) (Math.random () * -200 - 100);
		slimeX [whichSlime] = (int) (Math.random () * 600);
	    }
	    else if (slimeDirection [whichSlime] == 3)
	    {
		slimeY [whichSlime] = (int) (Math.random () * 100 + 600);
		slimeX [whichSlime] = (int) (Math.random () * 600);
	    }
	    spawnSlime = false;
	}

	/* Causes the slimes to move based on their directions
	*/
	public void slimeAI ()
	{
	    for (int i = 0 ; i < slimeType.length - 1 ; i++)
	    {
		// Movement of slimes (0 = left to right, 1 = right to left,
		// 2 = up to down, 3 = down to up, 4 = follow player)
		if (slimeMovementCounter [i] == slimeSpeed [i])
		{
		    if (slimeDirection [i] == 0)
		    {
			if (slimeX [i] < 700)
			    slimeX [i]++;
			if (slimeX [i] >= 700)
			    slimeReset (i);
		    }
		    else if (slimeDirection [i] == 1)
		    {
			if (slimeX [i] > -100)
			    slimeX [i]--;
			if (slimeX [i] <= -100)
			    slimeReset (i);
		    }
		    else if (slimeDirection [i] == 2)
		    {
			if (slimeY [i] < 700)
			    slimeY [i]++;
			if (slimeY [i] >= 700)
			    slimeReset (i);
		    }
		    else if (slimeDirection [i] == 3)
		    {
			if (slimeY [i] > -100)
			    slimeY [i]--;
			if (slimeY [i] <= -100)
			    slimeReset (i);
		    }
		    else if (slimeDirection [i] == 4)
		    {
			if (heroX + hero.getWidth (null) / 2 < slimeX [i] + 10)
			{
			    slimeX [i]--;
			}

			else if (heroX + hero.getWidth (null) / 2 > slimeX [i] + 10)
			{
			    slimeX [i]++;
			}

			if (heroY + hero.getHeight (null) < slimeY [i] + 5)
			{
			    slimeY [i]--;
			}

			else if (heroY + hero.getHeight (null) > slimeY [i] + 5)
			{
			    slimeY [i]++;
			}
		    }
		    slimeMovementCounter [i] = 0;
		}
		else if (slimeMovementCounter [i] < slimeSpeed [i])
		{
		    slimeMovementCounter [i]++;
		}
	    }
	}


	/* Draws the slimes on the screen
	* @param g the graphics
	*/
	public void slimeDraw (Graphics g)
	{
	    // Drop a splatter at this time
	    if (time % 150 == 0 && time != 0)
	    {
		mutantSplatterTrue = true;
		stickySplatterTrue = true;
		acidSplatterTrue = true;

		splatterX [6] = slimeX [18];
		splatterY [6] = slimeY [18];

		for (int s = 0 ; s < 3 ; s++)
		{
		    splatterX [s] = slimeX [s + 10];
		    splatterY [s] = slimeY [s + 10];
		}

		for (int a = 0 ; a < 3 ; a++)
		{
		    splatterX [a + 3] = slimeX [a + 7];
		    splatterY [a + 3] = slimeY [a + 7];
		}
	    }

	    // Collision with splatter
	    if (mutantSplatterTrue == true)
	    {
		g.drawImage (mutantSplatter, splatterX [6], splatterY [6], null);
		Rectangle heroSize = new Rectangle (heroX + 12, heroY + 4, 30, 11);
		Rectangle mutantSplatterSize = new Rectangle (splatterX [6] + 15, splatterY [6] + 10, 65, 65);
		if (heroSize.intersects (mutantSplatterSize))
		{
		    inAcid = true;
		    inSticky = true;
		}
	    }

	    Rectangle[] stickySplatterSize = new Rectangle [3];

	    // Collision with splatter
	    if (stickySplatterTrue)
	    {
		Rectangle heroSize = new Rectangle (heroX + 12, heroY + 4, 30, 11);

		for (int k = 0 ; k < 3 ; k++)
		{
		    g.drawImage (stickySplatter, splatterX [k], splatterY [k], null);
		    stickySplatterSize [k] = new Rectangle (splatterX [k] + 4, splatterY [k] + 4, 10, 10);
		    if (heroSize.intersects (stickySplatterSize [k]))
		    {
			inSticky = true;
		    }
		}
	    }

	    Rectangle[] acidSplatterSize = new Rectangle [3];

	    // Collision with splatter
	    if (acidSplatterTrue)
	    {
		Rectangle heroSize = new Rectangle (heroX + 12, heroY + 4, 30, 11);

		for (int j = 0 ; j < 3 ; j++)
		{
		    g.drawImage (acidSplatter, splatterX [j + 3], splatterY [j + 3], null);
		    acidSplatterSize [j] = new Rectangle (splatterX [j + 3], splatterY [j + 3],
			    acidSplatter.getWidth (null), acidSplatter.getHeight (null));
		    if (heroSize.intersects (acidSplatterSize [j]))
		    {
			inAcid = true;
		    }
		}
	    }

	    // Show the images for each slime
	    for (int i = 0 ; i < slimeType.length ; i++)
	    {
		currentSlime = new ImageIcon ("../Images/" + slimes [i] + ".gif").getImage ();
		g.drawImage (currentSlime, slimeX [i], slimeY [i], null);
	    }
	}


	/* The final encounter with the boss slime,at 30000 points
	* @param g the graphics
	*/
	public void motherSlime (Graphics g)
	{
	    slimeDirection [19] = 4;

	    // Show the image for mother slime if she has not been caught yet
	    if (!caughtMotherSlime)
	    {
		g.drawImage (new ImageIcon ("../Images/motherSlime.gif").getImage (), slimeX [19], slimeY [19], null);
		delayTime = time;
	    }

	    // Move mother slime if she has not been caught yet
	    if (!caughtMotherSlime)
	    {
		if (heroX + hero.getWidth (null) / 2 < slimeX [19] + 52)
		{
		    slimeX [19]--;
		}

		else if (heroX + hero.getWidth (null) / 2 > slimeX [19] + 52)
		{
		    slimeX [19]++;
		}

		if (heroY + hero.getHeight (null) < slimeY [19] + 52)
		{
		    slimeY [19]--;
		}

		else if (heroY + hero.getHeight (null) > slimeY [19] + 52)
		{
		    slimeY [19]++;
		}
	    }

	    // Show the princess in her cage
	    if (caughtMotherSlime)
	    {
		g.drawImage (cage, slimeX [19] + 25, slimeY [19] + 25, null);
		g.drawImage (princess, slimeX [19] + 25, slimeY [19] + 25, null);

		if (time == delayTime + 30)
		{
		    gameWon = true;
		}
	    }
	}


	/* Checks is each achievement is unlocked
	* @param whichSlime checks which slime was captured in order to give the achievement
	*/
	public void achievements (int whichSlime)
	{
	    // First row (score based achievements)
	    if (score >= 100)
		achieved [0] = 1;
	    if (score >= 250)
		achieved [1] = 1;
	    if (score >= 500)
		achieved [2] = 1;
	    if (score >= 1000)
		achieved [3] = 1;
	    if (score >= 2000)
		achieved [4] = 1;

	    // Second row (combo based achievements)
	    if (combo == 2)
		achieved [5] = 1;
	    if (combo == 3)
		achieved [6] = 1;
	    if (combo == 4)
		achieved [7] = 1;
	    if (combo == 5)
		achieved [8] = 1;

	    // Second and third row (points in one swing based achievements)
	    if (bestSwing >= 50)
		achieved [9] = 1;
	    if (bestSwing >= 100)
		achieved [10] = 1;
	    if (bestSwing >= 200)
		achieved [11] = 1;

	    // Fourth row (slimes caught achievements)
	    if (slimeType [whichSlime] == 5)
		achieved [12] = 1;
	    if (slimeType [whichSlime] == 10)
		achieved [13] = 1;
	    if (slimeType [whichSlime] == 15)
		achieved [14] = 1;
	    if (slimeType [whichSlime] == 25)
		achieved [15] = 1;
	    if (slimeType [whichSlime] == 30)
		achieved [16] = 1;
	    if (slimeType [whichSlime] == 50)
		achieved [17] = 1;
	    if (slimeType [whichSlime] == 100)
		achieved [18] = 1;
	    if (slimeType [whichSlime] == 250)
		achieved [19] = 1;
	}


	/** Repaint the drawing panel
	  * @param g The Graphics context
	  */
	public void paintComponent (Graphics g)
	{
	    super.paintComponent (g);

	    // Reset the game
	    if (resetGame)
	    {
		for (int i = 0 ; i < 20 ; i++)
		{
		    slimeReset (i);
		}
		resetGame = false;
	    }

	    // Introduction screen
	    if (introduction)
	    {
		g.drawImage (introductionScreen, 0, 0, null);
	    }

	    // Menu screen
	    if (atMenu)
	    {
		g.drawImage (menuScreen, 0, 0, null);
		timer.stop ();
	    }

	    // Intruction screen
	    if (instructions)
	    {
		g.drawImage (instructionScreen, 0, 0, null);
	    }

	    // Store screen
	    if (store)
	    {
		g.drawImage (storeScreen, 0, 0, null);

		// Upgrades
		setFont (new Font ("Comic Sans MS", Font.BOLD, 20));
		g.drawString ("Bucket Upgrade     " + currentBucket, 50, 120);

		if (currentBucket <= 4)
		    g.drawString (" --> " + (currentBucket + 1) + "     $"
			    + bucketPrice [currentBucket - 1] + "   BUY   ", 260, 120);
		else
		    g.drawString ("MAXED!", 300, 120);

		g.drawString ("Sprint Upgrade     " + currentSprint, 50, 220);

		if (currentSprint <= 4)
		    g.drawString (" --> " + (currentSprint + 1) + "     $"
			    + sprintPrice [currentSprint] + "   BUY   ", 255, 220);
		else
		    g.drawString ("MAXED!", 300, 220);

		g.drawString ("Slime Suit Upgrade     " + currentSuit, 50, 320);

		if (currentSuit <= 4)
		    g.drawString (" --> " + (currentSuit + 1) + "     $"
			    + suitPrice [currentSuit] + "   BUY   ", 295, 320);
		else
		    g.drawString ("MAXED!", 350, 320);

		g.drawString ("Medical Kit Upgrade     " + currentMedicalKit, 50, 420);

		if (currentMedicalKit <= 2)
		    g.drawString (" --> " + (currentMedicalKit + 1) + "     $"
			    + medicalKitPrice [currentMedicalKit] + "   BUY   ", 310, 420);
		else
		    g.drawString ("MAXED!", 350, 420);

		g.drawString ("Cash: $" + String.valueOf (cash), 50, 500);
	    }

	    // Achievement screen
	    if (achievements)
	    {
		g.drawImage (achievementScreen, 0, 0, null);

		File nameAndRequired = new File ("../Images/achievements.txt");

		// Block out the achievements that the player has not achieved yet
		for (int j = 1 ; j < 5 ; j++)
		    for (int i = 0 ; i < 5 ; i++)
		    {
			if (achieved [achievementCounter] == 0 && i <= 4)
			    g.drawImage (splatter, i * 110, j * 100, null);

			achievementName [achievementCounter] = nameAndRequired.readLine ();
			required [achievementCounter] = nameAndRequired.readLine ();

			achievementCounter++;
		    }

		achievementCounter = 0;

		if (achievementInfo)
		{
		    g.drawString (achievementName [achievementNumber], 20, 560);
		    setFont (font1);
		    g.drawString (required [achievementNumber], 20, 585);
		    achievementInfo = false;
		}
	    }

	    // Bestiary screen
	    if (bestiary)
	    {
		g.drawImage (bestiaryScreen, 0, 0, null);
	    }

	    // Main game screen
	    if (newGame)
	    {
		g.drawImage (background, 0, 0, this);

		// Mother slime
		if (score >= 5000)
		{
		    motherSlime (g);
		}

		// If player swings it will re draw the bucket along its path
		if (swing)
		{
		    swingBucket (g);
		}

		// Draws the score on the background
		setFont (new Font ("Comic Sans MS", Font.BOLD, 36));
		g.setColor (Color.BLACK);
		g.drawString (String.valueOf (cash), SCREEN_WIDTH - 120, SCREEN_HEIGHT - 50);
		g.drawString ("$", SCREEN_WIDTH - 155, SCREEN_HEIGHT - 50);

		// Draws the medical kit
		if (currentMedicalKit > 0)
		{
		    g.drawImage (medicalKit, 20, 20, this);
		}

		// Draws the splatters and the slimes
		slimeDraw (g);

		// Draws the hero and the rotating image of the hero
		rotation (g);

		// Will update the hero's stamina and display the correct amount of stamina
		drawStamina (g);

		// Time based events
		timer.start ();
	    }

	    // Game over
	    if (gameOver)
		g.drawImage (gameOverScreen, 0, 0, null);
	    if (gameWon)
		g.drawImage (gameWonScreen, 0, 0, null);
	} // paint component method
    }


    /** Monitors mouse movement over the game panel and responds
	    */
    private class MouseHandler extends MouseAdapter
    {
	/** Responds to mouse clicks
	* @param event the event created by the mouse click
	*/
	public void mousePressed (MouseEvent event)
	{
	    Point pressed = event.getPoint ();

	    // Menu options
	    if (pressed.x > 300 && pressed.x < 450 && pressed.y < 220 && pressed.y > 170 && atMenu == true)
	    {
		newGame = true;
		instructions = false;
		store = false;
		achievements = false;
		bestiary = false;
		atMenu = false;

		hero = new ImageIcon ("../Images/hero" + currentSuit + ".gif").getImage ();
		bucket = new ImageIcon ("../Images/bucket" + currentBucket + ".gif").getImage ();

		if (currentMedicalKit > 0)
		    medicalKit = new ImageIcon ("../Images/medicalKit" + currentMedicalKit + ".gif").getImage ();

		for (int i = 0 ; i < 7 ; i++)
		{
		    splatterX [i] = -100;
		    splatterY [i] = -100;
		}
	    }
	    else if (pressed.x > 330 && pressed.x < 550 && pressed.y < 285 && pressed.y > 230 && atMenu == true)
	    {
		instructions = true;
		atMenu = false;
	    }
	    else if (pressed.x > 330 && pressed.x < 440 && pressed.y < 340 && pressed.y > 290 && atMenu == true)
	    {
		store = true;
		atMenu = false;
	    }
	    else if (pressed.x > 330 && pressed.x < 580 && pressed.y < 400 && pressed.y > 350 && atMenu == true)
	    {
		achievements = true;
		atMenu = false;
	    }
	    else if (pressed.x > 330 && pressed.x < 470 && pressed.y < 450 && pressed.y > 415 && atMenu == true)
	    {
		bestiary = true;
		atMenu = false;
	    }

	    // Back buttons
	    if (pressed.x > 490 && pressed.x < 580 && pressed.y < 580 && pressed.y > 545 && instructions == true)
	    {
		instructions = false;
		atMenu = true;
	    }
	    if (pressed.x > 490 && pressed.x < 580 && pressed.y < 580 && pressed.y > 545 && store == true)
	    {
		store = false;
		atMenu = true;
	    }
	    if (pressed.x > 490 && pressed.x < 580 && pressed.y < 580 && pressed.y > 545 && achievements == true)
	    {
		achievements = false;
		atMenu = true;
	    }
	    if (pressed.x > 490 && pressed.x < 580 && pressed.y < 580 && pressed.y > 545 && bestiary == true)
	    {
		bestiary = false;
		atMenu = true;
	    }

	    // Buy Upgrades at store
	    if (store)
	    {
		if (pressed.x > 445 && pressed.x < 495 && pressed.y > 100
			&& pressed.y < 125 && currentBucket <= 4 && cash - bucketPrice [currentBucket - 1] >= 0)
		{
		    currentBucket++;
		    cash -= bucketPrice [currentBucket - 2];
		    allowedToCatch = allowedToCatch * 2;
		}

		else if (pressed.x > 440 && pressed.x < 490 && pressed.y > 200
			&& pressed.y < 225 && currentSprint <= 4 && cash - sprintPrice [currentSprint] >= 0)
		{
		    currentSprint++;
		    cash -= sprintPrice [currentSprint - 1];
		}
		else if (pressed.x > 480 && pressed.x < 530 && pressed.y > 300
			&& pressed.y < 325 && currentSuit <= 4 && cash - suitPrice [currentSuit] >= 0)
		{
		    currentSuit++;
		    cash -= suitPrice [currentSuit - 1];
		}
		else if (pressed.x > 490 && pressed.x < 540 && pressed.y > 400
			&& pressed.y < 425 && currentMedicalKit <= 2 && cash - medicalKitPrice [currentMedicalKit] >= 0)
		{
		    currentMedicalKit++;
		    cash -= medicalKitPrice [currentMedicalKit - 1];
		}
	    }

	    // Swing bucket
	    if (pressed.x > 0 && pressed.x < SCREEN_WIDTH && pressed.y > 0 &&
		    pressed.y < SCREEN_HEIGHT && newGame == true && swingCooldown == 0)
	    {
		swingCooldown = 150;
		swing = true;
	    }

	    repaint ();
	}
    }


    private class MouseMotionHandler extends MouseMotionAdapter
    {
	/** Responds to mouse-movement inputs
	*@paramevent   The event created by the mouse movement
	*/
	public void mouseMoved (MouseEvent event)
	{

	    Point mousePosition = event.getPoint ();
	    repaint (); //Repaint the screen to show any changes
	    mouseX = mousePosition.x;
	    mouseY = mousePosition.y;

	    if (achievements)
	    {
		for (int j = 1 ; j < 5 ; j++)
		    for (int i = 0 ; i < 5 ; i++)
		    {
			if (mouseX > i * 100 && mouseX < (i + 1) * 110 && mouseY > j * 100
				&& mouseY < (j + 1) * 100 && achieved [achievementCounter] == 1)
			{
			    achievementNumber = achievementCounter;
			    achievementInfo = true;
			}
			achievementCounter++;
		    }

		achievementCounter = 0;
	    }
	}
    }


    // Inner class to handle key events
    private class KeyHandler extends KeyAdapter
    {
	public void keyPressed (KeyEvent event)
	{
	    // Go back to menu if player pressed x
	    if (newGame)
		if (event.getKeyCode () == KeyEvent.VK_X && !gameOver)
		{
		    newGame = false;
		    atMenu = true;
		    timerOn = false;
		}

	    // Enter key (to leave intructions, or close the game at certain points)
	    if (introduction)
		if (event.getKeyCode () == KeyEvent.VK_ENTER)
		{
		    introduction = false;
		    atMenu = true;
		}
	    if (gameWon)
		if (event.getKeyCode () == KeyEvent.VK_ENTER)
		{
		    System.exit (0);
		}
	    if (gameOver)
		if (event.getKeyCode () == KeyEvent.VK_ENTER)
		{
		    atMenu = true;
		    newGame = false;
		    gameOver = false;
		    engulfed = false;
		    heroX = heroSpawnX;
		    heroY = heroSpawnY;
		    bucketX = heroX;
		    bucketY = heroY;
		    stamina = startingStamina;
		    if (currentSuit > 0)
		    {
			currentSuit--;
		    }

		    if (currentBucket > 2)
		    {
			currentBucket--;
		    }

		    if (currentMedicalKit > 0)
		    {
			currentMedicalKit--;
		    }

		    if (currentSprint > 0)
		    {
			currentSprint--;
		    }
		    resetGame = true;
		    score -= 2000;
		}
	    repaint ();
	}
    }


    public static void main (String[] args)
    {
	SlimeBusters frame = new SlimeBusters ();
	frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
	frame.pack ();
	frame.setVisible (true);

    } // main method
} // SlimeBusters class


