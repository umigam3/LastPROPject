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
import edu.upc.epsevg.prop.amazons.IAuto;
import edu.upc.epsevg.prop.amazons.Move;
import edu.upc.epsevg.prop.amazons.SearchType;
import java.awt.Point;
import java.util.ArrayList;

public class LastPROPject implements IPlayer, IAuto {

    String name;
    private GameStatus s;

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
        int valor = -100000, heu = 0;
        int numberOfNodesExplored = 0, maxDepthReached = 2;
        Point amazonFrom = new Point (0, 0), amazonTo = new Point (0, 0), arrowTo = new Point (0, 0);
        Move bestmove = new Move(amazonFrom, amazonTo, arrowTo, numberOfNodesExplored, maxDepthReached, SearchType.MINIMAX);
        Float alfa = Float.NEGATIVE_INFINITY, beta = Float.POSITIVE_INFINITY;

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
                
                // Creem una copia del tauler per a poder explorar una jugada
                GameStatus mov_fitxa = new GameStatus(s);
                
                // Movem la fitxa i ho anem fent amb totes les posibles tirades
                mov_fitxa.moveAmazon(amazonFrom, a.get(i));
                amazonTo = a.get(i);
                
                // Recorrem el tauler per veure les posicions buides per a posar la flexa
                for (int x = 0; x < s.getSize(); ++x) {
                    for (int y = 0; y < s.getSize(); ++y) {
                        GameStatus put_fletxa = new GameStatus(mov_fitxa);

                        if (mov_fitxa.getPos(x, y) == CellType.EMPTY) {
                            
                            // Creem una coordenada on anirem posant les fletxes                            
                            Point coord = new Point (x,y);
                            put_fletxa.placeArrow(coord);
                            arrowTo = coord;
                            
                            // Cridem al MinValor per començar amb el algorisme minimax
                            heu = MinValor(put_fletxa, color, amazonFrom, amazonTo, arrowTo, numberOfNodesExplored, maxDepthReached-1, alfa, beta);
                            
                            if (valor < heu) {
                                //Elegir el mejor mov
                                bestmove = new Move(amazonFrom, amazonTo, arrowTo, numberOfNodesExplored, maxDepthReached, SearchType.MINIMAX);
                                valor = heu;
                            }
                        }
                    }
                }
            }
        }
        
        return bestmove;
    }
    
    
    private int MinValor(GameStatus s, CellType color, Point amazonFrom, Point amazonTo, Point arrowTo, int numberOfNodesExplored, int maxDepthReached, double alfa, double beta){
        
        // Obtenim els valors necessaris per a fer la simulació
        int valor = 100000;
        if (maxDepthReached == 0) return heuristica(s, numberOfNodesExplored);
        color = s.getCurrentPlayer();
        
        // Recorrem les diverses fitxes del tauler
        int qn = s.getNumberOfAmazonsForEachColor();
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
                            GameStatus put_fletxa = new GameStatus(mov_fitxa);
                            
                            // Creem una coordenada on anirem posant les fletxes
                            Point coord = new Point (x,y);
                            put_fletxa.placeArrow(coord);
                            arrowTo = coord;
                            
                            // Cridem al MinValor agafant el max entre aquesta crida i el valor
                            valor = Math.min(valor, MaxValor(put_fletxa, color, amazonFrom, amazonTo, arrowTo, numberOfNodesExplored, maxDepthReached-1, alfa, beta));
                            
                            // Poda alfa-beta
                            beta=Math.min(valor,beta);
                            if(beta<=alfa) return valor;
                        }
                    }
                }    
            }
        }
        
        return valor;
    }
    
    private int MaxValor(GameStatus s, CellType color, Point amazonFrom, Point amazonTo, Point arrowTo, int numberOfNodesExplored, int maxDepthReached, double alfa, double beta){
        
        // Obtenim els valors necessaris per a fer la simulació
        int valor = -100000;
        if (maxDepthReached == 0) return heuristica(s, numberOfNodesExplored);
        color = s.getCurrentPlayer();
        
        // Recorrem les diverses fitxes del tauler
        int qn = s.getNumberOfAmazonsForEachColor();
        for (int q = 0; q < qn; ++q) {
            // Obtenim la coordenada de la fitxa amb el color i el seu index
            amazonFrom = s.getAmazon(color, q);
            // Creem un vector dels posibles moviments de cada fitxa
            ArrayList<Point> a = s.getAmazonMoves(amazonFrom, false);
            
            // Recorrem els posibles moviments de cada fitxa
            for (int i = 0; i < a.size(); ++i) {
                // Creem una copia del tauler per a poder explorar una jugada
                GameStatus mov_fitxa = new GameStatus(s);
                // Movem la fitxa i ho anem fent amb totes les posibles tirades
                mov_fitxa.moveAmazon(amazonFrom, a.get(i));
                amazonTo = a.get(i);
                
                // Recorrem el tauler per veure les posicions buides per a posar la flexa
                for (int x = 0; x < s.getSize(); ++x) {
                    for (int y = 0; y < s.getSize(); ++y) {
                        if (mov_fitxa.getPos(x, y) == CellType.EMPTY) {
                            GameStatus put_fletxa = new GameStatus(mov_fitxa);
                            
                            // Creem una coordenada on anirem posant les fletxes                            
                            Point coord = new Point (x,y);
                            put_fletxa.placeArrow(coord);
                            arrowTo = coord;
                            
                            // Cridem al MinValor agafant el max entre aquesta crida i el valor
                            valor = Math.max(valor, MinValor(put_fletxa, color, amazonFrom, amazonTo, arrowTo, numberOfNodesExplored, maxDepthReached-1, alfa, beta));
                            
                            // Poda alfa-beta
                            alfa=Math.max(valor,alfa);
                            if(beta<=alfa) return valor;
                        }
                    }
                }       
            }
        }
        
        return valor;
    }
    
    // Heuristica
    public int heuristica(GameStatus s, int numberOfNodesExplored) {
        ++numberOfNodesExplored;
        // Heuristica del Amazons
        // Per començar obtenim els valors necessaris per a l'heuristica
        int res, res_contrari, total = 0;
        // Obtenim el color actual i la fitxa del PLAYER1 i el PLAYER 2
        CellType color_contrari, color = s.getCurrentPlayer();
        Point fitxa, fitxa_contraria;
        
        // Obtenim el color contrari
        if (color == CellType.PLAYER1) color_contrari = CellType.PLAYER2;
        else color_contrari = CellType.PLAYER1;
        
        // Contem totes les fitxes 
        int qn = s.getNumberOfAmazonsForEachColor();

        // Recorrem les fitxes de cada color
        for (int q = 0; q < qn; ++q) {
            // Agafem la fitxa del nostre color
            fitxa = s.getAmazon(color, q);
            // Agafem la fitxa del color contrari
            fitxa_contraria = s.getAmazon(color_contrari, q);

            // Sistema d'anells
            for (int ring = 1; ring < 2; ++ring) {
                // Cridem a la funció moviments amb la fitxa del color actual
                res = moviments(s, fitxa, 1);
                // Cridem a la funció moviments amb la fitxa del color contrari
                res_contrari = fletxa(s, fitxa_contraria, 1);
                
                // Anem sumant les heuristiques tretes per les dues funcions
                total = total + res + res_contrari;
            }
            if (total > 5000) return total;
        }
        
        //System.out.println("Heuristica:"+res);
        return total;
    }

    int moviments(GameStatus s, Point p, int ring) {
        int heuristica = 0;
        int cells = ring*2 + 1;
        
        for (int i = 0; i < cells; ++i) {
            Point empty = new Point (p.x - ring + i, p.y + ring);
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) ++heuristica;
        }
      
        for (int i = 0; i < cells; ++i) {
            Point empty = new Point (p.x - ring + i, p.y - ring);
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) ++heuristica;
        }

        for (int j = 0; j < cells - 2; ++j)
        {
            Point empty = new Point (p.x - ring, p.y - ring + j);
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) ++heuristica;
        }
        
        for (int j = 0; j < cells - 2; ++j)
        {
            Point empty = new Point (p.x + ring, p.y - ring + j);
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) ++heuristica;
        }
        
        //if (buides == 8) return 10000;
        //if (s.isGameOver()) return 10000;
        return heuristica;
    }
    
    int fletxa(GameStatus s, Point p, int ring) {
        int heuristica = 0, buides = 0;
        int cells = ring*2 + 1;
        
        for (int i = 0; i < cells; ++i) {
            Point empty = new Point (p.x - ring + i, p.y + ring);
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) heuristica = heuristica - 3; ++buides;
        }
      
        for (int i = 0; i < cells; ++i) {
            Point empty = new Point (p.x - ring + i, p.y - ring);
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) heuristica = heuristica - 3; ++buides;
        }

        for (int j = 0; j < cells - 2; ++j)
        {
            Point empty = new Point (p.x - ring, p.y - ring + j);
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) heuristica = heuristica - 3; ++buides;
        }
        
        for (int j = 0; j < cells - 2; ++j)
        {
            Point empty = new Point (p.x + ring, p.y - ring + j);
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) heuristica = heuristica - 3; ++buides;
        }
        
        if (buides == 0) return 10000;
        //if (s.isGameOver()) return 10000;
        return heuristica;
    }
    
    /**
     * Ens diu si un Point p es troba dins de un Tauler s
     * 
     * @return boolean
     */
    boolean isOnBoard(GameStatus s, Point p) {
        boolean isIn = false;
        if (p.x < s.getSize() && p.x >= 0 && p.y < s.getSize() && p.y >= 0) isIn = true;
        return isIn;
    }
    
    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public void timeout() {
        // Bah! Humans do not enjoy timeouts, oh, poor beasts !
        //System.out.println("No timeout at the time");
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
