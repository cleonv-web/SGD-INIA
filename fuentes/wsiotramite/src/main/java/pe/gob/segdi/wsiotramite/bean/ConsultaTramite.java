package pe.gob.segdi.wsiotramite.bean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultaTramite", propOrder = {
		"vrucentrem",
		"vcuo"
		
})
public class ConsultaTramite implements Serializable{

	private static final long serialVersionUID = 1L;
	private String vrucentrem;
	private String vcuo;
		
	
	public String getVrucentrem() {
		return vrucentrem;
	}
	public void setVrucentrem(String vrucentrem) {
		this.vrucentrem = vrucentrem;
	}
	public String getVcuo() {
		return vcuo;
	}
	public void setVcuo(String vcuo) {
		this.vcuo = vcuo;
	}
	
	

}
