package com.gerken.audioGuide.services.logging;

import com.gerken.audioGuide.interfaces.Logger;
import com.gerken.audioGuide.services.Log4JAdapter;

public class LoggerFactory {
	public static Logger createLogger(Class<?> cls) {
        //return new DefaultLoggingAdapter(cls.getName());
		return new Log4JAdapter(cls);
	}
}
