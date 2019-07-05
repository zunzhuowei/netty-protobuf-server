package com.game.qs.http.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class ResultObject implements Serializable {

    private int code;

    private String desc;

    private Object data;

    public ResultObject() {

    }

    public ResultObject(int code) {
        this.code = code;
    }

    public ResultObject(int code, Object data) {
        this.code = code;
        this.data = data;
    }

}
