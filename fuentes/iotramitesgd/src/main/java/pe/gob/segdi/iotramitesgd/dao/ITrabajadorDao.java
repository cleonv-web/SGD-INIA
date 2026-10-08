package pe.gob.segdi.iotramitesgd.dao;

import pe.gob.segdi.iotramitesgd.bean.AuditoriaLogin;
import pe.gob.segdi.iotramitesgd.bean.Trabajador;

import java.util.List;


public interface ITrabajadorDao {

    Trabajador getTrabajador(String ususario) throws Exception;

    void insAuditoriaLogin(int cod_trabajador, int flag_acc) throws Exception;

    List<AuditoriaLogin> getAuditoriaLogin(int cod_trabajador) throws Exception;

    void updEstadoTrabajador(int cod_trabajador) throws Exception;
}
