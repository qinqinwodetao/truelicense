/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package de.schlichtherle.xml;

/**
 * An instance of this class is thrown if a {@link GenericCertificate}'s
 * content is tried to be retrieved but the certificate has neither signed
 * nor verified any content.
 *
 * @author  Christian Schlichtherle
 * @version $Id$
 */
public class GenericCertificateNotLockedException
extends IllegalStateException {
    private static final long serialVersionUID = 1L;
}
