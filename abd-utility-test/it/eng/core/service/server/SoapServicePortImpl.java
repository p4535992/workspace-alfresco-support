
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package it.eng.core.service.server;

import java.util.logging.Logger;
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
 * 2017-05-29T15:55:42.774+02:00
 * Generated source version: 3.1.11
 * 
 */

@javax.jws.WebService(
                      serviceName = "SoapServiceService",
                      portName = "SoapServicePort",
                      targetNamespace = "http://server.service.core.eng.it/",
                      wsdlLocation = "https://condidoc-test.regione.toscana.it/IrisBusiness/business/soap/ServiceSoap?wsdl",
                      endpointInterface = "it.eng.core.service.server.SoapService")
                      
public class SoapServicePortImpl implements SoapService {

    private static final Logger LOG = Logger.getLogger(SoapServicePortImpl.class.getName());

    /* (non-Javadoc)
     * @see it.eng.core.service.server.SoapService#serviceoperationinvoke(java.lang.String serializationtype, java.lang.String uuidtransaction, java.lang.String tokenid, java.lang.String servicename, java.lang.String operationame, java.util.List<java.lang.String> objectsserialize)*
     */
    public java.lang.String serviceoperationinvoke(java.lang.String serializationtype, java.lang.String uuidtransaction, java.lang.String tokenid, java.lang.String servicename, java.lang.String operationame, java.util.List<java.lang.String> objectsserialize) throws ServiceException_Exception   { 
        LOG.info("Executing operation serviceoperationinvoke");
        System.out.println(serializationtype);
        System.out.println(uuidtransaction);
        System.out.println(tokenid);
        System.out.println(servicename);
        System.out.println(operationame);
        System.out.println(objectsserialize);
        try {
            java.lang.String _return = "";
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new ServiceException_Exception("ServiceException...");
    }

}
