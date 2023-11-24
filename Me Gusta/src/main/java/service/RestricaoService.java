package service;

import spark.Request;

import com.google.gson.Gson;

import spark.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.postgresql.util.PSQLException;

import dao.RestricaoDAO;
import dao.UsuarioDAO;
import model.Restricao;
import model.Usuario;

public class RestricaoService {
	private RestricaoDAO dao = new RestricaoDAO();
	private UsuarioDAO userDao = new UsuarioDAO();
	
	/* Método de inserção de restrição
	 * Coleta dados dos formulários e envia para DAO
	 */
	public String insereRestricao(Request request, Response response) {
		System.out.println("Recuperando dados...");
		// Linhas que recebem os parâmetros da URL
	    String nome = request.queryParams("nome");
	    String tipo = request.queryParams("tipo");
	    
	    String resp = "";
	    boolean status = false;
	    boolean restrictionExists = false;

	    try {
		    	// Verifica se a instrição já foi inserida
		        if(dao.verificarRestricao(nome)) {
		        	restrictionExists = true; 
		        } else {
		        	// Em caso positivo, chama a função
		        	status = dao.inserirRestricao(new Restricao(nome, tipo));
		        }

		        // Verifica a resposta
		        if (status) {
		            resp = "Restrição (" + nome + ") inserida!";
		            response.status(201); // 201 Created
		        } else {
		            resp = "Restrição (" + nome + ") não inserida!";
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
	    
	    // Objeto JSON que será manipulado no Front-end
	    JSONObject resposta = new JSONObject();
	    resposta.put("success", status);
		resposta.put("restrictionExists", restrictionExists);

	    Gson gson = new Gson();
	    return gson.toJson(resposta); // Retorna o objeto resposta
	}
	
	/* Método de alteração de restrição
	 * Coleta dados dos formulários e envia para DAO
	 */
	public String alteraRestricao(Request request, Response response) {
		
		// Linhas que recebem os parâmetros da URL
	    String idParam = request.queryParams("id");
		UUID id = UUID.fromString(idParam);
	    String nome = request.queryParams("nome");
	    String tipo = request.queryParams("tipo");
	    String resp = "";
	    boolean status = false;
	    boolean restrictionExists = false;

	    try {
		    	// Verifica se a restrição já não foi inserida
		        if(dao.verificarRestricao(nome)) {
		        	restrictionExists = true;
		        } else {
		        	// Em caso positivo, chama a função
		        	status = dao.atualizarRestricao(new Restricao(id, nome, tipo));
		        }

		        // Verifica a resposta
		        if (status) {
		            resp = "Restrição(" + nome + ") alterada!";
		            response.status(201); // 201 Created
		        } else {
		            resp = "Restrição (" + nome + ") alterada!";
		            response.status(404); // 404 Not found
		        }

	        System.out.printf(resp);
	    } catch (Exception e) {
	        System.err.println("Erro inesperado: " + e.getMessage());
	    }
	    
	    // Objeto JSON que será manipulado no Front-end
	    JSONObject resposta = new JSONObject();
	    resposta.put("success", status);
		resposta.put("restrictionExists", restrictionExists);

	    Gson gson = new Gson();
	    return gson.toJson(resposta); // Retorna o objeto resposta
	}
    
	/* Método GET de restrições
	 * Recebe todas as linhas da tabela restrição no BD
	 */
	public String obterRestricoes() {
	    List<Restricao> restricoes = dao.getRestricoes();
        Gson gson = new Gson();
        return gson.toJson(restricoes);
	}
	
	/* Método GET de restrições
	 * Recebe todas as linhas da tabela restrição no BD ordenadas alfabeticamente por nome ou tipo
	 */
    public String obterRestricoesOrdenadas(String order) {
		String orderBy = order;
        System.out.println("Valor de orderBy: " + orderBy);
        
        List<Restricao> restricoes = dao.getRestricoes();
        List<Restricao> restricoesOrdenadas = new ArrayList<>();
     
        if ("alfabetica".equals(orderBy)) {

        	restricoesOrdenadas = dao.sort(restricoes);
        } else if ("insercao".equals(orderBy)) {

        	restricoesOrdenadas = dao.getRestricoes();
        } else if ("tipo".equals(orderBy)){
        	
        	restricoesOrdenadas = dao.sortTipo(restricoes);
        }
        
        Gson gson = new Gson();
		return gson.toJson(restricoesOrdenadas);
    }
    
	/* Método GET de pesquisa com campo de texto
	 * Recebe um termo de pesquisa como argumento e pesquisa nas linhas do BD
	 */
	
    public String obterResultadosPesquisa(String termoPesquisa) {
        List<Restricao> restricoes = dao.getPesquisa(termoPesquisa);
        Gson gson = new Gson();
        return gson.toJson(restricoes);
    }
    
	/* Método GET para uma restrição
	 * A partir de um argumento user, faz a pesquisa de uma restrição no BD e retorna um objeto
	 */
    public String buscaRestricao(String nome) {
    	
	    Restricao restriction = dao.buscarRestricao(nome);
	    
    	Gson gson = new Gson();
    	JSONObject resposta = new JSONObject();
    	boolean status = false;
    	if(restriction == null) {
		    resposta.put("success", status);
    	} else {
    		status = true;
		    resposta.put("success", status);
		    resposta.put("object", restriction);
    	}
    	
    	System.out.println("Status: " + status);
	    return gson.toJson(resposta);
    }
    
	/* Método DELETE para uma restricao
	 * Exclui uma linha da tabela restrição
	 */
    public String deletarRestricao(Request request, Response response) {
    	String restricao = request.queryParams("apagarRestricao");
    	String user = request.queryParams("validSuperuser");
    	String senha = request.queryParams("confirmarSenha");
    	
    	Gson gson = new Gson();
    	JSONObject resposta = new JSONObject();
    	boolean status = false;
    	boolean passwordIncorrect = false;
    	boolean restrictionExists = dao.restricaoExiste(restricao);
    	Usuario usuario = userDao.buscarUsuario(user);
    	boolean validSuperuser = false;
    	
    	if(restrictionExists) {
    		if(usuario.getPermissao() == 'S') {
    			validSuperuser = true;
            	// Permite deletar user apenas se a senha corresponder
            	if(!(userDao.verificaSenha(senha, user))){
            		passwordIncorrect = true;
            	} else {
            		status = dao.excluirRestricao(restricao);
            	}
    		} 
    	}
    
    	resposta.put("success", status);
    	resposta.put("validSuperuser", validSuperuser);
    	resposta.put("restrictionExists", restrictionExists);
    	resposta.put("passwordIncorrect", passwordIncorrect);

	    return gson.toJson(resposta);
    }
    
	/* Método DELETE para todas restrições
	 * Exclui todas as linhas da tavbela restrição no bd
	 */
    public String deletarTodas(Request request, Response response) {
    	
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
	    		status = dao.apagarTodasRestricoes();
			} 
    	} 
    
    	resposta.put("success", status);
    	resposta.put("passwordIncorrect", passwordIncorrect);
    	resposta.put("validSuperuser", validSuperuser);

	    return gson.toJson(resposta);
    }
}