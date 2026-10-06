package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.Articulos;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArticulosDaoImpl implements IArticulosDao {

    @Override
    public List<Articulos> listar() {
        List<Articulos> lista = new ArrayList<>();
        String sql = "{CALL sp_listar_todo(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Articulos a = new Articulos(
                		rs.getInt("idArticulo"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("presentacion"),
                        rs.getString("tipoArticulo"),
                        rs.getInt("stockActual"),
                        rs.getInt("stockMinimo")
                );
                lista.add(a);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar artículos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Articulos buscarPorId(int id) {
        Articulos a = null;
        String sql = "{CALL sp_articulos_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    a = new Articulos(
                        rs.getInt("idArticulo"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("presentacion"),
                        rs.getString("tipoArticulo"),
                        rs.getInt("stockActual"),
                        rs.getInt("stockMinimo")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar artículo: " + e.getMessage());
        }
        return a;
    }

    @Override
    public boolean insertar(Articulos a) {
        // Se asume que el procedimiento recibe: nombre, presentacion, tipo_articulo, descripcion, stock_actual, stock_minimo
        String sql = "{CALL sp_articulos_insertar(?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setString(1, a.getNombre());
            cs.setString(2, a.getPresentacion());
            cs.setString(3, a.getTipoArticulo());
            cs.setString(4, a.getDescripcion());
            cs.setInt(5, a.getStockActual());
            cs.setInt(6, a.getStockMinimo());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar artículo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Articulos a) {
        // Se asume que el procedimiento recibe: id, nombre, presentacion, tipo_articulo, descripcion, stock_actual, stock_minimo
        String sql = "{CALL sp_articulos_actualizar(?, ?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, a.getIdArticulo());
            cs.setString(2, a.getNombre());
            cs.setString(3, a.getPresentacion());
            cs.setString(4, a.getTipoArticulo());
            cs.setString(5, a.getDescripcion());
            cs.setInt(6, a.getStockActual());
            cs.setInt(7, a.getStockMinimo());

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
