
package it.eng.core.service.server;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per exceptionType.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="exceptionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="MANAGED"/&gt;
 *     &lt;enumeration value="UNMANAGED"/&gt;
 *     &lt;enumeration value="WARNING"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "exceptionType")
@XmlEnum
public enum ExceptionType {

    MANAGED,
    UNMANAGED,
    WARNING;

    public String value() {
        return name();
    }

    public static ExceptionType fromValue(String v) {
        return valueOf(v);
    }

}
