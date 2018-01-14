package RunningGame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.*;
import javax.swing.JFrame;
import javax.swing.Timer;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		JFrame f = new JFrame();
		GameJPanel gm = new GameJPanel();
		f.setTitle("Go!");
		f.setSize(600,800);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(gm);
		f.setVisible(true);
		Timer t = new Timer(10, 
				new ActionListener (){
					public void actionPerformed(ActionEvent evt) {
						f.repaint();
			}	
		});
		t.start();
	}

}
