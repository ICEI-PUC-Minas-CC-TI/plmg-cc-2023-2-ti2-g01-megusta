package model;

import java.util.UUID;

public class Receita {
	UUID id;
	String titulo;
	String desc;
	String autor;
	double porcao;
	double custo;
	String dificuldade;
	String preparo;
	double prepTime;
	String valorNut;
	
	public Receita(String titulo, String desc, String autor, double porcao, double custo, String dificuldade,
			String preparo, double prepTime, String valorNut) {
		super();
		this.id = UUID.randomUUID();
		this.titulo = titulo;
		this.desc = desc;
		this.autor = autor;
		this.porcao = porcao;
		this.custo = custo;
		this.dificuldade = dificuldade;
		this.preparo = preparo;
		this.prepTime = prepTime;
		this.valorNut = valorNut;
	}
	public Receita() {
		super();
		this.titulo = "";
		this.desc = "";
		this.autor = "";
		this.porcao = 0;
		this.custo = 0;
		this.dificuldade = "";
		this.preparo = "";
		this.prepTime = 0;
		this.valorNut = "";
	}
	public String getId() {
		return id.toString();
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getAutor() {
		return autor;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	public double getPorcao() {
		return porcao;
	}
	public void setPorcao(double porcao) {
		this.porcao = porcao;
	}
	public double getCusto() {
		return custo;
	}
	public void setCusto(double custo) {
		this.custo = custo;
	}
	public String getDificuldade() {
		return dificuldade;
	}
	public void setDificuldade(String dificuldade) {
		this.dificuldade = dificuldade;
	}
	public String getPreparo() {
		return preparo;
	}
	public void setPreparo(String preparo) {
		this.preparo = preparo;
	}
	public double getPrepTime() {
		return prepTime;
	}
	public void setPrepTime(double prepTime) {
		this.prepTime = prepTime;
	}
	public String getValorNut() {
		return valorNut;
	}
	public void setValorNut(String valorNut) {
		this.valorNut = valorNut;
	}
	
}
