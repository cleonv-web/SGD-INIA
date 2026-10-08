/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.web.util;

import java.util.regex.*;

/**
 *
 * @author WCutipa
 */
public class BusquedaTextual {
    private static boolean instanciated = false;
    private static BusquedaTextual instancia;

  private static final String RESERVED_CHARS[][] =
    { {"\\","\\\\"},
      {",","\\,"},
      {"&","\\&"},
      {"=","\\="},
      {"{","\\{"},
      {"}","\\}"},
      //{"(","\\("},                      // enabled, do NOT escape
      //{")","\\)"},                      // enabled, do NOT escape
      {"[","\\["},
      {"]","\\]"},
      {"-","\\-"},
      {";","\\;"},
      {"~","\\~"},
      {"|","\\|"},
      {"$","\\$"},
      {"!","\\!"},
      {">","\\>"},
      // {"%","\\%"},                     // enabled, do NOT escape
      {"_","\\_"},
      {"*","%"},                          // used as wildcard
      {"?","_"} };                        // used as wildcard    

  public static final char    GROUP_DELIMITER       = '"';    // group delimiter
  public static final char    RANGE_SIGN            = '-';    // indicate a range
  public static final char    WILDCARD_SINGLE       = '?';    // wildcard for a single char (1)
  public static final char    WILDCARD_MULTI        = '*';    // wildcard for multiple or none chars (0..n)
  
  
    private BusquedaTextual(){
    }

    public static BusquedaTextual getInstancia(){
        if(!BusquedaTextual.instanciated){
            BusquedaTextual.instancia = new BusquedaTextual();
            BusquedaTextual.instanciated = true;
        }
        return BusquedaTextual.instancia;
    }

    public static String getContextValue(String textoBusca){
        String retval=null;
        textoBusca = Utilidades.reduceWhitespace(textoBusca);
        String [] vect = textoBusca.split(" ");        
        retval = BusquedaTextual.getContextAvanzado(vect);
        
        return retval;
    }
    
    public static String getContextMixto(String textoBusca){
        String retval="<query>\n" +
                        "<textquery lang=\"SPANISH\" grammar=\"CONTEXT\">" +textoBusca+"  \n" +
                        "    <progression>\n" +
                        "      <seq><rewrite>transform((TOKENS, \"{\", \"}\", \";\"))</rewrite></seq>\n" +
                        "      <seq><rewrite>transform((TOKENS, \"?{\", \"}\", \";\"))</rewrite>/seq>\n" +
                        "      <seq><rewrite>transform((TOKENS, \"${\", \"}\", \";\"))</rewrite></seq>\n" +
                        "      <seq><rewrite>transform((TOKENS, \"\", \"%\", \";\"))</rewrite></seq>\n" +
                        "    </progression>\n" +
                        "</textquery>\n" +
                        "<score datatype=\"INTEGER\" algorithm=\"COUNT\"/>\n" +
                        "</query>";
        
        return retval;
    }

    public static String getContextSimple(String textoBusca){
        String retval="<query>\n" +
                        "<textquery lang=\"SPANISH\" grammar=\"CONTEXT\">" +textoBusca+"  \n" +
                        "    <progression>\n" +
                        "      <seq><rewrite>transform((TOKENS, \"{\", \"}\", \";\"))</rewrite></seq>\n" +
                        "      <seq><rewrite>transform((TOKENS, \"?{\", \"}\", \";\"))</rewrite>/seq>\n" +
                        "      <seq><rewrite>transform((TOKENS, \"${\", \"}\", \";\"))</rewrite></seq>\n" +
                        "      <seq><rewrite>transform((TOKENS, \"\", \"%\", \";\"))</rewrite>/seq>\n" +
                        "    </progression>\n" +
                        "</textquery>\n" +
                        "<score datatype=\"INTEGER\" algorithm=\"COUNT\"/>\n" +
                        "</query>";
        
        return retval;
    }

    public static String getContextAvanzado(String [] vect ){
        StringBuffer retval = new StringBuffer();
        int tipoBusca = 0;  // 0:normal con parecidos y like; 1:Normal con Parecidos 2: Busqueda Exacta
        int ini = 1;
        
        for(String s:vect)
        {
            // Verificamos si el caracter es el -
            if (s.length()>1) {
                tipoBusca = 0;
                if (s.charAt(0) != RANGE_SIGN){
                    if (ini>1){
                        retval.append(" AND (");
                    }else{
                        retval.append("(");
                    }
                }else{
                    retval.append(" NOT(");
                    s = s.substring(1);
                    tipoBusca = 1;
                }
                
                if (s.length()<=3){
                    tipoBusca = 1;
                }
                        
                if (s.charAt(0) == GROUP_DELIMITER){
                    tipoBusca=2; 
                }
                
                if (tipoBusca == 0){
                    retval.append("{"+s+"}");
                    retval.append(" OR ");
                    retval.append("?{"+s+"}");
                    /*retval.append(" OR ");
                    retval.append("${"+s+"}");*/
                    retval.append(" OR ");
                    retval.append(s+"%");
                    retval.append(") ");                    
                }else if(tipoBusca == 1){
                    retval.append("{"+s+"}");
                    retval.append(" OR ");
                    retval.append("?{"+s+"}");
                    /*retval.append(" OR ");
                    retval.append("${"+s+"}");*/
                    retval.append(") ");                    
                }else if(tipoBusca == 2){
                    retval.append("{"+s+"}");
                    retval.append(") ");                    
                }

                ini++;
            }
        }
        
        return retval.toString();
    }
    

    
}
