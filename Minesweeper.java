import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;


public class Minesweeper extends JFrame {
    private JButton[][] boardButtons;
    private Cell[][] gameboard;

    public Minesweeper() {
        setTitle("Minesweeper");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create the game board with buttons
        int rows = 24;
        int cols = 24;
        gameboard = new Cell[24][24];
        boardButtons = new JButton[rows][cols];
        JPanel boardPanel = new JPanel(new GridLayout(rows+1, cols));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                boardButtons[i][j] = new JButton();
                boardButtons[i][j].addActionListener(new ButtonClickListener(i, j));
                boardPanel.add(boardButtons[i][j]);
            }
        }

        add(boardPanel, BorderLayout.CENTER);
        setSize(600, 625);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class GameBoard {

        public GameBoard(int row, int col, int mine_num, int start_x , int start_y) {
            Random rand = new Random();
            List<Integer> choose = new ArrayList<Integer>();
            for(int i=0;i<row;i++){
                for(int j=0;j<col;j++){
                    gameboard[i][j]=new Cell(i,j);
                    choose.add(i*24+j);
                }
            }
            choose.removeAll(get_neighbour(start_x, start_y));
            for (int i = 0; i < mine_num ; i++) {
                int randomIndex = rand.nextInt(choose.size());
                int randomElement = choose.get(randomIndex);
                gameboard[Math.floorDiv(randomElement, 24)][randomElement%24].type="mine";
                choose.remove(randomIndex);
            }
            for(int i=0;i<row;i++){
                for(int j=0;j<col;j++){
                    List<Integer> neighbour=get_neighbour(i, j);
                    int mine=0;
                    for (int k = 0; k < neighbour.size() ; k++) {
                        if (gameboard[Math.floorDiv(neighbour.get(k), 24)][neighbour.get(k)%24].type=="mine")
                            mine++;
                    }
                    if (mine >0)
                        gameboard[i][j].type=Integer.toString(mine);
                }
            }
        }


        private List<Integer> get_neighbour(int x, int y) {
            List<Integer> output = new ArrayList<Integer>();
            if (x-1>=0 && y-1>=0) 
                output.add((x-1)*24+y-1);
            if (x-1>=0) 
                output.add((x-1)*24+y);
            if ((x-1)<24 && y+1>=0) 
                output.add((x-1)*24+y+1);
            if (y+1<24) 
                output.add(x*24+y+1);
            if (x+1<24 && y+1<24) 
                output.add((x+1)*24+y+1);
            if (x+1<24) 
                output.add((x+1)*24+y);
            if (x+1<24 && y-1>=0) 
                output.add((x+1)*24+y-1);
            if (y-1>0) 
                output.add(x*24+y-1);
            return output;
        }

    }

    public class Cell {
        public String type;
        public int x;
        public int y;
        public boolean revealed;

        public Cell(int x, int y) {
            this.x=x;
            this.y=y;
            this.type="empty";
            this.revealed=false;
        }
    }

    private class ButtonClickListener implements ActionListener {
        private int row;
        private int col;
        private boolean started = false;
      

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            // Handle button click event
            JButton clickedButton = (JButton) e.getSource();
            if (clickedButton == boardButtons[row][col] && !gameboard[row][col].revealed) {
                if (!started) {
                    new GameBoard(24,24,99,row,col);
                    started=true;
                }
                gameboard[row][col].revealed=true;
                clickedButton.setEnabled(false);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Minesweeper();
            }
        });
    }
}
