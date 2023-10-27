package service;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.postgresql.util.PSQLException;

import com.google.gson.Gson;

import dao.UsuarioDAO;
import model.Usuario;
import spark.Request;

public class IngredienteService {
	private IngredienteDAO dao = new IngredienteDAO();
	
	
	//coletar dados e enviar para o DAO
	public String insereIngrediente(Request request, Response response) {
		
		JSONObject nutritionalInfo = new JSONObject();
		
		String nome = request.queryParams("nome");
		String categoria = request.queryParams("category");
		Double calorie = Double.parseDouble(request.queryParams("calorie"));
		Double carboidratos = Double.parseDouble(request.queryParams("carboidratos"));
		Double proteinas = Double.parseDouble(request.queryParams("proteinas"));
		Double gorduraTrans = Double.parseDouble(request.queryParams("gorduraTrans"));
		Double gorduraSaturada = Double.parseDouble(request.queryParams("gorduraSaturada"));
		Double fibra = Double.parseDouble(request.queryParams("fibra"));
		Double sodio = Double.parseDouble(request.queryParams("sodio"));
		
		//json
		nutritionalInfo.put("calorie", calorie);
		nutritionalInfo.put("carboidratos", carboidratos);
		nutritionalInfo.put("proteinas", proteinas);
		nutritionalInfo.put("gorduraTrans", gorduraTrans);
		nutritionalInfo.put("gorduraSaturada", gorduraSaturada);
		nutritionalInfo.put("fibra", fibra);
		nutritionalInfo.put("sodio", sodio);
		
	    String resp = "";
	    boolean status = false;
	    boolean userExists = false;
	    
	    try {
	        if(!(dao.verificaIngrediente(nome))) {
	        	userExists = true;
	        } else {
	        	//vê como insere imagem dps
	        	status = dao.inserirIngrediente(new Ingrediente(nome, categoria, nutritionalInfo, imagem));
	        }

	        if (status) {
	            resp = "Ingrediente (" + nome + ") inserido!";
	            response.status(201); // 201 Created
	        } else {
	            resp = "Ingrediente (" + nome + ") não inserido!";
	            response.status(404); // 404 Not found
	        }

	        System.out.printf(resp);
	    } catch (PSQLException e) {
	        // Tratamento específico para violação de restrição de comprimento
	        if ("22001".equals(e.getSQLState())) {
	            System.err.println("Valor inválido: comprimento excede o máximo permitido.");
	        } else {
	            // Tratamento para outras exceções PSQLException
	            throw new RuntimeException(e);
	        }
	    } catch (Exception e) {
	        System.err.println("Erro inesperado: " + e.getMessage());
	    }
	    JSONObject resposta = new JSONObject();
	    resposta.put("success", status);
		resposta.put("userExists", userExists);

	    Gson gson = new Gson();
	    return gson.toJson(resposta);
	}
	
	public List<Ingrediente> obterIngredientes() {
	    IngredienteDAO ingredienteDAO = new IngredienteDAO();
	    return ingredieteDAO.getIngredientes();
	}
	
	public List<Ingrediente> obterIngredientesOrdenados() {
	    IngredienteDAO ingredienteDAO = new IngredienteDAO();
	    List<Ingrediente> ingredientes = ingredienteDAO.getIngredientes();
	    return ingredienteDAO.sort(ingredientes);
	}
	
	public List<Ingrediente> obterResultadosPesquisa(String termoPesquisa) {
	    IngredienteDAO ingredienteDAO = new IngredienteDAO();
	    return ingredienteDAO.getPesquisa(termoPesquisa);
	}
	
    public String obterIngredientesOrdenadosJSON(String order) {
		String orderBy = order;
        System.out.println("Valor de orderBy: " + orderBy);
        
        List<Ingrediente> ingredientes = new ArrayList<>();
        
        if ("alfabetica".equals(orderBy)) {

        	ingredientes = obterIngredientesOrdenados();
        } else if ("insercao".equals(orderBy)) {

        	ingrediente = obterIngredientes();
        } 
        
        Gson gson = new Gson();
		return gson.toJson(ingredientes);
    }
	
    public String obterIngredientesJSON() {
        List<Ingrediente> ingredientes = obterIngredientes();
        Gson gson = new Gson();
        return gson.toJson(ingredientes);
    }
    
    public String obterResultadosPesquisaJSON(String termoPesquisa) {
        List<Ingrediente> ingredientes = obterResultadosPesquisa(termoPesquisa);
        Gson gson = new Gson();
        return gson.toJson(ingredientes);
    }
	
}