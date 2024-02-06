import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

public class Minesweeper extends JFrame {
    private JButton[][] boardButtons;
    private Cell[][] gameboard;
    private final Color[] colour_list={new Color(0,0,255), new Color(0,128,0), new Color(255,0,0), new Color(0,0,128), new Color(128,0,0), new Color(0,128,128), new Color(0,0,0), new Color(128,128,128)};
    private boolean started;
    private int seconds;
    private Timer timer;

    public Minesweeper() {
        setTitle("Minesweeper");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        started=false;
        seconds=0;
        int rows = 24;
        int cols = 24; 
        boardButtons = new JButton[rows][cols];
        gameboard = new Cell[rows][cols];
        JPanel boardPanel = new JPanel(new GridLayout(rows+1, cols));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                boardButtons[i][j] = new JButton();
                boardButtons[i][j].addMouseListener(new ButtonClickListener(i, j));
                boardButtons[i][j].setBackground(Color.darkGray);
                boardButtons[i][j].setMargin(new Insets(0, 0, 0, 0));
                boardPanel.add(boardButtons[i][j]);

                gameboard[i][j]=new Cell(i,j);
            }
        }

        JLabel timerLabel = new JLabel("0:00");
        timerLabel.setSize(30,30);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timer = new Timer(1000, e -> {
            seconds++;
            timerLabel.setText(Math.floorDiv(seconds, 60)+":"+seconds%60);
        });
        add(boardPanel, BorderLayout.CENTER);
        add(timerLabel, BorderLayout.NORTH);
        setSize(800, 850);
        setLocationRelativeTo(null);
        setVisible(true);
    }
        
    private void gameboard_init(int row, int col, int mine_num, int start_x , int start_y) {
        Random rand = new Random();
        List<Integer> choose = new ArrayList<Integer>();
        for(int i=0;i<row;i++){
            for(int j=0;j<col;j++){
                choose.add(i*24+j);
            }
        }
        choose.removeAll(get_neighbour(start_x, start_y));
        choose.remove(Integer.valueOf(start_x*24+start_y));
        for (int i = 0; i < mine_num ; i++) {
            int randomIndex = rand.nextInt(choose.size());
            int randomElement = choose.get(randomIndex);
            gameboard[Math.floorDiv(randomElement, 24)][randomElement%24].type="mine";
            //boardButtons[Math.floorDiv(randomElement, 24)][randomElement%24].setEnabled(false);
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
        timer.start();
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

        if (gameboard[row][col].type=="empty")
            flooding(row,col);

        else if (gameboard[row][col].type=="mine") {
            boardButtons[row][col].setBackground(Color.red);
            game_end(false);
        }

        else {
            boardButtons[row][col].setText(gameboard[row][col].type);
            boardButtons[row][col].setUI(new MetalButtonUI() {
                protected Color getDisabledTextColor() {
                    return colour_list[Integer.parseInt(gameboard[row][col].type)-1];
                }
            });
            boardButtons[row][col].setFont(new FontUIResource("Arial", Font.BOLD, 24));
            if (check_game_end())
                game_end(true);
        }
    }

    private void full_reveal(){
        for (int row=0;row<24;row++){
            final int currentRow = row;
            for (int col=0;col<24;col++){
                final int currentCol = col;
                boardButtons[row][col].setEnabled(false);
                boardButtons[row][col].setBackground(Color.white);
                boardButtons[row][col].setIcon(null);
                if (gameboard[row][col].type=="empty"){}
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

    private void game_end(boolean win) {
        timer.stop();
        if (win) {
            full_reveal();
            int option = JOptionPane.showOptionDialog(null, "Time : "+Math.floorDiv(seconds, 60)+":"+seconds%60+"\nRetry?", "You Win", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (option == JOptionPane.YES_OPTION) 
                reload();
            else 
                System.exit(0);
        }
        else{
            for (int i=0;i<24;i++){
                for (int j=0;j<24;j++){
                    boardButtons[i][j].setEnabled(false);
                    if (gameboard[i][j].type=="mine")
                        boardButtons[i][j].setBackground(Color.red);
                }
            }
            int option = JOptionPane.showOptionDialog(null, "Retry?", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (option == JOptionPane.YES_OPTION) 
                reload();
            else 
                System.exit(0);
        }
    }

    private boolean check_game_end() {
        for (int row=0;row<24;row++){
            for (int col=0;col<24;col++){
                if (gameboard[row][col].type!="mine" && boardButtons[row][col].isEnabled())
                    return false;
            }
        }
        return true;
    }

    private void reload() {
        started=false;
        dispose();
        new Minesweeper();
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
                boardButtons[x][y].setIcon(null);
                flagged=false;
            }
            else{
                flagged=true;
                ImageIcon img = new ImageIcon("flag_icon.png");
                Image scaled_img = img.getImage().getScaledInstance(20, 20, DO_NOTHING_ON_CLOSE);
                boardButtons[x][y].setIcon(new ImageIcon(scaled_img));
            }
        }
    }

    private class ButtonClickListener implements MouseListener {
        private int row;
        private int col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void mouseClicked(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {
            JButton clickedButton = (JButton) e.getSource();
            if (clickedButton == boardButtons[row][col] && boardButtons[row][col].isEnabled()) {
                if (SwingUtilities.isLeftMouseButton(e) && !gameboard[row][col].flagged){
                    if (!started) {
                        gameboard_init(24,24,99,row,col);
                        started=true;
                    }
                    //full_reveal();
                    reveal(row,col);
                }
                else if(SwingUtilities.isRightMouseButton(e) && boardButtons[row][col].isEnabled()) 
                    gameboard[row][col].flag();
            }
        }
        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Minesweeper();
            }
        });
    }
}
