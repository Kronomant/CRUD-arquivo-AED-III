package bussines;
import java.io.*;
import java.util.Scanner;

public class ArquivoMestre {
    private int quantidadeRegistros = 0;
    private int ultimaPos = 17;
    private int ultimoCPF = 0;
    private int tamRegistro;
    private static final String FILEPATH = "prontuario.db";
    private final Diretorio dir = new Diretorio();

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

    public void escreverArqMestre(Registro obj ){
        //obj.setCpf(++ultimoCPF);
        try {
            RandomAccessFile arq = new RandomAccessFile(FILEPATH, "rw");
            //dir.inserirIndice(ultimoCPF, ultimaPos); //insere no indice
            dir.inserirIndice(obj.getCpf(), ultimaPos); // transformar inserirIndice em boolean pra impedir inserção
            // imprimir diretorio
            System.out.println(dir.toString());

            arq.seek(ultimaPos); //337 -> indice com cpf

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

    public void lerArqMestre(){
        int nbytes = 0;
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

    /*
    * receber cpf como parametro, esse cpf vai ir para a função do indice que retorna o endereço,
    * se esse cpf não estiver cadastrado tratar.
    * */
    public void atualizarProntuario(int cpf) {
        Scanner sc = new Scanner(System.in);
        String entrada = "";
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
    }

    public void excluirUsuario(int cpf)throws Exception{
        Registro obj = new Registro();
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

    public static void main(String[] args){
    }
}

