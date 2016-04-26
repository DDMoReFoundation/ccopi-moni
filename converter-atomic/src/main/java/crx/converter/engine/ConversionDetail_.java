/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import eu.ddmore.convertertoolbox.api.response.ConversionDetail;

/**
 * Conversion detail class.
 */
public class ConversionDetail_ implements ConversionDetail {
	private File file = null;
	private Map<String, String> info = new HashMap<String, String>();
	private Severity severity = Severity.INFO;
	private String message = null;
	
	@Override
	public void addInfo(String key, String value) {
		if (info != null) info.put(key, value);
	}
	
	@Override
	public Map<String, String> getInfo() {
		return info;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
	@Override
	public Severity getServerity() {
		return severity;
	}
	
	@Override
	public void setInfo(Map<String, String> info_) {
		info = info_; 	
	}
	
	@Override
	public void setMessage(String msg) {
		if (msg != null) message = msg;
	}
	
	@Override
	public void setSeverity(Severity s) {
		severity = s;
	}
	
	public File getFile() {
		return file;
	}

	public void setFile(File value) {
		file = value;
	}
}
