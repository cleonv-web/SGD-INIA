package pe.gob.segdi.iotramitesgd.bean;

import javax.faces.model.SelectItemGroup;
import java.io.Serializable;

public class CboGrupoBean implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private SelectItemGroup grupo;

    public SelectItemGroup getGrupo() {
        return grupo;
    }

    public void setGrupo(SelectItemGroup grupo) {
        this.grupo = grupo;
    }


}
