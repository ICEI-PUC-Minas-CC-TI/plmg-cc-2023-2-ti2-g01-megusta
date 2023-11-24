package service;

import spark.Request;

import com.google.gson.Gson;


import spark.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.postgresql.util.PSQLException;

import dao.UsuarioDAO;
import model.Usuario;

public class UsuarioService {
	private UsuarioDAO dao = new UsuarioDAO();

	/* Método de inserção de usuário
	 * Coleta dados dos formulários e envia para DAO
	 */
	public String insereUsuario(Request request, Response response) {

		// Linhas que recebem os parâmetros da URL
	    String nome = request.queryParams("nome");
	    String sobrenome = request.queryParams("sobrenome");
	    int idade = Integer.parseInt(request.queryParams("idade"));
	    String email = request.queryParams("email");
	    String senha = request.queryParams("senha");
	    String confirmSenha = request.queryParams("confirmSenha");
	    String user = request.queryParams("user");
	    char genero = request.queryParams("genero").charAt(0);
	    
	    String resp = "";
	    boolean status = false;
	    boolean userExists = false;
	    boolean equalsPassword = true;

	    try {
	    	if(senha.equals(confirmSenha)) {
		    	// Verifica se o user é válido (isto é, não está sendo utilizado)
		        if(!(dao.verificaUser(user))) {
		        	userExists = true; // O user existe
		        } else {
		        	// Em caso positivo, chama a função
		        	status = dao.inserirUsuario(new Usuario(nome, sobrenome, user, senha, email, genero, idade));
		        }


		        // Verifica a resposta
		        if (status) {
		            resp = "Usuario (" + nome + ") inserido!";
		            response.status(201); // 201 Created
		        } else {
		            resp = "Usuario (" + nome + ") não inserido!";
		            response.status(404); // 404 Not found
		        }

	    	} else {
	    		equalsPassword = false;
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
		resposta.put("userExists", userExists);
		resposta.put("equalsPassword", equalsPassword);

	    Gson gson = new Gson();
	    return gson.toJson(resposta); // Retorna o objeto resposta
	}
	
		/* Método de inserção de superusuário
		 * Coleta dados dos formulários e envia para DAO
		 */
		public String insereSuperuser(Request request, Response response) {
			
		    String nome = request.queryParams("nome");
		    String sobrenome = request.queryParams("sobrenome");
		    int idade = Integer.parseInt(request.queryParams("idade"));
		    String email = request.queryParams("email");
		    String senha = request.queryParams("senha");
		    String confirmSenha = request.queryParams("confirmSenha");
		    String user = request.queryParams("user");
		    char genero = request.queryParams("genero").charAt(0);
		    char permissao = 'S'; // A diferença é que o atributo permissao é S
		    String resp = "";
		    boolean status = false;
		    boolean userExists = false;
		    boolean equalsPassword = true;

		    try {
		    	// Verifica se as senhas informadas são iguais
		    	if(senha.equals(confirmSenha)) {
			        if(!(dao.verificaUser1(user))) {
			        	userExists = true;
			        } else {
			        	status = dao.inserirUsuario(new Usuario(nome, sobrenome, user, senha, email, genero, idade, permissao));
			        }

			        if (status) {
			            resp = "Superusuário (" + nome + ") inserido!";
			            response.status(201); // 201 Created
			        } else {
			            resp = "Superusuário (" + nome + ") não inserido!";
			            response.status(404); // 404 Not found
			        }
		    	} else {
		    		equalsPassword = false;
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
		    
		    // Cria o objeto JSON resposta que será manipulado no Front-end
		    JSONObject resposta = new JSONObject();
		    resposta.put("success", status);
			resposta.put("userExists", userExists);
			resposta.put("equalsPassword", equalsPassword);
			
		    Gson gson = new Gson();
		    return gson.toJson(resposta);
		}
		
		/* Método de alteração de superusuário
		 * Coleta dados dos formulários e envia para DAO
		 */
		public String alteraSuperuser(Request request, Response response) {
			
			String idParam = request.queryParams("id");
			UUID id = UUID.fromString(idParam);
		    String nome = request.queryParams("nome");
		 
		    String sobrenome = request.queryParams("sobrenome");
		    
		    String user = request.queryParams("user");
		
		    String email = request.queryParams("email");
		 
		    int idade = Integer.parseInt(request.queryParams("idade"));
		    
		    String senhaAtual = request.queryParams("senhaAtual");
		    
		    String senha = request.queryParams("senha");
		    String confirmSenha = request.queryParams("confirmSenha");
		    char genero = request.queryParams("genero").charAt(0);
		
		    char permissao = 'S';
		    String resp = "";
		    boolean status = false;
		    boolean userExists = false;
		    boolean passwordIncorrect = false;
		    boolean equalsPassword = true;

		    try {
		    	// Verifica se a senha atual corresponde à senha no BD
		    	if(!(dao.verificaSenha(senhaAtual, user))){
		    		passwordIncorrect = true; // Senha incorreta, aborta operação
		    	  // Em caso afirmativo, verifica se a nova senha corresponde ao campo confirmar senha
		    	} else if(senha.equals(confirmSenha)){
		    		// Em caso positivo, verifica se o user é válido
		    		if(!(dao.verificaUser(user))) {
			        	userExists = true;
			        } else {
			        	// Em caso positivo, chama a função de atualizar
			        	status = dao.atualizarUsuario(new Usuario(id, nome, sobrenome, user, senha, email, genero, idade, permissao));
			        }

			        if (status) {
			            resp = "Superusuário (" + nome + ") alterado!";
			            response.status(201); // 201 Created
			        } else {
			            resp = "Superusuário (" + nome + ") não alterado!";
			            response.status(404); // 404 Not found
			        }
		    	} else {
		    		equalsPassword = false;
		    	}

		        System.out.printf(resp);
		    } catch (Exception e) {
		        System.err.println("Erro inesperado: " + e.getMessage());
		    }
		    
		    JSONObject resposta = new JSONObject();
		    resposta.put("success", status);
		    resposta.put("passwordIncorrect", passwordIncorrect);
			resposta.put("userExists", userExists);
			resposta.put("equalsPassword", equalsPassword);

		    Gson gson = new Gson();
		    return gson.toJson(resposta);
		}
		
		/* Método de alteração de superusuário
		 * Coleta dados dos formulários e envia para DAO
		 */
		public String alteraUser(Request request, Response response) {
			System.out.println("At your service");
			String idParam = request.queryParams("id");
			UUID id = UUID.fromString(idParam);
		    String nome = request.queryParams("nome");
		 
		    String sobrenome = request.queryParams("sobrenome");
		    
		    String user = request.queryParams("user");
		
		    String email = request.queryParams("email");
		 
		    int idade = Integer.parseInt(request.queryParams("idade"));
		    
		    String senhaAtual = request.queryParams("senhaAtual");
		    
		    String senha = request.queryParams("senha");
		    String confirmSenha = request.queryParams("confirmSenha");
		    char genero = request.queryParams("genero").charAt(0);
		
		    char permissao = 'N';
		    String resp = "";
		    boolean status = false;
		    boolean userExists = false;
		    boolean passwordIncorrect = false;
		    boolean equalsPassword = true;

		    try {
		    	// Verifica se a senha atual corresponde à senha no BD
		    	if(!(dao.verificaSenha(senhaAtual, user))){
		    		passwordIncorrect = true; // Senha incorreta, aborta operação
		    	  // Em caso afirmativo, verifica se a nova senha corresponde ao campo confirmar senha
		    	} else if(senha.equals(confirmSenha)){
		    		// Em caso positivo, verifica se o user é válido
		    		if(!(dao.verificaUser(user))) {
			        	userExists = true;
			        } else {
			        	// Em caso positivo, chama a função de atualizar
			        	status = dao.atualizarUsuario(new Usuario(id, nome, sobrenome, user, senha, email, genero, idade, permissao));
			        }

			        if (status) {
			            resp = "Usuário (" + nome + ") alterado!";
			            response.status(201); // 201 Created
			        } else {
			            resp = "Usuário (" + nome + ") não alterado!";
			            response.status(404); // 404 Not found
			        }
		    	} else {
		    		equalsPassword = false;
		    	}

		        System.out.printf(resp);
		    } catch (Exception e) {
		        System.err.println("Erro inesperado: " + e.getMessage());
		    }
		    
		    JSONObject resposta = new JSONObject();
		    resposta.put("success", status);
		    resposta.put("passwordIncorrect", passwordIncorrect);
			resposta.put("userExists", userExists);
			resposta.put("equalsPassword", equalsPassword);

		    Gson gson = new Gson();
		    return gson.toJson(resposta);
		}
		
	/* Método de autenticação de usuário
	 * Utilizado na operação de log-in
	 */
	public String autenticaUsuario(Request request, Response response){
		
		boolean status = false;
		
	    String senha = request.queryParams("senha");
	    String user = request.queryParams("user");
	    
	    Usuario usuario = dao.buscarUsuario(user);
	    boolean info = dao.informouIngredientes(usuario.getId());
	    status = dao.autenticarUsuario(user, senha); // Se o retorno for true, o usuário e a senha estão corretos e permite o acesso

	    JSONObject resposta = new JSONObject();
	    resposta.put("success", status);
	    resposta.put("object", usuario);
	    resposta.put("info", info);

	    Gson gson = new Gson();
	    return gson.toJson(resposta);
	}
	
	/* Método para salvas as restrições do usuário */
	public String salvaRestricoes(Request request, Response response){
		boolean status = false;
		
		String user = request.queryParams("user");
	    String[] restricoesArray = request.queryParamsValues("restrictions");
	    
	    Usuario usuario = dao.buscarUsuario(user);
		 
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
		
		try {
			status = dao.salvarRestricoes(usuario.getId(), restricoesList);
		} catch (PSQLException e) {
			e.printStackTrace();
		}
		
	    JSONObject resposta = new JSONObject();
	    resposta.put("success", status);

	    Gson gson = new Gson();
	    return gson.toJson(resposta);
	}
	
	/* Método para atualizar as restrições do usuário */
	public String atualizaRestricoes(Request request, Response response){
		boolean status = false;
		
		String user = request.queryParams("user");
	    String[] restricoesArray = request.queryParamsValues("restrictions");
	    
	    Usuario usuario = dao.buscarUsuario(user);
		 
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
		
		try {
			status = dao.atualizarRestricoes(usuario.getId(), restricoesList);
		} catch (PSQLException e) {
			e.printStackTrace();
		}
		
	    JSONObject resposta = new JSONObject();
	    resposta.put("success", status);

	    Gson gson = new Gson();
	    return gson.toJson(resposta);
	}
	
	public String resetRestricoes(Request request, Response response) {
		boolean status = false;
		String user = request.queryParams("user");
		Usuario usuario = dao.buscarUsuario(user);
		
		status = dao.resetarRestricoes(usuario.getId());
				
	    JSONObject resposta = new JSONObject();
	    resposta.put("success", status);

	    Gson gson = new Gson();
	    return gson.toJson(resposta);
	}
	
	/* Método para salvas os ingredientes desejados pelo usuário */
	public String salvaIngredientes(Request request, Response response){
		boolean status = false;
		
		String user = request.queryParams("user");
		
	    String[] ingredientesArray = request.queryParamsValues("alimentos");
	    
	    Usuario usuario = dao.buscarUsuario(user);
		 
	    List<UUID> ingredientesList = new ArrayList<>();

	    // Verifica se há restrições
	    if (ingredientesArray != null && ingredientesArray.length > 0) {
	        for (String ingredienteId : ingredientesArray) {
	            try {
	                UUID uuid = UUID.fromString(ingredienteId);
	                ingredientesList.add(uuid);
	            } catch (IllegalArgumentException e) {
	                // Tratar a exceção se a string não puder ser convertida para UUID
	                System.err.println("Erro ao converter para UUID: " + e.getMessage());
	            }
	        }
	    }
	    
	    System.out.println("Tamanho: " + ingredientesList.size());
		
		try {
			status = dao.salvarIngredientes(usuario.getId(), ingredientesList);
		} catch (PSQLException e) {
			e.printStackTrace();
		}
		
	    JSONObject resposta = new JSONObject();
	    resposta.put("success", status);

	    Gson gson = new Gson();
	    return gson.toJson(resposta);
	}
	
	/* Método para atualizar os ingredientes desejados pelo usuário */
	public String atualizaIngredientes(Request request, Response response){
		boolean status = false;
		
		String user = request.queryParams("user");
	    String[] ingredientesArray = request.queryParamsValues("alimentos");
	    
	    Usuario usuario = dao.buscarUsuario(user);
		 
	    List<UUID> ingredientesList = new ArrayList<>();

	    // Verifica se há restrições
	    if (ingredientesArray != null && ingredientesArray.length > 0) {
	        for (String ingredienteId : ingredientesArray) {
	            try {
	                UUID uuid = UUID.fromString(ingredienteId);
	                ingredientesList.add(uuid);
	            } catch (IllegalArgumentException e) {
	                // Tratar a exceção se a string não puder ser convertida para UUID
	                System.err.println("Erro ao converter para UUID: " + e.getMessage());
	            }
	        }
	    }
		
		try {
			status = dao.atualizarIngredientes(usuario.getId(), ingredientesList);
		} catch (PSQLException e) {
			e.printStackTrace();
		}
		
	    JSONObject resposta = new JSONObject();
	    resposta.put("success", status);

	    Gson gson = new Gson();
	    return gson.toJson(resposta);
	}
	
	/* Método para limpar os ingredientes desejados pelo usuário */
	public String resetIngredientes(Request request, Response response) {
		boolean status = false;
		String user = request.queryParams("user");
		Usuario usuario = dao.buscarUsuario(user);
		
		status = dao.resetarIngredientes(usuario.getId());
				
	    JSONObject resposta = new JSONObject();
	    resposta.put("success", status);

	    Gson gson = new Gson();
	    return gson.toJson(resposta);
	}

	/* Método para salvar os ingredientes não desejados pelo usuário */
	public String salvaIngredientesX(Request request, Response response){
		boolean status = false;
		
		String user = request.queryParams("user");
	    String[] ingredientesArray = request.queryParamsValues("alimentos");
	    
	    Usuario usuario = dao.buscarUsuario(user);
		 
	    List<UUID> ingredientesList = new ArrayList<>();

	    // Verifica se há restrições
	    if (ingredientesArray != null && ingredientesArray.length > 0) {
	        for (String ingredienteId : ingredientesArray) {
	            try {
	                UUID uuid = UUID.fromString(ingredienteId);
	                ingredientesList.add(uuid);
	            } catch (IllegalArgumentException e) {
	                // Tratar a exceção se a string não puder ser convertida para UUID
	                System.err.println("Erro ao converter para UUID: " + e.getMessage());
	            }
	        }
	    }
		
		try {
			status = dao.salvarIngredientesX(usuario.getId(), ingredientesList);
		} catch (PSQLException e) {
			e.printStackTrace();
		}
		
	    JSONObject resposta = new JSONObject();
	    resposta.put("success", status);

	    Gson gson = new Gson();
	    return gson.toJson(resposta);
	}
	
	/* Método para atualizar os ingredientes não desejados pelo usuário */
	public String atualizaIngredientesX(Request request, Response response){
		boolean status = false;
		
		String user = request.queryParams("user");
	    String[] ingredientesArray = request.queryParamsValues("alimentos");
	    
	    Usuario usuario = dao.buscarUsuario(user);
		 
	    List<UUID> ingredientesList = new ArrayList<>();

	    // Verifica se há restrições
	    if (ingredientesArray != null && ingredientesArray.length > 0) {
	        for (String ingredienteId : ingredientesArray) {
	            try {
	                UUID uuid = UUID.fromString(ingredienteId);
	                ingredientesList.add(uuid);
	            } catch (IllegalArgumentException e) {
	                // Tratar a exceção se a string não puder ser convertida para UUID
	                System.err.println("Erro ao converter para UUID: " + e.getMessage());
	            }
	        }
	    }
		
		try {
			status = dao.atualizarIngredientesX(usuario.getId(), ingredientesList);
		} catch (PSQLException e) {
			e.printStackTrace();
		}
		
	    JSONObject resposta = new JSONObject();
	    resposta.put("success", status);

	    Gson gson = new Gson();
	    return gson.toJson(resposta);
	}
	
	/* Método para limpar os ingredientes não desejados pelo usuário */
	public String resetIngredientesX(Request request, Response response) {
		boolean status = false;
		String user = request.queryParams("user");
		Usuario usuario = dao.buscarUsuario(user);
		
		status = dao.resetarIngredientesX(usuario.getId());
				
	    JSONObject resposta = new JSONObject();
	    resposta.put("success", status);

	    Gson gson = new Gson();
	    return gson.toJson(resposta);
	}	

	/* Método GET de usuários
	 * Recebe todas as linhas da tabela usuário no BD
	 */
	public List<Usuario> obterUsuarios() {
	    UsuarioDAO usuarioDAO = new UsuarioDAO();
	    return usuarioDAO.getUsuarios();
	}

	/* Método GET de usuários
	 * Recebe todas as linhas da tabela usuário no BD ordenadas alfabeticamente com o atributo user
	 */
	public List<Usuario> obterUsuariosOrdenados() {
	    UsuarioDAO usuarioDAO = new UsuarioDAO();
	    List<Usuario> usuarios = usuarioDAO.getUsuarios();
	    return usuarioDAO.sort(usuarios);
	}
	
	/* Método GET de pesquisa com campo de texto
	 * Recebe um termo de pesquisa como argumento e pesquisa nas linhas do BD
	 */
	public List<Usuario> obterResultadosPesquisa(String termoPesquisa) {
	    UsuarioDAO usuarioDAO = new UsuarioDAO();
	    return usuarioDAO.getPesquisa(termoPesquisa);
	}
	
	/* Método GET para um usuário
	 * A partir de um argumento user, faz a pesquisa de um usuário no BD e retorna um objeto
	 */
	public Usuario buscaUsuario(String user) {
	    UsuarioDAO usuarioDAO = new UsuarioDAO();
	    return usuarioDAO.buscarUsuario(user);
	}
	
	/* Método auxiliar para ordenação
	 * Recebe um parâmetro com o tipo de ordenação e chama a função correspondente para cada caso
	 */
    public String obterUsuariosOrdenadosJSON(String order) {
		String orderBy = order;
        
        List<Usuario> usuarios = new ArrayList<>();
        
        if ("alfabetica".equals(orderBy)) {

        	usuarios = obterUsuariosOrdenados();
        } else if ("insercao".equals(orderBy)) {

        	usuarios = obterUsuarios();
        } 
        
        Gson gson = new Gson();
		return gson.toJson(usuarios);
    }
	
    public String obterUsuariosJSON() {
        List<Usuario> usuarios = obterUsuarios();
        Gson gson = new Gson();
        return gson.toJson(usuarios);
    }
    
    public String obterResultadosPesquisaJSON(String termoPesquisa) {
        List<Usuario> usuarios = obterResultadosPesquisa(termoPesquisa);
        Gson gson = new Gson();
        return gson.toJson(usuarios);
    }
    
    public String buscaUsuarioJSON(String user) {
    	
    	Usuario user1 = buscaUsuario(user);
    	Gson gson = new Gson();
    	JSONObject resposta = new JSONObject();
    	boolean status = false;
    	if(user1 == null) {
		    resposta.put("success", status);
		    resposta.put("objetc", null);
    	} else {
    		status = true;
		    resposta.put("success", status);
		    resposta.put("object", user1);
    	}
	    return gson.toJson(resposta);
    }
    
	/* Método DELETE para um usuário
	 * 
	 */
    public String deletarUser(Request request, Response response) {
    	
    	String user = request.queryParams("apagarUser");
    	String senha = request.queryParams("confirmarSenha");
    	Gson gson = new Gson();
    	JSONObject resposta = new JSONObject();
    	boolean status = false;
    	boolean passwordIncorrect = false;
    	
    	// Permite deletar user apenas se a senha corresponder
    	if(!(dao.verificaSenha(senha, user))){
    		passwordIncorrect = true;
    	} else {
    		status = dao.excluirUsuario(user);
    	}
    
    	resposta.put("success", status);
    	resposta.put("passwordIncorrect", passwordIncorrect);

	    return gson.toJson(resposta);
    }
    
	/* Método DELETE para todos os usuários
	 * 
	 */
    public String deletarTodos(Request request, Response response) {
    	
    	String user = request.queryParams("superuser");
    	String senha = request.queryParams("senhaSuperuser");
    	Gson gson = new Gson();
    	JSONObject resposta = new JSONObject();
    	boolean status = false;
    	boolean passwordIncorrect = true;
    	boolean validSuperuser = false;
    	Usuario user1 = dao.buscarUsuario(user);
    	
    	// Permite delete all apenas se o usuário tiver supermissões E se as senhas forem corrrespondentes
    	if(user1.getPermissao() == 'S') {
			validSuperuser = true;
			if(dao.verificaSenha(senha, user)){
	    		passwordIncorrect = false;
	    		status = dao.apagarTodosUsuarios();
			} 
    	} 
    
    	resposta.put("success", status);
    	resposta.put("passwordIncorrect", passwordIncorrect);
    	resposta.put("validSuperuser", validSuperuser);

	    return gson.toJson(resposta);
    }
}