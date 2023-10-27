package model;

import java.util.UUID;

public class Anotacao {
	private UUID id;
	private String message;
	private String color;
	
	//getters
	public UUID getId() {
		return id;
	}
	public String getMessage() {
		return message;
	}
	public String getColor() {
		return color;
	}
	
	//gerador de id
    private UUID geradorUUID() {
        return UUID.randomUUID();
    }
	
	//setters
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
    public void setMessage(String message) {
    	this.message = message;
    }
    public void setColor(String color) {
    	this.color = color;
    }
    
    //construtores
    public Anotacao() {
    	super();
    }
    public Anotacao(String message, String color) {
    	setId();
    	setMessage(message);
    	setColor(color);
    }
    public Anotacao (UUID id, String message, String color) {
    	setId(id);
    	setMessage(message);
    	setColor(color);
    }
}
