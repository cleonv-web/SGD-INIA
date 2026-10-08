/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.util;

/**
 *
 * @author oti2
 */
public class ProcessResult<T>  {
     private boolean IsSuccess;
     private T Result ;
     private String Message;
    private String Codigo;
    private String Exception;
   private String TiAcceso;

  public String getTiAcceso() {
        return TiAcceso;
    }

    public void setTiAcceso(String TiAcceso) {
        this.TiAcceso = TiAcceso;
    }

   public String getCodigo() {
        return Codigo;
    }
    public String getException() {
        return Exception;
    }

    public void setCodigo(String Codigo) {
        this.Codigo = Codigo;
    }
   public String getMessage() {
        return Message;
    }
    public void setException(String Exception) {
        this.Exception = Exception;
    }
    public void setMessage(String Message) {
        this.Message = Message;
    }

    public T getResult() {
        return Result;
    }

    public void setResult(T Result) {
        this.Result = Result;
    }
     
     public boolean isIsSuccess() {
        return IsSuccess;
    }

    public void setIsSuccess(boolean IsSuccess) {
        this.IsSuccess = IsSuccess;
    }
}
