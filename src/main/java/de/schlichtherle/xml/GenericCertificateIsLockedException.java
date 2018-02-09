/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package de.schlichtherle.xml;

import java.beans.*;

/**
 * Thrown if a property is tried to set once a {@link GenericCertificate}
 * has been locked.
 *
 * @author  Christian Schlichtherle
 * @version $Id$
 */
public class GenericCertificateIsLockedException extends PropertyVetoException {
    private static final long serialVersionUID = 1L;

    public GenericCertificateIsLockedException(PropertyChangeEvent evt) {
        super(evt.getPropertyName(), evt);
    }
}
