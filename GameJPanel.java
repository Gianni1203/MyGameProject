package RunningGame;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.Timer;


public class GameJPanel extends javax.swing.JPanel 
{
	private int countTime, speed;
	private int score, coinSize, plantSize;
	private int index;
	private int  change;
	private ArrayList <PlantIcon> a;
	private LoadingMap maps;
	private Poly leftRoad,middleRoad,rightRoad;
	private Boat actor;
	private RoadLine line, lineRight;
	private ArrayList <Coin> coinCollection;
	private BufferedImage imgbg;
	private Menu menu;
	private End button;
	private AudioClip clip, clip2, coinMusic;
	private int starttime;
	public static enum STATE{
		MENU,
		GAME,
		GAMEOVER
	};
	public static STATE state = STATE.MENU;
	public GameJPanel()
	{
		speed = 10;
		coinCollection = new ArrayList <Coin>();
		coinSize = 70;
		plantSize = 100;
		actor = new Boat(245, 570);
		line = new RoadLine(145, 155);
		lineRight = new RoadLine(445, 455);
		maps = new LoadingMap();
		index = 0;
		score = 0;
		try {
			button = new End();
			menu = new Menu();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		leftRoad= new Poly(-150,150,150,-150,0,0,800,800);
		middleRoad= new Poly(150,450,450,150,0,0,800,800);
		rightRoad= new Poly(450,750,750,450,0,0,800,800);
		change = 20;
		try   //背景音樂及音效	
		{
			clip = Applet.newAudioClip(getClass().getResource("resources/GameMusic.wav"));
			clip2 = Applet.newAudioClip(getClass().getResource("resources/Boom.wav"));
			coinMusic = Applet.newAudioClip(getClass().getResource("resources/Coin.wav"));
			imgbg = ImageIO.read(getClass().getResource("resources/space.png"));	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		try {
			maps.input();
		} 
		catch (FileNotFoundException e) {
			System.out.println("Error!!");
		}
		a = new ArrayList<PlantIcon>();
		Timer t1 = new Timer(100, new ActionListener() {
			int n = 0;
			Coin k;
			@Override public void actionPerformed(ActionEvent evt) {
				countTime ++;
				if(state == STATE.GAME) {
					if(countTime % 2 == 0)
					{
						line = new RoadLine(145, 155);
						lineRight = new RoadLine(445, 455);
					}
					line.reset(change, 0);
					lineRight.reset(change, 0);
					if(countTime % speed == 0)
					{
						if(n<maps.getI()) {  //讀取一列中是否有1
							if(maps.getValue(n, 0) == 1) {  //當第1行為1就印障礙物
								PlantIcon p1 = new PlantIcon(-100, 0, plantSize);
								a.add(p1);
							}
							if (maps.getValue(n, 0) == 2)
							{
								k = new Coin (-100, 0, coinSize);
								coinCollection.add(k);
							}
							if(maps.getValue(n, 1) == 1) {  //當第2行為1就印障礙物
								PlantIcon p2 = new PlantIcon(250, 0, plantSize);
								a.add(p2);
							}
							if (maps.getValue(n, 1) == 2)
							{
								k = new Coin (250, 0, coinSize);
								coinCollection.add(k);
							}
							if(maps.getValue(n, 2) == 1) {  //當第3行為1就印障礙物
								PlantIcon p3 = new PlantIcon(550, 0, plantSize);
								a.add(p3);
							}
							if (maps.getValue(n, 2) == 2)
							{
								k = new Coin (550, 0, coinSize);
								coinCollection.add(k);
							}
							n++;
						}	
						if(n == maps.getI()) {
							n = 0;
							try {
								maps.input();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
						}
					}
					for(int i = 0; i < a.size(); i ++) {
						a.get(i).reset(change, plantSize);
						if(a.get(i).disapear(actor) == true) 
						{
							clip2.play();
							state = STATE.GAMEOVER; 
						}
					}
					for(int i = 0; i< coinCollection.size(); i++)
					{
						coinCollection.get(i).reset(change,coinSize);
						if(coinCollection.get(i).disapear(actor))
						{
							actor.stateChange(3);
							score++;	
							coinCollection.remove(i);
							coinMusic.play();
							break;
						}
						else 
							actor.stateChange(0);
					}
				}
			}
		});
		Timer t2 = new Timer(100, new ActionListener() {
			int timespeed = 100;
			int k =1;
			@Override public void actionPerformed(ActionEvent evt) {
				if(state == STATE.GAME)
					starttime++;
				if(starttime == 15)
				{
					timespeed = 100;
					k = 1;
					t1.start();
					t1.setDelay(100);
				}
				if(state == STATE.GAMEOVER)
					t1.stop();		
				if(score > 20 * k && timespeed > 20)
				{
					k++;
					timespeed -= 20;
					t1.setDelay(timespeed);
				}		
				//System.out.println(state);
				//System.out.println(t1.getDelay());
			}
		});
		t2.start();
		this.addKeyListener(new MykeyClass());
		setFocusable(true);
		clip.loop();
	}

	public void paint (Graphics g)
	{
		if(state == STATE.GAME || state == STATE.GAMEOVER) {
			g.drawImage(imgbg,0,0,600,800, null);
			leftRoad.paintfill(g,1);
			middleRoad.paintfill(g,0);
			rightRoad.paintfill(g,1);
			for(int i = 0; i < a.size(); i ++) {
				a.get(i).paint(g);
			}
			line.paint(g);
			lineRight.paint(g);;
			for(int i =0; i < coinCollection.size(); i++)
				coinCollection.get(i).paint(g, countTime);
			Font f1 = new Font("Helvetica",Font.BOLD ,30);
			g.setFont(f1);
			g.setColor(new Color(225,0,0,225));
			g.drawString("Score:" + score, 400, 30);
			for(int i = 0; i < a.size(); i ++) {
				a.get(i).paint(g);
			}
			actor.paint(g);
		}
		if(state == STATE.GAMEOVER)   //bump//
		{
			button.paint(g);
			Font f2 = new Font("Helvetica",Font.BOLD ,50);
			g.setFont(f2);
			g.setColor(new Color(225,0,0,225));
			g.drawString("Score " + score, 200, 400);
		}
		else if(state == STATE.MENU) {
			menu.paint(g);
		}
	}
	class MykeyClass extends KeyAdapter
	{
		@Override public void keyPressed(KeyEvent e)
		{
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_RIGHT)
				actor.move(1);
			if (key == KeyEvent.VK_LEFT)
				actor.move(-1);
			if(key == KeyEvent.VK_DOWN) {
				button.endButton(1);
				menu.menuButton(1);
			}
			if(key == KeyEvent.VK_UP) {
				button.endButton(0);
				menu.menuButton(0);
			}
			if(state == STATE.GAMEOVER || state == STATE.MENU)
				if(key == KeyEvent.VK_SPACE || key == KeyEvent.VK_ENTER) 
				{
					if(button.getState() == 1 || menu.getState() == 1)
						System.exit(0);
					else 
					{
						state = STATE.GAME;
						score = 0;
						coinCollection.clear();
						a.clear();
						starttime = 0;
					}	
				}
			if(key == KeyEvent.VK_ESCAPE)
				state = STATE.MENU;
		}
		@Override public void keyReleased(KeyEvent e)
		{
			actor.stateChange(0);
		}
	}
}
