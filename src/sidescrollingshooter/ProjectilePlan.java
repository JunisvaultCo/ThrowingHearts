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
//class necessary in order not to waste time and resources by
//calculating the degreeImages(images for each degree) for each
//projectile created.
final public class ProjectilePlan
{
  public BufferedImage degreeImages[];
  public BufferedImage bi;
  public int length,height;
  public String image;
  public ProjectilePlan(BufferedImage bi, String img)
  {
    this.length=bi.getWidth();
    this.height=bi.getHeight();
    this.bi=bi;
    setImages();
    image=img;
  }
  public void setImages()
  {
    degreeImages = new BufferedImage[360];
    for(int i=0;i<360;i++)
    {
      AffineTransform tx = AffineTransform.getRotateInstance(Math.toRadians(i), this.length/2, this.height/2);
      AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
      degreeImages[i] = op.filter(this.bi, null);
    }
  }
}