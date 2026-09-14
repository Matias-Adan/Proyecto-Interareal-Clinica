package com.clinica.dao;

import com.clinica.modelo.Medicamento;
import com.clinica.config.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoDaoImpl implements IMedicamentoDao {

    @Override
    public List<Medicamento> listar() {
        List<Medicamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM medicamento";
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
    public boolean insertar(Medicamento m) {
        String sql = "INSERT INTO medicamento (...) VALUES (...)";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizar(Medicamento m) {
        String sql = "UPDATE medicamento SET ... WHERE id = ?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM medicamento WHERE id = ?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}