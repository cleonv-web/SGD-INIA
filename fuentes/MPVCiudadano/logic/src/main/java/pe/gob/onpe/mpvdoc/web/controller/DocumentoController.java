package pe.gob.onpe.mpvdoc.web.controller;

import java.io.IOException;
import java.util.*;
import javax.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;
import org.codehaus.jackson.map.ObjectMapper;
import pe.gob.onpe.mpvdoc.bean.DocumentoFileBean;
import pe.gob.onpe.mpvdoc.web.util.ApplicationProperties;

@Controller
public class DocumentoController {
    @Autowired private ApplicationProperties applicationProperties;

    @RequestMapping(method=RequestMethod.POST, value="/goUploadDoc", produces="application/json;charset=UTF-8")
    public @ResponseBody String goUploadDoc(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException {
        return upload(request, response, false);
    }
    @RequestMapping(method=RequestMethod.POST, value="/goUploadDocAnexos", produces="application/json;charset=UTF-8")
    public @ResponseBody String goUploadDocAnexos(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException {
        return upload(request, response, true);
    }
    @SuppressWarnings("unchecked")
    @RequestMapping(method=RequestMethod.POST, value="/goRemoveAnexo", produces="application/json;charset=UTF-8")
    public @ResponseBody String removeAnexo(@RequestParam("id") String id, HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) synchronized (session) {
            LinkedList<DocumentoFileBean> files = (LinkedList<DocumentoFileBean>)session.getAttribute("SessionAnexos");
            if (files != null) {
                Iterator<DocumentoFileBean> iterator = files.iterator();
                while (iterator.hasNext()) if (id.equals(iterator.next().getIdDocumento())) iterator.remove();
                session.setAttribute("SessionAnexos", files);
            }
        }
        return "{\"ok\":true}";
    }
    @SuppressWarnings("unchecked")
    private String upload(MultipartHttpServletRequest request, HttpServletResponse response, boolean annex) throws IOException {
        Iterator<String> names = request.getFileNames();
        if (!names.hasNext()) return reject(response, "Selecciona un archivo.");
        MultipartFile file = request.getFile(names.next());
        String name = file.getOriginalFilename();
        if (name == null) return reject(response, "El archivo no tiene nombre.");
        name = name.replace('\\', '/'); name = name.substring(name.lastIndexOf('/') + 1);
        long limit = annex ? Long.parseLong(applicationProperties.getFileSizeMaxAnexo()) * 1024 * 1024 : Long.parseLong(applicationProperties.getFileSizeMaxDocumento());
        if (name.length() > 255 || file.isEmpty() || file.getSize() > limit || !name.toLowerCase(Locale.ROOT).matches(annex ? ".*\\.(pdf|doc|docx|jpg|jpeg)$" : ".*\\.pdf$"))
            return reject(response, "Formato o tamaño de archivo no permitido.");
        byte[] bytes = file.getBytes();
        if (!annex && (bytes.length < 5 || bytes[0] != '%' || bytes[1] != 'P' || bytes[2] != 'D' || bytes[3] != 'F' || bytes[4] != '-'))
            return reject(response, "El documento principal debe ser un PDF válido.");
        HttpSession session = request.getSession();
        Map<String, String> item = new LinkedHashMap<String, String>();
        item.put("name", name); item.put("fileSize", file.getSize() / 1024 + " KB");
        item.put("fileType", file.getContentType() == null ? "application/octet-stream" : file.getContentType());
        synchronized (session) {
            if (annex) {
                LinkedList<DocumentoFileBean> files = (LinkedList<DocumentoFileBean>)session.getAttribute("SessionAnexos");
                if (files == null) files = new LinkedList<DocumentoFileBean>();
                if (files.size() >= 20) return reject(response, "Se alcanzó el máximo de 20 anexos por presentación.");
                DocumentoFileBean meta = new DocumentoFileBean();
                meta.setIdDocumento(UUID.randomUUID().toString()); meta.setNombreArchivo(name);
                meta.setTamanoArchivo(item.get("fileSize")); meta.setTipoArchivo(item.get("fileType")); meta.setArchivoBytes(bytes);
                files.add(meta); session.setAttribute("SessionAnexos", files); item.put("id", meta.getIdDocumento());
            } else {
                session.setAttribute("SessionDoc", bytes); session.setAttribute("SessionDocName", name); item.put("fileString", "");
            }
        }
        return new ObjectMapper().writeValueAsString(Collections.singletonList(item));
    }
    private String reject(HttpServletResponse response, String text) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return new ObjectMapper().writeValueAsString(Collections.singletonMap("error", text));
    }
}
