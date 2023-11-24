package dao;

import spark.Response;
import spark.Request;
import java.sql.*;
import java.util.*;

import model.Anotacao;
//import model.Usuario;

public class AnotacaoDAO extends DAO{
	
	public AnotacaoDAO () {
		super();
		conectar();
	}
	
	public boolean inserirAnotacao(Anotacao anotacao) {
		boolean status = false;
		try {
			PreparedStatement ps = conexao.prepareStatement (
					"INSERT INTO anotacao (id, mensagem, cor, usuario_id) VALUES(?, ?, ?, ?)" );
			
			ps.setObject(1, anotacao.getId());
			ps.setString(2, anotacao.getMessage()!= null ? anotacao.getMessage() : "");
			ps.setString(3, anotacao.getColor() != null ? anotacao.getColor(): "");
			ps.setObject(4, anotacao.getUserId());
			
			ps.executeUpdate();
            ps.close();
            status = true;			 
		} catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return status;
	}
	
	public boolean excluirAnotacao (UUID note_id) {
		System.out.println("Quase lá");
        boolean status = false;
        try {
            PreparedStatement ps = conexao.prepareStatement("DELETE FROM anotacao WHERE id = ?");
            ps.setObject(1, note_id);
            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
	}
	
	public boolean apagarTodasAnotacoes (UUID user_id) {
        boolean status = false;
        try {
            PreparedStatement ps = conexao.prepareStatement("DELETE FROM anotacao WHERE usuario_id = ?");
            ps.setObject(1, user_id);
            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
	}
	
	public List<Anotacao> getAnotacoes(UUID userId) {
		
		List<Anotacao> anotacoes = new ArrayList <> ();
		UUID id = null, user_id = null; String message = "", color = "";
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery("SELECT * FROM anotacao");
            
            while (rs.next()) {
            	user_id = (UUID) rs.getObject("usuario_id");
            	if(userId.toString().equals(user_id.toString())) {
                	id = (UUID) rs.getObject("id");
                    message = rs.getString("mensagem");
                    color = rs.getString("cor");
                    
                    Anotacao anotacao = new Anotacao(id, message, color, user_id);
                    anotacoes.add(anotacao);
            	}
            }
            
		} catch (Exception e) {
            throw new RuntimeException(e);
        }
        return anotacoes;
	}
}