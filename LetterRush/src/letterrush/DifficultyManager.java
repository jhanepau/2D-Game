/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package letterrush;

/**
 *
 * @author User
 */
public class DifficultyManager {
        private static String difficulty = "Easy"; // Default

    public static void setDifficulty(String newDifficulty) {
        difficulty = newDifficulty;
    }

    public static String getDifficulty() {
        return difficulty;
    }
}
