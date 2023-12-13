package com.STM.safetravelmap.entities;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Cuenta_usuarioTest extends TestCase {
    private Cuenta_usuario cuenta_usuario = new Cuenta_usuario();
    @Before
    public void setup(){
        cuenta_usuario = new Cuenta_usuario();
    }

    @Test
    public void testConsultar_usuario() throws Exception{
        assertEquals(true, cuenta_usuario.consultar_usuario("test@gmail.com", true));
    }




}