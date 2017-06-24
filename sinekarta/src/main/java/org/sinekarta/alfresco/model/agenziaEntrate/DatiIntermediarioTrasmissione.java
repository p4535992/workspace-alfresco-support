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
 *         &lt;element ref="{}CodFisc"/>
 *         &lt;element ref="{}NumIscrizioneAlboCaf" minOccurs="0"/>
 *         &lt;element ref="{}ImpegnoTrasmissione"/>
 *         &lt;element ref="{}DataImpegno"/>
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
    "codFisc",
    "numIscrizioneAlboCaf",
    "impegnoTrasmissione",
    "dataImpegno"
})
@XmlRootElement(name = "DatiIntermediarioTrasmissione")
public class DatiIntermediarioTrasmissione implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @XmlElement(name = "CodFisc", required = true)
    protected String codFisc;
    @XmlElement(name = "NumIscrizioneAlboCaf")
    protected Integer numIscrizioneAlboCaf;
    @XmlElement(name = "ImpegnoTrasmissione")
    protected int impegnoTrasmissione;
    @XmlElement(name = "DataImpegno", required = true)
    protected DataImpegno dataImpegno;

    /**
     * Gets the value of the codFisc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodFisc() {
        return codFisc;
    }

    /**
     * Sets the value of the codFisc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodFisc(String value) {
        this.codFisc = value;
    }

    /**
     * Gets the value of the numIscrizioneAlboCaf property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumIscrizioneAlboCaf() {
        return numIscrizioneAlboCaf;
    }

    /**
     * Sets the value of the numIscrizioneAlboCaf property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumIscrizioneAlboCaf(Integer value) {
        this.numIscrizioneAlboCaf = value;
    }

    /**
     * Gets the value of the impegnoTrasmissione property.
     * 
     */
    public int getImpegnoTrasmissione() {
        return impegnoTrasmissione;
    }

    /**
     * Sets the value of the impegnoTrasmissione property.
     * 
     */
    public void setImpegnoTrasmissione(int value) {
        this.impegnoTrasmissione = value;
    }

    /**
     * Gets the value of the dataImpegno property.
     * 
     * @return
     *     possible object is
     *     {@link DataImpegno }
     *     
     */
    public DataImpegno getDataImpegno() {
        return dataImpegno;
    }

    /**
     * Sets the value of the dataImpegno property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataImpegno }
     *     
     */
    public void setDataImpegno(DataImpegno value) {
        this.dataImpegno = value;
    }

}
