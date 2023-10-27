package dao;

import java.sql.*;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//import app.PreparedStatement;
//import app.SQLException;
import model.Ingrediente;

public class IngredienteDAO extends DAO {

    public IngredienteDAO() {
        super();
        conectar();
    }

    public boolean inserirIngrediente(Ingrediente ingrediente) {
        boolean status = false;
        try {
            
            PreparedStatement ps = conexao.prepareStatement(
                    "INSERT INTO \"MG\".ingrediente (id, nome, categoria, nutritionalinfo, imagem) VALUES (?, ?, ?, ?, ?, ?)");

            ps.setObject(1, ingrediente.getId());
            ps.setString(2, ingrediente.getNome() != null ? ingrediente.getNome() : "");
            ps.setString(3, ingrediente.getCategoria() != null ? ingrediente.getCategoria() : "");

            // Convertendo o objeto JSON para uma string antes de inserir no banco
            String nutritionalinfoString = ingrediente.getNutritionalinfo() != null ? ingrediente.getNutritionalinfo().toJSONString() : null;

            // Utilizando setObject para o tipo JSONB
            ps.setObject(4, nutritionalinfoString, Types.OTHER);

            ps.setBytes(5, ingrediente.getImagem() != null ? ingrediente.getImagem() : new byte[0]);

            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return status;
    }

    public boolean atualizarIngrediente(Ingrediente ingrediente) {
        boolean status = false;
        try {
            PreparedStatement ps = conexao.prepareStatement(
                    "UPDATE \"MG\".ingrediente SET nome = ?, categoria = ?, nutritionalinfo = ?, imagem = ? WHERE id = ?");

            ps.setString(1, ingrediente.getNome());
            ps.setString(2, ingrediente.getCategoria());

            // Convertendo o objeto JSON para uma string antes de atualizar no banco
            String nutritionalinfoString = ingrediente.getNutritionalinfo() != null ? ingrediente.getNutritionalinfo().toJSONString() : null;

            // Utilizando setObject para o tipo JSONB
            ps.setObject(3, nutritionalinfoString, Types.OTHER);

            ps.setBytes(4, ingrediente.getImagem());
            ps.setObject(5, ingrediente.getId());

            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
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
    


    public List<Ingrediente> getIngredientesOrdenadosPorNome() {
        List<Ingrediente> ingredientes = getIngredientes(); // Suponha que você tenha um método para obter todos os ingredientes
        quicksortPorNome(ingredientes, 0, ingredientes.size() - 1);
        return ingredientes;
    }

    private void quicksortPorNome(List<Ingrediente> ingredientes, int inicio, int fim) {
        if (inicio < fim) {
            int indiceParticao = particionarPorNome(ingredientes, inicio, fim);
            quicksortPorNome(ingredientes, inicio, indiceParticao - 1);
            quicksortPorNome(ingredientes, indiceParticao + 1, fim);
        }
    }

    private int particionarPorNome(List<Ingrediente> ingredientes, int inicio, int fim) {
        String pivo = ingredientes.get(fim).getNome();
        int i = inicio - 1;

        for (int j = inicio; j < fim; j++) {
            if (ingredientes.get(j).getNome().compareToIgnoreCase(pivo) < 0) {
                i++;
                Collections.swap(ingredientes, i, j);
            }
        }

        Collections.swap(ingredientes, i + 1, fim);
        return i + 1;
    }
    
    public Ingrediente buscarIngredientePorNome(String nome) {
        List<Ingrediente> ingredientesOrdenados = getIngredientesOrdenadosPorNome();
        return buscaBinariaPorNome(ingredientesOrdenados, nome, 0, ingredientesOrdenados.size() - 1);
    }

    private Ingrediente buscaBinariaPorNome(List<Ingrediente> ingredientes, String nome, int inicio, int fim) {
        if (inicio <= fim) {
            int meio = (inicio + fim) / 2;
            String nomeMeio = ingredientes.get(meio).getNome();

            if (nomeMeio.equalsIgnoreCase(nome)) {
                return ingredientes.get(meio);
            } else if (nomeMeio.compareToIgnoreCase(nome) < 0) {
                return buscaBinariaPorNome(ingredientes, nome, meio + 1, fim);
            } else {
                return buscaBinariaPorNome(ingredientes, nome, inicio, meio - 1);
            }
        }

        return null; // Ingrediente não encontrado
    }


    private JSONObject parseJson(String json) {
        try {
            return (JSONObject) new JSONParser().parse(json);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
