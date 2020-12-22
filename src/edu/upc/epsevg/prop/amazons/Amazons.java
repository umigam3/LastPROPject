package edu.upc.epsevg.prop.amazons;

import edu.upc.epsevg.prop.amazons.players.HumanPlayer;
import edu.upc.epsevg.prop.amazons.players.CarlinhosPlayer;
import edu.upc.epsevg.prop.amazons.players.LastPROPject;
import edu.upc.epsevg.prop.amazons.players.LastPROPject2;
import edu.upc.epsevg.prop.amazons.players.Pruebas;
import edu.upc.epsevg.prop.amazons.players.RandomPlayer;
import javax.swing.SwingUtilities;

/**
 *
 * @author bernat
 */
public class Amazons {
        /**
     * @param args
     */
    public static void main(String[] args) {
        
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                IPlayer player1 = new LastPROPject2("LastPROPject");
                IPlayer player2 = new CarlinhosPlayer();

                new AmazonsBoard(player1 , player2, 10, Level.FULL_BOARD);
                
                System.out.println("Commit");
            }
        });
    }
}
