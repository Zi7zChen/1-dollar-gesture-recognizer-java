package $1;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

//java canvas, main is here
public class DisplayBox {	
	
	public static void main(String[] args){
		new DisplayBox().show();
	}
	public void show(){
		//main frame is created
		JFrame frame = new JFrame("1$ Display Box");
		Container content = frame.getContentPane();
		//set layout on content pane
		content.setLayout(new BorderLayout());
		//create draw area
		final Draw drawArea = new Draw();
		
		JPanel panel = new JPanel();
		JButton rButton = new JButton("Recognize");
		JButton pButton = new JButton("Recognize(faster)");
		ActionListener actionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == rButton) {
					drawArea.showResult();
				} else if (e.getSource() == pButton) {
					drawArea.showProtractorResult();
				}
			}
		};	
		rButton.addActionListener(actionListener);
		pButton.addActionListener(actionListener);
		panel.add(rButton);
		panel.add(pButton);
		
		//add to content panel
		content.add(drawArea, BorderLayout.CENTER);
		content.add(panel, BorderLayout.SOUTH);
		frame.setSize(800, 600);
		//allow to close frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//allow the swing paint result
		frame.setVisible(true);
	}
}

class Draw extends JComponent{
	//initialize the variables
	int currentX, currentY, previousX, previousY;
	private Vector p = new Vector();
	Image display;
	Graphics2D graphics2D;
	Recognizer oneD = new Recognizer();
	public Draw(){
		setDoubleBuffered(true);
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent event){
				//When mouse is pressed, starting getting the values of x, y
				previousX = event.getX();
				previousY = event.getY();
				p.addElement(new Point(previousX,previousY));
			}
		});

		//can be modified to store the x and y 
		addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent event){
				//update x,y when the mouse is dragged
				currentX = event.getX();
				currentY = event.getY();
				p.addElement(new Point(currentX,currentY));

				if(graphics2D != null){
					//draw line
					graphics2D.drawLine(previousX, previousY, currentX, currentY);
				}
				//update the drawing area
				repaint();
				//store the current x,y as previous, and get ready for next set of x,y
				previousX = currentX;
				previousY = currentY;
			}
		});
	}
	
	//Show the result of the 1$ on the screen
	public void showResult(){
		Long time=System.nanoTime();
		String result =oneD.Recognize(p,false).name;
		time=System.nanoTime()-time;
		time/=1000;
		JOptionPane.showMessageDialog(null, "input: "+result+"\n"+time+" microseconds", "Result: ",JOptionPane.INFORMATION_MESSAGE);
		p.clear();
		clear();
	}
	public void showProtractorResult(){
		Long time=System.nanoTime();
		String result =oneD.Recognize(p,true).name;
		time=System.nanoTime()-time;
		time/=1000;
		JOptionPane.showMessageDialog(null, "input: "+result+"\n"+time+" microseconds", "Result: ",JOptionPane.INFORMATION_MESSAGE);
		p.clear();
		clear();
	}
	protected void paintComponent(Graphics g){
		if (display == null){
			//create the first image
			display = createImage(getSize().width, getSize().height);
			graphics2D = (Graphics2D) display.getGraphics();
			//making the lines smooth
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			//clear draw area
			clear();
		}
		g.drawImage(display, 0, 0, null);

	}
	//clear the screen after the result is shown
	public void clear(){
		graphics2D.setPaint(Color.white);
		graphics2D.fillRect(0, 0, getSize().width, getSize().height);
		graphics2D.setPaint(Color.blue);
		repaint();
	}
}

