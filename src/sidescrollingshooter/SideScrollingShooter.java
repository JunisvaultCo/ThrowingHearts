/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sidescrollingshooter;

import throwinghearts.NPC;
import collisionrequired.Collisionable;
import collisionrequired.Quadtree;
import collisionrequired.Platform;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 *
 * @author Jul
 */
abstract public class SideScrollingShooter extends JFrame
{
  //arrays with tans for each degree between 0 and 180
  //in order to use the theory of x<y<z means tan(y)
  //is between tan(x) and tan(z) if all three belong
  //either to [0,90) or [90,180) ( no need to go up to
  //360 thanks to flipped images and the repetivity of
  //tan on each interval of (k*180,(k+1)*180) )
    
  //Used for approximating rotation because rotating
  //images might be expensive.
  public static double tansNeg[];
  public static double tansPos[];
  public static final int A = 65;
  public static final int D = 68;
  public static final int W = 87;
  public static final int LEFT = 37;
  public static final int UP = 38;
  public static final int RIGHT = 39;
  
  public static final int MONSTERHIT=1;
  public static final int PLAYERHIT=2;
  public static final int PROJECTILEHIT=3;
  public static final int PLATFORMHIT=4;
    
  public BufferedImage BI ;
  public Thread runner;
    
  public ShooterHolder player;
  public Quadtree root;
  public ArrayList<NPC> monsters;
  public ArrayList<Platform> map;
  public ArrayList<Collisionable> collis;
  public ArrayList<Projectile> projectiles;
    
  public boolean gameRunning;
  public int targetFps;
  public int fps;
  public long lastFpsTime;
  public int addX;
  public int addY;
  public int endX;
  public int endY;
  public SideScrollingShooter()
  {
   //sets the tans
   tansPos = new double[90];
   tansNeg = new double[180];
   for(int i=0;i<90;i++)
     tansPos[i]= Math.tan(Math.toRadians(i));
   for(int i=90;i<180;i++)
     tansNeg[i]= Math.tan(Math.toRadians(i));
  }
  public abstract void save();
  public abstract void doGameUpdates(double delta);
  public void doGameLoop()
  {
    long lastLoopTime = System.nanoTime();
    final long OPTIMAL_TIME = 1000000000 / targetFps;
    BI = new BufferedImage(endX, endY, BufferedImage.TYPE_INT_ARGB);

    // keep looping round til the game ends
    while (gameRunning)
    {
      // work out how long its been since the last update, this
      // will be used to calculate how far the entities should
      // move this loop
      long now = System.nanoTime();
      long updateLength = now - lastLoopTime;
      lastLoopTime = now;
      double delta = updateLength / ((double)OPTIMAL_TIME);

      // update the frame counter
      lastFpsTime += updateLength;
      fps++;
    
      // update our FPS counter if a second has passed since
      // we last recorded
      if (lastFpsTime >= 1000000000)
      {
        System.out.println("(FPS: "+fps+")");
        lastFpsTime = 0;
        fps = 0;
      }
        
      // update the game logic
      doGameUpdates(delta);
     
      // draw everyting
      repaint();
     
      // we want each frame to take 10 milliseconds, to do this
      // we've recorded when we started the frame. We add 10 milliseconds
      // to this and then factor in the current time to give 
      // us our final value to wait for
      // remember this is in ms, whereas our lastLoopTime etc. vars are in ns.
      if(gameRunning)
      {
        try{Thread.sleep( (lastLoopTime-System.nanoTime() + OPTIMAL_TIME)/1000000);}
        catch(Exception e){e.printStackTrace();}
      }
    }
  }
  //something so as to listen to what might happen
  //receives ID in order to track what event.
  public abstract boolean receiveEvent(int ID, Object... arguments);
  //what makes the image be centered on the player
  public void setCentered()
  {
    if ((player.x - (400 - player.length/2)) >= 0)
      addX = 400 - player.length/2 - (int)player.x ;
    if ((player.y - (300 - player.height/2)) >= 0)
      addY = 300 - player.height/2- (int)player.y ;
    if ((player.x - endX+(400+ player.length/2)) >= 0)
      addX = 800 - endX;
    if ((player.y - endY+(300+player.height/2)) >= 0)
      addY = 600 - endY;
  }
}
