////Project 3
////Alexander Kuczala
////December 2008
import javax.swing.*;
public class Minesweeper {
    public static void main(String[] args) {
        //set up main frame
        JFrame frame = new JFrame("MineSweeper");
        System.out.println("Alexander Kuczala\n2008");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MineSweeperPanel(frame));
        frame.pack();
        frame.setVisible(true);
    }
}
