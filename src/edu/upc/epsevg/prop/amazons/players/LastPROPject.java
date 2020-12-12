/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.amazons.players;

/**
 *
 * @author Umigame
 */
import edu.upc.epsevg.prop.amazons.CellType;
import edu.upc.epsevg.prop.amazons.GameStatus;
import edu.upc.epsevg.prop.amazons.IPlayer;
import edu.upc.epsevg.prop.amazons.Move;
import edu.upc.epsevg.prop.amazons.SearchType;
import java.awt.Point;
import java.util.ArrayList;

public class LastPROPject implements IPlayer {

    String name;
    int prof;

    public LastPROPject(String name) {
        this.name = name;
    }

    /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param s Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    @Override
    public Move move(GameStatus s) {
        // Obtenim els valors necessaris per a fer la simulació
        int valor = -100000, heuristica = 0;
        int numberOfNodesExplored = 0, maxDepthReached = 2;
        Point amazonFrom = new Point (0, 0), amazonTo = new Point (0, 0), arrowTo = new Point (0, 0);
        Move bestmove = new Move(amazonFrom, amazonTo, arrowTo, numberOfNodesExplored, maxDepthReached, SearchType.MINIMAX);
        
        // Recorrem les diverses fitxes del tauler
        int qn = s.getNumberOfAmazonsForEachColor();
        CellType color = s.getCurrentPlayer();
        for (int q = 0; q < qn; ++q) {
            // Obtenim la coordenada de la fitxa amb el color i el seu index
            amazonFrom = s.getAmazon(color, q);
            // Creem un vector dels posibles moviments de cada fitxa
            ArrayList<Point> a = s.getAmazonMoves(amazonFrom, false);
            
            // Recorrem els posibles moviments de cada fitxa
            for (int i = 0; i < a.size(); ++i) {
                // Cada cop explorem un node més
                ++numberOfNodesExplored;
                // Creem una copia del tauler per a poder explorar una jugada
                GameStatus mov_fitxa = new GameStatus(s);
                // Movem la fitxa i ho anem fent amb totes les posibles tirades
                mov_fitxa.moveAmazon(amazonFrom, a.get(i));
                amazonTo = a.get(i);
                
                // Recorrem el tauler per veure les posicions buides per a posar la flexa
                for (int x = 0; x < s.getSize(); ++x) {
                    for (int y = 0; y < s.getSize(); ++y) {
                        if (mov_fitxa.getPos(x, y) == CellType.EMPTY) {
                            Point coord = new Point (x,y);
                            mov_fitxa.placeArrow(coord);
                        }
                    }
                }
                
                heuristica = MinValor(mov_fitxa, color, amazonFrom, amazonTo, arrowTo, numberOfNodesExplored, maxDepthReached-1);
                
                if (valor < heuristica) {
                    //Elegir el mejor mov
                    bestmove = new Move(amazonFrom, amazonTo, arrowTo, numberOfNodesExplored, maxDepthReached, SearchType.MINIMAX);
                    valor = heuristica;
                }
            }
        }
        return bestmove;
    }
    
    
    private int MinValor(GameStatus s, int color, Point amazonFrom, Point amazonTo, int numberOfNodesExplored, int maxDepthReached){
        
    
        for (int colmin = 0; colmin < t.getMida(); ++colmin) {
            // Si es pot fer el moviment creem un nou taulell i fem el moviment
            if (t.movpossible(colmin)){
                Tauler tcopia = new Tauler(t);
                tcopia.afegeix(colmin, color);
                // Ens quedem amb el valor mes petit del valor actual i el 
                // resultat retornat per la funció MaxValor (es a dir el mes  
                // petit de tots els possibles moviments)
                valor = Math.min(valor, MaxValor(tcopia, -color, profunditat-1, colmin, jugador, alfa, beta));
                // Poda Alfa-Beta
                beta=Math.min(valor,beta);
                if(beta<=alfa) return valor;
            }
        }
        // Retornem el valor mes petit trobat
        return valor;
    }
    
    private int MaxValor(Tauler t, int color, int profunditat, int col, int jugador, double alfa, double beta){
        // Si es guanya, perd o estem a la profunditat 0 es retorna la heuristica
        // i per tant, es deixa d'explorar més cap abaix
        if (profunditat == 0 || t.solucio(col, -color) || t.solucio(col, color)) {
            return heuristica(t, color, col, jugador, profunditat);
        }
        // Creem un valor enter molt petit suficient per a la heuristica
        int valor = -100000;
        // Recorrem els possibles moviments a partir del taulell rebut
        for (int colmax = 0; colmax < t.getMida(); ++colmax) {
            // Si es pot fer el moviment creem un nou taulell i fem el moviment
            if (t.movpossible(colmax)){
                Tauler tcopia = new Tauler(t);
                tcopia.afegeix(colmax, color);
                // Ens quedem amb el valor mes gran del valor actual i el 
                // resultat retornat per la funció MinValor (es a dir el mes  
                // gran de tots els possibles moviments)
                valor = Math.max(valor, MinValor(tcopia, -color, profunditat-1, colmax, jugador, alfa, beta));
                // Poda Alfa-Beta
                alfa=Math.max(valor,alfa);
                if(beta<=alfa) return valor;
            }
        }
        // Retornem el valor mes gran trobat
        return valor;
    }
    */
    
    // Heuristica
    public int heuristica() {
        return 0;
    }
    
    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public void timeout() {
        // Bah! Humans do not enjoy timeouts, oh, poor beasts !
        System.out.println("No timeout at the time");
    }

    /**
     * Retorna el nom del jugador que s'utlilitza per visualització a la UI
     *
     * @return Nom del jugador
     */
    @Override
    public String getName() {
        return "LastPROPject(" + name + ")";
    }
}
