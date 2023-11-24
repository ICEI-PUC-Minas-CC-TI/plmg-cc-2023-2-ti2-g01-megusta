package model;

import java.util.UUID;

public class Restricao {
    private UUID id;
    private String nome;
    private String tipo;

    public Restricao(String nome, String tipo) {
        setId(); 
        setNome(nome);
        setTipo(tipo);
    }
    
    public Restricao(UUID id, String nome, String tipo) {
        setId(id); // Já possui um id, então chama setId diretamente
        setNome(nome);
        setTipo(tipo);
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

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /* GETTERS Métodos que retornam o valor dos atributos de um objeto. */

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    private UUID geradorUUID() {
        return UUID.randomUUID();
    }
}