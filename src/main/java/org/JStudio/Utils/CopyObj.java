package org.JStudio.Utils;

public class CopyObj {
    private final String obj_name;
    private final Object obj;

    public CopyObj(String obj_name, Object obj) {
        this.obj_name = obj_name;
        this.obj = obj;
    }

    public String getObj_name() {
        return obj_name;
    }
    public Object getObj() {
        return obj;
    }
}
