
package it.eng.core.service.server;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per serviceoperationinvoke complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="serviceoperationinvoke"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="serializationtype" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="uuidtransaction" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tokenid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="servicename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="operationame" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="objectsserialize" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "serviceoperationinvoke", propOrder = {
    "serializationtype",
    "uuidtransaction",
    "tokenid",
    "servicename",
    "operationame",
    "objectsserialize"
})
public class Serviceoperationinvoke {

    protected String serializationtype;
    protected String uuidtransaction;
    protected String tokenid;
    protected String servicename;
    protected String operationame;
    protected List<String> objectsserialize;

    /**
     * Recupera il valore della proprietà serializationtype.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerializationtype() {
        return serializationtype;
    }

    /**
     * Imposta il valore della proprietà serializationtype.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerializationtype(String value) {
        this.serializationtype = value;
    }

    /**
     * Recupera il valore della proprietà uuidtransaction.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUuidtransaction() {
        return uuidtransaction;
    }

    /**
     * Imposta il valore della proprietà uuidtransaction.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUuidtransaction(String value) {
        this.uuidtransaction = value;
    }

    /**
     * Recupera il valore della proprietà tokenid.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTokenid() {
        return tokenid;
    }

    /**
     * Imposta il valore della proprietà tokenid.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTokenid(String value) {
        this.tokenid = value;
    }

    /**
     * Recupera il valore della proprietà servicename.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicename() {
        return servicename;
    }

    /**
     * Imposta il valore della proprietà servicename.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicename(String value) {
        this.servicename = value;
    }

    /**
     * Recupera il valore della proprietà operationame.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationame() {
        return operationame;
    }

    /**
     * Imposta il valore della proprietà operationame.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationame(String value) {
        this.operationame = value;
    }

    /**
     * Gets the value of the objectsserialize property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the objectsserialize property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getObjectsserialize().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getObjectsserialize() {
        if (objectsserialize == null) {
            objectsserialize = new ArrayList<String>();
        }
        return this.objectsserialize;
    }

}
