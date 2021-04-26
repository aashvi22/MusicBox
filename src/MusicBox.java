import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;
import java.util.Arrays;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;

public class MusicBox extends JFrame implements Runnable, ActionListener, AdjustmentListener
{
	JToggleButton button[][]=new JToggleButton[37][180];
	JPanel buttonPanel, tempoPanel,menuButtonPanel;
	JScrollPane buttonPane;
	JPanel panel=new JPanel();
	boolean notStopped=true;
	JFrame frame=new JFrame();
	String[] clipNames;
	Clip[] clip;
	Font font= new Font("Arial", Font.PLAIN, 12);
	String name;
	String[]instrumentNames= {"Bell", "Piano", "Marimba", "Oboe", "Glockenspiel", "Oh_Ah"};
	JMenuItem[] instrumentItems;
	JMenuItem wii, takefive, oogwayascends;
	int col=0;
	boolean playing=false;
	boolean isRainbow = false;
	boolean isRewind = false;
	JButton stopPlay, clear, restart, random, add, remove, rainbow, rewind;
	JMenuBar menuBar;
	JMenu file, instrumentMenu, songs;
	JMenuItem save,load;
	Thread thread = new Thread(this);
	JScrollBar tempoBar, volumeBar;
	int tempo=200;
	JLabel tempoLabel;
	
	JFileChooser fileChooser;
	JLabel[]labels = new JLabel[button.length];
	
	public MusicBox()
	{
		
	  clipNames=new String[] {"C0","B1","ASharp1", "A1", "GSharp1", "G1","FSharp1","F1","E1", "DSharp1","D1", "CSharp1", "C1",
				"B2","ASharp2", "A2", "GSharp2", "G2","FSharp2","F2","E2", "DSharp2","D2", "CSharp2", "C2",
				"B3","ASharp3", "A3", "GSharp3", "G3","FSharp3","F3","E3", "DSharp3","D3", "CSharp3", "C3"};
		clip=new Clip[clipNames.length];
		String initInstrument=instrumentNames[0]+"/"+instrumentNames[0];
		try {
			for(int x=0;x<clipNames.length;x++)
			{
	         	File file = new File("/Users/krinalmanakiwala/eclipse-workspace/"+initInstrument+" - "+clipNames[x]+".wav");
	        	 	AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
	         	clip[x] = AudioSystem.getClip();
	         	clip[x].open(audioIn);
			}
		}catch (UnsupportedAudioFileException e) {
         e.printStackTrace();
		} catch (IOException e) {
         e.printStackTrace();
		} catch (LineUnavailableException e) {
         e.printStackTrace();
		}
		menuBar=new JMenuBar();
		menuBar.setLayout(new GridLayout(1,2));
		file=new JMenu("File");
		save=new JMenuItem("Save");
		load=new JMenuItem("Load");
		file.add(save);
		file.add(load);
		save.addActionListener(this);
		load.addActionListener(this);
		String currDir = System.getProperty("user.dir");
		fileChooser=new JFileChooser(currDir);
		
		songs=new JMenu("Songs");
		wii=new JMenuItem("Wii music");
		wii.addActionListener(this);
		takefive=new JMenuItem("Take Five");
		takefive.addActionListener(this);
		oogwayascends = new JMenuItem("Oogway Ascends");
		oogwayascends.addActionListener(this);
		songs.add(wii);
		songs.add(takefive);
		songs.add(oogwayascends);
		
		menuButtonPanel = new JPanel();
		menuButtonPanel.setLayout(new GridLayout(1,8));
		stopPlay = new JButton("Play");
		stopPlay.addActionListener(this);
		menuButtonPanel.add(stopPlay);
		clear = new JButton("Clear");
		clear.addActionListener(this);
		menuButtonPanel.add(clear);
		restart = new JButton("Restart");
		restart.addActionListener(this);
		menuButtonPanel.add(restart);
		random = new JButton("Random");
		random.addActionListener(this);
		menuButtonPanel.add(random);
		add = new JButton("Add");
		add.addActionListener(this);
		menuButtonPanel.add(add);
		remove = new JButton("Remove");
		remove.addActionListener(this);
		menuButtonPanel.add(remove);
		rainbow = new JButton("Rainbow");
		rainbow.addActionListener(this);
		menuButtonPanel.add(rainbow);
		rewind = new JButton("Rewind");
		rewind.addActionListener(this);
		
		menuButtonPanel.add(rewind);
		menuBar.add(menuButtonPanel,BorderLayout.WEST);
		
		buttonPanel=new JPanel();
		buttonPanel.setLayout(new GridLayout(button.length, button[0].length,2,5));
      
		for(int r=0;r<button.length;r++) {
	    	  	String name=clipNames[r].replaceAll("Sharp", "#");
	    	  	for(int c = 0; c < button[0].length;c++) {
	    	  		button[r][c]=new JToggleButton();
	    	  		button[r][c].setFont(font);
	    	  		button[r][c].setText(name);
	    	  		button[r][c].setPreferredSize(new Dimension(30,30));
	    	  		button[r][c].setMargin(new Insets(0,0,0,0));
	    	  		buttonPanel.add(button[r][c]);
	    	  	}
		}
		buttonPane=new JScrollPane(buttonPanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		buttonPane.getVerticalScrollBar().setUnitIncrement(20);
		buttonPane.getVerticalScrollBar().setUnitIncrement(20);
		this.add(buttonPane,BorderLayout.CENTER);
		this.add(menuBar, BorderLayout.NORTH);
		
		instrumentMenu=new JMenu("instrument");
		instrumentItems=new JMenuItem[instrumentNames.length];
		for(int i = 0; i < instrumentNames.length;i++) {
			instrumentItems[i] = new JMenuItem(instrumentNames[i]);
			instrumentItems[i].addActionListener(this);
			instrumentMenu.add(instrumentItems[i]);
		}
		
		menuBar.add(file, BorderLayout.EAST);
		menuBar.add(instrumentMenu, BorderLayout.EAST);
		menuBar.add(songs, BorderLayout.EAST);
		
		tempoBar = new JScrollBar(JScrollBar.HORIZONTAL, 200,0,50,500);
		tempoBar.addAdjustmentListener(this);
		tempo=tempoBar.getValue();
		tempoLabel=new JLabel(String.format("%s%6s", "Tempo: ",tempo));
		tempoPanel=new JPanel(new BorderLayout());
		tempoPanel.add(tempoLabel, BorderLayout.WEST);
		tempoPanel.add(tempoBar, BorderLayout.CENTER);
		this.add(tempoPanel,BorderLayout.SOUTH);
		
		
		setSize(300,300);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Thread timing = new Thread(this);
		timing.start();
	}


	public static void main(String args[])
	{
		MusicBox app=new MusicBox();
	}
	public void load(File loadfile) {
		System.out.println("load file being called");
		try {
			
			BufferedReader input = new BufferedReader(new FileReader(loadfile));
			String temp;
			temp=input.readLine();
			tempo=Integer.parseInt(temp.substring(0,3));
			tempoBar.setValue(tempo);
			
			Character[][] song = new Character[button.length][temp.length()-4];
			
			int r = 0;
			while((temp=input.readLine())!=null) {
				for(int c = 4; c < song[0].length; c++) {
					song[r][c-4]=temp.charAt(c);
				}
				r++;
			}
			setNotes(song);
			
		}
		catch(IOException exc) {
			System.out.println(exc);
		}
		col=0;
		playing=false;
		stopPlay.setText("Play");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getSource().toString());
		if(e.getSource()==stopPlay) {
			playing=!playing;
			if(!playing)
				stopPlay.setText("Play");
			else
				stopPlay.setText("Stop");
		}
		for(int i = 0; i < instrumentItems.length;i++) {
			if(e.getSource()==instrumentItems[i])
			{
				String selectedInstrument=instrumentNames[i]+"/"+instrumentNames[i];
				try {
					for(int x=0;x<clipNames.length;x++)
					{
			         	File file = new File("/Users/krinalmanakiwala/eclipse-workspace/"+selectedInstrument+" - "+clipNames[x]+".wav");
			        	 	AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
			         	clip[x] = AudioSystem.getClip();
			         	clip[x].open(audioIn);
					}
				}catch (UnsupportedAudioFileException exc) {
					exc.printStackTrace();
				} catch (IOException exc) {
					exc.printStackTrace();
				} catch (LineUnavailableException exc) {
					exc.printStackTrace();
				}
				col=0;
				playing=false;
				stopPlay.setText("play");
			}
		}
		if(e.getSource()==clear) {
			for(int r = 0; r < button.length;r++) {
				for(int c = 0; c < button[0].length; c++) {
					button[r][c].setSelected(false);
					button[r][c].setForeground(Color.BLACK);
				}
			}
			col=0;
			playing=false;
			stopPlay.setText("Play");
		}
		if(e.getSource()==restart) {
			for(int r = 0; r < button.length;r++) {
				for(int c = 0; c < button[0].length; c++) {    
					button[r][c].setForeground(Color.BLACK);
				}
			}
			col=0;
		}
		if(e.getSource()==save) {
			saveSong();
		}
		if(e.getSource()==load) {
			int returnVal = fileChooser.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				load(fileChooser.getSelectedFile());
				
			}

		}
		if(e.getSource()==random) {
			for(int r = 0; r < button.length;r++) {
				for(int c = 0; c < button[0].length; c++) {
					double n = Math.random()*100;
					if(n<=25 && n>=0) {
						button[r][c].setSelected(true);
					}
				}
			}
		}
		if(e.getSource()==add) {
			this.remove(buttonPane);
			buttonPane.remove(buttonPanel);
			buttonPanel=new JPanel();
			
			JToggleButton [][]temp=new JToggleButton[button.length][button[0].length+1];
			for(int r = 0; r < temp.length; r++) {
				for(int c = 0; c < temp[0].length;c++) {
					if(c==temp[0].length-1) {
						temp[r][c]=new JToggleButton();
						temp[r][c].setFont(font);
						temp[r][c].setText(clipNames[r].replaceAll("Sharp", "#"));
						temp[r][c].setPreferredSize(new Dimension(30,30));
						temp[r][c].setMargin(new Insets(0,0,0,0));
						temp[r][c].setSelected(false);
						temp[r][c].setForeground(Color.BLACK);
					}
					else{
						buttonPanel.remove(button[r][c]);
						temp[r][c]=button[r][c];
						temp[r][c].setSelected(button[r][c].isSelected());
					}
				}
			}
			
			button=new JToggleButton[temp.length][temp[0].length];
			button=temp;
			
			buttonPanel.setLayout(new GridLayout(button.length, button[0].length,2,5));
			
			for(int r = 0; r < button.length; r++) {
				for(int c = 0; c < button[0].length;c++) {
					buttonPanel.add(button[r][c]);
				}
			}
			buttonPane=new JScrollPane(buttonPanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			buttonPane.getVerticalScrollBar().setUnitIncrement(20);
			buttonPane.getVerticalScrollBar().setUnitIncrement(20);
			this.add(buttonPane,BorderLayout.CENTER);
			this.revalidate();
		}
		if(e.getSource()==remove) {
			this.remove(buttonPane);
			buttonPane.remove(buttonPanel);
			buttonPanel=new JPanel();
			
			JToggleButton [][]temp=new JToggleButton[button.length][button[0].length-1];
			for(int r = 0; r < temp.length; r++) {
				for(int c = 0; c < temp[0].length+1;c++) {
					if(c<temp[0].length) {
						buttonPanel.remove(button[r][c]);
						temp[r][c]=button[r][c];
						temp[r][c].setSelected(button[r][c].isSelected());
					}
					else
						buttonPanel.remove(button[r][c]);
				}
			}
			
			button=new JToggleButton[temp.length][temp[0].length];
			button=temp;
			
			buttonPanel.setLayout(new GridLayout(button.length, button[0].length,2,5));
			
			for(int r = 0; r < button.length; r++) {
				for(int c = 0; c < button[0].length;c++) {
					buttonPanel.add(button[r][c]);
				}
			}
			buttonPane=new JScrollPane(buttonPanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			buttonPane.getVerticalScrollBar().setUnitIncrement(20);
			buttonPane.getVerticalScrollBar().setUnitIncrement(20);
			this.add(buttonPane,BorderLayout.CENTER);
			this.revalidate();
		}
		if(e.getSource()==rainbow) {
			
			isRainbow=!isRainbow;
		}
		if(e.getSource()==wii) {
			File file = new File("/Users/krinalmanakiwala/eclipse-workspace/MusicBox/wiimusic");
			load(file);
		}
		if(e.getSource()==takefive) {
			File file = new File("/Users/krinalmanakiwala/eclipse-workspace/MusicBox/TakeFive");
			load(file);
		}
		if(e.getSource()==oogwayascends) {
			File file = new File("/Users/krinalmanakiwala/eclipse-workspace/MusicBox/OogwayAscends");
			load(file);
		}
		if(e.getSource()==rewind) {
			isRewind=!isRewind;
		}
	}
		
	public void setNotes(Character[][]notes) {
		System.out.println("set notes is called");
		buttonPane.remove(buttonPanel);
		buttonPanel=new JPanel();
		for(int r = 0; r < button.length; r++) {
			for(int c = 0; c < button[0].length;c++) {
				buttonPanel.remove(button[r][c]);
			}
		}
		button = new JToggleButton[37][notes[0].length];
		buttonPanel.setLayout(new GridLayout(button.length, button[0].length,2,5));
	      
		for(int r=0;r<button.length;r++) {
	    	  	String name=clipNames[r].replaceAll("Sharp", "#");
	    	  	for(int c = 0; c < button[0].length;c++) {
	    	  		button[r][c]=new JToggleButton();
	    	  		button[r][c].setFont(font);
	    	  		button[r][c].setText(name);
	    	  		button[r][c].setPreferredSize(new Dimension(30,30));
	    	  		button[r][c].setMargin(new Insets(0,0,0,0));
	    	  		buttonPanel.add(button[r][c]);
	    	  	}
		}
		this.remove(buttonPane);
		buttonPane=new JScrollPane(buttonPanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.add(buttonPane,BorderLayout.CENTER);
		
		for(int r=0;r<button.length;r++) {
    	  	
	    	  	for(int c = 0; c < button[0].length;c++) {
	    	  		try {
	    	  			if(notes[r][c]=='x')
	    	  				button[r][c].setSelected(true);
	    	  			else
	    	  				button[r][c].setSelected(false);
	    	  		}
	    	  		catch(NullPointerException nexc) {}
	    	  		catch(ArrayIndexOutOfBoundsException aexc) {}
	    	  	}
		}
		this.revalidate();
	}
	public void saveSong() {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("*.txt","txt");
		fileChooser.setFileFilter(filter);
		if(fileChooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try {
				String st = file.getAbsolutePath();
				String output="";
				String [] noteNames = new String[clipNames.length];
				for(int i = 0; i < clipNames.length; i++) {
					noteNames[i]=clipNames[i].replaceAll("Sharp", "#");
					while(noteNames[i].length()<4) {
						noteNames[i]+=" ";
					}
				}
				for(int r = 0; r < button.length; r++) {
					if(r==0) {
						output+=tempo;
						for(int x = 0; x < button[0].length; x++) {
							output+= " ";
						}
					}
					else {
						output+=noteNames[r];
						for(int c = 0; c < button[0].length; c++) {
							if(button[r-1][c].isSelected())
								output += "x";
							else output += "-";
						}
					}
					output+="\n";
				}
				BufferedWriter outputStream = new BufferedWriter(new FileWriter(st));
				outputStream.write(output);
				outputStream.close();
			}
			catch(IOException exc) {
				System.out.println(exc);
			}
		}
	}
	@Override
	public void run() {
		do
		{
			try
			{
				if(!playing)
					thread.sleep(0);
				else {
					for(int r = 0; r < button.length;r++) {
						if(button[r][col].isSelected()){
							clip[r].start();
							if(isRainbow) {
								FloatControl gainControl = 
							    (FloatControl) clip[r].getControl(FloatControl.Type.MASTER_GAIN);
								gainControl.setValue(-10f);
							}
							else {
								FloatControl gainControl = 
									    (FloatControl) clip[r].getControl(FloatControl.Type.MASTER_GAIN);
										gainControl.setValue(1f);
							}
							button[r][col].setForeground(Color.YELLOW);
						}
						else if(isRainbow) {
							switch(col%7) {
								case 0: button[r][col].setForeground(Color.RED); break;
								case 1: button[r][col].setForeground(Color.ORANGE); break;
								case 2: button[r][col].setForeground(Color.YELLOW); break;
								case 3: button[r][col].setForeground(Color.GREEN); break;
								case 4: button[r][col].setForeground(Color.BLUE); break;
								case 5: button[r][col].setForeground(new Color(149,0,255)); break;
								case 6: button[r][col].setForeground(Color.MAGENTA); break;
							}
						}
						else
							button[r][col].setForeground(Color.BLUE);
					}
					System.out.println(col);
					
					thread.sleep(tempo);
					for(int r=0;r<button.length;r++)
					{
						if(button[r][col].isSelected()) {
							clip[r].stop();
							clip[r].setFramePosition(0);
						}
						button[r][col].setForeground(Color.BLACK);
					}
					if(isRewind) {
						col--;
						if(col==0)
							col=button[0].length-1;
					}
					else {
						col++;
						if(col==button[0].length-1)
							col=0;
					}
				}
			}
			catch(InterruptedException e)
			{
			}
		}while(notStopped);
		
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		tempo=tempoBar.getValue();
		tempoLabel.setText(String.format("%s%6s", "Tempo: ",tempo));
	}
}