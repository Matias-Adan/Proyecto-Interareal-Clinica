package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Consultorio;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConsultorioDaoImpl implements IConsultorioDao {

    @Override
    public List<Consultorio> listar() {
        List<Consultorio> lista = new ArrayList<>();
        String sql = "{CALL sp_seleccionar(\"consultorio\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Consultorio c = new Consultorio(
                        rs.getInt("idConsultorio"),
                        rs.getInt("numero"),
                        rs.getString("tipo"),
                        rs.getString("estado"),
                        rs.getInt("piso")
                );
                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar consultorios: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Consultorio buscarPorId(int id) {
        Consultorio c = null;
        String sql = "{CALL sp_consultorio_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    c = new Consultorio(
                            rs.getInt("idConsultorio"),
                            rs.getInt("numero"),
                            rs.getString("tipo"),
                            rs.getString("estado"),
                            rs.getInt("piso")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar consultorio: " + e.getMessage());
        }
        return c;
    }

    @Override
    public boolean insertar(Consultorio c) {
        String sql = "{CALL sp_consultorio_insertar(?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, c.getNumero());
            cs.setString(2, c.getTipo());
            cs.setString(3, c.getEstado());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar consultorio: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Consultorio c) {
        String sql = "{CALL sp_consultorio_actualizar(?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, c.getIdConsultorio());
            cs.setInt(2, c.getNumero());
            cs.setString(3, c.getTipo());
            cs.setString(4, c.getEstado());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar consultorio: " + e.getMessage());
            return false;
        }
    }

}