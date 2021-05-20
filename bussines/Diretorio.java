package bussines;

import java.lang.*;

public class Diretorio {
    private int profundidadeGlobal;
    private int[] vetor;
    private int Nentrada;

    public Diretorio(int pg, int nEntradas){
        this.profundidadeGlobal = pg;
        this.vetor = new int[(int)Math.pow(2,profundidadeGlobal)];
    }


}
