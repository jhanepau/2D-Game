/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package letterrush;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author junoc
 */
public class storeMenu extends javax.swing.JFrame {
    private String username;  // Change from int userId to String username
    private Connection conn;

    public storeMenu(String username) {
        initComponents();
                
        hintButton.addActionListener(e -> purchaseItem("Hint", 20, hintButton));
        lifeButton.addActionListener(e -> purchaseItem("life", 30, lifeButton));
        tfreezeButton.addActionListener(e -> purchaseItem("freeze", 50, tfreezeButton));

        this.username = username;

        // Connect to the database and update UI
        try {
            conn = DriverManager.getConnection("jdbc:derby://localhost:1527/user", "root", "root");
            updateCoins();
            updatePurchaseLabels();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateCoins() {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT COINS FROM USERS WHERE USERNAME = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int coins = rs.getInt("COINS");
                SwingUtilities.invokeLater(() -> coinLabel.setText("Coins: " + coins));
            } else {
                JOptionPane.showMessageDialog(this, "No data found for username: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving coins: " + e.getMessage());
        }
    }

    private void purchaseItem(String item, int cost, JButton button) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT COINS FROM USERS WHERE USERNAME = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int coins = rs.getInt("COINS");

                if (coins >= cost) {
                    PreparedStatement updateCoins = conn.prepareStatement("UPDATE USERS SET COINS = ? WHERE USERNAME = ?");
                    updateCoins.setInt(1, coins - cost);
                    updateCoins.setString(2, username);
                    updateCoins.executeUpdate();

                    // Insert or update the purchase
                    PreparedStatement check = conn.prepareStatement("SELECT * FROM PURCHASES WHERE USERNAME = ? AND ITEM_NAME = ?");
                    check.setString(1, username);
                    check.setString(2, item);
                    ResultSet checkRs = check.executeQuery();

                    if (checkRs.next()) {
                        int currentQty = checkRs.getInt("QUANTITY");
                        PreparedStatement updateQty = conn.prepareStatement("UPDATE PURCHASES SET QUANTITY = ? WHERE USERNAME = ? AND ITEM_NAME = ?");
                        updateQty.setInt(1, currentQty + 1);
                        updateQty.setString(2, username);
                        updateQty.setString(3, item);
                        updateQty.executeUpdate();
                    } else {
                        PreparedStatement insert = conn.prepareStatement("INSERT INTO PURCHASES (USERNAME, ITEM_NAME, QUANTITY) VALUES (?, ?, 1)");
                        insert.setString(1, username);
                        insert.setString(2, item);
                        insert.executeUpdate();
                    }

                    updateCoins();
                    updatePurchaseLabels();
                    successPscreen();
                } else {
                    notenoughCscreen();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error purchasing item: " + e.getMessage());
        }
    }

    private void updatePurchaseLabels() {
        SwingUtilities.invokeLater(() -> {
            hintLabel.setText("Hints: 0");
            lifeLabel.setText("Life: 0");
            freezeLabel.setText("Freeze: 0");
        });

        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT ITEM_NAME, QUANTITY FROM PURCHASES WHERE USERNAME = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String item = rs.getString("ITEM_NAME");
                int qty = rs.getInt("QUANTITY");

                SwingUtilities.invokeLater(() -> {
                    switch (item.toLowerCase()) {
                        case "hint":
                            hintLabel.setText("Hints: " + qty);
                            break;
                        case "life":
                            lifeLabel.setText("Life: " + qty);
                            break;
                        case "freeze":
                            freezeLabel.setText("Freeze: " + qty);
                            break;
                        default:
                            System.out.println("Unknown item: " + item);
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating purchase labels: " + e.getMessage());
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        hintButton = new javax.swing.JButton();
        lifeButton = new javax.swing.JButton();
        tfreezeButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        coinLabel = new javax.swing.JLabel();
        hintLabel = new javax.swing.JLabel();
        lifeLabel = new javax.swing.JLabel();
        freezeLabel = new javax.swing.JLabel();
        storeBG = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        hintButton.setContentAreaFilled(false);
        getContentPane().add(hintButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 540, 180, 80));

        lifeButton.setContentAreaFilled(false);
        getContentPane().add(lifeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 540, 180, 80));

        tfreezeButton.setContentAreaFilled(false);
        getContentPane().add(tfreezeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 540, 180, 80));

        backButton.setContentAreaFilled(false);
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        getContentPane().add(backButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1230, 30, 150, 50));
        getContentPane().add(coinLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 190, 70, 40));
        getContentPane().add(hintLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 290, 70, 40));
        getContentPane().add(lifeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(1240, 190, 70, 40));
        getContentPane().add(freezeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(1250, 280, 70, 40));

        storeBG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/letterrush/store (1).png"))); // NOI18N
        getContentPane().add(storeBG, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1480, 730));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        Category c = new Category(username);
        c.setVisible(true);
        c.setLocationRelativeTo(null);
        this.dispose();
    }//GEN-LAST:event_backButtonActionPerformed

    private void successPscreen() {
    successpurchase sp = new successpurchase(this);
    sp.setLocationRelativeTo(this);
    sp.setVisible(true);
    }
    
    private void notenoughCscreen() {
    notenoughcoins sp = new notenoughcoins(this);
    sp.setLocationRelativeTo(this);
    sp.setVisible(true);
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
            java.util.logging.Logger.getLogger(storeMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(storeMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(storeMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(storeMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new storeMenu("").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JLabel coinLabel;
    private javax.swing.JLabel freezeLabel;
    private javax.swing.JButton hintButton;
    private javax.swing.JLabel hintLabel;
    private javax.swing.JButton lifeButton;
    private javax.swing.JLabel lifeLabel;
    private javax.swing.JLabel storeBG;
    private javax.swing.JButton tfreezeButton;
    // End of variables declaration//GEN-END:variables
}
