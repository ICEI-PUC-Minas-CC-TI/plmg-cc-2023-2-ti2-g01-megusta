package dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.Receita;
import model.Usuario;

public class ReceitaDAO extends DAO{

	public ReceitaDAO() {
		super();
		conectar();
	}
	 public boolean inserirReceita(Receita rec, Usuario user) {
	        boolean status = false;
	        try {
	            PreparedStatement ps = conexao.prepareStatement("INSERT INTO \"public\".\"Receita\" (id, titulo, desc, autor, porcao, custo, dificuldade, preparo, prepTime, valorNut) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	            ps.setString(1, rec.getId());
	            ps.setString(2, rec.getTitulo());
	            ps.setString(3, null);
	            ps.setString(4, rec.getAutor());
	            ps.setDouble(5, rec.getPorcao());
	            ps.setDouble(6,rec.getCusto());
	            ps.setString(7, rec.getDificuldade());
	            ps.setString(8, rec.getPreparo());
	            ps.setDouble(9, rec.getPrepTime());
	            ps.setString(10, rec.getValorNut());
	            ps.executeUpdate();
	            ps.close();
	            ps = conexao.prepareStatement("INSERT INTO \"public\".\"UserRec\" (UserID, RecID) VALUES (?, ?)");
	            ps.setString(1, user.getId().toString());
	            ps.setUUID(2, rec.getId());
	            status = true;
	        }
	        catch(SQLException e) {
	            e.printStackTrace();
	            throw new RuntimeException(e);
	        }
	        return status;
	 }
}
