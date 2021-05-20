package bussines;

import java.io.*;

public class Registro {
    private int cpf;
    private String nome;
    private String nascimento;
    private int idade;
    private char sexo;
    private String anotacoes;
    private char lapide;


    public Registro(int cpf, String n, int i, String nas, char s, String a, char l) {
        this.cpf = cpf;
        this.nome = n;
        this.nascimento = nas;
        this.idade = i;
        this.sexo = s;
        this.anotacoes = a;
        this.lapide = l;
    }

    public Registro(String nome, int i, String nas, char s) {
        this.cpf = 0;

        if(nome.length() > 60)
            this.nome = nome.substring(0,59);
        else
            this.nome = nome;

        if(nas.length() > 10)
            this.nascimento = nas.substring(0,9);
        else
            this.nascimento = nas;

        this.idade = i;
        this.sexo = s;
        this.anotacoes = " ";
        this.lapide = ' ';
    }

    public Registro() {
        this.cpf = 0;
        this.nome = "";
        this.nascimento = "";
        this.idade = 0;
        this.sexo = ' ';
        this.anotacoes = "";
        this.lapide = ' ';
    }

    public char getLapide() {
        return lapide;
    }

    public void setCpf(int cpf){
        this.cpf = cpf;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public void setSexo(char sexo) {
        this.sexo = sexo;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }

    public void setNascimento(String nascimento) {
        this.nascimento = nascimento;
    }

    public String toString() {

        return "\nCPF: " + this.cpf +
                "\nNome: " + this.nome +
                "\nData de Nascimento.: " + this.nascimento +
                "\nIdade: " + this.idade +
                "\nSexo: " + this.sexo +
                "\nAnotações: " + this.anotacoes;
    }
    /*
    * Serializar objeto para vetor de bytes
    * */
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

    /*
    * Deserializar vetor de bytes, transformando em um objeto
    * */
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
}
