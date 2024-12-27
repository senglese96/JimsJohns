package com.example.jimsjohns;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.jimsjohns.ui.BathroomForm;

import org.junit.Test;

public class FormValidationTest {
    @Test
    public void bathroomFormInput_CorrectInput_ReturnsTrue() {
        BathroomForm bathroomForm = new BathroomForm();
        assertTrue(bathroomForm.validate("hello", "world"));
    }

    @Test
    public void bathroomFormInput_EmptyInput_ReturnsFalse() {
        BathroomForm bathroomForm = new BathroomForm();
        assertFalse(bathroomForm.validate("", "world"));
    }

    @Test
    public void bathroomFormInput_LongInput_ReturnsFalse() {
        BathroomForm bathroomForm = new BathroomForm();
        assertFalse(bathroomForm.validate("hello my name is john smith and this is the story of how I" +
                " very nearly died on day while I was gardening my precious lawn", "world"));
    }
}
