package com.clinica.dao;

import com.clinica.modelo.Estudio;
import com.clinica.config.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstudioDaoImpl implements IEstudioDao {

    @Override
    public List<Estudio> listar() {
        List<Estudio> lista = new ArrayList<>();
        String sql = "SELECT * FROM estudio";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
               
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean insertar(Estudio e) {
        String sql = "INSERT INTO estudio (...) VALUES (...)";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizar(Estudio e) {
        String sql = "UPDATE estudio SET ... WHERE id = ?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM estudio WHERE id = ?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}