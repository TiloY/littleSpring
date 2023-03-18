package org.littlespring.beans.factory;

import org.littlespring.beans.BeansException;

public class BeanCreationException extends BeansException {


    public BeanCreationException(String message) {
        super(message);
    }

    public BeanCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
