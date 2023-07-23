package com.example.safetravelmap.entities;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class Cuenta_administradorTest extends TestCase {

    private Cuenta_administrador cuenta_administrador = new Cuenta_administrador();
    @Before
    public void setup(){
        cuenta_administrador = new Cuenta_administrador();
    }

    @Test
    public void testConsultar_administrador() throws Exception{
        assertEquals(true, cuenta_administrador.consultar_administrador("test@gmail.com", true));
    }
}