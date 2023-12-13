package com.STM.safetravelmap.entities;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MapaTest extends TestCase {
    private Mapa mapa = new Mapa();
    @Before
    public void setup(){
        mapa = new Mapa();
    }

    @Test
    public void testCargarMapa() throws Exception{
        assertEquals(true, mapa.cargarMapa("AIzaSyBjynErAHv4RNpcKaa9c6cin4kfYf9G8Wg"));
    }

    @Test
    public void testCargarMarcadores() throws Exception{
        assertEquals(true, mapa.cargarMarcadores());
    }
}