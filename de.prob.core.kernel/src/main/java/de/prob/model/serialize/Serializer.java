package de.prob.model.serialize;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
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

	public static ModelObject deserialize(final String code) {
		byte[] decoded = Base64.decodeBase64(code);
		ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
		GZIPInputStream gzis;
		StringBuffer res = new StringBuffer();
		try {
			gzis = new GZIPInputStream(bais);
			InputStreamReader reader = new InputStreamReader(gzis);
			BufferedReader in = new BufferedReader(reader);

			String readed;
			while ((readed = in.readLine()) != null) {
				res.append(readed);
				res.append('\n');
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		XStream xStream = new XStream();
		return (ModelObject) xStream.fromXML(res.toString());
	}
}
