package org.dhbw.mosbach.ai.base;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class V2Info {

    @XmlAttribute(required = true)
    public long V2id;

    @XmlAttribute(required = true)
    public String SOAPURL;

    @XmlAttribute(required = true)
    public double speed;

    @XmlTransient
    public Position position;



}
