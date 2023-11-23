package com.example.safetravelmap.entities;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class ReincidenciaTest extends TestCase {
    private Reincidencia reincidencia = new Reincidencia();
    @Before
    public void setup(){
        reincidencia = new Reincidencia();
    }

    @Test
    public void testCrearReincidencia() throws Exception{
        assertEquals(true, reincidencia.crearReincidencia());
    }
}