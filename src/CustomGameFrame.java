//window to customize game settings
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
public class CustomGameFrame extends JFrame {
    private MineSweeperPanel mainPanel;
    private JPanel panel;
    private JLabel widthChoiceLabel, heightChoiceLabel, mineChoiceLabel;
    private JTextField widthChoice, heightChoice, mineChoice;
    private JButton start, cancel;
    
    private int width, height, mines;
    
    public CustomGameFrame(MineSweeperPanel mainPanel,int width,int height,int mines) {
        //create panel, text fields, buttons
        this.mainPanel = mainPanel;
        this.width = width; this.height = height; this.mines = mines;
        panel = new JPanel();
        panel.setLayout(new GridLayout(3,2));
        widthChoiceLabel = new JLabel("Width");
        heightChoiceLabel = new JLabel("Height");
        mineChoiceLabel = new JLabel("Mines");
        widthChoice = new JTextField(""+width);
        heightChoice = new JTextField(""+height);
        mineChoice = new JTextField(""+mines);
        start = new JButton("Start"); cancel = new JButton("Cancel");
        start.addActionListener(new ButtonListener());
        cancel.addActionListener(new ButtonListener());
        panel.add(widthChoiceLabel);panel.add(heightChoiceLabel);panel.add(mineChoiceLabel);
        panel.add(widthChoice);panel.add(heightChoice);panel.add(mineChoice);
        panel.add(start);panel.add(cancel);
        
        //set frame settings
        this.setTitle("Custom Settings");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.getContentPane().add(panel);
        this.pack();
        this.setVisible(true);
    }
    //close window
    private void close() {
        this.dispose();
    }
    //buttons to set settings or cancel
    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if(((JButton)event.getSource()).equals(start)) {
                //attempt to parse numbers from input
                try {
                width = Integer.parseInt(widthChoice.getText());
                height = Integer.parseInt(heightChoice.getText());
                mines = Integer.parseInt(mineChoice.getText());
                //bounds on mines, height, width
                if(mines>width*height-1 || height > 32 || width > 60)
                    return;
                }
                //do nothing if input text is invalid
                catch(Exception e) {
                    return;
                }
                mainPanel.reset(width, height, mines);
            }
            close();
        }
        
    }
}