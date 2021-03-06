/*******************************************************************************
 * Copyright (C) 2013 Mango Solutions Ltd - All rights reserved.
 ******************************************************************************/
package eu.ddmore.convertertoolbox.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import eu.ddmore.convertertoolbox.api.response.ConversionDetail;

/**
 * Bean representing details generated by a conversion activity
 */
public final class ConversionDetailImpl implements ConversionDetail {

    private Severity severity;
    private Map<String, String> info;
    private String message;

    public ConversionDetailImpl() {
        info = new HashMap<String, String>();
    }

    @Override
    public Severity getServerity() {
        return severity;
    }

    @Override
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    @Override
    public Map<String, String> getInfo() {
        return Collections.unmodifiableMap(info);
    }

    @Override
    public void setInfo(Map<String, String> info) {
        this.info.putAll(info);
    }

    @Override
    public void addInfo(String key, String value) {
        info.put(key, value);
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("ConversionDetailImpl [severity=%s, info=%s, message=%s]", severity, info, message);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((info == null) ? 0 : info.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((severity == null) ? 0 : severity.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ConversionDetailImpl other = (ConversionDetailImpl) obj;
        if (info == null) {
            if (other.info != null) {
                return false;
            }
        } else if (!info.equals(other.info)) {
            return false;
        }
        if (message == null) {
            if (other.message != null) {
                return false;
            }
        } else if (!message.equals(other.message)) {
            return false;
        }
        if (severity != other.severity) {
            return false;
        }
        return true;
    }
    

}
