package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Articulo;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArticuloDaoImpl implements IArticuloDao {

    @Override
    public List<Articulo> listar() {
        List<Articulo> lista = new ArrayList<>();
        String sql = "{CALL sp_seleccionar(\"articulos\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Articulo a = new Articulo(
                		rs.getInt("idArticulo"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("presentacion"),
                        rs.getString("tipoArticulo"),
                        rs.getInt("stockActual"),
                        rs.getInt("stockMinimo"),
                        rs.getDouble("temperaturaIdealEnCelcius")
                );
                lista.add(a);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar artículos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Articulo buscarPorId(int id) {
        Articulo a = null;
        String sql = "{CALL sp_articulos_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    a = new Articulo(
                        rs.getInt("idArticulo"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("presentacion"),
                        rs.getString("tipoArticulo"),
                        rs.getInt("stockActual"),
                        rs.getInt("stockMinimo"),
                        rs.getDouble("temperaturaIdealEnCelcius")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar artículo: " + e.getMessage());
        }
        return a;
    }

    @Override
    public boolean insertar(Articulo a) {
        String sql = "{CALL sp_articulos_insertar(?, ?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setString(1, a.getNombre());
            cs.setString(2, a.getDescripcion());
            cs.setString(3, a.getPresentacion());
            cs.setString(4, a.getTipoArticulo());
            cs.setInt(5, a.getStockActual());
            cs.setInt(6, a.getStockMinimo());
            cs.setDouble(7, a.getTemperaturaIdealEnCelcius());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar artículo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Articulo a) {
        String sql = "{CALL sp_articulos_actualizar(?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, a.getIdArticulo());
            cs.setString(2, a.getNombre());
            cs.setString(3, a.getPresentacion());
            cs.setString(4, a.getTipoArticulo());
            cs.setString(5, a.getDescripcion());
            cs.setInt(6, a.getStockActual());
            cs.setInt(7, a.getStockMinimo());
            cs.setDouble(8, a.getTemperaturaIdealEnCelcius());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar artículo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "{CALL sp_articulos_eliminar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar artículo: " + e.getMessage());
            return false;
        }
    }
}
