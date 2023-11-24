package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.postgresql.util.PSQLException;

import model.Restricao;
import model.Usuario;

public class RestricaoDAO extends DAO {

    public RestricaoDAO() {
        super();
        conectar();
    }

    public boolean inserirRestricao(Restricao restricao) throws PSQLException {
    
        boolean status = false;
        try {
            
            PreparedStatement ps = conexao.prepareStatement(
                    "INSERT INTO \"MG\".restricao (id, tipo, nome) VALUES (?, ?, ?)");

            ps.setObject(1, restricao.getId());
            ps.setString(3, restricao.getNome() != null ? restricao.getNome() : "");
            ps.setString(2, restricao.getTipo() != null ? restricao.getTipo() : "");

            ps.executeUpdate();
            ps.close();
            status = true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return status;
    }

    public boolean atualizarRestricao(Restricao restricao) {
    	System.out.println("Quase la!!");
        boolean status = false;
        
        try {
            PreparedStatement ps = conexao.prepareStatement(
                    "UPDATE \"MG\".restricao SET nome = ?, tipo = ? WHERE id = ?");
    
            ps.setString(1, restricao.getNome());
            ps.setString(2, restricao.getTipo());
            ps.setObject(3, restricao.getId());
    
            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }
    

    public boolean excluirRestricao(String nome) {
        boolean status = false;
        try {
            PreparedStatement ps = conexao.prepareStatement("DELETE FROM \"MG\".restricao WHERE nome = ?");
            ps.setString(1, nome);
            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }
    
    public List<Restricao> getRestricoes() {
        List<Restricao> restricoes = new ArrayList<>();
    
        try {
            Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery("SELECT * FROM \"MG\".restricao");
            while (rs.next()) {
                UUID id = (UUID) rs.getObject("id");
               
                String tipo = rs.getString("tipo");
                String nome = rs.getString("nome");
                
                Restricao restricao = new Restricao(id, nome, tipo);
                restricoes.add(restricao);
            }
            st.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return restricoes;
    }
    
    public List<Restricao> sort(List<Restricao> restrictionList) {
        int n = restrictionList.size();
        return quicksort(restrictionList, 0, n - 1);
    }
    
    public List<Restricao> sortTipo(List<Restricao> restrictionList) {
        int n = restrictionList.size();
        return quicksortTipo(restrictionList, 0, n - 1);
    }

    // Método swap que ordena pelo nome
    private List<Restricao> quicksort(List<Restricao> restrictionList, int esq, int dir) {
        int i = esq, j = dir;
        String pivo = restrictionList.get((dir + esq) / 2).getNome();
        while (i <= j) {
            while (restrictionList.get(i).getNome().compareTo(pivo) < 0) i++;
            while (restrictionList.get(j).getNome().compareTo(pivo) > 0) j--;
            if (i <= j) {
                swap(restrictionList, i, j);
                i++;
                j--;
            }
        }
        if (esq < j) quicksort(restrictionList, esq, j);
        if (i < dir) quicksort(restrictionList, i, dir);

        return restrictionList;
    }
    
    // Método quicksort que ordena pelo tipo de restrição e em um segundo caso pelo nome
    private List<Restricao> quicksortTipo(List<Restricao> restrictionList, int esq, int dir) {
        int i = esq, j = dir;
        String pivo = restrictionList.get((dir + esq) / 2).getTipo();
        String pivoNome = restrictionList.get((dir + esq) / 2).getNome();
        while (i <= j) {
            while ((restrictionList.get(i).getTipo().compareTo(pivo) < 0) || (restrictionList.get(i).getTipo().equals(pivo) && restrictionList.get(i).getNome().compareTo(pivoNome) < 0)) i++;
            while ((restrictionList.get(j).getTipo().compareTo(pivo) > 0) || (restrictionList.get(j).getTipo().equals(pivo) && restrictionList.get(j).getNome().compareTo(pivoNome) > 0)) j--;
            if (i <= j) {
                swap(restrictionList, i, j);
                i++;
                j--;
            }
        }
        if (esq < j) quicksortTipo(restrictionList, esq, j);
        if (i < dir) quicksortTipo(restrictionList, i, dir);

        return restrictionList;
    }

    private void swap(List<Restricao> restrictionList, int i, int j) {
        Restricao temp = restrictionList.get(i);
        restrictionList.set(i, restrictionList.get(j));
        restrictionList.set(j, temp);
    }
    
    public List<Restricao> getPesquisa(String termoPesquisa) {
        RestricaoDAO restricaoDAO = new RestricaoDAO();
        List<Restricao> todasRestricoes = restricaoDAO.getRestricoes();
        List<Restricao> resultadosPesquisa = new ArrayList<>();

        // Lógica de pesquisa - comparando o termo de pesquisa com cada usuario
        for (Restricao restricao : todasRestricoes) {
            if (restricaoToString(restricao).toLowerCase().contains(termoPesquisa.toLowerCase())) {
                resultadosPesquisa.add(restricao);
            }
        }

        return resultadosPesquisa;
    }
    
	/* Método GET
	 * Faz a conexão com o BD e buscar uma restrição específica
	 */
    public Restricao buscarRestricao(String name) {
    	System.out.println("Buscando...");
        Restricao restricao = null;
        try {
            Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery("SELECT * FROM \"MG\".restricao");

            // rs.next() para posicionar o cursor no primeiro resultado
            while (rs.next()) {
                String nome = rs.getString("nome");
               
                if (nome.equals(name)) {
                    UUID id = (UUID) rs.getObject("id");
                    String tipo = rs.getString("tipo");
                    restricao = new Restricao(id, nome, tipo);
                    break;  // Não precisa continuar após encontrar a requisição
                }
            }
            st.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return restricao;
    }
    
	/* Método de Verificação
	 * Faz a conexão com o BD e buscar uma restrição específica, retornando se ela existe ou nao
	 */
    public boolean restricaoExiste(String name) {
        boolean status = false;

        try (PreparedStatement ps = conexao.prepareStatement("SELECT * FROM \"MG\".restricao WHERE nome = ?")) {
            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                status = rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return status;
    }
    
    public boolean apagarTodasRestricoes() {
        boolean status = false;
        try {
            PreparedStatement ps = conexao.prepareStatement("DELETE FROM \"MG\".restricao");
            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }
    
    // Função para verificar se a restrição já foi cadastrada (método auxiliar de inserção)
    public boolean verificarRestricao(String restricao) {
    	boolean status = false;
    	List<Restricao> restricoes = getRestricoes();
    	
    	for(Restricao restricao1 : restricoes) {
    		
    		if(restricao1.getNome().equals(restricao)) {
    			status = true;
    		}
    	}
    	
    	return status;
    }
   

    // Função para converter uma restricao em uma string (método auxiliar da pesquisa)
    private String restricaoToString(Restricao restricao) {
        // Concatena todos os atributos relevantes da restrição em uma string
        return restricao.getNome() +
               restricao.getTipo();
    }
}