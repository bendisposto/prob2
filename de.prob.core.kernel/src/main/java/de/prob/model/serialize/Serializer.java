package de.prob.model.serialize;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;

import com.thoughtworks.xstream.XStream;

public class Serializer {

	public static String serialize(final ModelObject ebM) {
		XStream xstream = new XStream();
		String xml = xstream.toXML(ebM);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip;
		byte[] bytes;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(xml.getBytes());
			gzip.close();
			bytes = out.toByteArray();
		} catch (IOException e) {
			bytes = xml.getBytes();
		}
		return Base64.encodeBase64String(bytes);
	}
}
