package bussines;

import java.io.*;
import java.util.Arrays;

public class Bucket {
    private int profundidadeLocal;
    private int registros_Ativos;
    private char[] lapide;
    private int[] cpf;
    private int[] endereco;
    private int Nentradas;

    public char getLapide(int pos) {
        return lapide[pos];
    }

    public int getCpf(int pos) {
        return cpf[pos];
    }

    public int getEndereco(int pos) {
        return endereco[pos];
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


    public Bucket(){
        this.profundidadeLocal = 0;
        this.registros_Ativos = 0;
    }

    protected Bucket(int profundidade, int Nregistros, int Nentradas){
        this.profundidadeLocal = profundidade;
        this.registros_Ativos = Nregistros;
        this.lapide = new char[Nentradas];
        this.cpf = new int[Nentradas];
        this.endereco = new int[Nentradas];
        this.Nentradas = Nentradas;
    }

    /*
     * Serializar objeto para vetor de bytes
     * */
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

    /*
     * Deserializar vetor de bytes, transformando em um objeto
     * */
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

    @Override
    public String toString() {
        return "Bucket{" +
                "profundidadeLocal=" + profundidadeLocal +
                ", registros_Ativos=" + registros_Ativos +
                ", lapide=" + Arrays.toString(lapide) +
                ", cpf=" + Arrays.toString(cpf) +
                ", endereco=" + Arrays.toString(endereco) +
                ", Nentradas=" + Nentradas +
                '}';
    }
}
