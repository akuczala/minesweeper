//highscore window
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;
public class HighscoreFrame extends JFrame {
    private MineSweeperPanel mainPanel;
    private JPanel panel;
    private JTabbedPane tabs;
    private JPanel subPanel[];
    private JPanel buttonPanel;
    private JLabel[][] rankLabel, nameLabel, scoreLabel; 
    private JTextField nameField;
    private JButton ok, cancel;
    
    private final int n = 10;
    private final int nLevels = 4;
    //replaces spaces with code 0000 character (which I assume no one will enter)
    private final char spaceChar = Character.toChars(0)[0];
    
    //read/write variables
    private Scanner scan;
    private PrintWriter write;
    private String file = "scores.dat";
    private String[][] names;
    private int[][] scores;
    
    private int time, level, rank;
    
    public HighscoreFrame(MineSweeperPanel mainPanel, int time, int level) {
        //pass parameters, create arrays
        this.mainPanel = mainPanel;
        this.time = time; this.level = level;
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        tabs = new JTabbedPane();
        subPanel = new JPanel[nLevels];
        rankLabel = new JLabel[nLevels][n];
        nameLabel = new JLabel[nLevels][n];
        scoreLabel = new JLabel[nLevels][n];
        read();
        
        //create subpanel for each level
        for(int curLevel=0;curLevel<nLevels;curLevel++) {
            subPanel[curLevel] = new JPanel();
            subPanel[curLevel].setLayout(new GridLayout(n+1,3));
            //create rank, name and score label for each rank
            for(int i=0;i<n;i++) {
                rankLabel[curLevel][i] = new JLabel(""+(i+1)+".");
                subPanel[curLevel].add(rankLabel[curLevel][i]);
                //create text field for new rank
                if(rank==i && curLevel == level-1) {
                    nameField = new JTextField("");
                    subPanel[curLevel].add(nameField);
                }else{
                    nameLabel[curLevel][i] = new JLabel(names[curLevel][i]);
                    nameLabel[curLevel][i].setHorizontalAlignment(JLabel.CENTER);
                    subPanel[curLevel].add(nameLabel[curLevel][i]);
                }
                //do not show uncreated scores (time=1000)
                if(scores[curLevel][i]<1000)
                    scoreLabel[curLevel][i] = new JLabel(Integer.toString(scores[curLevel][i]));
                else
                    scoreLabel[curLevel][i] = new JLabel();
                scoreLabel[curLevel][i].setHorizontalAlignment(JLabel.CENTER);
                subPanel[curLevel].add(scoreLabel[curLevel][i]);
            }
            //tab names
            String tabName ="";
            if(curLevel==0)
                tabName = "Beginner";
            if(curLevel==1)
                tabName = "Intermediate";
            if(curLevel==2)
                tabName = "Expert";
            if(curLevel==3)
                tabName = "Ludicrous";
            tabs.addTab(tabName, subPanel[curLevel]);
        }
        tabs.setTabLayoutPolicy(tabs.SCROLL_TAB_LAYOUT);
        if(level!=0)
            tabs.setSelectedIndex(level-1);
        panel.add(tabs,BorderLayout.NORTH);
        buttonPanel = new JPanel();
        ok = new JButton("Ok"); cancel = new JButton("Cancel");
        ok.addActionListener(new ButtonListener());
        cancel.addActionListener(new ButtonListener());
        buttonPanel.add(ok);buttonPanel.add(cancel);
        panel.add(buttonPanel,BorderLayout.SOUTH);
        //manually set window size to show all components nicely
        panel.setPreferredSize(new Dimension(320,300));
        
        //set frame settings
        this.setTitle("Highscores");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.getContentPane().add(panel);
        this.pack();
        this.setVisible(true);
    }
    //read in highscores from file
    private void read() {
        names = new String[nLevels][n];
        scores = new int[nLevels][n];
        try {
            scan = new Scanner(new File(file));
            rank = -1;
            //scan through each level (line of text) and each name, score
            for(int curLevel=0;curLevel<nLevels;curLevel++) {
                int i = 0;
                while(scan.hasNext() && i<n) {
                    String curName = scan.next().replace(spaceChar, ' ');
                    int curScore = scan.nextInt();
                    //if new highscore, pop score in at current place
                    if(time<=curScore && level==curLevel+1 && rank==-1) {
                        rank = i;
                        names[curLevel][i] = "";
                        scores[curLevel][i] = time;
                        i++;
                    }
                    if(i<n) {
                        names[curLevel][i]= curName;
                        scores[curLevel][i] = curScore;
                    }
                    i++;
                }
                scan.nextLine();
            }
            scan.close();
        }
        //show appropiate error message if read error occurs
        catch(Exception e) {
            System.out.println("Highscore read error");
            System.out.println(e.toString());
        }
    }
    //save scores back in file
    private void save() {
        try {
            write = new PrintWriter(new File(file));
            for(int curLevel=0;curLevel<nLevels;curLevel++) {
                for(int i=0;i<n;i++) {
                    //save currently entered name into file
                    if(i==rank && level==curLevel+1)
                        write.print(nameField.getText().replace(' ', spaceChar)+" ");
                    else
                        write.print(names[curLevel][i].replace(' ', spaceChar)+" ");
                    write.print(Integer.toString(scores[curLevel][i])+" ");
                }
                write.println();
            }
        }
        //show appropiate error message
        catch(Exception e) {
            System.out.println("Highscores write error.");
            System.out.println(e.toString());
        }
        write.close();
        close();
    }
    //close window, resetting game if appropiate
    private void close() {
        if(time<1000)
            mainPanel.reset();
        this.dispose();
    }
    //button listener, save new highscore, close window
    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if(((JButton)event.getSource()).equals(ok)) {
                if(rank!=-1)
                    save();
            }
            close();
        }
        
    }
}