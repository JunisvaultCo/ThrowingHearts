/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collisionrequired;

import java.awt.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author Jul
 */
public class Quadtree
{
  public Rectangle bounds;
  public Quadtree[] nodes;
  public ArrayList<Collisionable> objects;//if final node objects isn't empty
  public int level;
  public static final int MAXLEVEL = 7;
  public static final int MAXITEMS = 10;
  
  public Quadtree(int level, Rectangle bounds)
  {
    this.bounds=bounds;
    this.level=level;
    nodes = new Quadtree[4];
    objects = new ArrayList(1);
  }
  public void split()
  {
    int newHeight=(int)bounds.getHeight()/2;
    int newWidth =(int)bounds.getWidth()/2;
    int x = (int)bounds.getX();
    int y = (int)bounds.getY();
    int left1=0;
    int left2=0;
    //since rectangles use ints, it has to put the remainder somewhere
    //so that the two divisions of a rectangle actually add up to have the
    //same size as the undivided rectangle
    if(((int)bounds.getWidth())%2!=0)
      left1=1;
    if(((int)bounds.getHeight())%2!=0)
      left2=1;
    nodes[0]=new Quadtree(level+1,new Rectangle(x         ,y          ,newWidth,newHeight));
    nodes[1]=new Quadtree(level+1,new Rectangle(x         ,y+newHeight,newWidth,newHeight+left2));
    nodes[2]=new Quadtree(level+1,new Rectangle(x+newWidth,y          ,newWidth+left1,newHeight));
    nodes[3]=new Quadtree(level+1,new Rectangle(x+newWidth,y+newHeight,newWidth+left1,newHeight+left2));
  }
  public void insert(Collisionable object)
  {
    int newHeight=(int)bounds.getHeight()/2;
    int newWidth=(int)bounds.getWidth()/2;
    int bx = (int)bounds.getX();
    int by = (int)bounds.getY();
    //since rectangles use ints, it has to put the remainder somewhere
    //so that the two divisions of a rectangle actually add up to have the
    //same size as the undivided rectangle
    int remainderX = bx + (int)bounds.getWidth()-(bx+newWidth);
    int remainderY = by + (int)bounds.getHeight()-(by+newHeight);
    //helper array
    Rectangle array[] =
    {
      new Rectangle(bx         ,by          ,newWidth,newHeight),
      new Rectangle(bx         ,by+newHeight,newWidth,remainderY),
      new Rectangle(bx+newWidth,by          ,remainderX,newHeight),
      new Rectangle(bx+newWidth,by+newHeight,remainderX,remainderY)
    };
    //if it still has nodes(not leaf)
    if(nodes[0] != null)
      for(int i=0;i<4;i++)
      {
        Rectangle rect= array[i];
        int rx = (int)rect.getX();
        int ry = (int)rect.getY();
        int rw = (int)rect.getWidth();
        int rh = (int)rect.getHeight();
        boolean checkY =(int)object.y    <= ry+rh;
        boolean checkX =(int)object.x    <= rx+rw;
        boolean checkEndY=(int)object.y+object.height >= ry;
        boolean checkEndX=(int)object.x+object.length >= rx;
        if((checkEndX&&checkX)&&(checkEndY&&checkY) )
          nodes[i].insert(object);
      }
    else
    {
      if(objects.size() <MAXITEMS || level>=MAXLEVEL )
      {
        objects.add(object);
        if(object.fathers==null) object.fathers= new ArrayList<>(10);
        object.fathers.add(this);
      }
      else
      { 
        split();
        for(int i=0; i< objects.size()+1;i++)
        {
          Collisionable obj;
          if(i== objects.size()) obj = object;
          else obj = objects.get(i);
          for(int j=0;j<4;j++)
          {
            //using helper array, shortening down words
            Rectangle rect= array[j];
            int rx = (int)rect.getX();
            int ry = (int)rect.getY();
            int rw = (int)rect.getWidth();
            int rh = (int)rect.getHeight();
            boolean checkY =(int)obj.y    <= ry+rh;
            boolean checkX =(int)obj.x    <= rx+rw;
            boolean checkEndY=(int)obj.y+obj.height >= ry;
            boolean checkEndX=(int)obj.x+obj.length >= rx;
            if((checkEndX&&checkX) &&(checkEndY&&checkY) )
              nodes[j].insert(obj);
          }
        }
        objects.clear();
      }
    }
  }
  public void remove(Collisionable object)
  {
    //remove from each father
    if(object.fathers==null) return;
    for(int i=0;i<object.fathers.size();i++)
      object.fathers.get(i).objects.remove(object);
    //remove fathers
    object.fathers.clear();
  }
  public void replace(Collisionable object)
  {
    remove(object);
    insert(object);
  }
}