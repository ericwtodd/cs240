package server;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class GeneratorTest {

    @Test
    public void testConstructor() {
        Generator generator = new Generator(true);
        //See if all the arrays were populated
        assertNotNull(generator.getLastNames());
        assertNotNull(generator.getFemaleNames());
        assertNotNull(generator.getMaleNames());
        assertNotNull(generator.getLocations());
    }

    @Test
    public void testRandomGenerators() {
        Generator generator = new Generator(true);

        //It should be mostly random, but sometimes this will fail even though it is random.
        assertNotEquals(generator.generateFemaleName(), generator.generateFemaleName());
        assertNotEquals(generator.generateMaleName(), generator.generateMaleName());
        assertNotEquals(generator.generateLastName(), generator.generateLastName());
        assertNotEquals(generator.generateLocation(), generator.generateLocation());

        //Look at randomly generated names and locations being printed.
//        System.out.println(generator.generateFemaleName());
//        System.out.println(generator.generateMaleName());
//        System.out.println(generator.generateLastName());
//        System.out.println(generator.generateLocation().getCountry());
    }
}