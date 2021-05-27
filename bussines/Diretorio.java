package bussines;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.*;
import java.util.Scanner;

public class Diretorio {
    private int profundidadeGlobal;
    private long[] vetor;
    private int Nentradas;
    private long tamanho_total_bucket = 0;
    private static final String FILEPATHDIR = "diretorio.db";
    private Bucket indice;
    private byte[] buffer;

    public Diretorio() throws Exception {
        try {
            RandomAccessFile arq = new RandomAccessFile(FILEPATHDIR, "r");
            arq.seek(0);
            this.profundidadeGlobal = arq.readInt();
            this.Nentradas = arq.readInt();
            this.tamanho_total_bucket = arq.readLong();
            this.vetor = new long[Nentradas];
            this.indice = new Bucket(0,0,Nentradas);
            this.buffer = new byte[(int) this.tamanho_total_bucket];
            for (int i = 0; i < Nentradas; i++) {
                this.vetor[i] = arq.readLong();
            }
            arq.close();
        } catch (FileNotFoundException ffe) {
            Scanner sc = new Scanner(System.in);
            RandomAccessFile arq = new RandomAccessFile(FILEPATHDIR, "rw");
            arq.seek(0);
            System.out.println("Não existe nenhum arquivo criado, para criar um novo\n");

            System.out.println("Digite o tamanho da profundidade global do diretorio: ");
            this.profundidadeGlobal = sc.nextInt();
            arq.writeInt(profundidadeGlobal);

            System.out.println("Digite o tamanho de cada bucket do indice : ");
            this.Nentradas = sc.nextInt();
            arq.writeInt(Nentradas);

            int posDiretorio = (int) Math.pow(2, profundidadeGlobal);
            this.vetor = new long[posDiretorio];

            this.tamanho_total_bucket = ((long)Nentradas * 10) + 8;
            arq.writeLong(tamanho_total_bucket);
            this.indice = new Bucket(this.profundidadeGlobal,0,Nentradas);
            this.buffer = new byte[(int) this.tamanho_total_bucket];
            for (int i = 0; i < posDiretorio; i++) { // for com a posição de cada bucket
                vetor[i] = i * tamanho_total_bucket;
                arq.writeLong(vetor[i]);
            }

            arq.close();
        }
    }
    public int hashFunction(int cpf){
        return cpf % (int)Math.pow(2,this.profundidadeGlobal);
    }

    public void inserirIndice (int cpf, int endereco) throws Exception {
        RandomAccessFile arq = new RandomAccessFile("indice.db", "rw");
        int posBucketCarregado = hashFunction(cpf);
        int novoBucket = hashFunction(cpf) + (int)Math.pow(2,this.profundidadeGlobal);// pos novo bucket
                    //indice
        arq.seek(this.vetor[hashFunction(cpf)]);//
        arq.read(buffer);
        indice.fromByteArray(buffer);

        if(indice.getRegistros_Ativos() < Nentradas) { // =ys
            indice.setLapide(' ',indice.getRegistros_Ativos());
            indice.setCpf(cpf,indice.getRegistros_Ativos());
            indice.setEndereco(endereco, indice.getRegistros_Ativos());
            indice.setRegistros_Ativos(1+indice.getRegistros_Ativos());
            arq.seek(this.vetor[hashFunction(cpf)]);//
            arq.write(indice.toByteArray());
            arq.close();
        }else if(indice.getProfundidadeLocal() < profundidadeGlobal){
            // split apenas de indice
        }else{
            // split de diretorio e indice

        }
        arq.close();
    }

    public void atualizarDiretorioEmDisco(){
        try{
            int posDiretorio = (int) Math.pow(2, profundidadeGlobal);
            RandomAccessFile arq = new RandomAccessFile(FILEPATHDIR, "rw");
            arq.seek(0);
            arq.writeInt(profundidadeGlobal);
            arq.writeInt(Nentradas);
            arq.writeLong(tamanho_total_bucket);
            for (int i = 0; i < posDiretorio; i++) { // for com a posição de cada bucket
                vetor[i] = i * tamanho_total_bucket;
                arq.writeLong(vetor[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void splitDiretorio(int cpf) throws Exception{

        long ultimoEndereco = vetor[(int)Math.pow(2,this.profundidadeGlobal)];// não pode ser

        ultimoEndereco += tamanho_total_bucket;
        int aux = hashFunction(cpf) + (int)Math.pow(2,this.profundidadeGlobal);

        int profAntiga = this.profundidadeGlobal++; // profundidade global atualizada
        int j=0;
        long[] novoDiretorio = new long[(int)Math.pow(2,this.profundidadeGlobal)]; // criação do novo diretorio

        for(int i =0; i < profundidadeGlobal; i++){  // preencher novo diretorio
            novoDiretorio[i] = vetor[j];
            j++;
            if(j == profAntiga)
                j=0;
        }
        novoDiretorio[aux] = ultimoEndereco; // posição do novo bucket
        vetor = novoDiretorio;
        atualizarDiretorioEmDisco();

    }

    public int lerIndice (int cpf) throws Exception{
        RandomAccessFile arq = new RandomAccessFile("indice.db", "r");
        int endereco = -1;

        arq.seek(this.vetor[hashFunction(cpf)]);
        arq.read(buffer);
        indice.fromByteArray(buffer); // Carregar bucket em memoria

        if(indice.getRegistros_Ativos() != 0){
            for(int i =0; i < Nentradas; i++){
                if(indice.getCpf(i) == cpf && indice.getLapide(i) != '*')
                    endereco = indice.getEndereco(i);
            }
        }
        arq.close();
        return endereco;
    }



}


