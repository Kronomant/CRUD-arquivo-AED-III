package bussines;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
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

    public int getProfundidadeGlobal() {
        return profundidadeGlobal;
    }

    /**
     * Construtor da classe: Assim que o programa é executado tenta carregar em memória todo o diretório,
     * Se não conseguir, cria um novo diretório em memória, com parametros de profundidade Global e tamanho de
     * cada Bucket definidos pelo usuário
     * 
     * @throws Exception
     */
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
        }catch (FileNotFoundException ffe) {
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
            sc.close();
            arq.close();
            arq2.close();
        }
    }

    /**
     * Função de Hash generica que recebe tanto cpf quanto profundidade, pois no split de buckets
     * o hash é realizado com a profundidade local e não global. 
     * @param cpf
     * @param profundidade
     * @return
     */
    public int hash(int cpf, int profundidade){
        return cpf % (int)Math.pow(2,profundidade);
    }

    /**
     * Função que atualiza o estado do diretório em disco sempre que ele sofre alguma alteração
     */
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
                arq.writeLong(vetor[i]);
            }
            arq.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    /**
     * Quando ocorre o split do diretório essa função calcula a posição no novo diretório que vai receber
     * o endereço do novo bucket
     * @param posBucketCarregado
     * @param profundidade
     * @return
     */
    public int novoIndice(int posBucketCarregado, int profundidade){
        return posBucketCarregado + (int)Math.pow(2,profundidade);
    }

    /**
     * Função recursiva que redistribui os cpf que colidiram, guarda o bucket em vetores, 
     * pois ele saí da memoria.
     * @param backupBucket
     * @param cpf
     * @param endereco
     * @throws Exception
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
                inserirIndice(backupCPFs[pos], backupEnderecos[pos], profundidadeLocal);
            }else{
                inserirIndice(cpf, endereco, profundidadeLocal);
            }
                
        }
    }

    /**
     * Função para inserir no indice, le o bucket verifica se está cheio, se não estiver insere
     * se estiver zera bucket em disco, verifica a profundidade local, se for igual a global realiza split
     * do diretorio e recursivamente re insere os cpfs, Se for menor que a global realiza somente split
     * de buckets e recursivamente re insere os cpfs.
     * @param cpf
     * @param endereco
     * @param profundidade
     */
    public void inserirIndice (int cpf, int endereco, int profundidade) {
        byte[] buffer2 = new byte[(int) this.tamanho_total_bucket];
        int posBucketCarregado = hash(cpf, profundidade); // hashFunction
        //Bucket bucket = new Bucket(Nentradas);
        try{
        RandomAccessFile arq = new RandomAccessFile("indice.db", "r");
        arq.seek(this.vetor[posBucketCarregado]);
        arq.read(buffer2);
        indice.fromByteArray(buffer2);
        arq.close();
        RandomAccessFile raf = new RandomAccessFile("indice.db", "rw");
         

        if(indice.getRegistros_Ativos() < Nentradas) {
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
            raf.seek(this.vetor[posBucketCarregado]);
            raf.write(bucketVazio.toByteArray()); // Criação novo Bucket Vazio
            if(profundidadeLocal == profundidadeGlobal){ // split Diretorio
                int indiceNovo = novoIndice(posBucketCarregado, this.profundidadeGlobal);
                splitDiretorio(indiceNovo);
                raf.seek(this.vetor[indiceNovo]);
                raf.write(bucketVazio.toByteArray()); // Zera bucket no disco
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

    /**
     * Função que realiza o split do diretório, dobrando o seu tamanho e modificando o endereço de 1 bucket
     * que o indice é passado por parametro
     * @param indicenovo
     * @throws Exception
     */
    public void splitDiretorio(int indicenovo) throws Exception{
        System.out.println("\nOcorreu colisão, profundidade Global atual: "+profundidadeGlobal);
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

    /**
     * Função que resgata o endereço de um cpf passado por parametro
     * @param cpf
     * @return
     * @throws Exception
     */
    public int lerIndice (int cpf) throws Exception{
        RandomAccessFile arq = new RandomAccessFile("indice.db", "r");
       
        arq.seek(this.vetor[hash(cpf, profundidadeGlobal)]);
        arq.read(buffer);
        indice.fromByteArray(buffer); // Carregar bucket em memoria

        if(indice.getRegistros_Ativos() != 0){
            for(int i =0; i < Nentradas; i++){
                if(indice.getCpf(i) == cpf && indice.getLapide(i) != '*'){
                    arq.close();
                    return indice.getEndereco(i);
                }
              
            }
        }
        arq.close();
        return 1;
        
        
    }

    /**
     * Função para imprimir todo o conteudo do indice, sequencia de buckets criados.
     */
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

    /**
     * Função para Imprimir estado do diretório
     */
    @Override
    public String toString() {
        return "Diretorio{" +
                "profundidadeGlobal: " + profundidadeGlobal +
                "\n vetor: " + Arrays.toString(vetor) +
                "\n Nentradas: " + Nentradas +
                "\n tamanho_total_bucket:  " + tamanho_total_bucket +
                "\n ultimoEndereço:  " + ultimoEndereco +
                '}';
    }
}