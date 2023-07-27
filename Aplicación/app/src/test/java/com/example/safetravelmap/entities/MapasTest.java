package com.example.safetravelmap.entities;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MapasTest extends TestCase {
    private Mapa mapa = new Mapa();
    @Before
    public void setup(){
        mapa = new Mapa();
    }

    @Test
    public void testConsultar_usuario() throws Exception{
        assertEquals(true, mapa.cargarMapa("test@gmail.com"));
    }




}