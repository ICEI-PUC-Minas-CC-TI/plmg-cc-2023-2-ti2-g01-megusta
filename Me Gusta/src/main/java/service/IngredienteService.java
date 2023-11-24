package service;

import spark.Request;

import com.google.gson.Gson;


import spark.Response;
import spark.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;

import org.json.simple.JSONObject;
import org.postgresql.util.PSQLException;

import dao.IngredienteDAO;
import dao.RestricaoDAO;
import dao.UsuarioDAO;
import model.Ingrediente;
import model.Restricao;
import model.Usuario;

public class IngredienteService {
	private IngredienteDAO dao = new IngredienteDAO();
	private RestricaoDAO restricaoDao = new RestricaoDAO();
	private UsuarioDAO userDao = new UsuarioDAO();
	
	/* Método de inserção de ingrediente
	 * Coleta dados dos formulários e envia para DAO
	 */
	public String insereIngrediente(Request request, Response response) {
		System.out.println("Recuperando valores...");
		// Linhas que recebem os parâmetros da URL
	    String nome = request.queryParams("nome");
	    String categoria = request.queryParams("categoria");
	    // Recupera a imagem (bytea)
	    InputStream imagemStream;
	    byte[] imagem = null;
		try {
			imagemStream = request.raw().getPart("imagem").getInputStream();
			imagem = IOUtils.toByteArray(imagemStream);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
		
	    // Recupera as informações nutricionais (JSON)
	    JSONObject nutritionalInfo = new JSONObject();
	    nutritionalInfo.put("calorias", request.queryParams("infoNutricional[calorias]"));
	    nutritionalInfo.put("carboidratos", request.queryParams("infoNutricional[carboidratos]"));
	    nutritionalInfo.put("proteinas", request.queryParams("infoNutricional[proteinas]"));
	    nutritionalInfo.put("gorduraTrans", request.queryParams("infoNutricional[gorduraTrans]"));
	    nutritionalInfo.put("gorduraSaturada", request.queryParams("infoNutricional[gorduraSaturada]"));
	    nutritionalInfo.put("fibra", request.queryParams("infoNutricional[fibra]"));
	    nutritionalInfo.put("sodio", request.queryParams("infoNutricional[sodio]"));
	    
	    String[] restricoesArray = request.queryParamsValues("restrictions");
	 
	    List<UUID> restricoesList = new ArrayList<>();

	    // Verifica se há restrições
	    if (restricoesArray != null && restricoesArray.length > 0) {
	        for (String restricaoId : restricoesArray) {
	            try {
	                UUID uuid = UUID.fromString(restricaoId);
	                restricoesList.add(uuid);
	            } catch (IllegalArgumentException e) {
	                // Tratar a exceção se a string não puder ser convertida para UUID
	                System.err.println("Erro ao converter para UUID: " + e.getMessage());
	            }
	        }
	    }
	    
	    System.out.println("Atributos recuperados!");
	    String resp = "";
	    boolean status = false;
	    boolean ingredientExists = false;

	    try {
		    	// Verifica se a instrição já foi inserida
		        if(dao.verificarIngrediente(nome)) {
		        	ingredientExists = true; 
		        } else {
		        	// Em caso positivo, chama a função
		        	status = dao.inserirIngrediente(new Ingrediente(nome, categoria, nutritionalInfo, imagem), restricoesList);
		        }

		        // Verifica a resposta
		        if (status) {
		            resp = "Ingrediente (" + nome + ") inserido!";
		            response.status(201); // 201 Created
		        } else {
		            resp = "Ingrediente (" + nome + ") não inserido!";
		            response.status(404); // 404 Not found
		        }

	        System.out.printf(resp);
	    } catch (Exception e) {
	        System.err.println("Erro inesperado: " + e.getMessage());
	    }
	    
	    // Objeto JSON que será manipulado no Front-end
	    JSONObject resposta = new JSONObject();
	    resposta.put("success", status);
		resposta.put("ingredientExists", ingredientExists);

	    Gson gson = new Gson();
	    return gson.toJson(resposta); // Retorna o objeto resposta
	}
	
	/* Método de atualização de ingrediente
	 * Coleta dados dos formulários e envia para DAO
	 */
	public String alteraIngrediente(Request request, Response response) {

		// Linhas que recebem os parâmetros da URL
	    String idParam = request.queryParams("id");
		UUID id = UUID.fromString(idParam);
	    String nome = request.queryParams("nome");
	    String categoria = request.queryParams("categoria");
	    // Recupera a imagem (bytea)
	    InputStream imagemStream;
	    byte[] imagem = null;
		try {
			imagemStream = request.raw().getPart("imagem").getInputStream();
			imagem = IOUtils.toByteArray(imagemStream);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
		
	    // Recupera as informações nutricionais (JSON)
	    JSONObject nutritionalInfo = new JSONObject();
	    nutritionalInfo.put("calorias", request.queryParams("infoNutricional[calorias]"));
	    nutritionalInfo.put("carboidratos", request.queryParams("infoNutricional[carboidratos]"));
	    nutritionalInfo.put("proteinas", request.queryParams("infoNutricional[proteinas]"));
	    nutritionalInfo.put("gorduraTrans", request.queryParams("infoNutricional[gorduraTrans]"));
	    nutritionalInfo.put("gorduraSaturada", request.queryParams("infoNutricional[gorduraSaturada]"));
	    nutritionalInfo.put("fibra", request.queryParams("infoNutricional[fibra]"));
	    nutritionalInfo.put("sodio", request.queryParams("infoNutricional[sodio]"));
	    
	    String[] restricoesArray = request.queryParamsValues("restrictions");
		 
	    List<UUID> restricoesList = new ArrayList<>();

	    // Verifica se há restrições
	    if (restricoesArray != null && restricoesArray.length > 0) {
	        for (String restricaoId : restricoesArray) {
	            try {
	                UUID uuid = UUID.fromString(restricaoId);
	                restricoesList.add(uuid);
	            } catch (IllegalArgumentException e) {
	                // Tratar a exceção se a string não puder ser convertida para UUID
	                System.err.println("Erro ao converter para UUID: " + e.getMessage());
	            }
	        }
	    }
	    
	    String resp = "";
	    boolean status = false;
	    boolean ingredientExists = false;

	    try {
		    	// Verifica se a instrição já foi inserida
		        if(dao.verificarIngrediente(nome)) {
		        	ingredientExists = true; 
		        } else {
		        	// Em caso positivo, chama a função
		        	status = dao.atualizarIngrediente(new Ingrediente(id, nome, categoria, nutritionalInfo, imagem), restricoesList);
		        }

		        // Verifica a resposta
		        if (status) {
		            resp = "Ingrediente (" + nome + ") inserido!";
		            response.status(201); // 201 Created
		        } else {
		            resp = "Ingrediente (" + nome + ") não inserido!";
		            response.status(404); // 404 Not found
		        }

	        System.out.printf(resp);
	    } catch (Exception e) {
	        System.err.println("Erro inesperado: " + e.getMessage());
	    }
	    
	    // Objeto JSON que será manipulado no Front-end
	    JSONObject resposta = new JSONObject();
	    resposta.put("success", status);
		resposta.put("ingredientExists", ingredientExists);

	    Gson gson = new Gson();
	    return gson.toJson(resposta); // Retorna o objeto resposta
	}
    
	/* Método GET de ingredientes
	 * Recebe todas as linhas da tabela ingredientes no BD
	 */
	public String obterIngredientes() {
	    List<Ingrediente> ingredientes = dao.getIngredientes();
        Gson gson = new Gson();
        return gson.toJson(ingredientes);
	}
	
	/* Método GET de ingredientes 
	 * Recebe todas as linhas da tabela ingrediente no BD ordenadas alfabeticamente por nome ou categoria
	 */
    public String obterIngredientesOrdenados(String order) {
		String orderBy = order;
        System.out.println("Valor de orderBy: " + orderBy);
        
        List<Ingrediente> ingredientes = dao.getIngredientes();
        List<Ingrediente> ingredientesOrdenados = new ArrayList<>();
     
        if ("alfabetica".equals(orderBy)) {

        	ingredientesOrdenados = dao.sort(ingredientes);
        } else if ("insercao".equals(orderBy)) {

        	ingredientesOrdenados = dao.getIngredientes();
        } else if ("categoria".equals(orderBy)){
        	ingredientesOrdenados = dao.sortCategoria(ingredientes);
        }
        
        Gson gson = new Gson();
		return gson.toJson(ingredientesOrdenados);
    }
    
	/* Método GET de pesquisa com campo de texto
	 * Recebe um termo de pesquisa como argumento e pesquisa nas linhas do BD
	 */
	
    public String obterResultadosPesquisa(String termoPesquisa) {
        List<Ingrediente> ingredientes = dao.getPesquisa(termoPesquisa);
        Gson gson = new Gson();
        return gson.toJson(ingredientes);
    }
    
	/* Método GET para um ingrediente
	 * A partir de um argumento user, faz a pesquisa de um ingrediente no BD e retorna um objeto
	 */
    public String buscaIngrediente(String nome) {
    	
	    Ingrediente ingredient = dao.buscarIngrediente(nome);
	    
    	Gson gson = new Gson();
    	JSONObject resposta = new JSONObject();
    	boolean status = false;
    	if(ingredient == null) {
		    resposta.put("success", status);
    	} else {
    		status = true;
		    resposta.put("success", status);
		    resposta.put("object", ingredient);
    	}
    	
    	System.out.println("Status: " + status);
	    return gson.toJson(resposta);
    }
    
	/* Método DELETE para um ingrediente
	 * Exclui uma linha da tabela ingrediente
	 */
    public String deletarIngrediente(Request request, Response response) {
    	String ingrediente = request.queryParams("apagarIngrediente");
    	String user = request.queryParams("validSuperuser");
    	String senha = request.queryParams("confirmarSenha");
    	
    	Gson gson = new Gson();
    	JSONObject resposta = new JSONObject();
    	boolean status = false;
    	boolean passwordIncorrect = false;
    	boolean ingredientExists = dao.ingredienteExiste(ingrediente);
    	Usuario usuario = userDao.buscarUsuario(user);
    	boolean validSuperuser = false;
    	
    	if(ingredientExists) {
    		if(usuario.getPermissao() == 'S') {
    			validSuperuser = true;
            	// Permite deletar user apenas se a senha corresponder
            	if(!(userDao.verificaSenha(senha, user))){
            		passwordIncorrect = true;
            	} else {
            		status = dao.excluirIngrediente(ingrediente);
            	}
    		} 
    	}
    
    	resposta.put("success", status);
    	resposta.put("validSuperuser", validSuperuser);
    	resposta.put("ingredientExists", ingredientExists);
    	resposta.put("passwordIncorrect", passwordIncorrect);

	    return gson.toJson(resposta);
    }
    
	/* Método DELETE para todos os ingredientes
	 * Exclui todas as linhas da tabela ingrediente no bd
	 */
    public String deletarTodos(Request request, Response response) {
    	
    	String user = request.queryParams("superuser");
    	String senha = request.queryParams("senhaSuperuser");
    	Gson gson = new Gson();
    	JSONObject resposta = new JSONObject();
    	boolean status = false;
    	boolean passwordIncorrect = true;
    	boolean validSuperuser = false;
    	Usuario user1 = userDao.buscarUsuario(user);
    	
    	// Permite delete all apenas se o usuário tiver supermissões E se as senhas forem corrrespondentes
    	if(user1.getPermissao() == 'S') {
			validSuperuser = true;
			if(userDao.verificaSenha(senha, user)){
	    		passwordIncorrect = false;
	    		status = dao.apagarTodosIngredientes();
			} 
    	} 
    
    	resposta.put("success", status);
    	resposta.put("passwordIncorrect", passwordIncorrect);
    	resposta.put("validSuperuser", validSuperuser);

	    return gson.toJson(resposta);
    }
}