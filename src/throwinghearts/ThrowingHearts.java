/*
 */
package throwinghearts;
import sidescrollingshooter.*;
import collisionrequired.*;

/**
 * @author Jul
 */
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.imageio.*;
import javax.swing.*;

public class ThrowingHearts extends SideScrollingShooter implements Runnable,
                                             WindowListener, KeyListener,
                                             MouseMotionListener,MouseListener
{
    DrawingPanel dp;
    JButton spec,deb;
    JTextField FPSfield;
    
    int errorId;
    int counter;
    boolean isValentines=true;
    boolean isCreating=false;
    boolean playerHasInfinite=false;
    
    boolean specialMode = false;
    boolean debug = false;
    
    private class DrawingPanel extends JPanel
    {
      @Override
      public void paint(Graphics G)
      {
        //SPECIAL MODE
        boolean first = BI==null;
        if(!specialMode ||first)
          BI = new BufferedImage(endX, endY, BufferedImage.TYPE_INT_ARGB);
        Graphics g = BI.getGraphics();
        g.setColor(Color.BLACK);
        if(!specialMode||first )
          g.fillRect(0,0,endX,endY);
        g.drawImage(player.current, (int)player.x, (int)player.y,null);
        g.setColor(Color.WHITE);
        for (int i =0; i< collis.size();i++)
        {
          if(collis.get(i) instanceof Sprite)
          {
            Sprite spr = (Sprite)(collis.get(i));
            g.drawImage(spr.current, (int)(spr.x),(int)(spr.y), this);
          }
        }
        //draw lines
        for(int i =0; i< map.size(); i++)
        {
          Collisionable c1=map.get(i);
          g.drawLine((int)c1.x, (int)c1.y, (int)(c1.x+c1.length),(int)(c1.y+c1.height ));
        }
        for(int i=0; i<projectiles.size();i++)
        {
          Projectile p1=projectiles.get(i);  
          g.drawImage(p1.current, (int)p1.x,(int)p1.y,this);
        }
        if(player.hasShooter)
        {
          int origX;
          int origY;
          if(!player.isFlipped)
            origX = (int)player.x+5;
          else
            origX = (int)player.x+player.length-5;
          origY = (int)player.y+31;
          Shooter pShoot=player.shooter;
          g.drawImage(pShoot.current,origX-pShoot.length/2, origY-pShoot.width/2,null);

        }
        g.setColor(Color.RED);
      // just fer debug
        if(debug)
        {
          for(int i=0;i<collis.size();i++)
          {
            for(int j=0;j<collis.get(i).fathers.size();j++)
            {
              Rectangle rect = collis.get(i).fathers.get(j).bounds;
              g.drawRect((int)rect.getX(),(int)rect.getY(), (int)rect.getWidth(),(int)rect.getHeight());
            }
          }
        }
        setFont(new Font("Copperplate Gothic Light", Font.PLAIN, 13));
        g.drawString("Hearts left: "+ player.ammo,getInsets().left-addX, getInsets().top -addY+13);
        G.drawImage(BI,addX , addY, this);
      }
    }
    void set()
    {
      addX=0;
      addY=0;
      counter=0;
      if(targetFps==0)
        targetFps=60;
      errorId=0;
      BufferedImage BI = null;
      dp = new DrawingPanel();
      this.getLayeredPane().add(dp,new Integer(1),0);
      dp.setSize(800, 600);
      this.getLayeredPane().setPreferredSize(new Dimension(800, 600));
      this.requestFocusInWindow();
      int sizeX = 65;
      int sizeY = 50;
      map = new ArrayList(10);
      monsters = new ArrayList(10);
      collis = new ArrayList(100);
      projectiles = new ArrayList(100);
      ArrayList<BufferedImage> images = new ArrayList(20);
      ArrayList<String> strings = new ArrayList(20);
      try
      {
        //reading map VERY IMPORTANT: For collision checking,
        //the vertical ones are better off at the begining
        //then after those there should be the horizontal ones.
        //This is because one can go illegaly to the right
        //then the ground would be reupdated, then it would be set
        //correctly,but it will begin falling.
        BI = ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Cori.png"));
        BufferedImage flipped =  ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Cori2.png"));
        String str = "Maps/Map2.txt";
        InputStream IS = this.getClass().getClassLoader().getResourceAsStream(str);
        Scanner scann = new Scanner( IS);
        endX = scann.nextInt();
        endY = scann.nextInt();
        int i = scann.nextInt();
        int i1 = scann.nextInt();
        String s = scann.next();
        root = new Quadtree(0,new Rectangle(0,0,endX,endY));
        map.add(new Platform(2, new Point(0, 0),0,endY));
        root.insert(map.get(0));
        map.add(new Platform(2, new Point(endX -1,0),0,endY));
        root.insert(map.get(1));
        while (!s.equals("*"))
        {
          Platform plat = new Platform(scann.nextInt(),new Point(scann.nextInt(),
                  scann.nextInt()),scann.nextInt(),scann.nextInt());
          map.add(plat);
          root.insert(plat);
          collis.add(plat);
          s = scann.next();
        }
        while (!scann.next().equals("*"))
        {
          String sl = scann.next();
          String sl2 = scann.next();
          String sl3 = scann.next();
          int il1 = scann.nextInt();
          int il2 = scann.nextInt();
          int il3 = scann.nextInt();
          int il4 = scann.nextInt();
          int il5 = scann.nextInt();
          int il6 = scann.nextInt();
          long l = scann.nextLong();
          BufferedImage I = ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/" + sl));
          BufferedImage I2 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/"+ sl2));
          BufferedImage PI = ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/"+ sl3));
          boolean i8 = scann.nextBoolean();
          int i9 = scann.nextInt();
          int i10 = scann.nextInt();
          int i11 = scann.nextInt();
          int i12 = scann.nextInt();
          int i13 = scann.nextInt();
          ProjectilePlan plan = new ProjectilePlan(PI,"Star.png");
          NPC npc = new NPC(I,I2,il1,il2,il3,il4,sl,sl2,i10,il5,il6,l,i8,i9,plan,i12,i13,i11);
          root.insert(npc);
          collis.add(npc);
          Shooter shooter = new Shooter(0,0,PI,PI,0,0,this);
          npc.setShooter(shooter);
          monsters.add(npc);
        }
        while(scann.hasNext())
        {
          int degrees,x,y,pEndX,pEndY;
          String image;
          degrees=scann.nextInt();
          x=scann.nextInt();
          y=scann.nextInt();
          pEndX=scann.nextInt();
          pEndY=scann.nextInt();
          image = scann.next();
          BufferedImage I = ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/" + image));
          Projectile p = new Projectile(x,y,pEndX-x,pEndY-y,null, new ProjectilePlan(I,image),true);
          p.degrees=degrees;
          p.updateRadians();
          collis.add(p);
          scann.next();
        }
        scann.close();
        BufferedImage projectileI = ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Heart.png"));
        ProjectilePlan plan = new ProjectilePlan(projectileI,"Heart.png");
        player = new ShooterHolder(BI,flipped,i,i1,60,30,"Cori.png","Cori2.png",100,
                                   1000000000,playerHasInfinite,5,plan,5,31);//turn false to true if testing
        root.insert(player);
        BufferedImage fl = ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/cannonFlipped.png"));
        BufferedImage bi = ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/cannon.png"));
        Shooter shooter = new Shooter(80,80,bi,fl,40,10,this);
        player.setShooter( shooter);
        collis.add(player);
        for(int l=0;l<collis.size();l++)
          root.replace(collis.get(l));
        for(int l=0;l<monsters.size();l++)
          monsters.get(l).checkCollisions(collis,this);
        player.checkCollisions(collis,this);
        gameRunning = true;
        fps = 0;
        lastFpsTime = 0;
        if(runner==null)
        {
          runner = new Thread(this);
          runner.start();
        }
      }
      catch(Exception e)
      {
        e.printStackTrace();
        JFrame dialog = new JFrame("ERROR!");
        dialog.setLayout(new FlowLayout());
        dialog.setSize(300,150);
        dialog.add(new JLabel( e.getLocalizedMessage() ));
        dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        errorId=1;
      }
    }
    private ThrowingHearts()
    {
      super();
      JPanel jp = new JPanel();
      jp.setLayout(new GridLayout(3,0,0,20));
      JButton pB = new JButton("Joaca");
      pB.addActionListener( (e)->
              {
                getLayeredPane().remove(jp);
                set();
              });
      JButton gB = new JButton("Ghid");
      gB.addActionListener( (e)->
              {
                  JLayeredPane JLP = new JLayeredPane();
                  JTextArea JTA = new JTextArea();
                  if(isValentines)
                  {
                    JTA.append(("Salut!\r\n"));
                    JTA.append(("In acest joc, arunci inimi pentru a calma monstrii.\r\n"));
                    JTA.append(("Fii atent, insa, pentru ca ei te pot ataca inapoi!\r\n"));
                    JTA.append(("De asemenea, ai un numar restrans de inimi. Arunca bine!\r\n"));
                    JTA.append(("Apasand butonul click-stanga arunci inimi.\r\n"));
                    JTA.append(("Apasand butoanele AD/sageti mergi.\r\n"));
                    JTA.append(("Iar apasand butonul W/sageata sus sari.\r\n"));
                    JTA.append(("Pentru a castiga, calmeaza monstrii fara sa mori.\r\n"));
                    JTA.append(("Noroc!\r\n"));
                  }
                  JButton back = new JButton("Inapoi");
                  back.addActionListener( (i)->
                    {
                      setResizable(true);
                      getLayeredPane().remove(JLP);
                      setSize(800,600);
                      setResizable(false);
                      setLocationRelativeTo(null);
                    }
                  );
                  JLP.add(JTA, new Integer(2),0);
                  JLP.add(back, new Integer(3),0);
                  getLayeredPane().add(JLP, new Integer(4),0);
                  JTA.setEditable(false);
                  JTA.setForeground(Color.PINK);
                  JTA.setBackground(Color.BLACK);
                  Dimension d = JTA.getMinimumSize();
                  d.height+=50;
                  JTA.setBounds(0,0,d.width,d.height);
                  setResizable(true);
                  back.setBounds(d.width-100,d.height-40,100,40);
                  JLP.setSize(d);
                  setSize(getInsets().left + getInsets().right + d.width,
                  d.height+ getInsets().top + getInsets().bottom);
                  setResizable(false);
                  setLocationRelativeTo(null);
                  setVisible(true);
              });
      JButton cB = new JButton("Setari");
      cB.addActionListener( (e)->
              {
                JPanel j2 = new JPanel();
                j2.setLayout(new FlowLayout());
                j2.setBackground(Color.BLACK);
                JPanel jpl = new JPanel();
                jpl.setBackground(Color.BLACK);
                jpl.setLayout(new GridLayout(3,0,0,10));
                
                spec=new JButton("Mod Special:ON");
                if(!specialMode)
                  spec=new JButton("Mod Special:OFF");
              
                spec.addActionListener( (i)->
                {
                  if(specialMode)
                  {
                    specialMode= false;
                    requestFocusInWindow();
                    spec.setText("Mod Special:OFF");
                  }
                  else
                  {
                    specialMode=true;
                    requestFocusInWindow();
                    spec.setText("Mod Special:ON");
                  }
                });
                deb=new JButton("Debug:ON");
                if(!debug)
                  deb=new JButton("Debug:OFF");
                deb.addActionListener( (i)->
                {
                  if(debug)
                  {
                    debug= false;
                    requestFocusInWindow();
                    deb.setText("Debug:OFF");
                  }
                  else
                  {
                    debug=true;
                    requestFocusInWindow();
                    deb.setText("Debug:ON");
                  }
                });
                JPanel JpF = new JPanel();
                JpF.setLayout(new FlowLayout());
                JpF.setBackground(Color.BLACK);
                JLabel jl = new JLabel("Target FPS:");
                jl.setForeground(Color.WHITE);
                FPSfield = new JTextField(""+((targetFps==0)?60:targetFps),10);
                JButton jbf = new JButton("Salveaza");
                JpF.add(jl);
                JpF.add(FPSfield);
                JpF.add(jbf);
                jbf.addActionListener( (i)->
                  {
                    try
                    {
                      int fps= Integer.parseInt(FPSfield.getText());
                      if(fps<=0) throw new Exception();
                      targetFps=fps;
                    }
                    catch (Exception u)
                    {
                      FPSfield.setText("Numar Incorect!");
                    }
                  }
                );
                
                JButton back = new JButton("Inapoi");
                back.addActionListener( (i)->
                  {
                    setResizable(true);
                    getLayeredPane().remove(j2);
                    getLayeredPane().remove(back);
                    setSize(800,600);
                    setResizable(false);
                    setLocationRelativeTo(null);
                  }
                );
                setSize(400+getInsets().left+getInsets().right,
                        300+getInsets().top+getInsets().bottom);
                j2.setSize(400,300);
                back.setBounds(400-100,300-40,100,40);
                jpl.add(spec);
                jpl.add(deb);
                jpl.add(JpF);
                j2.add(jpl);
                getLayeredPane().add(j2, new Integer(4),0);
                getLayeredPane().add(back, new Integer(4),0);
                setLocationRelativeTo(null);
              }
      
      );
      jp.add(pB);
      jp.add(gB);
      jp.add(cB);
      int width=jp.getPreferredSize().width;
      int height=jp.getPreferredSize().height;
      jp.setBounds(400-width/2,300-height/2,width,height);
      getLayeredPane().add(jp);
      
    }
    @Override
    public void run()
    {
      ((SideScrollingShooter)this).doGameLoop();
    }
    //helper for creating maps and saving them without
    //saving over existing maps in the subdirectory
    @Override
    public void save()
    {
      if(isCreating)
      {
        int number=0;
        File file = new File("0map.txt");
        while(file.exists())
        {
          number++;
          file = new File(number+"map.txt");
        }
        try
        {
          FileWriter fw = new FileWriter(file,true);
          BufferedWriter bw = new BufferedWriter(fw);
          bw.write((int)endX + " ");
          bw.write((int)endY + "\r\n");
          bw.write((int)player.x+" ");
          bw.write((int)player.y+" ");
          bw.write("n\r\n");
          for(int i=2;i<map.size();i++)
          {
            bw.write(map.get(i).dirFlag+" ");
            bw.write((int)map.get(i).x+ " ");
            bw.write((int)map.get(i).y+ " ");
            bw.write((int)map.get(i).length+ " ");
            bw.write((int)map.get(i).height+ " ");
            if(i < map.size()-1)
              bw.write("n\r\n");
            else
              bw.write("* n\r\n");
          }
          for(int i=0;i<monsters.size();i++)
          {
            NPC mons = monsters.get(i);
            bw.write(mons.nameBI+ " ");
            bw.write(mons.nameFlipped+ " ");
            bw.write(mons.plan.image+ " ");
            bw.write((int)mons.x+ " ");
            bw.write((int)mons.y+ " ");
            bw.write(mons.height+ " ");
            bw.write(mons.length+ " ");
            bw.write(mons.state + " ");
            bw.write(mons.lineOfSight + " ");
            bw.write(mons.rechargeTime+ " ");
            bw.write(mons.hasInfiniteAmmo+ " ");
            bw.write(mons.ammo+ " ");
            if(i < monsters.size()-1)
              bw.write("n\r\n");
            else
              bw.write("*\r\n");
          }
          for(int i=0;i<collis.size();i++)
          {
            if(!(collis.get(i) instanceof Projectile )) continue;
            Projectile p = (Projectile)(collis.get(i));
            bw.write(p.degrees+" ");
            bw.write((int)p.x+" ");
            bw.write((int)p.y+" ");
            bw.write((int)p.length+" ");
            bw.write((int)p.height+" ");
            bw.write(p.plan.image);
            bw.write(" n\r\n");
          }
          bw.close();
        }
        catch(Exception exc){exc.printStackTrace();}
      }
    }
    //special valentines ending(when you calm all the monsters by
    //shooting them
    @Override
    public boolean receiveEvent(int ID, Object ...arguments)
    {
      if(ID == PROJECTILEHIT|| ID == PLATFORMHIT) return false;
      if(ID == MONSTERHIT && !(arguments[1] instanceof NPC))
      {
        NPC npc = (NPC)arguments[0];
        if (isValentines)
        {
          valentines();
          //This is specifically for valentines(goal for valentines:
          //calm all NPCs)
          if(npc.state != NPC.CALM ) counter++;
          npc.state = NPC.CALM;
          npc.hasShooter = false;
          return true;
        }
        monsters.remove(npc);
        collis.remove(npc);
        root.remove(npc);
        return true;
        //TODO: MORE BEHAVIOUR
      }
      if(ID == PLAYERHIT)
      {//TODO: instead move to a checkpoint, to a death screen etc
        save();
        this.getLayeredPane().remove(dp);
        set();
        
      }
      return false;
    }
    private void valentines()
    {
      if(counter == 2 && isValentines)
      {
        gameRunning= false;
        getLayeredPane().remove(dp);
   //     getLayeredPane().remove(jb);
        JTextArea JTA = new JTextArea();
        InputStream IS = getClass().getClassLoader().getResourceAsStream("EndMessage.txt");
        Scanner scan = new Scanner(IS);
        while(scan.hasNext())
        {
          JTA.append(scan.nextLine());
          JTA.append("\r\n");
        }
        JTA.setEditable(false);
        JTA.setForeground(Color.PINK);
        JTA.setBackground(Color.BLACK);
        Dimension d = JTA.getMinimumSize();
        getLayeredPane().add(JTA,new Integer(3),0);
        JTA.setBounds(0,0,d.width,d.height);
        setResizable(true);
        setSize(getInsets().left + getInsets().right + d.width,
                d.height+ getInsets().top + getInsets().bottom);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
      }
    }
    private void playerMovement(double delta)
    {
      player.yMovement();
      //lateral movement
      if(player.movementX != null)
      {
        switch(player.movementX)
        {
            case "Left":
              player.oldX=player.x;
              player.x-=delta*100.0/targetFps;
              setCentered();
            break;
            case "Right":
              player.oldX=player.x;
              player.x+=delta*100.0/targetFps;
              setCentered();
            break;
        }
      }
      player.checkCollisions(collis,this);
      //moving it accordingly in the quadtree after new position
      root.replace(player);
    }
    private void seeingPlayer(NPC mons)
    {
      if(mons.state == NPC.HOSTILE )
      {
        if(mons.x-player.x-player.length <=0)mons.setFlipped(false);
        else mons.setFlipped(true);
        if(Math.abs(mons.x - player.x) < mons.lineOfSight )//&& reminder
        //this comment is using stuff that is no longer existent in the code
        //   mons.groundY+mons.height == player.groundY+player.height)
        {
          if (mons.x > player.x+player.length)
          {
            mons.movementX = "Left";
            mons.isStopped=false;
          }
          else if( mons.x + mons.length < player.x)
          {
            mons.movementX = "Right";
            mons.isStopped=false;
          }
          else 
          {
            mons.movementX =null;
            mons.isStopped=true;
          }
          if (mons.movementX != null)
            mons.seenPlayer=true;
        }
        else mons.seenPlayer =false;
        if (System.nanoTime() - mons.lastTrigger >= mons.rechargeTime)
        {
          boolean shot = mons.shoot(player,this);
          if(shot)
            mons.lastTrigger = System.nanoTime();
        }
      }
    }
    //handling of monster movement on the x axis
    private void movingMonster(NPC mons,double delta)
    {
      if(mons.movementX == null) mons.movementX = "Left";
      double margin = delta*mons.speedX0/targetFps;
      //move faster (towards the player) if it has seen the player
      if(mons.seenPlayer) margin = delta *mons.speedSeen/targetFps;
      switch(mons.movementX)
      {
        case "Left":
          if( mons.x + mons.length/2 > mons.firstX)
          {
            mons.oldX= mons.x;
            mons.x-=margin;
            if( mons.x + mons.length/2 <= mons.firstX)
              mons.x= mons.firstX-mons.length/2;
            mons.pixMoved+=margin;
            if(!mons.hasShooter) mons.isFlipped=true;
          }
          else
          {
            //stop at the end of this platform then change movement if it has
            //moved enough pixels
            mons.movementX= "Right";
            Random rand = new Random();
            if (mons.pixMoved > 60 && !mons.seenPlayer)
            {
              mons.pixMoved=0;
              long timeStop = (long)( rand.nextInt(3) + 5 ) * 1000000000;
              mons.stopTime = System.nanoTime() + timeStop;
              mons.isStopped=true;
            }
          }
        break;
        case "Right":
          if(mons.x + mons.length< mons.lastX + mons.length/2)
          {
            mons.oldX= mons.x;  
            mons.x+=margin;
            if(mons.x + mons.length>= mons.lastX + mons.length/2)
              mons.x= mons.lastX -mons.length/2;
            mons.pixMoved+=margin;
            if(!mons.hasShooter) mons.isFlipped=false;
          }
          else
          {
            mons.movementX= "Left";
            Random rand = new Random();
            if (mons.pixMoved > 60 && !mons.seenPlayer)
            {
              mons.pixMoved=0;
              long timeStop = (long)( rand.nextInt(3) + 5 ) * 1000000000;
              mons.stopTime = System.nanoTime() + timeStop;
              mons.isStopped=true;
            }
          }
        break;
      }
    }
    private void monsterUpdates(double delta)
    {
      for (int i = 0; i< monsters.size();i++)
      {
        NPC mons = monsters.get(i);
        mons.yMovement();
        seeingPlayer(mons);
        if(!mons.isStopped)
        {
          movingMonster(mons,delta);
          mons.checkCollisions(collis,this);
          //Decide whether to stop movement
          if ( mons.pixMoved > 100 && !mons.seenPlayer)
          {
            Random rand = new Random();
            int chance = rand.nextInt(150);
            if( chance == 1)
            {
              mons.pixMoved=0;
              long timeStop = (long)( rand.nextInt(3) + 5 ) * 1000000000;
              mons.stopTime = System.nanoTime() + timeStop;
              mons.isStopped=true;
            }
          }
        }
        //if it doesn't see the player it can stop if it should be based on chance
        else if (!mons.seenPlayer)
        {
          long time = System.nanoTime();
          if(time >= mons.stopTime)
          {
            mons.isStopped= false;
            if(mons.movementX == null || mons.movementX.equals("Left"))
              mons.movementX= "Right";
            else if (mons.movementX.equals("Right"))
              mons.movementX= "Left";
          }
        }
        //moving it accordingly in the quadtree after new position
        root.replace(mons);
      }
    }
    @Override
    public void doGameUpdates(double delta)
    {
      valentines();
      player.setAimPoint(this);
      playerMovement(delta);
      setCentered();
      monsterUpdates(delta);
      for(int i=0; i< projectiles.size();i++)
      {
        Projectile p = projectiles.get(i);
        p.updateRadians();
        p.yMovement();
        p.oldX = p.x;
        p.x += p.speedX *delta*60.0/targetFps;
        if(!p.collide(this))
          root.replace(p);
        //moving it accordingly in the quadtree after new position
      }
    }
    @Override
    public void keyPressed(KeyEvent e)
    {
      if(!gameRunning) return;
      switch (e.getKeyCode())
      {
        case LEFT:
        case A:
          player.movementX = "Left";
        break;
        case D:
        case RIGHT:
          player.movementX = "Right";
        break;
        case W:
        case UP:
          //first jump
          if( player.speedY==0 )
          {
            //power of the jump
            player.speedY0 = player.speedJump;
            player.start = System.nanoTime();
            player.isOnPlat=false;
          }
          //second jump (jumping is capped at doublejumps)
          else if(!player.isDoubleJump)
          {
            //power of the jump
            player.speedY0 = player.speedJump;
            //set where to start the motion
            player.startY = player.y;
            player.isOnPlat=false;
            player.start = System.nanoTime();
            player.isDoubleJump = true;
          }
        break;
      }
    }
    @Override
    public void keyReleased(KeyEvent e)
    {
      if(!gameRunning) return;
      int keyCode = e.getKeyCode();
      if ( player.movementX != null && keyCode == LEFT ||
           keyCode == RIGHT || keyCode == A || keyCode == D)
        player.movementX = null;
    }
    @Override
    public void windowClosing(WindowEvent e)
    {
      if(!gameRunning) return;
      //helper for creating maps and saving them without
      //saving over existing maps in the subdirectory
      save();
    }
    @Override
    public void mouseMoved(MouseEvent m)
    {
      if(!gameRunning) return;
      if(player.hasShooter)
        player.setAim(new Point(m.getX(),m.getY()));
    }
    @Override
    public void mousePressed(MouseEvent m) 
    {
      if(!gameRunning) return;
      if(player.hasShooter)
      {
        player.setTarget(new Point(m.getX(),m.getY()));
        player.shoot(new Point(m.getX(),m.getY()),this,isCreating);
      }
    }
    @Override
    public void mouseReleased(MouseEvent m) 
    {
      if(!gameRunning) return;
      if(player.hasShooter)
      {
        player.target = null;
        player.hasTarget = false;
      }
    }
    
    @Override
    public void mouseDragged(MouseEvent m)
    {
      if(!gameRunning) return;
      if(player.hasShooter)
        player.setTarget(new Point(m.getX(),m.getY()));
    }
    @Override
    public void mouseClicked(MouseEvent m)
    {
    }
    //Methods from the MouseListener interface the class doesn't use
    @Override
    public void mouseEntered(MouseEvent m) {}
    @Override
    public void mouseExited(MouseEvent m) {}
    //Methods from the KeyListener interface the class  doesn't use
    @Override
    public void keyTyped(KeyEvent e) {}
    //Methods from the WindowListener interface the class doesn't use
    @Override
    public void windowClosed(WindowEvent e){}
    @Override
    public void windowOpened(WindowEvent e){}
    @Override
    public void windowDeiconified(WindowEvent e){}
    @Override
    public void windowIconified(WindowEvent e){}
    @Override
    public void windowActivated(WindowEvent e){}
    @Override
    public void windowDeactivated(WindowEvent e){}
   
    private static void createAndShowGUI()
    {
      ThrowingHearts CP = new ThrowingHearts();
      CP.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      CP.setResizable(false);
      CP.setSize(800,600);
      CP.setTitle("Throwing Hearts");
      CP.setLocationRelativeTo(null);
      CP.addWindowListener(CP);
      
      
      CP.addKeyListener(CP);
      CP.addMouseMotionListener(CP);
      CP.addMouseListener(CP);
      CP.setVisible(true);
      if(CP.errorId != 0) CP.dispose();
    }
    public static void main(String[] args)
    {
      javax.swing.SwingUtilities.invokeLater
      ( () ->
        {
          createAndShowGUI();
        }
      );
    }
    
}
