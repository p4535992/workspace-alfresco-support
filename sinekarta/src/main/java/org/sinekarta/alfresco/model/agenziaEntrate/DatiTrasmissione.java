//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.11.27 at 11:03:36 AM CET 
//


package org.sinekarta.alfresco.model.agenziaEntrate;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}SoggettoObbligatoTrasmissione"/>
 *         &lt;element ref="{}DatiIntermediarioTrasmissione" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "soggettoObbligatoTrasmissione",
    "datiIntermediarioTrasmissione"
})
@XmlRootElement(name = "DatiTrasmissione")
public class DatiTrasmissione implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @XmlElement(name = "SoggettoObbligatoTrasmissione", required = true)
    protected String soggettoObbligatoTrasmissione;
    @XmlElement(name = "DatiIntermediarioTrasmissione")
    protected DatiIntermediarioTrasmissione datiIntermediarioTrasmissione;

    /**
     * Gets the value of the soggettoObbligatoTrasmissione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSoggettoObbligatoTrasmissione() {
        return soggettoObbligatoTrasmissione;
    }

    /**
     * Sets the value of the soggettoObbligatoTrasmissione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSoggettoObbligatoTrasmissione(String value) {
        this.soggettoObbligatoTrasmissione = value;
    }

    /**
     * Gets the value of the datiIntermediarioTrasmissione property.
     * 
     * @return
     *     possible object is
     *     {@link DatiIntermediarioTrasmissione }
     *     
     */
    public DatiIntermediarioTrasmissione getDatiIntermediarioTrasmissione() {
        return datiIntermediarioTrasmissione;
    }

    /**
     * Sets the value of the datiIntermediarioTrasmissione property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatiIntermediarioTrasmissione }
     *     
     */
    public void setDatiIntermediarioTrasmissione(DatiIntermediarioTrasmissione value) {
        this.datiIntermediarioTrasmissione = value;
    }

}
