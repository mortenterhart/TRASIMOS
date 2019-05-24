package org.dhbw.mosbach.ai.base;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class V2Info {

    @XmlAttribute(required = true)
    long V2id;

    @XmlAttribute(required = true)
    String SOAPURL;

    @XmlAttribute(required = true)
    double speed;

    @XmlAttribute(required = true)
    Position position;



}
