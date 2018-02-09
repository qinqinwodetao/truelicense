/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package de.schlichtherle.xml;

/**
 * An instance of this class is thrown if the integrity of a
 * {@link GenericCertificate} has been detected to be compromised.
 *
 * @author  Christian Schlichtherle
 * @version $Id$
 */
public class GenericCertificateIntegrityException
extends java.security.GeneralSecurityException {
    private static final long serialVersionUID = 1L;
}