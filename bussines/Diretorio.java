package bussines;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.*;
import java.util.Arrays;
import java.util.Scanner;

public class Diretorio {
    private int profundidadeGlobal;
    private long[] vetor;
    private int Nentradas;
    private long tamanho_total_bucket = 0;
    private long ultimoEndereco = 0;

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
            this.ultimoEndereco = arq.readLong();
            this.vetor = new long[(int)Math.pow(2, profundidadeGlobal)];
            for (int i = 0; i < (int)Math.pow(2, profundidadeGlobal); i++) {
                this.vetor[i] = arq.readLong();
            }

            this.indice = new Bucket(0,0,Nentradas);
            this.buffer = new byte[(int) this.tamanho_total_bucket];
            arq.close();
        } catch (FileNotFoundException ffe) {
            Scanner sc = new Scanner(System.in);
            RandomAccessFile arq = new RandomAccessFile(FILEPATHDIR, "rw");
            RandomAccessFile arq2 = new RandomAccessFile("indice.db", "rw");
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

            this.ultimoEndereco = (posDiretorio-1) * tamanho_total_bucket;
            arq.writeLong(ultimoEndereco);

            this.indice = new Bucket(this.profundidadeGlobal,0,Nentradas);
            for(int i =0; i< posDiretorio; i++){
                arq2.write(indice.toByteArray());
            }
            this.buffer = new byte[(int) this.tamanho_total_bucket];
            for (int i = 0; i < posDiretorio; i++) { // for com a posição de cada bucket
                vetor[i] = i * tamanho_total_bucket;
                arq.writeLong(vetor[i]);
            }

            arq.close();
            arq2.close();
        }
    }
    public int hashFunction(int cpf){
        return cpf % (int)Math.pow(2,this.profundidadeGlobal);
    }

    public int reHashFunction(int cpf, int profundidadeLocal ){
        return  cpf % (int)Math.pow(2, this.profundidadeGlobal);
    }

    public void inserirIndice (int cpf, int endereco) throws Exception {
        RandomAccessFile arq = new RandomAccessFile("indice.db", "rw");
        int posBucketCarregado = hashFunction(cpf);
        //int novoBucket = hashFunction(cpf) + (int)Math.pow(2,this.profundidadeGlobal);// pos novo bucket
                    //indice
        arq.seek(this.vetor[posBucketCarregado]);//
        arq.read(buffer);
        indice.fromByteArray(buffer);

        if(indice.getRegistros_Ativos() < Nentradas) { // =ys
            indice.setLapide(' ',indice.getRegistros_Ativos());
            indice.setCpf(cpf,indice.getRegistros_Ativos());
            indice.setEndereco(endereco, indice.getRegistros_Ativos());
            indice.setRegistros_Ativos(1+indice.getRegistros_Ativos());
            arq.seek(this.vetor[posBucketCarregado]);//
            arq.write(indice.toByteArray());
            arq.close();
        }else if(indice.getProfundidadeLocal() < profundidadeGlobal){
            // split apenas de indice
            int indiceNovo = novoIndice(posBucketCarregado);
            this.vetor[indiceNovo] = ultimoEndereco + tamanho_total_bucket;
            ultimoEndereco += tamanho_total_bucket;
            // atualizar diretorio em disco
            atualizarDiretorioEmDisco();
            reInsereBucket(cpf, endereco, indice, indice.getRegistros_Ativos());
        }else{
            int indiceNovo = novoIndice(posBucketCarregado);
            // split de diretorio e indice
            splitDiretorio(indiceNovo);
            arq.seek(this.vetor[posBucketCarregado]);//
            Bucket IndiceVazio = new Bucket(indice.getProfundidadeLocal()+1,0,Nentradas);
            arq.write(IndiceVazio.toByteArray());
            reInsereBucket(cpf, endereco, indice, indice.getRegistros_Ativos());

        }
        arq.close();
    }

    public void reInsereBucket(int cpf, int endereco, Bucket indiceCarregado, int parada) throws Exception {
        RandomAccessFile arq = new RandomAccessFile("indice.db", "rw");
        int posBucketCarregado = reHashFunction(cpf, indiceCarregado.getProfundidadeLocal());
        arq.seek(this.vetor[posBucketCarregado]);//
        arq.read(buffer);
        indice.fromByteArray(buffer);

        if(indice.getRegistros_Ativos() < Nentradas) { // =ys
            indice.setLapide(' ',indice.getRegistros_Ativos());
            indice.setCpf(cpf,indice.getRegistros_Ativos());
            indice.setEndereco(endereco, indice.getRegistros_Ativos());
            indice.setRegistros_Ativos(1+indice.getRegistros_Ativos());
            arq.seek(this.vetor[posBucketCarregado]);//

            arq.write(indice.toByteArray());

            arq.close();
        }else if(indice.getProfundidadeLocal() < profundidadeGlobal){
            // split apenas de indice
            int indiceNovo = novoIndice(posBucketCarregado);
            this.vetor[indiceNovo] = ultimoEndereco + tamanho_total_bucket;
            ultimoEndereco += tamanho_total_bucket;
            // atualizar diretorio em disco
            atualizarDiretorioEmDisco();
            reInsereBucket(cpf, endereco, indice, indice.getRegistros_Ativos());

        }else{
            int indiceNovo = novoIndice(posBucketCarregado);
            // split de diretorio e indice
            splitDiretorio(indiceNovo);
            arq.seek(this.vetor[posBucketCarregado]);//
            Bucket IndiceVazio = new Bucket(indice.getProfundidadeLocal()+1,0,Nentradas);
            arq.write(IndiceVazio.toByteArray());
            reInsereBucket(cpf, endereco, indice, indice.getRegistros_Ativos());
        }
        if(parada <= indiceCarregado.getRegistros_Ativos()){
            int indice = indiceCarregado.getRegistros_Ativos() - parada ;
            reInsereBucket(indiceCarregado.getCpf(indice), indiceCarregado.getEndereco(indice), indiceCarregado, --parada);
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
            arq.writeLong(ultimoEndereco);
            for (int i = 0; i < posDiretorio; i++) { // for com a posição de cada bucket
                //vetor[i] = i * tamanho_total_bucket;
                arq.writeLong(vetor[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int novoIndice(int posBucketCarregado){
        return posBucketCarregado + (int)Math.pow(2,this.profundidadeGlobal);
    }

    public void splitDiretorio(int indicenovo) throws Exception{
        this.ultimoEndereco += tamanho_total_bucket; // Novo Bucket
        int profAntiga = (int)Math.pow(2,this.profundidadeGlobal++)-1; // profundidade global atualizada
        int j= 0;
        int tamanhoNovoDiretorio = (int)Math.pow(2,this.profundidadeGlobal);
        long[] novoDiretorio = new long[tamanhoNovoDiretorio]; // criação do novo diretorio

        for(int i =0; i < tamanhoNovoDiretorio; i++){  // preencher novo diretorio
            novoDiretorio[i] = vetor[j];      // vetorNovo[1 2 3 4, 1, 2, 3, 4]
            j++;
            if(j > profAntiga) {
                j = 0;
            }
        }
        novoDiretorio[indicenovo] = ultimoEndereco; // posição do novo bucket
        vetor = novoDiretorio;
        atualizarDiretorioEmDisco();
    }



    public void splitIndice(){

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

    @Override
    public String toString() {
        return "Diretorio{" +
                "profundidadeGlobal= " + profundidadeGlobal +
                "\n vetor= " + Arrays.toString(vetor) +
                "\n Nentradas= " + Nentradas +
                "\n tamanho_total_bucket= " + tamanho_total_bucket +
                "\n ultimoEndereço= " + ultimoEndereco +
                "\n indice=" + indice.toString() +
                '}';
    }
}


