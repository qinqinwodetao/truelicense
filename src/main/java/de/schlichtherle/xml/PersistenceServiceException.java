/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package de.schlichtherle.xml;

/**
 * Indicates failure to store or load a persistent object graph to or from an
 * XML file.
 * This exception is always instantiated with a valid cause.
 * Its detail message is copied from the detail message of the cause.
 *
 * @author  Christian Schlichtherle
 * @version $Id$
 */
public class PersistenceServiceException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an instance of {@code PersistenceServiceException}
     * with the given {@code cause}.
     *
     * @param cause The cause for this exception to be thrown.
     */
    public PersistenceServiceException(Throwable cause) {
        super(cause);
    }
}
