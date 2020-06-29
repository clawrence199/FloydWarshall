/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 


import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
/*
 * FileChooserDemo.java uses these files:
 *   images/Open16.gif
 *   images/Save16.gif
 */
public class FileChooserDemo extends JPanel
                             implements ActionListener {

    static private final String newline = "\n";
    JButton openButton, saveButton;
    JTextArea log;
    JFileChooser fc;
    
    //my variables for programming assignment 3
	private int numberOfEdges = 0;
	private String[] communityNames;
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	private ArrayList<String> communityPairs = new ArrayList<String>();
	private int[][] communitiesMatrix;

    
    
    public FileChooserDemo() {
        super(new BorderLayout());

        //Create the log first, because the action listeners
        //need to refer to it.
        log = new JTextArea(5,20);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(log);

        //Create a file chooser
        fc = new JFileChooser();

        //Uncomment one of the following lines to try a different
        //file selection mode.  The first allows just directories
        //to be selected (and, at least in the Java look and feel,
        //shown).  The second allows both files and directories
        //to be selected.  If you leave these lines commented out,
        //then the default mode (FILES_ONLY) will be used.
        //
        //fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        //Create the open button.  We use the image from the JLF
        //Graphics Repository (but we extracted it from the jar).
        openButton = new JButton("Open a File...",
                                 createImageIcon("images/Open16.gif"));
        openButton.addActionListener(this);

        //Create the save button.  We use the image from the JLF
        //Graphics Repository (but we extracted it from the jar).
        saveButton = new JButton("Save File",
                                 createImageIcon("images/Save16.gif"));
        saveButton.addActionListener(this);

        //For layout purposes, put the buttons in a separate panel
        JPanel buttonPanel = new JPanel(); //use FlowLayout
        buttonPanel.add(openButton);
        buttonPanel.add(saveButton);

        //Add the buttons and the log to this panel.
        add(buttonPanel, BorderLayout.PAGE_START);
        add(logScrollPane, BorderLayout.CENTER);
    }

    /**
     * @author Cory Lawrence
     * This program allows the user to select a file off of their operating 
     * system.  The program will read the top line of a file containing
     * dash (-) separated community names.  The following lines will be an 
     * integer and two community names representing the length of the path 
     * between the two communities.  The program reads these lines and creates
     * edge objects.  The edge objects are then converted into a matrix.
     * With the Floyd Warshall algorithm we now know the distance between 
     * any pair of communities.  When the user clicks save, the program
     * writes all pairs and their distances on alphabetized lines in the file. 
     * This program support locations less than a million miles apart.
     * The logic for opening the file starts when the user selects 
     * an appropriate option using the open button. 
     */
    @Override
	public void actionPerformed(ActionEvent e) {

        //Handle open button action.
        if (e.getSource() == openButton) {
            int returnVal = fc.showOpenDialog(FileChooserDemo.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                
                //This is where I create local variables for reading the file
                FileReader fileReader;
                BufferedReader bufferedReader;
                try {                      	
                	fileReader = new FileReader(file);
                	bufferedReader = new BufferedReader(fileReader);

            		//create a string array of the first line of communities
            		communityNames = bufferedReader.readLine().split("-");
            		
            		// count the remaining lines
                	while(bufferedReader.readLine() !=null) {
                		 numberOfEdges++;
                	}     
                	
                	bufferedReader.close();
                	fileReader.close();
                }	catch(IOException exception) {
                	}
                
                // create edges out of the subsequent lines
                try {
                	bufferedReader = new BufferedReader(new FileReader(file));
                	
                	//skip the first line 
                	bufferedReader.readLine();
                	
                	for(int i = 0; i < numberOfEdges; i++) {
                		String[] data = bufferedReader.readLine().split("-");
                		edges.add(new Edge(data[0], data[1], data[2]));
                	}
                	
                	bufferedReader.close();                	                	      	            
                }	catch(IOException exception){
                }
                
                log.append("File Received: " 
                + file.getName() + "." + newline);                
            } else {
                log.append("Open command cancelled by user." + newline);
            }
            log.setCaretPosition(log.getDocument().getLength());
            
         // Create matrix out of existing edges
        	communitiesMatrix = new int[communityNames.length][communityNames.length];
        	for(int i = 0; i < edges.size(); i++) {
        		for(int j = 0; j < communityNames.length; j++) {
        			if(edges.get(i).getCommunity1().equals(communityNames[j])) {
        				for(int k = 0; k < communityNames.length; k++) {
        					if(edges.get(i).getCommunity2().equals(communityNames[k])) {
        					
        						communitiesMatrix[j][k] = edges.get(i).getDistance();
        						communitiesMatrix[k][j] = edges.get(i).getDistance();                					}
        				}
        			}
        		}
        	}
        	
        	//We do not yet know the distances between communities that we must
        	//travel through other communities to get to. So we set those to max
        	for(int j = 0; j < communityNames.length; j++) {
        		for(int k = 0; k < communityNames.length; k++) {
        			if((j != k) && (communitiesMatrix[j][k] == 0)) {
        				communitiesMatrix[j][k] = (2000000);
        			}
        		}
        	}
        	
        	//Apply Floyd Warshall Algorithm
        	for(int k = 0; k < communityNames.length; k ++) {
        		for (int i = 0; i < communityNames.length; i++) {
        			for (int j = 0; j < communityNames.length; j++) {
        				 if(communitiesMatrix[i][j] > communitiesMatrix[i][k] + communitiesMatrix[k][j]) {
        					 communitiesMatrix[i][j] = communitiesMatrix[i][k] + communitiesMatrix[k][j];
        				 }
        			}
        		}
        	}
        	
        	//Creates an array list of strings formatted to be written
        	//the "if" statement disallows the reverse of pairs to be added
        	for(int j = 0; j < communityNames.length; j++) {
        		for(int k = 0; k < communityNames.length; k++) {
        			if (k > j) {
        				communityPairs.add(communityNames[j] + " - " 
        						+ communityNames[k] + " " + communitiesMatrix[j][k]);
        			}
        		}
        	}
        	
        	//Sort the ArrayList of pairs for convenience
        	Collections.sort(communityPairs);
        	
        //Handle save button action.
        } else if (e.getSource() == saveButton) {
            int returnVal = fc.showSaveDialog(FileChooserDemo.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                
                // this is my code for writing the solutions to a text file
                try {                	
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));                    
                    for (int i = 0; i < communityPairs.size(); i++) {
                    	writer.write(communityPairs.get(i) + "\n");
                	}          
                    
                    writer.close();
                } catch(IOException exception) {
                	
                }
                //End of code by Cory Lawrence
                log.append("Saved: " + file.getName() + "." + newline);
            } else {
                log.append("Save command cancelled by user." + newline);
            }
            log.setCaretPosition(log.getDocument().getLength());
        }
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = FileChooserDemo.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("FileChooserDemo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new FileChooserDemo());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() {
                //Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE); 
                createAndShowGUI();
            }
        });
    }
}