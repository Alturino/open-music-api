package com.onirutla.open_music_api.core;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class StrictStringDeserializer extends StringDeserializer {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        if (p.getCurrentToken().isBoolean()) {
            log.atInfo()
                    .setMessage("token is not a valid String")
                    .addKeyValue("process", "deserialize")
                    .addKeyValue("token", p.getCurrentToken().asString())
                    .log();
            ctxt.reportInputMismatch(String.class, p.getCurrentToken().getClass().getTypeName());
            return null;
        }
        if (p.getCurrentToken().isNumeric()) {
            log.atInfo()
                    .setMessage("token is not a valid String")
                    .addKeyValue("process", "deserialize")
                    .addKeyValue("token", p.getCurrentToken().asString())
                    .log();
            ctxt.reportInputMismatch(String.class, p.getCurrentToken().getClass().getTypeName());
            return null;
        }
        return super.deserialize(p, ctxt);
    }
}
