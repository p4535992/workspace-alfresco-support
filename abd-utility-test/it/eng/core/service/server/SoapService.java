package it.eng.core.service.server;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.1.11
 * 2017-05-29T15:55:42.798+02:00
 * Generated source version: 3.1.11
 * 
 */
@WebService(targetNamespace = "http://server.service.core.eng.it/", name = "SoapService")
@XmlSeeAlso({ObjectFactory.class})
public interface SoapService {

    @WebMethod
    @Action(input = "http://server.service.core.eng.it/SoapService/serviceoperationinvokeRequest", output = "http://server.service.core.eng.it/SoapService/serviceoperationinvokeResponse", fault = {@FaultAction(className = ServiceException_Exception.class, value = "http://server.service.core.eng.it/SoapService/serviceoperationinvoke/Fault/ServiceException")})
    @RequestWrapper(localName = "serviceoperationinvoke", targetNamespace = "http://server.service.core.eng.it/", className = "it.eng.core.service.server.Serviceoperationinvoke")
    @ResponseWrapper(localName = "serviceoperationinvokeResponse", targetNamespace = "http://server.service.core.eng.it/", className = "it.eng.core.service.server.ServiceoperationinvokeResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.lang.String serviceoperationinvoke(
        @WebParam(name = "serializationtype", targetNamespace = "")
        java.lang.String serializationtype,
        @WebParam(name = "uuidtransaction", targetNamespace = "")
        java.lang.String uuidtransaction,
        @WebParam(name = "tokenid", targetNamespace = "")
        java.lang.String tokenid,
        @WebParam(name = "servicename", targetNamespace = "")
        java.lang.String servicename,
        @WebParam(name = "operationame", targetNamespace = "")
        java.lang.String operationame,
        @WebParam(name = "objectsserialize", targetNamespace = "")
        java.util.List<java.lang.String> objectsserialize
    ) throws ServiceException_Exception;
}
