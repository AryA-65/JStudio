package org.JStudio.Plugins.SynthUtil;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL10.AL_INVALID_OPERATION;

/**
 * Exception type
 */
public class OpenALException extends RuntimeException{
    /**
     * Method that defines the exception type
     * @param errorCode
     */
    public OpenALException(int errorCode) {
        super("Internal " + (errorCode == AL_INVALID_NAME ? "invalid name" : errorCode == AL_INVALID_ENUM ? "invalid enum"
                : errorCode == AL_INVALID_VALUE ? "invalid value" : errorCode == AL_INVALID_OPERATION ? "invalid operation" : "unkown")
                + " OpenAL exception.");
    }

}
