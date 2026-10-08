package pe.gob.onpe.tramitedoc.web.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.gob.onpe.tramitedoc.bean.EtiquetaBean;
import pe.gob.onpe.tramitedoc.service.EtiquetaDocumentoService;

/**
 *
 * @author NGilt
 */
@Controller
@RequestMapping("/{version}/srEtiquetaDoc.do")
public class EtiquetaDocumentoController {

    @Autowired
    private EtiquetaDocumentoService etiquetaDocumentoService;

    @RequestMapping(method = RequestMethod.POST, params = "accion=goListaEtiquetas")
    private @ResponseBody
    String goListaEtiquetas(HttpServletRequest request, Model model) throws Exception {
        List<EtiquetaBean> listEtiquetas = null;
        listEtiquetas = etiquetaDocumentoService.getListEtiquetas();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(listEtiquetas);
        return json;
    }

}
