package org.JStudio.Plugins.Synthesizer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL10.AL_INVALID_OPERATION;

public class OpenALException extends RuntimeException{
    OpenALException(int errorCode) {
        super("Internal " + (errorCode == AL_INVALID_NAME ? "invalid name" : errorCode == AL_INVALID_ENUM ? "invalid enum"
                : errorCode == AL_INVALID_VALUE ? "invalid value" : errorCode == AL_INVALID_OPERATION ? "invalid operation" : "unkown")
                + " OpenAL exception.");
    }
}
