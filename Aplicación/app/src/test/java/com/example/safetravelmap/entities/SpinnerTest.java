package com.example.safetravelmap.entities;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class SpinnerTest extends TestCase {
    private Spinner spinner = new Spinner();
    @Before
    public void setup(){
        spinner = new Spinner();
    }

    @Test
    public void testCargarSpinner() throws Exception{
        assertEquals(true, spinner.cargarListado());
    }
}