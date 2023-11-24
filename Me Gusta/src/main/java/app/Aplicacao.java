package app;

import service.AnotacaoService;
import service.IngredienteService;
import service.ReceitaService;
import service.RestricaoService;
import service.UsuarioService;

import static spark.Spark.*;
import static spark.Spark.port;
import static spark.Spark.post;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;

import model.Receita;

import static spark.Spark.get;

public class Aplicacao {
    
    public static void main(String[] args) throws Exception {
    	
    	// Objetos do tipo EntidadeService
		UsuarioService usuarioService = new UsuarioService();
		IngredienteService ingredienteService = new IngredienteService();
		RestricaoService restricaoService = new RestricaoService();
		ReceitaService receitaService = new ReceitaService();
		AnotacaoService anotacaoService = new AnotacaoService();
		
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
        
        // Requisição para registrar as restrições usuário
        post("/restricoesuser/insert", (request, response) -> {  return usuarioService.salvaRestricoes(request, response);});
        
        // Requisição para atualizar as restrições usuário
        post("/restricoesuser/update", (request, response) -> {  return usuarioService.atualizaRestricoes(request, response);});
        
        // Requisição para resetar as restrições usuário
        post("/restricoesuser/reset", (request, response) -> {  return usuarioService.resetRestricoes(request, response);});

        // Requisição para registrar os ingredientes desejados por um usuário
        post("/selecionados/insert", (request, response) -> {  return usuarioService.salvaIngredientes(request, response);});
        
        // Requisição para atualizar os ingredientes desejados por um usuário
        post("/selecionados/update", (request, response) -> {  return usuarioService.atualizaIngredientes(request, response);});
        
        // Requisição para resetar os ingredientes desejados por um usuário
        post("/selecionados/reset", (request, response) -> {  return usuarioService.resetIngredientes(request, response);});
 
        // Requisição para registrar os ingredientes não desejados por um usuário
        post("/banidos/insert", (request, response) -> {  return usuarioService.salvaIngredientesX(request, response);});
        
        // Requisição para atualizar os ingredientes não desejados por um usuário
        post("/banidos/update", (request, response) -> {  return usuarioService.atualizaIngredientesX(request, response);});
        
        // Requisição para resetar os ingredientes não desejados por um usuário
        post("/banidos/reset", (request, response) -> {  return usuarioService.resetIngredientesX(request, response);});        
        
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
	    
	    post("/obterUsuario1", (request, response) -> {	        
	        String user = request.queryParams("user");
	        System.out.println("User: " + user);
	    	response.type("application/json");
	      
	        return usuarioService.buscaUsuarioJSON(user);
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
	    
	    // Requisição para alterar um usuário
	    post("/usuario/update", (request, response) -> { 
	    	response.type("application/json");
	    	System.out.println("Opa! Está tentando editar um usuário?");
	    	return usuarioService.alteraUser(request, response);
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
        // Requisição para registrar ingrediente
        post("/ingrediente/insert", (request, response) -> {  
        	
        	return ingredienteService.insereIngrediente(request, response);
        });
        
     // Requisição para listar os ingredientes
	    get("/ingrediente", (request, response) -> {
	        response.type("application/json");
	        return ingredienteService.obterIngredientes();
	    });
	    
	     // Requisição para listar os ingredientes selecionados
		    get("/ingrediente/selecionados", (request, response) -> {
		    	
		        response.type("application/json");
		        return ingredienteService.obterSelecionados(request, response);
		    });
		    
	     // Requisição para listar os ingredientes banidos
		    get("/ingrediente/banidos", (request, response) -> {
		    	System.out.println("Banidos");
		        response.type("application/json");
		        return ingredienteService.obterBanidos(request, response);
		    });		    
	    
	    // Requisição para exibir os ingredientes ordenados em ordem alfabética
	    get("/ingrediente/ordenar", (request, response) -> {
	        String orderBy = request.queryParams("orderBy");

	        response.type("application/json"); 
	        return ingredienteService.obterIngredientesOrdenados(orderBy);
	    });
	    
	    // Requisição para alterar um ingrediente
	    post("/ingrediente/update", (request, response) -> { 
	    	
	    	response.type("application/json");
	    	return ingredienteService.alteraIngrediente(request, response);
	    });
	    
        // Requisição para buscar um ingrediente
	    post("/obterIngrediente", (request, response) -> {
	    	
	        String nome = request.queryParams("alterarIngrediente");
	    	response.type("application/json");
	      
	        return ingredienteService.buscaIngrediente(nome);
	    });
	    
	    // Requisição de pesquisa pela caixa de texto
	    get("/ingrediente/pesquisar", (request, response) -> {
	        String termoPesquisa = request.queryParams("termo");

	        response.type("application/json"); 
	        return ingredienteService.obterResultadosPesquisa(termoPesquisa);
	    });
	    
	    // Requisição para deletar um ingrediente
	    post("/deletarIngrediente", (request, response) -> { 
	    	
	    	response.type("application/json");
	    	return ingredienteService.deletarIngrediente(request, response);
	    });
	    
	    // Requisição para deletar todos os ingredientes
	    post("/deletarTodosIngredientes", (request, response) -> { 
	    
	    	response.type("application/json");
	    	return ingredienteService.deletarTodos(request, response);
	    });
	    
	    /*------ REQUISIÇÕES DE RESTRIÇÃO ------*/
	    
        // Requisição para registrar restrição
        post("/restricao/insert", (request, response) -> { 
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
	    
	    /*------ REQUISIÇÕES DE RECEITA ------*/
        // Requisição para registrar receita
        post("/receita/insert", (request, response) -> {  
        	System.out.println("Atravessei!");
        	return receitaService.insereReceita(request, response);
        });
        
        // Requisição para listar as receitas
	    get("/receita", (request, response) -> {
	    	//System.out.println("Atravessei");
	        response.type("application/json");
	        UUID user_id = UUID.fromString(request.queryParams("userId"));
	        //System.out.println("Id recuperado.");
	        List<Receita> receitas = receitaService.obterReceitas();
	        //System.out.println("Receitas length: " + receitas.size());
	        //System.out.println("Receitas obtidas, vamos filtrá-las.");
	        List<Receita> receitasFiltradas = new ArrayList<>();
	        try {
	        	receitasFiltradas = receitaService.filtrarReceitas(receitas, user_id);
	        } catch(Exception e) {
	        	e.printStackTrace();
	        }
	        //System.out.println("Receitas filtradas length: " + receitasFiltradas.size());
	        //System.out.println("Voltando");
            Gson gson = new Gson();
            return gson.toJson(receitasFiltradas);
	    });
	    
	    // Requisição para exibir as receitas ordenadas
	    get("/receita/ordenar", (request, response) -> {
	        String orderBy = request.queryParams("orderBy");
	        UUID user_id = UUID.fromString(request.queryParams("user_id"));
	        response.type("application/json"); 
	        
	        List<Receita> receitasOrdenadas = receitaService.obterReceitasOrdenadas(orderBy);
	        List<Receita> receitasFiltradas = receitaService.filtrarReceitas(receitasOrdenadas, user_id);
	        
	        return receitasFiltradas;
	    });
	    
	    // Requisição para alterar uma receita
	    post("/receita/update", (request, response) -> { 
	    	System.out.println("Atravessei para alterar receitas");
	    	response.type("application/json");
	    	return receitaService.alteraReceita(request, response);
	    });
	    
        // Requisição para buscar uma receita para alterala
	   get("/obterReceita", (request, response) -> {
	    	//System.out.println("Atravessei");
	    	String idString = request.queryParams("alterarReceita");
	    	UUID id = UUID.fromString(idString);
	    	response.type("application/json");
	      
	        return receitaService.buscaReceita(id);
	    });
	    
	    // Requisição para buscar uma receita para exibi-la
	    get("/buscarReceita", (request, response) -> {
	    	System.out.println("Atravessei");
	    	String idString = request.queryParams("receitaId");
	    	UUID id = UUID.fromString(idString);
	    	response.type("application/json");
	    	System.out.println("ID: " + id);
	        return receitaService.buscaReceita(id);
	    });	 
	    
	    // Requisição para pegar as receitas do usuário
	    get("/minhasReceitas", (request, response) -> {
	    	System.out.println("Atravessei para pegar minhas receitas");
	    	String idString = request.queryParams("userId");
	    	UUID id = UUID.fromString(idString);
	    	response.type("application/json");
	        
	        return receitaService.buscaMinhasReceitas(id);
	    });	 	    
	    
	    // Requisição de pesquisa pela caixa de texto
	    get("/receita/pesquisar", (request, response) -> {
	        String termoPesquisa = request.queryParams("termo");

	        response.type("application/json"); 
	        return receitaService.obterResultadosPesquisa(termoPesquisa);
	    });
	    
	    // Requisição para deletar uma receita
	    post("/deletarReceita", (request, response) -> { 
	    	
	    	response.type("application/json");
	    	return receitaService.deletarReceita(request, response);
	    });
	    
	    // Requisição para salvar uma receita
	    post("/receita/save/", (request, response) -> { 
	    	
	    	response.type("application/json");
	    	return receitaService.salvaReceita(request, response);
	    });	    
	    
	    // Requisição para deletar todas as receitas
	    post("/deletarTodasReceitas", (request, response) -> { 
	    
	    	response.type("application/json");
	    	return receitaService.deletarTodas(request, response);
	    });
	    
	    // Requisição para inserir avaliação a uma receita
	    post("/review/insert", (request, response) -> { 
	    	System.out.println("Atravessei para avaliar");
	    	response.type("application/json");
	    	return receitaService.avalia(request, response);
	    });
	    
	    /*------ REQUISIÇÕES DE ANOTAÇÃO ------*/
	    
        // Requisição para registrar anotação
        post("/anotacao/insert", (request, response) -> { 
        	return anotacaoService.insereAnotacao(request, response);
        });
        
        // Requisição para listar as anotações
	    get("/anotacao/:id", (request, response) -> {
	        response.type("application/json");
	        return anotacaoService.obterAnotacoes(request, response);
	    });
	    
	    // Requisição para deletar uma anotação
	    post("/deletar/:note", (request, response) -> { 
	    	
	    	response.type("application/json");
	    	return anotacaoService.deletaAnotacao(request, response);
	    });
	    
	    // Requisição para deletar todas as anotacoes
	    post("/deletarTodasAnotacoes", (request, response) -> { 
	    
	    	response.type("application/json");
	    	return anotacaoService.deletarTodas(request, response);
	    });
    }
}
