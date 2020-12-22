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

public class LastPROPject2 implements IPlayer, IAuto {

    String name;
    private boolean timeout = false;
    private int nodesExplorats;
    private int heu;
    
    CellType player;

    public LastPROPject2(String name) {
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
        int valor = -100000;
        int maxDepthReached;
        CellType color = s.getCurrentPlayer();
        Point amazonFrom, amazonTo, arrowTo;
        Point amazonFromFinal = new Point (0, 0), amazonToFinal = new Point (0, 0), arrowToFinal = new Point (0, 0);
        Float alfa = Float.NEGATIVE_INFINITY, beta = Float.POSITIVE_INFINITY;

        // Variables globals
        int profunditat = 0;
        timeout = false;
        player = color;
        nodesExplorats = 0;
        
        while (true) {
            ++profunditat;
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
                            GameStatus put_fletxa = new GameStatus(mov_fitxa);

                            if (mov_fitxa.getPos(x, y) == CellType.EMPTY) {
                                
                                // Creem una coordenada on anirem posant les fletxes                            
                                Point coord = new Point (x,y);
                                put_fletxa.placeArrow(coord);
                                arrowTo = coord;

                                // Cridem al MinValor per començar amb el algorisme minimax
                                maxDepthReached = profunditat;
                                if (!timeout) heu = MinValor(put_fletxa, color, amazonFrom, amazonTo, arrowTo, maxDepthReached-1, alfa, beta);

                                if (valor < heu) {
                                    //Elegir el mejor mov
                                    amazonFromFinal = new Point(amazonFrom);
                                    amazonToFinal = new Point(amazonTo);
                                    arrowToFinal = new Point(arrowTo);
                                    valor = heu;
                                }
                            }
                        }
                    }
                }
            }
        if (timeout) break;
        }
        
        return new Move(amazonFromFinal, amazonToFinal, arrowToFinal, nodesExplorats, profunditat, SearchType.MINIMAX);
    }
    
    
    private int MinValor(GameStatus s, CellType color, Point amazonFrom, Point amazonTo, Point arrowTo, int maxDepthReached, double alfa, double beta){
        
        color = s.getCurrentPlayer();
        // Obtenim els valors necessaris per a fer la simulació
        int valor = 100000;
        if (maxDepthReached == 0) return heuristica(s);
                
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
                            if (!timeout) valor = Math.min(valor, MaxValor(put_fletxa, color, amazonFrom, amazonTo, arrowTo, maxDepthReached-1, alfa, beta));
                            
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
    
    private int MaxValor(GameStatus s, CellType color, Point amazonFrom, Point amazonTo, Point arrowTo, int maxDepthReached, double alfa, double beta){
        
        // Obtenim els valors necessaris per a fer la simulació
        color = s.getCurrentPlayer();

        int valor = -100000;
        if (maxDepthReached == 0) return heuristica(s);
        
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
                            if (!timeout) valor = Math.max(valor, MinValor(put_fletxa, color, amazonFrom, amazonTo, arrowTo, maxDepthReached-1, alfa, beta));
                            
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
    public int heuristica(GameStatus s) {
        ++nodesExplorats;
        // Heuristica del Amazons
        // Per començar obtenim els valors necessaris per a l'heuristica
        int res, res_contrari, total = 0;
        // Obtenim el color actual i la fitxa del PLAYER1 i el PLAYER 2
        CellType color_contrari, color = player;
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
                res = moviments(s, fitxa, ring);
                // Cridem a la funció moviments amb la fitxa del color contrari
                res_contrari = fletxa(s, fitxa_contraria, ring);
                
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
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) heuristica = heuristica + 10;
        }
      
        for (int i = 0; i < cells; ++i) {
            Point empty = new Point (p.x - ring + i, p.y - ring);
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) heuristica = heuristica + 10;
        }

        for (int j = 0; j < cells - 2; ++j)
        {
            Point empty = new Point (p.x - ring, p.y - ring + j);
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) heuristica = heuristica + 10;
        }
        
        for (int j = 0; j < cells - 2; ++j)
        {
            Point empty = new Point (p.x + ring, p.y - ring + j);
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) heuristica = heuristica + 10;
        }
        
        //if (buides == 8) return 10000;
        if (s.isGameOver() && s.GetWinner() == player) return 100000;
        return heuristica;
    }
    
    int fletxa(GameStatus s, Point p, int ring) {
        int heuristica = 0, buides = 0;
        int cells = ring*2 + 1;
        
        for (int i = 0; i < cells; ++i) {
            Point empty = new Point (p.x - ring + i, p.y + ring);
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) heuristica = heuristica - 5; ++buides;
        }
      
        for (int i = 0; i < cells; ++i) {
            //System.out.println("Fitxa: "+p);
            Point empty = new Point (p.x - ring + i, p.y - ring);
            //System.out.println("Empty: "+empty);
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) heuristica = heuristica - 5; ++buides;
        }

        for (int j = 0; j < cells - 2; ++j)
        {
            Point empty = new Point (p.x - ring, p.y - ring + j);
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) heuristica = heuristica - 5; ++buides;
        }
        
        for (int j = 0; j < cells - 2; ++j)
        {
            Point empty = new Point (p.x + ring, p.y - ring + j);
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) heuristica = heuristica - 5; ++buides;
        }
        
        
        if (buides <= 0) return 10000;
        if (s.isGameOver() && s.GetWinner() == player) return 100000;
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
        // Timeout for LastPROPject !
        timeout = true;
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

