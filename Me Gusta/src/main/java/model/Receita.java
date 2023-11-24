package model;

import java.util.UUID;

import org.json.simple.JSONObject;

public class Receita {
    private UUID id;
    private UUID usuario_id;
    private String titulo;
    private String descricao;
    private String[] instrucao;
    private int tempoPreparo;
    private String porcao;
    private String dificuldade;
    private double custo;
    private JSONObject nutritionalinfo;
    private byte[] imagem;
    private double avaliacao;
    
    public Receita(UUID usuario_id, String titulo, String descricao, String[] instrucao, int tempoPreparo, String porcao, 
    		String dificuldade, double custo, JSONObject nutritionalinfo, byte[] imagem) {
        setId();
        setUserId(usuario_id);
        setTitulo(titulo);
        setDescricao(descricao);
        setInstrucao(instrucao);
        setTempoPreparo(tempoPreparo);
        setPorcao(porcao);
        setDificuldade(dificuldade);
        setCusto(custo);
        setNutritionalInfo(nutritionalinfo);
        setImagem(imagem);
        setAvaliacao(0);
    }
    
    public Receita(UUID id, UUID usuario_id, String titulo, String descricao, String[] instrucao, int tempoPreparo, String porcao, 
    		String dificuldade, double custo, JSONObject nutritionalinfo, byte[] imagem) {
        setId(id); // Já possui um id, então chama setId diretamente
        setUserId(usuario_id);
        setTitulo(titulo);
        setDescricao(descricao);
        setInstrucao(instrucao);
        setTempoPreparo(tempoPreparo);
        setPorcao(porcao);
        setDificuldade(dificuldade);
        setCusto(custo);
        setNutritionalInfo(nutritionalinfo);
        setImagem(imagem);
    }

    public Receita(UUID id, UUID usuario_id, String titulo, String descricao, String[] instrucao, int tempoPreparo, String porcao, 
    		String dificuldade, double custo, JSONObject nutritionalinfo, byte[] imagem, double avaliacao) {
        setId(id); // Já possui um id, então chama setId diretamente
        setUserId(usuario_id);
        setTitulo(titulo);
        setDescricao(descricao);
        setInstrucao(instrucao);
        setTempoPreparo(tempoPreparo);
        setPorcao(porcao);
        setDificuldade(dificuldade);
        setCusto(custo);
        setNutritionalInfo(nutritionalinfo);
        setImagem(imagem);
        setAvaliacao(avaliacao);
    }

    /* SETTERS Métodos que definem o valor dos atributos de um objeto. */

    public void setId() {
        this.id = geradorUUID();
    }

    public void setId(UUID id) {
        if (id == null) {
            setId(); // Chama setId padrão se id for nulo ou vazio
        } else {
            this.id = id;
        }
    }
    
    public void setUserId(UUID usuario_id) {
            this.usuario_id = usuario_id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setInstrucao(String[] instrucao) {
        this.instrucao = instrucao;
    }
    
    public void setTempoPreparo(int tempoPreparo) {
        this.tempoPreparo = tempoPreparo;
    }
    
    public void setPorcao(String porcao) {
        this.porcao = porcao;
    }
    
    public void setDificuldade(String dificuldade) {
        this.dificuldade = dificuldade;
    }
    
    public void setCusto(double custo) {
        this.custo = custo;
    }
    
    public void setNutritionalInfo(JSONObject nutritionalinfo) {
        this.nutritionalinfo = nutritionalinfo;
    }
    
    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public void setAvaliacao(double avaliacao) {
        this.avaliacao = avaliacao;
    }
    
    /* GETTERS Métodos que retornam o valor dos atributos de um objeto. */

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return usuario_id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String[] getInstrucao() {
        return instrucao;
    }

    public int getTempoPreparo() {
        return tempoPreparo;
    }

    public String getPorcao() {
        return porcao;
    }

    public String getDificuldade() {
        return dificuldade;
    }

    public double getCusto() {
        return custo;
    }

    public JSONObject getNutritionalinfo() {
        return nutritionalinfo;
    }

    public byte[] getImagem() {
        return imagem;
    }
    
    public double getAvaliacao() {
        return avaliacao;
    }

    private UUID geradorUUID() {
        return UUID.randomUUID();
    }
}