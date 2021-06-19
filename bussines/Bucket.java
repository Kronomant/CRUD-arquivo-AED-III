package bussines;

import java.io.*;
import java.util.Arrays;

    /**
     * Classe Bucket: Responsável por armazenar em seu cabeçalho a profundidade local, a quantidade 
     * de restivos ativos em cada bucket, um array de lápides (indica a exclusão de um registro), um
     * array de CPFs, um array de endereços e a quantidade de entradas. Cada atributo da classe possui
     * seus respectivos métodos Get's e Set's
     */

public class Bucket implements Cloneable {
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
