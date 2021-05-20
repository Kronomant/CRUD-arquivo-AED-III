package bussines;

import java.io.*;

public class Bucket {
    private int profundidadeLocal;
    private int Nregistros;
    private int[] cpf;
    private int[] endereco;

    public Bucket(){
        this.profundidadeLocal = 0;
        this.Nregistros = 0;
    }

    public Bucket(int profundidade, int Nregistros, int Nentradas){
        this.profundidadeLocal = profundidade;
        this.Nregistros = Nregistros;
        this.cpf = new int[Nentradas];
        this.endereco = new int[Nentradas];
    }

    /*
     * Serializar objeto para vetor de bytes
     * */
    protected byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(profundidadeLocal);
        dos.writeInt(Nregistros);
        for(int i= 0; i < 4 ; i++){
            dos.writeInt(cpf[i]);
            dos.writeInt(endereco[i]);
        }
        dos.flush();
        return baos.toByteArray();
    }

    /*
     * Deserializar vetor de bytes, transformando em um objeto
     * */
    protected void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        profundidadeLocal = dis.readInt();
        Nregistros = dis.readInt();
        for(int i = 0; i < 4; i++){
            cpf[i] = dis.readInt();
            endereco[i] = dis.readInt();
        }
    }
}
