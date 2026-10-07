
package movingchar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
public class PizzaGame extends javax.swing.JFrame {

    private JLabel burgerLabel;
    private ImageIcon openMouth, closedMouth, paper;
    private int x = 100; // initial x position
    private JLabel fallingLetter;
    private Timer fallTimer;
    private int letterY = 0;
    private JLabel paperLabel;
    private ImageIcon paperIcon;
    private Random rand = new Random();


    private void resetLetter() {
    // Remove old letter (if still exists)
    if (fallingLetter != null) {
        remove(fallingLetter);
        repaint();
    }

    // Reset Y position
    letterY = 0;

    // Create new random letter
    char randomChar = (char) ('A' + new Random().nextInt(26));
    fallingLetter = new JLabel(String.valueOf(randomChar));
    fallingLetter.setFont(new Font("Arial", Font.BOLD, 30));
    fallingLetter.setForeground(Color.WHITE);

    // Set new random X position
    int randomX = new Random().nextInt(getWidth() - 30);
    fallingLetter.setBounds(randomX, letterY, 30, 30);

    add(fallingLetter);
    repaint();

    // Start falling again
    fallTimer.start();
}

    
    public PizzaGame() {
        initComponents();
        // Load images
        openMouth = new ImageIcon(getClass().getResource("burger(open-mouth).png"));
        closedMouth = new ImageIcon(getClass().getResource("burger(stop).png"));
        paper = new ImageIcon(getClass().getResource("paper.jpg"));
        // Set up label
        burgerLabel = new JLabel(closedMouth);
        burgerLabel.setBounds(x, 200, openMouth.getIconWidth(), openMouth.getIconHeight());
        // Set up falling letter
fallingLetter = new JLabel("A");
fallingLetter.setFont(new Font("Arial", Font.BOLD, 30));
fallingLetter.setForeground(Color.WHITE);
int randomX = new Random().nextInt(500); // random X position
fallingLetter.setBounds(randomX, letterY, 30, 30);
add(fallingLetter);
// Paper label (gagawa tayo ng random drop)

paperLabel = new JLabel(paper);

paperLabel.setBounds(new Random().nextInt(500), 0, 50, 50); // Adjust size if needed
add(paperLabel);



        // Frame settings
        setTitle("Burger Character Movement");
        setSize(600, 400);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);

        add(burgerLabel);
        setVisible(true);

        // Focusable to capture key events
        setFocusable(true);
        requestFocusInWindow();
        fallTimer = new Timer(50, e -> {
    letterY += 5;
    fallingLetter.setLocation(fallingLetter.getX(), letterY);

    // Collision detection
    if (burgerLabel.getBounds().intersects(fallingLetter.getBounds())) {
        System.out.println("Nakain ang letter!");
        fallTimer.stop();
        remove(fallingLetter);
        repaint();

        // Delay bago mag-reset
        Timer resetTimer = new Timer(1000, evt -> resetLetter());
        resetTimer.setRepeats(false);
        resetTimer.start();
    }

    // Reset if below screen
    if (letterY > getHeight()) {
        fallTimer.stop();
        resetLetter();
    }
});

fallTimer.start();

        // Add KeyListener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                burgerLabel.setIcon(openMouth); // mouth open when moving

                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT) {
                    x -= 10;
                } else if (key == KeyEvent.VK_RIGHT) {
                    x += 10;
                }
                burgerLabel.setLocation(x, burgerLabel.getY());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                burgerLabel.setIcon(closedMouth); // mouth closed when not moving
            }
            
        });
        // Start falling
Timer paperTimer = new Timer(80, new ActionListener() {
    int paperY = 0;

    @Override
    public void actionPerformed(ActionEvent e) {
        paperY += 5;
        paperLabel.setLocation(paperLabel.getX(), paperY);

        // Check if the paper is caught
        if (burgerLabel.getBounds().intersects(paperLabel.getBounds())) {
            ((Timer) e.getSource()).stop();

            int result = new Random().nextInt(2); // Random outcome
            if (result == 0) {
                JOptionPane.showMessageDialog(null, "GAME OVER! You caught a trap paper!");
            } else {
                JTextField answerField = new JTextField();
Object[] message = {
    "QUESTION: Crushh mo ba ko?",
    answerField
};

int option = JOptionPane.showConfirmDialog(
    null, 
    message, 
    "Answer the Question", 
    JOptionPane.OK_CANCEL_OPTION
);

if (option == JOptionPane.OK_OPTION) {
    String answer = answerField.getText().trim().toLowerCase();
    if (answer.equals("oo")) {
        JOptionPane.showMessageDialog(null, "keleg ako 🎉");
    } else {
        JOptionPane.showMessageDialog(null, "Wrong! The correct answer is dapat crush moko hmp. 💀");
    }
}

            }
        }

        // Stop if the paper falls beyond the screen
        if (paperY > getHeight()) {
            ((Timer) e.getSource()).stop();
        }
    }
});
paperTimer.start();
        
    }

        
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1685, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1078, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
   public static void main(String args[]) {
    SwingUtilities.invokeLater(() -> {
        new PizzaGame().setVisible(true);
    });
}

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

