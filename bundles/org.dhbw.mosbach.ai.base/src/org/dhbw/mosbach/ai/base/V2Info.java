package org.dhbw.mosbach.ai.base;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class V2Info {

    @XmlAttribute(required = true)
    public long V2id;

    @XmlAttribute(required = true)
    public String SOAPURL;

    @XmlAttribute(required = true)
    public double speed;

    @XmlElement
    public Position position;


    @XmlElement
    public Position direction;

}
