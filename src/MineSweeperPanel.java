import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.Timer;
public class MineSweeperPanel extends JPanel {
    private Square[][] square;
    private Square held;
    private Square highlight;
    private int xSquares, ySquares;
    private int zSquares, wSquares;
    private int xLength, yLength;
    private boolean twoD;
    private int mines;
    private int minesLeft;
    private int squaresCleared;
    //square side length
    private final int sqSize = 21;
    private boolean gameOver;
    private boolean firstClick;
    //frame and main panels
    private Frame frame;
    private MineField mineField;
    private JPanel gui;
    //menu items
    private JMenuBar menuBar;
    private JMenu fileMenu, difficultyMenu;
    private JMenuItem newGameChoice, 
            level1Choice, level2Choice, level3Choice, level4Choice, customChoice,
            scoresChoice, exitChoice;
    //window frames
    private CustomGameFrame customFrame;
    private HighscoreFrame scoreFrame;
    //other gui components
    private Timer timer;
    private int time;
    private JLabel timeDisplay;
    private JLabel mineDisplay;
    private JLabel face;
    private JPanel facePanel;
    //graphics
    private ImageIcon faceNormalIcon, faceSweepIcon, facePressIcon, faceLoseIcon, faceWinIcon;
    private ImageIcon buttonIcon, flagIcon, xIcon, mineIcon, boomIcon;
    
    public MineSweeperPanel(JFrame frame) {
        //create reference to frame for resizing
        this.frame = frame;
        //create gui
        gui = new JPanel();
        gui.setBackground(Color.black);
        gui.setLayout(new BorderLayout());
        //create menu
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File"); difficultyMenu = new JMenu("Difficulty");
        newGameChoice = new JMenuItem("New Game",KeyEvent.VK_F2);
        level1Choice = new JMenuItem("Beginner",KeyEvent.VK_B);
        level2Choice = new JMenuItem("Intermediate",KeyEvent.VK_I);
        level3Choice = new JMenuItem("Expert",KeyEvent.VK_E);
        level4Choice = new JMenuItem("Ludicrous",KeyEvent.VK_L);
        customChoice = new JMenuItem("Custom...",KeyEvent.VK_C);
        scoresChoice = new JMenuItem("Highscores",KeyEvent.VK_F3);
        exitChoice = new JMenuItem("Quit",KeyEvent.VK_Q);
        newGameChoice.addActionListener(new MenuListener());
        level1Choice.addActionListener(new MenuListener());
        level2Choice.addActionListener(new MenuListener());
        level3Choice.addActionListener(new MenuListener());
        level4Choice.addActionListener(new MenuListener());
        customChoice.addActionListener(new MenuListener());
        scoresChoice.addActionListener(new MenuListener());
        exitChoice.addActionListener(new MenuListener());
        difficultyMenu.add(level1Choice);difficultyMenu.add(level2Choice);
        difficultyMenu.add(level3Choice);difficultyMenu.add(level4Choice);
        difficultyMenu.add(customChoice);
        fileMenu.add(newGameChoice);fileMenu.add(difficultyMenu);
        fileMenu.add(scoresChoice);fileMenu.add(exitChoice);
        menuBar.add(fileMenu);
        gui.add(menuBar,BorderLayout.NORTH);
        //create timer
        timer = new Timer(1000, new TimerListener());
        timeDisplay = new JLabel("000");
        timeDisplay.setBackground(Color.black);
        timeDisplay.setForeground(Color.green);
        timeDisplay.setFont(new Font("Courier",Font.BOLD,18));
        timeDisplay.setHorizontalAlignment(JLabel.CENTER);
        gui.add(timeDisplay,BorderLayout.EAST);
        //create mine display
        mineDisplay = new JLabel();
        mineDisplay.setBackground(Color.black);
        mineDisplay.setForeground(Color.red);
        mineDisplay.setFont(new Font("Courier",Font.BOLD,18));
        mineDisplay.setHorizontalAlignment(JLabel.CENTER);
        gui.add(mineDisplay,BorderLayout.WEST);
        //create face button
        faceNormalIcon = new ImageIcon("faceNormal.png");
        faceSweepIcon = new ImageIcon("faceSweep.png");
        facePressIcon = new ImageIcon("facePress.png");
        faceLoseIcon = new ImageIcon("faceLose.png");
        faceWinIcon = new ImageIcon("faceWin.png");
        face = new JLabel(faceNormalIcon);
        face.addMouseListener(new FaceListener());
        facePanel = new JPanel();
        facePanel.setBackground(Color.black);
        facePanel.add(face);
        gui.add(facePanel,BorderLayout.CENTER);
        this.setLayout(new BorderLayout());
        this.add(gui,BorderLayout.NORTH);
        //load square graphics
        buttonIcon = new ImageIcon("button.png");
        flagIcon = new ImageIcon("flag.png");
        xIcon = new ImageIcon("x.png");
        mineIcon = new ImageIcon("mine.png");
        boomIcon = new ImageIcon("boom.png");
        //start game
        //newGame(10,8,6,4,80);
        newGame(6,4,10,8,80);
    }
    //reset game with settings
    public void reset(int xSquares, int ySquares, int mines) {
        face.setIcon(faceNormalIcon);
        this.remove(mineField);
        newGame(xSquares,ySquares,mines);
        frame.pack();
    }
    public void reset(int xSquares, int ySquares, int zSquares, int wSquares, int mines) {
        face.setIcon(faceNormalIcon);
        this.remove(mineField);
        newGame(xSquares,ySquares,zSquares,wSquares,mines);
        frame.pack();
    }
    //reset game with current settings
    public void reset() {
        reset(xSquares,ySquares,zSquares,wSquares,mines);
    }
    //create new game, mineField panel
    private void newGame(int xSquares, int ySquares, int zSquares,int wSquares, int mines) {
        this.xSquares = xSquares; this.ySquares = ySquares;
        this.zSquares = zSquares; this.wSquares = wSquares;
        this.mines = mines;
        twoD = zSquares==1 && wSquares==1;
        xLength = xSquares*zSquares;
        yLength = ySquares*wSquares;
        //create mine field
        mineField = new MineField();
        mineField.setPreferredSize(new Dimension(sqSize*xLength,sqSize*yLength));
        mineField.setBackground(new Color(55,55,55));
        mineField.setLayout(new GridLayout(yLength,xLength));
        mineField.setFocusable(true);
        //Create squares
        square = new Square[yLength][xLength];
         for(int j=0;j<yLength;j++) for(int i=0;i<xLength;i++) {
            square[j][i] = new Square(i%xSquares,j%ySquares,i/xSquares,j/ySquares,
                    sqSize,buttonIcon,flagIcon,xIcon,mineIcon,boomIcon);
            mineField.add(square[j][i]);       
        }
        held = null;
        //place mines
        int minesSet = 0;
        while(minesSet<mines) {
            Random rand = new Random();
            //choose random square, exclude first for first click replacement
            int xRand = rand.nextInt(xLength);
            int yRand = rand.nextInt(yLength);
            if(!square[yRand][xRand].hasMine() && !(xRand == 0 && yRand == 0)) {
                square[yRand][xRand].setMine();
                minesSet++;
            }
        }
        //find neighbors of each square
        for(int i=0;i<xLength;i++) for(int j=0;j<yLength;j++)
            square[j][i].findNeighbors(square);
        //minefield listeners
        mineField.addMouseListener(new ButtonListener());
        mineField.addMouseMotionListener(new ButtonListener());
        this.add(mineField,BorderLayout.SOUTH);
        
        //initial game conditions
        gameOver=false;
        firstClick = true;
        time = 0;
        squaresCleared=0;
        timeDisplay.setText("000");
        minesLeft = mines;
        updateMineDisplay();
        timer.start();
    }
    private void newGame(int xSquares, int ySquares, int mines) {
        newGame(xSquares,ySquares,1,1,mines);
    }
    //end game
    private void endGame() {
        gameOver = true;
        timer.stop();
        //set all squares to endgame status
        for(int i=0;i<xSquares*zSquares;i++) for(int j=0;j<ySquares*wSquares;j++)
            square[j][i].endGame();
        //if player has cleared/flagged all squares, it is a win
        if(squaresCleared==xSquares*ySquares*zSquares*wSquares) {
            face.setIcon(faceWinIcon);
            //open highscores if appropiate
            int level = getLevel();
            if(level!=0)
                scoreFrame = new HighscoreFrame(this,time,level);
        }
        else {
            face.setIcon(faceLoseIcon);
        }
    }
    //open highscores from menu
    private void openHighscores() {
        scoreFrame = new HighscoreFrame(this,9001,getLevel());
    }
    //determine current level of difficulty, if any
    private int getLevel() {
        //determine level
        int level = 0;
        if(xSquares==8 && ySquares==8 && mines==10)
            level = 1;
        if(xSquares==16 && ySquares==16 && mines==40)
            level = 2;
        if(xSquares==30 && ySquares==16 && mines==99)
            level = 3;
        if(xSquares==60 && ySquares==32 && mines==643)
            level = 4;
        return level;
    }
    //update the mine counter
    private void updateMineDisplay() {
        if(minesLeft<0)
            mineDisplay.setText("000");
        else
            if(minesLeft<10)
                   mineDisplay.setText("00"+minesLeft);
                else
                    if(minesLeft<100)
                       mineDisplay.setText("0"+minesLeft);
                    else
                           mineDisplay.setText(""+minesLeft);
    }
    //open settings options
    private void openCustomFrame() {
        customFrame = new CustomGameFrame(this,xSquares,ySquares,mines);
    }
    //close program
    private void exit() {
        System.exit(0);
    }
    //minefield panel
    private class MineField extends JPanel {
        //draw bounds of cleared squares
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            for(int i=0;i<xLength;i++) for(int j=0;j<yLength;j++)
                    square[j][i].draw(g);
            repaint();
        }
    }
    //mouse event listener
    private class ButtonListener implements MouseListener, MouseMotionListener {
        //hold cursored-over square, change face icon
        private void holdSquare(int i, int j) {
            if(square[j][i].isButton()) {
                face.setIcon(faceSweepIcon);
                square[j][i].hold();
                held = square[j][i];
            }
            else {
                square[j][i].highlightNeighbors(true);
                highlight = square[j][i];
            }
        }
        //release previously cursored-over square
        private void releaseSquare() {
            if(held!=null) {
                held.release();
                held=null;
                }
            if(highlight!=null) {
                highlight.highlightNeighbors(false);
                highlight = null;
            }
        }
        //hold pressed square
        public void mousePressed(MouseEvent e) {
            if(!gameOver) {
                int i = e.getX()/(sqSize);
                int j = e.getY()/(sqSize);
                if(i<0 || i>=xLength || j<0 || j>=yLength)
                    return;
                if(e.getButton()==MouseEvent.BUTTON1)
                    holdSquare(i,j);
            }
        }
        //left and right click events
        public void mouseReleased(MouseEvent e) {
            if(!gameOver) {
                int i = e.getX()/sqSize;
                int j = e.getY()/sqSize;
                //do nothing if index out of range
                if(i<0 || i>=xLength || j<0 || j>=yLength)
                    return;
                //release held square
                releaseSquare();
                //set face to normal
                face.setIcon(faceNormalIcon);
                //if square clickable and left click
                if(e.getButton()==MouseEvent.BUTTON1 && square[j][i].isButton()) {
                    //if this is a first click, and a mine, move mine to 0,0
                    if(firstClick) {
                        if(square[j][i].hasMine()) {
                            square[j][i].removeMine();
                            square[0][0].addMine();
                        }
                        firstClick = false;
                    }
                    //call square click event, add cleared squares to total
                    squaresCleared += square[j][i].pressed();
                    //end game if square has mine
                    if(square[j][i].hasMine())
                        endGame();
                }
                //right mouse button, toggle flags, change cleared squares accordingly
                if(e.getButton() == MouseEvent.BUTTON3) {
                    int flag =square[j][i].toggle();
                    if(square[j][i].hasMine())
                        squaresCleared-=flag;
                    minesLeft+=flag;
                    updateMineDisplay();
                }
                //win event, if total squares cleared equals size of board
                if(squaresCleared == xSquares*ySquares*zSquares*wSquares)
                    endGame();
            }
        }
        //release previous square and hold next square if mouse dragged
        public void mouseDragged(MouseEvent e) {
            if(!gameOver) {
                int i = e.getX()/(sqSize);
                int j = e.getY()/(sqSize);
                //release square, set face to normal if mouse goes out of screen
                if(i<0 || i>=xLength || j<0 || j>=yLength) {
                    releaseSquare();
                    face.setIcon(faceNormalIcon);
                    return;
                }

                releaseSquare();
                holdSquare(i,j);
            }
        }
        //unused events
        public void mouseMoved(MouseEvent e) {}        
        public void mouseClicked(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}

    }
    //menu events
    private class MenuListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
          if(((JMenuItem)e.getSource()).equals(newGameChoice))
               reset();
          if(((JMenuItem)e.getSource()).equals(level1Choice))
               reset(8,8,10);
          if(((JMenuItem)e.getSource()).equals(level2Choice))
               reset(16,16,40);
          if(((JMenuItem)e.getSource()).equals(level3Choice))
               reset(30,16,99);
          if(((JMenuItem)e.getSource()).equals(level4Choice))
               reset(60,32,643);
          if(((JMenuItem)e.getSource()).equals(customChoice))
              openCustomFrame();
          if(((JMenuItem)e.getSource()).equals(scoresChoice))
              openHighscores();
          if(((JMenuItem)e.getSource()).equals(exitChoice))
              exit();
        }
        
    }
    //face button events
    private class FaceListener implements MouseListener {

        //change face icon
        public void mousePressed(MouseEvent e) {
            face.setIcon(facePressIcon);
        }
        //reset game
        public void mouseReleased(MouseEvent e) {
            reset();
        }
        
        //unused events
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mouseClicked(MouseEvent e) {}
        
    }
    
    //timer
    private class TimerListener implements ActionListener {
        //increment timer, display time appropiately
        public void actionPerformed(ActionEvent e) {
            if(time<999)
                time++;
            else
                time=999;
            if(time<10)
               timeDisplay.setText("00"+time);
            else
                if(time<100)
                   timeDisplay.setText("0"+time);
                else
                       timeDisplay.setText(""+time);
        }
        
    }
}