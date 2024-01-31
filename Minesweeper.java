import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;


public class Minesweeper extends JFrame {
    private JButton[][] boardButtons;
    private Cell[][] gameboard = new Cell[25][25];
    private Color[] colour_list={new Color(0,0,255), new Color(0,128,0), new Color(255,0,0), new Color(0,0,128), new Color(128,0,0), new Color(0,128,128), new Color(0,0,0), new Color(128,128,128)};
    private boolean started = false;

    public Minesweeper() {
        setTitle("Minesweeper");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create the game board with buttons
        int rows = 24;
        int cols = 24; 
        boardButtons = new JButton[rows][cols];
        JPanel boardPanel = new JPanel(new GridLayout(rows+1, cols));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                boardButtons[i][j] = new JButton();
                boardButtons[i][j].addActionListener(new ButtonClickListener(i, j));
                boardButtons[i][j].setBackground(Color.darkGray);
                boardButtons[i][j].setMargin(new Insets(0, 0, 0, 0));
                boardPanel.add(boardButtons[i][j]);
            }
        }

        add(boardPanel, BorderLayout.CENTER);
        setSize(600, 625);
        setLocationRelativeTo(null);
        setVisible(true);
    }


        
    private void gameboard_init(int row, int col, int mine_num, int start_x , int start_y) {
        Random rand = new Random();
        List<Integer> choose = new ArrayList<Integer>();
        for(int i=0;i<row;i++){
            for(int j=0;j<col;j++){
                gameboard[i][j]=new Cell(i,j);
                choose.add(i*24+j);
            }
        }
        choose.removeAll(get_neighbour(start_x, start_y));
        choose.remove(Integer.valueOf(start_x*24+start_y));
        for (int i = 0; i < mine_num ; i++) {
            int randomIndex = rand.nextInt(choose.size());
            int randomElement = choose.get(randomIndex);
            gameboard[Math.floorDiv(randomElement, 24)][randomElement%24].type="mine";
            choose.remove(randomIndex);
        }
        for(int i=0;i<row;i++){
            for(int j=0;j<col;j++){
                if (gameboard[i][j].type=="mine")
                    continue;
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
        if (x-1>=0 && y+1<24) 
            output.add((x-1)*24+y+1);
        if (y+1<24) 
            output.add(x*24+y+1);
        if (x+1<24 && y+1<24) 
            output.add((x+1)*24+y+1);
        if (x+1<24) 
            output.add((x+1)*24+y);
        if (x+1<24 && y-1>=0) 
            output.add((x+1)*24+y-1);
        if (y-1>=0) 
            output.add(x*24+y-1);
        return output;
    }

    private void flooding(int x, int y) {
        List<Integer> neighbour = get_neighbour(x, y);
        for (int i=0;i<neighbour.size();i++){
            int a=Math.floorDiv(neighbour.get(i), 24);
            int b=neighbour.get(i)%24;
            if (boardButtons[a][b].isEnabled())
                reveal(a,b);
        }
    }

    private void reveal(int row, int col){
        boardButtons[row][col].setEnabled(false);
        boardButtons[row][col].setBackground(Color.white);
        if (gameboard[row][col].type=="empty"){
            flooding(row,col);
        }
        else if (gameboard[row][col].type=="mine") {
            boardButtons[row][col].setBackground(Color.red);
        }
        else{
            boardButtons[row][col].setText(gameboard[row][col].type);
            boardButtons[row][col].setUI(new MetalButtonUI() {
                protected Color getDisabledTextColor() {
                    return colour_list[Integer.parseInt(gameboard[row][col].type)-1];
                }
            });
            boardButtons[row][col].setFont(new FontUIResource("Arial", Font.BOLD, 20));
        }
    }

    private void full_reveal(){
        for (int row=0;row<24;row++){
            final int currentRow = row;
            for (int col=0;col<24;col++){
                final int currentCol = col;
                boardButtons[row][col].setEnabled(false);
                boardButtons[row][col].setBackground(Color.white);
                if (gameboard[row][col].type=="empty"){
                    ;
                }
                else if (gameboard[row][col].type=="mine") {
                    boardButtons[row][col].setBackground(Color.red);
                }
                else{
                    boardButtons[row][col].setText(gameboard[row][col].type);
                    boardButtons[row][col].setUI(new MetalButtonUI() {
                        protected Color getDisabledTextColor() {
                            return colour_list[Integer.parseInt(gameboard[currentRow][currentCol].type)-1];
                        }
                    });
                    boardButtons[row][col].setFont(new FontUIResource("Arial", Font.BOLD, 20));
                }
            }
        }
    }

    public class Cell {
        public String type;
        public int x;
        public int y;
        public boolean flagged;

        public Cell(int x, int y) {
            this.x=x;
            this.y=y;
            this.type="empty";
            this.flagged=false;
        }

        private void flag() {
            if (flagged){
                boardButtons[x][y].setEnabled(true);
                flagged=false;
            }
            else{
                boardButtons[x][y].setEnabled(false);
                flagged=true;
                try {
                    Image img = ImageIO.read(getClass().getResource("resources/water.bmp"));
                    button.setIcon(new ImageIcon(img));
                  } catch (Exception ex) {
                    System.out.println(ex);
                  }
                Image img = ImageIO.read(getClass().getResource("resources/water.bmp"));
                boardButtons[x][y].setIcon(new ImageIcon(img));
            }
        }
    }

    private class ButtonClickListener implements ActionListener {
        private int row;
        private int col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            // Handle button click event
            JButton clickedButton = (JButton) e.getSource();
            if (clickedButton == boardButtons[row][col]) {
                if (!started) {
                    gameboard_init(24,24,99,row,col);
                    started=true;
                }
                //full_reveal();
                reveal(row,col);
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
