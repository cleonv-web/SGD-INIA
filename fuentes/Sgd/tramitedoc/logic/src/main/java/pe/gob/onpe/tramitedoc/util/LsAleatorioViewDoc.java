/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;


/**
 *
 * @author ecueva
 */
public class LsAleatorioViewDoc {
    private final static LsAleatorioViewDoc instancia = new LsAleatorioViewDoc();    
    private static List<AleatorioViewDoc> list;

    private LsAleatorioViewDoc(){
        list=new ArrayList<AleatorioViewDoc>();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run(){
                LsAleatorioViewDoc.getInstancia().removeForTime();
                //System.out.println(LsAleatorioViewDoc.getInstancia().toString());
            }
        };
        timer.schedule(task,10000, 10000);
    }
    
    public static LsAleatorioViewDoc getInstancia(){
        return instancia;
    }
    
    private List<AleatorioViewDoc> getList() {
        return list;
    }
    
    public String add(){
        AleatorioViewDoc aleatorioViewDoc=new AleatorioViewDoc();
        return getList().add(aleatorioViewDoc)? aleatorioViewDoc.getNroviewDoc():"00000000";
    }
    
    public String add(String nroViewDoc){
        String random = Utilidades.generateRandomNumber(8);
        AleatorioViewDoc aleatorioViewDoc=new AleatorioViewDoc(nroViewDoc+random, new Date().getTime());
        return getList().add(aleatorioViewDoc)? random:"00000000";
    }
    
    public boolean contains(String nroAleatorio){
        return getList().contains(new AleatorioViewDoc(nroAleatorio,0L));
    }    
    
    public boolean remove(String nroAleatorio){
        return getList().remove(new AleatorioViewDoc(nroAleatorio,0L));
    }
    
    private boolean removeForTime(){
        return getList().removeAll(Collections.singleton(new AleatorioViewDoc(null,1L)));
    }
    
    @Override
    public String toString(){
        return this.getList().toString();
    }
}
