package pe.gob.segdi.iotramitesgd.controller;

import org.jboss.logging.Logger;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;
import org.primefaces.model.charts.pie.PieChartModel;
import pe.gob.segdi.iotramitesgd.bean.Reporte;
import pe.gob.segdi.iotramitesgd.service.IReporteService;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Objeto : LoginMB.java.
 * Descripción : Clase controladora de logueo de documentos, contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 * <p>
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 */

@ManagedBean("cReporte")
@SessionScoped
public class CReporteController extends BaseController {
    private static Logger depurador = Logger.getLogger(CReporteController.class.getName());
    private static final long serialVersionUID = 1L;

    private PieChartModel pieModelDespacho;
    private PieChartModel pieModelRecepcion;

    private DonutChartModel donutModelDespacho;
    private DonutChartModel donutModelRecepcion;
    private DonutChartModel donutModelDespachoPendienetesEnviadosRecepcionados;
    private DonutChartModel donutModelRecepcionPendientesRecepcionados;

    private BarChartModel barModelEnviadosRecepcionados;


    private Date dfecdesde;
    private Date dfechasta;
    private String fecdesde = "";
    private String fechasta = "";

    //@ManagedProperty(value = "#{iReporteService}")
    @Inject
    private IReporteService iReporteService;


    @PostConstruct
    public void init() {
        inicializarVariable();
    }

    @Override
    public String inicializarControladora() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String inicializarControladora(String x) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void inicializarVariable() {

        try {
            dfecdesde = new Date();
            dfechasta = new Date();

        } catch (Exception e) {
            depurador.error(null, e);
        }


    }

    @Override
    public void limpiarObjectos() {
        // TODO Auto-generated method stub

    }

    public String formReporteXBandejas() {
//		reporteXBandejasPie();
//		reporteXBandejasBar();
        return "reporteXBandejas";
    }


    public void reporteXBandejasPie() {
        //depurador.info("reporteBandejas ==>");
        try {
            Date fec = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy");
            String anio = format.format(fec);
            List<Reporte> lstReporte = iReporteService.getBandejaDespacho(anio);

            donutModelDespachoPendienetesEnviadosRecepcionados = new DonutChartModel();
            ChartData data = new ChartData();
            DonutChartDataSet dataSetPendientesEnviados = new DonutChartDataSet();
            List<Number> values = new ArrayList<>();
            List<String> bgColors = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            for (int i = 0; i < lstReporte.size(); i++) {
                if (lstReporte.get(i).getCflgest().equals("01")) {
                    values.add(lstReporte.get(i).getCantidad());
                    bgColors.add("rgb(255, 99, 132)");
                    labels.add("Pendientes");
                } else if (lstReporte.get(i).getCflgest().equals("02")) {
                    values.add(lstReporte.get(i).getCantidad());
                    bgColors.add("rgb(54, 162, 235)");
                    labels.add("Enviados");
                } else if (lstReporte.get(i).getCflgest().equals("03")) {
                    values.add(lstReporte.get(i).getCantidad());
                    bgColors.add("rgb(000,247,000)");
                    labels.add("Recepcionados");
                }
            }

            dataSetPendientesEnviados.setData(values);
            dataSetPendientesEnviados.setBackgroundColor(bgColors);
            data.addChartDataSet(dataSetPendientesEnviados);
            data.setLabels(labels);
            donutModelDespachoPendienetesEnviadosRecepcionados.setData(data);

            lstReporte = iReporteService.getBandejaRecepcion(anio);
            donutModelRecepcionPendientesRecepcionados = new DonutChartModel();
            data = new ChartData();
            DonutChartDataSet dataSetRecepcionadosObsevadosSubsanados = new DonutChartDataSet();
            values = new ArrayList<>();
            bgColors = new ArrayList<>();
            labels = new ArrayList<>();

            for (int i = 0; i < lstReporte.size(); i++) {
                if (lstReporte.get(i).getCflgest().equals("01")) {
                    values.add(lstReporte.get(i).getCantidad());
                    bgColors.add("rgb(255, 99, 132)");
                    labels.add("Pendientes");
                } else if (lstReporte.get(i).getCflgest().equals("02")) {
                    bgColors.add("rgb(000,247,000)");
                    values.add(lstReporte.get(i).getCantidad());
                    labels.add("Recepcionados");
                }
            }

            dataSetRecepcionadosObsevadosSubsanados.setData(values);
            dataSetRecepcionadosObsevadosSubsanados.setBackgroundColor(bgColors);
            data.addChartDataSet(dataSetRecepcionadosObsevadosSubsanados);
            data.setLabels(labels);
            donutModelRecepcionPendientesRecepcionados.setData(data);


        } catch (Exception e) {
            depurador.error(null, e);
        }

    }

    public void reporteXBandejasBar() {
        //depurador.info("reporteXBandejasBar ==>");
        try {

            Date fec = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy");
            String anio = format.format(fec);
            ChartData data = new ChartData();
            List<Reporte> lstReporte = iReporteService.getBandejaDespachoTotalEnviados(anio);

            barModelEnviadosRecepcionados = new BarChartModel();
            data = new ChartData();
            BarChartDataSet dataSetEnviados = new BarChartDataSet();
            dataSetEnviados.setLabel("Enviados");
            dataSetEnviados.setBackgroundColor("rgba(255,164,32,0.2)");
            dataSetEnviados.setBorderColor("rgb(255,164,32)");
            dataSetEnviados.setBorderWidth(1);
            List<Number> values = new ArrayList<>();
            values = new ArrayList<>();

            for (int i = 0; i < lstReporte.size(); i++) {
                if (lstReporte.get(i).getVmes().equals("01")) {
                    values.add(lstReporte.get(i).getCantidad());
                } else if (lstReporte.get(i).getVmes().equals("02")) {
                    values.add(lstReporte.get(i).getCantidad());
                } else if (lstReporte.get(i).getVmes().equals("03")) {
                    values.add(lstReporte.get(i).getCantidad());
                } else if (lstReporte.get(i).getVmes().equals("04")) {
                    values.add(lstReporte.get(i).getCantidad());
                } else if (lstReporte.get(i).getVmes().equals("05")) {
                    values.add(lstReporte.get(i).getCantidad());
                } else if (lstReporte.get(i).getVmes().equals("06")) {
                    values.add(lstReporte.get(i).getCantidad());
                } else if (lstReporte.get(i).getVmes().equals("07")) {
                    values.add(lstReporte.get(i).getCantidad());
                } else if (lstReporte.get(i).getVmes().equals("08")) {
                    values.add(lstReporte.get(i).getCantidad());
                } else if (lstReporte.get(i).getVmes().equals("09")) {
                    values.add(lstReporte.get(i).getCantidad());
                } else if (lstReporte.get(i).getVmes().equals("10")) {
                    values.add(lstReporte.get(i).getCantidad());
                } else if (lstReporte.get(i).getVmes().equals("11")) {
                    values.add(lstReporte.get(i).getCantidad());
                } else if (lstReporte.get(i).getVmes().equals("12")) {
                    values.add(lstReporte.get(i).getCantidad());
                }
            }

            dataSetEnviados.setData(values);
            data.addChartDataSet(dataSetEnviados);

            List<Reporte> lstReporte2 = iReporteService.getBandejaRecepcionTotalRecepcionados(anio);
            //data = new ChartData();
            BarChartDataSet dataSetRecepcionados = new BarChartDataSet();
            dataSetRecepcionados.setLabel("Recepcionados");
            dataSetRecepcionados.setBackgroundColor("rgba(000,247,000,0.2)");
            dataSetRecepcionados.setBorderColor("rgb(000,247,000)");
            dataSetRecepcionados.setBorderWidth(1);
            List<Number> values2 = new ArrayList<>();
            values2 = new ArrayList<>();

            for (int i = 0; i < lstReporte2.size(); i++) {
                if (lstReporte2.get(i).getVmes().equals("01")) {
                    values2.add(lstReporte2.get(i).getCantidad());
                } else if (lstReporte2.get(i).getVmes().equals("02")) {
                    values2.add(lstReporte2.get(i).getCantidad());
                } else if (lstReporte2.get(i).getVmes().equals("03")) {
                    values2.add(lstReporte2.get(i).getCantidad());
                } else if (lstReporte2.get(i).getVmes().equals("04")) {
                    values2.add(lstReporte2.get(i).getCantidad());
                } else if (lstReporte2.get(i).getVmes().equals("05")) {
                    values2.add(lstReporte2.get(i).getCantidad());
                } else if (lstReporte2.get(i).getVmes().equals("06")) {
                    values2.add(lstReporte2.get(i).getCantidad());
                } else if (lstReporte2.get(i).getVmes().equals("07")) {
                    values2.add(lstReporte2.get(i).getCantidad());
                } else if (lstReporte2.get(i).getVmes().equals("08")) {
                    values2.add(lstReporte2.get(i).getCantidad());
                } else if (lstReporte2.get(i).getVmes().equals("09")) {
                    values2.add(lstReporte2.get(i).getCantidad());
                } else if (lstReporte2.get(i).getVmes().equals("10")) {
                    values2.add(lstReporte2.get(i).getCantidad());
                } else if (lstReporte2.get(i).getVmes().equals("11")) {
                    values2.add(lstReporte2.get(i).getCantidad());
                } else if (lstReporte2.get(i).getVmes().equals("12")) {
                    values2.add(lstReporte2.get(i).getCantidad());
                }
            }

            dataSetRecepcionados.setData(values2);
            data.addChartDataSet(dataSetRecepcionados);
            List<String> labels = new ArrayList<>();
            labels.add("Enero");
            labels.add("Febrero");
            labels.add("Marzo");
            labels.add("Abril");
            labels.add("Mayo");
            labels.add("Junio");
            labels.add("Julio");
            labels.add("Agosto");
            labels.add("Septiembre");
            labels.add("Octubre");
            labels.add("Noviembre");
            labels.add("Diciembre");

            data.setLabels(labels);

            barModelEnviadosRecepcionados.setData(data);

            BarChartOptions options = new BarChartOptions();
            CartesianScales cScales = new CartesianScales();
            CartesianLinearAxes linearAxes = new CartesianLinearAxes();
            CartesianLinearTicks ticks = new CartesianLinearTicks();
            ticks.setBeginAtZero(true);
            linearAxes.setTicks(ticks);
            cScales.addYAxesData(linearAxes);
            options.setScales(cScales);


            barModelEnviadosRecepcionados.setOptions(options);

//	        Title title = new Title();
//	        title.setDisplay(true);
//	        title.setText("Bar Chart");
//	        options.setTitle(title);

//	        Legend legend = new Legend();
//	        legend.setDisplay(true);
//	        legend.setPosition("top");
//	        LegendLabel legendLabels = new LegendLabel();
//	        legendLabels.setFontStyle("bold");
//	        legendLabels.setFontColor("#2980B9");
//	        legendLabels.setFontSize(24);
//	        legend.setLabels(legendLabels);
//	        options.setLegend(legend);


        } catch (Exception e) {
            depurador.error(null, e);
        }

    }

    public String formReporteXBandejasXMes() {
        return "reporteXBandejasXMes";
    }

    public String formReporteXBandejasXFecha() {
        reporteXBandejasXFecha();
        return "reporteXBandejasXFecha";
    }

    public String reporteXBandejasXFecha() {
        //depurador.info("reporteXBandejasXFecha ==>");
        try {

            List<Reporte> lstReporte = new ArrayList<Reporte>();
            lstReporte = iReporteService.getBandejaDespachoXFecha(dfecdesde, dfechasta);
            pieModelDespacho = new PieChartModel();

            int contaPendiente = 0;
            int contaEnviado = 0;
            int contaRecepcionado = 0;
            int contaObservado = 0;
            int contaSubsanado = 0;

            for (int i = 0; i < lstReporte.size(); i++) {
                if (lstReporte.get(i).getCflgest().equals("P")) {
                    contaPendiente = lstReporte.get(i).getCantidad();
                } else if (lstReporte.get(i).getCflgest().equals("E")) {
                    contaEnviado = lstReporte.get(i).getCantidad();
                } else if (lstReporte.get(i).getCflgest().equals("R")) {
                    contaRecepcionado = lstReporte.get(i).getCantidad();
                } else if (lstReporte.get(i).getCflgest().equals("O")) {
                    contaObservado = lstReporte.get(i).getCantidad();
                } else if (lstReporte.get(i).getCflgest().equals("S")) {
                    contaSubsanado = lstReporte.get(i).getCantidad();
                }
            }

//			pieModelDespacho.set("Pendiente", contaPendiente);
//			pieModelDespacho.set("Enviados", contaEnviado);
//			pieModelDespacho.set("Recepcionados", contaRecepcionado);
//			pieModelDespacho.set("Observados", contaObservado);
//			pieModelDespacho.set("Subsanados", contaSubsanado);
//			
//			pieModelDespacho.setTitle("Bandeja Despacho");
//			pieModelDespacho.setLegendPosition("w");
//			pieModelDespacho.setShadow(false);
//			pieModelDespacho.setSeriesColors("FD0905,EFFE07,07FE0E,078EFE,FC9100");
//			pieModelDespacho.setShowDataLabels(true);
//			pieModelDespacho.setDataFormat("value");
//			

            lstReporte = iReporteService.getBandejaRecepcionXFecha(dfecdesde, dfechasta);
            pieModelRecepcion = new PieChartModel();

            contaPendiente = 0;
            contaEnviado = 0;
            contaRecepcionado = 0;
            contaObservado = 0;
            contaSubsanado = 0;

            for (int i = 0; i < lstReporte.size(); i++) {
                if (lstReporte.get(i).getCflgest().equals("P")) {
                    contaPendiente = lstReporte.get(i).getCantidad();
                } else if (lstReporte.get(i).getCflgest().equals("R")) {
                    contaRecepcionado = lstReporte.get(i).getCantidad();
                } else if (lstReporte.get(i).getCflgest().equals("O")) {
                    contaObservado = lstReporte.get(i).getCantidad();
                }
            }

//			pieModelRecepcion.set("Pendiente", contaPendiente);
//			pieModelRecepcion.set("Recepcionado", contaRecepcionado);
//			pieModelRecepcion.set("Observado", contaObservado);
//		
//			pieModelRecepcion.setTitle("Bandeja Recepcion");
//			pieModelRecepcion.setLegendPosition("w");
//			pieModelRecepcion.setShadow(false);
//			pieModelRecepcion.setSeriesColors("FD0905,07FE0E,078EFE");
//			pieModelDespacho.setShowDataLabels(true);
//			pieModelDespacho.setDataFormat("value");


        } catch (Exception e) {
            depurador.error(null, e);
        }
        return "reporteBandejasXFecha";
    }


    private boolean validateLogin() {
        //depurador.info("validateLogin ==>");
        boolean validLogin = false;
//	    String pattern = "^[a-zA-Z0-9_]+$"; // alphanumeric chars and underscores only
//
//	    if (usuario.equals("")) {
//			addInfoMessage("Campos Obligatorios", "Debe ingresar el usuario");
//		}else if (password.equals("")) {
//			addInfoMessage("Campos Obligatorios", "Debe ingresar el password");
//		}else{
//			validLogin = usuario.matches(pattern) && password.matches(pattern);
//		    if (!validLogin) {
//		      errorMessage = "Autenticación Invalida";
//		      addWarnMessage("Atención", errorMessage);
//		    }
//		}
        return validLogin;
    }




    /*
     * **************************************************
     * **************************************************/

//    public IReporteService getiReporteService() {
//        return iReporteService;
//    }
//
//    public void setiReporteService(IReporteService iReporteService) {
//        this.iReporteService = iReporteService;
//    }


    public PieChartModel getPieModelDespacho() {
        return pieModelDespacho;
    }

    public PieChartModel getPieModelRecepcion() {
        return pieModelRecepcion;
    }

    public Date getDfecdesde() {
        return dfecdesde;
    }

    public void setDfecdesde(Date dfecdesde) {
        this.dfecdesde = dfecdesde;
    }

    public Date getDfechasta() {
        return dfechasta;
    }

    public void setDfechasta(Date dfechasta) {
        this.dfechasta = dfechasta;
    }

    public DonutChartModel getDonutModelDespacho() {
        return donutModelDespacho;
    }

    public DonutChartModel getDonutModelRecepcion() {
        return donutModelRecepcion;
    }


    public BarChartModel getBarModelEnviadosRecepcionados() {
        return barModelEnviadosRecepcionados;
    }

    public void setBarModelEnviadosRecepcionados(BarChartModel barModelEnviadosRecepcionados) {
        this.barModelEnviadosRecepcionados = barModelEnviadosRecepcionados;
    }

    public DonutChartModel getDonutModelDespachoPendienetesEnviadosRecepcionados() {
        return donutModelDespachoPendienetesEnviadosRecepcionados;
    }

    public void setDonutModelDespachoPendienetesEnviadosRecepcionados(
            DonutChartModel donutModelDespachoPendienetesEnviadosRecepcionados) {
        this.donutModelDespachoPendienetesEnviadosRecepcionados = donutModelDespachoPendienetesEnviadosRecepcionados;
    }

    public DonutChartModel getDonutModelRecepcionPendientesRecepcionados() {
        return donutModelRecepcionPendientesRecepcionados;
    }

    public void setDonutModelRecepcionPendientesRecepcionados(DonutChartModel donutModelRecepcionPendientesRecepcionados) {
        this.donutModelRecepcionPendientesRecepcionados = donutModelRecepcionPendientesRecepcionados;
    }


}