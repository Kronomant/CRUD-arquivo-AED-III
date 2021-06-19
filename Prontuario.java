import java.util.Scanner;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.Random;

    /**
     * Classe Bucket: Responsável por armazenar em seu cabeçalho a profundidade local, a quantidade 
     * de restivos ativos em cada bucket, um array de lápides (indica a exclusão de um registro), um
     * array de CPFs, um array de endereços e a quantidade de entradas. Cada atributo da classe possui
     * seus respectivos métodos Get's e Set's
     */
class Bucket implements Cloneable {
    private int profundidadeLocal;
    private int registros_Ativos;
    private char[] lapide;
    private int[] cpf;
    private int[] endereco;
    private int Nentradas;

    public char getLapide(int pos) {
        return lapide[pos];
    }

    public char[] getVetLapide() {
        return lapide;
    }

    public int getCpf(int pos) {
        return cpf[pos];
    }

    public int[] getVetCpf() {
        return cpf;
    }

    public int getEndereco(int pos) {
        return endereco[pos];
    }

    public int[] getVetEndereco() {
        return endereco;
    }

    public int getProfundidadeLocal() {
        return profundidadeLocal;
    }

    public int getRegistros_Ativos() {
        return registros_Ativos;
    }

    public void setLapide(char lapide, int pos) {
        this.lapide[pos] = lapide;
    }

    public void setCpf(int cpf, int pos) {
        this.cpf[pos] = cpf;
    }

    public void setEndereco(int endereco, int pos) {
        this.endereco[pos] = endereco;
    }

    public void setProfundidadeLocal(int profundidadeLocal) {
        this.profundidadeLocal = profundidadeLocal;
    }

    public void setRegistros_Ativos(int registros_Ativos) {
        this.registros_Ativos = registros_Ativos;
    }

    /** 
     * Construtores da classe: Foram criados três construtores para auxiliar durante a 
     * implementação da solução. O primeiro construtor recebe por parâmetro apenas a 
     * quantidade de entradas. O segundo além da quantidade de entradas recebe a profundidade
     * local e a quantidade de registros ativos. Já o terceiro construtor recebe um próprio 
     * bucket como parâmetro para a criação de outro
     */
    public Bucket(int Nentradas){
        this.profundidadeLocal = 0;
        this.registros_Ativos = 0;
        this.lapide = new char[Nentradas];
        this.cpf = new int[Nentradas];
        this.endereco = new int[Nentradas];
        this.Nentradas = Nentradas;
    }

    protected Bucket(int profundidade, int Nregistros, int Nentradas){
        this.profundidadeLocal = profundidade;
        this.registros_Ativos = Nregistros;
        this.lapide = new char[Nentradas];
        this.cpf = new int[Nentradas];
        this.endereco = new int[Nentradas];
        this.Nentradas = Nentradas;
    }

    public Bucket(Bucket bucket){
        this.profundidadeLocal = bucket.getProfundidadeLocal();
        this.registros_Ativos = bucket.getRegistros_Ativos();
        this.lapide = bucket.getVetLapide();
        this.cpf = bucket.getVetCpf();
        this.endereco = bucket.getVetEndereco();
        
    }

    
    /**
     * Função responsável por serializar um objeto e transformá-lo
     * em um vetor de bytes
     * @return
     * @throws IOException
     */
    protected byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(profundidadeLocal);
        dos.writeInt(registros_Ativos);
        for(int i= 0; i < Nentradas ; i++){
            dos.writeChar(lapide[i]);
            dos.writeInt(cpf[i]);
            dos.writeInt(endereco[i]);
        }
        dos.flush();
        return baos.toByteArray();
    }

    /**
     * Função responsável por deserializar um vetor de bytes e transformá-lo
     * em um objeto
     * @param ba
     * @return
     * @throws IOException
     */
    protected void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        profundidadeLocal = dis.readInt();
        registros_Ativos = dis.readInt();
        for(int i = 0; i < Nentradas; i++){
            lapide[i] = dis.readChar();
            cpf[i] = dis.readInt();
            endereco[i] = dis.readInt();
        }
    }

    /**
     * Função responsável por imprimir o Bucket
     */

    @Override
    public String toString() {
        return "Bucket{" +
                "profundidadeLocal: " + profundidadeLocal +
                ", registros_Ativos: " + registros_Ativos +
                ", lapide: " + Arrays.toString(lapide) +
                ", cpf: " + Arrays.toString(cpf) +
                ", endereco: " + Arrays.toString(endereco) +
                ", Nentradas:" + Nentradas +
                '}';
    }

    /**
     * Função utilizada para clonar um Bucket caso seja necessário
     */

    @Override
    protected Bucket clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return (Bucket)super.clone();
    }

}
    /**
     * Classe Registro: Responsável por armazenar todos os dados dos prontuáios, cpf, nome, data de nascimento
     * idade, Sexo e o campo para anotações do médico
     */
class Registro {
    private int cpf;
    private String nome;
    private String nascimento;
    private int idade;
    private char sexo;
    private String anotacoes;
    private char lapide;


    /**
     * Construtor com parametros, limita o tamanho dos campos de nome e nascimento.
     * @param cpf
     * @param n
     * @param i
     * @param nas
     * @param s
     */
    public Registro(int cpf, String n, int i, String nas, char s) {
        this.cpf = cpf;
        
        if(n.length() > 60)
            this.nome = n.substring(0,59);
        else
            this.nome = n;
        if(nas.length() > 10)
            this.nascimento = nas.substring(0,9);
        else
            this.nascimento = nas;

        this.idade = i;
        this.sexo = s;
        this.anotacoes = " ";
        this.lapide = ' ';
    }

   
    /**
     * Construtor sem parametros
     */
    public Registro() {
        this.cpf = 0;
        this.nome = "";
        this.nascimento = "";
        this.idade = 0;
        this.sexo = ' ';
        this.anotacoes = "";
        this.lapide = ' ';
    }

    /**
     *  Get de valor para Lapude
     * @return
     */
    public char getLapide() {
        return lapide;
    }

    /**
     * Get de valor para CPF
     * @return
     */
    public int getCpf() { return cpf; }

    /**
     *  Set de valor para CPF
     * @param cpf
     */
    public void setCpf(int cpf){
        this.cpf = cpf;
    }

    /**
     * Set de valor para Idade
     * @param idade
     */
    public void setIdade(int idade) {
        this.idade = idade;
    }

    /**
     * Set de valor para Sexo
     * @param sexo
     */
    public void setSexo(char sexo) {
        this.sexo = sexo;
    }

    /**
     * Set de valor para Nome
     * @param nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Set de valor para Anotações
     * @param anotacoes
     */
    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }

    /**
     * Set de valor para nascimento 
     * @param nascimento
     */
    public void setNascimento(String nascimento) {
        this.nascimento = nascimento;
    }

    
    /**
     * Serializar objeto para vetor de bytes
     * @return
     * @throws IOException
     */
    protected byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeChar(lapide);
        dos.writeInt(cpf);
        dos.writeUTF(nome);
        dos.writeUTF(nascimento);
        dos.writeInt(idade);
        dos.writeChar(sexo);
        dos.writeUTF(anotacoes);
        dos.flush();
        return baos.toByteArray();
    }

    /**
     * Deserializar vetor de bytes, transformando em um objeto
     * @param ba
     * @throws IOException
     */
    protected void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        lapide = dis.readChar();
        cpf = dis.readInt();
        nome = dis.readUTF();
        nascimento = dis.readUTF();
        idade = dis.readInt();
        sexo = dis.readChar();
        anotacoes = dis.readUTF();
    }

    /**
     * Retorna String com todo o conteudo do registro
     */
    public String toString() {

        return "\nCPF: " + this.cpf +
                "\nNome: " + this.nome +
                "\nData de Nascimento.: " + this.nascimento +
                "\nIdade: " + this.idade +
                "\nSexo: " + this.sexo +
                "\nAnotações: " + this.anotacoes;
    }
}

    /**
     * Classe Diretório: Responsável por gerencar os arquivos diretorio.db e indice.db, inserindo cpfs sempre
     * que um registro novo é criado no arquivo prontuarui.db. Realiza a expansão do indice e do diretório
     * quando necessário
     */
class Diretorio {
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
            //sc.close();
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
        return -1;
    }

    public int excluirIndice (int cpf) throws Exception{
        RandomAccessFile arq = new RandomAccessFile("indice.db", "rw");
       
        arq.seek(this.vetor[hash(cpf, profundidadeGlobal)]);
        arq.read(buffer);
        indice.fromByteArray(buffer); // Carregar bucket em memoria
        int status = 0;
        for(int i =0; i < Nentradas; i++){
            if(indice.getCpf(i) == cpf){
                if(indice.getLapide(i) == ' '){
                    indice.setLapide('*', i);
                    int registrosAtivos = indice.getRegistros_Ativos() -1;
                    indice.setRegistros_Ativos(registrosAtivos);
                    arq.seek(this.vetor[hash(cpf, profundidadeGlobal)]);
                    arq.write(indice.toByteArray());
                    status = -1;
                }else{
                    status = -2;
                }
            }
        }
        arq.close();
        return status;
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

    /**
     * Classe Arquivo Mestre : Responsável por criar e gerenciar o arquivo prontuario.db que contem todos os 
     * registros. Cria, Le, atualiza e Excluí registros do arquivo. Contem os métodos para a simulação de 
     * inserções massivas.
     * 
     */
class ArquivoMestre {
    private int quantidadeRegistros = 0;
    private int ultimaPos = 17;
    private int ultimoCPF = 0;
    private int tamRegistro;
    private static final String FILEPATH = "prontuario.db";
    private final Diretorio dir = new Diretorio();


    /**
     * Nesse construtor a primeira tentativa feita é a de recuperar o arquivo prontuario, caso ele exista.
     * Do contrário é solicitado que o usuário informe o tamanho para o campo de anotações que ele deseja 
     * para que seja feita a criação do arquivo. 
     * @throws Exception
     */
    public ArquivoMestre() throws Exception {
        try {
            RandomAccessFile arq = new RandomAccessFile(FILEPATH, "r");
            arq.seek(0);
            this.quantidadeRegistros = arq.readInt();
            this.ultimaPos = arq.readInt();
            this.ultimoCPF = arq.readInt();
            this.tamRegistro = arq.readInt();

        }catch(FileNotFoundException ffe){
            Scanner sc = new Scanner(System.in);
            System.out.println("Digite o tamanho do campo de anotações: ");
            this.tamRegistro =  82 + sc.nextInt();
            RandomAccessFile arq = new RandomAccessFile(FILEPATH, "rw");
            arq.seek(0);
            arq.writeInt(0); // Quantidade de registros
            arq.writeInt(17); // ultima posição
            arq.writeInt(0); // ultimo CPF utilizado
            arq.writeInt(tamRegistro); // tamanho do registro (atributos + M)
            System.out.println("Um novo arquivo foi criado, agora você pode realizar a seguintes operações\n");
        }
    }
    
    private int getUltimoCPF(){
        return this.ultimoCPF;
    }

    /**
     * Método responsável pela escrita do registro realizada no arquivo mestre
     * na posição do último registro inserido. A quantidade de registros ativos é 
     * atualizada e tanto a última posição preenchida quanto o último cpf
     * que contém nela são escritos no arquivo
     * @param obj
     */
    public void escreverArqMestre(Registro obj ){
        try {
            RandomAccessFile arq = new RandomAccessFile(FILEPATH, "rw");
            dir.inserirIndice(obj.getCpf(), ultimaPos, dir.getProfundidadeGlobal());// transformar inserirIndice em boolean pra impedir inserção

            if(this.ultimaPos < 0){
                System.out.println("ERRO");
            }

            arq.seek(this.ultimaPos); //337 -> indice com cpf

            arq.write(obj.toByteArray());
            this.ultimaPos = ultimaPos + tamRegistro;
            arq.seek(0);

            arq.writeInt(++this.quantidadeRegistros); // Quantidade de registros
            arq.writeInt(this.ultimaPos); // ultima posição
            arq.writeInt(this.ultimoCPF); // ultimo CPF utilizado
            arq.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método responsável por realizar a opção 8 (Simulação)
     * Recebe como parâmetro o número de inserções que serão relizadas e possui um registro
     * fictício que será escrito no arquivo durante a simulação.
     * Nesse método também é calculado o tempo gasto para realizar todas as inserções e 
     * para realizar a busca de todos os cpf's
     * @param numeroInsercoes
     */
    public void simulacao( int numeroInsercoes){
        Random gerador = new Random();
        int[] vetorCPFs = new int[numeroInsercoes];
        Registro dados = new Registro(0,"teste",200, "23/04/1800", 'M');
        long tempoInicio =  System.nanoTime();
        for (int i = 0; i < numeroInsercoes; i++) {
            vetorCPFs[i] = gerador.nextInt(10000000);
            dados.setCpf(vetorCPFs[i]);
            escreverArqMestre(dados);
         }
         System.out.println("Profundidade Global Final: "+dir.getProfundidadeGlobal()+" Tempo inserção de "+numeroInsercoes+" registros: "+(System.nanoTime()-tempoInicio));
         
         tempoInicio = System.nanoTime();
         for (int i = 0; i < numeroInsercoes; i++) {       
            simulacaoBusca(vetorCPFs[i]);
         }
         System.out.println("Tempo busca de "+numeroInsercoes+" registros: "+(System.nanoTime()-tempoInicio));

    }

    /**
     * Nessa função todos os registros que são inseridos no arquivo mestre 
     * são lidos. Para isso é necessário saltar as informações contidas no cabeçalho 
     * e iniciar a leitura. Além disso, registros marcados como excluídos não podem
     * estar inclusos nessa leitura.
     */
    public void lerArqMestre(){
        Registro obj = new Registro();
        byte[] buffer = new byte[this.tamRegistro]; // criar vetor do tamanho dos registros
        try {
            RandomAccessFile arq = new RandomAccessFile(FILEPATH, "r");
            arq.seek(17);//posição apos o cabeçalho
            for(int i =0; i<this.quantidadeRegistros; i++){
                arq.read(buffer);
                obj.fromByteArray(buffer);
                if(obj.getLapide()!='*') {
                    System.out.println(obj);
                }
            }
            arq.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Esse método é responsável pela leitura dos 3 arquivos 
     * (Diretorio, Indice e Arquivo Mestre)
     */
    public void imprimeTodosArquivos(){
        System.out.println("Diretorio: \n");
        System.out.println(dir.toString());
        System.out.println("\nIndice: \n");
        dir.imprimeIndice();
        lerArqMestre();

    }

    /**
     * Método responsável por imprimir o arquivo Indice
     */
    public void imprimirIndice(){
        dir.imprimeIndice();
    }

    /**
     * Método responsável por imprimir o arquivo Diretorio
     */
    public void imprimirDiretorio(){
        System.out.println(dir.toString()); 
    }

    /**
     * Método responsável pela leitura do registro inserido no Bucket 
     * na posção passada por parâmetro
     * @param file
     * @param position
     * @return
     */
    private Registro read(RandomAccessFile file, int position){
        Registro obj = new Registro();
        byte[] buffer = new byte[this.tamRegistro]; // criar vetor do tamanho dos registros
        try{
            file.seek(position);
            file.read(buffer);
            obj.fromByteArray(buffer);
        }catch (Exception e){
            e.printStackTrace();
        }
        return  obj;
    }

    /**
     * Esse método recebe um cpf por parâmetro para realizar a sua busca.
     * Primeiro é encontrado o seu endereco no arquivo indice e após isso 
     * suas informações são recuperadas e impressas na tela
     * @param cpf
     */
    public void buscarRegistroUnico (int cpf){
        try{
            RandomAccessFile arq = new RandomAccessFile(FILEPATH, "r");
            int endereco = dir.lerIndice(cpf); // busca endereço no indice
            new Registro();
            Registro obj;
            obj = read(arq, endereco);
            System.out.println(obj);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Esse método é semelhante ao anterior e sua única distinção é o fato
     * de que ele não imprime o registro na tela. É utilizado durante a 
     * opção 8 de Simulação
     * @param cpf
     */
    public void simulacaoBusca (int cpf){
        try{
            RandomAccessFile arq = new RandomAccessFile(FILEPATH, "r");
            int endereco = dir.lerIndice(cpf); // busca endereço no indice
            new Registro();
            Registro obj;
            obj = read(arq, endereco);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Esse método é utilizado para atualizar um registro específico recebido por
     * parâmetro. Seu endereco no arquivo indece é encontrado. É perguntado ao 
     * usuário qual informação deseja alterar no prontuário. A informação escolhida é alterada
     * e enquanto o usuário não sinalizar que deseja sair da repetição, lhe é disponibilizado
     * um menu para escolher se deseja realizar mais alguma alteração. Ao final o registro é 
     * reeescrito com as modificações
     * @param cpf
     */
    public void atualizarProntuario(int cpf) {
        Scanner sc = new Scanner(System.in);
        Registro obj = new Registro();
        int opcao = 0;
        try {
            RandomAccessFile arq = new RandomAccessFile(FILEPATH, "rw");
            int endereco = dir.lerIndice(cpf); //retornaEndereço(cpf) // função do indice que ao passar cpf por parametro retorna endereço
            obj = read(arq, endereco);
            do {
                System.out.println("Qual atributo deseja alterar \n [1] cpf \n[2] idade \n[3] nome \n[4] Data de nascimento \n[5] sexo \n[6] Anotações: \n[7]sair");
                opcao = sc.nextInt();
                sc.nextLine(); // limpar Scanner
                switch (opcao) {
                    case 1:
                        System.out.println("Novo CPF: ");
                        obj.setCpf(sc.nextInt());       // altera cpf
                        break;
                    case 2:
                        System.out.println("Nova idade: ");
                        obj.setIdade(sc.nextInt());
                        break;
                    case 3:
                        System.out.println("Novo nome: ");
                        obj.setNome(sc.nextLine());
                        break;
                    case 4:
                        System.out.println("Nova data de nascimento: ");
                        obj.setNascimento(sc.nextLine());
                        break;
                    case 5:
                        System.out.println("Novo sexo: ");
                        obj.setSexo(sc.next().charAt(0));
                        break;
                    case 6:
                        System.out.println("Digite a anotação: ");
                        obj.setAnotacoes(sc.nextLine());
                    default:
                }
            }while (opcao != 7);
            arq.seek(endereco);             // coloca ponteiro no inicio do registro
            arq.write(obj.toByteArray());   // reescreve registro com as alterações
            arq.close();
        }catch(Exception e){
            e.printStackTrace(); // tratar quando endereço não existir
        }
        sc.close();
    }

    /**
     * Método utilizado para excluir um cpf específico.
     * Seu endereco é pesquisado no índice e sua lápide é 
     * marcada para simbolizar a sua exclusão
     * @param cpf
     * @throws Exception
     */
    public void excluirUsuario(int cpf)throws Exception{
        try{
            RandomAccessFile arq = new RandomAccessFile(FILEPATH, "rw");
            int endereco = dir.lerIndice(cpf);
            int status = dir.excluirIndice(cpf);
            if(status == -1){
                arq.seek(endereco);
                arq.writeChar('*');
                arq.close();
                System.out.println("\nRegistro Excluido com Sucesso!!!\n");
            }else if(status == -2){
                System.out.println("\nRegistro já foi Excluido\n");
            }else{
                System.out.println("\nRegistro não Existe\n");
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}

/**
 * Classe Prontuario: Classe de fronteira, responsavel por coletar dados do usuário e enviar para a classe
 * arquivo Mestre.
 */
public class Prontuario{
    public static void main(String[] args) throws Exception {
        ArquivoMestre ArqM = new ArquivoMestre();
        Scanner sc = new Scanner(System.in);
        String nome ="";
        String nascimento ="";
        char sexo = ' ';
        int idade = 0, opcao = 0, cpf=0;

        do {
            System.out.println("Menu" + "\n [1] - Adicionar Prontuário" + "\n [2] - Buscar prontuario" +
                    "\n [3] - Alterar Prontuario" + "\n [4] - Excluir Prontuario"+"\n [5] - Mostrar todos Prontuários "+
                    "\n [6] - Imprimir Diretório"+ "\n [7] - Imprimir indice"+"\n [8] - Simulação"+"\n [9] - Sair");
            opcao = sc.nextInt();
            sc.nextLine();
            switch (opcao){
                case 1:
                    System.out.println("Insira o nome do paciente: ");
                    nome = sc.nextLine();
                    System.out.println("Insira a data de nascimento do paciente: ");
                    nascimento = sc.nextLine();
                    System.out.println("Insira a idade do paciente: ");
                    idade = sc.nextInt();
                    System.out.println("Insira o cpf do paciente: ");
                    cpf = sc.nextInt();
                    System.out.println("Insira o sexo do paciente: ");
                    sexo = sc.next().charAt(0);
                    Registro dados = new Registro(cpf,nome,idade, nascimento, sexo);
                    ArqM.escreverArqMestre(dados);
                    break;
                case 2:
                    System.out.println("Digite o cpf do paciente: ");
                    ArqM.buscarRegistroUnico(sc.nextInt());
                    break;
                case 3:
                    System.out.println("Digite o cpf do paciente: ");
                    ArqM.atualizarProntuario(sc.nextInt());
                    break;
                case 4:
                    System.out.println("Digite o cpf do paciente: ");
                    ArqM.excluirUsuario(sc.nextInt());
                    break;
                case 5:
                    ArqM.lerArqMestre();
                    break;
                case 6:
                    ArqM.imprimirDiretorio();
                    break;
                case 7:
                    ArqM.imprimirIndice();
                    break;
                case 8:
                    System.out.println("Digite a quantidade de inserções a serem realizadas: ");
                    int insercoes = sc.nextInt();
                    ArqM.simulacao(insercoes);
                    break;
                default:
            }
        }while(opcao !=9);
        sc.close();

}
}

