package service;

import spark.Request;

import com.google.gson.Gson;

import spark.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.postgresql.util.PSQLException;

import dao.AnotacaoDAO;
import dao.UsuarioDAO;
import model.Anotacao;
import model.Usuario;

public class AnotacaoService {
	private AnotacaoDAO dao = new AnotacaoDAO();
	private UsuarioDAO userDao = new UsuarioDAO();
	
	/* Método de inserção de anotação
	 * Coleta dados dos formulários e envia para DAO
	 */
	public String insereAnotacao(Request request, Response response) {

		// Linhas que recebem os parâmetros da URL
	    String mensagem = request.queryParams("mensagem");
	    String cor = request.queryParams("cor");
	    UUID user_id = UUID.fromString(request.queryParams("user"));
	    
	    String resp = "";
	    boolean status = false;

	    try {
	       status = dao.inserirAnotacao(new Anotacao(mensagem, cor, user_id));
	        

	        // Verifica a resposta
	        if (status) {
	            resp = "Anotação inserida!";
	            response.status(201); // 201 Created
	        } else {
	            resp = "Anotação não inserida!";
	            response.status(404); // 404 Not found
	        }

	        System.out.printf(resp);
	    } catch (Exception e) {
	        System.err.println("Erro inesperado: " + e.getMessage());
	    }
	    
	    // Objeto JSON que será manipulado no Front-end
	    JSONObject resposta = new JSONObject();
	    resposta.put("success", status);

	    Gson gson = new Gson();
	    return gson.toJson(resposta); // Retorna o objeto resposta
	}
	
    
	/* Método GET de anotações
	 * Recebe todas as linhas da tabela anotação que dão match com o id do user logado
	 */
	public String obterAnotacoes(Request request, Response response) {
		
		UUID user_id = UUID.fromString(request.params(":id"));
		
	    List<Anotacao> anotacoes = dao.getAnotacoes(user_id);
        Gson gson = new Gson();
        return gson.toJson(anotacoes);
	}
	
	/* Método DELETE para uma anotação
	 * Exclui uma linha da tabela anotação
	 */
    public String deletaAnotacao(Request request, Response response) {
    	
    	UUID note_id = UUID.fromString(request.params(":note"));
    	Gson gson = new Gson();
    	JSONObject resposta = new JSONObject();
    	boolean status = false;

       status = dao.excluirAnotacao(note_id);

       resposta.put("success", status);

	    return gson.toJson(resposta);
    }
    
	/* Método DELETE para todas anotações
	 * Exclui todas as anotações de um determinado usuário
	 */
    public String deletarTodas(Request request, Response response) {
    	UUID user_id = UUID.fromString(request.queryParams("user_id"));
    	String user = request.queryParams("user");
    	String senha = request.queryParams("senha");
    	Gson gson = new Gson();
    	JSONObject resposta = new JSONObject();
    	boolean status = false;
    	boolean passwordIncorrect = true;
    	
		if(userDao.verificaSenha(senha, user)){
    		passwordIncorrect = false;
    		status = dao.apagarTodasAnotacoes(user_id);
		} 
    
    	resposta.put("success", status);
    	resposta.put("passwordIncorrect", passwordIncorrect);
 
	    return gson.toJson(resposta);
    }
}