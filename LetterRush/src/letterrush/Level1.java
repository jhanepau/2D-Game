/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package letterrush;

import java.awt.Color;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 *
 * @author User
 */
public class Level1 extends javax.swing.JFrame {

    private boolean isPaused = false; 
        private boolean isPausedgame = false; 
    private int level;

    boolean caughtRain = false; // becomes true when you catch rain
    float baseSlideAcceleration = 0.5f; // your normal acceleration value
    float baseSlideFriction = 0.2f; // your normal friction value
    private float slideAcceleration = 0.5f; // Normal acceleration
    private float slideFriction = 0.2f;    // Normal friction
    private float maxSlideSpeed = 5f;    // Maximum sliding speed
    boolean isMoving = false;
    private Timer letterFallTimer;
    private Timer rainTimer;
    private Random rand = new Random();
    private boolean alreadyCaught = false;
    private StringBuilder currentAnswer = new StringBuilder();  
    private int life = 3;
    private final String TARGET_WORD = "APPLE";
    private int paperDropSpeed = 50; // bilis ng bagsak
    private Random randomPaper = new Random();
    int paperY = 0;
    private int currentIndex = 0;
    private java.util.Map<JLabel, Boolean> letterCaughtMap = new java.util.HashMap<>();
    private float slideSpeed = 0.0f;
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private Timer slipperyEffectTimer;
    private boolean isSlippery = false;
    private long slipperyEndTime = 0; // Time when slippery effect ends
    private float slipperyTransitionSpeed = 0.05f; // Transition speed for slippery effect
    private String username;
    private Timer countdownTimer;
    private int remainingTimeInSeconds;  // e.g. 300 for 5 minutes
    private int startingTimeInSeconds;
    private int purchasedHints = 0;  // Number of hints purchased from DB
    private int hintsUsed = 0;       // Number of hints used
    private final int MAX_HINTS = 3; // Max number of hints shown
    private String[] hints = {
        "Hint 1: The answer is 42.",
        "Hint 2: Look for the key.",
        "Hint 3: Try using the blue door."
    };
    private Timer fallingItemTimer;
    private Timer gameClockTimer;
    private Timer letterFallTimerr;
    private boolean isGameFrozen = false;
    private int gameTimeSeconds = 0;
    private int freezeCount = 0;
private boolean isFrozen = false; // New variable to track if the game is frozen
List<Timer> letterTimers = new ArrayList<>();
    private String currentCategory;
private int currentLevel;

public Level1(String username, int currentLevel) {
        initComponents();
        AudioManager.getInstance().playAudio(new File("C:\\Users\\junoc\\Desktop\\PROJ\\AAA\\2D Game-20250427T111041Z-001\\2D Game\\Hi_-there.wav")); 

        this.username = username;
        this.currentLevel = currentLevel;
        setLocationRelativeTo(null);  
        this.setFocusable(true);
        this.requestFocusInWindow();
        startRainFall();
        startCountdown();
        enableDimLightsEffect();
        loadInitialCoins(username);
        this.freezeCount = freezeCount;
        purchasedHints = getItemQuantityFromDatabase(username, "Hint");
        hintButton.setEnabled(true);
        hintLabel.setText(String.valueOf(purchasedHints - hintsUsed));

        getContentPane().setLayout(null);
        getContentPane().add(bg);

        SwingUtilities.invokeLater(() -> {
            Insets insets = getInsets();  
            int width = getWidth() - insets.left - insets.right;
            int height = getHeight() - insets.top - insets.bottom;
            bg.setBounds(0, 0, width, height);
            bg.revalidate();
            bg.repaint();

            });

    this.addComponentListener(new java.awt.event.ComponentAdapter() {
    public void componentResized(java.awt.event.ComponentEvent evt) {
        Insets insets = getInsets();
        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom;
        bg.setBounds(0, 0, width, height);
    }
        });
    }

private void stopAllTimers() {
    for (Timer timer : letterTimers) {
        if (timer != null) {
            timer.stop();
        }
    }
    letterTimers.clear(); // optional: free memory
}


// Starts the countdown timer (affected by freeze)
public void startCountdown() {
    remainingTimeInSeconds = 300; // or change based on difficulty
    updateTimerLabel();

    countdownTimer = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            remainingTimeInSeconds--;
            updateTimerLabel();

            if (remainingTimeInSeconds <= 0) {
                countdownTimer.stop();
                endGameDueToTimeout();
            }
        }
    });

    countdownTimer.start();
}

    private void noFreeze() {
        pauseGame();
        nofreeze s = new nofreeze(this);
        s.setVisible(true);
        s.setLocationRelativeTo(null);

    }

private void freezeGameFor10Seconds() {
    int dbFreezeCount = getItemQuantityFromDatabase(username, "freeze");

    if (dbFreezeCount <= 0) {
        noFreeze();
        return;
    }

    // Update database and freeze counter
    updateItemQuantityInDatabase(username, "freeze", dbFreezeCount - 1);
    freezeCount = dbFreezeCount - 1;
    updatelblFreezeCount();

    freezeButton.setEnabled(false);

    // Stop the countdown timer
    if (countdownTimer != null && countdownTimer.isRunning()) {
        countdownTimer.stop();
        System.out.println("⏸️ Countdown paused for 10 seconds.");
    }

    // Set the freeze state
    isFrozen = true;

    // Ensure focus is still on the game panel and input continues
    this.requestFocusInWindow(); // or your game panel component to keep the key listener active

    // Use a separate Timer to manage the 10-second delay
    new javax.swing.Timer(10000, e -> {
        // Resume countdown after 10 seconds
        if (countdownTimer != null) {
            countdownTimer.start();
            System.out.println("▶️ Countdown resumed.");
        }

        isFrozen = false; // Reset the freeze state
        freezeButton.setEnabled(true);
        updatelblFreezeCount();

        // Ensure focus stays on the game panel so player can keep moving
        this.requestFocusInWindow();

        ((javax.swing.Timer) e.getSource()).stop(); // Stop the 10s delay timer
    }).start();
}

private void updatelblFreezeCount() {
    freezeLabel.setText(String.valueOf(freezeCount));
    freezeLabel.setEnabled(freezeCount > 0); // Disable if no freezes left
}
private int getItemQuantityFromDatabase(String username, String itemName) {
    int quantity = 0;
    try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/user", "root", "root")) {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT quantity FROM PURCHASES WHERE username = ? AND item_name = ?");
        stmt.setString(1, username);
        stmt.setString(2, itemName);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            quantity = rs.getInt("quantity");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return quantity;
}

private void updateItemQuantityInDatabase(String username, String itemName, int newQuantity) {
    try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/user", "root", "root")) {
        PreparedStatement stmt = conn.prepareStatement(
            "UPDATE PURCHASES SET quantity = ? WHERE username = ? AND item_name = ?");
        stmt.setInt(1, newQuantity);
        stmt.setString(2, username);
        stmt.setString(3, itemName);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private boolean hasExtraLife(String username) {
    int lifeCount = getItemQuantityFromDatabase(username, "life");
    System.out.println("Life count for " + username + ": " + lifeCount);
    return lifeCount > 0;
}


private boolean useExtraLife(String username) {
    int lifeCount = getItemQuantityFromDatabase(username, "life");

    if (lifeCount > 0) {
        updateItemQuantityInDatabase(username, "life", lifeCount - 1);
        System.out.println("Used 1 life for " + username + ". Remaining: " + (lifeCount - 1));
        return true;
    } else {
        System.out.println("No extra life available for " + username);
        return false;
    }
}

private void saveScoreToDatabase(String username, int level, int score, String category) {
    if (username == null || username.trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Username is missing. Cannot save score.");
        return;
    }

    if (category == null || category.trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Category is missing. Cannot save score.");
        return;
    }

    String url = "jdbc:derby://localhost:1527/user";
    String user = "root";
    String password = "root";

    try (Connection conn = DriverManager.getConnection(url, user, password)) {
        conn.setAutoCommit(true);

        String checkSql = "SELECT SCORE FROM USER_SCORES WHERE USERNAME = ? AND LEVEL = ? AND CATEGORY = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, username);
            checkStmt.setInt(2, level);
            checkStmt.setString(3, category);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int existingScore = rs.getInt("SCORE");
                if (score > existingScore) {
                    String updateSql = "UPDATE USER_SCORES SET SCORE = ? WHERE USERNAME = ? AND LEVEL = ? AND CATEGORY = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, score);
                        updateStmt.setString(2, username);
                        updateStmt.setInt(3, level);
                        updateStmt.setString(4, category);
                        updateStmt.executeUpdate();
                        System.out.println("Score updated for " + username + " on level " + level + " in " + category);
                    }
                } else {
                    System.out.println("Existing score is higher. Not updating.");
                }
            } else {
                String insertSql = "INSERT INTO USER_SCORES (USERNAME, LEVEL, SCORE, CATEGORY) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, username);
                    insertStmt.setInt(2, level);
                    insertStmt.setInt(3, score);
                    insertStmt.setString(4, category);
                    insertStmt.executeUpdate();
                    System.out.println("Inserted new score for " + username + " on level " + level + " in " + category);
                }
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error saving score: " + e.getMessage());
        e.printStackTrace();
    }
}


public void endGameSuccessfully() {
    if (countdownTimer != null) {
        stopAllTimers();
        countdownTimer.stop(); // Stop the timer if still running
    }

    int timeTaken = startingTimeInSeconds - remainingTimeInSeconds;
    int score = calculateScore(timeTaken, startingTimeInSeconds);
    
    String currentCategory = "Category1"; // Make sure this is set properly
    saveScoreToDatabase(username, this.level, score, currentCategory);

    SwingUtilities.invokeLater(() -> {
        levelCompleted lc = new levelCompleted(username, this.level, this); // Pass reference
        lc.setScore(score);
        lc.setVisible(true); // Show on top
        lc.setLocationRelativeTo(null);
    });
}


private int calculateScore(int timeTaken, int maxTime) {
    int remainingTime = maxTime - timeTaken;

    if (remainingTime >= 240) { // 4:00 or more
        return 100;
    } else if (remainingTime >= 210) { // 3:30 or more
        return 90;
    } else if (remainingTime >= 180) { // 3:00 or more
        return 80;
    } else if (remainingTime >= 150) { // 2:30 or more
        return 60;
    } else if (remainingTime >= 120) { // 2:00 or more
        return 50;
    } else if (remainingTime >= 90) { // 1:30 or more
        return 40;
    } else if (remainingTime >= 60) { // 1:00 or more
        return 30;
    } else {
        return 10; // Less than 1:00
    }
}

public void endGameSuccess() {
    if (countdownTimer != null) {
        countdownTimer.stop(); // Stop the timer if still running
        stopAllTimers();
    }

    int timeTaken = startingTimeInSeconds - remainingTimeInSeconds;
    int score = calculateScore(timeTaken, startingTimeInSeconds);

    String currentCategory = "Category1"; // Make sure this is set properly
    saveScoreToDatabase(username, this.level, score, currentCategory);

     SwingUtilities.invokeLater(() -> {
        levelCompleted lc = new levelCompleted(username, this.level, this); // Pass reference
        lc.setScore(score);
        lc.setVisible(true); 
        lc.setLocationRelativeTo(null);
    });
}

private void updateTimerLabel() {
    int minutes = remainingTimeInSeconds / 60;
    int seconds = remainingTimeInSeconds % 60;
    timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
}

private void endGameDueToTimeout() {
    JOptionPane.showMessageDialog(null, "Time's up! Game over.");
    // Optionally call a method to reset or return to menu
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dimPanel = new javax.swing.JLayeredPane();
        gifLabel = new javax.swing.JLabel();
        characterLabel = new javax.swing.JLabel();
        letterE = new javax.swing.JLabel();
        letterA = new javax.swing.JLabel();
        letterL = new javax.swing.JLabel();
        letterP = new javax.swing.JLabel();
        letterB = new javax.swing.JLabel();
        kmoss = new javax.swing.JLabel();
        heart1 = new javax.swing.JLabel();
        letterH = new javax.swing.JLabel();
        answerField = new javax.swing.JLabel();
        heart2 = new javax.swing.JLabel();
        heart3 = new javax.swing.JLabel();
        amoss = new javax.swing.JLabel();
        storeButton = new javax.swing.JButton();
        bombLabel = new javax.swing.JLabel();
        paperLabel = new javax.swing.JLabel();
        rain = new javax.swing.JLabel();
        hintButton = new javax.swing.JButton();
        freezeButton = new javax.swing.JButton();
        hintLabel = new javax.swing.JLabel();
        freezeLabel = new javax.swing.JLabel();
        timerLabel = new javax.swing.JLabel();
        coinLabel = new javax.swing.JLabel();
        coin = new javax.swing.JLabel();
        candyLabel = new javax.swing.JLabel();
        quitButton = new javax.swing.JButton();
        bg = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(dimPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1480, 610));

        gifLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(gifLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 100, 920, 440));

        characterLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        characterLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/letterrush/burger(closed) (2).png"))); // NOI18N
        getContentPane().add(characterLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 360, 210, 240));

        letterE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/letterrush/e.png"))); // NOI18N
        getContentPane().add(letterE, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        letterA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/letterrush/a (1).png"))); // NOI18N
        getContentPane().add(letterA, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 80, -1, -1));

        letterL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/letterrush/l.png"))); // NOI18N
        getContentPane().add(letterL, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 30, -1, -1));

        letterP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/letterrush/p (1).png"))); // NOI18N
        getContentPane().add(letterP, new org.netbeans.lib.awtextra.AbsoluteConstraints(1380, 10, -1, -1));

        letterB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/letterrush/bb.png"))); // NOI18N
        getContentPane().add(letterB, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 50, -1, -1));

        kmoss.setIcon(new javax.swing.ImageIcon(getClass().getResource("/letterrush/kmoss.png"))); // NOI18N
        getContentPane().add(kmoss, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 10, -1, -1));

        heart1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/letterrush/heart_full.png"))); // NOI18N
        getContentPane().add(heart1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 40, 130, 110));

        letterH.setIcon(new javax.swing.ImageIcon(getClass().getResource("/letterrush/h.png"))); // NOI18N
        getContentPane().add(letterH, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 50, 100, 100));

        answerField.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        getContentPane().add(answerField, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 640, 340, 70));

        heart2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/letterrush/heart_full.png"))); // NOI18N
        getContentPane().add(heart2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1160, 40, 150, 110));

        heart3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/letterrush/heart_full.png"))); // NOI18N
        getContentPane().add(heart3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1290, 40, 130, 110));

        amoss.setIcon(new javax.swing.ImageIcon(getClass().getResource("/letterrush/amoss.png"))); // NOI18N
        getContentPane().add(amoss, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 0, -1, -1));

        storeButton.setContentAreaFilled(false);
        storeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                storeButtonActionPerformed(evt);
            }
        });
        getContentPane().add(storeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 670, 140, 40));

        bombLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bombLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/letterrush/bomb (5).png"))); // NOI18N
        getContentPane().add(bombLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 30, 100, 100));

        paperLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        paperLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/letterrush/NYAPAPER.png"))); // NOI18N
        getContentPane().add(paperLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 140, 130, 90));

        rain.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rain.setIcon(new javax.swing.ImageIcon(getClass().getResource("/letterrush/rain (1) (1).png"))); // NOI18N
        getContentPane().add(rain, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 20, 50, 100));

        hintButton.setContentAreaFilled(false);
        hintButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hintButtonActionPerformed(evt);
            }
        });
        getContentPane().add(hintButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 650, 170, 50));

        freezeButton.setContentAreaFilled(false);
        freezeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                freezeButtonActionPerformed(evt);
            }
        });
        getContentPane().add(freezeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 640, 160, 60));
        getContentPane().add(hintLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 630, 30, 20));
        getContentPane().add(freezeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 630, 30, 20));

        timerLabel.setFont(new java.awt.Font("Verdana", 1, 48)); // NOI18N
        timerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(timerLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, 170, 70));

        coinLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(coinLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 90, 30, 30));

        coin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(coin, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 110, 100, 100));

        candyLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        candyLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/letterrush/candy (1).png"))); // NOI18N
        getContentPane().add(candyLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 120, 100, 80));

        quitButton.setContentAreaFilled(false);
        quitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitButtonActionPerformed(evt);
            }
        });
        getContentPane().add(quitButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 670, 150, 40));

        bg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/letterrush/general (2).png"))); // NOI18N
        bg.setAlignmentY(0.0F);
        getContentPane().add(bg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
    
    if (isPaused) {
        return; // Only block movement if the game is paused
    }

    System.out.println("Key Pressed: " + evt.getKeyCode()); // Debugging

    int key = evt.getKeyCode();
    int x = characterLabel.getX();
    int y = characterLabel.getY();

    // Handle slippery movement
    if (isSlippery) {
        if (key == KeyEvent.VK_RIGHT) {
            characterLabel.setLocation(x + 25, y); // Exaggerated movement right
        } else if (key == KeyEvent.VK_LEFT) {
            characterLabel.setLocation(x - 25, y); // Exaggerated movement left
        }
    } else {
        if (key == KeyEvent.VK_RIGHT) {
            characterLabel.setLocation(x + 10, y); // Normal movement right
        } else if (key == KeyEvent.VK_LEFT) {
            characterLabel.setLocation(x - 10, y); // Normal movement left
        }
    }

    if (key == KeyEvent.VK_UP) {
        characterLabel.setIcon(new ImageIcon("C:\\Users\\junoc\\Desktop\\PROJ\\AAA\\2D Game-20250427T111041Z-001\\2D Game\\2DGAME_IMAGE\\burger(closed) (2).png"));
    }
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
      if (isPaused) {
        return; // Only block on pause
    }

    int key = evt.getKeyCode();

    if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_LEFT) {
        characterLabel.setIcon(new ImageIcon("C:\\Users\\junoc\\Desktop\\PROJ\\AAA\\2D Game-20250427T111041Z-001\\2D Game\\2DGAME_IMAGE\\burger(open) (1).png"));
    }
    }//GEN-LAST:event_formKeyReleased

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        startLetterFall();
        startPaperFall();
        
    }//GEN-LAST:event_formWindowOpened

    private void storeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_storeButtonActionPerformed
        pauseGame();
        store s = new store(username, currentLevel);
        s.setVisible(true);
        s.setLocationRelativeTo(null);
        this.dispose();
    }//GEN-LAST:event_storeButtonActionPerformed

    private void noHints() {
        pauseGame();
        notenoughhints s = new notenoughhints(this);
        s.setVisible(true);
        s.setLocationRelativeTo(null);

    }
    
    private void maxhint() {
        pauseGame();
        maxhint s = new maxhint(this);
        s.setVisible(true);
        s.setLocationRelativeTo(null);

    }
    
    private void hintButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hintButtonActionPerformed
    System.out.println("Hint button clicked!");
    if (purchasedHints == 0) {
        System.out.println("No hints available.");
        noHints();
        return;
    }

    if (hintsUsed >= MAX_HINTS) {
        maxhint();
        return;
    }

    if (hintsUsed >= purchasedHints) {
        pauseGame();
        noHints();
        return;
    }

    String hintText = hints[hintsUsed];
    new hintFrame(this, hintText).setVisible(true);

    hintsUsed++;

    // Update remaining hint display
    hintLabel.setText(String.valueOf(purchasedHints - hintsUsed));

    // Optional: Update quantity in DB
    updateItemQuantityInDatabase(username, "Hint", purchasedHints - hintsUsed);

    }//GEN-LAST:event_hintButtonActionPerformed

    private void freezeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_freezeButtonActionPerformed
        freezeGameFor10Seconds();
    }//GEN-LAST:event_freezeButtonActionPerformed

    private void quitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitButtonActionPerformed
        pauseGame();
        quitConfirm qc = new quitConfirm(this); // 'this' refers to Level1 instance
        qc.setLocationRelativeTo(this);  // Center the dialog on the current window
        qc.setVisible(true); 
    }//GEN-LAST:event_quitButtonActionPerformed
    public String getUsername() {
        return username;
    }
    // Function to simulate the rain effect
    private void applyRainEffect() {
       if (caughtRain) {
        // Adjust sliding properties when rain is caught
        slideAcceleration *= 1.2f; // Slightly increase acceleration
        slideFriction *= 0.8f;     // Slightly reduce friction
    } else {
        slideAcceleration = baseSlideAcceleration; // Reset to base
        slideFriction = baseSlideFriction;         // Reset to base
    }
}


    // Function to update movement
    public void updateMovement() {
        applyRainEffect(); // Apply rain effect first
    
    // Adjust for slippery state
    if (isSlippery) {
        // Increase the effect of movement when slippery
        slideAcceleration = baseSlideAcceleration * 2.0f; // More acceleration
        slideFriction = baseSlideFriction * 0.1f; // Very low friction
    } else {
        // Apply normal movement when not slippery
        slideAcceleration = baseSlideAcceleration;
        slideFriction = baseSlideFriction;
    }
    
    // Handle movement logic based on slippery state
    if (isMovingLeft) {
        slideSpeed = Math.max(slideSpeed - slideAcceleration, -maxSlideSpeed);
    } else if (isMovingRight) {
        slideSpeed = Math.min(slideSpeed + slideAcceleration, maxSlideSpeed);
    } else {
        // Normal friction application
        if (slideSpeed > 0) {
            slideSpeed = Math.max(slideSpeed - slideFriction, 0);
        } else if (slideSpeed < 0) {
            slideSpeed = Math.min(slideSpeed + slideFriction, 0);
        }
    }
}


    // Function to simulate the "rain caught" event
   public void catchRain() {
    caughtRain = true; // Activate the rain effect
    isSlippery = true; // Enable slippery effect
    
    // Set a timer for the slippery effect to end after 3 seconds
    slipperyEffectTimer = new Timer(3000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            isSlippery = false; // Disable slippery effect after 3 seconds
            slipperyEffectTimer.stop();
        }
    });
    slipperyEffectTimer.start();
}
    
    private void startRainFall() {
        rainTimer = new Timer(30, e -> {
        if (!isPaused) {
            // Move rain downward
            int rainY = rain.getY();
            rain.setLocation(rain.getX(), rainY + 5);

            // Check if rain falls out of the screen
            if (rain.getY() > getHeight()) {
                resetRainPosition(); // Reset rain position if it moves out of screen
            }

            // Check for collision with character
            if (rain.getBounds().intersects(characterLabel.getBounds())) {
                catchRain(); // Trigger rain catch
                resetRainPosition(); // Optional: reset rain position
            }
        }
    });
        letterTimers.add(rainTimer);

    rainTimer.start();
}

// Function to reset rain position
private void resetRainPosition() {
    rain.setLocation(rand.nextInt(getWidth() - rain.getWidth()), -rain.getHeight());
}
private void updateHearts() {
    String heartEmpty = "C:\\Users\\junoc\\Desktop\\PROJ\\AAA\\2D Game-20250427T111041Z-001\\2D Game\\2DGAME_IMAGE\\heart_empty.png";
    
    switch (life) {
        case 2:
            heart3.setIcon(new ImageIcon(heartEmpty)); 
            break;
        case 1:
            heart2.setIcon(new ImageIcon(heartEmpty));
            break;
        case 0:
            heart1.setIcon(new ImageIcon(heartEmpty));
            break;
    }
}

public void updateRevive() {
    String heartEmpty = "C:\\Users\\junoc\\Desktop\\PROJ\\AAA\\2D Game-20250427T111041Z-001\\2D Game\\2DGAME_IMAGE\\heart_full.png";
    
    switch (life) {
        case 2:
            heart3.setIcon(new ImageIcon(heartEmpty)); 
            break;
        case 1:
            heart2.setIcon(new ImageIcon(heartEmpty));
            break;
        case 0:
            heart1.setIcon(new ImageIcon(heartEmpty));
            break;
    }
}

public void showGameOverScreen(String username, int currentLevel, javax.swing.JFrame currentLevelFrame) {
    stopAllTimers(); // stop all falling letters
    System.out.println("Level1 disposed, game fully stopped.");
    GameOverScreen gameOverScreen = new GameOverScreen(username, currentLevel, currentLevelFrame);
    gameOverScreen.setLocationRelativeTo(null); // Or 'this' if 'this' is not disposed yet
    gameOverScreen.setVisible(true);
}

private int[] usedXPositions = new int[20]; // or a List if you want dynamic sizing
private int usedCount = 0;

public void startLetterFall() {
    List<JLabel> allLetters = Arrays.asList(letterA, letterP, letterL, letterE, letterB, letterH, amoss, kmoss);
List<Rectangle> usedBounds = new ArrayList<>();

    String currentDifficulty = DifficultyManager.getDifficulty();
    int fallSpeed;
    switch (currentDifficulty) {
        case "Easy": fallSpeed = 100; break;
        case "Medium": fallSpeed = 30; break;
        case "Hard": fallSpeed = 15; break;
        default: fallSpeed = 100; break;
    }

Consumer<JLabel> setNonOverlappingStart = label -> {
    int x, y = -label.getHeight();
    Rectangle rect;
    boolean overlaps;

    do {
        x = rand.nextInt(getWidth() - label.getWidth());
        rect = new Rectangle(x, y, label.getWidth(), label.getHeight());
        overlaps = false;

        for (Rectangle used : usedBounds) {
            if (used.intersects(rect)) {
                overlaps = true;
                break;
            }
        }
    } while (overlaps);
        usedBounds.add(rect);
        label.setLocation(x, y);
    };

// === LETTER A ===
setNonOverlappingStart.accept(letterA);
Timer letterATimer = new Timer(fallSpeed + rand.nextInt(10), e -> {
    letterA.setLocation(letterA.getX(), letterA.getY() + 5);

    if (letterA.getY() > getHeight()) {
        resetLetterPosition(letterA);
    }

    if (letterA.getBounds().intersects(characterLabel.getBounds())) {
        ((Timer) e.getSource()).stop();

        if (currentIndex < TARGET_WORD.length() && TARGET_WORD.charAt(currentIndex) == 'A') {
            currentAnswer.append('A');
            currentIndex++;
            answerField.setText(currentAnswer.toString());
            checkWordCompleted();
        } else {
            // Wrong letter caught
            currentAnswer.setLength(0);
            currentIndex = 0;
            answerField.setText("");

            if (life > 0) {
                life--;
                updateHearts();
            }

            if (life <= 0) {
                pauseGame(); // Pause all timers

                if (hasExtraLife(username)) {
                    useLife lifeScreen = new useLife(username, currentLevel, this);
                    lifeScreen.setLocationRelativeTo(null);
                    lifeScreen.setVisible(true);
                } else {
                pauseGame();
                noLives qp = new noLives(username, currentLevel, this);
                qp.setVisible(true); // Show on top
                qp.setLocationRelativeTo(null);
                return;
                }
            }
        }

        resetLetterPosition(letterA);
        ((Timer) e.getSource()).start(); // Resume timer after processing
    }
});
letterTimers.add(letterATimer);
letterATimer.start();

// === LETTER P ===
setNonOverlappingStart.accept(letterP);
Timer letterPTimer = new Timer(fallSpeed + rand.nextInt(10), e -> {
    letterP.setLocation(letterP.getX(), letterP.getY() + 5);

    if (letterP.getY() > getHeight()) {
        resetLetterPosition(letterP);
    }

    if (letterP.getBounds().intersects(characterLabel.getBounds())) {
        ((Timer) e.getSource()).stop();

        if (currentIndex < TARGET_WORD.length() && TARGET_WORD.charAt(currentIndex) == 'P') {
            currentAnswer.append('P');
            currentIndex++;
            answerField.setText(currentAnswer.toString());
            checkWordCompleted();
        } else {
            // Wrong letter caught
            currentAnswer.setLength(0);
            currentIndex = 0;
            answerField.setText("");

            if (life > 0) {
                life--;
                updateHearts();
            }

            if (life <= 0) {
                pauseGame(); // Pause all timers

                if (hasExtraLife(username)) {
                    useLife lifeScreen = new useLife(username, currentLevel, this);
                    lifeScreen.setLocationRelativeTo(null);
                    lifeScreen.setVisible(true);
                } else {
                pauseGame();
                noLives qp = new noLives(username, currentLevel, this);
                qp.setVisible(true); // Show on top
                qp.setLocationRelativeTo(null);
                return;
                }
            }
        }

        resetLetterPosition(letterP);
        ((Timer) e.getSource()).start(); // Resume timer after processing
    }
});
letterTimers.add(letterPTimer);
letterPTimer.start();

// === LETTER L ===
setNonOverlappingStart.accept(letterL);
Timer letterLTimer = new Timer(fallSpeed + rand.nextInt(10), e -> {
    letterL.setLocation(letterL.getX(), letterL.getY() + 5);

    if (letterL.getY() > getHeight()) {
        resetLetterPosition(letterL);
    }

    if (letterL.getBounds().intersects(characterLabel.getBounds())) {
        ((Timer) e.getSource()).stop();

        if (currentIndex < TARGET_WORD.length() && TARGET_WORD.charAt(currentIndex) == 'L') {
            currentAnswer.append('L');
            currentIndex++;
            answerField.setText(currentAnswer.toString());
            checkWordCompleted();
        } else {
            // Wrong letter caught
            currentAnswer.setLength(0);
            currentIndex = 0;
            answerField.setText("");

            if (life > 0) {
                life--;
                updateHearts();
            }

            if (life <= 0) {
                pauseGame(); // Pause all timers

                if (hasExtraLife(username)) {
                    useLife lifeScreen = new useLife(username, currentLevel, this);
                    lifeScreen.setLocationRelativeTo(null);
                    lifeScreen.setVisible(true);
                } else {
                pauseGame();
                noLives qp = new noLives(username, currentLevel, this);
                qp.setVisible(true); // Show on top
                qp.setLocationRelativeTo(null);
                return;
                }
            }
        }

        resetLetterPosition(letterL);
        ((Timer) e.getSource()).start(); // Resume timer after processing
    }
});

letterTimers.add(letterLTimer);
letterLTimer.start();

// === LETTER E ===
setNonOverlappingStart.accept(letterE);

Timer letterETimer = new Timer(fallSpeed + rand.nextInt(10), e -> {
    letterE.setLocation(letterE.getX(), letterE.getY() + 5);

    if (letterE.getY() > getHeight()) {
        resetLetterPosition(letterE);
    }

    if (letterE.getBounds().intersects(characterLabel.getBounds())) {
        ((Timer) e.getSource()).stop();

        if (currentIndex < TARGET_WORD.length() && TARGET_WORD.charAt(currentIndex) == 'E') {
            currentAnswer.append('E');
            currentIndex++;
            answerField.setText(currentAnswer.toString());
            checkWordCompleted();
        } else {
            // Wrong letter caught
            currentAnswer.setLength(0);
            currentIndex = 0;
            answerField.setText("");

            if (life > 0) {
                life--;
                updateHearts();
            }

            if (life <= 0) {
                pauseGame(); // Pause all timers

                if (hasExtraLife(username)) {
                    useLife lifeScreen = new useLife(username, currentLevel, this);
                    lifeScreen.setLocationRelativeTo(null);
                    lifeScreen.setVisible(true);
                } else {
                pauseGame();
                noLives qp = new noLives(username, currentLevel, this);
                qp.setVisible(true); // Show on top
                qp.setLocationRelativeTo(null);
                return;
                }
            }
        }

        resetLetterPosition(letterE);
        ((Timer) e.getSource()).start(); // Resume timer after processing
    }
});

letterTimers.add(letterETimer);
letterETimer.start();


// === LETTER B (forbidden) ===
setNonOverlappingStart.accept(letterB);

Timer letterBTimer = new Timer(fallSpeed + rand.nextInt(150), e -> {
    letterB.setLocation(letterB.getX(), letterB.getY() + 5);

    if (letterB.getY() > getHeight()) {
        resetLetterPosition(letterB);
    }

    if (letterB.getBounds().intersects(characterLabel.getBounds())) {
        ((Timer) e.getSource()).stop();  // Stop falling

        currentAnswer.setLength(0);
        currentIndex = 0;
        answerField.setText("");

        if (life > 0) {
            life--;
            updateHearts();
        }

        if (life <= 0) {
            pauseGame(); // Pause all timers and game state

            if (hasExtraLife(username)) {
                useLife qp = new useLife(username, currentLevel, this);
                qp.setLocationRelativeTo(null);
                qp.setVisible(true);
            } else {
                pauseGame();
                noLives qp = new noLives(username, currentLevel, this);
                qp.setVisible(true); // Show on top
                qp.setLocationRelativeTo(null);
                return;
            }
        }

        resetLetterPosition(letterB);
        ((Timer) e.getSource()).start(); // Resume falling
    }
});

letterTimers.add(letterBTimer);
letterBTimer.start();

setNonOverlappingStart.accept(letterH);

Timer letterHTimer = new Timer(fallSpeed + rand.nextInt(140), e -> {
    letterH.setLocation(letterH.getX(), letterH.getY() + 5);

    if (letterH.getY() > getHeight()) {
        resetLetterPosition(letterH);
    }

    if (letterH.getBounds().intersects(characterLabel.getBounds())) {
        ((Timer) e.getSource()).stop();  // Stop falling

        currentAnswer.setLength(0);
        currentIndex = 0;
        answerField.setText("");

        if (life > 0) {
            life--;
            updateHearts();
        }

        if (life <= 0) {
            pauseGame(); // Pause all timers and game state

            if (hasExtraLife(username)) {
                useLife qp = new useLife(username, currentLevel, this);
                qp.setLocationRelativeTo(null);
                qp.setVisible(true);
            } else {
                pauseGame();
                noLives qp = new noLives(username, currentLevel, this);
                qp.setVisible(true); // Show on top
                qp.setLocationRelativeTo(null);
                return;
            }
        }

        resetLetterPosition(letterH);
        ((Timer) e.getSource()).start(); // Resume falling
    }
});

letterTimers.add(letterHTimer);
letterHTimer.start();

setNonOverlappingStart.accept(amoss);

Timer amossTimer = new Timer(fallSpeed + rand.nextInt(130), e -> {
    amoss.setLocation(amoss.getX(), amoss.getY() + 5);

    if (amoss.getY() > getHeight()) {
        resetLetterPosition(amoss);
    }

    if (amoss.getBounds().intersects(characterLabel.getBounds())) {
        ((Timer) e.getSource()).stop();  // Stop falling

        currentAnswer.setLength(0);
        currentIndex = 0;
        answerField.setText("");

        if (life > 0) {
            life--;
            updateHearts();
        }

        if (life <= 0) {
            pauseGame(); // Pause all timers and game state

            if (hasExtraLife(username)) {
                useLife qp = new useLife(username, currentLevel, this);
                qp.setLocationRelativeTo(null);
                qp.setVisible(true);
            } else {
                pauseGame();
                noLives qp = new noLives(username, currentLevel, this);
                qp.setVisible(true); // Show on top
                qp.setLocationRelativeTo(null);
                return;
            }
        }

        resetLetterPosition(amoss);
        ((Timer) e.getSource()).start(); // Resume falling
    }
});

letterTimers.add(amossTimer);
amossTimer.start();



setNonOverlappingStart.accept(kmoss);

Timer kmossTimer = new Timer(fallSpeed + rand.nextInt(200), e -> {
    kmoss.setLocation(kmoss.getX(), kmoss.getY() + 5);

    if (kmoss.getY() > getHeight()) {
        resetLetterPosition(kmoss);
    }

    if (kmoss.getBounds().intersects(characterLabel.getBounds())) {
        ((Timer) e.getSource()).stop();  // Stop falling

        currentAnswer.setLength(0);
        currentIndex = 0;
        answerField.setText("");

        if (life > 0) {
            life--;
            updateHearts();
        }

        if (life <= 0) {
            pauseGame(); // Pause all timers and game state

            if (hasExtraLife(username)) {
                useLife qp = new useLife(username, currentLevel, this);
                qp.setLocationRelativeTo(null);
                qp.setVisible(true);
            } else {
                pauseGame();
                noLives qp = new noLives(username, currentLevel, this);
                qp.setVisible(true); // Show on top
                qp.setLocationRelativeTo(null);
                return;
            }
        }

        resetLetterPosition(kmoss);
        ((Timer) e.getSource()).start(); // Resume falling
    }
});

letterTimers.add(kmossTimer);
kmossTimer.start();




    
    // === BOMB ===
    setNonOverlappingStart.accept(bombLabel);
    Timer bombTimer = new Timer(fallSpeed + rand.nextInt(100), e -> {
        bombLabel.setLocation(bombLabel.getX(), bombLabel.getY() + 5);
        if (bombLabel.getY() > getHeight()) resetLetterPosition(bombLabel);

        if (bombLabel.getBounds().intersects(characterLabel.getBounds())) {
            ((Timer) e.getSource()).stop();
            pauseGame();
                caughtBomb qp = new caughtBomb(username, currentLevel, this);
                qp.setVisible(true); // Show on top
                qp.setLocationRelativeTo(null);

        }
    });
    letterTimers.add(bombTimer);

    bombTimer.setInitialDelay(10000); // Optional: add an initial delay to make the bomb fall after a short delay
    bombTimer.start();
 
setNonOverlappingStart.accept(coin);

Timer coinTimer = new Timer(fallSpeed + rand.nextInt(10), e -> {
    // Move coin down
    coin.setLocation(coin.getX(), coin.getY() + 5);

    // If coin falls off screen, reset it
    if (coin.getY() > getHeight()) {
        resetLetterPosition(coin);
    }

    // Coin caught by character
    if (coin.getBounds().intersects(characterLabel.getBounds())) {
        ((Timer) e.getSource()).stop();

        // Update database and refresh coin label
        updateCoinsInDatabase(username); // This must also call updateCoinLabel()

        // Reset coin position after catching
        resetLetterPosition(coin);

        ((Timer) e.getSource()).start();
    }
});
    letterTimers.add(coinTimer);

    coinTimer.start();

final int maxAppearances = 3;
final int[] appearanceCount = {0};
final Random rand = new Random();

candyLabel.setVisible(false); // hide candy initially

Timer candySpawner = new Timer(30_000, e -> { // 30,000 ms = 30 seconds
    if (appearanceCount[0] >= maxAppearances) {
        ((Timer) e.getSource()).stop(); // stop the spawner after 3 spawns
        return;
    }

    setNonOverlappingStart.accept(candyLabel);
    candyLabel.setVisible(true);

    Timer candyTimer = new Timer(20, ev -> {
        // Move candy down
        candyLabel.setLocation(candyLabel.getX(), candyLabel.getY() + 10);

        if (candyLabel.getY() > getHeight()) {
            appearanceCount[0]++;
            ((Timer) ev.getSource()).stop();
            resetLetterPosition(candyLabel);
            candyLabel.setVisible(false);
            return;
        }

        if (candyLabel.getBounds().intersects(characterLabel.getBounds())) {
            appearanceCount[0]++;
            ((Timer) ev.getSource()).stop();

            pauseGame();
            ImageIcon gif = new ImageIcon("C:\\Users\\junoc\\Desktop\\PROJ\\AAA\\2D Game-20250427T111041Z-001\\2D Game\\2DGAME_IMAGE\\ateapple.gif");
            gifLabel.setIcon(gif);
            gifLabel.setVisible(true);

            currentAnswer.setLength(0);
            currentAnswer.append("APPLE");
            currentIndex = TARGET_WORD.length();
            answerField.setText(currentAnswer.toString());

            Timer delayTimer = new Timer(4000, evt -> checkWordCompleted());
            delayTimer.setRepeats(false);
            delayTimer.start();

            Timer hideGifTimer = new Timer(5500, evt -> gifLabel.setIcon(null));
            hideGifTimer.setRepeats(false);
            hideGifTimer.start();

            resetLetterPosition(candyLabel);
            candyLabel.setVisible(false);
        }
    });

    letterTimers.add(candyTimer);
    candyTimer.start();
});

candySpawner.start(); // Start the 30-second interval spawner

}


private void updateCoinsInDatabase(String username) {
    String updateSQL = "UPDATE USERS SET coins = coins + 1 WHERE username = ?";
    String fetchSQL = "SELECT coins FROM USERS WHERE username = ?";

    try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/user", "root", "root")) {

        // 1. Increment coin count
        try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
            updateStmt.setString(1, username);
            updateStmt.executeUpdate();
        }

        // 2. Retrieve updated coin count
        try (PreparedStatement fetchStmt = conn.prepareStatement(fetchSQL)) {
            fetchStmt.setString(1, username);
            ResultSet rs = fetchStmt.executeQuery();
            if (rs.next()) {
                int newCoinCount = rs.getInt("coins");
                updateCoinLabel(newCoinCount); // Update the UI label
            }
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}

public void loadInitialCoins(String username) {
    String query = "SELECT coins FROM USERS WHERE username = ?";

    try (Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/user", "root", "root");
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            int coinCount = rs.getInt("coins");
            updateCoinLabel(coinCount); // update the label on UI
        } else {
            JOptionPane.showMessageDialog(this, "No data found for user: " + username);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error fetching coins: " + e.getMessage());
    }
}

private void updateCoinLabel(int coinCount) {
    coinLabel.setText(String.valueOf(coinCount));
}



    private void resetLetterVoidPosition(JLabel letter) {
        int newX = rand.nextInt(getWidth() - letter.getWidth());
        letter.setLocation(newX, 0);
    }

private void resetCaughtFlags() {
    for (JLabel letter : letterCaughtMap.keySet()) {
        letterCaughtMap.put(letter, false);
    }
}

public void pauseGame() {
    isPausedgame = true; // Set the paused flag

    // Stop all timers
    for (Timer timer : letterTimers) {
        timer.stop();
    }
}

public void resumeGame() {
    isPausedgame = false; // Set the paused flag to false

    // Restart all timers
    for (Timer timer : letterTimers) {
        timer.start();
    }
}

private void enableDimLightsEffect() {
    // Setup the dimPanel if needed (already added in GUI builder)
    dimPanel.setBounds(0, 0, getWidth(), getHeight());
    dimPanel.setOpaque(false); // Keep transparent background
    dimPanel.setLayout(null);
    dimPanel.setVisible(false); // Start hidden
    dimPanel.setFocusable(false);

    // Set a black transparent background using background color with alpha
    dimPanel.setBackground(new Color(0, 0, 0, 200)); // RGBA, 200 = ~0.8 alpha

    // Ensure the panel resizes with the window
    this.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            dimPanel.setSize(getWidth(), getHeight());
        }
    });

    // Timer to auto-hide after appearing
    Timer[] hideTimer = new Timer[1];

    // Flicker effect: randomly show dim panel every 5 seconds (40% chance)
    Timer flickerTimer = new Timer(5000, e -> {
        if (Math.random() < 0.4) {
            dimPanel.setVisible(true);
            dimPanel.repaint();

            if (hideTimer[0] != null) hideTimer[0].stop();

            hideTimer[0] = new Timer(3000, evt -> dimPanel.setVisible(false));
            hideTimer[0].setRepeats(false);
            hideTimer[0].start();
        }
    });

    flickerTimer.setInitialDelay(0);
    flickerTimer.start();
}

private void resetLetterPosition(JLabel letter) {
    int newX;
    int newY = 0;  // Reset Y to 0 so it starts from the top.
    boolean isColliding;

    // Create a list of all the X positions that are already occupied by letters
    java.util.List<Integer> occupiedXPositions = new java.util.ArrayList<>();
    java.util.List<Integer> occupiedYPositions = new java.util.ArrayList<>();

    // Populate the list with the X and Y positions of other falling letters
    for (JLabel otherLetter : letterCaughtMap.keySet()) {
        if (otherLetter != letter) {
            occupiedXPositions.add(otherLetter.getX());
            occupiedYPositions.add(otherLetter.getY());
        }
    }

    // Generate a random X position for the letter, ensuring it doesn't overlap with others
    do {
        newX = rand.nextInt(getWidth() - letter.getWidth());  // Random X within the window width

        // Check if the new X position overlaps with any other letter horizontally
        isColliding = false;
        for (int i = 0; i < occupiedXPositions.size(); i++) {
            int x = occupiedXPositions.get(i);
            int y = occupiedYPositions.get(i);

            // If new X and Y is within range of any existing letter, it's a collision
            if (Math.abs(newX - x) < letter.getWidth() && Math.abs(newY - y) < letter.getHeight()) {
                isColliding = true;
                break;  // Exit the loop early if a collision is found
            }
        }
    } while (isColliding);  // Repeat until no collision is detected

    // Set the new position for the letter
    letter.setLocation(newX, newY);
}


    private void checkWordCompleted() {
        if (currentAnswer.toString().equals(TARGET_WORD)) {
            stopAllTimers();
            endGameSuccess();
        }
    }
    
    private void startBombTimer(JLabel bomb, int fallSpeed) {
    Timer bombTimer = new Timer(fallSpeed, e -> {
        if (isPaused) {
            return;
        }

        bomb.setLocation(bomb.getX(), bomb.getY() + 5);

        if (bomb.getY() > getHeight()) {
            resetLetterPosition(bomb); // reset bomb if it falls past the screen
        }

        if (bomb.getBounds().intersects(characterLabel.getBounds())) {
            pauseGame();
                caughtBomb qp = new caughtBomb(username, currentLevel, this);
                qp.setVisible(true); // Show on top
                qp.setLocationRelativeTo(null);
                showGameOverScreen(username, currentLevel, this);
        }
    });

    bombTimer.start();
}

private void startPaperFall() {
    resetPaper(); // Ensure paper starts at a random top position
    paperY = 0;

    Timer paperTimer = new Timer(30, e -> {
        if (isPaused) return;

        paperY += 2; // Adjust fall speed
        paperLabel.setLocation(paperLabel.getX(), paperY);

        // Check collision with character
        if (characterLabel.getBounds().intersects(paperLabel.getBounds())) {
            ((Timer) e.getSource()).stop(); // Stop the paper timer for now

        int randomOutcome = ThreadLocalRandom.current().nextInt(2); // ← 0 or 1
        System.out.println("Random outcome: " + randomOutcome); // ← I-add ito!

            if (randomOutcome == 0) {
                pauseGame();
                trapPaper qp = new trapPaper(username, currentLevel, this);
                qp.setVisible(true); // Show on top
                qp.setLocationRelativeTo(null);
            } else {
                pauseGame();
                questionpaper qp = new questionpaper(username, currentLevel, this);
                qp.setVisible(true); // Show on top
                qp.setLocationRelativeTo(null);
            }

            resetPaper();
            startPaperFall(); 
        }

        // If paper falls off screen (missed)
        if (paperY > getHeight()) {
            resetPaper(); // Randomize position
            paperY = 0;   // Reset Y position to top
        }
    });

    letterTimers.add(paperTimer);
    paperTimer.setInitialDelay(10000); // Optional: add an initial delay to make the bomb fall after a short delay
    paperTimer.setRepeats(true);
    paperTimer.start();
}


private void resetPaper() {
    // Set random X and reset Y
    int randomX = randomPaper.nextInt(Math.max(1, getWidth() - paperLabel.getWidth())); // Avoid negative width
    paperLabel.setLocation(randomX, 0);
}

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Level1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Level1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Level1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Level1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Level1("", 1).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel amoss;
    private javax.swing.JLabel answerField;
    private javax.swing.JLabel bg;
    private javax.swing.JLabel bombLabel;
    private javax.swing.JLabel candyLabel;
    private javax.swing.JLabel characterLabel;
    private javax.swing.JLabel coin;
    private javax.swing.JLabel coinLabel;
    private javax.swing.JLayeredPane dimPanel;
    private javax.swing.JButton freezeButton;
    private javax.swing.JLabel freezeLabel;
    private javax.swing.JLabel gifLabel;
    private javax.swing.JLabel heart1;
    private javax.swing.JLabel heart2;
    private javax.swing.JLabel heart3;
    private javax.swing.JButton hintButton;
    private javax.swing.JLabel hintLabel;
    private javax.swing.JLabel kmoss;
    private javax.swing.JLabel letterA;
    private javax.swing.JLabel letterB;
    private javax.swing.JLabel letterE;
    private javax.swing.JLabel letterH;
    private javax.swing.JLabel letterL;
    private javax.swing.JLabel letterP;
    private javax.swing.JLabel paperLabel;
    private javax.swing.JButton quitButton;
    private javax.swing.JLabel rain;
    private javax.swing.JButton storeButton;
    private javax.swing.JLabel timerLabel;
    // End of variables declaration//GEN-END:variables
}
