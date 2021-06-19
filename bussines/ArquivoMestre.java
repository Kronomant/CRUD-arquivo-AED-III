package bussines;
import java.io.*;
import java.util.Scanner;
import java.util.Random;


    /**
     * Classe Bucket: Responsável por armazenar em seu cabecalho a quantidade de registros,
     * a ultima posicao que foi inserido o registro, o ultimo cpf inserido e o tamanho do registro 
     */

public class ArquivoMestre {
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
            arq.seek(endereco);
            arq.writeChar('*');
            arq.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}

