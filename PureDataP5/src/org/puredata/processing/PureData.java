/**
 * 
 * For information on usage and redistribution, and for a DISCLAIMER OF ALL
 * WARRANTIES, see the file, "LICENSE.txt," in this distribution.
 * 
 */

package org.puredata.processing;


import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.puredata.core.PdBase;
import org.puredata.core.PdMidiReceiver;
import org.puredata.core.PdReceiver;

import processing.core.PApplet;


/**
 * libpd for Processing.
 * 
 * @author Peter Brinkmann (peter.brinkmann@gmail.com)
 */
public class PureData implements PdReceiver, PdMidiReceiver {

	private final PApplet parent;
	
	private final Method pdPrintCallback;
	private final Method receiveBangCallback;
	private final Method receiveFloatCallback;
	private final Method receiveSymbolCallback;
	private final Method receiveListCallback;
	private final Method receiveMessageCallback;
	
	private final Method receiveNoteOnCallback;
	private final Method receivePolyAftertouchCallback;
	private final Method receiveControlChangeCallback;
	private final Method receiveProgramChangeCallback;
	private final Method receiveAftertouchCallback;
	private final Method receivePitchBendCallback;

	/**
	 * @param parent             owner
	 * @param int sampleRate     sample rate
	 * @param int inputChannels  number of input channels
	 * @param int outputChannels number of output channels
	 */
	public PureData(PApplet parent, int sampleRate, int inputChannels, int outputChannels) {
		this.parent = parent;
		parent.registerMethod("dispose", this);
		parent.registerMethod("pre", this);
		if (!PdBase.implementsAudio()) {
			throw new RuntimeException("PdBase does not implement audio!");
		}
		if (PdBase.openAudio(inputChannels, outputChannels, sampleRate) != 0) {
			throw new RuntimeException("Bad audio parameters: " + inputChannels + ", " + outputChannels + ", " + sampleRate);
		}
		PdBase.setReceiver(this);
		PdBase.setMidiReceiver(this);
		PdBase.computeAudio(true);
		PdBase.clearSearchPath();
		PdBase.addToSearchPath(parent.dataPath(""));
		Map<String, Method> methods = extractMethods(parent.getClass());
		pdPrintCallback = methods.get("pdPrint");
		receiveBangCallback = methods.get("receiveBang");
		receiveFloatCallback = methods.get("receiveFloat");
		receiveSymbolCallback = methods.get("receiveSymbol");
		receiveListCallback = methods.get("receiveList");
		receiveMessageCallback = methods.get("receiveMessage");
		receiveNoteOnCallback = methods.get("receiveNoteOn");
		receivePolyAftertouchCallback = methods.get("receivePolyAftertouch");
		receiveControlChangeCallback = methods.get("receiveControlChange");
		receiveProgramChangeCallback = methods.get("receiveProgramChange");
		receiveAftertouchCallback = methods.get("receiveAftertouch");
		receivePitchBendCallback = methods.get("receivePitchBend");
	}
	
	private Map<String, Method> extractMethods(Class<?> clazz) {
		Map<String, Method> result = new HashMap<String, Method>();
		Method[] methods = clazz.getMethods();
		for (Method method: methods) {
			result.put(method.getName(), method);
		}
		return result;
	}
	
	/**
	 * Processing dispose callback, automatically registered in the constructor.
	 */
	public void dispose() {
		stop();
		PdBase.release();
	}
	
	/**
	 * Processing pre-draw callback, automatically registered in the constructor.
	 */
	public void pre() {
		PdBase.pollMidiQueue();
		PdBase.pollPdMessageQueue();
	}
	
	/**
	 * Start audio.
	 */
	public void start() {
		PdBase.startAudio();
	}
	
	/**
	 * Stop audio.
	 */
	public void stop() {
		PdBase.pauseAudio();
	}
	
	/**
	 * Delegates to the corresponding method in {@link PdBase}.
	 */
	public int subscribe(String sym) {
		return PdBase.subscribe(sym);
	}
	
	/**
	 * Delegates to the corresponding method in {@link PdBase}.
	 */
	public void unsubscribe(String sym) {
		PdBase.unsubscribe(sym);
	}

	/**
	 * Delegates to the corresponding method in {@link PdBase}.
	 */
	public int openPatch(String file) {
		try {
			return PdBase.openPatch(parent.dataFile(file));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Delegates to the corresponding method in {@link PdBase}.
	 */
	public void closePatch(int handle) {
		PdBase.closePatch(handle);
	}

	/**
	 * Delegates to the corresponding method in {@link PdBase}.
	 */
	public int arraySize(String name) {
		return PdBase.arraySize(name);
	}
	
	/**
	 * Delegates to the corresponding method in {@link PdBase}.
	 */
	public int readArray(float[] to, int toOffset, String fromArray, int fromOffset, int n) {
		return PdBase.readArray(to, toOffset, fromArray, fromOffset, n);
	}
	
	/**
	 * Delegates to the corresponding method in {@link PdBase}.
	 */
	public int writeArray(String toArray, int toOffset, float[] fromArray, int fromOffset, int n) {
		return PdBase.writeArray(toArray, toOffset, fromArray, fromOffset, n);
	}
	
	/**
	 * Delegates to the corresponding method in {@link PdBase}.
	 */
	public void sendBang(String recv) {
		PdBase.sendBang(recv);
	}

	/**
	 * Delegates to the corresponding method in {@link PdBase}.
	 */
	public void sendFloat(String recv, float x) {
		PdBase.sendFloat(recv, x);
	}

	/**
	 * Delegates to the corresponding method in {@link PdBase}.
	 */
	public void sendSymbol(String recv, String sym) {
		PdBase.sendSymbol(recv, sym);
	}

	/**
	 * Delegates to the corresponding method in {@link PdBase}.
	 */
	public void sendList(String recv, Object... args) {
		PdBase.sendList(recv, args);
	}

	/**
	 * Delegates to the corresponding method in {@link PdBase}.
	 */
	public void sendMessage(String recv, String mesg, Object... args) {
		PdBase.sendMessage(recv, mesg, args);
	}
	
	/**
	 * Delegates to the corresponding method in {@link PdBase}.
	 */
	public void sendNoteOn(int channel, int note, int velocity) {
		PdBase.sendNoteOn(channel, note, velocity);
	}
	
	/**
	 * Delegates to the corresponding method in {@link PdBase}.
	 */
	public void sendPolyAftertouch(int channel, int note, int velocity) {
		PdBase.sendPolyAftertouch(channel, note, velocity);
	}
	
	/**
	 * Delegates to the corresponding method in {@link PdBase}.
	 */
	public void sendControlChange(int channel, int controller, int value) {
		PdBase.sendControlChange(channel, controller, value);
	}
	
	/**
	 * Delegates to the corresponding method in {@link PdBase}.
	 */
	public void sendAftertouch(int channel, int value) {
		PdBase.sendAftertouch(channel, value);
	}
	
	/**
	 * Delegates to the corresponding method in {@link PdBase}.
	 */
	public void sendPitchBend(int channel, int value) {
		PdBase.sendPitchBend(channel, value);
	}
	
	/**
	 * Delegates to the corresponding method in {@link PdBase}.
	 */
	public void sendProgramChange(int channel, int value) {
		PdBase.sendProgramChange(channel, value);
	}
	
	/**
	 * Delegates to parent's pdPrint method, if it exists; no-op otherwise.
	 */
	@Override
	public void print(String s) {
		if (pdPrintCallback != null) {
			try {
				pdPrintCallback.invoke(parent, s);
			} catch (Exception e) {
				// Do nothing.
			}
		}
	}

	/**
	 * Delegates to parent's receiveBang method, if it exists; no-op otherwise.
	 */
	@Override
	public void receiveBang(String source) {
		if (receiveBangCallback != null) {
			try {
				receiveBangCallback.invoke(parent, source);
			} catch (Exception e) {
				// Do nothing.
			}
		}
	}

	/**
	 * Delegates to parent's receiveFloat method, if it exists; no-op otherwise.
	 */
	@Override
	public void receiveFloat(String source, float x) {
		if (receiveFloatCallback != null) {
			try {
				receiveFloatCallback.invoke(parent, source, x);
			} catch (Exception e) {
				// Do nothing.
			}
		}
	}

	/**
	 * Delegates to parent's receiveSymbol method, if it exists; no-op otherwise.
	 */
	@Override
	public void receiveSymbol(String source, String symbol) {
		if (receiveSymbolCallback != null) {
			try {
				receiveSymbolCallback.invoke(parent, source, symbol);
			} catch (Exception e) {
				// Do nothing.
			}
		}
	}

	/**
	 * Delegates to parent's receiveList method, if it exists; no-op otherwise.
	 */
	@Override
	public void receiveList(String source, Object... args) {
		if (receiveListCallback != null) {
			try {
				receiveListCallback.invoke(parent, source, args);
			} catch (Exception e) {
				// Do nothing.
			}
		}
	}

	/**
	 * Delegates to parent's receiveMessage method, if it exists; no-op otherwise.
	 */
	@Override
	public void receiveMessage(String source, String symbol, Object... args) {
		if (receiveMessageCallback != null) {
			try {
				receiveMessageCallback.invoke(parent, source, symbol, args);
			} catch (Exception e) {
				// Do nothing.
			}
		}
	}

	@Override
	public boolean beginBlock() {
		return false;
	}

	@Override
	public void endBlock() {
		// Do nothing.
	}

	@Override
	public void receiveAftertouch(int c, int v) {
		if (receiveAftertouchCallback != null) {
			try {
				receiveAftertouchCallback.invoke(parent, c, v);
			} catch (Exception e) {
				// Do nothing.
			}
		}
	}

	@Override
	public void receiveControlChange(int c, int k, int v) {
		if (receiveControlChangeCallback != null) {
			try {
				receiveControlChangeCallback.invoke(parent, c, k , v);
			} catch (Exception e) {
				// Do nothing.
			}
		}
	}

	@Override
	public void receiveNoteOn(int c, int k, int v) {
		if (receiveNoteOnCallback != null) {
			try {
				receiveNoteOnCallback.invoke(parent, c, k, v);
			} catch (Exception e) {
				// Do nothing.
			}
		}
	}

	@Override
	public void receivePitchBend(int c, int v) {
		if (receivePitchBendCallback != null) {
			try {
				receivePitchBendCallback.invoke(parent, c, v);
			} catch (Exception e) {
				// Do nothing.
			}
		}
	}

	@Override
	public void receivePolyAftertouch(int c, int k, int v) {
		if (receivePolyAftertouchCallback != null) {
			try {
				receivePolyAftertouchCallback.invoke(parent, c, k, v);
			} catch (Exception e) {
				// Do nothing.
			}
		}
	}

	@Override
	public void receiveProgramChange(int c, int v) {
		if (receiveProgramChangeCallback != null) {
			try {
				receiveProgramChangeCallback.invoke(parent, c, v);
			} catch (Exception e) {
				// Do nothing.
			}
		}
	}

	@Override
	public void receiveMidiByte(int p, int b) {
		// Do nothing; we don't support this low-level functionality in Processing.
	}
}