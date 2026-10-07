/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package letterrush;

import javax.swing.SwingUtilities;

/**
 *
 * @author junoc
 */
public class levelCompleted extends javax.swing.JFrame {
    private String username;
    private int currentLevel = 1;
    private javax.swing.JFrame previousFrame;
    
    public levelCompleted(String username, int currentLevel, javax.swing.JFrame previousFrame) {
    this.username = username;
    this.currentLevel = currentLevel;
    this.previousFrame = previousFrame;
    initComponents();
    }

private void switchFrame(String username, int level) {
    SwingUtilities.invokeLater(() -> {
        // Dispose the levelCompleted frame (this)
        this.dispose();

        // Dispose the previous game frame (now)
        if (previousFrame != null) {
            previousFrame.dispose();
        }

        // Open the next level frame
        switch (level) {
            case 1:
                new Level1(username, level).setVisible(true);
                break;
            case 2:
                new Level2(username).setVisible(true);
                break;
            // ... handle more levels
            default:
                System.out.println("Invalid level");
        }
    });
}




    public void setScore(int score) {
        messageLabel.setText(String.valueOf(score));
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nextButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        messageLabel = new javax.swing.JLabel();
        bgsusuccess = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        nextButton.setContentAreaFilled(false);
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
        getContentPane().add(nextButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 240, 60, 20));

        okButton.setContentAreaFilled(false);
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        getContentPane().add(okButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 240, 60, 20));

        messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(messageLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 190, 70, 30));

        bgsusuccess.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bgsusuccess.setIcon(new javax.swing.ImageIcon(getClass().getResource("/letterrush/levelup.png"))); // NOI18N
        getContentPane().add(bgsusuccess, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -50, 370, 400));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
  SwingUtilities.invokeLater(() -> {
        // Dispose the current levelCompleted frame
        this.dispose();

        // Dispose the previous game frame
        if (previousFrame != null) {
            previousFrame.dispose();
        }

        // Open the Category screen
        Category category = new Category(username);
        category.setLocationRelativeTo(null);
        category.setVisible(true);

    });
    }//GEN-LAST:event_okButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
           if (currentLevel < 10) {
        currentLevel++;
        switchFrame(username, currentLevel);
    } else {
        System.out.println("You have reached the last level!");
    }    }//GEN-LAST:event_nextButtonActionPerformed

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
            java.util.logging.Logger.getLogger(levelCompleted.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(levelCompleted.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(levelCompleted.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(levelCompleted.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {  
                javax.swing.JFrame previous = null;
                new levelCompleted("", 1, previous).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bgsusuccess;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
}
