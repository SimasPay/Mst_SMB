package appselector.com.mfino.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
 /**
  * 
  * @author Shashank
  *
  */

@Entity
@Table(name = "system_parameters")
public class AppDetails {
 
	@Id 
	@GeneratedValue
	@Column(name = "ID" )
	private int id;
		
	@Column(name = "ParameterName")
 	private String ParameterName;

	@Column(name = "ParameterValue")
	private String ParameterValue;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getParameterName() {
		return ParameterName;
	}
	public void setParameterName(String parameterName) {
		ParameterName = parameterName;
	}
	public String getParameterValue() {
		return ParameterValue;
	}
	public void setParameterValue(String parameterValue) {
		ParameterValue = parameterValue;
	}
}
