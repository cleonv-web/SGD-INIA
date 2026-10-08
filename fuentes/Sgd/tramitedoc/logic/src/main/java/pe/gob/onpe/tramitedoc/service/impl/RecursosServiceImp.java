package pe.gob.onpe.tramitedoc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.tramitedoc.service.RecursosService;

import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: crosales
 * Date: 25/03/12
 * Time: 10:30 AM
 * To change this template use File | Settings | File Templates.
 */
@Service(value = "recursosService")
public class RecursosServiceImp implements RecursosService {
  /*  @Autowired
    private GetrDominiosCore getrDominiosCore;

    @Autowired
    private GetrNivelEducativoCore getrNivelEducativoCore;

    @Autowired
    private GetrSubtipoTramiteCore getrSubtipoTramiteCore;

    @Autowired
    private GetrEstadoCivilCore getrEstadoCivilCore;

    @Autowired
    private GetrObservacionCore getrObservacionCore;

    @Autowired
    private CttmDiagnosticoDiscapacidadCore cttmDiagnosticoDiscapacidadCore;
*/
    @Override
    public String getAllObjects() {
        StringBuffer retval = new StringBuffer();

        boolean sucessfull = false;
        StringBuffer prepare = new StringBuffer();
        try {
            /*prepare.append("'subtipotramite':" + getrSubtipoTramiteCore.allListToJson());
            prepare.append(",'domRestriccion':" + getrDominiosCore.getTodosRestriccionInJson());
            prepare.append(",'domDiscapacidad':" + getrDominiosCore.getTodosTipoDiscapacidadInJson());
            prepare.append(",'domInterdiccion':" + getrDominiosCore.getTodosTipoInterdiccionInJson());
            prepare.append(",'niveleduca':" + getrNivelEducativoCore.getTodosNivelesInJson());
            prepare.append(",'estadocivil':" + getrEstadoCivilCore.getTodosEstadoCivilInJson());
            prepare.append(",'observacion':" + getrObservacionCore.getObservacionesTodosInJson());
            prepare.append(",'diagnostico':" + cttmDiagnosticoDiscapacidadCore.getTodosDiagnosticoInJson());*/
                 //prepare.append(",tipooper:" + serializeTipoOperacion(tipoOPeracionDao.getAny(ocon, tipoOPeracionBean)));       //tipoOPeracionBean tiene seteado en año
                 //prepare.append(",planctas:" + planctascore.getAll(ocon, pAnio));
                 //prepare.append((!ptables.equals("x"))?",planctas:" + planctascore.getAll(ocon, pAnio):" ");
            sucessfull = true;
        }
        catch(Exception e)
        {
            sucessfull = false;
            e.printStackTrace();
        }
        retval.append("{'retval':");
        retval.append(sucessfull);
        /*retval.append(", ");
        retval.append(prepare);*/
        retval.append("}");

        return retval.toString();
    }

}
