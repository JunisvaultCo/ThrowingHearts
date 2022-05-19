/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package throwinghearts;

import java.awt.image.BufferedImage;
import sidescrollingshooter.ProjectilePlan;
import sidescrollingshooter.ShooterHolder;

/**
 *
 * @author Jul
 */
public class NPC extends ShooterHolder
{
  public static final int HOSTILE = 1;
  public static final int CALM = 2;
  public static final int FEARING =3;
  public int speedSeen;
  public long stopTime;
  public double pixMoved;
  public boolean isStopped;
  public boolean seenPlayer;
  public int state;
  public int lineOfSight;
      
  public NPC(BufferedImage bi,BufferedImage flipped,int x,int y,int h,
     int l, String img,String imgF,int speed,int st, int sight,long rt,
     boolean hasInfiniteAmmo, int ammo,ProjectilePlan plan,int offx,int offy, int speedSeen)
  {
    super(bi,flipped,x,y,h,l,img,imgF,speed,rt,hasInfiniteAmmo,ammo,plan,offx,offy);
    this.speedSeen=speedSeen;
    isStopped=false;
    pixMoved= 0;
    state =st;
    lineOfSight= sight;
    seenPlayer=false;
    lastTrigger = System.nanoTime();
  }
}
    