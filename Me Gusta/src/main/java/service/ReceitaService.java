package service;

import spark.Request;

import com.google.gson.Gson;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spark.Response;
import spark.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;

import org.json.simple.JSONObject;
import org.postgresql.util.PSQLException;

import dao.IngredienteDAO;
import dao.ReceitaDAO;
import dao.UsuarioDAO;
import model.Ingrediente;
import model.Receita;
import model.Usuario;

public class ReceitaService {
	private ReceitaDAO dao = new ReceitaDAO();
	private IngredienteDAO ingredienteDao = new IngredienteDAO();
	private UsuarioDAO userDao = new UsuarioDAO();
	
	/* Método de inserção de receita
	 * Coleta dados dos formulários e envia para DAO
	 */
	public String insereReceita(Request request, Response response) {
		System.out.println("A seu dispor!");
		String user = request.queryParams("user");
		Usuario usuario = userDao.buscarUsuario(user);
	    String titulo = request.queryParams("titulo");
	    String descricao = request.queryParams("descricao");
	    String[] instrucao = request.queryParamsValues("instrucao");
	    System.out.println("Até instrução, ok!");
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
		System.out.println("Imagem, ok!");
	    String dificuldade = request.queryParams("dificuldade");
	    System.out.println("Dificuldade, ok!");
	    String tempoPreparoStr = request.queryParams("tempoPreparo");
	    String custoStr = request.queryParams("custo");
	    int tempoPreparo = Integer.parseInt(tempoPreparoStr);
	    double custo = Double.parseDouble(custoStr);
	    System.out.println("Tempo e custo, ok!");
	    String porcao = request.queryParams("porcao");
	    System.out.println("Porcao, ok!");
	    String[] ingredientesArray = request.queryParamsValues("alimentos");
	    List<UUID> ingredientesList = new ArrayList<>();

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
	    
	    System.out.println("Ingredientes, ok! Tamanho: " + ingredientesList.size());
	    String[] quantidadesArray = request.queryParamsValues("quantidade");
	    int[] quantidades = null;
	    int i = 0;

	    if (quantidadesArray != null && quantidadesArray.length > 0) {
	        int nonNullCount = 0;
	        for (String inteiro : quantidadesArray) {
	            if (!inteiro.trim().isEmpty()) {
	                nonNullCount++;
	            }
	        }

	        quantidades = new int[nonNullCount];

	        for (String inteiro : quantidadesArray) {
	            if (!inteiro.trim().isEmpty()) {
	                try {
	                    int quantidade = Integer.parseInt(inteiro);
	                    quantidades[i] = quantidade;
	                    i++;
	                } catch (NumberFormatException e) {
	                    // Handle the exception if the string cannot be converted to an integer
	                    System.err.println("Erro ao converter para int: " + e.getMessage());
	                }
	            }
	        }
	    }
	    
	    System.out.println("Quantidades, ok!");
	    JSONObject nutritionalinfo = dao.calculaInfoNutricional(ingredientesList, quantidades);
	    System.out.println("InfoNutri calculada com sucesso!");
	    String resp = "";
	    boolean status = false;
	    
	    try {
	    	System.out.println("Vamos chamar a DAO!");
	        	status = dao.inserirReceita(new Receita(usuario.getId(), titulo, descricao, instrucao, 
	        			tempoPreparo, porcao, dificuldade, custo, nutritionalinfo, imagem), ingredientesList, usuario.getId(), quantidades);

		        // Verifica a resposta
		        if (status) {
		            resp = "Receita (" + titulo + ") inserida!";
		            response.status(201); // 201 Created
		        } else {
		            resp = "Receita (" + titulo + ") não inserida!";
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
	
	/* Método de atualização de receitas
	 * Coleta dados dos formulários e envia para DAO
	 */
	public String alteraReceita(Request request, Response response) {
		//System.out.println("A seu dispor para alterar!");
		String user = request.queryParams("user"); 
		String recipe = request.queryParams("id");
		UUID recipe_id = UUID.fromString(recipe);
		Usuario usuario = userDao.buscarUsuario(user);
	    String titulo = request.queryParams("titulo");
	    String descricao = request.queryParams("descricao");
	    String[] instrucao = request.queryParamsValues("instrucao");
	    System.out.println("Até instrução, ok!");
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
		System.out.println("Imagem, ok!");
	    String dificuldade = request.queryParams("dificuldade");
	    System.out.println("Dificuldade, ok!");
	    String tempoPreparoStr = request.queryParams("tempoPreparo");
	    String custoStr = request.queryParams("custo");
	    int tempoPreparo = Integer.parseInt(tempoPreparoStr);
	    double custo = Double.parseDouble(custoStr);
	    System.out.println("Tempo e custo, ok!");
	    String porcao = request.queryParams("porcao");
	    System.out.println("Porcao, ok!");
	    String[] ingredientesArray = request.queryParamsValues("alimentos");
	    List<UUID> ingredientesList = new ArrayList<>();

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
	    
	    System.out.println("Ingredientes, ok! Tamanho: " + ingredientesList.size());
	    String[] quantidadesArray = request.queryParamsValues("quantidade");
	    int[] quantidades = null;
	    int i = 0;

	    if (quantidadesArray != null && quantidadesArray.length > 0) {
	        int nonNullCount = 0;
	        for (String inteiro : quantidadesArray) {
	            if (!inteiro.trim().isEmpty()) {
	                nonNullCount++;
	            }
	        }

	        quantidades = new int[nonNullCount];

	        for (String inteiro : quantidadesArray) {
	            if (!inteiro.trim().isEmpty()) {
	                try {
	                    int quantidade = Integer.parseInt(inteiro);
	                    quantidades[i] = quantidade;
	                    i++;
	                } catch (NumberFormatException e) {
	                    // Handle the exception if the string cannot be converted to an integer
	                    System.err.println("Erro ao converter para int: " + e.getMessage());
	                }
	            }
	        }
	    }
	    
	    System.out.println("Quantidades, ok!");
	    JSONObject nutritionalinfo = dao.calculaInfoNutricional(ingredientesList, quantidades);
	    System.out.println("InfoNutri calculada com sucesso!");
	    String resp = "";
	    boolean status = false;
	    
	    try {
	    	System.out.println("Vamos chamar a DAO!");
	        	status = dao.atualizarReceita(new Receita(recipe_id, usuario.getId(), titulo, descricao, instrucao, 
	        			tempoPreparo, porcao, dificuldade, custo, nutritionalinfo, imagem), ingredientesList, usuario.getId(), quantidades);

		        // Verifica a resposta
		        if (status) {
		            resp = "Receita (" + titulo + ") editada!";
		            response.status(201); // 201 Created
		        } else {
		            resp = "Receita (" + titulo + ") não editada!";
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
	
	/* Método Filtrar Receitas
	 * Restringe as receitas com base nas restrições do usuário
	 */
	
	public List<Receita> filtrarReceitas(List<Receita> receitas, UUID user_id) throws Exception{
		//System.out.println("Quase lá, filtrando receitas: " + user_id);
		List<Receita> receitasFiltradas = dao.filtrarReceitas(receitas, user_id);
		
		return receitasFiltradas;
	}
    
	/* Método GET de receitas
	 * Recebe todas as linhas da tabela receita no BD
	 */
	public List<Receita> obterReceitas() {
	    List<Receita> receitas = dao.getReceitas();
	    
        return receitas;
	}
	
	public String buscaMinhasReceitas(UUID id) {
		System.out.println("A seu serviço para pegar minhas receitas");
	    List<Receita> receitas = dao.buscarMinhasReceitas(id);
	    System.out.println("Eu tenho " + receitas.size() + " receitas");
    	Gson gson = new Gson();
	    return gson.toJson(receitas);
	}
	
	/* Método GET de receitas
	 * Recebe todas as linhas da tabela receita no BD ordenadas de diferentes modos
	 */
    public List<Receita> obterReceitasOrdenadas(String order) {
		String orderBy = order;
        System.out.println("Valor de orderBy: " + orderBy);
        
        List<Receita> receitas = dao.getReceitas();
        List<Receita> receitasOrdenadas = new ArrayList<>();
     
        if ("calorias".equals(orderBy)) {
        	receitasOrdenadas = dao.sortCalorias(receitas);
        } else if ("dificuldade".equals(orderBy)) {
        	receitasOrdenadas = dao.sortDificuldade(receitas);
        } else if ("custo".equals(orderBy)){
        	receitasOrdenadas = dao.sortPreco(receitas);
        } else if ("avaliacao".equals(orderBy)){
        	receitasOrdenadas = dao.sortAvaliacao(receitas);
        }
        
        return receitasOrdenadas;
    }
    
	/* Método GET de pesquisa com campo de texto
	 * Recebe um termo de pesquisa como argumento e pesquisa nas linhas do BD
	 */
	
    public String obterResultadosPesquisa(String termoPesquisa) {
        List<Receita> receitas = dao.getPesquisa(termoPesquisa);
        Gson gson = new Gson();
        return gson.toJson(receitas);
    }
    
	/* Método GET para uma receita
	 * A partir de um argumento id, faz a pesquisa de uma receita no BD e retorna um objeto
	 */
    public String buscaReceita(UUID id) {
    	System.out.println("A seu dispor");
    	Receita receita = dao.buscarReceita(id);
	    
    	Gson gson = new Gson();
    	JSONObject resposta = new JSONObject();
    	boolean status = false;
    	if(receita == null) {
		    resposta.put("success", status);
    	} else {
    		status = true;
		    resposta.put("success", status);
		    resposta.put("object", receita);
    	}
    	System.out.println("ID da receita: " + receita.getId());
	    return gson.toJson(resposta);
    }
    
	/* Método DELETE para uma receita
	 * Exclui uma linha da tabela receita
	 */
    public String deletarReceita(Request request, Response response) {
    	String ingrediente = request.queryParams("apagarIngrediente");
    	UUID id = UUID.fromString(ingrediente);
    	String user = request.queryParams("validSuperuser");
    	String senha = request.queryParams("confirmarSenha");
    	
    	Gson gson = new Gson();
    	JSONObject resposta = new JSONObject();
    	boolean status = false;
    	boolean passwordIncorrect = false;
    	Usuario usuario = userDao.buscarUsuario(user);
    	boolean validSuperuser = false;
    	
  
		if(usuario.getPermissao() == 'S') {
			validSuperuser = true;
        	// Permite deletar user apenas se a senha corresponder
        	if(!(userDao.verificaSenha(senha, user))){
        		passwordIncorrect = true;
        	} else {
        		status = dao.excluirReceita(id);
        	}
		} 
    
    	resposta.put("success", status);
    	resposta.put("validSuperuser", validSuperuser);
    	resposta.put("passwordIncorrect", passwordIncorrect);

	    return gson.toJson(resposta);
    }
    
	/* Método DELETE para todos as receitas
	 * Exclui todas as linhas da tabela receita no bd
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
	    		status = dao.apagarTodasReceitas();
			} 
    	} 
    
    	resposta.put("success", status);
    	resposta.put("passwordIncorrect", passwordIncorrect);
    	resposta.put("validSuperuser", validSuperuser);

	    return gson.toJson(resposta);
    }
    
    /* Método avalia
     * Recebe uma avaliação e o user de quem avaliou e comunica com DAO para salvar no bd
     */
    public String avalia(Request request, Response response) {
    	System.out.println("A seu dispor para avaliar");
    	boolean status = false;
    	UUID user_id = UUID.fromString(request.queryParams("user"));
    	System.out.println("User: " + user_id);
    	UUID recipe_id = UUID.fromString(request.queryParams("id"));
    	System.out.println("Recipe: " + recipe_id);
    	int avaliacao = Integer.parseInt(request.queryParams("avaliacao"));
    	System.out.println("Avaliação: " + avaliacao);
    	
    	try {
			status = dao.avaliar(user_id, recipe_id, avaliacao);
		} catch (PSQLException e) {
			e.printStackTrace();
		}
    	
    	JSONObject resposta = new JSONObject();
    	Gson gson = new Gson();
    	resposta.put("success", status);
    	return gson.toJson(resposta);
    }
    
    public double calculaReviews(Request request, Response response) {
    	UUID recipe_id = UUID.fromString(request.queryParams("recipe_id"));
    	
    	double media = dao.calcularReviews(recipe_id);
    	
    	return media;
    }
    
    /* Método salvaReceitas
     * Método para que o usuário salve uma receita na aba de receitas salvas
     */
    public String salvaReceita(Request request, Response response) throws SQLException {
        
        UUID autor = UUID.fromString(request.queryParams("autor"));
        UUID receita = UUID.fromString(request.queryParams("receita"));
        System.out.println("Autor:" + autor);
        boolean status = false;
        boolean isSalva = false;
        if(dao.isSalva(autor, receita)) {
        	isSalva = true;
        } else {
        	status = dao.salvarReceita(autor, receita);
        }

        JSONObject resposta = new JSONObject();
        Gson gson = new Gson();
        resposta.put("success", status);
        resposta.put("isSalva", isSalva);
        return gson.toJson(resposta);
    }
    
    /* Método GET para as receitas salvas */
    public String exibeReceitasSalvas(Request request, Response response) {
    	UUID user_id = UUID.fromString(request.queryParams("user_id"));
    	
    	List<Receita> receitasSalvas = dao.exibirReceitasSalvas(user_id);
    	
    	JSONObject resposta = new JSONObject();
    	Gson gson = new Gson();
    	resposta.put("object", receitasSalvas);
    	return gson.toJson(resposta);
    }
    
    /* Método liberaReceita
     * Método para que o usuário "des-salve" uma receita
     */
    public String liberaReceita(Request request, Response response) {
    	UUID user_id = UUID.fromString(request.queryParams("user_id"));
    	UUID recipe_id = UUID.fromString(request.queryParams("recipe_id"));
    	
    	boolean status = dao.liberarReceita(user_id, recipe_id);
    	
    	JSONObject resposta = new JSONObject();
    	Gson gson = new Gson();
    	resposta.put("success", status);
    	return gson.toJson(resposta);
    }
    
    /* Método liberaReceitas
     * Método para que o usuário resete a aba de receitas salvas
     */
    public String liberaReceitas(Request request, Response response) {
    	UUID user_id = UUID.fromString(request.queryParams("user_id"));
    	
    	boolean status = dao.liberarReceitas(user_id);
    	
    	JSONObject resposta = new JSONObject();
    	Gson gson = new Gson();
    	resposta.put("success", status);
    	return gson.toJson(resposta);
    }
}