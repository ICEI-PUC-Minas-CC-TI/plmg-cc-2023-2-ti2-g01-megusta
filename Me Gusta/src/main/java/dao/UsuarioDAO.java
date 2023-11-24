package dao;

import spark.Request;
import spark.Response;
import java.sql.*;
import java.util.*;

import org.postgresql.util.PSQLException;

import model.Restricao;
import model.Usuario;

public class UsuarioDAO extends DAO {

    public UsuarioDAO() {
        super();
        conectar();
    }

	/* Método INSERT
	 * Faz a conexão com o BD e insere um usuário na tabela 
	 */
    public boolean inserirUsuario(Usuario usuario) throws PSQLException {
        boolean status = false;
        try {
            
            PreparedStatement ps = conexao.prepareStatement(
                    "INSERT INTO \"MG\".usuario (id, nome, sobrenome, usuario, email, idade, genero, senha, permissao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

            ps.setObject(1, usuario.getId());
            ps.setString(2, usuario.getNome() != null ? usuario.getNome() : "");
            ps.setString(3, usuario.getSobrenome() != null ? usuario.getSobrenome() : "");
            ps.setString(4, usuario.getUsuario() != null ? usuario.getUsuario() : "");
            ps.setString(5, usuario.getEmail() != null ? usuario.getEmail() : "");
            //ps.setBytes(6, usuario.getProfilePic() != null ? usuario.getProfilePic() : new byte[0]);
            ps.setInt(6, usuario.getIdade() > 0 ? usuario.getIdade() : 0);
            ps.setString(7, String.valueOf(usuario.getGenero()));
            ps.setString(8, usuario.getSenha() != null ? toMD5(usuario.getSenha()) : "");
            ps.setString(9, String.valueOf(usuario.getPermissao()));

            ps.executeUpdate();
            ps.close();
            status = true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return status;
    }

	/* Método UPDATE
	 * Faz a conexão com o BD e atualiza um usuário na tabela 
	 */
    public boolean atualizarUsuario(Usuario usuario) {
        boolean status = false;
        
        try {
            PreparedStatement ps = conexao.prepareStatement(
                    "UPDATE \"MG\".usuario SET nome = ?, sobrenome = ?, usuario = ?, email = ?, idade = ?, genero = ?, senha = ?, permissao = ? WHERE id = ?");
    
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getSobrenome());
            ps.setString(3, usuario.getUsuario());
            ps.setString(4, usuario.getEmail());
            ps.setInt(5, usuario.getIdade());
            ps.setString(6, String.valueOf(usuario.getGenero()));
            ps.setString(7, usuario.getSenha() != null ? toMD5(usuario.getSenha()) : "");
            ps.setString(8, String.valueOf(usuario.getPermissao()));
            ps.setObject(9, usuario.getId());
    
            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }
    

	/* Método GET
	 * Faz a conexão com o BD e lista todos os usuários da tabela
	 */
    public List<Usuario> getUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
    
        try {
            Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery("SELECT * FROM \"MG\".usuario");
            while (rs.next()) {
                UUID id = (UUID) rs.getObject("id");
                String nome = rs.getString("nome");
                String sobrenome = rs.getString("sobrenome");
                String user = rs.getString("usuario");
                String email = rs.getString("email");
 

                int idade = rs.getInt("idade");
                String generoStr = rs.getString("genero");
                char genero = generoStr.charAt(0);
                String senha = rs.getString("senha");
                String permissaoStr = rs.getString("permissao");
                char permissao = permissaoStr.charAt(0);
    
                Usuario usuario = new Usuario(id, nome, sobrenome, user, senha, email, genero, idade, permissao);
                usuarios.add(usuario);
            }
            st.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return usuarios;
    }
    
	/* Método sort
	 * Recebe uma lista de usuários e chama o método quicksort
	 */
    public List<Usuario> sort(List<Usuario> userList) {
        int n = userList.size();
        return quicksort(userList, 0, n - 1);
    }

	/* Método INSERT
	 * Recebe uma lista de usuários e os ordena alfabeticamente com base no atributo usuário 
	 */
    private List<Usuario> quicksort(List<Usuario> userList, int esq, int dir) {
        int i = esq, j = dir;
        String pivo = userList.get((dir + esq) / 2).getUsuario();
        while (i <= j) {
            while (userList.get(i).getUsuario().compareTo(pivo) < 0) i++;
            while (userList.get(j).getUsuario().compareTo(pivo) > 0) j--;
            if (i <= j) {
                swap(userList, i, j);
                i++;
                j--;
            }
        }
        if (esq < j) quicksort(userList, esq, j);
        if (i < dir) quicksort(userList, i, dir);

        return userList;
    }

	/* Método SWAP
	 * Troca valores de posição na lista de usuários
	 */
    private void swap(List<Usuario> userList, int i, int j) {
        Usuario temp = userList.get(i);
        userList.set(i, userList.get(j));
        userList.set(j, temp);
    }
    
	/* Método PESQUISA
	 * Cria uma lista de usuários com todas as linhas da tabela e faz a pesquisa do termo recebido como parâmetro
	 */
    public List<Usuario> getPesquisa(String termoPesquisa) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> todosUsuarios = usuarioDAO.getUsuarios();
        List<Usuario> resultadosPesquisa = new ArrayList<>();

        // Lógica de pesquisa - comparando o termo de pesquisa com cada usuario
        for (Usuario usuario : todosUsuarios) {
            if (usuarioToString(usuario).toLowerCase().contains(termoPesquisa.toLowerCase())) {
                resultadosPesquisa.add(usuario);
            }
        }

        return resultadosPesquisa;
    }
    
	/* Método GET
	 * Faz a conexão com o BD e busca um usuário específico
	 */
    public Usuario buscarUsuario(String user) {
        
        Usuario username = new Usuario();
        try {
            Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery("SELECT * FROM \"MG\".usuario");

            // rs.next() para posicionar o cursor no primeiro resultado
            while (rs.next()) {
                String user1 = rs.getString("usuario");
                if (user1.equals(user)) {
                    UUID id = (UUID) rs.getObject("id");
                    String nome = rs.getString("nome");
                    String sobrenome = rs.getString("sobrenome");
                    String email = rs.getString("email");
                    int idade = rs.getInt("idade");
                    String generoStr = rs.getString("genero");
                    char genero = generoStr.charAt(0);
                    String senha = rs.getString("senha");
                    String permissaoStr = rs.getString("permissao");
                    char permissao = permissaoStr.charAt(0);
                    username = new Usuario(id, nome, sobrenome, user1, senha, email, genero, idade, permissao);
                    break;  // Não precisa continuar após encontrar o usuário
                }
            }
            st.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return username;
    }

	/* Método Autenticar usuário
	 * Faz a conexão com o BD e verifica se o user e a senha informados no log-in são válidos e correspondem
	 */
    public boolean autenticarUsuario(String user, String senha) {

        boolean status = false;
        
        String senhaCriptografada = toMD5(senha);
        
        try {
            PreparedStatement ps = conexao.prepareStatement("SELECT * FROM \"MG\".usuario WHERE usuario = ?");

            ps.setString(1, user);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String password = rs.getString("senha");
                
                if (password.equals(senhaCriptografada)) {
                    status = true;
                }
            }
        
            ps.close();
        } catch (Exception e) {
        	System.out.println("Ixiii..");
            throw new RuntimeException(e);
        }
        
        return status;
    }

	/* Método salvarRestricoes
	 * Faz a conexão com o BD e salva as restrições informadas pelo usuário
	 */
    public boolean salvarRestricoes(UUID id, List<UUID> restricoesList) throws PSQLException {
        boolean status = false;
        try {
            
            PreparedStatement ps = conexao.prepareStatement(
                    "INSERT INTO \"MG\".usuariorestricao (user_id, restriction_id) VALUES (?, ?)");

            for(UUID restricao : restricoesList) {
                ps.setObject(1, id);
                ps.setObject(2, restricao);
                ps.executeUpdate();
            }

            ps.close();
            status = true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return status;
    }
    
	/* Método atualizarRestricoes
	 * Faz a conexão com o BD e atualiza as restrições informadas pelo usuário
	 */
    public boolean atualizarRestricoes(UUID id, List<UUID> restricoesList) throws PSQLException {
        boolean status = false;
        try {
            // Remove todas as associações existentes na tabela ingredienteRestricao
            PreparedStatement psDelete = conexao.prepareStatement(
                "DELETE FROM \"MG\".usuariorestricao WHERE user_id = ?");
            psDelete.setObject(1, id);
            psDelete.executeUpdate();
            psDelete.close();
            
            PreparedStatement ps = conexao.prepareStatement(
                    "INSERT INTO \"MG\".usuariorestricao (user_id, restriction_id) VALUES (?, ?)");

            for(UUID restricao : restricoesList) {
                ps.setObject(1, id);
                ps.setObject(2, restricao);
                ps.executeUpdate();
            }

            ps.close();
            status = true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return status;
    }
    
	/* Método restarRestricoes
	 * Faz a conexão com o BD e apaga as linhas da tabela usuariorestricoes referentes ao id passado por parâmetro
	 */
    public boolean resetarRestricoes(UUID id) {
    	
        boolean status = false;
        try {
            PreparedStatement ps = conexao.prepareStatement("DELETE FROM \"MG\".usuariorestricao WHERE user_id = ?");
            ps.setObject(1, id);
            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }

	/* Método salvarIngredientes
	 * Faz a conexão com o BD e salva os ingredientes selecionados pelo usuário
	 */
    public boolean salvarIngredientes(UUID id, List<UUID> ingredientesList) throws PSQLException {
        boolean status = false;
        try {
            
            PreparedStatement ps = conexao.prepareStatement(
                    "INSERT INTO \"MG\".ingredientesselecionados (user_id, ingredient_id) VALUES (?, ?)");

            for(UUID ingrediente : ingredientesList) {
                ps.setObject(1, id);
                ps.setObject(2, ingrediente);
                ps.executeUpdate();
            }

            ps.close();
            status = true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return status;
    }
    
	/* Método atualizarIngredientes
	 * Faz a conexão com o BD e atualiza os ingredientes selecionados pelo usuário
	 */
    public boolean atualizarIngredientes(UUID id, List<UUID> ingredientesList) throws PSQLException {
        boolean status = false;
        try {
            // Remove todas as associações existentes na tabela ingredienteRestricao
            PreparedStatement psDelete = conexao.prepareStatement(
                "DELETE FROM \"MG\".ingredientesselecionados WHERE user_id = ?");
            psDelete.setObject(1, id);
            psDelete.executeUpdate();
            psDelete.close();
            
            PreparedStatement ps = conexao.prepareStatement(
                    "INSERT INTO \"MG\".ingredientesselecionados (user_id, ingredient_id) VALUES (?, ?)");

            for(UUID ingrediente : ingredientesList) {
                ps.setObject(1, id);
                ps.setObject(2, ingrediente);
                ps.executeUpdate();
            }

            ps.close();
            status = true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return status;
    }
    
	/* Método restarIngredientes
	 * Faz a conexão com o BD e apaga as linhas da tabela ingredientesselecionados referentes ao id passado por parâmetro
	 */
    public boolean resetarIngredientes(UUID id) {
    	
        boolean status = false;
        try {
            PreparedStatement ps = conexao.prepareStatement("DELETE FROM \"MG\".ingredientesselecionados WHERE user_id = ?");
            ps.setObject(1, id);
            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }
    
	/* Método salvarIngredientesX
	 * Faz a conexão com o BD e salva os ingredientes banidos pelo usuário
	 */
    public boolean salvarIngredientesX(UUID id, List<UUID> ingredientesList) throws PSQLException {
        boolean status = false;
        try {
            
            PreparedStatement ps = conexao.prepareStatement(
                    "INSERT INTO \"MG\".ingredientesbanidos (user_id, ingredient_id) VALUES (?, ?)");

            for(UUID ingrediente : ingredientesList) {
                ps.setObject(1, id);
                ps.setObject(2, ingrediente);
                ps.executeUpdate();
            }

            ps.close();
            status = true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return status;
    }
    
	/* Método atualizarIngredientesX
	 * Faz a conexão com o BD e atualiza os ingredientes banidos pelo usuário
	 */
    public boolean atualizarIngredientesX(UUID id, List<UUID> ingredientesList) throws PSQLException {
        boolean status = false;
        try {
            // Remove todas as associações existentes na tabela ingredienteRestricao
            PreparedStatement psDelete = conexao.prepareStatement(
                "DELETE FROM \"MG\".ingredientesbanidos WHERE user_id = ?");
            psDelete.setObject(1, id);
            psDelete.executeUpdate();
            psDelete.close();
            
            PreparedStatement ps = conexao.prepareStatement(
                    "INSERT INTO \"MG\".ingredientesbanidos (user_id, ingredient_id) VALUES (?, ?)");

            for(UUID ingrediente : ingredientesList) {
                ps.setObject(1, id);
                ps.setObject(2, ingrediente);
                ps.executeUpdate();
            }

            ps.close();
            status = true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return status;
    }
    
	/* Método restarIngredientesX
	 * Faz a conexão com o BD e apaga as linhas da tabela ingredientesselecionados referentes ao id passado por parâmetro
	 */
    public boolean resetarIngredientesX(UUID id) {
    	
        boolean status = false;
        try {
            PreparedStatement ps = conexao.prepareStatement("DELETE FROM \"MG\".ingredientesbanidos WHERE user_id = ?");
            ps.setObject(1, id);
            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }   
    
    public boolean informouIngredientes(UUID id) {
        boolean status1 = false, status2 = false;

        try {
            // Verifica a tabela ingredientesselecionados
            PreparedStatement ps1 = conexao.prepareStatement("SELECT * FROM \"MG\".ingredientesselecionados WHERE user_id = ?");
            ps1.setObject(1, id);
            ResultSet rs1 = ps1.executeQuery();
            status1 = rs1.next(); // Define status1 como true se houver um resultado

            // Verifica a tabela ingredientesbanidos
            PreparedStatement ps2 = conexao.prepareStatement("SELECT * FROM \"MG\".ingredientesbanidos WHERE user_id = ?");
            ps2.setObject(1, id);
            ResultSet rs2 = ps2.executeQuery();
            status2 = rs2.next(); // Define status2 como true se houver um resultado

            // Fecha os recursos
            rs1.close();
            ps1.close();
            rs2.close();
            ps2.close();

        } catch (Exception e) {
            System.out.println("Ixiii..");
            throw new RuntimeException(e);
        }
        System.out.println("Status1: " + status1);
        System.out.println("Status2: " + status2);

        // Retorna true se o ID estiver presente em ambas as tabelas
        return status1 && status2;
    }

    
	/* Método DELETE ALL
	 * Faz a conexão com o BD e apaga todos os valores da tabela
	 */
    public boolean apagarTodosUsuarios() {
    	
        boolean status = false;
        try {
            PreparedStatement ps = conexao.prepareStatement("DELETE FROM \"MG\".usuario");
            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }
    
	/* Método DELETE
	 * Faz a conexão com o BD e apaga um único usuário
	 */
    public boolean excluirUsuario(String username) {
    	
        boolean status = false;
        try {
            PreparedStatement ps = conexao.prepareStatement("DELETE FROM \"MG\".usuario WHERE usuario = ?");
            ps.setString(1, username);
            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }
    
    // Função para verificar se já existe usuário com tal user (método auxiliar para alterar)
    public boolean verificaUser(String user) {
    	
    	boolean status = true;
    	List<Usuario> users = getUsuarios();
    	Usuario user1 = buscarUsuario(user);
    	
    	if(user1.getUsuario() != null && user1.getUsuario().equals(user)) {
    		status = true;
    	} else {
    		for(Usuario usuario : users) {
        		if(usuario.getUsuario() != null && usuario.getUsuario().equals(user)) {
        			status = false;
        		}
        	}
    	}
    	
    	return status;
    }
    
    // Função para verificar se já existe usuário com tal user (método auxiliar de inserção)
    public boolean verificaUser1(String user) {
    	
    	boolean status = true;
    	List<Usuario> users = getUsuarios();

    		for(Usuario usuario : users) {
        		if(usuario.getUsuario() != null && usuario.getUsuario().equals(user)) {
        			status = false;
        		}
    	    }
    	
    	return status;
    }
    
    // Função para verificar se as senhas correspondem
    public boolean verificaSenha(String senha, String usuario) {
    	boolean status = false;
    	Usuario user = buscarUsuario(usuario);
    	String password = user.getSenha();
    	String senhaCriptografada = toMD5(senha);
    
    	if(senhaCriptografada.equals(password)) {
    			status = true;
    	}
    	
    	return status;
    }
    
    // Função para converter um usuário em uma string (método auxiliar da pesquisa)
    private String usuarioToString(Usuario usuario) {
        // Concatene todos os atributos relevantes do usuário em uma string
        return usuario.getNome() +
               usuario.getSobrenome() +
               usuario.getUsuario() +
               usuario.getEmail() +
               usuario.getIdade() +
               usuario.getPermissao();
    }

}