import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import bussines.*;

public class Prontuario{
    public static void main(String[] args) throws Exception {
        ArquivoMestre ArqM = new ArquivoMestre();
        Scanner sc = new Scanner(System.in);
        String nome ="";
        String nascimento ="";
        char sexo = ' ';
        int idade = 0, opcao = 0, cpf=0;

        do {
            System.out.println("Menu" + "\n [1] - Adicionar Prontuário" + "\n [2] - Mostrar todos Prontuários " +
                    "\n [3] - Alterar Prontuario" + "\n [4] - Excluir Prontuario"+"\n [5] - Buscar prontuario"+"\n [6] - Imprimir Indice"+"\n [7] - Sair");
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
                    //Registro dados = new Registro(nome,idade, nascimento, sexo);
                    Registro dados = new Registro(cpf,nome,idade, nascimento, sexo);
                    ArqM.escreverArqMestre(dados);

                    break;
                case 2:
                    ArqM.lerArqMestre();
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
                    System.out.println("Digite o cpf do paciente: ");
                    ArqM.buscarRegistroUnico(sc.nextInt());
                    break;
                case 6:
                    ArqM.imprimeDiretorioIndice();
                    break;
                default:
            }
        }while(opcao !=7);


    

}


}