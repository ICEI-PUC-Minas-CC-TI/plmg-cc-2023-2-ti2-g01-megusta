package service;

import org.postgresql.util.PSQLException;

import com.google.gson.Gson;

import dao.ReceitaDAO;
import model.Receita;
import model.Usuario;
import spark.Request;
import spark.Response;

public class ReceitaService {
	private ReceitaDAO dao = new ReceitaDAO();
	public String insereReceita(Request request, Response response, Usuario user) {

		// Linhas que recebem os parâmetros da URL
	    String titulo = request.queryParams("titulo");
	    String desc = request.queryParams("desc");
	    double porcao = Double.parseDouble(request.queryParams("porcao"));
	    double custo = Double.parseDouble(request.queryParams("custo"));
	    double prepTime = Double.parseDouble(request.queryParams("prepTime"));
	    String autor = request.queryParams("autor");
	    String dificuldade = request.queryParams("dificuldade");
	    String preparo = request.queryParams("preparo");
	    String valorNut = request.queryParams("valorNut");
	    
	    String resp = "";
	    Receita rec = new Receita( titulo,  desc,  autor,  porcao,  custo,  dificuldade,
				 preparo,  prepTime,  valorNut);
	    if(dao.inserirReceita(rec, user) == true) {
            resp = "Usuario (" + titulo + ") inserido!";
            response.status(201); // 201 Created
		} else {
			resp = "Usuario (" + titulo + ") não inserido!";
			response.status(404); // 404 Not found
		}
	    
	    return resp;
	}
}
