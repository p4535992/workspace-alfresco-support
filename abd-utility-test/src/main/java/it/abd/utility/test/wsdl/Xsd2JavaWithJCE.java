package it.abd.utility.test.wsdl;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;

import org.apache.cxf.tools.wsdlto.WSDLToJava;
import org.xml.sax.InputSource;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;

public class Xsd2JavaWithJCE {
	
    public static void main(String... args) throws Exception {
		

    	// Configure sources & output
    	String schemaPath = "C:\\Users\\Pancio\\Desktop\\SIPResult.xsd";
    	String outputDirectory = "C:\\Users\\Pancio\\Desktop\\";
    	
    	// Setup schema compiler
    	SchemaCompiler sc = XJC.createSchemaCompiler();
    	sc.forcePackageName("com.xyz.schema.generated");

    	// Setup SAX InputSource
    	File schemaFile = new File(schemaPath);
    	
     	// to work around windows platform issue
        String id = schemaFile.getAbsolutePath().replace('\\', '/');
    	InputSource is = new InputSource(new FileInputStream(schemaFile));
    	//is.setSystemId(schemaFile.getAbsolutePath());
    	is.setSystemId(id);
    	// Parse & build
    	sc.parseSchema(is);
    	S2JJAXBModel model = sc.bind();
    	JCodeModel jCodeModel = model.generateCode(null, null);
    	jCodeModel.build(new File(outputDirectory));
    }

}
