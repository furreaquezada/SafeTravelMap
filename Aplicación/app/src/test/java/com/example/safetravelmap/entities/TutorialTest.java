package com.example.safetravelmap.entities;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class TutorialTest extends TestCase {
    private Tutorial tutorial = new Tutorial();
    @Before
    public void setup(){
        tutorial = new Tutorial();
    }

    @Test
    public void testListadoTutorial() throws Exception{
        assertEquals(true, tutorial.listadoTutoriales());
    }
}