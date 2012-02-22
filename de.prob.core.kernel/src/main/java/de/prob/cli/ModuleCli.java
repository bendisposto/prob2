package de.prob.cli;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;

public class ModuleCli extends AbstractModule {

	@Override
	protected void configure() {
		bind(ProBInstance.class).toProvider(ProBInstanceProvider.class);
		bind(OsSpecificInfo.class).toProvider(OsInfoProvider.class)
				.asEagerSingleton();
	}

	@Provides
	@OsName
	public String getOsName() {
		return System.getProperty("os.name");
	}

	@Provides
	@DebuggingKey
	public String createDebuggingKey() {
		Random random;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			random = new Random();
		}
		return Long.toHexString(random.nextLong());
	}

	@Retention(RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
	@BindingAnnotation
	@interface OsName {
	}

	@Retention(RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
	@BindingAnnotation
	@interface DebuggingKey {
	}

}
