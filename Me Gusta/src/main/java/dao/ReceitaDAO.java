package dao;

import java.sql.*;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.postgresql.util.PSQLException;

import com.google.gson.Gson;

//import app.PreparedStatement;
//import app.SQLException;
import model.Ingrediente;
import model.Receita;
import model.Restricao;

public class ReceitaDAO extends DAO {
    public ReceitaDAO() {
    	super();
        conectar();
    }

    public boolean inserirReceita(Receita receita, List<UUID> ingredientesList, UUID usuario_id, int[] quantidades) {
    	System.out.println("Quase lá!");
    	int i = 0;
        boolean status = false;
        try {
            // Insere o ingrediente na tabela receita
            PreparedStatement ps = conexao.prepareStatement(
                "INSERT INTO receita (id, usuario_id, titulo, descricao, imagem, instrucao, tempopreparo, porcao, dificuldade, custo, nutritionalinfo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
            UUID receitaId = receita.getId();
            ps.setObject(1, receitaId);
            ps.setObject(2, usuario_id);
            ps.setString(3, receita.getTitulo() != null ? receita.getTitulo() : "");
            ps.setString(4, receita.getDescricao() != null ? receita.getDescricao() : "");
            ps.setBytes(5, receita.getImagem() != null ? receita.getImagem() : new byte[0]);
            String[] instrucaoArray = receita.getInstrucao();

	        // Supondo que 'conn' é sua conexão com o banco de dados
	        Array instrucaoSqlArray = conexao.createArrayOf("VARCHAR", instrucaoArray);
	
	        // Agora você pode definir o valor no PreparedStatement
	        ps.setArray(6, instrucaoSqlArray);

            ps.setInt(7, receita.getTempoPreparo());
            ps.setString(8, receita.getPorcao() != null ? receita.getPorcao() : "");
            ps.setString(9, receita.getDificuldade() != null ? receita.getDificuldade() : "");
            ps.setDouble(10, receita.getCusto());
            // Convertendo o objeto JSON para uma string antes de inserir no banco
            String nutritionalinfoString = receita.getNutritionalinfo() != null ? receita.getNutritionalinfo().toJSONString() : null;

            // Utilizando setObject para o tipo JSONB
            ps.setObject(11, nutritionalinfoString, Types.OTHER);
            ps.executeUpdate();
        
            for (UUID ingredientId : ingredientesList) {
        
                try { PreparedStatement psIngrediente = conexao.prepareStatement(
                        "INSERT INTO receitaingredientes (recipe_id, ingredient_id, quantidade) VALUES (?, ?, ?)");

                	psIngrediente.setObject(1, receitaId);
                	psIngrediente.setObject(2, ingredientId);
                    psIngrediente.setInt(3, quantidades[i]);                
                    psIngrediente.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                
                i++;
            }

            status = true;
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return status;
    }
    
    public boolean atualizarReceita(Receita receita, List<UUID> ingredientesList, UUID usuario_id, int[] quantidades) {
    	System.out.println("Quase lá para atualizar");
    	int i = 0;
        boolean status = false;
        try {
            // Atualiza
            PreparedStatement ps = conexao.prepareStatement(
            		"UPDATE receita SET titulo = ?, descricao = ?, instrucao = ?, tempoPreparo = ?, custo = ?, dificuldade = ?, porcao = ?, imagem = ?, nutritionalinfo = ? WHERE id = ? AND usuario_id = ?");

            ps.setString(1, receita.getTitulo() != null ? receita.getTitulo() : "");
            ps.setString(2, receita.getDescricao() != null ? receita.getDescricao() : "");
            ps.setBytes(8, receita.getImagem() != null ? receita.getImagem() : new byte[0]);
            String[] instrucaoArray = receita.getInstrucao();

	        // Supondo que 'conn' é sua conexão com o banco de dados
	        Array instrucaoSqlArray = conexao.createArrayOf("VARCHAR", instrucaoArray);
	
	        // Agora você pode definir o valor no PreparedStatement
	        ps.setArray(3, instrucaoSqlArray);

            ps.setInt(4, receita.getTempoPreparo());
            ps.setString(7, receita.getPorcao() != null ? receita.getPorcao() : "");
            ps.setString(6, receita.getDificuldade() != null ? receita.getDificuldade() : "");
            ps.setDouble(5, receita.getCusto());           
            // Convertendo o objeto JSON para uma string antes de inserir no banco
            String nutritionalinfoString = receita.getNutritionalinfo() != null ? receita.getNutritionalinfo().toJSONString() : null;
            // Utilizando setObject para o tipo JSONB
            ps.setObject(9, nutritionalinfoString, Types.OTHER);
            
            UUID receitaId = receita.getId();
            ps.setObject(10, receitaId);
            ps.setObject(11, usuario_id);
            ps.executeUpdate();
            ps.close();
            
            // Remove todas as associações existentes na tabela receitaingredientes
            PreparedStatement psDelete = conexao.prepareStatement(
                "DELETE FROM receitaingredientes WHERE recipe_id = ?");
            psDelete.setObject(1, receitaId);
            psDelete.executeUpdate();
            psDelete.close();
            
            for (UUID ingredientId : ingredientesList) {
                
                try { PreparedStatement psIngrediente = conexao.prepareStatement(
                        "INSERT INTO receitaingredientes (recipe_id, ingredient_id, quantidade) VALUES (?, ?, ?)");

                	psIngrediente.setObject(1, receitaId);
                	psIngrediente.setObject(2, ingredientId);
                    psIngrediente.setInt(3, quantidades[i]);                
                    psIngrediente.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                
                i++;
            }

            status = true;
           
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return status;
    }
    
    public List<Receita> filtrarReceitas(List<Receita> receitas, UUID user_id) throws Exception {
    	System.out.println("Dentro do filtro");
        List<Receita> restricoesFiltradas = new ArrayList<>();
        int count = 0;

        try {
				PreparedStatement ps =  conexao.prepareStatement("select * from receita where id not in(select recipe_id from receitaingredientes where ingredient_id in(select ingredient_id from ingredienterestricao where restriction_id in (select restriction_id from  usuariorestricao where user_id=?)))");
				ps.setObject(1, user_id);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            UUID id = (UUID) rs.getObject("id");
                            UUID usuario_id = (UUID) rs.getObject("usuario_id");
                            String titulo = rs.getString("titulo");
                            String descricao = rs.getString("descricao");
                            Array instrucaoArray = rs.getArray("instrucao");
                            String[] instrucao = {""};

                            if (instrucaoArray != null) {
                                instrucao = (String[]) instrucaoArray.getArray();
                            }

                            int tempoPreparo = rs.getInt("tempoPreparo");
                            String porcao = rs.getString("porcao");
                            String dificuldade = rs.getString("dificuldade");
                            double custo = rs.getDouble("custo");
                            String nutritionalinfoString = rs.getString("nutritionalinfo");
                            JSONObject nutritionalinfo = parseJson(nutritionalinfoString);
                            byte[] imagem = rs.getBytes("imagem");

                            Receita receita = new Receita(id, usuario_id, titulo, descricao, instrucao, tempoPreparo, porcao, dificuldade, custo, nutritionalinfo, imagem);
                            restricoesFiltradas.add(receita);
                        }
                    }
                }
             	catch (Exception e) {
            	 e.printStackTrace();}
        	
        return restricoesFiltradas;
   }
    
    public boolean excluirReceita(UUID id) {
        boolean status = false;
        try {
            PreparedStatement ps = conexao.prepareStatement("DELETE FROM receita WHERE id = ?");
            ps.setObject(1, id);
            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }

    public List<Receita> getReceitas() {
        List<Receita> receitas = new ArrayList<>();
        System.out.println("Quase lá. Pegando as receitas.");
        try {
            Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery("SELECT * FROM receita");
            while (rs.next()) {
                UUID id = (UUID) rs.getObject("id");
                UUID usuario_id = (UUID) rs.getObject("usuario_id");
                String titulo = rs.getString("titulo");
                String descricao = rs.getString("descricao");
                Array instrucaoArray = rs.getArray("instrucao");

                String[] instrucao = {""};
                
                // Se 'instrucaoArray' não for nulo, converte-o para um array Java
                if (instrucaoArray != null) {
                     instrucao = (String[]) instrucaoArray.getArray();
                }
                
                int tempoPreparo = rs.getInt("tempoPreparo");
                String porcao = rs.getString("porcao");
                String dificuldade = rs.getString("dificuldade");
                double custo = rs.getDouble("custo");
                // Recuperando a string JSON do banco
                String nutritionalinfoString = rs.getString("nutritionalinfo");

                // Convertendo a string JSON armazenada no banco para um objeto JSON
                JSONObject nutritionalinfo = parseJson(nutritionalinfoString);

                byte[] imagem = rs.getBytes("imagem");

                double avaliacao = calcularReviews(id); 

                Receita receita = new Receita(id, usuario_id, titulo, descricao, instrucao, tempoPreparo, porcao, dificuldade, custo, nutritionalinfo, imagem, avaliacao);
                receitas.add(receita);
            }
            st.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return receitas;
    }
    
    public List<Receita> buscarMinhasReceitas(UUID user_id) {
        List<Receita> receitas = new ArrayList<>();
        System.out.println("Quase lá. Pegando minhas receitas.");
        try {
            Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery("SELECT * FROM receita");
            while (rs.next()) {
            	UUID usuario_id = (UUID) rs.getObject("usuario_id");
            	if(usuario_id.toString().equals(user_id.toString())) {
                UUID id = (UUID) rs.getObject("id");
                String titulo = rs.getString("titulo");
                String descricao = rs.getString("descricao");
                Array instrucaoArray = rs.getArray("instrucao");

                String[] instrucao = {""};
                
                // Se 'instrucaoArray' não for nulo, converte-o para um array Java
                if (instrucaoArray != null) {
                     instrucao = (String[]) instrucaoArray.getArray();
                }
                
                int tempoPreparo = rs.getInt("tempoPreparo");
                String porcao = rs.getString("porcao");
                String dificuldade = rs.getString("dificuldade");
                double custo = rs.getDouble("custo");
                // Recuperando a string JSON do banco
                String nutritionalinfoString = rs.getString("nutritionalinfo");

                // Convertendo a string JSON armazenada no banco para um objeto JSON
                JSONObject nutritionalinfo = parseJson(nutritionalinfoString);

                byte[] imagem = rs.getBytes("imagem");

                double avaliacao = calcularReviews(id); 

                Receita receita = new Receita(id, usuario_id, titulo, descricao, instrucao, tempoPreparo, porcao, dificuldade, custo, nutritionalinfo, imagem, avaliacao);
                receitas.add(receita);
            	}
            }
            st.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return receitas;
    }
    
    public List<Receita> sortPreco(List<Receita> receitaList) {
        int n = receitaList.size();
        return quicksortPreco(receitaList, 0, n - 1);
    }

    // Método swap que ordena pelo nome
    private List<Receita> quicksortPreco(List<Receita> receitaList, int esq, int dir) {
        int i = esq, j = dir;
        double pivo = receitaList.get((dir + esq) / 2).getCusto();
        while (i <= j) {
            while (receitaList.get(i).getCusto() < pivo) i++;
            while (receitaList.get(j).getCusto() > pivo) j--;
            if (i <= j) {
                swap(receitaList, i, j);
                i++;
                j--;
            }
        }
        if (esq < j) quicksortPreco(receitaList, esq, j);
        if (i < dir) quicksortPreco(receitaList, i, dir);

        return receitaList;
    }
    
    public List<Receita> sortAvaliacao(List<Receita> receitaList) {
        int n = receitaList.size();
        return quicksortAvaliacao(receitaList, 0, n - 1);
    }

    // Método swap que ordena pelo nome
    private List<Receita> quicksortAvaliacao(List<Receita> receitaList, int esq, int dir) {
        int i = esq, j = dir;
        double pivo = getAvaliacao(receitaList.get((dir + esq) / 2).getId());
        while (i <= j) {
            while (getAvaliacao(receitaList.get(i).getId()) < pivo) i++;
            while (getAvaliacao(receitaList.get(j).getId()) > pivo) j--;
            if (i <= j) {
                swap(receitaList, i, j);
                i++;
                j--;
            }
        }
        if (esq < j) quicksortAvaliacao(receitaList, esq, j);
        if (i < dir) quicksortAvaliacao(receitaList, i, dir);

        return receitaList;
    }
    
    public List<Receita> sortCalorias(List<Receita> receitaList) {
        int n = receitaList.size();
        return quicksortCalorias(receitaList, 0, n - 1);
    }

    // Método swap que ordena pelo nome
    private List<Receita> quicksortCalorias(List<Receita> receitaList, int esq, int dir) {
        int i = esq, j = dir;
        double pivo = getCalorias(receitaList.get((dir + esq) / 2).getId());
        while (i <= j) {
            while (getCalorias(receitaList.get(i).getId()) < pivo) i++;
            while (getCalorias(receitaList.get(j).getId()) > pivo) j--;
            if (i <= j) {
                swap(receitaList, i, j);
                i++;
                j--;
            }
        }
        if (esq < j) quicksortCalorias(receitaList, esq, j);
        if (i < dir) quicksortCalorias(receitaList, i, dir);

        return receitaList;
    }
    
    public List<Receita> sortDificuldade(List<Receita> receitaList) {
        int n = receitaList.size();
        return quicksortDificuldade(receitaList, 0, n - 1);
    }

    // Método swap que ordena pelo nome
    private List<Receita> quicksortDificuldade(List<Receita> receitaList, int esq, int dir) {
        int i = esq, j = dir;
        int dificuldade, dificuldadeI, dificuldadeJ;
        int pivo = (esq + dir)/2;
        if(receitaList.get(pivo).getDificuldade().equals("difícil")) {
        	dificuldade = 2;
        } else if (receitaList.get(pivo).getDificuldade().equals("média")) {
        	dificuldade = 1;
        } else {
        	dificuldade = 0;
        }
        
        while (i <= j) {
            if(receitaList.get(i).getDificuldade().equals("difícil")) {
            	dificuldadeI = 2;
            } else if (receitaList.get(i).getDificuldade().equals("média")) {
            	dificuldadeI = 1;
            } else {
            	dificuldadeI = 0;
            }
            
            if(receitaList.get(j).getDificuldade().equals("difícil")) {
            	dificuldadeJ = 2;
            } else if (receitaList.get(j).getDificuldade().equals("média")) {
            	dificuldadeJ = 1;
            } else {
            	dificuldadeJ = 0;
            }
        	
            while (dificuldadeI < dificuldade) i++;
            while (dificuldadeJ > dificuldade) j--;
            if (i <= j) {
                swap(receitaList, i, j);
                i++;
                j--;
            }
        }
        
        if (esq < j) quicksortDificuldade(receitaList, esq, j);
        if (i < dir) quicksortDificuldade(receitaList, i, dir);

        return receitaList;
    }
    
    private void swap(List<Receita> recipeList, int i, int j) {
        Receita temp = recipeList.get(i);
        recipeList.set(i, recipeList.get(j));
        recipeList.set(j, temp);
    }
    
    /* getAvaliacao
     * Método utilizado para retornar a média das avaliações de cada receita
     */
    public double getAvaliacao(UUID id) {
    	double avaliacao = 0;
    	int count = 0;
        try {
            Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery("SELECT * FROM reviews");
            while (rs.next()) {
                UUID recipe_id = (UUID) rs.getObject("recipe_id");
                if(id.toString().equals(recipe_id.toString())) {
                	avaliacao += rs.getDouble("avaliacao");
                	count++;
                }
            }
            st.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        double mediaAvaliacao = avaliacao / count;
    	
    	return mediaAvaliacao;
    }
    
    /* getCalorias
     * Método utilizado para retornar as calorias totais de cada receita
     */
    public double getCalorias(UUID id) {
    	double calorias = 0;
    	UUID ingredienteId;
    	List<UUID> ingredientesId = new ArrayList<>();
    	JSONObject nutritionalinfo = new JSONObject();
    	String nutritionalinfoString = "";
        try {
            Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery("SELECT * FROM ingredientesreceita");
            while (rs.next()) {
                UUID recipe_id = (UUID) rs.getObject("recipe_id");
                if(id.toString().equals(recipe_id.toString())) {
                	ingredienteId = (UUID) rs.getObject("ingredient_id");
                	ingredientesId.add(ingredienteId);
                }
            }
            st.close();
            
            st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = st.executeQuery("SELECT * FROM ingredientes");
            for(int i = 0; i < ingredientesId.size(); i++) {
                while (rs.next()) {
                    UUID ingredient_id = (UUID) rs.getObject("id");
                    if(id.toString().equals(ingredient_id.toString())) {
                        // Recuperando a string JSON do banco
                        nutritionalinfoString = rs.getString("nutritionalinfo");

                        // Convertendo a string JSON armazenada no banco para um objeto JSON
                        nutritionalinfo = parseJson(nutritionalinfoString);

                        calorias += Double.parseDouble(nutritionalinfo.get("calorias").toString());
                    }
                }
            }
            st.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    	
    	return calorias;
    }

	/* Método Pesquisa
	 * Adiciona no array de resultado as strings que contém o termo de pesquisa
	 */
    public List<Receita> getPesquisa(String termoPesquisa) {
 
        List<Receita> todasReceitas = getReceitas();
        List<Receita> resultadosPesquisa = new ArrayList<>();

        // Lógica de pesquisa - comparando o termo de pesquisa com cada usuario
        for (Receita receita : todasReceitas) {
            if (receitaToString(receita).toLowerCase().contains(termoPesquisa.toLowerCase())) {
                resultadosPesquisa.add(receita);
            }
        }

        return resultadosPesquisa;
    }
    
	/* Método GET
	 * Faz a conexão com o BD e buscar uma receita específica
	 */
    public Receita buscarReceita(UUID id) {
    	System.out.println("Quase lá");
        Receita receita = null;
        try {
            Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery("SELECT * FROM receita");

            // rs.next() para posicionar o cursor no primeiro resultado
            while (rs.next()) {
                UUID idBd = (UUID) rs.getObject("id");
               
                if (id.toString().equals(idBd.toString())) {
                	String titulo = rs.getString("titulo");
                    UUID usuario_id = (UUID) rs.getObject("usuario_id");
                    String descricao = rs.getString("descricao");
                    Array instrucaoArray = rs.getArray("instrucao");

                    String[] instrucao = {""};
                    
	                // Se 'instrucaoArray' não for nulo, converte-o para um array Java
	                if (instrucaoArray != null) {
	                     instrucao = (String[]) instrucaoArray.getArray();
	                }
	                 
	                int tempoPreparo = rs.getInt("tempoPreparo");
	                String porcao = rs.getString("porcao");
	                String dificuldade = rs.getString("dificuldade");
	                double custo = rs.getInt("custo");
	                
                    // Recuperando a string JSON do banco
                    String nutritionalinfoString = rs.getString("nutritionalinfo");

                    // Convertendo a string JSON armazenada no banco para um objeto JSON
                    JSONObject nutritionalinfo = parseJson(nutritionalinfoString);

                    byte[] imagem = rs.getBytes("imagem");
                    double avaliacao = calcularReviews(id); 
                    receita = new Receita(id, usuario_id, titulo, descricao, instrucao, tempoPreparo, porcao, dificuldade, custo, nutritionalinfo, imagem, avaliacao);
                    break;  // Não precisa continuar após encontrar 
                }
            }
            st.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return receita;
    }
    
    public boolean avaliar(UUID user_id, UUID recipe_id, int review) throws PSQLException {
        System.out.println("DAOavaliar");
        boolean status = false;

        try {
            // Verificar se já existe uma avaliação para o usuário e receita
            if (avaliacaoExistente(user_id, recipe_id)) {
                // Se existir, apagar a avaliação existente
                apagarAvaliacao(user_id, recipe_id);
            }

            // Inserir a nova avaliação
            PreparedStatement ps = conexao.prepareStatement(
                    "INSERT INTO reviews (user_id, recipe_id, review) VALUES (?, ?, ?)");

            ps.setObject(1, user_id);
            ps.setObject(2, recipe_id);
            ps.setInt(3, review);

            ps.executeUpdate();
            ps.close();
            status = true;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return status;
    }

    private boolean avaliacaoExistente(UUID user_id, UUID recipe_id) throws SQLException {
        PreparedStatement ps = conexao.prepareStatement(
                "SELECT COUNT(*) FROM reviews WHERE user_id = ? AND recipe_id = ?");
        ps.setObject(1, user_id);
        ps.setObject(2, recipe_id);

        ResultSet rs = ps.executeQuery();
        rs.next();
        int count = rs.getInt(1);

        rs.close();
        ps.close();

        return count > 0;
    }

    private void apagarAvaliacao(UUID user_id, UUID recipe_id) throws SQLException {
        PreparedStatement ps = conexao.prepareStatement(
                "DELETE FROM reviews WHERE user_id = ? AND recipe_id = ?");
        ps.setObject(1, user_id);
        ps.setObject(2, recipe_id);

        ps.executeUpdate();
        ps.close();
    }

    public double calcularReviews(UUID recipe_id) {
    	double reviews = 0, media = 0, i = 0;
    	System.out.println("Calcular reviews");
        try {
            Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet  rs = st.executeQuery("SELECT * FROM reviews");

            while (rs.next()) {
                UUID id = (UUID) rs.getObject("recipe_id");
                if(id.toString().equals(recipe_id.toString())) {
                	reviews += rs.getInt("review");
                	i++;
                }
            }
    
            st.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        media = reviews / i;
    	
    	return media;
    }
    
    /* Deletar Todas Receitas
     * Deleta todas as linhas da tabela receita
     */
    public boolean apagarTodasReceitas() {
        boolean status = false;
        try {
            PreparedStatement ps = conexao.prepareStatement("DELETE FROM receita");
            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }
   
    public JSONObject calculaInfoNutricional(List<UUID> ingredientesIds, int[] quantidades) {
        JSONObject nutritionalinfo = new JSONObject();
        JSONObject ni = new JSONObject();
        int quantidade; UUID id;
        double calorias, carboidratos, proteinas, gorduraTrans, gorduraSaturada, fibra, sodio;
        calorias = carboidratos = proteinas = gorduraTrans = gorduraSaturada = fibra = sodio = 0;
        System.out.println("Tamanho dos ids: " + ingredientesIds.size() + "Tamanho das quantidades: " + quantidades.length);
        if (ingredientesIds.size() == quantidades.length) {
            for (int i = 0; i < ingredientesIds.size(); i++) {
                id = ingredientesIds.get(i);
                quantidade = quantidades[i];

                ni = calculaInfoNutricionalIndividual(id, quantidade);
                System.out.println("ni: " + ni);
                calorias += Double.parseDouble(ni.get("calorias").toString());
                carboidratos += Double.parseDouble(ni.get("carboidratos").toString());
                proteinas += Double.parseDouble(ni.get("proteinas").toString());
                gorduraTrans += Double.parseDouble(ni.get("gorduraTrans").toString());
                gorduraSaturada += Double.parseDouble(ni.get("gorduraSaturada").toString());
                fibra += Double.parseDouble(ni.get("fibra").toString());
                sodio += Double.parseDouble(ni.get("sodio").toString());
                System.out.println("Oi");
            }
        }

        nutritionalinfo.put("calorias", calorias);
        nutritionalinfo.put("carboidratos", carboidratos);
        nutritionalinfo.put("proteinas", proteinas);
        nutritionalinfo.put("gorduraTrans", gorduraTrans);
        nutritionalinfo.put("gorduraSaturada", gorduraSaturada);
        nutritionalinfo.put("fibra", fibra);
        nutritionalinfo.put("sodio", sodio);
        System.out.println("Before final return - nutritionalinfo: " + nutritionalinfo);
        return nutritionalinfo;
    }

    public JSONObject calculaInfoNutricionalIndividual(UUID id, int quantidade) {
        JSONObject nutritionalinfo = new JSONObject();
        String nutritionalinfoString = "";
        double calorias, carboidratos, proteinas, gorduraTrans, gorduraSaturada, fibra, sodio;
        calorias = carboidratos = proteinas = gorduraTrans = gorduraSaturada = fibra = sodio = 0;

        try {
            Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet  rs = st.executeQuery("SELECT * FROM ingrediente");

            while (rs.next()) {
                UUID ingredient_id = (UUID) rs.getObject("id");
                System.out.println("Current ingredient_id: " + ingredient_id);

                if (id.toString().equals(ingredient_id.toString())) {
                    // Recuperando a string JSON do banco
                    nutritionalinfoString = rs.getString("nutritionalinfo");

                    // Print out values before calculations
                    System.out.println("Before calculations - nutritionalinfo: " + nutritionalinfo);

                    // Convertendo a string JSON armazenada no banco para um objeto JSON
                    nutritionalinfo = parseJson(nutritionalinfoString);

                    // Print out values after parsing JSON
                    System.out.println("After parsing JSON - nutritionalinfo: " + nutritionalinfo);

                    calorias += Double.parseDouble(nutritionalinfo.get("calorias").toString()) * quantidade;
                    carboidratos += Double.parseDouble(nutritionalinfo.get("carboidratos").toString()) * quantidade;
                    proteinas += Double.parseDouble(nutritionalinfo.get("proteinas").toString()) * quantidade;
                    gorduraTrans += Double.parseDouble(nutritionalinfo.get("gorduraTrans").toString()) * quantidade;
                    gorduraSaturada += Double.parseDouble(nutritionalinfo.get("gorduraSaturada").toString()) * quantidade;
                    fibra += Double.parseDouble(nutritionalinfo.get("fibra").toString()) * quantidade;
                    sodio += Double.parseDouble(nutritionalinfo.get("sodio").toString()) * quantidade;
                }
            }

            st.close();
        } catch (Exception e) {
            System.err.println("Error in calculaInfoNutricionalIndividual: " + e.getMessage());
            throw new RuntimeException(e);
        }

        nutritionalinfo.put("calorias", calorias);
        nutritionalinfo.put("carboidratos", carboidratos);
        nutritionalinfo.put("proteinas", proteinas);
        nutritionalinfo.put("gorduraTrans", gorduraTrans);
        nutritionalinfo.put("gorduraSaturada", gorduraSaturada);
        nutritionalinfo.put("fibra", fibra);
        nutritionalinfo.put("sodio", sodio);

        return nutritionalinfo;
    }
    
    public boolean salvarReceita(UUID user_id, UUID recipe_id) {
    	boolean status = false;
    	
    	try {
            PreparedStatement ps = conexao.prepareStatement(
                    "INSERT INTO receitassalvas (user_id, recipe_id) VALUES (?, ?)");

            ps.setObject(1, user_id);
            ps.setObject(2, recipe_id);

            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    	
    	return status;
    }
    
    public boolean isSalva(UUID user_id, UUID recipe_id) throws SQLException {
        PreparedStatement ps = conexao.prepareStatement(
                "SELECT COUNT(*) FROM receitassalvas WHERE user_id = ? AND recipe_id = ?");
        ps.setObject(1, user_id);
        ps.setObject(2, recipe_id);

        ResultSet rs = ps.executeQuery();
        rs.next();
        int count = rs.getInt(1);

        rs.close();
        ps.close();

        return count > 0;
    }
    
    public List<Receita> exibirReceitasSalvas(UUID user_id){
    	List<UUID> receitasIds = new ArrayList<>();
        List<Receita> receitasSalvas = new ArrayList<>();
        
        UUID recipe_id = null;
        try {
            Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery("SELECT * FROM restricao");
            while (rs.next()) {
                UUID usuario_id = (UUID) rs.getObject("user_id");
               
                if(user_id.toString().equals(usuario_id.toString())) {
                	recipe_id = (UUID) rs.getObject("recipe_id");
                }
                
               
				receitasIds.add(recipe_id);
            }
            st.close();
            
            st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = st.executeQuery("SELECT * FROM receitas");

            for(UUID receitaId : receitasIds) {
                while (rs.next()) {
                    UUID id = (UUID) rs.getObject("recipe_id");
                   
                    if(receitaId.toString().equals(id.toString())) {
                    	UUID usuario_id = (UUID) rs.getObject("usuario_id");
                        String titulo = rs.getString("titulo");
                        String descricao = rs.getString("descricao");
                        Array instrucaoArray = rs.getArray("instrucao");

                        String[] instrucao = {""};
                        
                        // Se 'instrucaoArray' não for nulo, converte-o para um array Java
                        if (instrucaoArray != null) {
                             instrucao = (String[]) instrucaoArray.getArray();
                        }
                        
                        int tempoPreparo = rs.getInt("tempoPreparo");
                        String porcao = rs.getString("porcao");
                        String dificuldade = rs.getString("dificuldade");
                        double custo = rs.getDouble("custo");
                        // Recuperando a string JSON do banco
                        String nutritionalinfoString = rs.getString("nutritionalinfo");

                        // Convertendo a string JSON armazenada no banco para um objeto JSON
                        JSONObject nutritionalinfo = parseJson(nutritionalinfoString);

                        byte[] imagem = rs.getBytes("imagem");

                        Receita receita = new Receita(id, usuario_id, titulo, descricao, instrucao, tempoPreparo, porcao, dificuldade, custo, nutritionalinfo, imagem);
    				receitasSalvas.add(receita);
                }
             } 
            
            }
            st.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return receitasSalvas;
    }
    
    public boolean liberarReceita(UUID user_id, UUID recipe_id) {
        boolean status = false;
        try {
            PreparedStatement ps = conexao.prepareStatement("DELETE FROM resceitassalvas WHERE recipe_id = ? AND user_id = ?");
            ps.setObject(1, recipe_id);
            ps.setObject(2, user_id);
            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }
    
    public boolean liberarReceitas(UUID user_id) {
        boolean status = false;
        try {
            PreparedStatement ps = conexao.prepareStatement("DELETE FROM receitassalvas WHERE user_id = ?");
            ps.setObject(1, user_id);
            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }
    
    // Função para converter um ingrediente em uma string (método auxiliar da pesquisa)
    private String receitaToString(Receita receita) {
        // Concatena todos os atributos relevantes da receita em uma string
        return receita.getTitulo() +
               receita.getDescricao();
    }
    
    private JSONObject parseJson(String json) {
        try {
            return (JSONObject) new JSONParser().parse(json);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
