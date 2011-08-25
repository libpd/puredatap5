/**
 * 
 * For information on usage and redistribution, and for a DISCLAIMER OF ALL
 * WARRANTIES, see the file, "LICENSE.txt," in this distribution.
 * 
 */

package com.noisepages.nettoyeur.processing;


import org.puredata.jack.PdJackProcessor;
import org.puredata.processing.PureDataP5Base;

import processing.core.PApplet;

import com.noisepages.nettoyeur.jack.JackException;
import com.noisepages.nettoyeur.jack.JackNativeClient;


/**
 * Support for using Pd and Jack with Processing.
 * 
 * @author Peter Brinkmann (peter.brinkmann@gmail.com)
 */
public class PureDataP5Jack extends PureDataP5Base {

	private JackNativeClient client = null;
	private final PdJackProcessor processor;
	private final String source, sink;

	/**
	 * Constructor.
	 * 
	 * @param parent owning instance of PApplet
	 * @param nIn desired number of input channels
	 * @param nOut desired number of output channels
	 */
	public PureDataP5Jack(PApplet parent, int nIn, int nOut) {
		this(parent, nIn, nOut, null, null);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param parent owning instance of PApplet
	 * @param nIn desired number of input channels
	 * @param nOut desired number of output channels
	 * @param source name of JACK client to connect inputs to
	 * @param sink name of JACK client to connect outputs to
	 */
	public PureDataP5Jack(PApplet parent, int nIn, int nOut, String source, String sink) {
		super(parent);
		this.source = source;
		this.sink = sink;
		try {
			processor = PdJackProcessor.createPdJackProcessor(nIn, nOut);
		} catch (JackException e) {
			// Converting to an unchecked exception is questionable from a Java point of view,
			// but it's the Processing way.
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void start()  {
		stop();
		try {
			client = processor.createClient("processing");
			if (source != null) {
				client.connectInputPorts(source);
			}
			if (sink != null) {
				client.connectOutputPorts(sink);
			}
		} catch (JackException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void stop() {
		if (client != null) {
			client.close();
			client = null;
		}
	}
}