package edu.emmerson.jdeveloper.ojdeploy;

import java.io.Serializable;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;

import org.kohsuke.stapler.DataBoundConstructor;

public class OjdeployRegistry extends AbstractDescribableImpl<OjdeployRegistry> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean disabled = false;
	
	private String name;
	
	private String value;
	
	public OjdeployRegistry() {		
	}

	@DataBoundConstructor
	public OjdeployRegistry(String name, String value, boolean disabled) {
		super();
		this.name = name;
		this.value = value;
		this.setDisabled(disabled);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OjdeployRegistry other = (OjdeployRegistry) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OjdeployRegistry [disabled=" + disabled + ", name=" + name
				+ ", value=" + value + "]";
	}


	@Extension
    public static class DescriptorImpl extends Descriptor<OjdeployRegistry> {
        public String getDisplayName() { return "OjdeployRegistry"; }
    }
	
	
}
