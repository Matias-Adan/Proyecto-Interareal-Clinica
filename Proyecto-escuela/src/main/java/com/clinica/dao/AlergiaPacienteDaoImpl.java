package com.clinica.dao;

import com.clinica.config.Conexion;
import com.clinica.modelo.AlergiaPaciente;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlergiaPacienteDaoImpl implements IAlergiaPacienteDao {

    @Override
    public List<AlergiaPaciente> listar() {
        List<AlergiaPaciente> lista = new ArrayList<>();
        String sql = "{CALL sp_seleccionar(\"alergiapaciente\")}";
        
        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                AlergiaPaciente ap = new AlergiaPaciente(
                        rs.getInt("idAlergiaPaciente"),
                        rs.getInt("pacienteId"),
                        rs.getInt("alergiaId")
                );
                lista.add(ap);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar alergias de pacientes: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public AlergiaPaciente buscarPorId(int id) {
        AlergiaPaciente ap = null;
        String sql = "{CALL sp_alergiapaciente_listar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    ap = new AlergiaPaciente(
                            rs.getInt("idAlergiaPaciente"),
                            rs.getInt("pacienteId"),
                            rs.getInt("alergiaId")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar alergia de paciente: " + e.getMessage());
        }
        return ap;
    }

    @Override
    public boolean insertar(AlergiaPaciente ap) {
        String sql = "{CALL sp_alergiapaciente_insertar(?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, ap.getPacienteId());
            cs.setInt(2, ap.getAlergiaId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar alergia de paciente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(AlergiaPaciente ap) {
        String sql = "{CALL sp_alergiapaciente_actualizar(?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, ap.getIdAlergiaPaciente());
            cs.setInt(2, ap.getPacienteId());
            cs.setInt(3, ap.getAlergiaId());

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar alergia de paciente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "{CALL sp_alergiapaciente_eliminar(?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar alergia de paciente: " + e.getMessage());
            return false;
        }
    }
}