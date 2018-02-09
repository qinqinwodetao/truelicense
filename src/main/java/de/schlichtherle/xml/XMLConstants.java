/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package de.schlichtherle.xml;

/**
 * Provides some constants used throughout this package.
 *
 * @author  Christian Schlichtherle
 * @version $Id$
 */
public interface XMLConstants {
    
    /**
     * {@code "UTF-8"} - the character encoding used by {@code XMLEncoder}
     * and {@code XMLDecoder}.
     */
    String XML_CHARSET = "UTF-8";
    
    /** The default buffer size for buffered I/O, which is {@value}. */
    int DEFAULT_BUFSIZE = 8 * 1024;
}
