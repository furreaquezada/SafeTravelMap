package com.example.safetravelmap.entities;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class DesperfectoTest extends TestCase {
    private Desperfecto desperfecto = new Desperfecto();
    @Before
    public void setup(){
        desperfecto = new Desperfecto();
    }

    @Test
    public void testDescontarPuntos() throws Exception{
        assertEquals(true, desperfecto.restarPuntos(50));
    }
}