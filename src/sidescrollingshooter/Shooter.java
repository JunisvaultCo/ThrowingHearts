/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sidescrollingshooter;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jul
 */
public final class Shooter
{
  public int length;
  public int width;
  public BufferedImage bi;
  public BufferedImage flipped;
  public BufferedImage current;
  public int realLength;
  public int realWidth;
  public double tan;
  //instead of calculating all the possible images,
  //which slows down the game,just calculate 180
  //images (for each degree one image(0,1,2...179)), then
  //approximate the images by approximating the tan
  //(if x<y<z, x,y,z all between [0,90) or all
  //between[90,180), then tan(x)<tan(y)<tan(z)
  //therefore making it possible to know the
  //approximation of the degree by knowing
  //the approximation of the tan(and not
  //having to call the expensive Math.atan)
  public BufferedImage degreeImagesNot[];
  public BufferedImage degreeImages[];
  public Shooter(int l,int w, BufferedImage bi,BufferedImage fl,int rl,int rw,SideScrollingShooter s)
  {
    this.bi = bi;
    length = l;
    width = w;
    flipped = fl;
    realWidth = rw;
    realLength = rl;
    settingImages();
    tan =1;
    setCurrent(false,s);
  }
  public void settingImages()
  {
    degreeImagesNot= new BufferedImage[180];
    degreeImages= new BufferedImage[180];
    if(width == 0 ||length ==0 ) return;
      for(int i=0;i<180;i++)
      {
        AffineTransform tx;
        if(i<90)
          tx = AffineTransform.getRotateInstance(Math.toRadians(i), length/2, width/2);
        else
          tx = AffineTransform.getRotateInstance(Math.toRadians(i-180), length/2, width/2);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        degreeImagesNot[i] = op.filter(bi,null);
        degreeImages[i] = op.filter(flipped,null);
      }
  }
  public void setCurrent(boolean flip, SideScrollingShooter s)
  {
    if(width ==0 ||length ==0) return;
    int begin, end;
    double[] currentTan;
    if(tan<=0)
    {
      currentTan = SideScrollingShooter.tansNeg;
      begin =90;
      end = 179;
    }
    else
    {
      currentTan = SideScrollingShooter.tansPos;
      begin = 0;
      end = 89;
    }
    if(!flip)
    //uses the variation of binarysearch to get the closest, within an error
    //of 1 degree
      current = degreeImagesNot[Util.binarySearch(currentTan,tan,begin,end)];
    else
      current = degreeImages[Util.binarySearch(currentTan,tan,begin,end)];
  }
}
   