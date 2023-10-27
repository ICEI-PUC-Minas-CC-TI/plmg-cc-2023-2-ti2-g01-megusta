package app;

import service.RestricaoService;
import service.UsuarioService;

import static spark.Spark.*;
import static spark.Spark.port;
import static spark.Spark.post;

import static spark.Spark.get;

public class Aplicacao {
    
    public static void main(String[] args) throws Exception {
    	
    	// Objetos do tipo EntidadeService
		UsuarioService usuarioService = new UsuarioService();
		//IngredienteService ingredienteService = new IngredienteService();
		RestricaoService restricaoService = new RestricaoService();
		//ReceitaService receitaService = new ReceitaService();
		//AnotacaoService anotacaoService = new AnotacaoService();
		
	    port(6789);
        // Configuração CORS
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
            response.header("Access-Control-Allow-Credentials", "true");
        });

        /*------ REQUISIÇÕES DE USUÁRIO ------*/
        // Requisição para registrar usuário
        post("/usuario/insert", (request, response) -> {  return usuarioService.insereUsuario(request, response);});

        // Requisição para registrar superuser
        post("/usuario/insertS", (request, response) -> { return usuarioService.insereSuperuser(request, response);});

        // Requisição para autenticar 
        get("/usuario/autenticar", (request, response) -> { 

        	response.type("application/json");
        	return usuarioService.autenticaUsuario(request, response);
        });
        
        // Requisição para listar os usuários
	    get("/usuario", (request, response) -> {
	        response.type("application/json");
	        return usuarioService.obterUsuariosJSON();
	    });
	    
	    // Requisição para exibir os usuários ordenados em ordem alfabética
	    get("/usuario/ordenar", (request, response) -> {
	        String orderBy = request.queryParams("orderBy");

	        response.type("application/json");
	        return usuarioService.obterUsuariosOrdenadosJSON(orderBy);
	    });
	    
	    // Requisição de pesquisa pela caixa de texto
	    get("/usuario/pesquisar", (request, response) -> {
	        String termoPesquisa = request.queryParams("termo");

	        response.type("application/json"); 
	        return usuarioService.obterResultadosPesquisaJSON(termoPesquisa);
	    });
	    
        // Requisição para buscar um usuario
	    post("/obterUsuario", (request, response) -> {	        
	        String user = request.queryParams("alterarUser");
	    	response.type("application/json");
	      
	        return usuarioService.buscaUsuarioJSON(user);
	    });
	    
	    // Requisição para alterar um superusuário
	    post("/usuario/updateS", (request, response) -> { 
	    	response.type("application/json");
	    	return usuarioService.alteraSuperuser(request, response);
	    });
	    
	    // Requisição para deletar um usuário
	    post("/deletarUsuario", (request, response) -> { 
	    	
	    	response.type("application/json");
	    	return usuarioService.deletarUser(request, response);
	    });
	    
	    // Requisição para deletar todos os usuários
	    post("/deletarTodos", (request, response) -> { 
	    
	    	response.type("application/json");
	    	return usuarioService.deletarTodos(request, response);
	    });
	    
	    /*------ REQUISIÇÕES DE INGREDIENTE ------*/
	    
	    /*------ REQUISIÇÕES DE RESTRIÇÃO ------*/
	    
        // Requisição para registrar restrição
        post("/restricao/insert", (request, response) -> { 
        	System.out.println("Cheguei aqui!");
        	return restricaoService.insereRestricao(request, response);
        });
        
        // Requisição para listar as restrições
	    get("/restricao", (request, response) -> {
	        response.type("application/json");
	        return restricaoService.obterRestricoes();
	    });
	    
	    // Requisição para exibir as restrições ordenados em ordem alfabética
	    get("/restricao/ordenar", (request, response) -> {
	        String orderBy = request.queryParams("orderBy");

	        response.type("application/json"); 
	        return restricaoService.obterRestricoesOrdenadas(orderBy);
	    });
	    
	    // Requisição para alterar uma restrição
	    post("/restricao/update", (request, response) -> { 
	    	
	    	response.type("application/json");
	    	return restricaoService.alteraRestricao(request, response);
	    });
	    
        // Requisição para buscar uma restrição
	    post("/obterRestricao", (request, response) -> {
	    	
	        String nome = request.queryParams("alterarRestricao");
	    	response.type("application/json");
	      
	        return restricaoService.buscaRestricao(nome);
	    });
	    
	    // Requisição de pesquisa pela caixa de texto
	    get("/restricao/pesquisar", (request, response) -> {
	        String termoPesquisa = request.queryParams("termo");

	        response.type("application/json"); 
	        return restricaoService.obterResultadosPesquisa(termoPesquisa);
	    });
	    
	    // Requisição para deletar uma restrição
	    post("/deletarRestricao", (request, response) -> { 
	    	
	    	response.type("application/json");
	    	return restricaoService.deletarRestricao(request, response);
	    });
	    
	    // Requisição para deletar todas as restrições
	    post("/deletarTodas", (request, response) -> { 
	    
	    	response.type("application/json");
	    	return restricaoService.deletarTodas(request, response);
	    });
    }
}
