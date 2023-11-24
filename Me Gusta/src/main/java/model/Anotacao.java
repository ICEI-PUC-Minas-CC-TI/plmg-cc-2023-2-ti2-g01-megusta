package model;

import java.util.UUID;

public class Anotacao {
	private UUID id;
	private UUID user_id;
	private String message;
	private String color;
	
    //construtores
    public Anotacao() {
    	super();
    }
    public Anotacao(String message, String color, UUID user_id) {
    	setId();
    	setMessage(message);
    	setColor(color);
    	setUserId(user_id);
    }
    public Anotacao (UUID id, String message, String color, UUID user_id) {
    	setId(id);
    	setMessage(message);
    	setColor(color);
    	setUserId(user_id);
    }
	
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
	
	public UUID getUserId() {
		return user_id;
	}
	
	//gerador de id
    private UUID geradorUUID() {
        return UUID.randomUUID();
    }
	
    /* GETTERS Métodos que retornam o valor dos atributos de um objeto. */
    
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
    
    public void setUserId(UUID user_id) {
        this.user_id = user_id;
    }
    
    public void setMessage(String message) {
    	this.message = message;
    }
    public void setColor(String color) {
    	this.color = color;
    }
}
