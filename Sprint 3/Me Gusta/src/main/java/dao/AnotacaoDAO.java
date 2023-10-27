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
					"INSERT INTO \"MG\".anotacao (id, message, color) VALUES(?, ?, ?)" );
			
			ps.setObject(1, anotacao.getId());
			ps.setString(2, anotacao.getMessage()!= null ? anotacao.getMessage() : "");
			ps.setString(3, anotacao.getColor() != null ? anotacao.getColor(): "");
			
			ps.executeUpdate();
            ps.close();
            status = true;			 
		} catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return status;
	}
	
	//rever a forma de excluir isso aqui
	
	public boolean excluirAnotacao (Anotacao anotacao) {
        boolean status = false;
        try {
            PreparedStatement ps = conexao.prepareStatement("DELETE FROM \"MG\".anotacao WHERE id = ?");
            ps.setObject(1, anotacao.getId());
            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
	}
	
	public boolean apagarTodasAnotacoes () {
        boolean status = false;
        try {
            PreparedStatement ps = conexao.prepareStatement("DELETE FROM \"MG\".anotacao");
            ps.executeUpdate();
            ps.close();
            status = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
	}
	
	public List<Anotacao> getAnotacoes() {
		List<Anotacao> anotacoes = new ArrayList <> ();
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery("SELECT * FROM \"MG\".anotacao");
            
            while (rs.next()) {
            	UUID id = (UUID) rs.getObject("id");
                String message = rs.getString("message");
                String color = rs.getString("color");
                
                Anotacao anotacao = new Anotacao(id, message, color);
                anotacoes.add(anotacao);
            }
            
		} catch (Exception e) {
            throw new RuntimeException(e);
        }
        return anotacoes;
	}
}