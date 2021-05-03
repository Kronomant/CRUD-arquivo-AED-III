import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;

public class ArquivoMestre {
  private int idProntuario;
  private String nome;
  private String nascimento;
  private char sexo;
  private String anotacoes;

  public ArquivoMestre(int i, String n, String nas, char s, String a) {
    this.idProntuario = i;
    this.nome = n;
    this.nascimento = nas;
    this.sexo = s;
    this.anotacoes = a;
  }

  public ArquivoMestre() {
    this.idProntuario = 0;
    this.nome = "";
    this.nascimento = "";
    this.sexo = ' ';
    this.anotacoes = "";
  }

  public String toString() {
    
    return "\nID....: " + this.idProntuario + 
           "\nNome: " + this.nome + 
           "\nData de Nascimento.: " + this.nascimento + 
           "\nSexo: " + this.sexo +
           "\nAnotações: " + this.anotacoes;
  }

    private byte[] toByteArray() throws IOException {
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       DataOutputStream dos = new DataOutputStream(baos);
       dos.writeInt(idProntuario);
       dos.writeUTF(nome);
       dos.writeUTF(nascimento);
       dos.writeChar(sexo);
       dos.writeUTF(anotacoes);
       return baos.toByteArray();
  }

    private void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        idProntuario = dis.readInt();
        nome = dis.readUTF();
        nascimento = dis.readUTF();
        sexo = dis.readChar();
        anotacoes = dis.readUTF();
  }

    public static void escreverArqMestre(ArquivoMestre obj){
        FileOutputStream arq;
        DataOutputStream dos;
        byte ba[];


        try {
            arq = new FileOutputStream("prontuarios.db", true);
            dos = new DataOutputStream(arq);

            ba = obj.toByteArray();
            dos.writeInt(ba.length);
            dos.write(ba); // escrever no arquivo mestre

            arq.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }

    public static ArquivoMestre lerArqMestre(){
        ArquivoMestre temp  = new ArquivoMestre();
        ArquivoMestre temp2  = new ArquivoMestre();
        FileInputStream arq;
        DataInputStream dis;
        byte ba[];
        int tam = 0;

        try {
            arq = new FileInputStream("prontuarios.db");
            dis = new DataInputStream(arq);

            tam = dis.readInt();
            ba = new byte[100];
            dis.read(ba);
            temp.fromByteArray(ba);
            System.out.print("Primeira leitura \n"+temp);
             dis.read(ba);
            temp2.fromByteArray(ba);
            /*System.out.print("Segunda leitura \n"+temp2); */
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print("Terceira leitura \n"+temp);

        return temp;
    }


    public static void main(String[] args){


      ArquivoMestre anotacao1 = new ArquivoMestre(1, "Maurício", "24/05/1988", 'M',"dsjdhsahkdjsad");
      ArquivoMestre anotacao2 = new ArquivoMestre(2, "Luana", "24/05/1994", 'F',"jskkjaksajsakansa");
      ArquivoMestre anotacao3 = new ArquivoMestre();

      escreverArqMestre(anotacao1);
      escreverArqMestre(anotacao2);
      anotacao3 = lerArqMestre();
      /*anotacao3 = lerArqMestre();
      System.out.print(anotacao3); */

    }


}