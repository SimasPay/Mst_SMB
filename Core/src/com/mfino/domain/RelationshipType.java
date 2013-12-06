package com.mfino.domain;

/**
 * 
 * @author Sasi
 *
 */
public class RelationshipType {
	
	private Integer ID;
	private String description;
	
	public Integer getID() {
		return ID;
	}

	public void setID(Integer iD) {
		this.ID = iD;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if(obj == null){
            return false;
        } else {
            if(getID() != null){
            	return (getID().equals(((RelationshipType)obj).getID()));
            }
            
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		if(null != this.getID()){
			return this.getID().intValue();
		}
		
		return -1;
	}
	
	@Override
	public String toString() {
		return "ID:"+getID() + ", Description:"+getDescription();
	}
}
