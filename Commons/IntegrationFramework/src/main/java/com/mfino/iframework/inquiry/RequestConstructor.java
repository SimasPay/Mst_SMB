package com.mfino.iframework.inquiry;

import java.io.File;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import com.mfino.fix.CFIXMsg;
import com.mfino.iframework.domain.Field;
import com.mfino.iframework.domain.Integration;
import com.mfino.iframework.domain.Message;
import com.mfino.iframework.domain.MessageType;
import com.mfino.iframework.domain.Parameter;
import com.mfino.iframework.inquiry.domain.Inquiry;
import com.mfino.iframework.inquiry.domain.ObjectFactory;

public class RequestConstructor {

	public static final String	MESSAGE_INQUIRY	= "Inquiry";

	void construct(CFIXMsg fixmsg) {

		try {
			JAXBContext unMarshallingCxt = JAXBContext.newInstance(Integration.class);
			Unmarshaller um = unMarshallingCxt.createUnmarshaller();

			Integration integration = (Integration) um.unmarshal(new File(""));
			Message message = null;
			for (Message msg : integration.getMessage()) {
				if (MESSAGE_INQUIRY.equals(msg.getType())) {
					message = msg;
					break;
				}
			}
			MessageType messageType = message.getRequestOut();

			List<Field> fieldList = messageType.getField();
			List<Parameter> paramList = integration.getParameter();

			JAXBContext marshallCxt = JAXBContext.newInstance(Inquiry.class);
			Marshaller mar = marshallCxt.createMarshaller();
			
			
			for (Field field : fieldList) {
				for (Parameter param : paramList) {

				}
			}

		}
		catch (JAXBException ex) {
		}

	}

}
