package dao;

import java.sql.*;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//import app.PreparedStatement;
//import app.SQLException;
import model.Ingrediente;
import model.Restricao;

public class IngredienteDAO extends DAO {

    public IngredienteDAO() {
        super();
        conectar();
    }

    public boolean inserirIngrediente(Ingrediente ingrediente, List<UUID> restricoesList) {
    	System.out.println("É a hora da verdade!");
        boolean status = false;
        try {
            // Insere o ingrediente na tabela ingrediente
            PreparedStatement ps = conexao.prepareStatement(
                "INSERT INTO \"MG\".ingrediente (id, nome, categoria, nutritionalinfo, imagem) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
            UUID ingredienteId = ingrediente.getId();
            ps.setObject(1, ingredienteId);
            ps.setString(2, ingrediente.getNome() != null ? ingrediente.getNome() : "");
            ps.setString(3, ingrediente.getCategoria() != null ? ingrediente.getCategoria() : "");

            // Convertendo o objeto JSON para uma string antes de inserir no banco
            String nutritionalinfoString = ingrediente.getNutritionalinfo() != null ? ingrediente.getNutritionalinfo().toJSONString() : null;

            // Utilizando setObject para o tipo JSONB
            ps.setObject(4, nutritionalinfoString, Types.OTHER);
            ps.setBytes(5, ingrediente.getImagem() != null ? ingrediente.getImagem() : new byte[0]);
            ps.executeUpdate();
            System.out.println("Tenho fome de UUID's!");
            for (UUID restrictionId : restricoesList) {
            	System.out.println("Iterando para a restrição: " + restrictionId);
                try { PreparedStatement psRestricao = conexao.prepareStatement(
                        "INSERT INTO \"MG\".ingredienterestricao (ingredient_id, restriction_id) VALUES (?, ?)");

                    psRestricao.setObject(1, ingredienteId);
                    psRestricao.setObject(2, restrictionId);
                    psRestricao.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

            status = true;
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return status;
    }
    
    public boolean atualizarIngrediente(Ingrediente ingrediente, List<UUID> restricoesList) {
        boolean status = false;
        try {
            // Insere o ingrediente na tabela ingrediente
            PreparedStatement ps = conexao.prepareStatement(
            		"UPDATE \"MG\".ingrediente SET nome = ?, categoria = ?, nutritionalinfo = ?, imagem = ? WHERE id = ?");

            ps.setString(1, ingrediente.getNome() != null ? ingrediente.getNome() : "");
            ps.setString(2, ingrediente.getCategoria() != null ? ingrediente.getCategoria() : "");

            // Convertendo o objeto JSON para uma string antes de inserir no banco
            String nutritionalinfoString = ingrediente.getNutritionalinfo() != null ? ingrediente.getNutritionalinfo().toJSONString() : null;

            // Utilizando setObject para o tipo JSONB
            ps.setObject(3, nutritionalinfoString, Types.OTHER);
            ps.setBytes(4, ingrediente.getImagem() != null ? ingrediente.getImagem() : new byte[0]);
            
            UUID ingredienteId = ingrediente.getId();
            ps.setObject(5, ingredienteId);
            ps.executeUpdate();
            ps.close();
            
            // Remove todas as associações existentes na tabela ingredienteRestricao
            PreparedStatement psDelete = conexao.prepareStatement(
                "DELETE FROM \"MG\".ingredienterestricao WHERE ingredient_id = ?");
            psDelete.setObject(1, ingredienteId);
            psDelete.executeUpdate();
            psDelete.close();
            
            for (UUID restrictionId : restricoesList) {
            	System.out.println("Iterando para a restrição: " + restrictionId);
                try { PreparedStatement psRestricao = conexao.prepareStatement(
                        "INSERT INTO \"MG\".ingredienterestricao (ingredient_id, restriction_id) VALUES (?, ?)");

                    psRestricao.setObject(1, ingredienteId);
                    psRestricao.setObject(2, restrictionId);
                    psRestricao.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

            status = true;
           
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return status;
    }

    public boolean excluirIngrediente(String nome) {
        boolean status = false;
        try {
            PreparedStatement ps = conexao.prepareStatement("DELETE FROM \"MG\".ingrediente WHERE nome = ?");
            ps.setObject(1, nome);
            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }

    public List<Ingrediente> getIngredientes() {
        List<Ingrediente> ingredientes = new ArrayList<>();

        try {
            Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery("SELECT * FROM \"MG\".ingrediente");
            while (rs.next()) {
                UUID id = (UUID) rs.getObject("id");
                String nome = rs.getString("nome");
                String categoria = rs.getString("categoria");

                // Recuperando a string JSON do banco
                String nutritionalinfoString = rs.getString("nutritionalinfo");

                // Convertendo a string JSON armazenada no banco para um objeto JSON
                JSONObject nutritionalinfo = parseJson(nutritionalinfoString);

                byte[] imagem = rs.getBytes("imagem");

                Ingrediente ingrediente = new Ingrediente(id, nome, categoria, nutritionalinfo, imagem);
                ingredientes.add(ingrediente);
            }
            st.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ingredientes;
    }
    
    public List<Ingrediente> sort(List<Ingrediente> ingredientList) {
        int n = ingredientList.size();
        return quicksort(ingredientList, 0, n - 1);
    }
    
    public List<Ingrediente> sortCategoria(List<Ingrediente> ingredientList) {
        int n = ingredientList.size();
        return quicksortCategoria(ingredientList, 0, n - 1);
    }

    // Método swap que ordena pelo nome
    private List<Ingrediente> quicksort(List<Ingrediente> ingredientList, int esq, int dir) {
        int i = esq, j = dir;
        String pivo = ingredientList.get((dir + esq) / 2).getNome();
        while (i <= j) {
            while (ingredientList.get(i).getNome().compareTo(pivo) < 0) i++;
            while (ingredientList.get(j).getNome().compareTo(pivo) > 0) j--;
            if (i <= j) {
                swap(ingredientList, i, j);
                i++;
                j--;
            }
        }
        if (esq < j) quicksort(ingredientList, esq, j);
        if (i < dir) quicksort(ingredientList, i, dir);

        return ingredientList;
    }
    
    // Método quicksort que ordena pela categoria e em um segundo caso pelo nome
    private List<Ingrediente> quicksortCategoria(List<Ingrediente> ingredientList, int esq, int dir) {
        int i = esq, j = dir;
        String pivo = ingredientList.get((dir + esq) / 2).getCategoria();
        String pivoNome = ingredientList.get((dir + esq) / 2).getNome();
        while (i <= j) {
            while ((ingredientList.get(i).getCategoria().compareTo(pivo) < 0) || (ingredientList.get(i).getCategoria().equals(pivo) && ingredientList.get(i).getNome().compareTo(pivoNome) < 0)) i++;
            while ((ingredientList.get(j).getCategoria().compareTo(pivo) > 0) || (ingredientList.get(j).getCategoria().equals(pivo) && ingredientList.get(j).getNome().compareTo(pivoNome) > 0)) j--;
            if (i <= j) {
                swap(ingredientList, i, j);
                i++;
                j--;
            }
        }
        if (esq < j) quicksortCategoria(ingredientList, esq, j);
        if (i < dir) quicksortCategoria(ingredientList, i, dir);

        return ingredientList;
    }

    private void swap(List<Ingrediente> ingredientList, int i, int j) {
        Ingrediente temp = ingredientList.get(i);
        ingredientList.set(i, ingredientList.get(j));
        ingredientList.set(j, temp);
    }
    
	/* Método Pesquisa
	 * Adiciona no array de resultado as strings que contém o termo de pesquisa
	 */
    public List<Ingrediente> getPesquisa(String termoPesquisa) {
  
        List<Ingrediente> todosIngredientes = getIngredientes();
        List<Ingrediente> resultadosPesquisa = new ArrayList<>();

        // Lógica de pesquisa - comparando o termo de pesquisa com cada usuario
        for (Ingrediente ingrediente : todosIngredientes) {
            if (ingredienteToString(ingrediente).toLowerCase().contains(termoPesquisa.toLowerCase())) {
                resultadosPesquisa.add(ingrediente);
            }
        }

        return resultadosPesquisa;
    }
    
	/* Método GET
	 * Faz a conexão com o BD e buscar um ingrediente específico
	 */
    public Ingrediente buscarIngrediente(String name) {
        Ingrediente ingrediente = null;
        try {
            Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery("SELECT * FROM \"MG\".ingrediente");

            // rs.next() para posicionar o cursor no primeiro resultado
            while (rs.next()) {
                String nome = rs.getString("nome");
               
                if (nome.equals(name)) {
                    UUID id = (UUID) rs.getObject("id");
                    String categoria = rs.getString("categoria");
                    // Recuperando a string JSON do banco
                    String nutritionalinfoString = rs.getString("nutritionalinfo");

                    // Convertendo a string JSON armazenada no banco para um objeto JSON
                    JSONObject nutritionalinfo = parseJson(nutritionalinfoString);

                    byte[] imagem = rs.getBytes("imagem");

                    ingrediente = new Ingrediente(id, nome, categoria, nutritionalinfo, imagem);
                    break;  // Não precisa continuar após encontrar a requisição
                }
            }
            st.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ingrediente;
    }
    
	/* Método de Verificação
	 * Faz a conexão com o BD e buscar um ingrediente específico, retornando se ele existe ou nao
	 */
    public boolean ingredienteExiste(String name) {
        boolean status = false;

        try (PreparedStatement ps = conexao.prepareStatement("SELECT * FROM \"MG\".ingrediente WHERE nome = ?")) {
            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                status = rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return status;
    }
    
    /* Deletar Todos Ingredientes
     * Deleta todas as linhas da tabela ingrediente
     */
    public boolean apagarTodosIngredientes() {
        boolean status = false;
        try {
            PreparedStatement ps = conexao.prepareStatement("DELETE FROM \"MG\".ingrediente");
            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }
    
    // Função para verificar se o ingrediente já foi cadastrada (método auxiliar de inserção)
    public boolean verificarIngrediente(String ingrediente) {
    	boolean status = false;
    	List<Ingrediente> ingredientes = getIngredientes();
    	
    	for(Ingrediente ingrediente1 : ingredientes) {
    		
    		if(ingrediente1.getNome().equals(ingrediente)) {
    			status = true;
    		}
    	}
    	
    	return status;
    }

    // Função para converter um ingrediente em uma string (método auxiliar da pesquisa)
    private String ingredienteToString(Ingrediente ingrediente) {
        // Concatene todos os atributos relevantes do ingrediente em uma string
        return ingrediente.getNome() +
               ingrediente.getCategoria();
    }

    private JSONObject parseJson(String json) {
        try {
            return (JSONObject) new JSONParser().parse(json);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
