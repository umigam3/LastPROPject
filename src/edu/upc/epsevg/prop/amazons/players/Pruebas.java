/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.amazons.players;

import edu.upc.epsevg.prop.amazons.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Dani
 */
public class Pruebas implements IPlayer, IAuto {
    
    private boolean inTime;
    
    CellType jugadr;
    
    private String name;
    private int nodesExplorats;
   
    @Override
    public Move move(GameStatus s){
        
        CellType color = s.getCurrentPlayer();
        int profMax = 100, valor = Integer.MIN_VALUE, heu, prof;
        Float alfa = Float.NEGATIVE_INFINITY, beta = Float.POSITIVE_INFINITY;
        Point queenFrom = new Point(0,0), queenTo = new Point(0,0), arrowTo = new Point(0,0);
        
        nodesExplorats=0;
        inTime=true;
        jugadr = color;

        // Profunditats de menys a més, ha de ser major a 0
        for (prof=1; prof <= profMax && inTime; prof++){
            // Todas las fichas del color
            for (int num = 0; num < s.getNumberOfAmazonsForEachColor(); ++num) {
               Point pos = s.getAmazon(color, num);
               ArrayList<Point> arr = s.getAmazonMoves(pos, false);
               // Movimientos posibles
               for (int i = 0; i < arr.size() && arr.size() > 0; i++){
                   GameStatus mov_queen = new GameStatus(s);
                   mov_queen.moveAmazon(pos, arr.get(i));
                   // Lista de posiciones vacias
                   for (int x = 0; x < s.getSize(); x++){
                       for (int y = 0; y < s.getSize(); y++){
                           if (mov_queen.getPos(x, y)==CellType.EMPTY){
                                GameStatus mov_arrow = new GameStatus(mov_queen);
                                if (mov_arrow.getPos(x, y)==CellType.EMPTY){
                                    mov_arrow.placeArrow(new Point(x, y));
                                    heu = MinValor(mov_arrow, prof-1, alfa, beta);
                                    if (valor < heu) {
                                        valor=heu;
                                        queenFrom=pos;
                                        queenTo=arr.get(i);
                                        arrowTo = new Point(x,y);
                                        System.out.println("Heuristica:"+heu);
                                    }
                                }
                            }
                        }                    
                    }
                }
            }
        }
        return new Move(queenFrom, queenTo, arrowTo, nodesExplorats, prof-1, SearchType.MINIMAX);
    }

    private int MinValor(GameStatus s, int profunditat, float alfa, float beta){
        CellType color = s.getCurrentPlayer();
        if (profunditat == 0 || s.isGameOver()) {
            if (s.isGameOver()){
                return Integer.MAX_VALUE;
            }
            return heuristica(s, profunditat);
        }
        int valor = Integer.MIN_VALUE;
        for (int num = 0; num < s.getNumberOfAmazonsForEachColor(); ++num) {
            Point pos = s.getAmazon(color, num);
            ArrayList<Point> arr = s.getAmazonMoves(pos, false);
            for (int i = 0; i < arr.size() && arr.size() > 0; i++){
                GameStatus mov_queen = new GameStatus(s);
                mov_queen.moveAmazon(pos, arr.get(i));
                // Lista de posiciones vacias
                for (int x = 0; x < s.getSize(); x++){
                    for (int y = 0; y < s.getSize(); y++){
                        if (mov_queen.getPos(x, y)==CellType.EMPTY && inTime){
                            GameStatus mov_arrow = new GameStatus(mov_queen);
                            mov_arrow.placeArrow(new Point(x, y));
                           
                            valor = Math.min(valor, MaxValor(mov_arrow, profunditat-1, alfa, beta));
                            
                            beta=Math.min(valor,beta);
                            if(beta<=alfa) return valor;
                        }
                    }
                }
            }
        }
        return valor;
    }

    private int MaxValor(GameStatus s, int profunditat, float alfa, float beta){
        CellType color = s.getCurrentPlayer();
        if (profunditat == 0 || s.isGameOver()) {
            if (s.isGameOver()){
                return Integer.MIN_VALUE;
            }
            return heuristica(s, profunditat);
        }
        int valor = Integer.MAX_VALUE;
        for (int num = 0; num < s.getNumberOfAmazonsForEachColor(); ++num) {
            Point pos = s.getAmazon(color, num);
            ArrayList<Point> arr = s.getAmazonMoves(pos, false);
            for (int i = 0; i < arr.size() && arr.size() > 0; i++){
                GameStatus mov_queen = new GameStatus(s);
                mov_queen.moveAmazon(pos, arr.get(i));
                // Lista de posiciones vacias
                for (int x = 0; x < s.getSize(); x++){
                    for (int y = 0; y < s.getSize(); y++){
                        if (mov_queen.getPos(x, y)==CellType.EMPTY && inTime){
                            GameStatus mov_arrow = new GameStatus(mov_queen);
                            mov_arrow.placeArrow(new Point(x, y));
                
                            valor = Math.max(valor, MinValor(mov_arrow, profunditat-1, alfa, beta));
                            
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
        // Heuristica del Amazons
        // Per començar obtenim els valors necessaris per a l'heuristica
        int res, res_contrari, total = 0;
        // Obtenim el color actual i la fitxa del PLAYER1 i el PLAYER 2
        CellType color_contrari, color = jugadr;
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
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) heuristica = heuristica - 10; ++buides;
        }
      
        for (int i = 0; i < cells; ++i) {
            Point empty = new Point (p.x - ring + i, p.y - ring);
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) heuristica = heuristica - 10; ++buides;
        }

        for (int j = 0; j < cells - 2; ++j)
        {
            Point empty = new Point (p.x - ring, p.y - ring + j);
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) heuristica = heuristica - 10; ++buides;
        }
        
        for (int j = 0; j < cells - 2; ++j)
        {
            Point empty = new Point (p.x + ring, p.y - ring + j);
            if (isOnBoard(s, empty) && s.getPos(empty) == CellType.EMPTY) heuristica = heuristica - 10; ++buides;
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
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void timeout() {
        System.out.print("Timeout\n");
        inTime=false;
    }
    
    public Pruebas (String name) {
        this.name = name;
    }
}
