/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package de.schlichtherle.xml;

import java.beans.*;
import java.io.*;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Provides a collection of static methods to support comfortable loading and
 * storing of objects as XML data.
 * <p>
 * This class uses the classes {@link XMLEncoder} and
 * {@link XMLDecoder} to encode and decode an object
 * to and from an XML file for long term persistence.
 * It allows you to provide custom {@link PersistenceDelegate}
 * instances for the serialisation of any classes which do not implement the
 * JavaBean design pattern and are not supported by {@code XMLEncoder}
 * as a default.
 * <p>
 * For the latter case, {@code PersistenceService} offers the
 * {@link #setPersistenceDelegate(Class, PersistenceDelegate)} method which
 * could be called from a static initializer block in the class which would
 * like to use {@code PersistenceService}'s {@code store} and {@code load}
 * methods.
 * <p>
 * Note that the Java API already provides some default persistence delegates
 * for some classes of its API which are not JavaBeans. If in doubt, simply
 * test the class before writing a custom {@code PersistenceDelegate}.
 * If you see exceptions happening, you most probably need to provide a 
 * {@code PersistenceDelegate} and use the {@code setPersistenceDelegate}
 * method to install it.
 * <p>
 * Note that the store and load methods in this class have been designed
 * to deal with <em>any</em> kind of {@link Throwable}s throughout the course
 * of (de)serialization, even {@link OutOfMemoryError}s.
 * <p>
 * This class is thread.safe.
 *
 * @see     XMLEncoder
 * @see     XMLDecoder
 * @see     PersistenceDelegate
 * @see     java.beans.DefaultPersistenceDelegate
 * @see     <a href="http://java.sun.com/products/jfc/tsc/articles/persistence4/">Sun Developer Network Site: Using XMLEncoder</a>
 * @author  Christian Schlichtherle
 * @version $Id$
 */
public class PersistenceService implements XMLConstants {
    
    /**
     * This map maps from {@code Class} instances to
     * {@code PersistenceDelegate} instances. Its elements are installed
     * in the {@code XMLEncoder} prior to encoding an object.
     */
    private static final HashMap allPDs = new HashMap();
    
    /**
     * The buffer size for I/O used in the store and load methods.
     * You may customise this to your needs - the default is
     * {@link #DEFAULT_BUFSIZE}.
     */
    public static int BUFSIZE = DEFAULT_BUFSIZE;

    /** You cannot instantiate this class. */
    private PersistenceService() { }

    /**
     * Returns an {@code ExceptionListener}.
     * This custom exception listener enforces zero tolerance when encoding
     * or decoding objects to or from XML files in order not to compromise
     * the integrity of an object.
     */
    private static final ExceptionListener createExceptionListener() {
        return new ExceptionListener() {
            public void exceptionThrown(Exception exc) {
                throw exc instanceof UndeclaredThrowableException
                    ? (UndeclaredThrowableException) exc // don't wrap again
                    : new UndeclaredThrowableException(exc);
            }
        };
    }
    
    /**
     * Associates a {@code PersistenceDelegate} to the given
     * class {@code type}.
     * <p>
     * This must be called prior to the {@code store} methods for each class
     * which's instances are to be persisted.
     * Thus, a good place to make this call is in a <em>static initializer
     * block</em> for the corresponding class.
     * Here is an example:
     * {@code <pre>
     *      class PersistentObject {
     *          static {
     *              PersistenceService.setPersistenceDelegate(
     *                  PersistentObject.class,
     *                  new DefaultPersistenceDelegate(
     *                      new String[] { "property" }));
     *          }
     *
     *          public int property;
     *
     *          public PersistentObject(int property) {
     *              this.property = property;
     *          }
     *      }
     * </pre>}
     * <p>
     * Note that you should not use this method for any class which's source
     * code you can control.
     * The preferred way to associate a persistence delegate with a class is
     * to write a {@code BeanInfo} class which's
     * {@code BeanDescriptor} has an attribute set with
     * {@code "persistenceDelegate"} as its name and the respective
     * persistence delegate as its value
     * (see {@link Encoder#getPersistenceDelegate}).
     * However, this method is still useful in case you can't control the
     * source code, as then at least you can still associate a persistence
     * delegate to this class.
     *
     * @see java.beans.XMLEncoder
     * @see java.beans.PersistenceDelegate
     * @see java.beans.DefaultPersistenceDelegate
     */
    public static synchronized final void setPersistenceDelegate(
            Class clazz,
            PersistenceDelegate persistenceDelegate) {
        allPDs.put(clazz, persistenceDelegate);
    }

    /**
     * Installs all persistence delegates registered via
     * {@code {@link #setPersistenceDelegate(Class, PersistenceDelegate)}}
     * in {@code encoder}.
     *
     * @param  encoder The encoder - may <em>not</em> be {@code null}.
     * @throws NullPointerException If {@code encoder} is {@code null}.
     */
    protected static synchronized void installPersistenceDelegates(
            final Encoder encoder) {
        Iterator i = allPDs.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            encoder.setPersistenceDelegate(
                    (Class) entry.getKey(),
                    (PersistenceDelegate) entry.getValue());
        }
    }
    
    /**
     * Stores the object {@code root}, which may form the root of an entire
     * object graph, as XML content to the output stream {@code xmlOut} for
     * long term persistence.
     * <p>
     * Please note the following:
     * <ul>
     * <li>This method will <em>not</em> tolerate any I/O or other
     *     serialisation exceptions!</li>
     * <li>The underlying stream is <em>always</em> closed (even if an
     *     exception is thrown).</li>
     * </ul>
     *
     * @param root The object to store - may be {@code null}.
     * @param xmlOut The unbuffered stream to output the XML content
     *        - may <em>not</em> be {@code null}.
     *
     * @throws NullPointerException If {@code xmlOut} is }null}.
     * @throws PersistenceServiceException If <em>any</em> throwable was thrown
     *         during serialization. Its cause is always set.
     */
    public static void store(
            final Object root,
            final OutputStream xmlOut)
    throws PersistenceServiceException {
        if (xmlOut == null)
            throw new NullPointerException();
        
        // Guard against OutOfMemoryError here!
        // This is not unlikely as XMLEncoder clones the root, which could be
        // a large object graph.
        // Remember: We guarantee to close the underlying stream, that's why
        // we make a fuss out of this.
        try {
            OutputStream bufOut = null;
            XMLEncoder encoder = null;
            try {
                bufOut = new BufferedOutputStream(xmlOut, BUFSIZE);
                encoder = new XMLEncoder(bufOut);
                installPersistenceDelegates(encoder);
                encoder.setExceptionListener(createExceptionListener());
                encoder.writeObject(root);
            } finally {
                if (encoder != null) {
                    try {
                        // This method actually writes the XML content
                        // and may throw an OutOfMemoryError (again)!
                        encoder.close();
                    } catch (Throwable paranoid) {
                        // In case of an exception during the closing of
                        // the encoder it does not properly close its
                        // underlying stream. Do this now.
                        bufOut.close();
                        throw paranoid;
                    }
                } else if (bufOut != null) {
                    // Allocating the encoder failed.
                    // This should not normally happen unless the JVM is very
                    // very scarce on heap memory!
                    bufOut.close();
                } else {
                    // Allocating the buffered output stream failed.
                    // This should not normally happen unless the JVM is very
                    // very scarce on heap memory!
                    xmlOut.close();
                }
            }
        } catch (UndeclaredThrowableException exc) {
            // Allocating a new exception should always succeed as the encoder
            // and its associated clone of the root object graph is now
            // eligible for garbage collection!
            throw new PersistenceServiceException(exc.getCause()); // unwrap cause
        } catch (Throwable thr) {
            // Allocating a new exception should always succeed as the encoder
            // and its associated clone of the root object graph is now
            // eligible for garbage collection!
            throw new PersistenceServiceException(thr);
        }
    }
    
    /**
     * Stores the object {@code root}, which may form the root of an entire
     * object graph, as XML content to the file {@code file} for
     * long term persistence.
     * This method supports writing to a file located in a ZIP or JAR file.
     * <p>
     * Please note the following:
     * <ul>
     * <li>This method will <em>not</em> tolerate any I/O or other
     *     serialisation exceptions!</li>
     * <li>This is a transaction, i.e. the method either completely succeeds
     *     with saving the file or the file is restored to its original state.
     * </ul>
     *
     * @param  root The object to store - may be {@code null}.
     * @param  file The file to output the XML content to
     *         - may <em>not</em> be {@code null}.
     * @throws NullPointerException If {@code file} is {@code null}.
     * @throws PersistenceServiceException If <em>any</em> throwable was thrown
     *         during serialization. Its cause is always set.
     */
    public static void store(
            final Object root,
            final File file)
    throws PersistenceServiceException {
        if (file == null)
            throw new NullPointerException();
        
        File backup = null;
        boolean renamed = false;
        try {
            backup = getRenamedFile(file);
            renamed = file.renameTo(backup);
            store(root, new FileOutputStream(file));
            if (renamed)
                backup.delete();
        } catch (Throwable ex) {
            if (renamed) {
                // (Note that the stream has been closed by store(...) already!)
                // The transaction failed at a point in time where the
                // target file has at least been partially written, so we
                // need to restore the target file from the backup again.
                try {
                    file.delete();
                } catch (Throwable paranoid) {
                    ex = paranoid;
                }
                try {
                    backup.renameTo(file);
                } catch (Throwable paranoid) {
                    ex = paranoid;
                }
            }
            throw ex instanceof PersistenceServiceException
                ? (PersistenceServiceException) ex
                : new PersistenceServiceException(ex);
        }
    }

    private static File getRenamedFile(File plainFile) {
        final StringBuilder path = new StringBuilder(plainFile.getPath());
        File renamedFile;
        do {
            // This should be OK on any current Java platform...
            renamedFile = new File(path.append('~').toString());
        } while (renamedFile.exists());
        return renamedFile;
    }
    
    /**
     * Stores the object {@code root}, which may form the root of an entire
     * object graph, as XML content into a UTF-8 encoded byte array for
     * long term persistence.
     * <p>
     * Please note the following:
     * <ul>
     * <li>This method will <em>not</em> tolerate any I/O or other
     *     serialisation exceptions!</li>
     * </ul>
     *
     * @param  root The object to store - may be {@code null}.
     * @return The XML with UTF-8 charset encoded byte array
     *         representation of {@code root}
     *         - {@code null} is never returned.
     * @throws PersistenceServiceException If <em>any</em> throwable was thrown
     *         during serialization. Its cause is always set.
     */
    public static byte[] store2ByteArray(Object root)
    throws PersistenceServiceException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            store(root, out);
            return out.toByteArray();
        } catch (PersistenceServiceException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new PersistenceServiceException(ex);
        }
    }

    /**
     * Stores the object {@code root}, which may form the root of an entire
     * object graph, as XML content into a string for long term persistence.
     * <p>
     * Please note the following:
     * <ul>
     * <li>This method will <em>not</em> tolerate any I/O or other
     *     serialisation exceptions!</li>
     * </ul>
     *
     * @param  root The object to store - may be {@code null}.
     * @return The XML string encoded representation of {@code root}
     *         - {@code null} is never returned.
     * @throws PersistenceServiceException If <em>any</em> throwable was thrown
     *         during serialization. Its cause is always set.
     */
    public static String store2String(Object root)
    throws PersistenceServiceException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            store(root, out);
            return out.toString(XML_CHARSET);
        } catch (UnsupportedEncodingException cannotHappen) {
            throw new AssertionError(cannotHappen);
        } catch (PersistenceServiceException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new PersistenceServiceException(ex);
        }
    }
    
    /**
     * Loads a single object, which may form the root of an entire
     * object graph, from XML content in the given input stream {@code xmlIn}.
     * <p>
     * Please note the following:
     * <ul>
     * <li>The stream is connected to a new {@code BufferedInputStream}
     *     with {@code BUFSIZE} as its buffer size and is <em>always</em>
     *     closed (even if an exception is thrown).</li>
     * <li>This method will <em>not</em> tolerate any I/O or other
     *     deserialisation exceptions!</li>
     * </ul>
     *
     * @param  xmlIn The unbuffered stream to input the XML content
     *         - may <em>not</em> be {@code null}.
     * @return The root of the loaded object graph - may be {@code null}.
     * @throws NullPointerException If {@code xmlIn} is {@code null}.
     * @throws PersistenceServiceException If <em>any</em> throwable was thrown
     *         during serialization. Its cause is always set.
     */
    public static Object load(InputStream xmlIn)
    throws PersistenceServiceException {
        if (xmlIn == null)
            throw new NullPointerException();

        XMLDecoder decoder = null;
        try {
            // Note that the constructor already loads the complete object
            // graph into memory. If anything goes wrong, an unchecked
            // exception is thrown already HERE!
            decoder = new XMLDecoder(
                    new BufferedInputStream(xmlIn, BUFSIZE),
                    null,
                    createExceptionListener());
            return decoder.readObject();
        } catch (UndeclaredThrowableException ex) {
            throw new PersistenceServiceException(ex.getCause()); // unwrap cause
        } catch (Throwable ex) {
            throw new PersistenceServiceException(ex);
        } finally {
            if (decoder != null) {
                try {
                    decoder.close(); // Could throw e.g. OutOfMemoryError (again)!
                }
                catch (Throwable paranoid) {
                    throw new PersistenceServiceException(paranoid);
                }
            }
        }
    }
    
    /**
     * Loads a single object, which may form the root of an entire
     * object graph, from XML content in the given file {@code file}.
     * <p>
     * Please note the following:
     * <ul>
     * <li>This method will <em>not</em> tolerate any I/O or other
     *     deserialisation exceptions!</li>
     * </ul>
     *
     * @param  file The file to load the XML content from
     *         - may <em>not</em> be {@code null}.
     * @return The root of the loaded object graph - may be {@code null}.
     * @throws NullPointerException If {@code file} is {@code null}.
     * @throws PersistenceServiceException If <em>any</em> throwable was thrown
     *         during serialization. Its cause is always set.
     */
    public static Object load(File file)
    throws PersistenceServiceException {
        if (file == null)
            throw new NullPointerException();
        
        try {
            return load(new FileInputStream(file));
        } catch (PersistenceServiceException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new PersistenceServiceException(ex);
        }
    }
    
    /**
     * Loads a single object, which may form the root of an entire
     * object graph, from XML content in the UTF-8 encoded byte array
     * {@code encoded}.
     * <p>
     * Please note the following:
     * <ul>
     * <li>This method will <em>not</em> tolerate any I/O or other
     *     deserialisation exceptions!</li>
     * </ul>
     *
     * @param  encoded The XML with UTF-8 charset encoded byte array
     *         representation of the root of an object graph
     *         - may <em>not</em> be {@code null}.
     * @return The root of the loaded object graph - may be {@code null}.
     * @throws NullPointerException If {@code encoded} is {@code null}.
     * @throws PersistenceServiceException If <em>any</em> throwable was thrown
     *         during serialization. Its cause is always set.
     */
    public static Object load(final byte[] encoded)
    throws PersistenceServiceException {
        if (encoded == null)
            throw new NullPointerException();
        
        try {
            return load(new ByteArrayInputStream(encoded));
        } catch (PersistenceServiceException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new PersistenceServiceException(ex);
        }
    }
    
    /**
     * Loads a single object, which may form the root of an entire
     * object graph, from XML content in the string {@code encoded}.
     * <p>
     * Please note the following:
     * <ul>
     * <li>This method will <em>not</em> tolerate any I/O or other
     *     deserialisation exceptions!</li>
     * </ul>
     *
     * @param  encoded The XML string encoded representation of the root
     *         of an object graph
     *         - may <em>not</em> be {@code null}.
     * @return The root of the loaded object graph - may be {@code null}.
     * @throws NullPointerException If {@code encoded} is {@code null}.
     * @throws PersistenceServiceException If <em>any</em> throwable was thrown
     *         during serialization. Its cause is always set.
     */
    public static Object load(final String encoded)
    throws PersistenceServiceException {
        if (encoded == null)
            throw new NullPointerException();
        
        try {
            return load(encoded.getBytes(XML_CHARSET));
        } catch (UnsupportedEncodingException cannotHappen) {
            throw new AssertionError(cannotHappen);
        } catch (PersistenceServiceException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new PersistenceServiceException(ex);
        }
    }
}
