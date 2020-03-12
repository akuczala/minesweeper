//Square class
//represents individual square in mine field
import java.awt.*;
import javax.swing.*;
//square is extension of JLabel
public class Square extends JLabel {
    
    //location of square on grid (not pixel distance)
    private int x, y, z, w;
    private int xSquares, ySquares, zSquares, wSquares;
    //side length of square
    private int sqSize;
    //has mine or not
    private boolean mine;
    //adjacent squares
    private Square[] neighbor;
    //number of adjacent mines (displayed as number)
    private int neighborMines;
    //highlighted
    private boolean highlight;
    //possible states of square
    private enum State {button,held,clear,flag,boom,endgame,X,revealMine};
    private State state;
    //images for square
    private ImageIcon buttonIcon, flagIcon, xIcon, mineIcon, boomIcon;
    
    //read in x, y on square grid, size, and graphics
    public Square(int x,int y,int z,int w,int sqSize,
            ImageIcon buttonIcon, ImageIcon flagIcon, ImageIcon xIcon,
            ImageIcon mineIcon, ImageIcon boomIcon) {
        this.x = x; this.y = y;
        this.z = z; this.w = w;
        xSquares = 6;
        ySquares = 4;
        zSquares = 10;
        wSquares = 8;
        this.sqSize = sqSize;
        //pass graphics
        this.buttonIcon = buttonIcon;this.flagIcon = flagIcon; this.xIcon = xIcon;
        this.mineIcon = mineIcon; this.boomIcon = boomIcon;
        highlight = false;
        mine = false;
        setButtonState();
    }
    //find adjacent squares in grid
    public void findNeighbors(Square[][] sq) {
        int width = sq[0].length;
        int height = sq.length;
        //indices of relative locations of squares
        // 0 1 2
        // 3 4 5
        // 6 7 8
        //each boolean indicates whether corresponding neighbor exists
        boolean[] choose = new boolean[81];
        //x and y offsets of neighbors in array from this square
        //int[][] offset = {{-1,-1},{0,-1},{1,-1},
          //                 {-1, 0}       ,{1, 0},
            //               {-1, 1},{0, 1},{1, 1}};
        int[][] offset = {{-1,-1},{0,-1},{1,-1},
                           {-1, 0},{0,0},{1, 0},
                           {-1, 1},{0, 1},{1, 1}};
        //default all neighboring squares to exist by default
        for(int i=0;i<81;i++) choose[i] = true;
        //center
        choose[9*4+4] = false;
        //set nonexisting neighboring squares in choose false
        for(int u=0;u<9;u++) {
            if(x==0)
                choose[u*9+0] = choose[u*9+3] = choose[u*9+6] = false;
            if(x==xSquares-1)
                choose[u*9+2] = choose[u*9+5] = choose[u*9+8] = false;
            if(y==0)
                choose[u*9+0] = choose[u*9+1] = choose[u*9+2] = false;
            if(y==ySquares-1)
                choose[u*9+6] = choose[u*9+7] = choose[u*9+8] = false;
        }
        for(int i=0;i<9;i++) {
            if(z==0)
                choose[9*0+i] = choose[9*3+i] = choose[9*6+i] = false;
            if(z==zSquares-1)
                choose[9*2+i] = choose[9*5+i] = choose[9*8+i] = false;
            if(w==0)
                choose[9*0+i] = choose[9*1+i] = choose[9*2+i] = false;
            if(w==wSquares-1)
                choose[9*6+i] = choose[9*7+i] = choose[9*8+i] = false;
        }
        //count number of existing neighboring squares
        int count = 0;
        for(boolean cur: choose)
            if(cur) count ++;
        //add neighboring squares that exist
        neighbor = new Square[count];
        int cur = 0;
        //System.out.println("Square:" + x + " " + y + " " + z+ " " + w);
        for(int i = 0;i<81;i++)
            if(choose[i]) {
                int xOffs = offset[i%9][0];
                int yOffs = offset[i%9][1];
                int zOffs = offset[i/9][0];
                int wOffs = offset[i/9][1];
                int xCoord = x+z*xSquares+xOffs+zOffs*xSquares;
                int yCoord = y+w*ySquares+yOffs+wOffs*ySquares;
                /*

                */
                 //neighbor[cur] = sq[y+yOffs][x+xOffs];
                if(xCoord>xSquares*zSquares || yCoord>ySquares*wSquares
                        || xCoord < 0 || yCoord<0) {
                    System.out.println("out");
                System.out.println("i: " + i);
                System.out.println("i%9: " + i%9);
                System.out.println("[i%9]: " + offset[i%9][1]);
                System.out.println("xOffs: " + xOffs);
                System.out.println("yOffs: " + yOffs);
                System.out.println("zOffs: " + zOffs);
                System.out.println("wOffs: " + wOffs);

                System.out.println("xCoord: " +xCoord);
                System.out.println("yCoord: " +yCoord);
                }
                 neighbor[cur] = sq[yCoord][xCoord];
                cur++;
            }
       countMines();
    }
    //when clicked on, returns number of total cleared squares
    public int pressed() {
        int cleared = 0;
        if(state == state.button) {
            if(mine)
                setBoomState();
            else {
                if(neighborMines==0)
                    cleared=passSweep(cleared);
                else
                    cleared=clear(cleared);
            }
        }
        return cleared;
    }
    //square state setting functions, changes state and icon
    private void setButtonState() {
        state = State.button;
        this.setIcon(buttonIcon);
        this.setVisible(true);
    }
    private void setFlagState() {
        state = State.flag;
        this.setIcon(flagIcon);
        this.setVisible(true);
    }
    private void setRevealMineState() {
        state = State.revealMine;
        this.setIcon(mineIcon);
    }
    private void setBoomState() {
        state = State.boom;
        this.setIcon(boomIcon);
    }
    private void setXState() {
        state =  State.X;
        this.setIcon(xIcon);
    }
    //endgame states
    public void endGame() {
        if(state == State.button && mine)
            setRevealMineState();
        if(state == State.flag && !mine)
            setXState();
    }
    public void highlightNeighbors(boolean highlight) {
        for(Square cur: neighbor)
            cur.highlight(highlight);
    }
    public void highlight(boolean highlight) {
        this.highlight  = highlight;
    }
    //dissapear when held
    public void hold() {
        if(state == State.button) {
            state = State.held;
            this.setVisible(false);
        }
    }
    //reappear when released
    public void release() {
        state = State.button;
        this.setVisible(true);
    }
    //toggle between flagged and nonflagged
    public int toggle() {
        if(state == State.button) {
            setFlagState();
            return -1;
        }
        else
            if(state == State.flag) {
                setButtonState();
                return 1;
            }
        return 0;
    }
    //remove mine from square, update neighbors' counts
    public void removeMine() {
        mine = false;
        for(Square cur : neighbor)
            cur.countMines();
    }
    //add mine to square, update neighbors' counts
    public void addMine() {
        mine = true;
        for(Square cur : neighbor)
            cur.countMines();
    }
    //clear square, check neighbors for squares with 0 neighbor mines,
    // clear these similarly, recursively
    private int passSweep(int cleared) {
        cleared=clear(cleared);
        for(Square cur : neighbor)
            if(neighborMines==0 && cur.hasMine() == false && cur.state == State.button)
                cleared=cur.passSweep(cleared);
        return cleared;
    }
    //return neighboring mines
    public int getNeighborMines() {
        return neighborMines;
    }
    //update neighbors' counts of mines
    public int countMines() {
        int count = 0;
        for(Square cur : neighbor)
            if(cur.hasMine())
                count ++;
        neighborMines = count;
        return count;
    }
    //returns if square is in button state
    public boolean isButton() {
        return state==State.button;
    }
    //sets mine, initial placement
    public void setMine() {
        mine = true;
    }
    //returns whether or not square has mine
    public boolean hasMine() {
        return mine;
    }
    //clear the square, display number of neighboring mines
    //return updated number of squares cleared 
    public int clear(int cleared) {
        this.state = State.clear;
        if(neighborMines==0)
            this.setVisible(false);
        else {
            //number colors
            Color c = Color.black;
            switch ((neighborMines-1)%8+1) {
                case 1: c = Color.blue;break;
                case 2: c = Color.green;break;
                case 3: c = Color.orange;break;
                case 4: c = Color.magenta;break;
                case 5: c = Color.red;break;
                case 6: c = Color.cyan;break;
                case 7: c = Color.pink;break;
                case 8: c = Color.black;break;
            }
            this.setIcon(null);
            this.setVisible(true);
            this.setForeground(c);
            this.setFont(new Font("Courier",Font.BOLD, 16));
            this.setHorizontalAlignment(JLabel.CENTER);
            this.setText(""+neighborMines);
        }
        return cleared+1;
    }
    //draw bounding square if clear
    public void draw(Graphics g) {
        if(state==State.clear) {
            int drawx = sqSize*(x+z*xSquares);
            int drawy = sqSize*(y+w*ySquares);
            g.setColor(Color.black);
            g.drawRect(drawx,drawy, sqSize, sqSize);
            if(x==0 || y ==0 || x == xSquares-1 || y == ySquares-1)
                g.setColor(Color.white);
            if(highlight) {
                g.setColor(Color.yellow);
                g.drawRect(drawx+1,drawy+1, sqSize-1, sqSize-1);
            }
            if(x==0)
                g.drawLine(drawx,drawy,drawx,drawy+sqSize);
            if(y==0)
                g.drawLine(drawx,drawy,drawx+sqSize,drawy);
            if(x==xSquares-1)
                g.drawLine(drawx+sqSize,drawy,drawx+sqSize,drawy+sqSize);
            if(y==ySquares-1)
                g.drawLine(drawx,drawy+sqSize, drawx+sqSize, drawy+sqSize);
        }
    }
}