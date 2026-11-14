package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Receta;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecetaDaoImpl implements IRecetaDao {

    @Override
    public List<Receta> listar() {
        List<Receta> lista = new ArrayList<>();
        String sql = "{CALL sp_seleccionar(\"recetas\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Date sqlDate = rs.getDate("fecha");
                
                Receta r = new Receta(
                        rs.getInt("idReceta"),
                        sqlDate != null ? sqlDate.toLocalDate() : null,
                        rs.getString("indicacion"),
                        rs.getInt("consultaId")
                );
                lista.add(r);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar recetas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Receta buscarPorId(int id) {
        Receta r = null;
        String sql = "{CALL sp_recetas_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    Date sqlDate = rs.getDate("fecha");
                    
                    r = new Receta(
                            rs.getInt("idReceta"),
                            sqlDate != null ? sqlDate.toLocalDate() : null,
                            rs.getString("indicacion"),
                            rs.getInt("consultaId")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar receta: " + e.getMessage());
        }
        return r;
    }

    @Override
    public boolean insertar(Receta r) {
        String sql = "{CALL sp_recetas_insertar(?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setDate(1, r.getFecha() != null ? Date.valueOf(r.getFecha()) : null);
            cs.setString(2, r.getIndicacion());
            cs.setInt(3, r.getConsultaId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar receta: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int insertarYRetornarId(Receta r) {
        String sql = "{CALL sp_recetas_insertar(?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setDate(1, r.getFecha() != null ? Date.valueOf(r.getFecha()) : null);
            cs.setString(2, r.getIndicacion());
            cs.setInt(3, r.getConsultaId());

            if (cs.executeUpdate() == 0) {
                return 0;
            }

            // Se consulta en la misma conexión para obtener el id autogenerado.
            try (PreparedStatement ps = con.prepareStatement("SELECT LAST_INSERT_ID()");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "No se pudo insertar la receta: " + e.getMessage(), e);
        }
        return 0;
    }

}