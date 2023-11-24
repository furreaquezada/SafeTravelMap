package com.example.safetravelmap.entities;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DesperfectoTest {
    private Desperfecto desperfecto;

    @Before
    public void setup() {
        desperfecto = new Desperfecto();
    }

    @Test
    public void validarPresenciaEnMap() {
        assertTrue("El desperfecto debería estar presente en el mapa", desperfecto.validarPresenciaEnMap(1, 1));
    }
}
