package bussines;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.*;
import java.util.Arrays;
import java.util.Scanner;

import javax.print.PrintException;

public class Diretorio {
    private int profundidadeGlobal;
    private long[] vetor;
    private int Nentradas;
    private long tamanho_total_bucket = 0;
    private long ultimoEndereco = 0;

    private static final String FILEPATHDIR = "diretorio.db";
    private Bucket indice;
    private byte[] buffer;

    public int getProfundidadeGlobal() {
        return profundidadeGlobal;
    }

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
        return  cpf % (int)Math.pow(2, profundidadeLocal);
    }

    public int hash(int cpf, int profundidade){
        return cpf % (int)Math.pow(2,profundidade);
    }

    /*public void inserirIndice (int cpf, int endereco) throws Exception {
        byte[] buffer2 = new byte[(int) this.tamanho_total_bucket];
        int posBucketCarregado = hash(cpf, profundidadeGlobal); // hashFunction

        RandomAccessFile arq = new RandomAccessFile("indice.db", "rw");
        arq.seek(this.vetor[posBucketCarregado]);
        arq.read(buffer2);
        indice.fromByteArray(buffer2);

    
        System.out.println("registros ativos: " + indice.getRegistros_Ativos());//0  /0  / 1
        System.out.println("profundidade local: "+ indice.getProfundidadeLocal());//3 /3  / 3
        System.out.println("endereco bucket: " + this.vetor[posBucketCarregado]);//140  /56  /56
        System.out.println("Bucket em memoriao: " + indice.toString()+"\n");// 7  

        if(indice.getRegistros_Ativos() < Nentradas) {
            System.out.println("Inserindo CPF: " + cpf);
            indice.setLapide(' ',indice.getRegistros_Ativos());
            indice.setCpf(cpf,indice.getRegistros_Ativos());
            indice.setEndereco(endereco, indice.getRegistros_Ativos());
            indice.setRegistros_Ativos(1+indice.getRegistros_Ativos());
            arq.seek(this.vetor[posBucketCarregado]);//
            arq.write(indice.toByteArray());
            arq.close();
        } else{ //Ocorreu colisão
            int profundidadeLocal = indice.getProfundidadeLocal();
            indice.setProfundidadeLocal(indice.getProfundidadeLocal()+1); // aumenta profundidade do indice
            Bucket bucketVazio = new Bucket(indice.getProfundidadeLocal(),0,Nentradas);
            arq.seek(posBucketCarregado);
            arq.write(bucketVazio.toByteArray()); // Zera bucket no disco
            arq.close();
            bucketVazio = indice.clone();
            if(profundidadeLocal < profundidadeGlobal){//split de buckets
                ultimoEndereco += tamanho_total_bucket;
                this.vetor[posBucketCarregado] = ultimoEndereco;
                atualizarDiretorioEmDisco();
                reHash(cpf, endereco, bucketVazio, 0);                
            }else{// split do diretorio
                
                int indiceNovo = novoIndice(posBucketCarregado, this.profundidadeGlobal);
                // split de diretorio e indice
                splitDiretorio(indiceNovo);
                System.out.println("Hora da colisão: "+indice.getCpf(0));
                System.out.println("Hora da colisão: "+indice.getCpf(1));
                reHash(cpf, endereco, bucketVazio, 0); 
            }

        }

    }

    public void reHash (int cpf, int endereco, Bucket bucketCarregado, int pos) throws Exception {
        byte[] buffer2 = new byte[(int) this.tamanho_total_bucket];
        int posBucketCarregado = hash(cpf,bucketCarregado.getProfundidadeLocal()); // hashFunction

        RandomAccessFile arq = new RandomAccessFile("indice.db", "rw");
        arq.seek(this.vetor[posBucketCarregado]);
        Bucket indicVazio = new Bucket(bucketCarregado.getProfundidadeLocal(),0,Nentradas);
        arq.seek(this.vetor[posBucketCarregado]);
        arq.read(buffer2);
        indicVazio.fromByteArray(buffer2);
        indicVazio.setProfundidadeLocal(bucketCarregado.getProfundidadeLocal());

    
        System.out.println("registros ativos: " + indicVazio.getRegistros_Ativos());//0  /0  / 1
        System.out.println("profundidade local: "+ indicVazio.getProfundidadeLocal());//3 /3  / 3
        System.out.println("endereco bucket: " + this.vetor[posBucketCarregado]);//140  /56  /56
        System.out.println("Bucket em memoriao: " + indicVazio.toString()+"\n");// 7  

        if(indicVazio.getRegistros_Ativos() < Nentradas) {
            System.out.println("Inserindo CPF: " + cpf);
            indicVazio.setLapide(' ',indicVazio.getRegistros_Ativos());
            indicVazio.setCpf(cpf,indicVazio.getRegistros_Ativos());
            indicVazio.setEndereco(endereco, indicVazio.getRegistros_Ativos());
            indicVazio.setRegistros_Ativos(1+indicVazio.getRegistros_Ativos());
            arq.seek(this.vetor[posBucketCarregado]);//
            arq.write(indicVazio.toByteArray());
            arq.close();
        } else{ //Ocorreu colisão
            int profundidadeLocal = indicVazio.getProfundidadeLocal();
            indicVazio.setProfundidadeLocal(indicVazio.getProfundidadeLocal()+1); // aumenta profundidade do indicVazio
            Bucket bucketVazio = new Bucket(indicVazio.getProfundidadeLocal(),0,Nentradas);
            arq.seek(posBucketCarregado);
            arq.write(bucketVazio.toByteArray()); // Zera bucket no disco
            arq.close();
            if(profundidadeLocal < profundidadeGlobal && pos < bucketCarregado.getRegistros_Ativos()){//split de buckets
                ultimoEndereco += tamanho_total_bucket;
                this.vetor[posBucketCarregado] = ultimoEndereco;
                atualizarDiretorioEmDisco();
                reHash(bucketCarregado.getCpf(pos), bucketCarregado.getEndereco(pos), bucketCarregado, pos);                
            }else{// split do diretorio
                
                int indiceNovo = novoIndice(posBucketCarregado, this.profundidadeGlobal);
                // split de diretorio e indice
                splitDiretorio(indiceNovo);
                System.out.println("Hora da colisão: "+indicVazio.getCpf(0));
                System.out.println("Hora da colisão: "+indicVazio.getCpf(1));
                reHash(cpf, endereco, bucketVazio, 0);
            }

        }
        if(pos < bucketCarregado.getRegistros_Ativos()){
            System.out.println("\nProximo cpf a ser inserido: " + bucketCarregado.getCpf(pos));
            reHash(bucketCarregado.getCpf(pos), bucketCarregado.getEndereco(pos), bucketCarregado, ++pos);
          
        }

    }


    */



    private void reHash(Bucket backupBucket, int cpf, int endereco)throws Exception{
        int[] backupCPFs = new int[Nentradas];
        int[] backupEnderecos = new int[Nentradas];
        int profundidadeLocal = backupBucket.getProfundidadeLocal();
        for(int i = 0; i< Nentradas; i++){
            backupCPFs[i] = backupBucket.getCpf(i);
            backupEnderecos[i] = backupBucket.getEndereco(i);
        }

        for(int pos = 0; pos <= Nentradas; pos++){
            if(pos != Nentradas){
                //System.out.println("Hora do reHash: "+backupCPFs[pos]);
                inserirIndice(backupCPFs[pos], backupEnderecos[pos], profundidadeLocal);
            }else{
                inserirIndice(cpf, endereco, profundidadeLocal);
            }
                
        }
    }


    public void inserirIndice (int cpf, int endereco, int profundidade) throws Exception {
        byte[] buffer2 = new byte[(int) this.tamanho_total_bucket];
        int posBucketCarregado = hash(cpf, profundidade); // hashFunction
        Bucket bucket = new Bucket(Nentradas);
        System.out.println("Buffer: "+buffer2.toString()+"\n");
        System.out.println("Bucket antes da leitura: " + bucket.toString()+"\n");

        try{
        RandomAccessFile arq = new RandomAccessFile("indice.db", "r");
        arq.seek(this.vetor[posBucketCarregado]);
        arq.read(buffer2);
        indice.fromByteArray(buffer2);
        arq.close();
        RandomAccessFile raf = new RandomAccessFile("indice.db", "rw");

        
        /*if(indice.getProfundidadeLocal() == 0){
            indice.setProfundidadeLocal(profundidade);
        }*/

        /*System.out.println("Inserindo CPF: " + cpf);
        System.out.println("registros ativos: " + indice.getRegistros_Ativos());//0  /0  / 1
        System.out.println("profundidade local: "+ indice.getProfundidadeLocal());//3 /3  / 3
        System.out.println("endereco indice: " + this.vetor[posindiceCarregado]);//140  /56  /56*/
        System.out.println("indice em memoria: " + indice.toString()+"\n");// 7  

        if(indice.getRegistros_Ativos() < Nentradas) {
            //System.out.println("Inserindo CPF: " + cpf);
            indice.setLapide(' ',indice.getRegistros_Ativos());
            indice.setCpf(cpf,indice.getRegistros_Ativos());
            indice.setEndereco(endereco, indice.getRegistros_Ativos());
            indice.setRegistros_Ativos(1+indice.getRegistros_Ativos());
            raf.seek(this.vetor[posBucketCarregado]);//
            raf.write(indice.toByteArray());
            raf.close();
        } else{ //Ocorreu colisão
            int profundidadeLocal = indice.getProfundidadeLocal();
            indice.setProfundidadeLocal(indice.getProfundidadeLocal()+1); // aumenta profundidade do bucket
            Bucket bucketVazio = new Bucket(indice.getProfundidadeLocal(),0,Nentradas);
            Bucket bucketfantasma = new Bucket(0,0,Nentradas);
            raf.seek(this.vetor[posBucketCarregado]);
            raf.write(bucketVazio.toByteArray()); // Zera bucket no disco
            
            /*raf.seek(posBucketCarregado);
            raf.read(buffer2);
            bucketVazio.fromByteArray(buffer2);
            bucketfantasma.fromByteArray(buffer2);
            System.out.println("Bucket Zerado:"+bucketVazio.toString()); // verificar se zerou 
            System.out.println("Bucket fantasma:"+bucketfantasma.toString()); // verificar se zerou 
            */
            
            if(profundidadeLocal == profundidadeGlobal){ // split Diretorio
                int indiceNovo = novoIndice(posBucketCarregado, this.profundidadeGlobal);
                // Zera bucket no disco
                // split de diretorio e bucket
                splitDiretorio(indiceNovo);
                //System.out.println("bucket na colisão: "+bucket.toString());
                raf.seek(this.vetor[indiceNovo]);
                raf.write(bucketVazio.toByteArray());
                raf.close();
                reHash(indice, cpf, endereco);             
            }else{//split de buckets
                
                    ultimoEndereco += tamanho_total_bucket;
                    this.vetor[posBucketCarregado] = ultimoEndereco;
                    atualizarDiretorioEmDisco();
                    raf.seek(this.vetor[posBucketCarregado]);
                    raf.write(bucketVazio.toByteArray());
                    raf.close();

                    reHash(indice, cpf, endereco); 
                
            }

        }
        raf.close();

        }catch(Exception e){
            e.printStackTrace();
        }

        

    }
/*
    public void inserirIndice (int cpf, int endereco) throws Exception {
        byte[] buffer2 = new byte[(int) this.tamanho_total_bucket];
        RandomAccessFile arq = new RandomAccessFile("indice.db", "rw");
        int posBucketCarregado = hashFunction(cpf);
        System.out.println(posBucketCarregado);
        arq.seek(this.vetor[posBucketCarregado]);//
        arq.read(buffer2);
        indice.fromByteArray(buffer2);
        System.out.println(indice.toString());
    

        if(indice.getRegistros_Ativos() < Nentradas) { 
            indice.setLapide(' ',indice.getRegistros_Ativos());
            indice.setCpf(cpf,indice.getRegistros_Ativos());
            indice.setEndereco(endereco, indice.getRegistros_Ativos());
            indice.setRegistros_Ativos(1+indice.getRegistros_Ativos());
            arq.seek(this.vetor[posBucketCarregado]);//
            arq.write(indice.toByteArray());
            arq.close();
        }else if(indice.getProfundidadeLocal() < profundidadeGlobal){
            // split apenas de indice
            //int indiceNovo = novoIndice(posBucketCarregado, indice.getProfundidadeLocal());
            arq.seek(this.vetor[posBucketCarregado]); 
            Bucket IndiceVazio = new Bucket(indice.getProfundidadeLocal()+1,0,Nentradas);
            arq.write(IndiceVazio.toByteArray());
            this.vetor[posBucketCarregado] = ultimoEndereco + tamanho_total_bucket;
            ultimoEndereco += tamanho_total_bucket;
            // atualizar diretorio em disco
            atualizarDiretorioEmDisco();
            
            indice.setProfundidadeLocal(indice.getProfundidadeLocal()+1);
            reInsereBucket(cpf, endereco, indice, 0);
        }else{
            int indiceNovo = novoIndice(posBucketCarregado, this.profundidadeGlobal);
            // split de diretorio e indice
            splitDiretorio(indiceNovo);

            arq.seek(this.vetor[posBucketCarregado]); 
            Bucket IndiceVazio = new Bucket(indice.getProfundidadeLocal()+1,0,Nentradas);
            arq.write(IndiceVazio.toByteArray());// zera bucket cheio na memoria
            indice.setProfundidadeLocal(indice.getProfundidadeLocal()+1);
            arq.close();
            //reInsereBucket(cpf, endereco, indice, indice.getRegistros_Ativos());
            reInsereBucket(cpf, endereco, indice, 0);
            
            
        }
        arq.close();
    }

    public void reInsereBucket(int cpf, int endereco, Bucket indiceCarregado, int parada) throws Exception {
        
        byte[] buffer2 = new byte[(int) this.tamanho_total_bucket];
        RandomAccessFile arq = new RandomAccessFile("indice.db", "rw");
        int posBucketCarregado = reHashFunction(cpf, indiceCarregado.getProfundidadeLocal());
        Bucket indicVazio = new Bucket(indiceCarregado.getProfundidadeLocal(),0,Nentradas);
        arq.seek(this.vetor[posBucketCarregado]);//
        arq.read(buffer2);
        indicVazio.fromByteArray(buffer2);
        indicVazio.setProfundidadeLocal(indiceCarregado.getProfundidadeLocal());
        System.out.println("Inserindo CPF: " + cpf);

        System.out.println("registros ativos: " + indicVazio.getRegistros_Ativos());//0  /0  / 1
        System.out.println("profundidade local: "+ indicVazio.getProfundidadeLocal());//3 /3  / 3
        System.out.println("endereco bucket: " + this.vetor[posBucketCarregado]);//140  /56  /56
        System.out.println("pos diretorio" + posBucketCarregado);// 7               /3   /3

        if(indicVazio.getRegistros_Ativos() < Nentradas) { // =ys
            System.out.println("batata");
            indicVazio.setLapide(' ',indicVazio.getRegistros_Ativos());
            indicVazio.setCpf(cpf,indicVazio.getRegistros_Ativos());
            indicVazio.setEndereco(endereco, indicVazio.getRegistros_Ativos());
            indicVazio.setRegistros_Ativos(1+indicVazio.getRegistros_Ativos());
            arq.seek(this.vetor[posBucketCarregado]);//

            arq.write(indicVazio.toByteArray());

            arq.close();
        }else if(indicVazio.getProfundidadeLocal() < profundidadeGlobal && parada < indiceCarregado.getRegistros_Ativos()){
            // split apenas de indice
            System.out.println("pepino");
            //int indiceNovo = novoIndice(posBucketCarregado, indicVazio.getProfundidadeLocal());
            this.vetor[posBucketCarregado] = ultimoEndereco + tamanho_total_bucket;
            ultimoEndereco += tamanho_total_bucket;
            // atualizar diretorio em disco
            atualizarDiretorioEmDisco();
            //reInsereBucket(cpf, endereco, indicVazio, indicVazio.getRegistros_Ativos());
            reInsereBucket(indiceCarregado.getCpf(parada), indiceCarregado.getEndereco(parada), indiceCarregado, ++parada);

        }else if(indicVazio.getProfundidadeLocal() == profundidadeGlobal){
            System.out.println("xuxu");
            int indiceNovo = novoIndice(posBucketCarregado, profundidadeGlobal);
            // split de diretorio e indice
            splitDiretorio(indiceNovo);
            arq.seek(this.vetor[posBucketCarregado]);//
            Bucket indiceVazio = new Bucket(indicVazio.getProfundidadeLocal()+1,0,Nentradas);
            arq.write(indiceVazio.toByteArray());
            // OLhar isso aqui Amanha
            //reInsereBucket(cpf, endereco, indicVazio, indicVazio.getRegistros_Ativos());
            reInsereBucket(cpf, endereco, indiceVazio, 0);
        }
        if(parada < indiceCarregado.getRegistros_Ativos()){
            //int indyce = indiceCarregado.getRegistros_Ativos() - parada ;
            reInsereBucket(indiceCarregado.getCpf(parada), indiceCarregado.getEndereco(parada), indiceCarregado, ++parada);
        }

        arq.close();
    }*/

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
            arq.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public int novoIndice(int posBucketCarregado, int profundidade){
        return posBucketCarregado + (int)Math.pow(2,profundidade);
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

    public void imprimeIndice(){
        long pos=0;
        Bucket bucket = new Bucket(0,0,Nentradas);
        try{
            RandomAccessFile arq = new RandomAccessFile("indice.db", "r");
            do{
                arq.seek(pos);
                arq.read(buffer);
                bucket.fromByteArray(buffer); // Carregar bucket em memoria
                System.out.println(bucket.toString());
                pos += tamanho_total_bucket;            
            }while(pos<=ultimoEndereco);
            arq.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }


    @Override
    public String toString() {
        return "Diretorio{" +
                "profundidadeGlobal: " + profundidadeGlobal +
                "\n vetor: " + Arrays.toString(vetor) +
                "\n Nentradas: " + Nentradas +
                "\n tamanho_total_bucket:  " + tamanho_total_bucket +
                "\n ultimoEndereço:  " + ultimoEndereco +
                "\n indice em Memória: " + indice.toString() +
                '}';
    }
}

/*
public void setNewValue(int cpfRecebido, int pos, char lapide)throws Exception{
    RandomAccessFile raf = new RandomAccessFile("indice.db", "rw");
    long posIndice = Diretorio.vetor[Diretorio.hashFunction(cpfRecebido)];
    raf.seek(posIndice);
    long pos1 = raf.getFilePointer();
    int profLocal = raf.readInt();
    int qtd = raf.readInt();
    if(qtd < max_itens_bucket){//atualiza o valor de registros
        raf.seek(pos1+4);
        raf.writeInt(++qtd);
        for(int x = 0; x < max_itens_bucket; x++){
            long pos2 = raf.getFilePointer();
            if(raf.readChar() == '?'){
                raf.seek(pos2);
                raf.writeChar(lapide);
                raf.writeInt(cpfRecebido);
                raf.writeInt(pos);
                x = max_itens_bucket;
                raf.close();
            }else{
                raf.seek(pos2+10);
            }
        }
    }else{//caso ocorra uma colisao
        raf.close();
        Bucket b1 = new Bucket();
        b1.read(cpfRecebido);//salvar o bucket cheio na memoria
        Bucket b2 = new Bucket();
        b2.profundidadeLocal = b1.profundidadeLocal+1;
        b2.reWriteBucket(cpfRecebido);
        if(b1.profundidadeLocal == Diretorio.profundidadeGlobal){//necessario expandir diretorio
            atualizarProfundidadeGlobal(cpfRecebido);
            aumentarBucketsIndice();
            reHash(b1, cpfRecebido, pos);
        }else{//somente atualizar e dar reHash no valores
            colisaoLocalMenorGlobal(cpfRecebido);
            aumentarBucketsIndice();
            reHash(b1, cpfRecebido, pos);
        }
    }
}

private void colisaoLocalMenorGlobal(int cpfColapsado) throws Exception{
    Diretorio.vetor[Diretorio.hashFunction(cpfColapsado)] = (Bucket.tamanho_cabecalho_indice+(Bucket.totalBucketCriados*Bucket.tamanho_total_bucket_bytes));
    RandomAccessFile raf = new RandomAccessFile("diretorio.db", "rw");
    raf.seek(1);//anda o byte q guarda o valor da prof. global para reescrever o vetor
    for(int x = 0; x < (int)(Math.pow(2, (Diretorio.profundidadeGlobal))); x++){
        raf.writeLong(Diretorio.vetor[x]);
    }
    raf.close();
}

private void atualizarProfundidadeGlobal(int cpfColapsado) throws Exception{
    Diretorio.profundidadeGlobal++;
    RandomAccessFile raf = new RandomAccessFile("diretorio.db", "rw");
    raf.writeByte(Diretorio.profundidadeGlobal);
    long[]vet = Diretorio.cloneVet();
    Diretorio.vetor = new long[(int)Math.pow(2, Diretorio.profundidadeGlobal)];
    for(int x = 0; x < Diretorio.vetor.length/2; x++){
        Diretorio.vetor[x] = vet[x];
    }
    for(int x = Diretorio.vetor.length/2, y = 0; x < Diretorio.vetor.length; x++, y++){
        Diretorio.vetor[x] = vet[y];
    }
    int parte1 = (int)(cpfColapsado%Math.pow(2, (Diretorio.profundidadeGlobal-1)));
    int parte2 = (int)(Math.pow(2, (Diretorio.profundidadeGlobal-1)));
    Diretorio.vetor[parte1+parte2] = (Bucket.tamanho_cabecalho_indice+(Bucket.totalBucketCriados*Bucket.tamanho_total_bucket_bytes));//colocar a posicao do novo bucket no vetor do diretorio
    raf.seek(1);//anda o byte q guarda o valor da prof. global para reescrever o vetor
    for(int x = 0; x < (int)(Math.pow(2, (Diretorio.profundidadeGlobal))); x++){
        raf.writeLong(Diretorio.vetor[x]);
    }
    raf.close();
}

private void aumentarBucketsIndice()throws Exception{
    RandomAccessFile raf = new RandomAccessFile("indice.db", "rw");
    raf.writeInt(++Bucket.totalBucketCriados);//atualiza o cabeçalho
    raf.close();
    Bucket b2 = new Bucket();
    b2.writeBucket(Bucket.totalBucketCriados-1);
}

private void reHash(Bucket backupBucket, int cpfColisao, int posNoArquivoMestre)throws Exception{
    for(int x = 0; x <= Bucket.max_itens_bucket; x++){
        if(x != Bucket.max_itens_bucket)
            setNewValue(backupBucket.cpf[x], backupBucket.posicao[x], backupBucket.lapide[x]);
        else
        setNewValue(cpfColisao, posNoArquivoMestre, ' ');
    }
}*/