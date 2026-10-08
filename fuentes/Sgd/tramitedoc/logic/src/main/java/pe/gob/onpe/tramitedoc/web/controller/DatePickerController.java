/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.web.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.ConAnioBean;
import pe.gob.onpe.tramitedoc.bean.ConDiaBean;
import pe.gob.onpe.tramitedoc.bean.ConMesBean;
import pe.gob.onpe.tramitedoc.bean.DatePickerBean;
import pe.gob.onpe.tramitedoc.service.ConReferencedData;

/**
 *
 * @author NGilt
 */
@Controller
@RequestMapping("/{version}/srDatePicker.do")
public class DatePickerController {

    @Autowired
    private ConReferencedData conReferencedData;

    @RequestMapping(method = RequestMethod.POST, params = "accion=goCargarDatePicker")
    public String goCargarDatePicker(HttpServletRequest request, Model model) {

        String anio = Utility.getInstancia().dateToFormatStringYYYY(new Date());
        Calendar cal = Calendar.getInstance();
        String mes = Integer.toString(cal.get(Calendar.MONTH) + 1);
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        String fMesActualDiaInicial = "01/" + mes + "/" + anio;
        String fMesActualDiaFinal = Integer.toString(maxDay) + "/" + mes + "/" + anio;

        DatePickerBean datePicker = new DatePickerBean(anio, mes);
        List<ConAnioBean> anioList = conReferencedData.getConAnioList();
        List<ConMesBean> mesList = conReferencedData.getConMesList();
        List<ConDiaBean> diaList = conReferencedData.getConDiaList();
        String anioInicial="";
        String anioFinal="";
        if (anioList.size() > 0) {
            anioInicial=anioList.get(anioList.size()-1).getCoAnio();
            anioFinal=anioList.get(0).getCoAnio();
        }
        
        model.addAttribute("configDatePicker", datePicker);
        model.addAttribute("conAnioList", anioList);
        model.addAttribute("conMesList", mesList);
        model.addAttribute("conDiaList", diaList);
        model.addAttribute("conMesActualFInicial", fMesActualDiaInicial);
        model.addAttribute("conMesActualFFinal", fMesActualDiaFinal);
        model.addAttribute("anioDataPickerInicial", anioInicial);
        model.addAttribute("anioDataPickerFinal", anioFinal);

        return "datePicker";
    }
}
