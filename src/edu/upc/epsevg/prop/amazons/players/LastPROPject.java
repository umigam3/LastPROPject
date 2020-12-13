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
                        // Cada cop explorem un node més
                        ++numberOfNodesExplored;

                        if (mov_fitxa.getPos(x, y) == CellType.EMPTY) {
                                                        
                            Point coord = new Point (x,y);
                            put_fletxa.placeArrow(coord);
                            arrowTo = coord;
                            
                            heu = MinValor(put_fletxa, color, amazonFrom, amazonTo, arrowTo, numberOfNodesExplored, maxDepthReached-1);
                            
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
    
    
    private int MinValor(GameStatus s, CellType color, Point amazonFrom, Point amazonTo, Point arrowTo, int numberOfNodesExplored, int maxDepthReached){
        
        // Obtenim els valors necessaris per a fer la simulació
        int valor = 100000;
        if (maxDepthReached == 0) return heuristica(s);
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
                            // Cada cop explorem un node més
                            ++numberOfNodesExplored;
                            
                            Point coord = new Point (x,y);
                            put_fletxa.placeArrow(coord);
                            arrowTo = coord;
                            
                            valor = Math.min(valor, MaxValor(put_fletxa, color, amazonFrom, amazonTo, arrowTo, numberOfNodesExplored, maxDepthReached-1));
                        }
                    }
                }    
            }
        }
        return valor;
    }
    
    private int MaxValor(GameStatus s, CellType color, Point amazonFrom, Point amazonTo, Point arrowTo, int numberOfNodesExplored, int maxDepthReached){
        
        // Obtenim els valors necessaris per a fer la simulació
        int valor = -100000;
        if (maxDepthReached == 0) return heuristica(s);
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
                            // Cada cop explorem un node més
                            ++numberOfNodesExplored;
                            GameStatus put_fletxa = new GameStatus(mov_fitxa);
                            
                            Point coord = new Point (x,y);
                            put_fletxa.placeArrow(coord);
                            arrowTo = coord;
                            
                            valor = Math.max(valor, MinValor(put_fletxa, color, amazonFrom, amazonTo, arrowTo, numberOfNodesExplored, maxDepthReached-1));
                        }
                    }
                }       
            }
        }
        return valor;
    }
    
    // Heuristica
    public int heuristica(GameStatus s) {
        /*
        class mov
        {
          public Point;
          public poss;
        }


        int heuristica()
        {
          // Heuristica del Amazons
          array [4]

          int ring = 1;for ficha
                  array[ficha] = moviments(s, p, ring)

        }


        int moviments(GameStatus s, point p, int ring)
        {
          int total = 0;
          int cells = ring*2 + 1;

          for int i = 0; i < cells; ++i
          {
            if ([p.x - ring + i][p.y - ring] esta dentro && [p.x - ring + i][p.y + ring] == vacio) ++free;
          }
          for int i = 0; i < cells; ++j
          {
            if ([p.x - ring + i][p.y + ring] esta dentro && [p.x - ring + i][p.y + ring] == vacio) ++free;
          }


          for int j = 0; j < cells - 2; ++j
          {
            if ([p.x - ring][p.y - ring + j] esta dentro && [p.x - ring][p.y - ring + j] == vacio) ++free;
          }
            for int j = 0; i < cells - 2; ++i
          {
            if ([p.x + ring][p.y - ring + j] esta dentro && [p.x - ring][p.y - ring + j] == vacio) ++free;
          }

          return free;
        }
        */
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
