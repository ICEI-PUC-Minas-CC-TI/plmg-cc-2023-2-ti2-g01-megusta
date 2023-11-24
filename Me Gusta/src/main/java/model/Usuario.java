package model;

import java.util.UUID;

public class Usuario {
    private UUID id;
    private String nome;
    private String sobrenome;
    private String usuario;
    private String senha;
    private String email;
    private char genero;
    private int idade;
    private char permissao;
    private String[] condicaoSaude; 
    private byte[] profilePic; 
    
	public Usuario() {
		super();
	}

    public Usuario(String nome, String sobrenome, String usuario, String senha, String email, 
    char genero, int idade) {
        setId(); 
        setNome(nome);
        setSobrenome(sobrenome);
        setUsuario(usuario);
        setSenha(senha);
        setEmail(email);
        setGenero(genero);
        setIdade(idade);
        setPermissao('N');
    }
    
    public Usuario(String nome, String sobrenome, String usuario, String senha, String email, 
    char genero, int idade, char permissao) {
        setId(); 
        setNome(nome);
        setSobrenome(sobrenome);
        setUsuario(usuario);
        setSenha(senha);
        setEmail(email);
        setGenero(genero);
        setIdade(idade);
        setPermissao(permissao);
    }
    
    public Usuario(UUID id, String nome, String sobrenome, String usuario, String senha, String email, 
    char genero, int idade, char permissao) {
        setId(id); // Já possui um id, então chama setId diretamente
        setNome(nome);
        setSobrenome(sobrenome);
        setUsuario(usuario);
        setSenha(senha);
        setEmail(email);
        setGenero(genero);
        setIdade(idade);
        setPermissao(permissao);
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

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGenero(char genero){
        this.genero = genero;
    }

    public void setIdade(int idade){
        this.idade = idade;
    }

    public void setCondicaoSaude(String[] condicaoSaude){
        this.condicaoSaude = condicaoSaude;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }
    
    public void setPermissao(char permissao) {
    	this.permissao = permissao;
    }

    /* GETTERS Métodos que retornam o valor dos atributos de um objeto. */

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }
    
    public String getUsuario() {
        return usuario;
    }
    
    public String getSenha() {
        return senha;
    }
    
    public String getEmail() {
        return email;
    }
    
    public char getGenero() {
        return genero;
    }
    
    public int getIdade() {
        return idade;
    }
    
    public String[] getCondicaoSaude() {
        return condicaoSaude;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }
    
    public char getPermissao() {
        return permissao;
    }

    private UUID geradorUUID() {
        return UUID.randomUUID();
    }
}