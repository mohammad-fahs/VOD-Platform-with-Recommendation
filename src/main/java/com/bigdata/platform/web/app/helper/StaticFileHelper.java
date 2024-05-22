package com.bigdata.platform.web.app.helper;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;
public class StaticFileHelper {
    private static final Logger LOGGER = Logger.getLogger(StaticFileHelper.class.getName());
    public static String getFileContent(String fileName) {
        try {
            Resource resource = new ClassPathResource("static/" + fileName);

            if (!resource.exists()) {
                LOGGER.severe("Resource does not exist: " + "static/" + fileName);
                throw new UncheckedIOException("Resource does not exist: " + "static/" + fileName, new FileNotFoundException("static/" + fileName));
            }

            InputStream input = resource.getInputStream();
            String staticResourceString = new String(input.readAllBytes());

            if (staticResourceString.isEmpty())
                throw new IllegalArgumentException("The file is empty: " + "static/" + fileName);

            return staticResourceString;
        } catch (IOException e) {
            LOGGER.severe("IOException while attempting to read file " + fileName + " : " + ExceptionUtils.getStackTrace(e));
            throw new UncheckedIOException("Error reading file: " + fileName, e);
        } catch (IllegalArgumentException e) {
            LOGGER.severe("IllegalArgumentException: " + e.getMessage() + " : " + ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }
}

